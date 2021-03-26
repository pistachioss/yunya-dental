package com.yunya.middletable.service.patient;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.PatientOriginLogMapper;
import com.yunya.middletable.dao.report.BasePatientOriginLogMapper;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.patient_central.PatientOriginLog;
import com.yunya.models.report.BasePatientOriginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 简介:患者来源中间表同步
 *
 * @author: WY
 * @date: 2021/3/15 11:04
 * @description: 患者来源中间表同步-业务层
 * @since: 1.0.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasePatientOriginLogBiz extends BaseBiz<BasePatientOriginLogMapper, BasePatientOriginLog> {

    @Resource
    private PatientOriginLogMapper patientOriginLogMapper;

    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;


    /**
     * 患者来源推荐关系中间表数据同步
     * @param model 消息信息
     */
    public void operate(MessageModel model) {
        // 操作类型（0-新增 1-修改 2-删除）
        Integer operateType = model.getOperateType();
        Integer id = (Integer)model.getParamMap().get("id");
        PatientOriginLog patientOriginLog = patientOriginLogMapper.selectByPrimaryKey(id);
        if (patientOriginLog != null){
            BasePatientOriginLog basePatientOriginLog = getBasePatientOriginLog(patientOriginLog);
            switch (operateType){
                case 0:
                    mapper.delete(basePatientOriginLog);
                    mapper.insertSelective(basePatientOriginLog);
                    break;
                case 1:
                    mapper.updateByPrimaryKeySelective(basePatientOriginLog);
                    break;
                case 2:
                    mapper.delete(basePatientOriginLog);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 获取中间表bean
     * @param patientOriginLog 基础bean
     * @return 中间表bean
     */
    private BasePatientOriginLog getBasePatientOriginLog(PatientOriginLog patientOriginLog) {
        BasePatientOriginLog basePatientOriginLog = new BasePatientOriginLog();
        BeanUtils.copyProperties(patientOriginLog,basePatientOriginLog);
        return basePatientOriginLog;
    }


    /**
     * 患者来源推荐关系中间表数据批量同步
     * @param form 间隔时间
     */
    public void pullPatientData(PullForm form) throws InterruptedException {
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        Example emp = new Example(PatientOrigin.class);
        emp.createCriteria().andBetween("updTime",startDate,endDate);
        List<PatientOriginLog> patientOriginLogLists = patientOriginLogMapper.selectByExample(emp);
        if (StringHelper.isNotNull(patientOriginLogLists)){
            List<List<PatientOriginLog>> partitionLists = Lists.partition(patientOriginLogLists, 100);
            CountDownLatch countDownLatch = new CountDownLatch(partitionLists.size());
            long start = System.currentTimeMillis();
            for (List<PatientOriginLog>  patientOriginLogList:  partitionLists) {
                importExcelThreadPool.execute(() ->{
                   try{
                       mapper.deleteList(patientOriginLogList);
                       mapper.insertList(patientOriginLogList);
                   }catch (Exception e){
                       log.info("患者来源推荐关系中间表数据批量同步入库异常",e);
                   }finally{
                       countDownLatch.countDown();
                   }

                });
            }
            countDownLatch.await();
            long end =System.currentTimeMillis();
            log.info("患者来源推荐关系中间表数据批量同步入库完成,时长：[{}]秒",(end-start) /1000);
        }
    }
}