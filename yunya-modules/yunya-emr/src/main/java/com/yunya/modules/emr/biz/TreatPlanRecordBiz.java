package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.TreatPlanRecordChangeForm;
import com.yunya.feign.emr.domain.model.TreatPlanDetailModel;
import com.yunya.feign.emr.domain.model.TreatPlanRecordModel;
import com.yunya.feign.emr.domain.model.TreatPlanStepModel;
import com.yunya.feign.emr.domain.query.TreatPlanRecordQuery;
import com.yunya.feign.emr.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.FileSourceTypeEnum;
import com.yunya.framework.common.enums.OperationTypeEnum;
import com.yunya.framework.common.enums.TreatPlanStatusEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.emr.TreatPlanRecord;
import com.yunya.models.emr.TreatPlanRecordHistory;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.emr.mapper.TreatPlanRecordHistoryMapper;
import com.yunya.modules.emr.mapper.TreatPlanRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private MedicalCommonRecordBiz medicalCommonRecordBiz;
    @Autowired
    private RemoteTreatmentOtherFeign remoteTreatmentOtherFeign;

    /**
     * 保存治疗计划
     *
     * @param model
     * @param isChange 方案是否变更
     * @return
     */
    public void save(TreatPlanRecordModel model, Byte isChange) {
        Date now = new Date(System.currentTimeMillis());
        Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
        TreatPlanRecord query = new TreatPlanRecord();
        query.setMedicalRecordId(model.getMedicalRecordId());
        TreatPlanRecord entity = mapper.selectOne(query);
        boolean hasOld = !ObjectUtils.isEmpty(entity);
        boolean hasNew = !ObjectUtils.isEmpty(model);
        if (hasOld || hasNew) {// 未添加治疗计划
            Byte operation = OperationTypeEnum.INSERT.getCode();
            if (hasOld) {// 修改
                mapper.deleteByPrimaryKey(entity.getId());
                if (hasNew) {// 修改未删除
                    addTreatPlanRecord(entity, model, userId, now);
                    operation = OperationTypeEnum.UPDATE.getCode();
                } else { // 删除
                    operation = OperationTypeEnum.DELETE.getCode();
                }
            } else if (hasNew) {// 新增
                entity = new TreatPlanRecord();
                entity.setCrtId(userId);
                entity.setCrtTime(now);
                addTreatPlanRecord(entity, model, userId, now);
            }
            addTreatPlanHistory(entity, model.getOperationReason(), isChange, operation, userId, now);
            treatPlanStepBiz.save(entity.getId(), model.getTreatPlanSteps());
        }
    }

    /**
     * 添加治疗计划操作记录
     * @param entity
     * @param operationReason
     * @param isChange
     * @param operation
     * @param userId
     * @param now
     */
    private void addTreatPlanHistory(TreatPlanRecord entity, String operationReason, Byte isChange, Byte operation, Integer userId, Date now) {
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
        history.setOperationReason(StringHelper.isEmpty(operationReason)?"":operationReason);
        history.setIsChange(isChange);
        history.setCrtId(userId);
        history.setCrtTime(now);
        treatPlanRecordHistoryMapper.insertSelective(history);
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
        entity.setStatus(model.getStatus());
        entity.setRemark(model.getRemark());
        entity.setUptId(userId);
        entity.setUptTime(now);
        entity.setDentistId(model.getDentistId());
        mapper.insertSelective(entity);
    }

    /**
     * 根据治疗计划id查询
     *
     * @param planId
     * @return
     */
    public MedicalTreatPlanRecordVO findOneById(Integer planId) {
        TreatPlanRecord entity = mapper.selectByPrimaryKey(planId);
        if (ObjectUtils.isEmpty(entity)) {
            throw new ClientServiceException("该治疗计划不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        MedicalTreatPlanRecordVO result = entity2VO(entity);
        putTreatPlanStepList(result, planId);
        return result;
    }

    /**
     * 根据治疗计划id查询上一次变更前数据
     *
     * @param planId
     * @param status
     * @return
     */
    public MedicalTreatPlanRecordVO findPreOneById(Integer planId, Byte status) {
        TreatPlanRecord entity = treatPlanRecordHistoryMapper.selectTreatPlanHistoryPreOneById(planId, status);
        if (ObjectUtils.isEmpty(entity)) {
            throw new ClientServiceException("该治疗计划不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        MedicalTreatPlanRecordVO result = entity2VO(entity);
        putPreTreatPlanStepList(result, planId, status);
        return result;
    }

    /**
     * 查询并填充治疗步骤和明细
     *
     * @param result
     * @param planId
     */
    private void putTreatPlanStepList(MedicalTreatPlanRecordVO result, Integer planId) {
        List<TreatPlanStepVO> steps = treatPlanStepBiz.findTreatPlanStepByPlanId(planId);
        if (StringHelper.isNotEmpty(steps)) {
            accumulation(steps, result);
        }
    }

    /**
     * 查询并填充治疗步骤和明细
     *  @param result
     * @param planId
     * @param status
     */
    private void putPreTreatPlanStepList(MedicalTreatPlanRecordVO result, Integer planId, Byte status) {
        List<TreatPlanStepVO> steps = treatPlanStepBiz.findTreatPlanStepPreByPlanId(planId, status);
        if (StringHelper.isNotEmpty(steps)) {
            accumulation(steps, result);
        }
    }

    private void accumulation(List<TreatPlanStepVO> steps, MedicalTreatPlanRecordVO result) {
        int totalQuantity = 0;
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (TreatPlanStepVO step : steps) {
            totalQuantity += step.getQuanity();
            totalAmount = totalAmount.add(step.getAmount());
        }
        result.setTreatPlanSteps(steps);
        result.setTotalQuanity(totalQuantity);
        result.setTotalAmount(totalAmount);
    }

    /**
     * 数据转换
     * @param entity
     * @return
     */
    private MedicalTreatPlanRecordVO entity2VO(TreatPlanRecord entity) {
        MedicalTreatPlanRecordVO result = new MedicalTreatPlanRecordVO();
        result.setPlanId(entity.getId());
        result.setPlanName(entity.getPlanName());
        result.setSummary(entity.getSummary());
        result.setDentistId(entity.getDentistId());
        result.setMedicalRecordId(entity.getMedicalRecordId());
        OrganizationInfo org = remoteSystemServiceFeign.findOrgInfoByOrgId(entity.getOrgId());
        if (!ObjectUtils.isEmpty(org)) {
            result.setAbbreviation(org.getAbbreviation());
        }
        Integer dentistId = entity.getDentistId();
        if (!ObjectUtils.isEmpty(dentistId)) {
            SysEmployee dentist = remoteSystemServiceFeign.findSysEmployeeById(dentistId);
            if (!ObjectUtils.isEmpty(dentist)) {
                result.setDentistName(dentist.getName());
            }
        }
        Integer crtId = entity.getCrtId();
        if (!ObjectUtils.isEmpty(crtId)) {
            SysEmployee crtEmp = remoteSystemServiceFeign.findSysEmployeeById(crtId);
            if (!ObjectUtils.isEmpty(crtEmp)) {
                result.setCrtName(crtEmp.getName());
            }
        }
        Integer uptId = entity.getUptId();
        if (!ObjectUtils.isEmpty(uptId)) {
            SysEmployee uptEmp = remoteSystemServiceFeign.findSysEmployeeById(uptId);
            if (!ObjectUtils.isEmpty(uptEmp)) {
                result.setUptName(uptEmp.getName());
            }
        }
        result.setCrtTime(entity.getCrtTime());
        result.setUptTime(entity.getUptTime());
        result.setStatus(entity.getStatus());
        result.setRemark(entity.getRemark());
        return result;
    }

    /**
     * 分页条件查询
     *
     * @param query
     * @return
     */
    public PageInfo<TreatPlanRecordInfoVO> findTreatPlanRecordInfoList(TreatPlanRecordQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<TreatPlanRecord> list = mapper.selectTreatPlanRecordInfoList(query);
        PageInfo pageInfo = new PageInfo(list);
        if (StringHelper.isNotEmpty(list)) {
            List<TreatPlanRecordInfoVO> result = new ArrayList<>();
            list.forEach(entity->{
                TreatPlanRecordInfoVO vo = new TreatPlanRecordInfoVO();
                vo.setPlanId(entity.getId());
                vo.setPlanName(entity.getPlanName());
                vo.setStatus(entity.getStatus());
                vo.setCrtTime(entity.getCrtTime());
                OrganizationInfo org = remoteSystemServiceFeign.findOrgInfoByOrgId(entity.getOrgId());
                if (!ObjectUtils.isEmpty(org)) {
                    vo.setAbbreviation(org.getAbbreviation());
                }
                SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(entity.getDentistId());
                if (!ObjectUtils.isEmpty(employee)) {
                    vo.setDentistName(employee.getName());
                }
                result.add(vo);
            });
            pageInfo.setList(result);
        }
        return pageInfo;
    }

    /**
     * 根据治疗计划id查询病历及治疗计划
     *
     * @param planId
     * @return
     */
    public MedicalTreatPlanRecordVO findMedicalTreatPlanById(Integer planId) {
        MedicalTreatPlanRecordVO result = findMedicalTreatPlanBaseInfoById(planId);
        // 照片影像列表
        XUploadFileQuery query = new XUploadFileQuery();
        query.setWhetherPage(false);
        query.setSourceType(FileSourceTypeEnum.MEDICAL_COMMON.getCode());
        Integer medicalRecordId = result.getMedicalRecordId();
        query.setSourceIds(Arrays.asList(medicalRecordId));
        result.setXRayFilms(remoteTreatmentOtherFeign.findXUploadFileList(query));
        return result;
    }

    private MedicalTreatPlanRecordVO findMedicalTreatPlanBaseInfoById(Integer planId) {
        MedicalTreatPlanRecordVO result = findOneById(planId);
        Integer medicalRecordId = result.getMedicalRecordId();
        MedicalCommonRecord medical = medicalCommonRecordBiz.findMedicalIllegaHistoryById(medicalRecordId);
        if (!ObjectUtils.isEmpty(medical)) {
            // 主述、现病史、既往史、检查、诊断
            result.setChiefComplaint(medical.getChiefComplaint());
            result.setPastHistory(medical.getPastHistory());
            result.setPresentIllness(medical.getPresentIllness());
            result.setExamination(json2List(medical.getExamination()));
            result.setDiagnosis(json2List(medical.getDiagnosis()));
        }
        return result;
    }

    /**
     * json格式数据转List
     *
     * @param jsonStr
     * @return
     */
    private List<ExaminationsVO> json2List(String jsonStr) {
        if (StringHelper.isNotEmpty(jsonStr)) {
            JSONArray jsonArray = JSONArray.parseArray(jsonStr);
            return jsonArray.toJavaList(ExaminationsVO.class);
        }
        return null;
    }

    /**
     * 治疗计划变更
     *
     * @param form
     * @return
     */
    public MedicalTreatPlanRecordVO treatPlanChange(TreatPlanRecordChangeForm form) {
        Integer planId = form.getPlanId();
        MedicalTreatPlanRecordVO treatPlan = findMedicalTreatPlanById(planId);
        Byte status = treatPlan.getStatus();
        Byte changeType = form.getChangeType();
        if (changeType == 0) {// 方案确认
            checkStatus(status, TreatPlanStatusEnum.UNCONFIRM);
            consistencyUpdateStatus(treatPlan, TreatPlanStatusEnum.CONFIRMED.getCode());
            treatPlan.setStatus(TreatPlanStatusEnum.CONFIRMED.getCode());
            return treatPlan;
        } else if (changeType == 1) {// 方案变更
            checkStatus(status, TreatPlanStatusEnum.COMPLETED, TreatPlanStatusEnum.TERMINATION);
            // 已完成的项目不变
            TreatPlanRecordModel model = new TreatPlanRecordModel();
            model.setTreatPlanId(planId);
            model.setTreatPlanSteps(form.getDetails());
            save(model, (byte) 1);
        } else if (changeType == 2) {// 提前终止
            checkStatus(status, TreatPlanStatusEnum.COMPLETED, TreatPlanStatusEnum.TERMINATION);
            // 终止计划，终止步骤，终止项目
            consistencyUpdateStatus(treatPlan, TreatPlanStatusEnum.TERMINATION.getCode());
        } else {// 撤销终止
            checkStatus(status, TreatPlanStatusEnum.TERMINATION);
            // 恢复计划，步骤，项目在终止前的状态
            treatPlan = findPreOneById(planId, TreatPlanStatusEnum.TERMINATION.getCode());
            updateStatus(treatPlan);
        }
        return null;
    }

    private void updateStatus(MedicalTreatPlanRecordVO treatPlan) {
        TreatPlanRecordModel model = new TreatPlanRecordModel();
        model.setTreatPlanId(treatPlan.getPlanId());
        model.setStatus(treatPlan.getStatus());
        detailVO2Model(model, treatPlan.getTreatPlanSteps(), null);
        save(model, null);
    }

    /**
     * 一致性更新状态
     *
     * @param treatPlan
     * @param consistencyStatus
     */
    private void consistencyUpdateStatus(MedicalTreatPlanRecordVO treatPlan, Byte consistencyStatus) {
        TreatPlanRecordModel model = new TreatPlanRecordModel();
        model.setTreatPlanId(treatPlan.getPlanId());
        model.setStatus(TreatPlanStatusEnum.TERMINATION.getCode());
        detailVO2Model(model, treatPlan.getTreatPlanSteps(), consistencyStatus);
        save(model, null);
    }

    private void detailVO2Model(TreatPlanRecordModel model, List<TreatPlanStepVO> treatPlanSteps, Byte consistencyStatus) {
        if (StringHelper.isNotEmpty(treatPlanSteps)) {
            List<TreatPlanStepModel> steps = new ArrayList<>();
            boolean consistency = ObjectUtils.isEmpty(consistencyStatus);
            treatPlanSteps.forEach(step->{
                TreatPlanStepModel stepModel = new TreatPlanStepModel();
                stepModel.setTreatPlanStepId(step.getTreatPlanStepId());
                Byte status = step.getStatus();
                if (consistency) {
                    status = consistencyStatus;
                }
                stepModel.setStatus(status);
                List<TreatPlanDetailModel> detailModels = new ArrayList<>();
                List<TreatPlanDetailVO> details = step.getTreatPlanDetails();
                details.forEach(detail->{
                    TreatPlanDetailModel detailModel = new TreatPlanDetailModel();
                    detailModel.setDetailId(detail.getDetailId());
                    Byte detailStatus = step.getStatus();
                    if (consistency) {
                        detailStatus = consistencyStatus;
                    }
                    detailModel.setStatus(detailStatus);
                    detailModels.add(detailModel);
                });
                stepModel.setTreatPlanDetails(detailModels);
                steps.add(stepModel);
            });
            model.setTreatPlanSteps(steps);
        }
    }

    /**
     * 校验状态
     *
     * @param status
     * @param statusEnums
     */
    private void checkStatus(Byte status, TreatPlanStatusEnum... statusEnums) {
        for (TreatPlanStatusEnum statusEnum : statusEnums) {
            if (!statusEnum.equals(status)) {
                throw new ClientServiceException("该治疗计划状态不允许操作", OperationCodeConstants.OPERATION_NOT_ALLOW);
            }
        }
    }

    /**
     * 查询患者所有已确认、进行中的治疗计划列表
     *
     * @param query
     * @return
     */
    public PageInfo<TreatPlanRecordVO> findPatientTreatPlanList(TreatPlanRecordQuery query) {
        List<Byte> status = Arrays.asList(TreatPlanStatusEnum.CONFIRMED.getCode(), TreatPlanStatusEnum.EXECUTING.getCode());
        query.setStatus(status);
        List<TreatPlanRecord> list = mapper.selectTreatPlanRecordInfoList(query);
        List<TreatPlanRecordVO> result = new ArrayList<>();
        if (StringHelper.isNotEmpty(list)) {
            list.forEach(entity->{
                MedicalTreatPlanRecordVO vo = entity2VO(entity);
                putTreatPlanStepList(vo, entity.getId());
                result.add(vo);
            });
        }
        return new PageInfo<>(result);
    }
}
