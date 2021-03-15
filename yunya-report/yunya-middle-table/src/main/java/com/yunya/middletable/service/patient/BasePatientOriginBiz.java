package com.yunya.middletable.service.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.PatientOriginMapper;
import com.yunya.middletable.dao.report.BasePatientOriginMapper;
import com.yunya.middletable.service.BaseTreatmentProcessBiz;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.report.BasePatientOrigin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
public class BasePatientOriginBiz extends BaseBiz<BasePatientOriginMapper, BasePatientOrigin> {

    @Resource
    private PatientOriginMapper patientOriginMapper;

    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;


    /**
     * 患者来源中间表数据同步
     * @param model 消息信息
     */
    public void operate(MessageModel model) {
        // 操作类型（0-新增 1-修改 2-删除）
        Integer operateType = model.getOperateType();
        Integer id = (Integer)model.getParamMap().get("id");
        PatientOrigin patientOrigin = patientOriginMapper.selectByPrimaryKey(id);
        if (patientOrigin != null){
            BasePatientOrigin basePatientOrigin = getBasePatientOrigin(patientOrigin);
            switch (operateType){
                case 0:
                   mapper.delete(basePatientOrigin);
                   mapper.insertSelective(basePatientOrigin);
                    break;
                case 1:
                    mapper.updateByPrimaryKeySelective(basePatientOrigin);
                    break;
                case 2:
                    mapper.delete(basePatientOrigin);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 获取中间表bean
     * @param patientOrigin 基础bean
     * @return 中间表bean
     */
    private BasePatientOrigin getBasePatientOrigin(PatientOrigin patientOrigin) {
        BasePatientOrigin basePatientOrigin = new BasePatientOrigin();
        BeanUtils.copyProperties(patientOrigin,basePatientOrigin);
        return basePatientOrigin;
    }


    /**
     * 患者来源中间表时间段数据同步
     * @param form 间隔时间
     */
    public void pullPatientData(PullForm form) throws InterruptedException {
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        Example emp = new Example(PatientOrigin.class);
        emp.createCriteria().andBetween("updTime",startDate,endDate);
        List<PatientOrigin> patientOriginList = patientOriginMapper.selectByExample(emp);
        if (StringHelper.isNotNull(patientOriginList)){
            CountDownLatch latch = new CountDownLatch(patientOriginList.size());
            List<Future> resultFutures = new ArrayList<>();
            resultFutures.add(importExcelThreadPool.submit(
                    () -> {
                        try{
                            patientOriginList.forEach(
                                    patientOrigin -> {
                                        BasePatientOrigin basePatientOrigin = getBasePatientOrigin(patientOrigin);
                                        mapper.delete(basePatientOrigin);
                                        mapper.insertSelective(basePatientOrigin);
                                    }
                            );
                        }finally{
                            latch.countDown();
                        }
                    }
            ));
            latch.await();
            BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);
        }
    }
}