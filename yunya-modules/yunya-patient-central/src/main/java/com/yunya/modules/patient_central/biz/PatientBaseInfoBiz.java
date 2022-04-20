package com.yunya.modules.patient_central.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.PatientPhotoForm;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PatientBaseInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLabelRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.query.SelfRegistrationPatientQuery;
import com.yunya.feign.patient_central.domain.vo.SelfRegistrationPatientVO;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientArchivesVo;
import com.yunya.feign.patient_central.domain.vo.app.AppPatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.LastTreatmentInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.DictionaryItem;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.modules.patient_central.constant.WoPlatformHeartbeat;
import com.yunya.modules.patient_central.mapper.*;
import org.apache.commons.httpclient.NameValuePair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.RedisConstants.PATIENT_BASE_INFO;

/**
 * 简单介绍:</br> 患者基本信息业务层
 *
 * @author: WY
 * @date 2020/7/25 11:25
 * @description: 患者基本信息业务层（增删查改）
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientBaseInfoBiz extends BaseBiz<PatientBaseInfoMapper, PatientBaseInfo> {

  private static final Logger logger = LoggerFactory.getLogger(PatientBaseInfoBiz.class);

  /** 注入redis */
  @Resource private RedisUtils redisUtils;

  /** 注入患者信息Mapper */
  @Resource private PatientBaseInfoMapper patientBaseInfoMapper;

  /** 注入患者其他信息Mapper */
  @Resource private PatientExtInfoMapper patientExtInfoMapper;

  /** 注入患者扩展信息Mapper */
  @Resource private PatientExpInfoMapper patientExpInfoMapper;

  /** 注入系统服务 */
  @Resource private RemoteSystemServiceFeign remoteSystemServiceFeign;

  /** 注入wo平台对象 */
  @Resource private WoPersonBiz woPersonBiz;

  /** 注入预付款Mapper */
  @Resource private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  /** 注入会员卡对象 */
  @Resource private PatientMemberInfoBiz patientMemberInfoBiz;

  /** 注入患者来源Mapper */
  @Resource private PatientOriginMapper patientOriginMapper;

  /** 注入会员卡Mapper */
  @Resource private PatientMemberInfoMapper patientMemberInfoMapper;

  /** 注入患者照片Mapper */
  @Resource private PatientImgMapper patientImgMapper;

  /** 设备心跳回调 */
  @Resource private InformationCallbackBiz informationCallbackBiz;

  /** 注入服务 */
  @Resource private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

  /** 标签Mapper */
  @Resource private PatientLabelRecordMapper patientLabelRecordMapper;

  /** 就诊服务 */
  @Resource private RemoteTreatmentServiceFeign treatmentServiceFeign;

  /** 报表服务 */
  @Resource private RemoteReportServiceFeign remoteReportServiceFeign;

  /** 患者来源绑定关系 */
  @Resource private PatientOriginLogMapper patientOriginLogMapper;

  /** 患者牙齿信息*/
  @Autowired private PatientToothInfoMapper patientToothInfoMapper;

  /** 患者儿童属性信息 */
  @Autowired private PatientChildInfoMapper patientChildInfoMapper;

  /** 患者分组关系*/
  @Autowired private PatientGroupRelationMapper patientGroupRelationMapper;

  /** 获取患者服务端口号 */
  @Value("${codeUrl.url}")
  private String servePrort;

  /**
   * 通过患者id查询患者共用属性
   *
   * @param id 患者id
   * @return PatientPublicInfo
   */
  public PatientPublicInfoVo findPatientPublicInfoById(Integer id) {
    PatientPublicInfoVo patientPublicInfoVo =
        this.patientBaseInfoMapper.findPatientPublicInfoById(id);
    if (patientPublicInfoVo != null && patientPublicInfoVo.getMemberTypeId() != null) {
      MemberType memberType =
          this.remoteSystemServiceFeign.findMemberTypeById(patientPublicInfoVo.getMemberTypeId());
      if (memberType != null) {
        patientPublicInfoVo.setMemberCardName(memberType.getName());
      }
    }
    return patientPublicInfoVo;
  }

  /**
   * 查询患者是否存在
   *
   * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
   */
  public ResponseResult findUserExists(PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
    PatientBaseInfoVo patientBaseInfoVo;
    patientBaseInfoVo = patientBaseInfoMapper.findUserExists(patientBaseInfoQueryForm);
    if (patientBaseInfoVo != null) {
      return ResponseUtil.fail(OperationCodeConstants.DATA_EXIST, "添加失败,该用户已存在", patientBaseInfoVo);
    }
    List<PatientBaseInfoVo> userExistsByMobile =
        patientBaseInfoMapper.findUserExistsByMobile(patientBaseInfoQueryForm.getMobile());
    if (StringHelper.isNotEmpty(userExistsByMobile)) {
      return ResponseUtil.fail(OperationCodeConstants.PHONE_EXIST, "该手机号已存在", userExistsByMobile);
    }
    return ResponseUtil.success();
  }

  /**
   * 添加患者信息
   *
   * @param patientBaseInfoModel 新增患者基本信息参数模型
   */
  public PatientBaseInfoVo addPatient(PatientBaseInfoModel patientBaseInfoModel) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientBaseInfoModel, patientBaseInfo);
    // 患者id不为空表明,是修改操作
    if (StringHelper.isNotNull(patientBaseInfoModel.getId())) {
      patientBaseInfoMapper.updateByPrimaryKeySelective(patientBaseInfo);
      redisUtils.delete(PATIENT_BASE_INFO + patientBaseInfoModel.getId());
      return patientBaseInfoMapper.selectPatienInfoById(patientBaseInfo.getId());
    }
    Integer originType = patientBaseInfo.getOriginType();
    if (null != originType) {
      if (patientBaseInfo.getOriginType() == 1 || patientBaseInfo.getOriginType() == 2) {
        if (patientBaseInfoModel.getSourceId() != null) {
          patientBaseInfo.setOriginId(patientBaseInfoModel.getSourceId());
        }
      }
    }
    patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
    patientBaseInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientBaseInfo.setCrtName(BaseContextHandler.getName());
    patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    mapper.insertPatientInfo(patientBaseInfo);
    PatientBaseInfoVo patientBaseInfoVo =
        this.patientBaseInfoMapper.selectPatienInfoById(patientBaseInfo.getId());
    if (patientBaseInfoVo.getOriginId() != null) {
      PatientOriginLog patientOriginLog = new PatientOriginLog();
      patientOriginLog.setPatientId(patientBaseInfoVo.getId());
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
      remoteRabbitMqServiceFeign.sendMessage(
          patientOriginLog.getId(), 0, MsgCategoryEnum.BasePatientOriginLog);
    }
    // 创建预付款 并发送消息
    this.addPatientPrepaymentsInfo(patientBaseInfo);
    sendMessages(patientBaseInfo.getId(), 0);
    return patientBaseInfoVo;
  }

  /**
   * 发送消息
   *
   * @param id 操作id
   */
  public void sendMessages(Integer id, Integer operateType) {
    MessageModel messageModel = new MessageModel();
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", id);
    messageModel.setParamMap(map);
    messageModel.setOperateType(operateType);
    messageModel.setMsgCategoryEnum(MsgCategoryEnum.BasePatient);
    remoteRabbitMqServiceFeign.sendMessage(messageModel);
  }

  /**
   * 添加患者时,创建预付款账户
   *
   * @param patientBaseInfo 患者信息
   */
  public void addPatientPrepaymentsInfo(PatientBaseInfo patientBaseInfo) {
    if (patientBaseInfo.getId() != null) {
      PatientPrepaymentsInfo patientPrepaymentsInfo = new PatientPrepaymentsInfo();
      patientPrepaymentsInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      patientPrepaymentsInfo.setPatientId(patientBaseInfo.getId());
      // 预付款卡号生成规则 开通Y
      String cardNumberStr = this.patientMemberInfoBiz.generateCardNumber("Y");
      patientPrepaymentsInfo.setPrepaymentNumber(cardNumberStr);
      patientPrepaymentsInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientPrepaymentsInfo.setCrtName(BaseContextHandler.getName());
      this.patientPrepaymentsInfoMapper.insertSelective(patientPrepaymentsInfo);
      remoteRabbitMqServiceFeign.sendMessage(
          patientPrepaymentsInfo.getId(), 1, 0, MsgCategoryEnum.BasePatientMember);
    }
  }

  /**
   * 添加完善患者扩展信息、其他信息
   *
   * @param patientExtendInfoModel 患者基本信息+扩展信息+其他信息 参数模板
   */
  public void addPatientInfo(PatientExtendInfoModel patientExtendInfoModel) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    PatientBaseInfoModel patientBaseInfoModel = patientExtendInfoModel.getPatientBaseInfoModel();
    BeanUtils.copyProperties(patientBaseInfoModel, patientBaseInfo);
    patientBaseInfo.setPinyinName(HanyuPinyinHelper.toHanyuPinyin(patientBaseInfo.getName()));
    patientBaseInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientBaseInfo.setUpdName(BaseContextHandler.getName());
    patientBaseInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientBaseInfo.setUpdTime(new Date());
    Integer originType = patientBaseInfo.getOriginType();
    if (null != originType) {
      if (patientBaseInfo.getOriginType() == 1 || patientBaseInfo.getOriginType() == 2) {
        patientBaseInfo.setOriginId(patientExtendInfoModel.getPatientBaseInfoModel().getSourceId());
      }
    }
    // 添加患者来源推荐关系
    addPatientOrigin(patientBaseInfo);

    // 完善患者基本信息  对补全信息进行更新
    this.mapper.updateByPrimaryKeySelective(patientBaseInfo);
    Integer patientId = patientBaseInfo.getId();
    // 患者分组
    savePatientGroupRelation(patientId, patientBaseInfoModel.getGroupIds());
    redisUtils.delete(PATIENT_BASE_INFO + patientId);
    sendMessages(patientId, 1);

    PatientExpInfo patientExpInfo = new PatientExpInfo();
    PatientExpInfoModel patientExpInfoModel = patientExtendInfoModel.getPatientExpInfoModel();
    // 完善患者扩展信息
    BeanUtils.copyProperties(patientExpInfoModel, patientExpInfo);
    Integer pregnancyMonth = patientExpInfoModel.getPregnancyMonth();
    if (!ObjectUtils.isEmpty(pregnancyMonth)) {
      patientExpInfo.setPregnancyWeek(DateUtil.pregancyMonth2Week(pregnancyMonth));
    }
    savePatientToothInfo(patientExpInfoModel, patientId);
    PatientChildInfoModel patientChildInfoModel = patientExtendInfoModel.getPatientChildInfoModel();
    if (!ObjectUtils.isEmpty(patientChildInfoModel)) {
      savePatientChildInfo(patientChildInfoModel, patientId);
      String guardian = patientChildInfoModel.getGuardian();
      if (StringHelper.isNotEmpty(guardian)) {
        patientExpInfo.setGuardian(guardian);
      }
    }
    // 如果用户没有扩展信息就添加扩展信息 如果有就修改
    PatientExpInfoVo patientExpInfoVo =
        patientExpInfoMapper.selectByPatientId(patientId);
    if (patientExpInfoVo == null) {
      patientExpInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientExpInfo.setCrtName(BaseContextHandler.getName());
      // 添加扩展信息
      this.patientExpInfoMapper.insertSelective(patientExpInfo);
    } else {
      patientExpInfo.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientExpInfo.setUpdName(BaseContextHandler.getName());
      patientExpInfo.setUpdTime(new Date());
      // 修改扩展信息
      this.patientExpInfoMapper.updateByPrimaryKey(patientExpInfo);
    }

    // 完善患者其他信息（标签、疾病史、过敏原）
    List<PatientExtInfoModel> patientExtInfoList =
        patientExtendInfoModel.getPatientExtInfoModelList();
    List<PatientExtInfoVo> patientExtInfos =
        this.patientExtInfoMapper.patientExtInfoListByid(patientId);
    // 判断是否已存在信息，若存在就删除
    if (!StringHelper.isEmpty(patientExtInfos)) {
      this.patientExtInfoMapper.deletePatientExtInfoByPatientId(patientId);
    }
    if (!StringHelper.isEmpty(patientExtInfoList)) {
      List<PatientExtInfoModel> addPatientExtInfoList = new ArrayList<>();
      Iterator patientExtInfoModelIterator = patientExtInfoList.iterator();
      // 循环添加 标签、疾病史、过敏原 集合
      while (patientExtInfoModelIterator.hasNext()) {
        PatientExtInfoModel patientExtInfoModel =
            (PatientExtInfoModel) patientExtInfoModelIterator.next();
        patientExtInfoModel.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientExtInfoModel.setCrtName(BaseContextHandler.getName());
        patientExtInfoModel.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientExtInfoModel.setUpdName(BaseContextHandler.getName());
        patientExtInfoModel.setUpdTime(new Date());
        addPatientExtInfoList.add(patientExtInfoModel);
      }
      this.patientExtInfoMapper.insertPatientExtInfoList(addPatientExtInfoList);
    }
  }

  /**
   * 保存患者分组关系
   *
   * @param patientId
   * @param groupIds
   */
  private void savePatientGroupRelation(Integer patientId, List<Integer> groupIds) {
    Integer userId = Integer.parseInt(BaseContextHandler.getUserID());
    Date now = new Date(System.currentTimeMillis());
    Example example = new Example(PatientGroupRelation.class);
    Example.Criteria c = example.createCriteria();
    c.andEqualTo("patientId", patientId);
    patientGroupRelationMapper.deleteByExample(example);
    if (StringHelper.isNotEmpty(groupIds)) {
      groupIds.forEach(groupId->{
        PatientGroupRelation entity = new PatientGroupRelation();
        entity.setPatientId(patientId);
        entity.setGroupId(groupId);
        entity.setCrtId(userId);
        entity.setCrtTime(now);
        entity.setUptId(userId);
        entity.setUptTime(now);
        patientGroupRelationMapper.insertSelective(entity);
      });
    }
  }

  /**
   * 保存患者儿童属性信息
   *
   * @param model
   * @param patientId
   */
  private void savePatientChildInfo(PatientChildInfoModel model, Integer patientId) {
    Date now = new Date(System.currentTimeMillis());
    int userId = Integer.parseInt(BaseContextHandler.getUserID());
    PatientChildInfo query = new PatientChildInfo();
    query.setPatientId(patientId);
    query.setInservice(true);
    PatientChildInfo childInfo = patientChildInfoMapper.selectOne(query);
    if (!ObjectUtils.isEmpty(childInfo)) {
      childInfo.setSchool(model.getSchool());
      childInfo.setGrade(model.getGrade());
      childInfo.setMedicationHistory(model.getMedicationHistory());
      childInfo.setMotherPregnancy(model.getMotherPregnancy());
      childInfo.setFatherHasCaries(model.getFatherHasCaries());
      childInfo.setMotherHasCaries(model.getMotherHasCaries());
      childInfo.setToothClearliness(model.getToothClearliness());
      String toothLastCheck = model.getToothLastCheck();
      if (StringHelper.isNotEmpty(toothLastCheck)) {
        childInfo.setToothLastCheck(DateUtil.parse2Date(toothLastCheck));
      }
      childInfo.setToothSprouting(model.getToothSprouting());
      childInfo.setUsedDentalFloss(model.getUsedDentalFloss());
      childInfo.setUsedFluorideToothpaste(model.getUsedFluorideToothpaste());
      childInfo.setUseFlossTimes(model.getUseFlossTimes());
      childInfo.setBrushingTimes(model.getBrushingTimes());
      childInfo.setDiet(model.getDiet());
      List<Integer> habitIds = model.getHabitIds();
      if (StringHelper.isNotEmpty(habitIds)) {
        childInfo.setHabitIds(StringHelper.join(habitIds, ","));
      }
      childInfo.setUptId(userId);
      childInfo.setUptTime(now);
      patientChildInfoMapper.updateByPrimaryKey(childInfo);
    } else {
      childInfo = new PatientChildInfo();
      childInfo.setPatientId(patientId);
      childInfo.setSchool(model.getSchool());
      childInfo.setGrade(model.getGrade());
      childInfo.setMedicationHistory(model.getMedicationHistory());
      childInfo.setMotherPregnancy(model.getMotherPregnancy());
      childInfo.setFatherHasCaries(model.getFatherHasCaries());
      childInfo.setMotherHasCaries(model.getMotherHasCaries());
      childInfo.setToothClearliness(model.getToothClearliness());
      String toothLastCheck = model.getToothLastCheck();
      if (StringHelper.isNotEmpty(toothLastCheck)) {
        childInfo.setToothLastCheck(DateUtil.parse2Date(toothLastCheck));
      }
      childInfo.setToothSprouting(model.getToothSprouting());
      childInfo.setUsedDentalFloss(model.getUsedDentalFloss());
      childInfo.setUsedFluorideToothpaste(model.getUsedFluorideToothpaste());
      childInfo.setUseFlossTimes(model.getUseFlossTimes());
      childInfo.setBrushingTimes(model.getBrushingTimes());
      childInfo.setDiet(model.getDiet());
      List<Integer> habitIds = model.getHabitIds();
      if (StringHelper.isNotEmpty(habitIds)) {
        childInfo.setHabitIds(StringHelper.join(habitIds, ","));
      }
      childInfo.setCrtId(userId);
      childInfo.setCrtTime(now);
      childInfo.setUptId(userId);
      childInfo.setUptTime(now);
      patientChildInfoMapper.insertSelective(childInfo);
    }
  }

  /**
   * 保存患者的牙齿信息
   *
   * @param expInfo
   * @param patientId
   */
  private void savePatientToothInfo(PatientExpInfoModel expInfo, Integer patientId) {
    if (!ObjectUtils.isEmpty(expInfo)) {
      Date now = new Date(System.currentTimeMillis());
      int userId = Integer.parseInt(BaseContextHandler.getUserID());
      PatientToothInfo query = new PatientToothInfo();
      query.setPatientId(patientId);
      query.setInservice(true);
      PatientToothInfo toothInfo = patientToothInfoMapper.selectOne(query);
      if (!ObjectUtils.isEmpty(toothInfo)) {
        toothInfo.setHadMissTooth(expInfo.getHadMissTooth());
        List<Integer> missToothIds = expInfo.getMissToothHistory();
        if (StringHelper.isNotEmpty(missToothIds)) {
          toothInfo.setMissToothHistory(StringHelper.join(missToothIds, ","));
        }
        toothInfo.setHadFillTreat(expInfo.getHadFillTreat());
        List<Integer> fillMeterials = expInfo.getFillTreatHistory();
        if (StringHelper.isNotEmpty(fillMeterials)) {
          toothInfo.setFillTreatHistory(StringHelper.join(fillMeterials, ","));
        }
        String fillTreatLastDate = expInfo.getFillTreatLastDate();
        if (StringHelper.isNotEmpty(fillTreatLastDate)) {
          toothInfo.setFillTreatLastDate(DateUtil.parse2Date(fillTreatLastDate));
        }
        toothInfo.setHadPeriodontalSurgery(expInfo.getHadPeriodontalSurgery());
        toothInfo.setHadOcclusalAdjust(expInfo.getHadOcclusalAdjust());
        toothInfo.setHadRestorativeDentures(expInfo.getHadRestorativeDentures());
        toothInfo.setRpdPart(expInfo.getRpdPart());
        String rpdDate = expInfo.getRpdDate();
        if (StringHelper.isNotEmpty(rpdDate)) {
          toothInfo.setRpdDate(DateUtil.parse2Date(rpdDate));
        }
        toothInfo.setLpdPart(expInfo.getLpdPart());
        String lpdDate = expInfo.getLpdDate();
        if (StringHelper.isNotEmpty(lpdDate)) {
          toothInfo.setLpdDate(DateUtil.parse2Date(lpdDate));
        }
        toothInfo.setHadOrthodontic(expInfo.getHadOrthodontic());
        String ortSDate = expInfo.getOrthodonticStartDate();
        if (StringHelper.isNotEmpty(ortSDate)) {
          toothInfo.setOrthodonticStartDate(DateUtil.parse2Date(ortSDate));
        }
        String ortEDate = expInfo.getOrthodonticEndDate();
        if (StringHelper.isNotEmpty(ortEDate)) {
          toothInfo.setOrthodonticEndDate(DateUtil.parse2Date(ortEDate));
        }
        toothInfo.setHadPreventiveTreat(expInfo.getHadPreventiveTreat());
        toothInfo.setPreventiveTreatCycle(expInfo.getPreventiveTreatCycle());
        toothInfo.setPreventiveTreatLastMonth(expInfo.getPreventiveTreatLastMonth());
        toothInfo.setHadDiffcultTreat(expInfo.getHadDiffcultTreat());
        toothInfo.setHadHygieneEducation(expInfo.getHadHygieneEducation());
        toothInfo.setMissTeethUnrepeatCause(expInfo.getMissTeethUnrepeatCause());
        toothInfo.setUsedPlaqueDna(expInfo.getUsedPlaqueDna());
        toothInfo.setUptId(userId);
        toothInfo.setUptTime(now);
        patientToothInfoMapper.updateByPrimaryKeySelective(toothInfo);
      } else {
        toothInfo = new PatientToothInfo();
        toothInfo.setPatientId(patientId);
        toothInfo.setHadMissTooth(expInfo.getHadMissTooth());
        List<Integer> missToothIds = expInfo.getMissToothHistory();
        if (StringHelper.isNotEmpty(missToothIds)) {
          toothInfo.setMissToothHistory(StringHelper.join(missToothIds, ","));
        }
        toothInfo.setHadFillTreat(expInfo.getHadFillTreat());
        List<Integer> fillMeterials = expInfo.getFillTreatHistory();
        if (StringHelper.isNotEmpty(fillMeterials)) {
          toothInfo.setFillTreatHistory(StringHelper.join(fillMeterials, ","));
        }
        String fillTreatLastDate = expInfo.getFillTreatLastDate();
        if (StringHelper.isNotEmpty(fillTreatLastDate)) {
          toothInfo.setFillTreatLastDate(DateUtil.parse2Date(fillTreatLastDate));
        }
        toothInfo.setHadPeriodontalSurgery(expInfo.getHadPeriodontalSurgery());
        toothInfo.setHadOcclusalAdjust(expInfo.getHadOcclusalAdjust());
        toothInfo.setHadRestorativeDentures(expInfo.getHadRestorativeDentures());
        toothInfo.setRpdPart(expInfo.getRpdPart());
        String rpdDate = expInfo.getRpdDate();
        if (StringHelper.isNotEmpty(rpdDate)) {
          toothInfo.setRpdDate(DateUtil.parse2Date(rpdDate));
        }
        toothInfo.setLpdPart(expInfo.getLpdPart());
        String lpdDate = expInfo.getLpdDate();
        if (StringHelper.isNotEmpty(lpdDate)) {
          toothInfo.setLpdDate(DateUtil.parse2Date(lpdDate));
        }
        toothInfo.setHadOrthodontic(expInfo.getHadOrthodontic());
        String ortSDate = expInfo.getOrthodonticStartDate();
        if (StringHelper.isNotEmpty(ortSDate)) {
          toothInfo.setOrthodonticStartDate(DateUtil.parse2Date(ortSDate));
        }
        String ortEDate = expInfo.getOrthodonticEndDate();
        if (StringHelper.isNotEmpty(ortEDate)) {
          toothInfo.setOrthodonticEndDate(DateUtil.parse2Date(ortEDate));
        }
        toothInfo.setHadPreventiveTreat(expInfo.getHadPreventiveTreat());
        toothInfo.setPreventiveTreatCycle(expInfo.getPreventiveTreatCycle());
        toothInfo.setPreventiveTreatLastMonth(expInfo.getPreventiveTreatLastMonth());
        toothInfo.setHadDiffcultTreat(expInfo.getHadDiffcultTreat());
        toothInfo.setHadHygieneEducation(expInfo.getHadHygieneEducation());
        toothInfo.setMissTeethUnrepeatCause(expInfo.getMissTeethUnrepeatCause());
        toothInfo.setUsedPlaqueDna(expInfo.getUsedPlaqueDna());
        toothInfo.setCrtId(userId);
        toothInfo.setCrtTime(now);
        toothInfo.setUptId(userId);
        toothInfo.setUptTime(now);
        patientToothInfoMapper.insertSelective(toothInfo);
      }
    }
  }

  /**
   * 添加患者来源推荐关系
   *
   * @param patientBaseInfo 患者信息
   */
  public void addPatientOrigin(PatientBaseInfo patientBaseInfo) {
    if (patientBaseInfo.getOriginId() != null) {
      PatientOriginLog patientOriginLog = new PatientOriginLog();
      patientOriginLog.setPatientId(patientBaseInfo.getId());
      patientOriginLog.setInservice(true);
      PatientOriginLog patientOriginLogVo = patientOriginLogMapper.selectOne(patientOriginLog);
      PatientOriginLog insertPatientOriginLog = new PatientOriginLog();
      if (patientOriginLogVo != null) {
        if (!patientOriginLogVo.getOriginId().equals(patientBaseInfo.getOriginId())) {
          insertPatientOriginLog.setPatientId(patientBaseInfo.getId());
          insertPatientOriginLog.setOriginType(patientBaseInfo.getOriginType());
          insertPatientOriginLog.setOriginId(patientBaseInfo.getOriginId());
          insertPatientOriginLog.setInservice(patientBaseInfo.getInservice());
          insertPatientOriginLog.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
          insertPatientOriginLog.setCrtName(BaseContextHandler.getName());
          insertPatientOriginLog.setCrtTime(new Date());
          insertPatientOriginLog.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
          insertPatientOriginLog.setUpdName(BaseContextHandler.getName());
          insertPatientOriginLog.setUpdTime(new Date());

          // 修改推荐关系状态 并发送消息
          patientOriginLogVo.setInservice(false);
          patientOriginLogMapper.updateByPrimaryKey(patientOriginLogVo);
          sendMemberRelationMessages(patientOriginLogVo.getId(), 1);
          // 添加推荐关系 并发送消息
          patientOriginLogMapper.insertSelective(insertPatientOriginLog);
          remoteRabbitMqServiceFeign.sendMessage(
              insertPatientOriginLog.getId(), 0, MsgCategoryEnum.BasePatientOriginLog);
        }
      } else {
        insertPatientOriginLog.setPatientId(patientBaseInfo.getId());
        insertPatientOriginLog.setOriginType(patientBaseInfo.getOriginType());
        insertPatientOriginLog.setOriginId(patientBaseInfo.getOriginId());
        insertPatientOriginLog.setInservice(patientBaseInfo.getInservice());
        insertPatientOriginLog.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        insertPatientOriginLog.setCrtName(BaseContextHandler.getName());
        insertPatientOriginLog.setCrtTime(new Date());
        insertPatientOriginLog.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        insertPatientOriginLog.setUpdName(BaseContextHandler.getName());
        insertPatientOriginLog.setUpdTime(new Date());
        patientOriginLogMapper.insertSelective(insertPatientOriginLog);
        remoteRabbitMqServiceFeign.sendMessage(
            insertPatientOriginLog.getId(), 0, MsgCategoryEnum.BasePatientOriginLog);
      }
    }
  }

  /**
   * 会员关联消息 参数模板
   *
   * @param id 操作
   * @param OperateType 操作类型
   */
  public void sendMemberRelationMessages(Integer id, Integer OperateType) {
    MessageModel messageModel = new MessageModel();
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", id);
    messageModel.setParamMap(map);
    messageModel.setOperateType(OperateType);
    messageModel.setMsgCategoryEnum(MsgCategoryEnum.BasePatientOriginLog);
    remoteRabbitMqServiceFeign.sendMessage(messageModel);
  }

  /**
   * 根据患者id查询患者资料
   *
   * @param id 患者id
   * @return PatientExtendInfoModel
   */
  public ResponseResult<PatientExtendInfoVo> findPatientData(Integer id) {
    PatientExtendInfoVo patientExtendInfoVo = new PatientExtendInfoVo();
    PatientBaseInfo patientBaseInfo = this.mapper.selectByPrimaryKey(id);
    if (patientBaseInfo == null) {
      return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "未查询到患者信息", "");
    }
    PatientBaseInfoVo patientBaseInfoVo = new PatientBaseInfoVo();
    Date birthday = patientBaseInfo.getBirthday();
    if (birthday != null) {
      // 计算年龄
      Integer age = DateUtil.differFromDate(birthday, new Date(System.currentTimeMillis()));
      patientBaseInfo.setAge(age);
      String timeStr = new DateTime(birthday).toString("yyyy-MM-dd");
      patientBaseInfoVo.setBirthday(timeStr);
    }

    BeanUtils.copyProperties(patientBaseInfo, patientBaseInfoVo);
    markAttribute(patientBaseInfoVo);
    int originType = 2;
    if (patientBaseInfo.getOriginType() != null && patientBaseInfo.getOriginType() > originType) {
      PatientOrigin patientOrigin =
          patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
      if (patientOrigin != null) {
        // 获取患者来源的父级id
        patientBaseInfoVo.setSourceParentId(patientOrigin.getParentId());
        patientBaseInfoVo.setSourceName(patientOrigin.getName());
      }
    }
    // 基本信息
    patientExtendInfoVo.setPatientBaseInfoVo(getTypeName(patientBaseInfoVo));
    // 扩展信息
    PatientExpInfoVo patientExpInfoVo = patientExpInfoMapper.selectByPatientId(id);
    if (patientExpInfoVo != null) {
      if (patientExpInfoVo.getPatientKind() != null) {
        DictionaryItem item =
            remoteSystemServiceFeign.findDictionaryItemById(patientExpInfoVo.getPatientKind());
        if (item != null) {
          patientExpInfoVo.setPatientKindName(item.getName());
        }
      }
      fillPatientToothInfo(patientExpInfoVo);
      patientExtendInfoVo.setPatientExpInfoVo(patientExpInfoVo);
    }

    // 标签
    List<PatientExtInfoVo> patientExtInfoVos = patientExtInfoMapper.patientExtInfoListByid(id);
    if (!StringHelper.isEmpty(patientExtInfoVos)) {
      for (PatientExtInfoVo patientExtInfoVo : patientExtInfoVos) {
        if (patientExtInfoVo.getDictItemId() != null) {
          DictionaryItem dictionaryItem =
              remoteSystemServiceFeign.findDictionaryItemById(patientExtInfoVo.getDictItemId());
          if (dictionaryItem != null) {
            patientExtInfoVo.setDictItemName(dictionaryItem.getName());
          }
        }
      }
    }
    patientExtendInfoVo.setPatientExtInfoListVo(patientExtInfoVos);

    fillPatientChildInfo(patientExtendInfoVo, patientBaseInfo.getId());
    return ResponseUtil.success(patientExtendInfoVo);
  }

  /**
   * 填充儿童属性
   *
   * @param patientExtendInfoVo
   * @param patientId
   */
  private void fillPatientChildInfo(PatientExtendInfoVo patientExtendInfoVo, Integer patientId) {
    PatientChildInfoVO childInfo = patientChildInfoMapper.selectPatientChildInfoByPatientId(patientId);
    if (!ObjectUtils.isEmpty(childInfo)) {
      String habitIdStr = childInfo.getHabitIdStr();
      if (StringHelper.isNotEmpty(habitIdStr)) {
        childInfo.setHabitIds(StringHelper.split2IntList(habitIdStr, ","));
      }
      patientExtendInfoVo.setPatientChildInfoVO(childInfo);
    }
  }

  /**
   * 填充牙齿信息
   *
   * @param expInfo
   */
  private void fillPatientToothInfo(PatientExpInfoVo expInfo) {
    Integer pregnancyWeek = expInfo.getPregnancyWeek();
    if (!ObjectUtils.isEmpty(pregnancyWeek)) {
      expInfo.setPregnancyMonth(DateUtil.pregancyWeek2Month(pregnancyWeek));
    }
    PatientToothInfo toothInfo = patientToothInfoMapper.selectPatientToothInfoByPatientId(expInfo.getPatientId());
    if (!ObjectUtils.isEmpty(toothInfo)) {
      expInfo.setToothRecordId(toothInfo.getId());
      expInfo.setHadMissTooth(toothInfo.getHadMissTooth());
      String missToothHistory = toothInfo.getMissToothHistory();
      if (StringHelper.isNotEmpty(missToothHistory)) {
        expInfo.setMissToothHistory(StringHelper.split2IntList(missToothHistory, ","));
      }
      expInfo.setMissTeethUnrepeatCause(toothInfo.getMissTeethUnrepeatCause());
      expInfo.setHadFillTreat(toothInfo.getHadFillTreat());
      String fillTreatHistory = toothInfo.getFillTreatHistory();
      if (StringHelper.isNotEmpty(fillTreatHistory)) {
        expInfo.setFillTreatHistory(StringHelper.split2IntList(fillTreatHistory,","));
      }
      expInfo.setFillTreatLastDate(DateUtil.format(toothInfo.getFillTreatLastDate()));
      expInfo.setHadOcclusalAdjust(toothInfo.getHadOcclusalAdjust());
      expInfo.setHadPreventiveTreat(toothInfo.getHadPreventiveTreat());
      expInfo.setRpdPart(toothInfo.getRpdPart());
      expInfo.setRpdDate(DateUtil.format(toothInfo.getRpdDate()));
      expInfo.setLpdPart(toothInfo.getLpdPart());
      expInfo.setLpdDate(DateUtil.format(toothInfo.getLpdDate()));
      expInfo.setPreventiveTreatCycle(toothInfo.getPreventiveTreatCycle());
      expInfo.setPreventiveTreatLastMonth(toothInfo.getPreventiveTreatLastMonth());
      expInfo.setHadRestorativeDentures(toothInfo.getHadRestorativeDentures());
      expInfo.setUsedPlaqueDna(toothInfo.getUsedPlaqueDna());
      expInfo.setHadPeriodontalSurgery(toothInfo.getHadPeriodontalSurgery());
      expInfo.setHadOrthodontic(toothInfo.getHadOrthodontic());
      expInfo.setOrthodonticStartDate(DateUtil.format(toothInfo.getOrthodonticStartDate()));
      expInfo.setOrthodonticEndDate(DateUtil.format(toothInfo.getOrthodonticEndDate()));
      expInfo.setHadHygieneEducation(toothInfo.getHadHygieneEducation());
      expInfo.setHadDiffcultTreat(toothInfo.getHadDiffcultTreat());
    }
  }

  /**
   * 标记展示属性
   *
   * @param patientBaseInfoVo
   */
  private void markAttribute(PatientBaseInfoVo patientBaseInfoVo) {
    Integer crtId = patientBaseInfoVo.getCrtId();
    Byte attribute = 0;
    if (crtId==-666 || crtId==-777) {// 成人or儿童患者登记
      // 年龄小于12岁的展示儿童属性
      if (patientBaseInfoVo.getAge() <= 12) {
        attribute = 1;
      }
    }
    patientBaseInfoVo.setAttribute(attribute);
  }

  /**
   * 获取 来源名称 推荐人名称 推荐来源名称
   *
   * @param patientBaseInfoVo 患者信息
   * @return PatientBaseInfoVo
   */
  private PatientBaseInfoVo getTypeName(PatientBaseInfoVo patientBaseInfoVo) {
    if (patientBaseInfoVo.getOriginType() != null) {
      PatientOrigin patientOrig;
      PatientOrigin patientOrigin =
          patientOriginMapper.getTypeName(patientBaseInfoVo.getOriginType());
      if (patientOrigin != null) {
        patientBaseInfoVo.setOriginTypeName(patientOrigin.getName());
      }
      if (patientBaseInfoVo.getOriginId() != null) {
        switch (patientBaseInfoVo.getOriginType()) {
            // 查询员工
          case 1:
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setUserId(patientBaseInfoVo.getOriginId());
            model.setWhetherPage(false);
            List<SysUserInfoDetail> list =
                remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            if (!StringHelper.isEmpty(list)) {
              patientBaseInfoVo.setOriginName(list.get(0).getName());
            }
            //              patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
            patientOrig = patientOriginMapper.getTypeName(1);
            if (patientOrig != null) {
              patientBaseInfoVo.setSourceName(patientOrig.getName());
            }
            patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
            break;
            // 查询患者
          case 2:
            PatientBaseInfo patientBaseInfo =
                patientBaseInfoMapper.selectByPrimaryKey(patientBaseInfoVo.getOriginId());
            if (patientBaseInfo != null) {
              patientBaseInfoVo.setOriginName(patientBaseInfo.getName());
            }
            //              patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
            patientOrig = patientOriginMapper.getTypeName(2);
            if (patientOrig != null) {
              patientBaseInfoVo.setSourceName(patientOrig.getName());
            }
            patientBaseInfoVo.setSourceId(patientBaseInfoVo.getOriginId());
            break;
          default:
            PatientOrigin activity =
                patientOriginMapper.selectByPrimaryKey(patientBaseInfoVo.getOriginId());
            if (activity != null) {
              if (activity.getSourceAttribute() != null) {
                DictionaryItem dictionaryItemById =
                    remoteSystemServiceFeign.findDictionaryItemById(activity.getSourceAttribute());
                if (dictionaryItemById != null) {
                  patientBaseInfoVo.setOriginName(
                      dictionaryItemById.getName() + "-" + activity.getName());
                  patientBaseInfoVo.setSourceName(
                      dictionaryItemById.getName() + "-" + activity.getName());
                } else {
                  patientBaseInfoVo.setOriginName(activity.getName());
                  patientBaseInfoVo.setSourceName(activity.getName());
                }
              }
            }
            break;
        }
      }
    }
    Integer mobileOwner = patientBaseInfoVo.getMobileOwner();
    if (!ObjectUtils.isEmpty(mobileOwner)) {
      // 根据ID查询字典明细
      DictionaryItem dictionaryItem =
              remoteSystemServiceFeign.findDictionaryItemById(mobileOwner);
      if (dictionaryItem != null) {
        // 手机号所属名称
        patientBaseInfoVo.setMobileOwnerName(dictionaryItem.getName());
      }
    }
    return patientBaseInfoVo;
  }

  /**
   * 模糊查询患者
   *
   * @param form 患者模糊查询模板
   * @return List<PatientBaseInfoVo>
   */
  public List<PatientBaseInfoVo> findPatientByNameAndMobile(PatientLikeFinleQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<PatientBaseInfoVo> patients = patientBaseInfoMapper.findPatientByNameAndMobile(form);
    if (StringHelper.isNotEmpty(patients)) {
      // 调用就诊服务查询患者末次就诊记录
      if (StringHelper.isNotEmpty(patients)) {
        List<Integer> ids = patients.stream().map(PatientBaseInfoVo::getId).collect(Collectors.toList());
        List<LastTreatmentInfoVO> lastTreatmentInfoVOS = treatmentServiceFeign.lastTreatmentInfoByBatch(ids);
        if (!lastTreatmentInfoVOS.isEmpty()) {
          for (PatientBaseInfoVo patient : patients) {
            lastTreatmentInfoVOS.stream().filter(vo->vo.getPatientId().equals(patient.getId())).findFirst().ifPresent(info->{
              patient.setLastVisitTime(info.getTreatmentDate());
              patient.setLastVisit(info.getDentistName());
            });

          }
        }
        return patients.stream()
            .filter(
                entity ->
                    !(null != entity.getMedicalNumber() && entity.getMedicalNumber().contains("*")))
            .collect(Collectors.toList());
      }
    }
    return patients;
  }

  /**
   * 根据患者id集合查询患者list
   *
   * @param ids 患者id集合
   * @param hasDied 是否去世
   * @return List<PatientBaseInfoVo>
   */
  public List<PatientBaseInfoVo> findPatientInfoByIds(List<Integer> ids, Boolean hasDied) {
    return patientBaseInfoMapper.selectPatientInfoByIdList(ids, hasDied);
  }

  /**
   * 拍照
   *
   * @param id 患者id
   */
  public void takeAPhoto(Integer id) {
    PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectPatientById(id);
    if (patientBaseInfo != null) {
      JSONObject object = new JSONObject();
      object.put("taskNo", "faceTakeImg");
      object.put("interfaceName", "face/takeImg");
      object.put("personId", patientBaseInfo.getPersonId());
      object.put("result", true);
      redisUtils.set("takePhotosPatientId", patientBaseInfo.getId());

      // 如果未获取到设备号 任务就不创建
      if (StringHelper.isNotNull(informationCallbackBiz.getSN())) {
        redisUtils.set(informationCallbackBiz.getSN(), object);
      }
      redisUtils.set("userId", BaseContextHandler.getUserID());
      redisUtils.set("userName", BaseContextHandler.getName());
    }
  }

  /**
   * 删除照片并查询
   *
   * @param faceId 硬件照片id
   */
  public ResponseResult deleteThePhoto(String faceId) {
    PatientImg patientImg = patientImgMapper.selectByFaceId(faceId);
    if (StringHelper.isNotNull(patientImg)) {
      PatientBaseInfoVo patientBaseInfoVo =
          patientBaseInfoMapper.selectOneById(patientImg.getPatientId());
      if (StringHelper.isNotNull(patientBaseInfoVo)) {

        // 判断删除的是否是第一张照片，如果是 同时清空患者头像
        if (StringHelper.isNotEmpty(patientImg.getFaceIdOne())) {
          if (patientImg.getFaceIdOne().equals(faceId)) {
            patientBaseInfoMapper.updateFaceUrlById(patientBaseInfoVo.getId());
            patientImgMapper.updateFaceImg("img_one", "face_id_one", patientImg.getId());
          }
        }

        // 判断是否删除第二张照片
        if (StringHelper.isNotEmpty(patientImg.getFaceIdTwo())) {
          if (patientImg.getFaceIdTwo().equals(faceId)) {
            patientImgMapper.updateFaceImg("img_two", "face_id_two", patientImg.getId());
          }
        }

        // 判断是否删除第三张照片
        if (StringHelper.isNotEmpty(patientImg.getFaceIdThree())) {
          if (patientImg.getFaceIdThree().equals(faceId)) {
            patientImgMapper.updateFaceImg("img_three", "face_id_three", patientImg.getId());
          }
        }

        // 创建任务
        JSONObject object = new JSONObject();
        object.put("taskNo", "faceDelete");
        object.put("interfaceName", "face/delete");
        object.put("faceId", faceId);
        object.put("result", true);

        // 如果未获取到设备号 任务就不创建
        if (StringHelper.isNotNull(informationCallbackBiz.getSN())) {
          redisUtils.set(informationCallbackBiz.getSN(), object);
        }

        return ResponseUtil.success();
      }
      return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "未查询到患者信息", "");
    }
    return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL, "未查询到患者照片对象信息", "");
  }

  /**
   * 获取人员照片
   *
   * @param patientId 患者id
   * @return PatientImg
   */
  public PatientImgVo getFaceUrl(Integer patientId) {
    PatientImgVo patientImgVo = null;
    PatientImg patientImg = patientImgMapper.selectByPatient(patientId);
    if (patientImg != null) {
      patientImgVo = new PatientImgVo();
      BeanUtils.copyProperties(patientImg, patientImgVo);
    }
    return patientImgVo;
  }

  /**
   * 根据患者id查询患者信息
   *
   * @param id 患者id
   * @return PatientBaseInfo
   */
  public PatientBaseInfo findPatientInfoById(Integer id) {
    String patientKey = PATIENT_BASE_INFO + id;
    PatientBaseInfo patientBaseInfo = redisUtils.get(patientKey, PatientBaseInfo.class);
    if (null == patientBaseInfo) {
      patientBaseInfo = this.patientBaseInfoMapper.selectPatientById(id);
      if (patientBaseInfo != null) {
        redisUtils.set(patientKey, patientBaseInfo);
      }
    }
    return patientBaseInfo;
  }

  /**
   * 根据患者id查询患者全部信息
   *
   * @param ids 患者id
   * @return PatientTotalInfoVo
   */
  public List<PatientTotalInfoVo> findPatientTotalInfoList(List<Integer> ids) {
    if (StringHelper.isNotEmpty(ids)) {
      List<PatientTotalInfoVo> patientTotalInfoVos = mapper.selectPatientDataByIds(ids);
      if (StringHelper.isNotEmpty(patientTotalInfoVos)) {
        List<Integer> patientKinds =
            patientTotalInfoVos.stream()
                .map(PatientTotalInfoVo::getPatientKind)
                .collect(Collectors.toList());
        if (StringHelper.isNotEmpty(patientKinds)) {
          List<DictionaryItem> dictionaryItems =
              this.remoteSystemServiceFeign.findDictionaryItemByIds(patientKinds);
          patientTotalInfoVos.forEach(
              patientTotalInfoVo -> {
                Integer patientKind = patientTotalInfoVo.getPatientKind();
                if (patientKind != null) {
                  boolean b =
                      dictionaryItems.stream()
                          .anyMatch(departmentRoom -> departmentRoom.getId().equals(patientKind));
                  if (b) {
                    DictionaryItem dictionaryItem =
                        dictionaryItems.stream()
                            .filter(entity -> entity.getId().equals(patientKind))
                            .findAny()
                            .get();
                    String name = dictionaryItem.getName();
                    if (StringHelper.isNotBlank(name)) {
                      patientTotalInfoVo.setPatientKindName(name);
                    }
                  }
                }
                // 设置患者扩展信息
                this.setPatientExtInfo(patientTotalInfoVo.getId(), patientTotalInfoVo);
              });
        }
      }
      return patientTotalInfoVos;
    }
    return new ArrayList<>();
  }

  /**
   * 根据患者id查询患者全部信息
   *
   * @param id 患者id
   * @return PatientTotalInfoVo
   */
  public PatientTotalInfoVo findPatientTotalInfo(Integer id) {
    PatientTotalInfoVo patientData = mapper.selectPatientDataById(id);
    if (null != patientData) {
      if (patientData.getPatientKind() != null) {
        DictionaryItem item =
            remoteSystemServiceFeign.findDictionaryItemById(patientData.getPatientKind());
        if (item != null) {
          patientData.setPatientKindName(item.getName());
        }
      }
      // 设置患者扩展信息
      this.setPatientExtInfo(id, patientData);
    }
    return patientData;
  }

  /**
   * 设置患者扩展信息
   *
   * @param id 患者ID
   * @param patientData 患者数据详情
   */
  private void setPatientExtInfo(Integer id, PatientTotalInfoVo patientData) {
    PatientExtInfo patientExtInfo = new PatientExtInfo();
    patientExtInfo.setPatientId(id);
    List<PatientExtInfo> extInfos = patientExtInfoMapper.select(patientExtInfo);
    if (StringHelper.isNotEmpty(extInfos)) {
      StringBuilder labels = new StringBuilder(16);
      StringBuilder diseases = new StringBuilder(16);
      StringBuilder allergens = new StringBuilder(16);
      StringBuilder allergensDescriptions = new StringBuilder(16);
      for (PatientExtInfo extInfo : extInfos) {
        Byte type = extInfo.getType();
        if (StringHelper.isNotNull(extInfo.getDescription())) {
          if (type == 2) {
            allergensDescriptions.append(extInfo.getDescription());
            allergensDescriptions.append(",");
          }
        }
        if (extInfo.getDictItemId() != null) {
          DictionaryItem item =
              remoteSystemServiceFeign.findDictionaryItemById(extInfo.getDictItemId());
          switch (type) {
            case 0:
              if (null != item) {
                labels.append(item.getName());
              }
              break;
            case 1:
              if (null != item) {
                diseases.append(item.getName());
              }
              break;
            case 2:
              if (null != item) {
                allergens.append(item.getName());
                allergens.append(",");
              }
              break;
            default:
              break;
          }
        }
      }
      patientData.setLabels(labels.toString());
      patientData.setDiseases(diseases.toString());
      // 去掉最后的逗号
      if (allergens.length() > 0) {
        allergens.deleteCharAt(allergens.length() - 1);
        patientData.setAllergens(allergens.toString());
      }
      if (allergensDescriptions.length() > 0) {
        allergensDescriptions.deleteCharAt(allergensDescriptions.length() - 1);
        patientData.setAllergensDescriptions(allergensDescriptions.toString());
      }
    }
  }

  /**
   * 根据患者id查询患者来访信息
   *
   * @param id 患者id
   * @return PatientVisitInfoVo
   */
  public PatientVisitInfoVo findPatientVisitInfo(Integer id) {
    PatientVisitInfoVo patientVisitInfoVo = patientBaseInfoMapper.findPatientVisitInfo(id);
    // 查询患者是否开通会员卡
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectOneByPatientId(patientVisitInfoVo.getPatientId());
    // 获取会员卡号
    if (patientMemberInfo != null) {
      patientVisitInfoVo.setCardNumber(patientMemberInfo.getCardNumber());
      patientVisitInfoVo.setMemberTypeId(patientMemberInfo.getMemberTypeId());
      // 获取会员卡名称
      MemberType memberType =
          remoteSystemServiceFeign.findMemberTypeById(patientMemberInfo.getMemberTypeId());
      if (memberType != null && memberType.getName() != null) {
        patientVisitInfoVo.setMemberCardName(memberType.getName());
      }
    }
    if (patientVisitInfoVo.getPatientKind() != null) {
      // 查询患者类型字典名称
      DictionaryItem dictionaryItemById =
          remoteSystemServiceFeign.findDictionaryItemById(patientVisitInfoVo.getPatientKind());
      if (dictionaryItemById != null) {
        patientVisitInfoVo.setPatientKindName(dictionaryItemById.getName());
      }
    }
    // 获取标签
    patientVisitInfoVo.setLabels(getLabels(patientVisitInfoVo.getPatientId()));
    return patientVisitInfoVo;
  }

  /**
   * 根据患者id获取患者标签
   *
   * @param patientId 患者id
   * @return String
   */
  public String getLabels(Integer patientId) {
    PatientExtInfo patientExtInfo = new PatientExtInfo();
    StringBuilder labels = new StringBuilder(16);
    patientExtInfo.setPatientId(patientId);
    List<PatientExtInfo> extInfos = patientExtInfoMapper.select(patientExtInfo);
    if (StringHelper.isNotEmpty(extInfos)) {
      for (PatientExtInfo extInfo : extInfos) {
        Byte type = extInfo.getType();
        Integer dictItemId = extInfo.getDictItemId();
        if (dictItemId != null) {
          // 查询标签字典名称
          DictionaryItem item = remoteSystemServiceFeign.findDictionaryItemById(dictItemId);
          if (type == 0 && null != item) {
            labels.append(item.getName());
            labels.append("、");
          }
        }
      }
    }
    return labels.toString();
  }

  /**
   * 根据门诊id获取病历号后六位
   *
   * @param orgId 门诊id
   * @return String
   */
  public String findMedicalNumberByOrgId(Integer orgId) {
    return mapper.findMedicalNumberByOrgId(orgId);
  }

  /**
   * 根据门诊编号获取病历号后六位
   *
   * @param clinNum 门诊编号
   * @return String
   */
  public Integer findMedicalNumberByClinNum(String clinNum) {
    //    Integer.parseInt(number)
    List<Integer> existNums = mapper.findMedicalNumberByClinNum(clinNum);
    if (existNums == null || existNums.size() == 0) {
      return 1;
    }
    // 去重与排序
    existNums = existNums.stream().distinct().collect(Collectors.toList());
    Collections.sort(existNums);
    /** 二分法获取最小的可用整数 */
    // 数组最小索引值
    int low = 0;
    // 数组最大索引值
    int high = existNums.size() - 1;
    while (low <= high) {
      Integer mid = new Double(Math.floor((low + high) / 2)).intValue();
      if (existNums.get(mid).equals(mid + 1)) {
        low = mid + 1;
      } else {
        high = mid - 1;
      }
    }
    // 编号比索引大1;
    return low + 1;
  }

  /**
   * 修改头像
   *
   * @param patientPhotoForm 编辑患者头像
   */
  public void uptPhoto(PatientPhotoForm patientPhotoForm) {
    PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
    BeanUtils.copyProperties(patientPhotoForm, patientBaseInfo);
    this.patientBaseInfoMapper.updatePhoto(patientBaseInfo);
  }

  /**
   * 修改设备密码
   *
   * @param form 修改密码Form
   */
  public void updPass(UpdPassForm form) {
    NameValuePair[] data = {
      new NameValuePair("oldPass", form.getOldPass()),
      new NameValuePair("newPass", form.getNewPass())
    };
    // 调用心跳接口修改设备密码
    JSONObject jsonObject =
        WoPlatformHeartbeat.httpPostHeartbeatAccess(redisUtils.get("URL") + "/setPassWord", data);
  }

  /**
   * 根据姓名/病例编号/手机号/姓名拼音模糊查询患者
   *
   * @param form 患者模糊查询模板
   * @return List<PatientBaseInfoVo>
   */
  public List<AppPatientBaseInfoVo> appFindPatientByNameAndMobile(PatientLikeFinleQueryForm form) {
    List<AppPatientBaseInfoVo> appPatientBaseInfoVos =
        patientBaseInfoMapper.appFindPatientByNameAndMobile(form);
    if (!StringHelper.isEmpty(appPatientBaseInfoVos)) {
      Byte[] str = {0, 1};
      int age = 14;
      for (AppPatientBaseInfoVo appPatientBaseInfoVo : appPatientBaseInfoVos) {
        // 设置患者的类型(未成年男性0，成年男性1，未成年女性2，成年女性3)
        Integer integer = appPatientBaseInfoVo.getAge();
        if (integer != null) {
          // 未成年男性0
          Byte gender = appPatientBaseInfoVo.getGender();
          if (integer < age && str[0].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(0);
          }
          // 成年男性1
          if (integer >= age && str[0].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(1);
          }
          // 未成年女性2
          if (integer < age && str[1].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(2);
          }
          // 成年女性3
          if (integer >= age && str[1].equals(gender)) {
            appPatientBaseInfoVo.setPatientKind(3);
          }
        } else {
          // 年龄为空，性别为男 4
          if (str[0].equals(appPatientBaseInfoVo.getGender())) {
            appPatientBaseInfoVo.setPatientKind(4);
          } else {
            // 年龄为空，性别为女 5
            appPatientBaseInfoVo.setPatientKind(5);
          }
        }
      }
    }
    return appPatientBaseInfoVos;
  }

  /**
   * app端患者档案查询
   *
   * @param patientId 患者id
   * @return AppPatientArchivesVo
   */
  public AppPatientArchivesVo patientArchives(Integer patientId) {
    AppPatientArchivesVo appPatientArchivesVo = patientBaseInfoMapper.appPatientArchives(patientId);
    if (appPatientArchivesVo != null) {
      // 获取会员卡名称
      if (appPatientArchivesVo.getMemberTypeId() != null) {
        MemberType memberType =
            this.remoteSystemServiceFeign.findMemberTypeById(
                appPatientArchivesVo.getMemberTypeId());
        if (memberType != null && memberType.getName() != null) {
          appPatientArchivesVo.setMemberCardName(memberType.getName());
        }
        Byte[] str = {0, 1};
        int age = 14;
        // 设置患者的类型(未成年男性0，成年男性1，未成年女性2，成年女性3)
        Integer integer = appPatientArchivesVo.getAge();
        if (integer != null) {
          // 未成年男性0
          Byte gender = appPatientArchivesVo.getGender();
          if (integer < age && str[0].equals(gender)) {
            appPatientArchivesVo.setPatientKind(0);
          }
          // 成年男性1
          if (integer >= age && str[0].equals(gender)) {
            appPatientArchivesVo.setPatientKind(1);
          }
          // 未成年女性2
          if (integer < age && str[1].equals(gender)) {
            appPatientArchivesVo.setPatientKind(2);
          }
          // 成年女性3
          if (integer >= age && str[1].equals(gender)) {
            appPatientArchivesVo.setPatientKind(3);
          }
        } else {
          // 年龄为空，性别为男 4
          if (str[0].equals(appPatientArchivesVo.getGender())) {
            appPatientArchivesVo.setPatientKind(4);
          } else {
            // 年龄为空，性别为女 5
            appPatientArchivesVo.setPatientKind(5);
          }
        }
      }
    }
    return appPatientArchivesVo;
  }

  /**
   * 查询患者是否存在
   *
   * @param patientBaseInfoQueryForm 患者信息查询QueryFrom
   * @return ResponseResult
   */
  public ResponseResult appFindUserExists(PatientBaseInfoQueryForm patientBaseInfoQueryForm) {
    List<PatientBaseInfoVo> patientBaseInfoVos;
    patientBaseInfoVos = patientBaseInfoMapper.findUserExistsList(patientBaseInfoQueryForm);
    if (!StringHelper.isEmpty(patientBaseInfoVos)) {
      return ResponseUtil.fail(
          OperationCodeConstants.SAME_DATA_EXIST, "添加失败,该用户已存在", patientBaseInfoVos);
    }
    List<PatientBaseInfoVo> patientBaseInfoVoList =
        patientBaseInfoMapper.findUserExistsByMobileList(patientBaseInfoQueryForm.getMobile());
    if (!StringHelper.isEmpty(patientBaseInfoVoList)) {
      return ResponseUtil.fail(
          OperationCodeConstants.RETURN_MOBILE_ISNULL, "该手机号已存在", patientBaseInfoVoList);
    }
    return ResponseUtil.success();
  }

  /**
   * 修改患者信息
   *
   * @param patientExtendInfoModel 患者信息
   */
  public void updatePatientInfo(PatientExtendInfoModel patientExtendInfoModel) {
    PatientBaseInfoModel patientBaseInfoModel = patientExtendInfoModel.getPatientBaseInfoModel();
    if (patientBaseInfoModel != null) {
      if (patientBaseInfoModel.getOriginType() != null) {
        if (patientBaseInfoModel.getOriginType() == 1
            || patientBaseInfoModel.getOriginType() == 2) {
          patientBaseInfoModel.setOriginId(
              patientExtendInfoModel.getPatientBaseInfoModel().getSourceId());
        }
      }
      PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
      BeanUtils.copyProperties(patientBaseInfoModel, patientBaseInfo);
      // 添加患者来源推荐关系
      addPatientOrigin(patientBaseInfo);
      patientBaseInfoMapper.updateByPrimaryKeySelective(patientBaseInfo);
      redisUtils.delete(
          PATIENT_BASE_INFO + patientExtendInfoModel.getPatientBaseInfoModel().getId());
    }
  }

  /**
   * 拍照回调
   *
   * @param picturesCallbackInfoModel 拍照回调接收结果Model
   */
  public Boolean takePictures(PicturesCallbackInfoModel picturesCallbackInfoModel) {
    // 如果未获取到设备号 任务就不创建
    System.out.println(
        "******************************拍照回调成功**************************************");
    if (picturesCallbackInfoModel != null && picturesCallbackInfoModel.getBase64() != null) {
      if (informationCallbackBiz.getSN().equals(picturesCallbackInfoModel.getDeviceKey())) {
        // 创建患者照片对象
        PatientImg patientImg = new PatientImg();
        // 创建患者信息对象
        PatientBaseInfo patientBaseInfo = new PatientBaseInfo();
        patientBaseInfo.setFaceUrl(picturesCallbackInfoModel.getBase64());
        // 查询患者照片信息
        PatientImg patientImgModel =
            patientImgMapper.selectByPatient(
                Integer.parseInt(redisUtils.get("takePhotosPatientId")));

        // 如果照片不为空就判断那个照片位置是空的 让后插入照片
        if (patientImgModel != null) {
          // 添加一张照片
          if (patientImgModel.getImgOne() == null
              || StringHelper.isEmpty(patientImgModel.getImgOne())) {
            // 获取照片base64码
            patientImgModel.setImgOne(picturesCallbackInfoModel.getBase64());
            // 获取设备照片id
            patientImgModel.setFaceIdOne(picturesCallbackInfoModel.getFaceId());
            addPatientImg(patientImgModel);
            // 患者信息对象 头像更新
            updatePatientInfoImg(patientBaseInfo);
            return true;
          }
          // 添加二张照片
          if (patientImgModel.getImgTwo() == null
              || StringHelper.isEmpty(patientImgModel.getImgTwo())) {
            // 获取照片base64码
            patientImgModel.setImgTwo(picturesCallbackInfoModel.getBase64());
            // 获取设备照片id
            patientImgModel.setFaceIdTwo(picturesCallbackInfoModel.getFaceId());
            addPatientImg(patientImgModel);
            return true;
          }
          // 添加三张照片
          if (patientImgModel.getImgThree() == null
              || StringHelper.isEmpty(patientImgModel.getImgThree())) {
            // 获取照片base64码
            patientImgModel.setImgThree(picturesCallbackInfoModel.getBase64());
            // 获取设备照片id
            patientImgModel.setFaceIdThree(picturesCallbackInfoModel.getFaceId());
            addPatientImg(patientImgModel);
            return true;
          }

          // 如果没有照片对象就创建 并把第一张照片作为头像
        } else {
          patientImg.setPatientId(Integer.parseInt(redisUtils.get("takePhotosPatientId")));
          patientImg.setImgOne(picturesCallbackInfoModel.getBase64());
          patientImg.setFaceIdOne(picturesCallbackInfoModel.getFaceId());
          patientImg.setCrtId(Integer.parseInt(redisUtils.get("userId")));
          patientImg.setCrtName(redisUtils.get("userName"));
          patientImgMapper.insert(patientImg);
          // 患者信息头像同时更新
          updatePatientInfoImg(patientBaseInfo);
          return true;
        }
      }
      return false;
    }
    redisUtils.delete("userId");
    redisUtils.delete("userName");
    return false;
  }

  /**
   * 插入照片
   *
   * @param patientImgModel 患者照片对象
   */
  public void addPatientImg(PatientImg patientImgModel) {
    patientImgModel.setUpdId(Integer.parseInt(redisUtils.get("userId")));
    patientImgModel.setUpdName(redisUtils.get("userName"));
    patientImgModel.setUpdTime(new Date());
    patientImgMapper.updateByPrimaryKeySelective(patientImgModel);
  }

  /**
   * 更新患者信息头像
   *
   * @param patientBaseInfo 患者信息
   */
  public void updatePatientInfoImg(PatientBaseInfo patientBaseInfo) {
    patientBaseInfo.setId(Integer.parseInt(redisUtils.get("takePhotosPatientId")));
    patientBaseInfo.setUptId(Integer.parseInt(redisUtils.get("userId")));
    patientBaseInfo.setUpdName(redisUtils.get("userName"));
    patientBaseInfo.setUpdTime(new Date());
    patientBaseInfoMapper.updateByPrimaryKeySelective(patientBaseInfo);
    redisUtils.delete("takePhotosPatientId");
    redisUtils.delete("userId");
    redisUtils.delete("userName");
  }

  /**
   * 操作标签
   *
   * @param patientLabelRecordModelList 标签list
   */
  public void operatingLabel(List<PatientLabelRecordModel> patientLabelRecordModelList) {
    if (StringHelper.isNotEmpty(patientLabelRecordModelList)) {
      patientLabelRecordModelList.forEach(
          patientLabelRecordModel -> {
            PatientLabelRecord patientLabelRecord = new PatientLabelRecord();
            BeanUtils.copyProperties(patientLabelRecordModel, patientLabelRecord);
            patientLabelRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            patientLabelRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientLabelRecord.setCrtName(BaseContextHandler.getName());
            patientLabelRecord.setCrtTime(new Date());
            patientLabelRecordMapper.insert(patientLabelRecord);
          });
    }
  }

  /**
   * 查询标签操作记录列表
   *
   * @return List<PatientLabelRecord>
   */
  public PageInfo<PatientLabelRecordVo> labelList(PatientLabelRecordQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<PatientLabelRecordVo> patientLabelRecordList =
        patientLabelRecordMapper.selectLabelList(form.getPatientId());
    if (StringHelper.isNotEmpty(patientLabelRecordList)) {
      patientLabelRecordList.forEach(
          patientLabelRecordVo -> {
            if (StringHelper.isNotNull(patientLabelRecordVo.getDictItemId())) {
              // 查询标签字典名称
              DictionaryItem dictionaryItem =
                  remoteSystemServiceFeign.findDictionaryItemById(
                      patientLabelRecordVo.getDictItemId());
              if (dictionaryItem != null) {
                patientLabelRecordVo.setDictItemName(dictionaryItem.getName());
              }
            }
          });
      return new PageInfo<>(patientLabelRecordList);
    }
    return null;
  }

  /**
   * 根据条件查询患者全部信息
   *
   * @param queryForm
   * @return
   */
  public List<PatientTotalInfoVo> findPatientTotalInfo(PatientBaseInfoQueryForm queryForm) {
    List<PatientTotalInfoVo> patientTotalInfoVos = mapper.findPatientTotalInfo(queryForm);
    if (StringHelper.isNotEmpty(patientTotalInfoVos)) {
      patientTotalInfoVos.forEach(
          patientTotalInfoVo -> {
            Integer patientKind = patientTotalInfoVo.getPatientKind();
            if (patientKind != null) {
              DictionaryItem dictionaryItemById =
                  remoteSystemServiceFeign.findDictionaryItemById(patientKind);
              patientTotalInfoVo.setPatientKindName(dictionaryItemById.getName());
            }
            // 设置患者扩展信息
            this.setPatientExtInfo(patientTotalInfoVo.getId(), patientTotalInfoVo);
          });
    }
    return patientTotalInfoVos;
  }

  /**
   * 查询员工来源
   *
   * @return 员工二维码
   */
  public String staffQRCode(Integer id) {
    PatientOrigin patientOrigin = new PatientOrigin();
    patientOrigin.setOriginType(1);
    patientOrigin.setParentId(0);
    PatientOrigin patientOrigins = patientOriginMapper.selectOne(patientOrigin);
    if (patientOrigins != null) {
      return servePrort
          + "/#/register?"
          + "originType="
          + patientOrigins.getOriginType()
          + "&originId="
          + id;
    }
    return null;
  }

  /**
   * 条件查询自助登记患者的人数
   *
   * @param query
   * @return
   */
  public Integer countSelfRegistrationPatient(SelfRegistrationPatientQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    return mapper.countSelfRegistrationPatient(query);
  }

  /**
   * 条件查找自助登记患者列表
   *
   * @return
   */
  public PageInfo<SelfRegistrationPatientVO> findSelfRegistrationPatientList(SelfRegistrationPatientQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<SelfRegistrationPatientVO> result = mapper.selectSelfRegistrationPatientList(query);
    if (StringHelper.isNotEmpty(result)) {
      result.forEach(vo->{
        Integer originType = vo.getOriginType();
        Integer id = vo.getOriginId();
        if (!ObjectUtils.isEmpty(id)) {
          if (originType == 1) {
            SysEmployee employee = remoteSystemServiceFeign.findSysEmployeeById(id);
            if (!ObjectUtils.isEmpty(employee)) {
              vo.setOriginChannel(employee.getName());
            }
          } else if (originType == 2) {
            PatientBaseInfo patient = findPatientInfoById(id);
            if (!ObjectUtils.isEmpty(patient)) {
              vo.setOriginChannel(patient.getName());
            }
          }
        }
      });
    }
    return new PageInfo<>(result);
  }
}
