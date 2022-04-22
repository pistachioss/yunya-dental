package com.yunya.middletable.service.patient;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.domain.vo.PatientTreatInfoVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.PatientBaseInfoMapper;
import com.yunya.middletable.dao.patient.PatientGroupRelationMapper;
import com.yunya.middletable.dao.patient.PatientOriginMapper;
import com.yunya.middletable.dao.patient.PatientPrepaymentsInfoMapper;
import com.yunya.middletable.dao.report.BasePatientGroupRelationMapper;
import com.yunya.middletable.dao.report.BasePatientMapper;
import com.yunya.middletable.dao.report.BasePatientMemberMapper;
import com.yunya.middletable.dao.system.DictionaryItemMapper;
import com.yunya.middletable.service.BaseTreatmentProcessBiz;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientGroupRelation;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import com.yunya.models.report.BasePatient;
import com.yunya.models.report.BasePatientGroupRelation;
import com.yunya.models.report.BasePatientMember;
import com.yunya.models.system.DictionaryItem;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


/**
 * 简介: 报表服务患者信息同步
 *
 * @author: WY
 * @date: 2020/10/15 13:23
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasePatientBiz extends BaseBiz<BasePatientMapper, BasePatient> {
  /** 注入对象 */
  @Resource private PatientBaseInfoMapper patientBaseInfoMapper;

  @Resource private PatientOriginMapper patientOriginMapper;

  @Resource private BasePatientMemberBiz basePatientMemberBiz;

  @Resource private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  @Resource private BasePatientMemberMapper basePatientMemberMapper;

  @Resource private PatientGroupRelationMapper patientGroupRelationMapper;

  @Resource private BasePatientGroupRelationMapper basePatientGroupRelationMapper;

  @Resource private DictionaryItemMapper dictionaryItemMapper;

  /** 多线程 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

  /**
   * 患者信息操作
   *
   * @param msg 消息
   */
  public void operate(MessageModel msg) {
    Integer patientId = (Integer) msg.getParamMap().get("id");
    Integer operateType = msg.getOperateType();
    BasePatient patient = generatePatientBaseInfo(patientId);
    switch (operateType) {
      case 0:
        mapper.delete(patient);
        mapper.insertSelective(patient);
        addPrepaidInfo(patient);
        break;
      case 1:
        assert patient != null;
        mapper.updateByPrimaryKeySelective(patient);
        savePatientGroupRelation(patientId);
        break;
      case 2:
        PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientId);
        if (StringHelper.isNotNull(patientBaseInfo) && StringHelper.isNotNull(patient)) {
          mapper.delete(patient);
          mapper.insertSelective(patient);
        }
        mapper.delete(patient);
        break;
      default:
        break;
    }
  }


  /**
   * 保存患者分组关系
   *
   * @param patientId
   */
  public void savePatientGroupRelation(Integer patientId) {
    Example example = new Example(PatientGroupRelation.class);
    Example.Criteria c = example.createCriteria();
    c.andEqualTo("patientId", patientId);
    List<PatientGroupRelation> groups = patientGroupRelationMapper.selectByExample(example);
    if (StringHelper.isNotEmpty(groups)) {
      basePatientGroupRelationMapper.deleteByExample(example);
      groups.forEach(group->{
        BasePatientGroupRelation entity = new BasePatientGroupRelation();
        BeanUtils.copyProperties(group, entity);
        DictionaryItem dictionaryItem = dictionaryItemMapper.selectByPrimaryKey(entity.getGroupId());
        if (!ObjectUtils.isEmpty(dictionaryItem)) {
          entity.setGroupName(dictionaryItem.getName());
        }
        entity.setId(null);
        basePatientGroupRelationMapper.insertSelective(entity);
      });
    }
  }

  public Date getDateTime(String dateStr){
    Date date = null;
    //获得SimpleDateFormat类，我们转换为yyyy-MM-dd的时间格式
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
    //使用SimpleDateFormat的parse()方法生成Date
     date = sf.parse(dateStr);
    //打印Date
      System.out.println(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  /**
   * 添加患者信息是添加预付款信息
   *
   * @param patient 患者信息
   */
  public void addPrepaidInfo(BasePatient patient) {
    if (patient != null){
      PatientPrepaymentsInfo patientPrepaymentsInfo = new PatientPrepaymentsInfo();
      patientPrepaymentsInfo.setPatientId(patient.getPatientId());
      PatientPrepaymentsInfo patientPrepayments =
              patientPrepaymentsInfoMapper.selectOne(patientPrepaymentsInfo);
      if (patientPrepayments != null) {
        BasePatientMember basePatientMember =
                basePatientMemberBiz.getPatientMemberInfo(patientPrepayments.getId(), 1);
        basePatientMemberMapper.deleteByPrimaryKey(basePatientMember);
        basePatientMemberMapper.insert(basePatientMember);
      }
    }
  }

  /**
   * 构建中间表组织信息
   *
   * @param patientId 患者id
   */
  private BasePatient generatePatientBaseInfo(Integer patientId) {
    PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientId);
    return null != patientBaseInfo ? setPatientBaseInfo(patientId) : null;
  }

  /**
   * 设置患者信息属性
   *
   * @param patientId 患者信息
   * @return BasePatient
   */
  private BasePatient setPatientBaseInfo(Integer patientId) {
    PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientId);
    if (null != patientBaseInfo) {
      BasePatient basePatient = new BasePatient();
      basePatient.setPatientId(patientBaseInfo.getId());
      basePatient.setOrgId(patientBaseInfo.getOrgId());
      basePatient.setName(patientBaseInfo.getName());
      basePatient.setMobile(patientBaseInfo.getMobile());
      basePatient.setMedicalNumber(patientBaseInfo.getMedicalNumber());
      if (patientBaseInfo.getBirthday() != null) {
        basePatient.setBirthday(new DateTime(patientBaseInfo.getBirthday()).toDate());
      }
      if (patientBaseInfo.getOriginId() != null) {
        basePatient.setOriginType(patientBaseInfo.getOriginType());
        basePatient.setOriginId(patientBaseInfo.getOriginId());
        PatientOrigin patientOrigin = new PatientOrigin();
        patientOrigin.setParentId(0);
        patientOrigin.setOriginType(patientBaseInfo.getOriginType());
        PatientOrigin origin = patientOriginMapper.selectOne(patientOrigin);
        if (origin != null) {
          basePatient.setOriginTypeName(origin.getName());
        }
      }
      if (patientBaseInfo.getFaceUrl() != null) {
        basePatient.setFaceUrl(patientBaseInfo.getFaceUrl());
      }
      basePatient.setGender(patientBaseInfo.getGender());
      basePatient.setPinyinName(patientBaseInfo.getPinyinName());
      basePatient.setPatientCrtTime(patientBaseInfo.getCrtTime());
      return basePatient;
    }
    return null;
  }

  /**
   * 拉取某段时间内的组织数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullPatientData(PullForm form) throws InterruptedException {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example emp = new Example(PatientBaseInfo.class);
    emp.createCriteria().andBetween("updTime", startDate, endDate);
    List<PatientBaseInfo> patientBaseInfos = patientBaseInfoMapper.selectByExample(emp);
    if (StringHelper.isNotEmpty(patientBaseInfos)) {
      CountDownLatch latch = new CountDownLatch(patientBaseInfos.size());
      List<Future> resultFutures = new ArrayList<>();
      resultFutures.add(
          importExcelThreadPool.submit(
              () -> {
                try {
                  patientBaseInfos.forEach(
                      patientBaseInfo -> {
                        Integer patientId = patientBaseInfo.getId();
                        mapper.deleteByPrimaryKey(patientId);
                        BasePatient patient = setPatientBaseInfo(patientId);
                        mapper.insertSelective(patient);
                      });
                } finally {
                  latch.countDown();
                }
              }));
      latch.await();
      BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);
    }
  }

  /**
   * 修改患者信息
   *
   * @param basePatient 患者信息
   */
  public void upd(BasePatient basePatient) {
    mapper.updateByPrimaryKeySelective(basePatient);
  }

  /**
   * 查询患者资料信息
   *
   * @param id 患者id
   * @return BasePatient
   */
  public BasePatient selectPatientInfo(Integer id) {
    BasePatient basePatient = new BasePatient();
    basePatient.setPatientId(id);
    return mapper.selectOne(basePatient);
  }


  /**
   * 批量更新患者信息
   */
  public void updPatientInfo() throws InterruptedException {
    // 查询有过初诊的患者id
    List<BasePatient> patientIdList  = mapper.selectPatientIdList();
    // k 患者id v 初诊信息
    Map<Integer, PatientTreatInfoVo> firstInfoMap = getFirstInfoMap();
    // k 患者id v 末诊信息
    Map<Integer, PatientTreatInfoVo> lastTreatInfoMap = getLastTreatInfoMap();

    if (!StringHelper.isEmpty(patientIdList)){
     List<List<BasePatient>> partitionLists = Lists.partition(patientIdList, 100);
     CountDownLatch countDownLatch = new CountDownLatch(partitionLists.size());
     long start = System.currentTimeMillis();
     for (List<BasePatient> basePatientList: partitionLists) {
       importExcelThreadPool.execute(() ->{
         try {
           mapper.updatePatientInfoList(basePatientList);
           countDownLatch.countDown();
         } catch (Exception e) {
           log.info("患者信息批量修改异常",e);
         }
       });
     }
     countDownLatch.await();
     long end = System.currentTimeMillis();
     log.info("患者信息批量修改完成，时长：[{}]秒",(end - start) / 1000);
   }

  }

  /**
   * 封装患者初诊信息
   * @return k 患者id v 初诊信息
   */
  private Map<Integer, PatientTreatInfoVo> getFirstInfoMap() {
    // 初诊信息
    List<PatientTreatInfoVo> firstInfoVoList = mapper.selectFirstVisitInfoList();
    return firstInfoVoList.stream().collect(toMap(PatientTreatInfoVo::getPatientId, Function.identity()));
  }

  /**
   * 封装患者末诊信息
   * @return k 患者id v 末诊信息
   */
  private Map<Integer, PatientTreatInfoVo> getLastTreatInfoMap() {
    // 末诊信息
    List<PatientTreatInfoVo> firstInfoVoList = mapper.selectFirstVisitInfoList();
    return firstInfoVoList.stream().collect(toMap(PatientTreatInfoVo::getPatientId, Function.identity()));
  }
}
