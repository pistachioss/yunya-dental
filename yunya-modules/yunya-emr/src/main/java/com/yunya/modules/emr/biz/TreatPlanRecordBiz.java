package com.yunya.modules.emr.biz;

import com.yunya.feign.emr.domain.model.TreatPlanRecordModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.OperationTypeEnum;
import com.yunya.framework.common.enums.TreatPlanStatusEnum;
import com.yunya.models.emr.TreatPlanRecord;
import com.yunya.models.emr.TreatPlanRecordHistory;
import com.yunya.modules.emr.mapper.TreatPlanRecordHistoryMapper;
import com.yunya.modules.emr.mapper.TreatPlanRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;

/**
 * 简介：治疗计划业务层
 *
 * @author: chenlin
 * @Description: 治疗计划业务层
 * @Date: 2022/1/11 10:01
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TreatPlanRecordBiz extends BaseBiz<TreatPlanRecordMapper, TreatPlanRecord> {
    /** 治疗计划历史记录*/
    @Autowired
    private TreatPlanRecordHistoryMapper treatPlanRecordHistoryMapper;
    /** 治疗计划步骤*/
    @Autowired
    private TreatPlanStepBiz treatPlanStepBiz;

    /**
     * 保存治疗计划
     *
     * @param model
     * @return
     */
    public void save(TreatPlanRecordModel model) {
        Date now = new Date(System.currentTimeMillis());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        TreatPlanRecord query = new TreatPlanRecord();
        query.setMedicalRecordId(model.getMedicalRecordId());
        TreatPlanRecord entity = mapper.selectOne(query);
        if (ObjectUtils.isEmpty(entity) && ObjectUtils.isEmpty(model)) {// 未添加治疗计划
            Byte operation = OperationTypeEnum.INSERT.getCode();
            if (!ObjectUtils.isEmpty(entity)) {// 修改
                mapper.deleteByPrimaryKey(entity.getId());
                if (!ObjectUtils.isEmpty(model)) {// 修改未删除
                    addTreatPlanRecord(entity, model, userId, now);
                    operation = OperationTypeEnum.UPDATE.getCode();
                } else { // 删除
                    operation = OperationTypeEnum.DELETE.getCode();
                }
            } else if (!ObjectUtils.isEmpty(model)) {// 新增
                entity = new TreatPlanRecord();
                entity.setCrtId(userId);
                entity.setCrtTime(now);
                addTreatPlanRecord(entity, model, userId, now);
            }
            addTreatPlanHistory(entity, operation, userId, now);
            treatPlanStepBiz.save(entity.getId(), model.getTreatPlanSteps());
        }
    }

    /**
     * 添加治疗计划操作记录
     *
     * @param entity
     * @param operation
     * @param userId
     * @param now
     */
    private void addTreatPlanHistory(TreatPlanRecord entity, Byte operation, Integer userId, Date now) {
        TreatPlanRecordHistory history = new TreatPlanRecordHistory();
        history.setMedicalRecordId(entity.getMedicalRecordId());
        history.setPlanId(entity.getId());
        history.setPlanName(entity.getPlanName());
        history.setSummary(entity.getSummary());
        history.setStatus(entity.getStatus());
        history.setPatientId(entity.getPatientId());
        history.setDentistId(entity.getDentistId());
        history.setRemark(entity.getRemark());
        history.setOrgId(entity.getOrgId());
        history.setOperation(operation);
        history.setWay(entity.getWay());
        history.setCrtId(userId);
        history.setCrtTime(now);
        treatPlanRecordHistoryMapper.insert(history);
    }

    /**
     * 添加治疗计划
     *
     * @param entity
     * @param model
     * @param userId
     * @param now
     */
    private void addTreatPlanRecord(TreatPlanRecord entity, TreatPlanRecordModel model, Integer userId, Date now) {
        entity.setOrgId(model.getOrgId());
        entity.setPatientId(model.getPatientId());
        entity.setMedicalRecordId(model.getMedicalRecordId());
        entity.setPlanName(model.getPlanName());
        entity.setSummary(model.getSummary());
        Byte status = TreatPlanStatusEnum.UNCONFIRM.getCode();
        if (model.getPatientIsConfirm()) {
            status = TreatPlanStatusEnum.CONFIRMED.getCode();
        }
        entity.setStatus(status);
        entity.setRemark(model.getRemark());
        entity.setUptId(userId);
        entity.setUptTime(now);
        entity.setDentistId(model.getDentistId());
        mapper.insert(entity);
    }
}
