package com.yunya.middletable.service.treatment_other;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.vo.FindAllRemindRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.report.BaseVisitRemindMapper;
import com.yunya.middletable.dao.treatment_other.VisitingRecordMapper;
import com.yunya.middletable.dao.treatment_other.VisitingRemindMapper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.report.BaseVisitRemind;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.VisitingRemind;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.yunya.middletable.constant.SynConstant.CUT_SLICE_100;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/10/20 19:12
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseVisitRemindBiz extends BaseBiz<BaseVisitRemindMapper, BaseVisitRemind> {
  private Logger log = LoggerFactory.getLogger(BaseVisitRemindBiz.class);

  /** 随访mapper */
  @Resource VisitingRecordMapper visitingRecordMapper;

  /** 提醒mapper */
  @Resource VisitingRemindMapper visitingRemindMapper;

  @Resource private RemoteTreatmentOtherFeign remoteTreatmentOtherFeign;

  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

  /**
   * 随访提醒中间表-操作
   *
   * @param msg 消息
   */
  public void operate(MessageModel msg) {
    Integer operateType = msg.getOperateType();
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer type = (Integer) msg.getParamMap().get("type");
    switch (operateType) {
      case 0:
        BaseVisitRemind insertBaseVisitRemind = getBaseVisitRemindInfo(id, type);
        insertBaseVisitRemind.setTime(null);
        if (StringHelper.isNotNull(insertBaseVisitRemind)) {
          mapper.delete(insertBaseVisitRemind);
          mapper.insertSelective(insertBaseVisitRemind);
        }
        break;
      case 1:
        BaseVisitRemind updBaseVisitRemind = getBaseVisitRemindInfo(id, type);
        mapper.updateByPrimaryKeySelective(updBaseVisitRemind);
        break;
      case 2:
        BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
        baseVisitRemind.setRecordId(id);
        baseVisitRemind.setType(type.byteValue());
        mapper.delete(baseVisitRemind);
        break;
      default:
        break;
    }
  }

  /**
   * 批量拉取-醒/随访-基础信息
   *
   * @param form 拉取时间
   */
  public void pullPatientData(PullForm form) throws InterruptedException {
    List<BaseVisitRemind> baseVisitRemindLists = new ArrayList<>();
    Integer type = form.getDataType();
    // 随访
    if (type == 0) {
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(VisitingRecord.class);
      emp.createCriteria()
          .andEqualTo("inservice", true)
          .andCondition("crt_time >= '" + new DateTime(startDate).toString("yyyy-MM-dd") + "'")
          .andCondition(
              "crt_time < '" + new DateTime(endDate).plusDays(1).toString("yyyy-MM-dd") + "'");
      List<VisitingRecord> visitingRecordList = visitingRecordMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(visitingRecordList)) {
        for (VisitingRecord visitingRecord :visitingRecordList ){
          mapper.deleteByPrimaryKeyAndType(visitingRecord.getId(), type);
          BaseVisitRemind baseVisitRemindInfo = getBaseVisitRemindInfo(visitingRecord,null, type);
          baseVisitRemindLists.add(baseVisitRemindInfo);
        }
        insertList(baseVisitRemindLists);
      }
    }

    // 提醒
    if (type == 1) {
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(VisitingRemind.class);
      emp.createCriteria()
          .andEqualTo("inservice", true)
          .andCondition("crt_time >= '" + new DateTime(startDate).toString("yyyy-MM-dd") + "'")
          .andCondition(
              "crt_time < '" + new DateTime(endDate).plusDays(1).toString("yyyy-MM-dd") + "'");
      List<VisitingRemind> visitingReminds = visitingRemindMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(visitingReminds)) {
        for (VisitingRemind visitingRemind :visitingReminds ){
          mapper.deleteByPrimaryKeyAndType(visitingRemind.getId(), type);
          BaseVisitRemind baseVisitRemindInfo = getBaseVisitRemindInfo(null,visitingRemind, type);
          baseVisitRemindLists.add(baseVisitRemindInfo);
        }
        insertList(baseVisitRemindLists);
      }
    }
  }

  /**
   * 批量插入随访提醒
   * @param baseVisitRemindLists 数据
   */
  public void insertList(List<BaseVisitRemind> baseVisitRemindLists) throws InterruptedException {
    List<List<BaseVisitRemind>> baseVisitRemindList = Lists.partition(baseVisitRemindLists, CUT_SLICE_100);
    CountDownLatch countDownLatch = new CountDownLatch(baseVisitRemindList.size());
    long start = System.currentTimeMillis();
    for (List<BaseVisitRemind> baseVisitReminds : baseVisitRemindList) {

      importExcelThreadPool.execute(
              () -> {
                try {
                  mapper.insertList(baseVisitReminds);
                } catch (Exception e) {
                  log.info("提醒随访迁移入库异常",e);
                  e.printStackTrace();
                }finally {
                  countDownLatch.countDown();
                }
              });
    }
    countDownLatch.await();
    long end = System.currentTimeMillis();
    log.info("随访/提醒信息入库，时长：[{}]秒", (end - start) / 1000);
  }

  /**
   * 获取-提醒/随访-基础信息-封装到中间表-返回
   *
   * @param type 提醒/随访
   * @return BaseVisitRemind
   */
  private BaseVisitRemind getBaseVisitRemindInfo(VisitingRecord visitingRecord,VisitingRemind visitingRemind, Integer type) {
    if (type == 0) {
      if (StringHelper.isNotNull(visitingRecord)) {
        BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
        baseVisitRemind.setRecordId(visitingRecord.getId());
        baseVisitRemind.setOrgId(visitingRecord.getOrgId());
        baseVisitRemind.setPatientId(visitingRecord.getPatientId());
        baseVisitRemind.setType((byte) type.intValue());
        baseVisitRemind.setUserId(visitingRecord.getCrtId());
        baseVisitRemind.setTime(visitingRecord.getExecuteDate());
        baseVisitRemind.setVisitingRemindTime(visitingRecord.getVisitingDate());
        baseVisitRemind.setContent(visitingRecord.getReason());
        baseVisitRemind.setCrtTime(visitingRecord.getCrtTime());
        return baseVisitRemind;
      }
      return null;
    }
    if (type == 1) {
      if (StringHelper.isNotNull(visitingRemind)) {
        BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
        baseVisitRemind.setRecordId(visitingRemind.getId());
        baseVisitRemind.setOrgId(visitingRemind.getOrgId());
        baseVisitRemind.setPatientId(visitingRemind.getPatientId());
        baseVisitRemind.setType((byte) type.intValue());
        baseVisitRemind.setUserId(visitingRemind.getCrtId());
        if (visitingRemind.getStatus()){
          baseVisitRemind.setTime(visitingRemind.getUpdTime());
        }
        baseVisitRemind.setVisitingRemindTime(visitingRemind.getRemindDate());
        baseVisitRemind.setContent(visitingRemind.getRemindContent());
        baseVisitRemind.setCrtTime(visitingRemind.getCrtTime());
        return baseVisitRemind;
      }
      return null;
    }
    return null;
  }

  /**
   * 获取-提醒/随访-基础信息-封装到中间表-返回
   *
   * @param type 提醒/随访
   * @return BaseVisitRemind
   */
  private BaseVisitRemind getBaseVisitRemindInfo(Integer id, Integer type) {
    if (type == 0) {
      VisitingRecord visitingRecord = visitingRecordMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(visitingRecord)) {
        BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
        baseVisitRemind.setRecordId(visitingRecord.getId());
        baseVisitRemind.setOrgId(visitingRecord.getOrgId());
        baseVisitRemind.setPatientId(visitingRecord.getPatientId());
        baseVisitRemind.setType((byte) type.intValue());
        baseVisitRemind.setUserId(visitingRecord.getCrtId());
        baseVisitRemind.setVisitingRemindTime(visitingRecord.getVisitingDate());
        baseVisitRemind.setTime(visitingRecord.getExecuteDate());
        baseVisitRemind.setContent(visitingRecord.getReason());
        baseVisitRemind.setCrtTime(visitingRecord.getCrtTime());
        return baseVisitRemind;
      }
      return null;
    }
    if (type == 1) {
      VisitingRemind visitingRemind = visitingRemindMapper.selectByPrimaryKey(id);
      if (StringHelper.isNotNull(visitingRemind)) {
        BaseVisitRemind baseVisitRemind = new BaseVisitRemind();
        baseVisitRemind.setRecordId(visitingRemind.getId());
        baseVisitRemind.setOrgId(visitingRemind.getOrgId());
        baseVisitRemind.setPatientId(visitingRemind.getPatientId());
        baseVisitRemind.setType((byte) type.intValue());
        baseVisitRemind.setUserId(visitingRemind.getCrtId());
        baseVisitRemind.setVisitingRemindTime(visitingRemind.getRemindDate());
        if(visitingRemind.getStatus()){
          baseVisitRemind.setTime(visitingRemind.getUpdTime());
        }
        baseVisitRemind.setContent(visitingRemind.getRemindContent());
        baseVisitRemind.setCrtTime(visitingRemind.getCrtTime());
        return baseVisitRemind;
      }
      return null;
    }
    return null;
  }

  public Object getVisitingInfo(Integer id, Integer type) {
    if (type == 0) {
      VisitingRecord visitingRecord = visitingRecordMapper.selectByPrimaryKey(id);
      return visitingRecord;
    }
    if (type == 1) {
      VisitingRemind visitingRemind = visitingRemindMapper.selectByPrimaryKey(id);
      return visitingRemind;
    }
    return null;
  }

  /**
   * 随访提醒中间表导入计划时间的字段数据
   */
  public int dsj(PullForm pullForm){
  FindAllRemindRecordVO findAllRemindRecordVO = remoteTreatmentOtherFeign.findAllRecord(pullForm);
    int a = 0;
    int b = 0;
    if(findAllRemindRecordVO.getRecordList()!=null && findAllRemindRecordVO.getRecordList().size()>0){
      a =  mapper.insertVisitingRecordTime(findAllRemindRecordVO.getRecordList());
    }
    if(findAllRemindRecordVO.getRemindList() !=null && findAllRemindRecordVO.getRemindList().size()>0){
      b = mapper.insertVisitingRemindTime(findAllRemindRecordVO.getRemindList());
    }
    return a+b;
  }
}
