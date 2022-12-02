package com.yunya.modules.patient_central.biz;

import com.yunya.feign.oss.RemoteOssServiceFeign;
import com.yunya.feign.oss.domain.model.Base64UploadForm;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.feign.patient_central.domain.model.AdultPatientRegistrationModel;
import com.yunya.feign.patient_central.domain.model.ChildrenPatientRegistrationModel;
import com.yunya.feign.patient_central.domain.model.CustomerRegistrationModel;
import com.yunya.feign.patient_central.domain.model.PatientRegistrationModel;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.treatment_other.domain.model.MedicalRayFilmModel;
import com.yunya.feign.treatment_other.domain.query.XUploadFileQuery;
import com.yunya.feign.treatment_other.domain.vo.XUploadFileVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.UNKNOWN_ORIGIN_TYPE;
import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static com.yunya.framework.common.enums.FileSourceTypeEnum.PATIENT_SIGNATURE;

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

    @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

    @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

    @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

    @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

    @Resource private PatientOriginLogMapper patientOriginLogMapper;

    @Autowired private PatientChildInfoMapper patientChildInfoMapper;

    @Autowired private PatientToothInfoMapper patientToothInfoMapper;

    @Autowired private PatientExpInfoMapper patientExpInfoMapper;

    @Autowired private PatientExtInfoMapper patientExtInfoMapper;

    @Autowired private RemoteTreatmentOtherFeign  remoteTreatmentOtherFeign;

    @Autowired private RemoteOssServiceFeign remoteOssServiceFeign;

    /** 多线程 */
    @Resource(name = "customizeThreadPool")
    private ExecutorService executorService;

    @Value("${domainUrl}")
    private String domainUrl;

    /**
     * 添加客户登记
     * @param customerRegistrationModel  客户登记
     * @return PatientBaseInfoVo
     */
    public PatientBaseInfoVo addPatient(CustomerRegistrationModel customerRegistrationModel) {
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        BeanUtils.copyProperties(customerRegistrationModel, patientBaseInfo);
        Integer originId = patientBaseInfo.getOriginId();
        checkOriginSource(originId, patientBaseInfo.getOriginType(), originId, originId);
        defaultOriginType(patientBaseInfo);
        // 设置患者登记默认的门诊为总院
        patientBaseInfo.setOrgId(findRecentlyOrgId(39));
        patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
        patientBaseInfo.setInservice(true);
        patientBaseInfo.setCrtId(1);
        patientBaseInfo.setCrtName("客户登记");
        mapper.insertPatientInfo(patientBaseInfo);
        PatientBaseInfoVo patientBaseInfoVo = mapper.selectPatienInfoById(patientBaseInfo.getId());
        if (patientBaseInfoVo.getOriginId() != null) {
            addPatientOriginLog(patientBaseInfo, null);
        }
        // 创建预付款 并发送消息
        patientBaseInfoBiz.sendMessages(patientBaseInfo.getId(), 0);
        addPatientPrepaymentsInfo(patientBaseInfo, null);
        return patientBaseInfoVo;
    }

    /**
     * 设置默认患者来源为未知来源
     *
     * @param patientBaseInfo
     */
    private void defaultOriginType(PatientBaseInfo patientBaseInfo) {
        Integer originType = patientBaseInfo.getOriginType();
        Integer originId = patientBaseInfo.getOriginId();
        if (StringHelper.isNull(originType)) {
            patientBaseInfo.setOriginType(UNKNOWN_ORIGIN_TYPE);
        }
        if (UNKNOWN_ORIGIN_TYPE.equals(originType) && StringHelper.isNull(originId)) {
            PatientOrigin origin = patientOriginMapper.getTypeName(originType);
            if (StringHelper.isNotNull(origin)) {
                patientBaseInfo.setOriginId(origin.getId());
            }
        }
    }

    /**
     * 查询最近创建门诊或默认给定门诊id
     *
     * @param defaultOrgId
     * @return
     */
    private Integer findRecentlyOrgId(int defaultOrgId) {
        OrganizationInfo org = remoteSystemServiceFeign.findRecentlyOrDefaulOrg(defaultOrgId);
        return org.getId();
    }

    private void addPatientOriginLog(PatientBaseInfo patientBaseInfo, Integer patientId) {
        if (!ObjectUtils.isEmpty(patientId)) {
            Example example = new Example(PatientOriginLog.class);
            Example.Criteria c = example.createCriteria();
            c.andEqualTo("patientId",patientId);
            c.andEqualTo("inservice",true);
            PatientOriginLog entity = new PatientOriginLog();
            entity.setInservice(false);
            entity.setUptId(patientBaseInfo.getUptId());
            entity.setUpdName(patientBaseInfo.getUpdName());
            entity.setUpdTime(patientBaseInfo.getUpdTime());
            patientOriginLogMapper.updateByExampleSelective(entity, example);
        }
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
     * @param patientId
     * @param employeeId
     */
    private void checkOriginSource(Integer originId, Integer originType, Integer patientId, Integer employeeId) {
        if (originType == 1 && ObjectUtils.isEmpty(employeeId)) {
            throw new ClientServiceException("推荐员工不能为空", PARAMETERS_IS_ILLEGAL);
        }
        if (originType == 2 && ObjectUtils.isEmpty(patientId)) {
            throw new ClientServiceException("介绍人不能为空", PARAMETERS_IS_ILLEGAL);
        }
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
     * @param id
     */
    public void addPatientPrepaymentsInfo(PatientBaseInfo patientBaseInfo, Integer id) {
        if (ObjectUtils.isEmpty(id) && !ObjectUtils.isEmpty(patientBaseInfo.getId())) {
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
        String number = this.patientMemberInfoMapper.generateCardNumber4Prepay(orgId);
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
        Integer originType = model.getOriginType();
        Integer originId = model.getOriginId();
        Integer introducerId = model.getPatientId();
        Integer employeeId = model.getEmployeeId();
        checkOriginSource(originId, originType, introducerId, employeeId);
        if (originType == 1) {// 员工推荐
            originId = employeeId;
        } else if (originType == 2) {// 患者转介绍
            originId = introducerId;
        }
        PatientBaseInfo patientBaseInfo = savePatientBaseInfo(model, userId, userName, model.getMobileOwner(), originType, originId);
        int patientId = patientBaseInfo.getId();
        addPatientExpInfoByAdult(model, userId, userName, patientId);
        addPatientExtInfo(model, userId, userName, patientId);
        addPatientToothInfo(model, userId, patientId);
        PatientBaseInfoVo patientBaseInfoVo = mapper.selectPatienInfoById(patientId);
        if (!ObjectUtils.isEmpty(patientBaseInfoVo.getOriginId())) {
            addPatientOriginLog(patientBaseInfo, model.getId());
        }
        patientBaseInfoBiz.sendMessages(patientId, 0);
        // 创建预付款 并发送消息
        addPatientPrepaymentsInfo(patientBaseInfo, model.getId());
        savePatientSignature(model, userId, patientId);
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
        Boolean hadMissTooth = false;
        List<Integer> missToothPos = model.getMissToothHistory();
        if (StringHelper.isNotEmpty(missToothPos)) {
            toothInfo.setMissToothHistory(StringHelper.join(missToothPos, ","));
            hadMissTooth = true;
        }
        toothInfo.setHadMissTooth(hadMissTooth);
        Boolean hadFillTreat = false;
        List<Integer> fillMetiralIds = model.getFillTreatHistory();
        if (StringHelper.isNotEmpty(fillMetiralIds)) {
            toothInfo.setFillTreatHistory(StringHelper.join(fillMetiralIds, ","));
            hadFillTreat = true;
        }
        toothInfo.setHadFillTreat(hadFillTreat);
        toothInfo.setFillTreatLastDate(DateUtil.parse2Date(model.getFillTreatLastDate()));
        toothInfo.setHadPeriodontalSurgery(model.getHadPeriodontalSurgery());
        toothInfo.setHadOcclusalAdjust(model.getHadOcclusalAdjust());
        Boolean hadRestorativeDentures = false;
        String rpdPart = model.getRpdPart();
        String lpdPart = model.getLpdPart();
        if (StringHelper.isNotEmpty(rpdPart) || StringHelper.isNotEmpty(lpdPart)) {
            hadRestorativeDentures = true;
        }
        toothInfo.setHadRestorativeDentures(hadRestorativeDentures);
        toothInfo.setRpdPart(rpdPart);
        toothInfo.setRpdDate(DateUtil.parse2Date(model.getRpdDate()));
        toothInfo.setLpdPart(lpdPart);
        toothInfo.setLpdDate(DateUtil.parse2Date(model.getLpdDate()));
        Boolean hadPreventiveTreat = false;
        Short preventiveTreatCycle = model.getPreventiveTreatCycle();
        if (!ObjectUtils.isEmpty(preventiveTreatCycle)) {
            toothInfo.setPreventiveTreatCycle(preventiveTreatCycle);
            hadPreventiveTreat = true;
        }
        toothInfo.setHadPreventiveTreat(hadPreventiveTreat);
        toothInfo.setPreventiveTreatLastMonth(model.getPreventiveTreatLastMonth());
        toothInfo.setHadDiffcultTreat(model.getHadDiffcultTreat());
        toothInfo.setMissTeethUnrepeatCause(model.getMissTeethUnrepeatCause());
        Boolean hadOrthodontic = false;
        String orthodonticStartDate = model.getOrthodonticStartDate();
        String orthodonticEndDate = model.getOrthodonticEndDate();
        if (StringHelper.isNotEmpty(orthodonticStartDate) || StringHelper.isNotEmpty(orthodonticEndDate)) {
            hadOrthodontic = true;
        }
        toothInfo.setHadOrthodontic(hadOrthodontic);
        toothInfo.setOrthodonticStartDate(DateUtil.parse2Date(orthodonticStartDate));
        toothInfo.setOrthodonticEndDate(DateUtil.parse2Date(orthodonticEndDate));
        toothInfo.setHadHygieneEducation(model.getHadHygieneEducation());
        toothInfo.setUsedPlaqueDna(model.getUsedPlaqueDna());
        Date now = new Date(System.currentTimeMillis());
        toothInfo.setCrtId(userId);
        toothInfo.setCrtTime(now);
        toothInfo.setUptId(userId);
        toothInfo.setUptTime(now);
        Integer id = model.getId();
        if (!ObjectUtils.isEmpty(id)) {
            Example example = new Example(PatientToothInfo.class);
            Example.Criteria c = example.createCriteria();
            c.andEqualTo("patientId", id);
            patientToothInfoMapper.deleteByExample(example);
        }
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
        Integer pid = model.getId();
        if (!ObjectUtils.isEmpty(pid)) {
            Example example = new Example(PatientExpInfo.class);
            Example.Criteria c = example.createCriteria();
            c.andEqualTo("patientId", pid);
            patientExpInfoMapper.deleteByExample(example);
        }
        patientExpInfoMapper.insertSelective(expInfo);
    }

    /**
     * 添加or修改患者基础信息
     *
     * @param model
     * @param userId
     * @param userName
     * @param mobileOwner
     * @return
     */
    private PatientBaseInfo savePatientBaseInfo(PatientRegistrationModel model, int userId, String userName,
                                                Integer mobileOwner, Integer originType, Integer originId) {
        Date now = new Date(System.currentTimeMillis());
        PatientBaseInfo baseInfo = new PatientBaseInfo();
        baseInfo.setAge(model.getAge());
        baseInfo.setBirthday(model.getBirthdate());
        baseInfo.setGender(model.getGender());
        baseInfo.setMobile(model.getMobile());
        baseInfo.setMobileOwner(mobileOwner);
        String name = model.getName();
        baseInfo.setName(name);
        baseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(name));
        baseInfo.setOriginType(originType);
        baseInfo.setOriginId(originId);
        baseInfo.setUptId(userId);
        baseInfo.setUpdName(userName);
        baseInfo.setUpdTime(now);
        defaultOriginType(baseInfo);
        Integer id = model.getId();
        if (!ObjectUtils.isEmpty(id)) {
            baseInfo.setId(id);
            mapper.updateByPrimaryKeySelective(baseInfo);
        } else {
            baseInfo.setCrtId(userId);
            baseInfo.setCrtName(userName);
            baseInfo.setCrtTime(now);
            baseInfo.setOrgId(model.getOrgId());
            mapper.insertSelective(baseInfo);
        }
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
        PatientBaseInfo patientBaseInfo = savePatientBaseInfo(model, userId, userName, null, UNKNOWN_ORIGIN_TYPE, null);
        int patientId = patientBaseInfo.getId();
        addPatientExpInfoByChild(model, userId, userName, patientId);
        addPatientExtInfo(model, userId, userName, patientId);
        addPatientChildInfo(model, userId, patientId);

        PatientBaseInfoVo patientBaseInfoVo = mapper.selectPatienInfoById(patientId);
        if (patientBaseInfoVo.getOriginId() != null) {
            addPatientOriginLog(patientBaseInfo, model.getId());
        }
        patientBaseInfoBiz.sendMessages(patientId, 0);
        // 创建预付款 并发送消息
        addPatientPrepaymentsInfo(patientBaseInfo, model.getId());
        savePatientSignature(model, userId, patientId);
    }

    /**
     * 保存患者的签名
     *
     * @param patientModel
     * @param userId
     * @param patientId
     */
    public void savePatientSignature(PatientRegistrationModel patientModel, Integer userId, Integer patientId) {
        String signatureImg = patientModel.getSignatureImgUrl();
        if (StringHelper.isNotEmpty(signatureImg)) {
            String fileName = patientModel.getName() + "的电子签名";
            Date now = new Date(System.currentTimeMillis());
            Base64UploadForm form = new Base64UploadForm();
            form.setFileName(fileName);
            form.setData(signatureImg);
            form.setCompanyId(0);
            form.setObjectId(patientId);
            form.setOssCategory(3);
            String fileUrl = (String) remoteOssServiceFeign.uploadBase64Image(form).getData();
            XUploadFileVO file = new XUploadFileVO();
            file.setFileLocation(fileUrl);
            file.setUploadTime(now);
            file.setFileName(fileName);
            MedicalRayFilmModel model = new MedicalRayFilmModel();
            model.setSourceType(PATIENT_SIGNATURE.getCode());
            model.setSourceId(patientId);
            model.setRayFiles(Arrays.asList(file));
            model.setCrtId(userId);
            model.setCrtTime(now);
            remoteTreatmentOtherFeign.saveXRayFile2XUploadFile(model);
        }
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
        Integer id = model.getId();
        if (!ObjectUtils.isEmpty(id)) {
            patientExtInfoMapper.deletePatientExtInfoByPatientId(id);
        }
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
        Integer id = model.getId();
        if (!ObjectUtils.isEmpty(id)) {
            Example example = new Example(PatientExpInfo.class);
            Example.Criteria c = example.createCriteria();
            c.andEqualTo("patientId", id);
            patientExpInfoMapper.deleteByExample(example);
        }
        PatientExpInfo expInfo = new PatientExpInfo();
        expInfo.setPatientId(patientId);
        expInfo.setAddress(model.getDetailedAddress());
        expInfo.seteMail(model.getEMail());
        expInfo.setProvince(model.getProvince());
        expInfo.setCity(model.getCity());
        expInfo.setCountry(model.getCountry());
        expInfo.setProfession(model.getProfession());
        expInfo.setGuardian(model.getGuardian());
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
        Integer id = model.getId();
        if (!ObjectUtils.isEmpty(id)) {
            Example example = new Example(PatientChildInfo.class);
            Example.Criteria c = example.createCriteria();
            c.andEqualTo("patientId", id);
            patientChildInfoMapper.deleteByExample(example);
        }
        PatientChildInfo childInfo = new PatientChildInfo();
        childInfo.setPatientId(patientId);
        childInfo.setSchool(model.getSchool());
        childInfo.setGrade(model.getGrade());
        childInfo.setMedicationHistory(model.getMedicationHistory());
        childInfo.setMotherPregnancy(model.getMotherPregnancy());
        childInfo.setFatherHasCaries(model.getFatherHasCaries());
        childInfo.setMotherHasCaries(model.getMotherHasCaries());
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

    /**
     * 根据患者id查询患者注册信息
     *
     * @param patientId
     * @return
     */
    public PatientRegistrationVO findPatientRegistrationById(Integer patientId) {
        PatientRegistrationVO patientVO = new PatientRegistrationVO();
        PatientBaseInfo baseInfo = mapper.selectByPrimaryKey(patientId);
        if (!ObjectUtils.isEmpty(baseInfo)) {
            putAdultRegistration(patientVO, baseInfo);
            putChildrenRegistration(patientVO, baseInfo);
        }
        return patientVO;
    }

    /**
     * 填充儿童注册信息
     * @param patientVO
     * @param baseInfo
     */
    private void putChildrenRegistration(PatientRegistrationVO patientVO, PatientBaseInfo baseInfo) {
        Integer patientId = baseInfo.getId();
        List<PatientExtInfo> medicalHistorys = patientExtInfoMapper.selectListByPatientId(patientId, 1);
        ChildrenPatientRegistrationVO vo = new ChildrenPatientRegistrationVO();
        BeanUtils.copyProperties(baseInfo, vo);
        Date birthday = baseInfo.getBirthday();
        if (!ObjectUtils.isEmpty(birthday)) {
            vo.setBirthdate(birthday);
            Integer age = DateUtil.differFromDate(birthday, new Date(System.currentTimeMillis()));
            vo.setAge(age);
        }
        if (StringHelper.isNotEmpty(medicalHistorys)) {
            vo.setMedicalHistorys(medicalHistorys.stream()
                    .map(PatientExtInfo::getDescription).collect(Collectors.toList()));
        }
        List<PatientExtInfo> allergns = patientExtInfoMapper.selectListByPatientId(patientId, 2);
        if (StringHelper.isNotEmpty(allergns)) {
            vo.setAllergns(allergns.stream().map(PatientExtInfo::getDescription).collect(Collectors.toList()));
        }
        putSignatureUrl(vo, patientId);

        PatientExpInfoVo expInfo = patientExpInfoMapper.selectByPatientId(patientId);
        if (!ObjectUtils.isEmpty(expInfo)) {
            BeanUtils.copyProperties(expInfo, vo);
            Integer brushTimes = expInfo.getBrushTimes();
            if (!ObjectUtils.isEmpty(brushTimes)) {
                vo.setBrushingTimes(brushTimes.shortValue());
            }
            vo.setDetailedAddress(expInfo.getAddress());
        }
        PatientChildInfoVO child = patientChildInfoMapper.selectPatientChildInfoByPatientId(patientId);
        if (!ObjectUtils.isEmpty(child)) {
            BeanUtils.copyProperties(child, vo);
            String habitIdStr = child.getHabitIdStr();
            if (StringHelper.isNotEmpty(habitIdStr)) {
                vo.setHabitIds(StringHelper.split2IntList(habitIdStr, ","));
            }
        }
        vo.setId(patientId);
        patientVO.setChildrenPatient(vo);
    }

    /**
     * 填充成人注册信息
     *
     * @param patientVO
     * @param baseInfo
     */
    private void putAdultRegistration(PatientRegistrationVO patientVO, PatientBaseInfo baseInfo) {
        Integer patientId = baseInfo.getId();
        List<PatientExtInfo> medicalHistorys = patientExtInfoMapper.selectListByPatientId(patientId, 1);
        AdultPatientRegistrationVO vo = new AdultPatientRegistrationVO();
        BeanUtils.copyProperties(baseInfo, vo);
        Date birthday = baseInfo.getBirthday();
        if (!ObjectUtils.isEmpty(birthday)) {
            vo.setBirthdate(birthday);
            Integer age = DateUtil.differFromDate(birthday, new Date(System.currentTimeMillis()));
            vo.setAge(age);
        }
        if (StringHelper.isNotEmpty(medicalHistorys)) {
            vo.setMedicalHistorys(medicalHistorys.stream()
                    .map(PatientExtInfo::getDescription).collect(Collectors.toList()));
        }
        List<PatientExtInfo> allergns = patientExtInfoMapper.selectListByPatientId(patientId, 2);
        if (StringHelper.isNotEmpty(allergns)) {
            vo.setAllergns(allergns.stream().map(PatientExtInfo::getDescription).collect(Collectors.toList()));
        }
        putSignatureUrl(vo, patientId);

        PatientExpInfoVo expInfo = patientExpInfoMapper.selectByPatientId(patientId);
        if (!ObjectUtils.isEmpty(expInfo)) {
            BeanUtils.copyProperties(expInfo, vo);
            vo.setBrushingTime(expInfo.getBrushTime());
            vo.setBrushingTimes(expInfo.getBrushTimes());
            vo.setDetailedAddress(expInfo.getAddress());
            vo.setPregnancyMonth(DateUtil.pregancyWeek2Month(expInfo.getPregnancyWeek()));
        }
        PatientToothInfo toothInfo = patientToothInfoMapper.selectPatientToothInfoByPatientId(patientId);
        if (!ObjectUtils.isEmpty(toothInfo)) {
            BeanUtils.copyProperties(toothInfo, vo);
            vo.setRpdDate(DateUtil.format(toothInfo.getRpdDate()));
            vo.setLpdDate(DateUtil.format(toothInfo.getLpdDate()));
            String fillTreatHistory = toothInfo.getFillTreatHistory();
            String missToothHistory = toothInfo.getMissToothHistory();
            if (StringHelper.isNotEmpty(missToothHistory)) {
                vo.setMissToothHistory(StringHelper.split2IntList(missToothHistory, ","));
            }
            if (StringHelper.isNotEmpty(fillTreatHistory)) {
                vo.setFillTreatHistory(StringHelper.split2IntList(fillTreatHistory, ","));
            }
            vo.setFillTreatLastDate(DateUtil.format(toothInfo.getFillTreatLastDate()));
            vo.setOrthodonticStartDate(DateUtil.format(toothInfo.getOrthodonticStartDate()));
            vo.setOrthodonticEndDate(DateUtil.format(toothInfo.getOrthodonticEndDate()));
        }
        vo.setMobileOwner(baseInfo.getMobileOwner());
        Integer originType = baseInfo.getOriginType();
        if (!ObjectUtils.isEmpty(originType)) {
            vo.setOriginType(originType);
            if (originType.intValue() == 1) {
                Integer recEmpId = baseInfo.getOriginId();
                vo.setEmployeeId(recEmpId);
                SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(recEmpId);
                if (!ObjectUtils.isEmpty(employee)) {
                    vo.setRecEmpName(employee.getName());
                }
                vo.setPatientId(null);
                vo.setOriginId(null);
            } else if (originType.intValue() == 2) {
                Integer recPatientId = vo.getOriginId();
                vo.setPatientId(recPatientId);
                PatientBaseInfo recPatient = mapper.selectByPrimaryKey(vo.getOriginId());
                if (!ObjectUtils.isEmpty(recPatient)) {
                    vo.setRecPatientName(recPatient.getName());
                }
                vo.setOriginId(null);
                vo.setEmployeeId(null);
            } else {
                vo.setOriginId(vo.getOriginId());
                vo.setPatientId(null);
                vo.setEmployeeId(null);
            }
        }
        vo.setId(patientId);
        patientVO.setAdultPatient(vo);
    }

    /**
     * 获取患者电子签名
     * @param baseVO
     */
    private void putSignatureUrl(PatientRegistrationBaseVO baseVO, Integer patientId) {
        XUploadFileQuery query = new XUploadFileQuery();
        query.setWhetherPage(false);
        query.setSourceIds(Arrays.asList(patientId));
        query.setSourceType(PATIENT_SIGNATURE.getCode());
        List<XUploadFileVO> files = remoteTreatmentOtherFeign.findXUploadFileList(query);
        if (StringHelper.isNotEmpty(files)) {
            String fileUrl = files.get(0).getFileLocation();
            baseVO.setSignatureImgUrl(fileUrl);
            OssUrlForm form = new OssUrlForm();
            form.setIsThumb(false);
            form.setCompanyId(0);
            form.setOssCategory(3);
            form.setObjectId(patientId);
            form.setOssFilename(fileUrl);
            String url = (String) remoteOssServiceFeign.getUrl(form).getData();
            baseVO.setSignatureImgPath(domainUrl+"/"+url);
        }
    }
}