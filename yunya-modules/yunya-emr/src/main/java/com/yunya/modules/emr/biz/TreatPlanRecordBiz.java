package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.TreatPlanRecordChangeForm;
import com.yunya.feign.emr.domain.model.*;
import com.yunya.feign.emr.domain.query.TreatPlanRecordQuery;
import com.yunya.feign.emr.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.ClinicMemberPriceQuery;
import com.yunya.feign.treatment.domain.vo.ClinicItemPriceVO;
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
import com.yunya.models.emr.*;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.emr.mapper.TreatPlanRecordHistoryMapper;
import com.yunya.modules.emr.mapper.TreatPlanRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;

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
    /** 普通电子病历 */
    @Autowired
    private MedicalCommonRecordBiz medicalCommonRecordBiz;
    /** 系统*/
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    /** 其他*/
    @Autowired
    private RemoteTreatmentOtherFeign remoteTreatmentOtherFeign;
    /** 就诊*/
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    /** 系统*/
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;
    /** 治疗计划明细*/
    @Autowired
    private TreatPlanDetailBiz treatPlanDetailBiz;

    /**
     * 保存治疗计划（添加、修改、删除、方案变更（确认、终止、撤销））
     *
     * @param model
     * @param isChange 方案是否变更
     * @param needAll 是否保存全部（治疗计划、步骤、详情），false-只保存治疗计划
     * @return
     */
    public void save(TreatPlanRecordModel model, Byte isChange, boolean needAll) {
        String userID = BaseContextHandler.getUserID();
        Integer userId = model.getCrtId();
        if (StringHelper.isNotEmpty(userID)) {
            userId = Integer.parseInt(userID);
        }
        Date now = new Date(System.currentTimeMillis());
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
            if (needAll) {
                treatPlanStepBiz.save(entity.getId(), userId, model.getTreatPlanSteps());
            }
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
        /*Integer orgId = model.getOrgId();
        if (!ObjectUtils.isEmpty(orgId)) {
            entity.setOrgId(orgId);
        }
        Integer patientId = model.getPatientId();
        if (!ObjectUtils.isEmpty(patientId)) {
            entity.setPatientId(patientId);
        }
        Integer medicalRecordId = model.getMedicalRecordId();
        if (!ObjectUtils.isEmpty(medicalRecordId)) {
            entity.setMedicalRecordId(medicalRecordId);
        }
        String planName = model.getPlanName();
        if (!ObjectUtils.isEmpty(planName)) {
            entity.setPlanName(planName);
        }
        String summary = model.getSummary();
        if (!ObjectUtils.isEmpty(summary)) {
            entity.setSummary(summary);
        }
        Integer status = model.getStatus();
        if (!ObjectUtils.isEmpty(status)) {
            entity.setStatus(status.byteValue());
        }
        String remark = model.getRemark();
        if (!ObjectUtils.isEmpty(remark)) {
            entity.setRemark(remark);
        }
        Integer dentistId = model.getDentistId();
        if (!ObjectUtils.isEmpty(dentistId)) {
            entity.setDentistId(dentistId);
        }*/
        entity.setOrgId(model.getOrgId());
        entity.setPatientId(model.getPatientId());
        entity.setMedicalRecordId(model.getMedicalRecordId());
        entity.setPlanName(model.getPlanName());
        entity.setSummary(model.getSummary());
        entity.setStatus(model.getStatus().byteValue());
        entity.setRemark(model.getRemark());
        entity.setDentistId(model.getDentistId());
        entity.setUptId(userId);
        entity.setUptTime(now);
        mapper.insertSelective(entity);
    }

    /**
     * 根据病历id查询治疗计划详情
     *
     * @param medicalId
     * @return
     */
    public TreatPlanRecordVO findTreatPlanOneByMedicalId(Integer medicalId) {
        MedicalCommonRecord medical = medicalCommonRecordBiz.findMedicalIllegaHistoryById(medicalId);
        if (ObjectUtils.isEmpty(medical)) {
            throw new ClientServiceException("该病历不存在",OperationCodeConstants.DATA_NOT_EXIST);
        }
        // 兼容旧版本时的“计划”数据
        TreatPlanRecordVO plan = findCompatibleOldPlan(medical);
        if (ObjectUtils.isEmpty(plan)) {
            // 当前治疗计划
            TreatPlanRecord query = new TreatPlanRecord();
            query.setMedicalRecordId(medicalId);
            TreatPlanRecord treatPlanRecord = mapper.selectOne(query);
            if (!ObjectUtils.isEmpty(treatPlanRecord)) {
                plan = putTreatPlanStepList(treatPlanRecord);
            }
        }
        return plan;
    }

    /**
     * 兼容旧版本普通电子病历中的“计划”数据
     *
     * @param medical
     * @return
     */
    private TreatPlanRecordVO findCompatibleOldPlan(MedicalCommonRecord medical) {
        List<ExaminationsVO> plans = json2List(medical.getPlan());
        if (StringHelper.isNotEmpty(plans)) {
            StringBuilder builder = new StringBuilder();
            Iterator<ExaminationsVO> it = plans.iterator();
            while (it.hasNext()) {
                ExaminationsVO plan = it.next();
                String describe = plan.getDescribe();
                String toothPosition = plan.getTooth_position();
                if (StringHelper.isEmpty(describe) && StringHelper.isEmpty(toothPosition)) {
                    it.remove();
                    continue;
                }
                if (StringHelper.isNotEmpty(describe)) {
                    if (builder.length()>0) {
                        builder.append("，");
                    }
                    builder.append(describe);
                }
            }
            if (StringHelper.isNotEmpty(plans)) {
                TreatPlanRecordVO treatPlanVO = new TreatPlanRecordVO();
                treatPlanVO.setMedicalRecordId(medical.getId());
                treatPlanVO.setDentistId(medical.getMajorDentistId());
                treatPlanVO.setPlanName("");
                treatPlanVO.setTreatPlanSteps(oldPlanSteps(plans));
                treatPlanVO.setSummary(builder.toString());
                return treatPlanVO;
            }
        }
        return null;
    }

    /**
     * 给旧版本生成步骤
     *
     * @param plans
     * @return
     */
    private List<TreatPlanStepVO> oldPlanSteps(List<ExaminationsVO> plans) {
        TreatPlanStepVO step = new TreatPlanStepVO();
        step.setStepName("");
        List<TreatPlanDetailVO> details = new ArrayList<>();
        for (ExaminationsVO plan : plans) {
            String describe = StringHelper.isNotEmpty(plan.getDescribe())?plan.getDescribe():"";
            String toothPosition = StringHelper.isNotEmpty(plan.getTooth_position())?plan.getTooth_position():"";
            TreatPlanDetailVO detail = new TreatPlanDetailVO();
            detail.setRemark(describe);
            detail.setToothBit(toothPosition);
            details.add(detail);
        }
        step.setTreatPlanDetails(details);
        return Arrays.asList(step);
    }

    public MedicalTreatPlanRecordVO findOneById(Integer planId) {
        TreatPlanRecord entity = mapper.selectByPrimaryKey(planId);
        return putTreatPlanStepList(entity);
    }

    /**
     * 查询并填充治疗步骤和明细
     *
     * @param entity
     */
    private MedicalTreatPlanRecordVO putTreatPlanStepList(TreatPlanRecord entity) {
        return putTreatPlanStepList(entity, true);
    }

    private MedicalTreatPlanRecordVO putTreatPlanStepList(TreatPlanRecord entity, boolean onlySelectStatus) {
        if (ObjectUtils.isEmpty(entity)) {
            throw new ClientServiceException("该治疗计划不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        MedicalTreatPlanRecordVO result = entity2VO(entity);
        List<TreatPlanStepVO> steps = treatPlanStepBiz.findTreatPlanStepByPlanId(entity.getId(), onlySelectStatus);
        if (StringHelper.isNotEmpty(steps)) {
            accumulation(steps, result);
        }
        return result;
    }

    private void accumulation(List<TreatPlanStepVO> steps, MedicalTreatPlanRecordVO result) {
        int totalQuantity = 0;
        int completedNum = 0;
        int confirmNum = 0;
        int terminationNum = 0;
        BigDecimal totalAmount = new BigDecimal("0.00");
        Integer status = TreatPlanStatusEnum.EXECUTING.getCode();
        for (TreatPlanStepVO step : steps) {
            totalQuantity += step.getQuanity();
            totalAmount = totalAmount.add(step.getAmount());
            Integer stepStatus = step.getStatus();
            if (stepStatus.equals(TreatPlanStatusEnum.COMPLETED.getCode())) {
                completedNum++;
            } else if (stepStatus.equals(TreatPlanStatusEnum.CONFIRMED.getCode())) {
                confirmNum++;
            } else if (stepStatus.equals(TreatPlanStatusEnum.TERMINATION.getCode())) {
                terminationNum++;
            }
        }
        int size = steps.size();
        if (completedNum==size
                || confirmNum==size
                || terminationNum==size) {
            status = steps.get(0).getStatus();
        }
        Integer planStatus = result.getStatus();
        if (planStatus>TreatPlanStatusEnum.UNCONFIRM.getCode()) {
            result.setStatus(status);
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
        result.setPatientId(entity.getPatientId());
        result.setOrgId(entity.getOrgId());
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
        result.setStatus(entity.getStatus().intValue());
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
                vo.setStatus(entity.getStatus().intValue());
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
            result.setMedicalRecordId(medicalRecordId);
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
        Integer status = treatPlan.getStatus();
        Byte changeType = form.getChangeType();
        if (changeType == 0) {// 方案确认
            checkNotStatus(status, TreatPlanStatusEnum.UNCONFIRM);
            updateTreatPlanStatus(treatPlan, TreatPlanStatusEnum.CONFIRMED.getCode());
            treatPlan.setStatus(TreatPlanStatusEnum.CONFIRMED.getCode());
            return treatPlan;
        } else if (changeType == 1) {// 方案变更
            checkNotStatus(status, TreatPlanStatusEnum.UNCONFIRM, TreatPlanStatusEnum.CONFIRMED, TreatPlanStatusEnum.EXECUTING);
            // 已完成的项目不变
            TreatPlanRecordModel model = vo2modelBaseInfo(treatPlan);
            model.setTreatPlanSteps(form.getDetails());
            save(model, (byte) 1, true);
        } else if (changeType == 2) {// 提前终止
            checkNotStatus(status, TreatPlanStatusEnum.EXECUTING, TreatPlanStatusEnum.COMPLETED);
            // 终止计划，终止步骤，终止项目
            updateTreatPlanStatus(treatPlan, TreatPlanStatusEnum.TERMINATION.getCode());
        } else if (changeType == 3) {// 撤销终止
            checkNotStatus(status, TreatPlanStatusEnum.TERMINATION);
            // 恢复计划，步骤，项目在终止前的状态
            TreatPlanRecord entity = mapper.selectByPrimaryKey(planId);
            // 重新计算治疗计划的状态
            treatPlan = putTreatPlanStepList(entity, false);
            updateTreatPlanStatus(treatPlan, null);
        }
        return null;
    }

    /**
     * 更新治疗计划状态
     *
     * @param treatPlan
     * @param status
     */
    private void updateTreatPlanStatus(MedicalTreatPlanRecordVO treatPlan, Integer status) {
        TreatPlanRecordModel model = vo2modelBaseInfo(treatPlan);
        if (ObjectUtils.isEmpty(status)) {
            status = treatPlan.getStatus();
        }
        model.setStatus(status);
//        detailVO2Model(model, treatPlan.getTreatPlanSteps());
        save(model, null, false);
    }

    private TreatPlanRecordModel vo2modelBaseInfo(MedicalTreatPlanRecordVO treatPlan) {
        TreatPlanRecordModel model = new TreatPlanRecordModel();
        model.setPlanId(treatPlan.getPlanId());
        model.setPlanName(treatPlan.getPlanName());
        model.setMedicalRecordId(treatPlan.getMedicalRecordId());
        model.setOrgId(treatPlan.getOrgId());
        model.setDentistId(treatPlan.getDentistId());
        model.setPatientId(treatPlan.getPatientId());
        model.setSummary(treatPlan.getSummary());
        model.setRemark(treatPlan.getRemark());
        model.setCrtId(treatPlan.getCrtId());
        return model;
    }

    private void detailVO2Model(TreatPlanRecordModel model, List<TreatPlanStepVO> treatPlanSteps) {
        if (StringHelper.isNotEmpty(treatPlanSteps)) {
            List<TreatPlanStepModel> steps = new ArrayList<>();
            treatPlanSteps.forEach(step->{
                TreatPlanStepModel stepModel = new TreatPlanStepModel();
                stepModel.setTreatPlanStepId(step.getTreatPlanStepId());
                stepModel.setStepName(step.getStepName());
                List<TreatPlanDetailModel> detailModels = new ArrayList<>();
                List<TreatPlanDetailVO> details = step.getTreatPlanDetails();
                details.forEach(detail->{
                    TreatPlanDetailModel detailModel = new TreatPlanDetailModel();
                    detailModel.setDetailId(detail.getDetailId());
                    detailModel.setType(detail.getType());
                    detailModel.setBillingItemId(detail.getBillingItemId());
                    detailModel.setBillingItemName(detail.getBillingItemName());
                    detailModel.setToothBit(detail.getToothBit());
                    detailModel.setPrice(detail.getPrice());
                    detailModel.setQuantity(detail.getQuantity());
                    detailModel.setUnit(detail.getUnit());
                    detailModel.setRemark(detail.getRemark());
                    detailModel.setStatus(detail.getStatus());
                    detailModels.add(detailModel);
                });
                stepModel.setTreatPlanDetails(detailModels);
                steps.add(stepModel);
            });
            model.setTreatPlanSteps(steps);
        }
    }

    /**
     * 校验状态，如果status不属于statusEmums，则抛异常
     *
     * @param status
     * @param statusEnums
     */
    protected static void checkNotStatus(Integer status, TreatPlanStatusEnum... statusEnums) {
        int okSize = 0;
        for (TreatPlanStatusEnum statusEnum : statusEnums) {
            if (statusEnum.getCode().equals(status)) {
                okSize++;
                break;
            }
        }
        if (okSize == 0) {
            throw new ClientServiceException("该治疗计划项目不允许操作", OperationCodeConstants.OPERATION_NOT_ALLOW);
        }
    }

    /**
     * 查询患者所有已确认、进行中的治疗计划列表
     *
     *
     * @param orgId
     * @param query
     * @return
     */
    public PageInfo<TreatPlanRecordVO> findPatientTreatPlanList(Integer orgId, TreatPlanRecordQuery query) {
        List<Integer> status = Arrays.asList(TreatPlanStatusEnum.CONFIRMED.getCode(), TreatPlanStatusEnum.EXECUTING.getCode());
        query.setStatus(status);
        List<TreatPlanRecord> list = mapper.selectTreatPlanRecordInfoList(query);
        List<TreatPlanRecordVO> result = new ArrayList<>();
        if (StringHelper.isNotEmpty(list)) {
            list.forEach(entity-> result.add(putTreatPlanStepList(entity)));
            putPlanItemMemberPrice(orgId, result);
        }
        return new PageInfo<>(result);
    }

    /**
     * 装配会员折扣价
     *
     * @param orgId
     * @param result
     */
    private void putPlanItemMemberPrice(Integer orgId, List<TreatPlanRecordVO> result) {
        List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
        if (StringHelper.isNotEmpty(memberTypes)) {
            List<Integer> oralIds = new ArrayList<>();
            List<Integer> tariffIds = new ArrayList<>();
            result.forEach(vo -> {
                List<TreatPlanStepVO> steps = vo.getTreatPlanSteps();
                steps.forEach(step -> {
                    List<TreatPlanDetailVO> details = step.getTreatPlanDetails();
                    if (StringHelper.isNotEmpty(details)) {
                        details.forEach(detail -> {
                            Byte type = detail.getType();
                            Integer itemId = detail.getBillingItemId();
                            if (type.intValue() == 0) {
                                tariffIds.add(itemId);
                            } else {
                                oralIds.add(itemId);
                            }
                        });
                    }
                });
            });
            ClinicMemberPriceQuery query = new ClinicMemberPriceQuery();
            query.setOrgId(orgId);
            query.setOralIds(oralIds);
            query.setTariffIds(tariffIds);
            List<ClinicItemPriceVO> memberPriceList = remoteTreatmentServiceFeign.findClinicItemMemberPrice(query);
            Map<String, Map<Integer, BigDecimal>> priceMap = new HashMap<>();
            memberPriceList.forEach(vo -> {
                String key = vo.getItemId() + "," + vo.getClinicItemId();
                Map<Integer, BigDecimal> memberPrices = priceMap.get(key);
                if (memberPrices == null) {
                    memberPrices = new LinkedHashMap<>();
                }
                memberPrices.put(vo.getMemberTypeId(), vo.getClinicItemPrice());
                priceMap.put(key, memberPrices);
            });
            result.forEach(plan -> {
                List<TreatPlanStepVO> steps = plan.getTreatPlanSteps();
                steps.forEach(step -> {
                    List<TreatPlanDetailVO> details = step.getTreatPlanDetails();
                    if (StringHelper.isNotEmpty(details)) {
                        details.forEach(detail -> {
                            Integer quantity = detail.getQuantity();
                            Map<Integer, BigDecimal> map = new LinkedHashMap<>();
                            Map<Integer, BigDecimal> memberPrices = priceMap.get(detail.getType() + "," + detail.getBillingItemId());
                            if (StringHelper.isNotEmpty(memberPrices)) {
                                memberPrices.forEach((memberTypeId, discountPrice) -> {
                                    map.put(memberTypeId, new BigDecimal(quantity).multiply(discountPrice)
                                            .setScale(2, BigDecimal.ROUND_HALF_UP));
                                });
                            } else {
                                BigDecimal price = detail.getPrice();
                                memberTypes.forEach(memberType -> {
                                    BigDecimal memberPrice =
                                            (price
                                                    .multiply(BigDecimal.valueOf(memberType.getRate()))
                                                    .divide(BigDecimal.valueOf(100), 2))
                                                    .setScale(2, BigDecimal.ROUND_HALF_UP);
                                    map.put(memberType.getId(), memberPrice);
                                });
                            }
                            detail.setMemberPrices(map);
                        });
                    }
                });
            });
        }
    }

    public void recalculatePlanStatusById(Integer userId, List<Integer> planIds) {
        planIds.forEach(planId->{
            MedicalTreatPlanRecordVO treatPlan = findOneById(planId);
            treatPlan.setCrtId(userId);
            updateTreatPlanStatus(treatPlan, null);
        });
    }

    /**
     * 生成治疗计划核销表数据
     *
     * @param model
     */
    public void treatPlanDetailWriteoffQunatity(TreatPlanDetailWriteoffModel model) {
        removeInvalidData(model.getDeletedOrderDetailIds());
        List<TreatPlanDetailWriteoffInfoModel> models = model.getWriteoffInfoModels();
        List<Integer> detailIds = new ArrayList<>();
        models.forEach(vo-> detailIds.addAll(vo.getPlanDetailIds()));
        if (StringHelper.isNotEmpty(detailIds)) {
            List<TreatPlanDetail> list = treatPlanDetailBiz.sumTreatPlanDetailEnableQuantity(detailIds);
            Date now = new Date(System.currentTimeMillis());
            List<TreatPlanDetailWriteoff> datas = new ArrayList<>();
            models.forEach(vo -> {
                List<Integer> planDetailIds = vo.getPlanDetailIds();
                Map<Integer, Integer> writeoffQuantityMap = checkQuantityOver(planDetailIds, vo.getQuantity(), list);
                writeoffQuantityMap.forEach((detailId, writeoffQuantity)->{
                    TreatPlanDetailWriteoff data = new TreatPlanDetailWriteoff();
                    data.setOrderDetailId(vo.getOrderDetailId());
                    data.setPlanDetailId(detailId);
                    data.setWriteOffQuantity(writeoffQuantity);
                    data.setCrtId(vo.getCrtId());
                    data.setCrtTime(now);
                    datas.add(data);
                });
            });
            treatPlanDetailBiz.insertBatchOfWriteoffQuantity(datas);
        }
    }

    /**
     * 清理无效记录
     *
     * @param deletedIds
     */
    private void removeInvalidData(List<Integer> deletedIds) {
        if (StringHelper.isNotEmpty(deletedIds)) {
            treatPlanDetailBiz.deleteWriteoffByOrderDetailId(deletedIds);
        }
    }

    /**
     * 检查订单项目是否超过选定计划的项目数量
     *
     * @param planDetailIds
     * @param quantity
     * @param list
     */
    private Map<Integer, Integer> checkQuantityOver(List<Integer> planDetailIds, Integer quantity, List<TreatPlanDetail> list) {
        Map<Integer, Integer> result = new LinkedHashMap<>(16);
        if (StringHelper.isNotEmpty(list)) {
            for (TreatPlanDetail vo : list) {
                int enableQuantity = vo.getQuantity();
                if (enableQuantity <= 0) {
                    throw new ClientServiceException(vo.getBillingItemName()
                            + "在治疗计划中已使用，请重新选择治疗计划", OperationCodeConstants.OPERATION_NOT_ALLOW);
                }
                for (Integer detailId : planDetailIds) {
                    if (detailId.equals(vo.getId())) {
                        // 可用项目数量-已选项目数量
                        if (enableQuantity - quantity <= 0) {// 可用项目数量已全部用完
                            result.put(detailId, enableQuantity);
                            break;
                        } else {// 可用项目数量未用完
                            result.put(detailId, quantity);
                        }
                        enableQuantity -= quantity;
                    }
                }
            }
        } else {
            throw new ClientServiceException("治疗计划中项目不存在", OperationCodeConstants.DATA_NOT_EXIST);
        }
        return result;
    }
}
