package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.model.AdultPatientRegistrationModel;
import com.yunya.feign.patient_central.domain.model.ChildrenPatientRegistrationModel;
import com.yunya.feign.patient_central.domain.model.CustomerRegistrationModel;
import com.yunya.feign.patient_central.domain.model.PatientRegistrationModel;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.*;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 客户登记业务层
 *
 * @author: WY
 * @date: 2020/11/5 17:49
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CustomerRegistrationBiz extends BaseBiz<PatientBaseInfoMapper, PatientBaseInfo> {

    /** 注入患者Biz */
    @Autowired private PatientBaseInfoBiz  patientBaseInfoBiz;

    /** 注入患者来源Mapper */
    @Autowired private PatientOriginMapper patientOriginMapper;

    @Autowired private PatientMemberInfoBiz patientMemberInfoBiz;

    @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

    @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

    @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

    @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

    @Resource private PatientOriginLogMapper patientOriginLogMapper;

    @Autowired private PatientChildInfoMapper patientChildInfoMapper;

    @Autowired private PatientToothInfoMapper patientToothInfoMapper;

    @Autowired private PatientExpInfoMapper patientExpInfoMapper;

    @Autowired private PatientExtInfoMapper patientExtInfoMapper;

    /**
     * 添加客户登记
     * @param customerRegistrationModel  客户登记
     * @return PatientBaseInfoVo
     */
    public PatientBaseInfoVo addPatient(CustomerRegistrationModel customerRegistrationModel) {
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        BeanUtils.copyProperties(customerRegistrationModel, patientBaseInfo);
        checkOriginSource(patientBaseInfo.getOriginId(), patientBaseInfo.getOriginType());
        // 设置患者登记默认的门诊为总院
        patientBaseInfo.setOrgId(39);
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setCrtId(1);
        patientBaseInfo.setCrtName("客户登记");
        mapper.insertPatientInfo(patientBaseInfo);
        PatientBaseInfoVo patientBaseInfoVo = mapper.selectPatienInfoById(patientBaseInfo.getId());
        if (patientBaseInfoVo.getOriginId() != null) {
            addPatientOriginLog(patientBaseInfo);
        }
        // 创建预付款 并发送消息
        patientBaseInfoBiz.sendMessages(patientBaseInfo.getId(), 0);
        addPatientPrepaymentsInfo(patientBaseInfo);
        return patientBaseInfoVo;
    }

    private void addPatientOriginLog(PatientBaseInfo patientBaseInfo) {
        PatientOriginLog patientOriginLog = new PatientOriginLog();
        patientOriginLog.setPatientId(patientBaseInfo.getId());
        patientOriginLog.setOriginType(patientBaseInfo.getOriginType());
        patientOriginLog.setOriginId(patientBaseInfo.getOriginId());
        patientOriginLog.setInservice(patientBaseInfo.getInservice());
        patientOriginLog.setCrtId(patientBaseInfo.getCrtId());
        patientOriginLog.setCrtName(patientBaseInfo.getCrtName());
        patientOriginLog.setCrtTime(patientBaseInfo.getCrtTime());
        patientOriginLog.setUptId(patientBaseInfo.getUptId());
        patientOriginLog.setUpdName(patientBaseInfo.getUpdName());
        patientOriginLog.setUpdTime(patientBaseInfo.getUpdTime());
        patientOriginLogMapper.insertSelective(patientOriginLog);
        remoteRabbitMqServiceFeign.sendMessage(patientOriginLog.getId(), 0, MsgCategoryEnum.BasePatientOriginLog);
    }

    /**
     * 检查患者来源
     *
     * @param originId
     * @param originType
     */
    private void checkOriginSource(Integer originId, Integer originType) {
        if (originId != null) {
            if (originType > 2){
                PatientOrigin patientOrigin =
                        this.patientOriginMapper.selectByPrimaryKey(originId);
                if (patientOrigin == null) {
                    throw new ClientServiceException("该患者来源不存在", DATA_NOT_EXIST);
                }
                if (patientOrigin.getTimeLimit() ==1) {
                    Date curDate = DateUtil.getCurrentDate();
                    Date startDate = DateUtil.toDate(patientOrigin.getLimitStartDate());
                    Date endDate = DateUtil.toDate(patientOrigin.getLimitEndDate());
                    if (curDate.before(startDate) || curDate.after(endDate)) {
                        throw new ClientServiceException("该患者来源已过期", PARAMETERS_IS_ILLEGAL);
                    }
                }
            }
        }
    }

    /**
     * 添加患者时,创建预付款账户
     *
     * @param patientBaseInfo 患者信息
     */
    public void addPatientPrepaymentsInfo(PatientBaseInfo patientBaseInfo) {
        if (patientBaseInfo.getId() != null) {
            PatientPrepaymentsInfo patientPrepaymentsInfo = new PatientPrepaymentsInfo();
            patientPrepaymentsInfo.setOrgId(patientBaseInfo.getOrgId());
            patientPrepaymentsInfo.setPatientId(patientBaseInfo.getId());
            // 预付款卡号生成规则 开通Y
            patientPrepaymentsInfo.setPrepaymentNumber(
                    this.generateCardNumber("Y",patientBaseInfo.getOrgId()));
            patientPrepaymentsInfo.setCrtId(1);
            patientPrepaymentsInfo.setCrtName("管理员");
            this.patientPrepaymentsInfoMapper.insertSelective(patientPrepaymentsInfo);
            remoteRabbitMqServiceFeign.sendMessage(
                    patientPrepaymentsInfo.getId(), 1, 0, MsgCategoryEnum.BasePatientMember);
        }
    }


    /**
     * 生产预付款卡号
     *
     * @param mark 会员号标识 H：会员卡，Y：预付款
     * @return String 卡号
     */
    public String generateCardNumber(String mark, Integer orgId) {
        String number = this.patientMemberInfoMapper.generateCardNumber4Prepay(39);
        String suffix = String.format("%06d", Integer.parseInt(number) + 1);
        // 获取门诊简称
        OrganizationInfo organizationInfo =
                this.remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
        if (organizationInfo != null) {
            return mark + organizationInfo.getClinicNumber() + suffix;
        }
        return null;
    }

    /**
     * 添加成人患者登记
     * @param model  客户登记
     * @return PatientBaseInfoVo
     */
    public void addPatient(AdultPatientRegistrationModel model) {
        // 成人患者自主登记
        final int userId = -666;
        final String userName = "患者自主登记";
        PatientBaseInfo patientBaseInfo = addPatientBaseInfo(model, userId, userName, model.getMobile(), model.getMobileOwner());
        int patientId = patientBaseInfo.getId();
        addPatientExpInfoByAdult(model, userId, userName, patientId);
        addPatientExtInfo(model, userId, userName, patientId);
        addPatientToothInfo(model, userId, patientId);
        PatientBaseInfoVo patientBaseInfoVo = mapper.selectPatienInfoById(patientId);
        if (patientBaseInfoVo.getOriginId() != null) {
            addPatientOriginLog(patientBaseInfo);
        }
        // 创建预付款 并发送消息
        patientBaseInfoBiz.sendMessages(patientId, 0);
        addPatientPrepaymentsInfo(patientBaseInfo);
    }

    /**
     * 添加患者牙齿信息
     *
     * @param model
     * @param userId
     * @param patientId
     */
    private void addPatientToothInfo(AdultPatientRegistrationModel model, int userId, int patientId) {
        PatientToothInfo toothInfo = new PatientToothInfo();
        toothInfo.setPatientId(patientId);
        toothInfo.setHadMissTooth(model.getHadMissTooth());
        List<Integer> missToothPos = model.getMissToothHistory();
        if (StringHelper.isNotEmpty(missToothPos)) {
            toothInfo.setMissToothHistory(StringHelper.join(missToothPos, ","));
        }
        toothInfo.setHadFillTreat(model.getHadFillTreat());
        List<Integer> fillMetiralIds = model.getFillTreatHistory();
        if (StringHelper.isNotEmpty(fillMetiralIds)) {
            toothInfo.setFillTreatHistory(StringHelper.join(fillMetiralIds, ","));
        }
        toothInfo.setFillTreatLastDate(model.getFillTreatLastDate());
        toothInfo.setHadPeriodontalSurgery(model.getHadPeriodontalSurgery());
        toothInfo.setHadOcclusalAdjust(model.getHadOcclusalAdjust());
        toothInfo.setHadRestorativeDentures(model.getHadRestorativeDentures());
        toothInfo.setRpdPart(model.getRpdPart());
        toothInfo.setRpdDate(model.getRpdDate());
        toothInfo.setLpdPart(model.getLpdPart());
        toothInfo.setLpdDate(model.getLpdDate());
        toothInfo.setHadPreventiveTreat(model.getHadPreventiveTreat());
        toothInfo.setPreventiveTreatCycle(model.getPreventiveTreatCycle());
        toothInfo.setPreventiveTreatLastMonth(model.getPreventiveTreatLastMonth());
        toothInfo.setHadDiffcultTreat(model.getHadDiffcultTreat());
        toothInfo.setMissTeethUnrepeatCause(model.getMissTeethUnrepeatCause());
        toothInfo.setHadOrthodontic(model.getHadOrthodontic());
        toothInfo.setOrthodonticStartDate(model.getOrthodonticStartDate());
        toothInfo.setOrthodonticEndDate(model.getOrthodonticEndDate());
        toothInfo.setHadHygieneEducation(model.getHadHygieneEducation());
        toothInfo.setUsedPlaqueDna(model.getUsedPlaqueDna());
        Date now = new Date(System.currentTimeMillis());
        toothInfo.setCrtId(userId);
        toothInfo.setCrtTime(now);
        toothInfo.setUptId(userId);
        toothInfo.setUptTime(now);
        patientToothInfoMapper.insertSelective(toothInfo);
    }

    /**
     * 添加成人患者额外信息
     *
     * @param model
     * @param userId
     * @param userName
     * @param patientId
     */
    private void addPatientExpInfoByAdult(AdultPatientRegistrationModel model, int userId, String userName, int patientId) {
        PatientExpInfo expInfo = new PatientExpInfo();
        expInfo.setPatientId(patientId);
        expInfo.setAddress(model.getDetailedAddress());
        expInfo.seteMail(model.getEMail());
        expInfo.setProvince(model.getProvince());
        expInfo.setCity(model.getCity());
        expInfo.setCountry(model.getCountry());
        expInfo.setProfession(model.getProfession());
        expInfo.setEmergencyPhone(model.getEmergencyPhone());
        expInfo.setState(model.getState());
        expInfo.setEmployer(model.getEmployer());
        expInfo.setHeredity(model.getHeredity());
        Integer pregnancyMonth = model.getPregnancyMonth();
        if (!ObjectUtils.isEmpty(pregnancyMonth)) {
            expInfo.setPregnancyWeek(DateUtil.pregancyMonth2Week(pregnancyMonth));
        }
        expInfo.setFeedBaby(model.getFeedBaby());
        expInfo.setOtherHealth(model.getOtherHealth());
        expInfo.setBrushTimes(model.getBrushingTimes());
        expInfo.setBrushTime(model.getBrushingTime());
        expInfo.setBrushHardness(model.getBrushHardness());
        expInfo.setUseCollutory(model.getUseCollutory());
        expInfo.setBruxism(model.getBruxism());
        expInfo.setUseFloss(model.getUseFloss());
        expInfo.setSmokingAge(model.getSmokingAge());
        expInfo.setSmokingNum(model.getSmokingNum());
        Date now = new Date(System.currentTimeMillis());
        expInfo.setCrtId(userId);
        expInfo.setCrtName(userName);
        expInfo.setCrtTime(now);
        expInfo.setUptId(userId);
        expInfo.setUpdName(userName);
        expInfo.setUpdTime(now);
        patientExpInfoMapper.insertSelective(expInfo);
    }

    /**
     * 添加患者基础信息
     *
     * @param model
     * @param userId
     * @param userName
     * @param mobile
     * @param mobileOwner
     * @return
     */
    private PatientBaseInfo addPatientBaseInfo(PatientRegistrationModel model, int userId, String userName, String mobile, Integer mobileOwner) {
        Date now = new Date(System.currentTimeMillis());
        checkOriginSource(model.getOriginId(), model.getOriginType());
        PatientBaseInfo baseInfo = new PatientBaseInfo();
        baseInfo.setAge(model.getAge());
        baseInfo.setBirthday(model.getBirthdate());
        baseInfo.setGender(model.getGender());
        baseInfo.setMobile(mobile);
        baseInfo.setMobileOwner(mobileOwner);
        String name = model.getName();
        baseInfo.setName(name);
        baseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(name));
        baseInfo.setOrgId(model.getOrgId());
        baseInfo.setOriginId(model.getOriginId());
        baseInfo.setOriginType(model.getOriginType());
        baseInfo.setCrtId(userId);
        baseInfo.setCrtName(userName);
        baseInfo.setCrtTime(now);
        baseInfo.setUptId(userId);
        baseInfo.setUpdName(userName);
        baseInfo.setUpdTime(now);
        mapper.insertSelective(baseInfo);
        return baseInfo;
    }

    /**
     * 添加儿童患者登记
     * @param model  客户登记
     * @return PatientBaseInfoVo
     */
    public void addPatient(ChildrenPatientRegistrationModel model) {
        // 儿童患者登记
        final int userId = -777;
        final String userName = "患者自主登记";
        PatientBaseInfo patientBaseInfo = addPatientBaseInfo(model, userId, userName, null, null);
        int patientId = patientBaseInfo.getId();
        addPatientExpInfoByChild(model, userId, userName, patientId);
        addPatientExtInfo(model, userId, userName, patientId);
        addPatientChildInfo(model, userId, patientId);

        PatientBaseInfoVo patientBaseInfoVo = mapper.selectPatienInfoById(patientId);
        if (patientBaseInfoVo.getOriginId() != null) {
            addPatientOriginLog(patientBaseInfo);
        }
        // 创建预付款 并发送消息
        patientBaseInfoBiz.sendMessages(patientId, 0);
        addPatientPrepaymentsInfo(patientBaseInfo);
    }

    /**
     * 添加过敏源、疾病史
     *
     * @param model
     * @param userId
     * @param userName
     * @param patientId
     */
    private void addPatientExtInfo(PatientRegistrationModel model, int userId, String userName, int patientId) {
        Date now = new Date(System.currentTimeMillis());
        // 疾病史
        List<String> medicalHistoryIds = model.getMedicalHistorys();
        if (StringHelper.isNotEmpty(medicalHistoryIds)) {
            PatientExtInfo extInfo = new PatientExtInfo();
            extInfo.setPatientId(patientId);
            extInfo.setOrgId(model.getOrgId());
            extInfo.setDescription(StringHelper.join(medicalHistoryIds, ","));
            extInfo.setType((byte) 1);
            extInfo.setCrtId(userId);
            extInfo.setCrtName(userName);
            extInfo.setCrtTime(now);
            extInfo.setUptId(userId);
            extInfo.setUpdName(userName);
            extInfo.setUpdTime(now);
            patientExtInfoMapper.insertSelective(extInfo);
        }
        // 过敏源
        List<String> allergnIds = model.getAllergns();
        if (StringHelper.isNotEmpty(allergnIds)) {
            PatientExtInfo extInfo = new PatientExtInfo();
            extInfo.setPatientId(patientId);
            extInfo.setOrgId(model.getOrgId());
            extInfo.setDescription(StringHelper.join(allergnIds, ","));
            extInfo.setType((byte) 2);
            extInfo.setCrtId(userId);
            extInfo.setCrtName(userName);
            extInfo.setCrtTime(now);
            extInfo.setUptId(userId);
            extInfo.setUpdName(userName);
            extInfo.setUpdTime(now);
            patientExtInfoMapper.insertSelective(extInfo);
        }
    }

    private void addPatientExpInfoByChild(ChildrenPatientRegistrationModel model, int userId, String userName, int patientId) {
        PatientExpInfo expInfo = new PatientExpInfo();
        expInfo.setPatientId(patientId);
        expInfo.setAddress(model.getDetailedAddress());
        expInfo.seteMail(model.getEMail());
        expInfo.setProvince(model.getProvince());
        expInfo.setCity(model.getCity());
        expInfo.setCountry(model.getCountry());
        expInfo.setProfession(model.getProfession());
        expInfo.setGuardian(model.getGuardian());
        expInfo.setUsefulPhone(model.getGuardianPhone());
        expInfo.setEmergencyPhone(model.getEmergencyPhone());
        expInfo.setState(model.getState());
        Date now = new Date(System.currentTimeMillis());
        expInfo.setCrtId(userId);
        expInfo.setCrtName(userName);
        expInfo.setCrtTime(now);
        expInfo.setUptId(userId);
        expInfo.setUpdName(userName);
        expInfo.setUpdTime(now);
        patientExpInfoMapper.insertSelective(expInfo);
    }

    /**
     * 添加患者儿童属性信息
     *
     * @param model
     * @param userId
     * @param patientId
     */
    private void addPatientChildInfo(ChildrenPatientRegistrationModel model, int userId, int patientId) {
        PatientChildInfo childInfo = new PatientChildInfo();
        childInfo.setPatientId(patientId);
        childInfo.setSchool(model.getSchool());
        childInfo.setGrade(model.getGrade());
        childInfo.setMedicationHistory(model.getMedicationHistory());
        childInfo.setMotherPregnancy(model.getMotherPregnancy());
        List<Integer> parentHasCaries = model.getParentHasCaries();
        childInfo.setParentHasCaries(StringHelper.join(parentHasCaries, ","));
        childInfo.setToothClearliness(model.getToothClearliness());
        childInfo.setToothLastCheck(model.getToothLastCheck());
        childInfo.setToothSprouting(model.getToothSprouting());
        childInfo.setUsedDentalFloss(model.getUsedDentalFloss());
        childInfo.setUsedFluorideToothpaste(model.getUsedFluorideToothpaste());
        childInfo.setUseFlossTimes(model.getUseFlossTimes());
        childInfo.setBrushingTimes(model.getBrushingTimes());
        childInfo.setDiet(model.getDiet());
        List<Integer> habitIds = model.getHabitIds();
        if (StringHelper.isNotEmpty(habitIds)) {
            childInfo.setHabitIds(StringHelper.join(habitIds,","));
        }
        Date now = new Date(System.currentTimeMillis());
        childInfo.setCrtId(userId);
        childInfo.setCrtTime(now);
        childInfo.setUptId(userId);
        childInfo.setUptTime(now);
        patientChildInfoMapper.insertSelective(childInfo);
    }
}