package com.yunya.modules.patient_central.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yunya.feign.patient_central.domain.form.CardRelationForm;
import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.app.MasertMemberRechargeRecordDetailVo;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.sms.model.SmsAutoEventSendRecordModel;
import com.yunya.feign.sms.model.SmsCommonSendRecordModel;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.ClinicChargeItemVO;
import com.yunya.feign.system.vo.MedicalOrganizationInfoVO;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.wechat.RemoteWechatServiceFeign;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.feign.wechat.enums.TemplateDataEnum;
import com.yunya.feign.wechat.enums.TemplateEnum;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.enums.PatientDepositAccountTypeEnum;
import com.yunya.framework.common.enums.SmsAutosendEventEnum;
import com.yunya.framework.common.enums.SmsTemplateItemEnum;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.mapper.*;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.yunya.feign.wechat.enums.TemplateEnum.MEMBER_OPEN_CARD;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.MEMBER_GENERAT_LOCK;
import static com.yunya.framework.common.constant.RedisConstants.PREPAYMENT_GENERAT_LOCK;
import static com.yunya.framework.common.enums.PatientDepositAccountTypeEnum.MEMBER;
import static com.yunya.framework.common.enums.PatientDepositAccountTypeEnum.NORMAL_PREPAYMENT;

/**
 * 简单介绍:</br> 患者会员卡信息 业务层
 *
 * @author: WY
 * @date 2020/7/30 13:23
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientMemberInfoBiz extends BaseBiz<PatientMemberInfoMapper, PatientMemberInfo> {

  private static final Logger log = org.slf4j.LoggerFactory.getLogger(PatientMemberInfoBiz.class);
  /**
   * 注入会员卡Mapper
   */
  @Autowired
  private PatientMemberInfoMapper patientMemberInfoMapper;
  /**
   * 注入会员绑定关系Mapper
   */
  @Autowired
  private PatientMemberRelationMapper patientMemberRelationMapper;
  /**
   * 注入会员变更记录Mapper
   */
  @Autowired
  private PatientMemberChangeLogMapper patientMemberChangeLogMapper;
  /**
   * 注入系统feign对象
   */
  @Autowired
  private RemoteSystemServiceFeign remoteSystemServiceFeign;
  /**
   * 注入报表feign对象
   */
  @Autowired
  private RemoteReportServiceFeign remoteReportServiceFeign;
  /** 注入就诊服务feign */
  @Autowired
  private RemoteTreatmentServiceFeign treatmentServiceFeign;
  /**
   * 注入会员充值Mapper
   */
  @Autowired
  private MemberRechargeRecordMapper memberRechargeRecordMapper;
  /**
   * 注入会员充值明细Mapper
   */
  @Autowired
  private MemberRechargeTollRecordMapper memberRechargeTollRecordMapper;
  /**
   * 注入会员退费Mapper
   */
  @Autowired
  private MemberReturnRecordMapper memberReturnRecordMapper;
  /**
   * 注入会员消费记录Mapper
   */
  @Autowired
  private MemberExpendRecordMapper memberExpendRecordMapper;
  /**
   * 注入服务
   */
  @Autowired
  private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;
  /**
   * 患者服务
   */
  @Autowired
  private PatientBaseInfoMapper patientBaseInfoMapper;
  /**
   * redis队列
   */
  @Autowired
  private RedisUtils redisUtils;
  /**
   * 微信推送
   */
  @Autowired
  private RemoteWechatServiceFeign remoteWechatServiceFeign;

  @Autowired
  private WxFansBiz wxFansBiz;
  /**
   * 预付款Mapper
   */
  @Autowired
  private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  @Autowired
  private PatientPrepaymentRelationBiz patientPrepaymentRelationBiz;
  @Autowired
  private PatientOriginBiz originBiz;
  @Autowired
  private PatientKinRelationBiz patientKinRelationBiz;

  public List<MasertMemberRechargeRecordDetailVo> findMemberRechargeRecordInfo(
      MemberExpendRecordQueryForm form) {
    return mapper.findMemberRechargeRecordInfo(form);
  }

  public List<MasertMemberRechargeRecordDetailVo> findMemberPrepaidRecordInfo(
      MemberExpendRecordQueryForm form) {
    return mapper.findMemberPrepaidRecordInfo(form);
  }

  public MasertMemberInfoVo findMasertMember(String unionId) {
    Integer id = wxFansBiz.getPatientIdByUonId(unionId);
    if (id == null) {
      return null;
    }
    PatientMemberInfoQueryForm form = new PatientMemberInfoQueryForm();
    form.setPatientId(id);
    MasertMemberInfoVo masertMemberInfoVo = patientMemberInfoMapper.selectMasertMemberInfo(form);
    if (masertMemberInfoVo != null) {
      // 获取会员卡名称
      MemberType memberType =
          this.remoteSystemServiceFeign.findMemberTypeById(
              masertMemberInfoVo.getMasterCardTypeId());
      if (memberType != null) {
        masertMemberInfoVo.setMasterMemberCardName(memberType.getName());
        masertMemberInfoVo.setRate(memberType.getRate());
        masertMemberInfoVo.setPictureCode(memberType.getPictureCode());
      }
    }
    return masertMemberInfoVo;
  }

  /**
   * 根据患者id查询会员基本信息
   *
   * @param id 患者id
   * @return MemberBaseInfoVo
   */
  public MemberBaseInfoVo findMemberBaseInfo(Integer id) {
    MemberBaseInfoVo memberBaseInfoVO1 = this.patientMemberInfoMapper.findMemberBaseInfo(id);
    if (memberBaseInfoVO1 == null) {
      // 开卡
      PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
      patientMemberInfo.setPatientId(id);
      patientMemberInfo.setOrgId(21);
      patientMemberInfo.setMemberTypeId(4);
      patientMemberInfo.setCrtId(1);
      patientMemberInfo.setCrtName("admin");
      patientMemberInfo.setInservice(false);
      generateCardNumber(patientMemberInfo);
//      this.cardLog(patientMemberInfo, "开卡", "");
      remoteRabbitMqServiceFeign.sendMessage(
          patientMemberInfo.getId(), MEMBER.getType(), 0, MsgCategoryEnum.BasePatientMember);
//      remoteWechatServiceFeign.pushTemplate(addCardPushMsg(patientMemberInfo));
    }
    MemberBaseInfoVo memberBaseInfoVO = this.patientMemberInfoMapper.findMemberBaseInfo(id);
    if (memberBaseInfoVO != null) {
      if (memberBaseInfoVO.getId() != null) {
        // 获取会员卡名称
        MemberType memberType =
            this.remoteSystemServiceFeign.findMemberTypeById(memberBaseInfoVO.getMemberTypeId());
        if (memberType != null && memberType.getName() != null) {
          memberBaseInfoVO.setMemberCardName(memberType.getName());
          memberBaseInfoVO.setMemberCardOldName(memberType.getOldName());
        }
      }
    }
    return memberBaseInfoVO;
  }

  /**
   * 查询会员卡关联关系
   *
   * @param form 患者会员卡关联关系
   * @return List<PatientMemberRelationVO>
   */
  public MemberRelationVo findMemberBindingRelation(PatientMemberRelationQueryForm form) {
    MemberRelationVo memberRelationVO = new MemberRelationVo();

    form.setBindType(0);
    memberRelationVO.setMemberRelationList(
        this.patientMemberInfoMapper.findMemberBindingRelation(form));
    form.setBindType(1);
    memberRelationVO.setMemberBalanceRelationList(
        this.patientMemberInfoMapper.findMemberBindingRelation(form));
    return memberRelationVO;
  }

  /**
   * 添加会员卡关联关系
   *
   * @param form 会员卡关联关系
   * @return ResponseResult
   */
  public ResponseResult addMemberBindingRelation(MemberBindingRelationInfoModel form) {
    Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
    Integer optId = Integer.parseInt(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    return addMemberBindingRelation4Open(form, orgId, optId, name);
  }

  /**
   * 会员关联消息 参数模板
   *
   * @param id          操作
   * @param OperateType 操作类型
   */
  public void sendMemberRelationMessages(Integer id, Integer OperateType) {
    MessageModel messageModel = new MessageModel();
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", id);
    map.put("type", 0);
    messageModel.setParamMap(map);
    messageModel.setOperateType(OperateType);
    messageModel.setMsgCategoryEnum(MsgCategoryEnum.BasePatientMemberRelation);
    remoteRabbitMqServiceFeign.sendMessage(messageModel);
  }

  /**
   * 会员操作消息 参数模板
   *
   * @param id            操作LogId
   * @param operateType   操作类型
   * @param operationType Log类型: 1.充值 2.消费 3.退款 4.撤销收费 5.账单退费 6.就诊账单返点 7.礼包账单返点 8.转账转入 9.转账转出, 20 赠金转出，21 赠金转入
   */
  public void sendMemberLogMessages(Integer id, Integer operateType, Integer operationType) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("id", id);
    paramMap.put("type", MEMBER.getType());
    paramMap.put("operationType", operationType);
    remoteRabbitMqServiceFeign.sendMessage(
        paramMap, operateType, MsgCategoryEnum.BasePatientMemberOccurLog);
  }
  public void sendMemberLogMessages2(Integer id, Integer operateType, Integer operationType, Integer id2, Integer operationType2) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("id", id);
    paramMap.put("type", MEMBER.getType());
    paramMap.put("operationType", operationType);
    paramMap.put("id2", id2);
    paramMap.put("operationType2", operationType2);
    remoteRabbitMqServiceFeign.sendMessage(
            paramMap, operateType, MsgCategoryEnum.BasePatientMemberOccurLog);
  }

  /**
   * 开卡
   *
   * @param openCardModel 开卡Model
   */
  public ResponseResult addMemberCard(OpenCardModel openCardModel) {
    PatientMemberInfo patientMember = new PatientMemberInfo();
    patientMember = patientMemberInfoMapper.selectOneByPatientId(openCardModel.getPatientId());
    if (patientMember == null) {
      PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
      patientMemberInfo.setPatientId(openCardModel.getPatientId());
      patientMemberInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      patientMemberInfo.setMemberTypeId(openCardModel.getMemberTypeId());
      patientMemberInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientMemberInfo.setCrtName(BaseContextHandler.getName());
      patientMemberInfo.setInservice(openCardModel.getMemberTypeId()!=4);// 普通会员，则标记为未激活
      generateCardNumber(patientMemberInfo);
      this.cardLog(patientMemberInfo, "开卡", "");
      remoteRabbitMqServiceFeign.sendMessage(
          patientMemberInfo.getId(), MEMBER.getType(), 0, MsgCategoryEnum.BasePatientMember);
      remoteWechatServiceFeign.pushTemplate(addCardPushMsg(patientMemberInfo));
    } else {
      // 升级开卡，普通会员转VIP
      patientMember.setMemberTypeId(openCardModel.getMemberTypeId());
      patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientMember.setUpdName(BaseContextHandler.getName());
      patientMember.setUpdTime(new Date());
      patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      patientMember.setInservice(true);
      this.mapper.updateByPrimaryKeySelective(patientMember);
      this.cardLog(patientMember, "变更", "更新");
      remoteRabbitMqServiceFeign.sendMessage(
          patientMember.getId(), MEMBER.getType(), 1, MsgCategoryEnum.BasePatientMember);
    }
    // 将不是普通会员，需
    // 删除该亲密付的绑定
    patientMemberRelationMapper.deleteOtherMemberRelation(
        openCardModel.getPatientId(), new ArrayList<>());

    return ResponseUtil.success();
  }

  /**
   * 充值开卡
   *
   * @param openCardModel 充值开卡Model
   */
  public ResponseResult addMemberCard2(OpenCardModel2 openCardModel) {
    if (openCardModel.getCardNumber().isEmpty()) {
      // 开卡
      PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
      patientMemberInfo.setPatientId(openCardModel.getPatientId());
      patientMemberInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      patientMemberInfo.setMemberTypeId(openCardModel.getMemberTypeId());
      patientMemberInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientMemberInfo.setCrtName(BaseContextHandler.getName());
      patientMemberInfo.setInservice(true);
      generateCardNumber(patientMemberInfo);
      this.cardLog(patientMemberInfo, "开卡", "");
      remoteRabbitMqServiceFeign.sendMessage(
          patientMemberInfo.getId(), MEMBER.getType(), 0, MsgCategoryEnum.BasePatientMember);
      remoteWechatServiceFeign.pushTemplate(addCardPushMsg(patientMemberInfo));
      // 开卡后赋值卡号
      openCardModel.setCardNumber(patientMemberInfo.getCardNumber());
    } else {
      // 升级开卡，普通会员转VIP
      PatientMemberInfo patientMember =
          this.patientMemberInfoMapper.selectOneByCardNumber(openCardModel.getCardNumber());
      patientMember.setMemberTypeId(openCardModel.getMemberTypeId());
      patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientMember.setUpdName(BaseContextHandler.getName());
      patientMember.setUpdTime(new Date());
      patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      patientMember.setInservice(true);
      this.mapper.updateByPrimaryKeySelective(patientMember);
      this.cardLog(patientMember, "变更", "更新");
      remoteRabbitMqServiceFeign.sendMessage(
          patientMember.getId(), MEMBER.getType(), 1, MsgCategoryEnum.BasePatientMember);
    }
    // 将不是普通会员，需
    // 删除该亲密付的绑定
    patientMemberRelationMapper.deleteOtherMemberRelation(
        openCardModel.getPatientId(), new ArrayList<>());

    // 充值
    if (openCardModel
        .getRechargePrincipal()
        .compareTo(openCardModel.getAccountedWayModel().getCreditAmount())
        == 0) {
      // 查询会员余额 余额增加
      PatientMemberInfo patientMemberInfo =
          patientMemberInfoMapper.selectCardNumber(openCardModel.getCardNumber());
      if (patientMemberInfo != null) {
        patientMemberInfo.setPrincipalAmount(
            patientMemberInfo.getPrincipalAmount().add(openCardModel.getRechargePrincipal()));
        BigDecimal rechargeBonus = openCardModel.getRechargeBonus();
        if (null != rechargeBonus) {
          patientMemberInfo.setBonusAmount(patientMemberInfo.getBonusAmount().add(rechargeBonus));
        }
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        // 添加会员卡充值记
        MemberRechargeRecord memberRechargeRecord = new MemberRechargeRecord();
        BeanUtils.copyProperties(openCardModel, memberRechargeRecord);
        memberRechargeRecord.setMemberId(openCardModel.getCardNumber());
        memberRechargeRecord.setCurrentRechargePrincipal(patientMemberInfo.getPrincipalAmount());
        memberRechargeRecord.setCurrentRechargeBonus(patientMemberInfo.getBonusAmount());
        memberRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setCrtName(BaseContextHandler.getName());
        memberRechargeRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setUpdName(BaseContextHandler.getName());
        memberRechargeRecord.setRemarks(openCardModel.getAccountedWayModel().getRemarks());
        memberRechargeRecord.setType(0);
        memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
        // 发送会员充值消息
        sendMemberLogMessages(memberRechargeRecord.getId(), 0, 1);
        // 添加会员卡充值收费记录
        AccountedWayModel accountedWayModel = openCardModel.getAccountedWayModel();
        if (StringHelper.isNotNull(accountedWayModel)) {
          MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
          BeanUtils.copyProperties(accountedWayModel, memberRechargeTollRecord);
          memberRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
          memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord.getId());
          memberRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
          memberRechargeTollRecord.setCrtName(BaseContextHandler.getName());
          memberRechargeTollRecordMapper.insertSelective(memberRechargeTollRecord);
        }
        // TODO: 充值后判断是否升级会员等级，退费后判断是否降级
        makeMemberLevelByRecharge(openCardModel.getCardNumber());

        // 会员卡充值发送短信 type:0充值 1消费
        memberSendMessages(memberRechargeRecord, 0);
        // 会员卡充值成功发送推送
        WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
        wxTemplateMsgModel.setPatientId(openCardModel.getPatientId());
        wxTemplateMsgModel.setTemplateEnum(TemplateEnum.RECHARGE_SUCCESS);
        PatientBaseInfoVo patientBaseInfoVo =
            patientBaseInfoMapper.selectOneById(openCardModel.getPatientId());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TemplateDataEnum.PATIENT_NAME.getArgName(), patientBaseInfoVo.getName());
        paramMap.put("keyword1", openCardModel.getRechargePrincipal());
        paramMap.put("keyword2", patientMemberInfo.getPrincipalAmount());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        paramMap.put("keyword3", sdf.format(new Date()));
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setId(Integer.valueOf(BaseContextHandler.getOrgId()));

        List<OrganizationInfoDetail> orgList =
            remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        log.info("门诊信息：" + orgList.get(0).toString());
        if (orgList != null && orgList.size() > 0) {
          paramMap.put("keyword4", orgList.get(0).getName());
        } else {
          throw new ClientServiceException("门诊信息为空", RETURN_VALUE_ISNULL);
        }
        paramMap.put("linkMobile", orgList.get(0).getTel());
        wxTemplateMsgModel.setParamMap(paramMap);
        remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);
      }
    } else {
      return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL, "充值金额与入账金额不相等!", null);
    }

    return ResponseUtil.success();
  }

  /**
   * 根据充值记录，判断会员等级
   *
   * @param cardNumber 会员卡号
   */
  public void makeMemberLevelByRecharge(String cardNumber) {
    // 判断该卡是否可以自动升级
    PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectCardNumber(cardNumber);
    //  || patientMemberInfo.getNonauto()
    if (patientMemberInfo == null) {
      return;
    }
    List<MemberType> memberTypeList = remoteSystemServiceFeign.findMemberTypeList(new MemberType());
    // 充值等级
    Integer rechargeTypeId = calcRechargelevel(memberTypeList, cardNumber);
    // 消费等级
    Integer cashTypeId = calcTotalCashlevel(memberTypeList, patientMemberInfo.getPatientId());
    // 变更等级
    PatientMemberInfo patientMember =
            this.patientMemberInfoMapper.selectOneByCardNumber(cardNumber);
    Integer newTypeId = calcMaxMemberLevel(memberTypeList, rechargeTypeId, cashTypeId, patientMember.getMinTypeId());
    if (!newTypeId.equals(patientMember.getMemberTypeId())){
      patientMember.setMemberTypeId(newTypeId);
      patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientMember.setUpdName(BaseContextHandler.getName());
      patientMember.setUpdTime(new Date());
      patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      this.mapper.updateByPrimaryKeySelective(patientMember);
      this.cardLog(patientMember, "变更", "更新");
      remoteRabbitMqServiceFeign.sendMessage(
              patientMember.getId(), MEMBER.getType(), 1, MsgCategoryEnum.BasePatientMember);
    }
  }

  /**
   * 根据累计现金消费，判断会员等级
   *
   * @param patientId 患者ID
   */
  public boolean makeMemberLevelByCashAmount(Integer patientId) {
    log.info("患者会员等级变更开始");
    // 判断该卡是否可以自动升级
    PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOneByPatientId(patientId);
    //  || patientMemberInfo.getNonauto()
    if (patientMemberInfo == null || patientMemberInfo.getMemberTypeId() == 4 || !patientMemberInfo.getInservice()) {
      return false;
    }
    List<MemberType> memberTypeList = remoteSystemServiceFeign.findMemberTypeList(new MemberType());
    // 充值等级
    Integer rechargeTypeId = calcRechargelevel(memberTypeList, patientMemberInfo.getCardNumber());
    // 消费等级
    Integer cashTypeId = calcTotalCashlevel(memberTypeList, patientId);
    // 变更等级
    Integer newTypeId = calcMaxMemberLevel(memberTypeList, rechargeTypeId, cashTypeId, patientMemberInfo.getMinTypeId());
    log.info("患者会员等级memberTypeId：{}, 充值等级rechargeTypeId：{}, 消费等级cashTypeId: {}, 变更等级newTypeId: {}",
            patientMemberInfo.getMemberTypeId(), rechargeTypeId, cashTypeId, newTypeId);
    if (!newTypeId.equals(patientMemberInfo.getMemberTypeId())) {
      // 变更等级
      PatientMemberInfo patientMember =
          this.patientMemberInfoMapper.selectOneByPatientId(patientId);
      patientMember.setMemberTypeId(newTypeId);
      patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientMember.setUpdName(BaseContextHandler.getName());
      patientMember.setUpdTime(new Date());
      patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      this.mapper.updateByPrimaryKeySelective(patientMember);
      log.info("会员等级变更入库：{}", JSONObject.toJSONString(patientMember));
      this.cardLog(patientMember, "变更", "更新");
      remoteRabbitMqServiceFeign.sendMessage(
          patientMember.getId(), MEMBER.getType(), 1, MsgCategoryEnum.BasePatientMember);
      // 将不是普通会员，需
      // 删除该亲密付的绑定
      patientMemberRelationMapper.deleteOtherMemberRelation(patientId, new ArrayList<>());
      return true;
    }
    return false;
  }

  public Integer calcMaxMemberLevel(List<MemberType> memberTypeList_tmp, Integer rechargeTypeId, Integer cashTypeId, Integer minTypeId) {
    Integer maxTypeId = 4;
    if (minTypeId.compareTo(0) > 0) {
      maxTypeId = minTypeId;
    }
    BigDecimal a, b, c;
    a = b = c = BigDecimal.ZERO;
    for(int i=0;i<memberTypeList_tmp.size();i++) {
      if (memberTypeList_tmp.get(i).getId().equals(maxTypeId)) {
        a = memberTypeList_tmp.get(i).getRechargeMaxAmount();
      }
      if (memberTypeList_tmp.get(i).getId().equals(rechargeTypeId)) {
        b = memberTypeList_tmp.get(i).getRechargeMaxAmount();
      }
      if (memberTypeList_tmp.get(i).getId().equals(cashTypeId)) {
        c = memberTypeList_tmp.get(i).getRechargeMaxAmount();
      }
    }
    if (b.compareTo(a)>0) {
      maxTypeId = rechargeTypeId;
      a = b;
    }
    if (c.compareTo(a) > 0) {
      maxTypeId = cashTypeId;
    }
    return maxTypeId;
  }

  public Integer calcRechargelevel(List<MemberType> memberTypeList_tmp, String cardNumber) {
    // TODO: 充值后判断是否升级会员等级，退费后判断是否降级
    BigDecimal sum = memberRechargeRecordMapper.findRechargeTotalAmountByCardNumber(cardNumber);
    MemberType tmp = new MemberType();
    List<MemberType> memberTypeList = memberTypeList_tmp.stream()
            .filter(memberType -> {
              return memberType.getRechargeMaxAmount().compareTo(BigDecimal.ZERO) > 0 &&
                      memberType.getRechargeMaxAmount().compareTo(sum) <= 0;
            })
            .sorted(Comparator.comparing(MemberType::getRechargeMaxAmount).reversed())
            .collect(Collectors.toList());
    Integer newTypeId = 4;
    if (memberTypeList != null && memberTypeList.size() > 0) {
      newTypeId = memberTypeList.get(0).getId();
    }
    return newTypeId;
  }

  public Integer calcTotalCashlevel(List<MemberType> memberTypeList_tmp, Integer patientId) {
    // TODO: 消费后判断是否升级会员等级，退费后判断是否降级
    Integer newTypeId = 4;
    BigDecimal sum = treatmentServiceFeign.getCashInfo(patientId).getCumulativeConsumption();
    log.info("患者个人现金消费总额：{}", sum);
    if (sum == null) {
      return newTypeId;
    }
    MemberType tmp = new MemberType();
    List<MemberType> memberTypeList = memberTypeList_tmp.stream()
            .filter(memberType -> {
              return memberType.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 && memberType.getTotalAmount().compareTo(sum) <= 0;
            })
            .sorted(Comparator.comparing(MemberType::getTotalAmount).reversed())
            .collect(Collectors.toList());
    if (memberTypeList != null && memberTypeList.size() > 0) {
      newTypeId = memberTypeList.get(0).getId();
    }
    return newTypeId;
  }

  /**
   * 退卡
   *
   * @param openCardModel 原藤卡激活（存在连带转换亲密付）Model
   */
  public ResponseResult disableMemberCard(OpenCardModel openCardModel) {
    // 反激活卡主身份
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectOneByPatientId(openCardModel.getPatientId());
    if (patientMemberInfo == null) {
      throw new ClientServiceException("未找到会员卡信息", DATA_NOT_EXIST);
    }
    patientMemberInfo.setInservice(false);
    patientMemberInfo.setMemberTypeId(4);
    patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
    //
    // 删除该患者的亲密付绑定
    patientMemberRelationMapper.deleteOtherMemberRelation(
        openCardModel.getPatientId(), new ArrayList<>());

    this.cardLog(patientMemberInfo, "退卡", "退卡");
    remoteRabbitMqServiceFeign.sendMessage(
            patientMemberInfo.getId(), MEMBER.getType(), 1, MsgCategoryEnum.BasePatientMember);

    return ResponseUtil.success();
  }

  public ResponseResult addMemberCard3(OpenCardModel3 openCardModel) {
    // 激活卡主身份
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectOneByPatientId(openCardModel.getPatientId());
    if (patientMemberInfo == null) {
      throw new ClientServiceException("未找到会员卡信息", DATA_NOT_EXIST);
    }
    patientMemberInfo.setInservice(true);
    patientMemberInfo.setMinTypeId(patientMemberInfo.getMemberTypeId());
    patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);

    // 无次一级会员时，默认普通会员
    Integer nextLevelId = 4;
    // 查询卡主会员的次一级会员
    MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(patientMemberInfo.getMemberTypeId());
    if (memberType != null && memberType.getNextLevelId() != null) {
      nextLevelId = memberType.getNextLevelId();
    }

    // TODO：不转化亲密付的用户，无藤卡转次一级新卡
    List<Integer> patientMemberRelations =
        patientMemberRelationMapper.FindMemberBindingRelation2(
            openCardModel.getPatientId(), openCardModel.getTransformPatientIds());
    Integer finalNextLevelId = nextLevelId;
    patientMemberRelations.forEach(
        r -> {
          PatientMemberInfo patientMemberInfo1 =
              patientMemberInfoMapper.selectOneByPatientId(r);
          // 执行无卡规则：开次一级卡，或升级到次一级卡
          if (patientMemberInfo1 == null) {
            // 开卡
            PatientMemberInfo patientMemberInfo2 = new PatientMemberInfo();
            patientMemberInfo2.setPatientId(r);
            patientMemberInfo2.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            patientMemberInfo2.setMemberTypeId(finalNextLevelId);
            patientMemberInfo2.setMinTypeId(finalNextLevelId);
            patientMemberInfo2.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMemberInfo2.setCrtName(BaseContextHandler.getName());
            generateCardNumber(patientMemberInfo2);
            this.cardLog(patientMemberInfo2, "开卡", "");
            remoteRabbitMqServiceFeign.sendMessage(
                patientMemberInfo2.getId(), MEMBER.getType(), 0, MsgCategoryEnum.BasePatientMember);
            remoteWechatServiceFeign.pushTemplate(addCardPushMsg(patientMemberInfo2));
          } else if (patientMemberInfo1.getMemberTypeId() == 4 && finalNextLevelId != 4) {
            // 升级到次一级卡，普通会员转VIP
            PatientMemberInfo patientMember =
                this.patientMemberInfoMapper.selectOneByCardNumber(patientMemberInfo1.getCardNumber());
            patientMember.setMemberTypeId(finalNextLevelId);
            patientMember.setMinTypeId(finalNextLevelId);
            patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMember.setUpdName(BaseContextHandler.getName());
            patientMember.setUpdTime(new Date());
            patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            patientMember.setInservice(true);
            this.mapper.updateByPrimaryKeySelective(patientMember);
            this.cardLog(patientMember, "变更", "更新");
            remoteRabbitMqServiceFeign.sendMessage(
                patientMember.getId(), MEMBER.getType(), 1, MsgCategoryEnum.BasePatientMember);
          }
        });

    // 删除不转化亲密付的用户，已改为删除所有关系
    patientMemberRelationMapper.deleteOtherMemberRelation(
        openCardModel.getPatientId(), openCardModel.getTransformPatientIds());

    // 转化亲密付用户列表
    openCardModel
        .getTransformPatientIds()
        .forEach(
            p -> {
              try {
                MemberBindingRelationInfoModel form = new MemberBindingRelationInfoModel();
                form.setMasterCardId(openCardModel.getPatientId());
                form.setPatientId(openCardModel.getPatientId());
                form.setSecondaryCardId(p);
                form.setBindType((byte) 1); // 原 共享会员卡余额，现 会员卡亲密付
                this.addMemberBindingRelation(form);
              } catch (Exception e) {
                // 已绑过的直接抛出异常
                log.info(e.toString());
              }
            });
    return ResponseUtil.success();
  }

  private WxTemplateMsgModel addCardPushMsg(PatientMemberInfo memberInfo) {
    MemberType memberType =
        remoteSystemServiceFeign.findMemberTypeById(memberInfo.getMemberTypeId());
    WxTemplateMsgModel model = new WxTemplateMsgModel();
    Map<String, Object> paramMap = Maps.newHashMap();
    paramMap.put("keyword1", memberInfo.getCardNumber());
    paramMap.put("keyword2", memberType.getName());
    paramMap.put("keyword3", LocalDateTime.now().toString("yyyy年MM月dd日 HH:mm:ss"));
    model.setPatientId(memberInfo.getPatientId());
    model.setTemplateEnum(MEMBER_OPEN_CARD);
    model.setParamMap(paramMap);
    return model;
  }

  /**
   * 生成会员卡号
   *
   * @return String 卡号
   */
  public void generateCardNumber(PatientMemberInfo patientMemberInfo) {
    log.info("==>开始生成预付款账号...");
    Integer orgId = patientMemberInfo.getOrgId();
    if (StringHelper.isNotNull(orgId)) {
      redisUtils.lockedFunc(
          MEMBER_GENERAT_LOCK,
          o -> {
            String number = mapper.generateCardNumber(orgId);
            String suffix = String.format("%06d", Integer.parseInt(number) + 1);
            // 获取门诊简称
            OrganizationInfo org = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
            String cnum = "000";  // 无门诊时，默认值
            if (StringHelper.isNotNull(org) && StringHelper.isNotNull(org.getClinicNumber())) {
              cnum = org.getClinicNumber();
            }
            // 生成规则：Y + 门诊编号 + 6位递增值（数据库）
            patientMemberInfo.setCardNumber(MEMBER.getPrefix() + cnum + suffix);
            mapper.insertSelective(patientMemberInfo);
            log.info("==========预付款账号生成结束===========");
            return null;
          });
    } else {
      throw new ClientServiceException("【开卡失败，门诊不存在，请重写登录后重试】", OperationCodeConstants.DATA_EXIST);
    }
  }

  /**
   * 添加患者时,创建预付款账户
   *
   * @param patientBaseInfo 患者信息
   */
  public void addPatientPrepaymentsInfo(PatientBaseInfo patientBaseInfo) {
    Integer patientId = patientBaseInfo.getId();
    if (StringHelper.isNotNull(patientId)) {
      Integer type = NORMAL_PREPAYMENT.getType();
      PatientPrepaymentsInfo patientPrepaymentsInfo = new PatientPrepaymentsInfo();
      patientPrepaymentsInfo.setOrgId(patientBaseInfo.getOrgId());
      patientPrepaymentsInfo.setPatientId(patientId);
      patientPrepaymentsInfo.setCrtId(patientBaseInfo.getCrtId());
      patientPrepaymentsInfo.setCrtName(patientBaseInfo.getCrtName());
      patientPrepaymentsInfo.setType(type);
      generateCardNumber(patientPrepaymentsInfo);
      remoteRabbitMqServiceFeign.sendMessage(
          patientPrepaymentsInfo.getId(), type, 0, MsgCategoryEnum.BasePatientMember);
    }
  }

  /**
   * 查找预付款账号，如果还未开通则先开通账号
   *
   * @param model
   * @return
   */
  public PatientPrepaymentsInfo openIfAbsent(PrepaidRechargeModel model) {
    Integer patientId = model.getPatientId();
    Integer type = model.getPrepaymentType();
    if (!PatientDepositAccountTypeEnum.isPrepaymentType(type)) {
      throw new ClientServiceException("无效的预付款账号类型", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    PatientPrepaymentsInfo info =
        patientPrepaymentsInfoMapper.selectOneByPatientId(patientId, type);
    if (StringHelper.isNull(info)) {
      Date now = DateUtil.now();
      int optId = Integer.parseInt(BaseContextHandler.getUserID());
      String optName = BaseContextHandler.getName();
      info = new PatientPrepaymentsInfo();
      info.setPrepaymentPrincipal(BigDecimal.ZERO);
      info.setPrepaymentBonus(BigDecimal.ZERO);
      info.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      info.setCrtId(optId);
      info.setCrtName(optName);
      info.setCrtTime(now);
      info.setUptId(optId);
      info.setUpdName(optName);
      info.setUpdTime(now);
      info.setPatientId(patientId);
      info.setType(type);
      generateCardNumber(info);
      remoteRabbitMqServiceFeign.sendMessage(
          info.getId(), type, 0, MsgCategoryEnum.BasePatientMember);
    } else {
      if (!info.getPrepaymentNumber().equals(model.getPrepaidCard())) {
        throw new ClientServiceException("无效的预付款账号", OperationCodeConstants.DATA_ERROR);
      }
    }
    return info;
  }

  /**
   * 生成预付款卡号
   *
   * @return String 卡号
   */
  public void generateCardNumber(PatientPrepaymentsInfo prepaymentsInfo) {
    log.info("==>开始生成预付款账号...");
    Integer orgId = prepaymentsInfo.getOrgId();
    if (StringHelper.isNotNull(orgId)) {
      Integer type = prepaymentsInfo.getType();
      String prefix = PatientDepositAccountTypeEnum.getTypeEnum(type).getPrefix();
      redisUtils.lockedFunc(
          PREPAYMENT_GENERAT_LOCK + type,
          o -> {
            String number = patientPrepaymentsInfoMapper.generateCardNumber4Prepay(prefix, orgId);
            String suffix = String.format("%06d", Integer.parseInt(number) + 1);
            // 获取门诊简称
            OrganizationInfo org = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
            if (StringHelper.isNotNull(org)) {
              // 生成规则：预付款类型前缀 + 门诊编号 + 6位递增值（数据库）
              prepaymentsInfo.setPrepaymentNumber(prefix + org.getClinicNumber() + suffix);
              patientPrepaymentsInfoMapper.insertSelective(prepaymentsInfo);
              log.info("==========预付款账号生成结束===========");
            }
            return null;
          });
    } else {
      throw new ClientServiceException(
          "【开通失败，门诊不存在，请重写登录后重试】", OperationCodeConstants.DATA_NOT_EXIST);
    }
  }

  /**
   * 根据关联类型删除关系
   *
   * @param cardRelationForm 会员卡关系删除Form
   */
  public void deleteRelationById(CardRelationForm cardRelationForm) {

    PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
    patientMemberRelation.setId(cardRelationForm.getId());
    PatientMemberRelation memberRelation =
        this.patientMemberRelationMapper.selectOne(patientMemberRelation);
    // 权限绑定 单项删除
    if (cardRelationForm.getBindType() == 0) {
      this.patientMemberRelationMapper.deleteByPrimaryKey(cardRelationForm.getId());
      remoteRabbitMqServiceFeign.sendMessage(
          cardRelationForm.getId(), 0, 2, MsgCategoryEnum.BasePatientMemberRelation);
    }
    // 共享值绑定 双项删除
    if (cardRelationForm.getBindType() == 1) {

      if (memberRelation != null) {
        Integer relationId =
            patientMemberRelationMapper.selectMemberRelationId(
                memberRelation.getSecondaryCardId(), memberRelation.getMasterCardId(), 1);
        if (relationId != null) {
          this.patientMemberRelationMapper.deleteMemberRelation(
              memberRelation.getSecondaryCardId(), memberRelation.getMasterCardId(), 1);
          // 发送会员关联删除消息
          remoteRabbitMqServiceFeign.sendMessage(
              relationId, 0, 2, MsgCategoryEnum.BasePatientMemberRelation);
        }
        this.patientMemberRelationMapper.delete(memberRelation);
        // 发送会员关联删除消息
        sendMemberRelationMessages(memberRelation.getId(), 2);
      }
    }

    // 发送微信推送消息
    WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
    wxTemplateMsgModel.setPatientId(memberRelation.getMasterCardId());
    wxTemplateMsgModel.setTemplateEnum(TemplateEnum.UNBIND_SUCCESS);

    PatientBaseInfoVo patientBaseInfoVo =
        patientBaseInfoMapper.selectOneById(memberRelation.getSecondaryCardId());
    Map<String, Object> paramMap = new HashMap<>();
    if (cardRelationForm.getBindType() == 0) {
      paramMap.put("first", "您好，您的会员卡副卡人已解绑，信息如下");
    } else {
      paramMap.put("first", "您好，您的会员卡余额共享人已解绑，信息如下");
    }

    if (patientBaseInfoVo != null) {
      paramMap.put("keyword1", patientBaseInfoVo.getName());
      paramMap.put("keyword2", patientBaseInfoVo.getMobile());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
      paramMap.put("keyword3", sdf.format(new Date()));
    } else {
      throw new ClientServiceException("无此患者信息", DATA_NOT_EXIST);
    }
    wxTemplateMsgModel.setParamMap(paramMap);
    remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);
  }

  /**
   * 开卡日志
   *
   * @param patientMemberInfo 患者会员信息
   * @param operationType     标识：开卡 or 变更
   * @param isUpd             是否修改
   */
  public void cardLog(PatientMemberInfo patientMemberInfo, String operationType, String isUpd) {
    // 会员卡记录日志
    PatientMemberChangeLog patientMemberChangeLog = new PatientMemberChangeLog();
    patientMemberChangeLog.setCardNumber(patientMemberInfo.getCardNumber());
    MemberType memberType =
        this.remoteSystemServiceFeign.findMemberTypeById(patientMemberInfo.getMemberTypeId());
    if (memberType != null) {
      patientMemberChangeLog.setMemberCardName(memberType.getName());
    }
    int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
    patientMemberChangeLog.setPatientId(patientMemberInfo.getPatientId());
    patientMemberChangeLog.setMemberTypeId(patientMemberInfo.getMemberTypeId());
    patientMemberChangeLog.setOrgId(orgId);
    // 获取门诊简称
    OrganizationInfo organizationInfo =
        this.remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
    if (organizationInfo != null) {
      patientMemberChangeLog.setOrgName(organizationInfo.getAbbreviation());
    }

    patientMemberChangeLog.setOperatorId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientMemberChangeLog.setOperatorName(BaseContextHandler.getName());
    patientMemberChangeLog.setOperatingTime(new Date());
    patientMemberChangeLog.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientMemberChangeLog.setCrtName(BaseContextHandler.getName());
    patientMemberChangeLog.setOperationType(operationType);
    // 不为空就是修改
    if (isUpd != null) {
      patientMemberChangeLog.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      patientMemberChangeLog.setUpdName(BaseContextHandler.getName());
      patientMemberChangeLog.setUpdTime(new Date());
    }

    this.patientMemberChangeLogMapper.insertSelective(patientMemberChangeLog);
  }

  /**
   * 修改会员卡类型
   *
   * @param form 会员卡类型修改Form
   */
  public void changeType(CardTypeForm form) {
    // 预检查是否有会员卡
    this.findMemberBaseInfo(form.getPatientId());

    PatientMemberInfo patientMember =
        this.patientMemberInfoMapper.selectOneByCardNumber(form.getCardNumber());
    // 判断是否提升会员等级
    List<MemberType> memberTypeList = remoteSystemServiceFeign.findMemberTypeList(new MemberType());
    List<MemberType> tmp1 = memberTypeList.stream()
        .filter(m->m.getId().equals(patientMember.getMemberTypeId()))
        .collect(Collectors.toList());
    List<MemberType> tmp2 = memberTypeList.stream()
        .filter(m->m.getId().equals(form.getMemberTypeId()))
        .collect(Collectors.toList());
    if (tmp1.size() == 0 || tmp2.size() == 0) {
      throw new ClientServiceException("会员等级查询异常", DATA_ERROR);
    }
    MemberType patientMemberType = tmp1.get(0);
    MemberType formMemberType = tmp2.get(0);
    if (patientMemberType.getTotalAmount().compareTo(formMemberType.getTotalAmount())>=0) {
      throw new ClientServiceException("会员等级仅可赋予高于当前会员等级身份", DATA_ERROR);
    }

    patientMember.setMemberTypeId(form.getMemberTypeId());
    patientMember.setMinTypeId(form.getMemberTypeId());
    patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientMember.setUpdName(BaseContextHandler.getName());
    patientMember.setUpdTime(new Date());
    patientMember.setInservice(true);
    patientMember.setNonauto(true);
//    patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    this.mapper.updateByPrimaryKeySelective(patientMember);


    // 将不是普通会员，需
    // 删除该亲密付的绑定
    patientMemberRelationMapper.deleteOtherMemberRelation(
            form.getPatientId(), new ArrayList<>());


    this.cardLog(patientMember, "升级", "更新");
    remoteRabbitMqServiceFeign.sendMessage(
        patientMember.getId(), MEMBER.getType(), 1, MsgCategoryEnum.BasePatientMember);
  }

  /**
   * 变更记录
   *
   * @param cardNumber 会员卡号
   * @return List<PatientMemberChangeLogVo>
   */
  public List<PatientMemberChangeLogVo> changeLog(String cardNumber) {
    return this.patientMemberChangeLogMapper.changeLog(cardNumber);
  }

  /**
   * 充值
   *
   * @param model 会员卡充值Model
   */
  public ResponseResult recharge(MemberRechargeModel model) {
    if (model.getRechargePrincipal().compareTo(model.getAccountedWayModel().getCreditAmount())
        == 0) {
      // 查询会员余额 余额增加
      PatientMemberInfo patientMemberInfo =
          patientMemberInfoMapper.selectCardNumber(model.getMemberId());
      if (patientMemberInfo != null) {
        patientMemberInfo.setPrincipalAmount(
            patientMemberInfo.getPrincipalAmount().add(model.getRechargePrincipal()));
        BigDecimal rechargeBonus = model.getRechargeBonus();
        if (null != rechargeBonus) {
          patientMemberInfo.setBonusAmount(patientMemberInfo.getBonusAmount().add(rechargeBonus));
        }
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        // 添加会员卡充值记
        MemberRechargeRecord memberRechargeRecord = new MemberRechargeRecord();
        BeanUtils.copyProperties(model, memberRechargeRecord);
        memberRechargeRecord.setCurrentRechargePrincipal(patientMemberInfo.getPrincipalAmount());
        memberRechargeRecord.setCurrentRechargeBonus(patientMemberInfo.getBonusAmount());
        memberRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setCrtName(BaseContextHandler.getName());
        memberRechargeRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setUpdName(BaseContextHandler.getName());
        memberRechargeRecord.setRemarks(model.getAccountedWayModel().getRemarks());
        memberRechargeRecord.setType(0);
        memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
        // 发送会员充值消息
        sendMemberLogMessages(memberRechargeRecord.getId(), 0, 1);
        // 添加会员卡充值收费记录
        AccountedWayModel accountedWayModel = model.getAccountedWayModel();
        if (StringHelper.isNotNull(accountedWayModel)) {
          MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
          BeanUtils.copyProperties(accountedWayModel, memberRechargeTollRecord);
          memberRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
          memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord.getId());
          memberRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
          memberRechargeTollRecord.setCrtName(BaseContextHandler.getName());
          memberRechargeTollRecordMapper.insertSelective(memberRechargeTollRecord);
        }
        // TODO: 充值后判断是否升级会员等级，退费后判断是否降级
        makeMemberLevelByRecharge(model.getMemberId());

        // 会员卡充值发送短信 type:0充值 1消费
        memberSendMessages(memberRechargeRecord, 0);
        // 会员卡充值成功发送推送
        WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
        wxTemplateMsgModel.setPatientId(model.getPatientId());
        wxTemplateMsgModel.setTemplateEnum(TemplateEnum.RECHARGE_SUCCESS);
        PatientBaseInfoVo patientBaseInfoVo =
            patientBaseInfoMapper.selectOneById(model.getPatientId());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TemplateDataEnum.PATIENT_NAME.getArgName(), patientBaseInfoVo.getName());
        paramMap.put("keyword1", model.getRechargePrincipal());
        paramMap.put("keyword2", patientMemberInfo.getPrincipalAmount());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        paramMap.put("keyword3", sdf.format(new Date()));
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setId(Integer.valueOf(BaseContextHandler.getOrgId()));

        List<OrganizationInfoDetail> orgList =
            remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        log.info("门诊信息：" + orgList.get(0).toString());
        if (orgList != null && orgList.size() > 0) {
          paramMap.put("keyword4", orgList.get(0).getName());
        } else {
          throw new ClientServiceException("门诊信息为空", RETURN_VALUE_ISNULL);
        }
        paramMap.put("linkMobile", orgList.get(0).getTel());
        wxTemplateMsgModel.setParamMap(paramMap);
        remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);
      }
    } else {
      return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL, "充值金额与入账金额不相等!", null);
    }
    return ResponseUtil.success();
  }


  /**
   * 充值(赠金转账)
   *
   * @param model 会员卡充值Model
   */
  public ResponseResult recharge2(MemberRechargeModel model) {
    if (model.getRechargePrincipal().compareTo(model.getAccountedWayModel().getCreditAmount())
        == 0) {
      // 赠金转入
      PatientMemberInfo patientMemberInfo =
          patientMemberInfoMapper.selectCardNumber(model.getMemberId());//memberId转入的会员卡
      // 赠金转出
      PatientMemberInfo patientMemberInfo2 =
          patientMemberInfoMapper.selectOneByPatientId(model.getPatientId());//patientId转出的患者
      Integer patientMemberInfo_RechargeId = 0;
      Integer patientMemberInfo2_RechargeId = 0;
      // 赠金转出
      if (patientMemberInfo2 != null) {
        BigDecimal rechargeBonus = model.getRechargeBonus();
        if (null != rechargeBonus && rechargeBonus.compareTo(BigDecimal.ZERO) > 0) {
          if (patientMemberInfo2.getBonusAmount().subtract(rechargeBonus).compareTo(BigDecimal.ZERO) < 0) {
            return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "赠金异常!", null);
          }
          patientMemberInfo2.setBonusAmount(patientMemberInfo2.getBonusAmount().subtract(rechargeBonus));
        } else {
          return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "赠金异常!", null);
        }
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo2);
        // 添加会员卡充值记
        MemberRechargeRecord memberRechargeRecord2 = new MemberRechargeRecord();
        BeanUtils.copyProperties(model, memberRechargeRecord2);
        memberRechargeRecord2.setCurrentRechargePrincipal(patientMemberInfo2.getPrincipalAmount());
        memberRechargeRecord2.setCurrentRechargeBonus(patientMemberInfo2.getBonusAmount());
        memberRechargeRecord2.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberRechargeRecord2.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord2.setCrtName(BaseContextHandler.getName());
        memberRechargeRecord2.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord2.setUpdName(BaseContextHandler.getName());
        memberRechargeRecord2.setRemarks(model.getMemberId());
        memberRechargeRecord2.setType(20);
        memberRechargeRecord2.setMemberId(patientMemberInfo2.getCardNumber());
        memberRechargeRecordMapper.insertSelective(memberRechargeRecord2);
        // 发送会员充值消息
        patientMemberInfo2_RechargeId = memberRechargeRecord2.getId();
//        sendMemberLogMessages(memberRechargeRecord2.getId(), 0, 20); //已移到最后调用
        // 添加会员卡充值收费记录
        AccountedWayModel accountedWayModel = model.getAccountedWayModel();
        if (StringHelper.isNotNull(accountedWayModel)) {
          MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
          BeanUtils.copyProperties(accountedWayModel, memberRechargeTollRecord);
          memberRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
          memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord2.getId());
          memberRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
          memberRechargeTollRecord.setCrtName(BaseContextHandler.getName());
          memberRechargeTollRecordMapper.insertSelective(memberRechargeTollRecord);
        }
        // 会员卡充值发送短信 type:0充值 1消费
        memberSendMessages(memberRechargeRecord2, 0);
        // 会员卡充值成功发送推送
        WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
        wxTemplateMsgModel.setPatientId(model.getPatientId());
        wxTemplateMsgModel.setTemplateEnum(TemplateEnum.RECHARGE_SUCCESS);
        PatientBaseInfoVo patientBaseInfoVo =
                patientBaseInfoMapper.selectOneById(model.getPatientId());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TemplateDataEnum.PATIENT_NAME.getArgName(), patientBaseInfoVo.getName());
        paramMap.put("keyword1", model.getRechargePrincipal());
        paramMap.put("keyword2", patientMemberInfo2.getPrincipalAmount());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        paramMap.put("keyword3", sdf.format(new Date()));
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setId(Integer.valueOf(BaseContextHandler.getOrgId()));

        List<OrganizationInfoDetail> orgList =
                remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        log.info("门诊信息：" + orgList.get(0).toString());
        if (orgList != null && orgList.size() > 0) {
          paramMap.put("keyword4", orgList.get(0).getName());
        } else {
          throw new ClientServiceException("门诊信息为空", RETURN_VALUE_ISNULL);
        }
        paramMap.put("linkMobile", orgList.get(0).getTel());
        wxTemplateMsgModel.setParamMap(paramMap);
        remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);
      } else {
        return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "会员查询异常!", null);
      }
      // 赠金转入 查询会员余额 余额增加
      if (patientMemberInfo != null) {
        BigDecimal rechargeBonus = model.getRechargeBonus();
        if (null != rechargeBonus) {
          patientMemberInfo.setBonusAmount(patientMemberInfo.getBonusAmount().add(rechargeBonus));
        }
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        // 添加会员卡充值记
        MemberRechargeRecord memberRechargeRecord = new MemberRechargeRecord();
        BeanUtils.copyProperties(model, memberRechargeRecord);
        memberRechargeRecord.setCurrentRechargePrincipal(patientMemberInfo.getPrincipalAmount());
        memberRechargeRecord.setCurrentRechargeBonus(patientMemberInfo.getBonusAmount());
        memberRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setCrtName(BaseContextHandler.getName());
        memberRechargeRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setUpdName(BaseContextHandler.getName());
        memberRechargeRecord.setRemarks(patientMemberInfo2.getCardNumber());
        memberRechargeRecord.setType(21);
        memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
        // 发送会员充值消息
        patientMemberInfo_RechargeId = memberRechargeRecord.getId();
//        sendMemberLogMessages(memberRechargeRecord.getId(), 0, 21); //已移到最后调用
        // 添加会员卡充值收费记录
        AccountedWayModel accountedWayModel = model.getAccountedWayModel();
        if (StringHelper.isNotNull(accountedWayModel)) {
          MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
          BeanUtils.copyProperties(accountedWayModel, memberRechargeTollRecord);
          memberRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
          memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord.getId());
          memberRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
          memberRechargeTollRecord.setCrtName(BaseContextHandler.getName());
          memberRechargeTollRecordMapper.insertSelective(memberRechargeTollRecord);
        }
        // 会员卡充值发送短信 type:0充值 1消费
        memberSendMessages(memberRechargeRecord, 0);
        // 会员卡充值成功发送推送
        WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
        wxTemplateMsgModel.setPatientId(patientMemberInfo.getPatientId());
        wxTemplateMsgModel.setTemplateEnum(TemplateEnum.RECHARGE_SUCCESS);
        PatientBaseInfoVo patientBaseInfoVo =
            patientBaseInfoMapper.selectOneById(patientMemberInfo.getPatientId());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TemplateDataEnum.PATIENT_NAME.getArgName(), patientBaseInfoVo.getName());
        paramMap.put("keyword1", model.getRechargePrincipal());
        paramMap.put("keyword2", patientMemberInfo.getPrincipalAmount());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        paramMap.put("keyword3", sdf.format(new Date()));
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setId(Integer.valueOf(BaseContextHandler.getOrgId()));

        List<OrganizationInfoDetail> orgList =
            remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        log.info("门诊信息：" + orgList.get(0).toString());
        if (orgList != null && orgList.size() > 0) {
          paramMap.put("keyword4", orgList.get(0).getName());
        } else {
          throw new ClientServiceException("门诊信息为空", RETURN_VALUE_ISNULL);
        }
        paramMap.put("linkMobile", orgList.get(0).getTel());
        wxTemplateMsgModel.setParamMap(paramMap);
        remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);
      } else {
        return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST, "会员查询异常!", null);
      }

      sendMemberLogMessages2(patientMemberInfo2_RechargeId, 0, 20, patientMemberInfo_RechargeId, 21);
    } else {
      return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL, "充值金额与入账金额不相等!", null);
    }
    return ResponseUtil.success();
  }

  /**
   * 会员卡充值/消费 短信发送
   *
   * @param object 泛型类
   * @param type   type:0充值 1消费
   */
  public void memberSendMessages(Object object, Integer type) {
    String eventCode = null;
    PatientBaseInfo patientBaseInfo = null;
    MemberRechargeRecord memberRechargeRecord = null;
    MemberExpendRecord memberExpendRecord = null;
    if (type == 0) {
      eventCode = SmsAutosendEventEnum.MEMBER_CHARGE.getCode();
      memberRechargeRecord = (MemberRechargeRecord) object;
      PatientMemberInfo patientMemberInfo =
          mapper.selectCardNumber(memberRechargeRecord.getMemberId());
      patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientMemberInfo.getPatientId());
    } else {
      eventCode = SmsAutosendEventEnum.MEMBER_CONSUME.getCode();
      memberExpendRecord = (MemberExpendRecord) object;
      PatientMemberInfo patientMemberInfo =
          mapper.selectCardNumber(memberExpendRecord.getMemberId());
      patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientMemberInfo.getPatientId());
    }

    if (patientBaseInfo != null) {
      MedicalOrganizationInfoVO medicalOrganizationInfoVO =
          remoteSystemServiceFeign.clinicExtInfoByCompanyId(
              Integer.parseInt(BaseContextHandler.getOrgId()));
      JSONObject templateParam = new JSONObject();
      templateParam.put(
          SmsTemplateItemEnum.CLINIC_PHONE.getAction(), medicalOrganizationInfoVO.getTel());
      templateParam.put(
          SmsTemplateItemEnum.CLINIC_ADDRESS.getAction(), medicalOrganizationInfoVO.getAddress());
      templateParam.put(
          SmsTemplateItemEnum.CLINIC_NAME.getAction(), medicalOrganizationInfoVO.getAbbreviation());
      // 充值
      if (type == 0) {
        // 患者姓名
        templateParam.put(SmsTemplateItemEnum.PATIENT_NAME.getAction(), patientBaseInfo.getName());
        // 会员卡号
        templateParam.put(
            SmsTemplateItemEnum.MEMBER_CARD_NUMBER.getAction(), memberRechargeRecord.getMemberId());
        // 会员充值金额
        BigDecimal rechargeBonus = memberRechargeRecord.getRechargeBonus();
        if (rechargeBonus == null) {
          rechargeBonus = BigDecimal.valueOf(0);
        }
        templateParam.put(
            SmsTemplateItemEnum.MEMBER_RECHARGE_AMOUNT.getAction(),
            memberRechargeRecord
                .getRechargePrincipal()
                .add(rechargeBonus)
                .setScale(2, BigDecimal.ROUND_HALF_UP));
        // 会员剩余金额
        BigDecimal currentRechargeBonus = memberRechargeRecord.getCurrentRechargeBonus();
        if (currentRechargeBonus == null) {
          currentRechargeBonus = BigDecimal.valueOf(0);
        }
        templateParam.put(
            SmsTemplateItemEnum.MEMBER_REMAINING_AMOUNT.getAction(),
            memberRechargeRecord
                .getCurrentRechargePrincipal()
                .add(currentRechargeBonus)
                .setScale(2, BigDecimal.ROUND_HALF_UP));
        // 消费
      } else {
        // 患者姓名
        templateParam.put(SmsTemplateItemEnum.PATIENT_NAME.getAction(), patientBaseInfo.getName());
        // 会员卡号
        templateParam.put(
            SmsTemplateItemEnum.MEMBER_CARD_NUMBER.getAction(), memberExpendRecord.getMemberId());
        // 会员消费金额
        BigDecimal expendGift = memberExpendRecord.getExpendGift();
        if (expendGift == null) {
          expendGift = BigDecimal.valueOf(0);
        }
        templateParam.put(
            SmsTemplateItemEnum.MEMBER_SPENDING_AMOUNT.getAction(),
            memberExpendRecord
                .getExpendPrincipal()
                .add(expendGift)
                .setScale(2, BigDecimal.ROUND_HALF_UP));
        // 会员剩余金额
        BigDecimal currentBonus = memberExpendRecord.getCurrentBonus();
        if (currentBonus == null) {
          currentBonus = BigDecimal.valueOf(0);
        }
        templateParam.put(
            SmsTemplateItemEnum.MEMBER_REMAINING_AMOUNT.getAction(),
            memberExpendRecord
                .getCurrentPrincipal()
                .add(currentBonus)
                .setScale(2, BigDecimal.ROUND_HALF_UP));
      }
      Integer orgId = Integer.parseInt(BaseContextHandler.getOrgId());
      SmsAutoEventSendRecordModel smsModel = new SmsAutoEventSendRecordModel();
      SmsCommonSendRecordModel model = new SmsCommonSendRecordModel();
      model.setMobile(patientBaseInfo.getMobile());
      model.setSendObject(patientBaseInfo.getName());
      model.setTemplateParam(templateParam);
      smsModel.setEventCode(eventCode);
      smsModel.setOrgId(orgId);
      smsModel.setUserId(Integer.parseInt(BaseContextHandler.getUserID()));
      smsModel.setName(BaseContextHandler.getName());
      smsModel.setModels(Collections.singletonList(model));
      //      log.info("推送短信队列数据：{}", JSONObject.toJSON(smsModel));
      redisUtils.lPush(RedisConstants.SMS_SEND_MESSAGE_QUEUE + orgId, smsModel);
    }
  }

  /**
   * 充值记录
   *
   * @param form 充值记录QueryForm
   * @return PageInfo<RechargeRecordVo>
   */
  public PageInfo<RechargeRecordVo> rechargeRecord(RechargeRecordQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<RechargeRecordVo> resultList = memberRechargeRecordMapper.RechargeRecord(form, 0);
    if (!StringHelper.isEmpty(resultList)) {
      for (RechargeRecordVo rechargeRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(rechargeRecordVo.getOrgId());
        if (organizationInfo != null) {
          rechargeRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
        List<MemberRechargeTollRecord> memberRechargeTollRecordList =
            memberRechargeTollRecordMapper.selectMemberRechargeRecord(rechargeRecordVo.getId());
        StringBuilder labels = new StringBuilder(16);
        if (!StringHelper.isEmpty(memberRechargeTollRecordList)) {
          for (MemberRechargeTollRecord memberRechargeTollRecord : memberRechargeTollRecordList) {
            if (memberRechargeTollRecord.getPaymentId() != null) {
              rechargeRecordVo.setPaymentId(memberRechargeTollRecord.getPaymentId());
              AccountItem accountItem =
                  remoteSystemServiceFeign.findAccountItemById(
                      memberRechargeTollRecord.getPaymentId());
              if (accountItem != null) {
                labels.append(accountItem.getName());
              }
            }
          }
        }
        rechargeRecordVo.setPayment(labels.toString());
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 充值记录
   *
   * @param form 赠金转出记录QueryForm
   * @return PageInfo<RechargeRecordVo>
   */
  public PageInfo<RechargeRecord2Vo> rechargeRecord2(RechargeRecordQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<RechargeRecordVo> resultList = memberRechargeRecordMapper.RechargeRecord(form, 20);
    List<RechargeRecord2Vo> tmps = new ArrayList<>();
    if (!StringHelper.isEmpty(resultList)) {
      for (RechargeRecordVo rechargeRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(rechargeRecordVo.getOrgId());
        if (organizationInfo != null) {
          rechargeRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
        String patientName2 =
            patientMemberInfoMapper.selectPatientNameByCardNumber2(rechargeRecordVo.getRemarks());//memberId转入的会员卡
        rechargeRecordVo.setPatientName2(patientName2);
        String patientName1 =
            patientMemberInfoMapper.selectPatientNameByCardNumber2(form.getCardNumber());
        rechargeRecordVo.setPatientName1(patientName1);
        RechargeRecord2Vo tmp = new RechargeRecord2Vo();
        tmp.setPatientName1(rechargeRecordVo.getPatientName1());
        tmp.setPatientName2(rechargeRecordVo.getPatientName2());
        tmp.setOperatingTime(rechargeRecordVo.getOperatingTime());
        tmp.setRechargeBonus(rechargeRecordVo.getRechargeBonus());
        tmp.setRemarks(rechargeRecordVo.getRemarks());
        tmp.setOrgName(rechargeRecordVo.getOrgName());
        tmp.setOperatorName(rechargeRecordVo.getOperatorName());
        tmps.add(tmp);
      }
    }
    return new PageInfo<>(tmps);
  }

  /**
   * 退费
   *
   * @param model 会员卡退费Model
   */
  public ResponseResult refund(MemberReturnRecordModel model) {
    // 查询会员余额 退减余额和赠金
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectCardNumber(model.getMemberId());
    if (patientMemberInfo != null) {
      if (patientMemberInfo.getPrincipalAmount().compareTo(model.getReturnPrincipalAmount()) < 0) {
        throw new ClientServiceException("会员卡本金余额不足", OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
      if (patientMemberInfo.getBonusAmount().compareTo(model.getReturnGiftAmount()) < 0) {
        throw new ClientServiceException("会员卡赠金余额不足", OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
      patientMemberInfo.setPrincipalAmount(
          patientMemberInfo.getPrincipalAmount().subtract(model.getReturnPrincipalAmount()));
      patientMemberInfo.setBonusAmount(
          patientMemberInfo.getBonusAmount().subtract(model.getReturnGiftAmount()));
      patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);

      // 添加会员卡退费记录
      MemberReturnRecord memberReturnRecord = new MemberReturnRecord();
      BeanUtils.copyProperties(model, memberReturnRecord);
      memberReturnRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      AccountItem accountItem =
          remoteSystemServiceFeign.findAccountItemById(memberReturnRecord.getReturnWayId());
      if (accountItem != null) {
        // 获取退费方式类型名称
        memberReturnRecord.setReturnWayType(accountItem.getName());
      }
      memberReturnRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      memberReturnRecord.setCrtName(BaseContextHandler.getName());
      memberReturnRecord.setCurrentPrincipal(patientMemberInfo.getPrincipalAmount());
      memberReturnRecord.setCurrentBonus(patientMemberInfo.getBonusAmount());
      memberReturnRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      memberReturnRecord.setUpdName(BaseContextHandler.getName());
      memberReturnRecord.setActualReturnAmount(model.getReturnPayAmount());
      memberReturnRecord.setRemarks(model.getReturnReason());
      memberReturnRecordMapper.insertSelective(memberReturnRecord);

      // TODO: 充值后判断是否升级会员等级，退费后判断是否降级
      makeMemberLevelByRecharge(model.getMemberId());

      sendMemberLogMessages(memberReturnRecord.getId(), 0, 3);
      return ResponseUtil.success();
    }
    return ResponseUtil.fail(
        OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到会员卡", patientMemberInfo);
  }

  /**
   * 退费记录列表
   *
   * @param form 退费记录QueryForm
   * @return PageInfo<MemberReturnRecordVo>
   */
  public PageInfo<MemberReturnRecordVo> refundList(MemberReturnRecordQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<MemberReturnRecordVo> resultList = memberReturnRecordMapper.refundList(form);
    if (!StringHelper.isEmpty(resultList)) {
      for (MemberReturnRecordVo memberReturnRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(memberReturnRecordVo.getOrgId());
        if (organizationInfo != null) {
          memberReturnRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 消费 同步操作
   *
   * @param model 消费Model
   * @return ResponseResult
   */
  public ResponseResult expend(MemberExpendRecordModel model) {
    if (model.getExpendTotal().compareTo(model.getPrincipalAmount().add(model.getBonusAmount())) != 0) {
      return ResponseUtil.fail(
          OperationCodeConstants.BALANCE_INSUFFICIENT, "会员卡消费总额不等于消费本金加消费赠金", model);
    }
    ReentrantLock reentrantLock = new ReentrantLock(true);
    try {
      reentrantLock.lock();
      log.info("============消费加锁操作===================");
      PatientMemberInfo patientMemberInfo =
          patientMemberInfoMapper.selectCardNumber(model.getMemberId());
      if (patientMemberInfo != null) {
        BigDecimal num =
            patientMemberInfo.getPrincipalAmount().add(patientMemberInfo.getBonusAmount());
        // 如果本金+赠金 小于 消费金额
        if (num.compareTo(model.getExpendTotal()) < 0) {
          return ResponseUtil.fail(
              OperationCodeConstants.BALANCE_INSUFFICIENT, "会员卡余额不足", patientMemberInfo);
        }
        if (patientMemberInfo.getPrincipalAmount().compareTo(model.getPrincipalAmount()) < 0) {
          return ResponseUtil.fail(
              OperationCodeConstants.BALANCE_INSUFFICIENT, "会员卡本金不足", patientMemberInfo);
        }
        if (patientMemberInfo.getBonusAmount().compareTo(model.getBonusAmount()) < 0) {
          return ResponseUtil.fail(
              OperationCodeConstants.BALANCE_INSUFFICIENT, "会员卡赠金不足", patientMemberInfo);
        }
        spending(model, patientMemberInfo);
      } else {
        return ResponseUtil.fail(
            OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到会员卡", patientMemberInfo);
      }

      // 发送微信推送消息
      WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
      wxTemplateMsgModel.setPatientId(patientMemberInfo.getPatientId());
      wxTemplateMsgModel.setTemplateEnum(TemplateEnum.MEMBER_CONSUME);

      PatientBaseInfoVo patientBaseInfoVo =
          patientBaseInfoMapper.selectOneById(model.getPatientId());

      Map<String, Object> paramMap = new HashMap<>();
      paramMap.put(TemplateDataEnum.PATIENT_NAME.getArgName(), patientBaseInfoVo.getName());
      if (patientBaseInfoVo != null) {
        paramMap.put("keyword1", model.getExpendTotal());
        paramMap.put(
            "keyword2",
            patientMemberInfo.getPrincipalAmount().add(patientMemberInfo.getBonusAmount()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        paramMap.put("keyword3", sdf.format(new Date()));
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setId(Integer.valueOf(BaseContextHandler.getOrgId()));

        List<OrganizationInfoDetail> orgList =
            remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        if (orgList != null && orgList.size() > 0) {
          paramMap.put("keyword4", orgList.get(0).getName());
        } else {
          throw new ClientServiceException("门诊信息为空", RETURN_VALUE_ISNULL);
        }
        paramMap.put("linkMobile", orgList.get(0).getTel());
      } else {
        throw new ClientServiceException("无此患者信息", DATA_NOT_EXIST);
      }
      wxTemplateMsgModel.setParamMap(paramMap);
      remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);

    } finally {
      reentrantLock.unlock();
      log.info("============消费锁释放操作===================");
    }
    return ResponseUtil.success();
  }

  /**
   * 消费
   *
   * @param model             消费Model
   * @param patientMemberInfo 会员卡信息
   */
  public void spending(MemberExpendRecordModel model, PatientMemberInfo patientMemberInfo) {
    // 账户本金
    BigDecimal principalAmount;
    // 账户赠金
    BigDecimal bonusAmount;
    // 创建消费记录对象
    MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
    BeanUtils.copyProperties(model, memberExpendRecord);
    /**
     * 本金处理
     */
    // 获取本金
    principalAmount = patientMemberInfo.getPrincipalAmount();
    // 本金-消费本金=新的本金
    BigDecimal surplus = principalAmount.subtract(model.getPrincipalAmount());
    // 设置会员卡本金
    patientMemberInfo.setPrincipalAmount(surplus);
    // 设置消费本金
    memberExpendRecord.setExpendPrincipal(model.getPrincipalAmount());

    /**
     * 赠金处理
     */
    // 获取赠金
    bonusAmount = patientMemberInfo.getBonusAmount();
    // 赠金-消费赠金=新的赠金
    BigDecimal costBonus = bonusAmount.subtract(model.getBonusAmount());
    // 设置会员卡赠金
    patientMemberInfo.setBonusAmount(costBonus);
    // 设置消费赠金
    memberExpendRecord.setExpendGift(model.getBonusAmount());

    patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
    // 添加消费记录
    memberExpendRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    memberExpendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    memberExpendRecord.setCrtName(BaseContextHandler.getName());
    memberExpendRecord.setCurrentPrincipal(patientMemberInfo.getPrincipalAmount());
    memberExpendRecord.setCurrentBonus(patientMemberInfo.getBonusAmount());
    memberExpendRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    memberExpendRecord.setUpdName(BaseContextHandler.getName());
    memberExpendRecord.setType(0);
    memberExpendRecordMapper.insertSelective(memberExpendRecord);
    // 发送预付款消费消息
    sendMemberLogMessages(memberExpendRecord.getId(), 0, 2);
    // 会员卡充值发送短信 type:0充值 1消费
    memberSendMessages(memberExpendRecord, 1);
  }

  /**
   * 消费记录
   *
   * @param queryForm 消费记录查询QueryForm
   * @return PageInfo<MemberExpendRecordVo>
   */
  public PageInfo<MemberExpendRecordVo> expendList(MemberExpendRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    return new PageInfo<>(getOrgInfo(queryForm));
  }

  /**
   * 查询消费记录并获取门诊名称
   */
  public List<MemberExpendRecordVo> getOrgInfo(MemberExpendRecordQueryForm queryForm) {
    List<MemberExpendRecordVo> resultList = memberExpendRecordMapper.expendList(queryForm);
    if (!StringHelper.isEmpty(resultList)) {
      for (MemberExpendRecordVo memberExpendRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(memberExpendRecordVo.getOrgId());
        if (organizationInfo != null) {
          memberExpendRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
      }
    }
    return resultList;
  }

  /**
   * 查询会员卡绑定信息
   *
   * @param form 查询患者会员信息form
   * @return List<MemberInfoVo>
   */
  public MemberInfoVo findMemberInfo(PatientMemberInfoQueryForm form) {
    MemberInfoVo result = new MemberInfoVo();
    PatientMemberInfo memberInfo = patientMemberIdentityLevel(form.getPatientId());
    MasertMemberInfoVo master = new MasertMemberInfoVo();
    MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberInfo.getMemberTypeId());
    if (StringHelper.isNotNull(memberType)) {
      master.setMasterMemberCardName(memberType.getName());
      master.setRate(memberType.getRate());
      master.setPictureCode(memberType.getPictureCode());
    }
    master.setId(memberInfo.getId());
    master.setMasterCardTypeId(memberInfo.getMemberTypeId());
    master.setMasterCardId(memberInfo.getPatientId());
    master.setMasterCardNumber(memberInfo.getCardNumber());
    master.setPoint(memberInfo.getPoint());
    master.setCrtTime(memberInfo.getCrtTime());

    result.setMasertMemberInfoVo(master);
    return result;
  }

  /**
   * 查询会员卡绑定信息
   *
   * @param form 查询患者会员信息form
   * @return List<MemberInfoVo>
   */
  @Deprecated
  public MemberInfoVo findMemberInfo0(PatientMemberInfoQueryForm form) {
    MemberInfoVo memberInfoVo = new MemberInfoVo();
    MasertMemberInfoVo masertMemberInfoVo = patientMemberInfoMapper.selectMasertMemberInfo(form);
    if (masertMemberInfoVo != null) {
      // 获取会员卡名称
      MemberType memberType =
          this.remoteSystemServiceFeign.findMemberTypeById(
              masertMemberInfoVo.getMasterCardTypeId());
      if (memberType != null) {
        masertMemberInfoVo.setMasterMemberCardName(memberType.getName());
        masertMemberInfoVo.setRate(memberType.getRate());
        masertMemberInfoVo.setPictureCode(memberType.getPictureCode());
      }
    }
    memberInfoVo.setMasertMemberInfoVo(masertMemberInfoVo);
    List<SecondaryMemberInfoVo> secondaryMemberInfoVos =
        patientMemberRelationMapper.findMemberInfo(form);
    if (!StringHelper.isEmpty(secondaryMemberInfoVos)) {
      for (SecondaryMemberInfoVo secondaryMemberInfoVo : secondaryMemberInfoVos) {
        // 获取会员卡名称
        MemberType memberType =
            this.remoteSystemServiceFeign.findMemberTypeById(
                secondaryMemberInfoVo.getSecondaryMemberTypeId());
        secondaryMemberInfoVo.setRate(memberType.getRate());
        secondaryMemberInfoVo.setPictureCode(memberType.getPictureCode());
        secondaryMemberInfoVo.setMemberCardName(memberType.getName());
      }
    }
    memberInfoVo.setSecondaryMemberInfoVos(secondaryMemberInfoVos);
    return memberInfoVo;
  }

  /**
   * 患者会员身份等级
   *
   * @param patientId
   * @return
   */
  public PatientMemberInfo patientMemberIdentityLevel(Integer patientId) {
    // 会员卡
    PatientMemberInfo memberInfo = patientMemberInfoMapper.selectOneByPatientId(patientId);
    if (StringHelper.isNotNull(memberInfo) && memberInfo.getInservice() && memberInfo.getMemberTypeId()!=4) {
      return memberInfo;
    }
    // 亲密付主卡人
    memberInfo = patientMemberInfoMapper.selectPatientBindMemberInfo(patientId);
    if (StringHelper.isNotNull(memberInfo) && memberInfo.getInservice()) {
      return memberInfo;
    }
    // 推荐关系人
    memberInfo = patientMemberInfoMapper.selectPatientReferrerMemberInfo(patientId);
    if (StringHelper.isNotNull(memberInfo) && memberInfo.getInservice()) {
      Integer memberTypeId = memberInfo.getMemberTypeId();
      if (memberTypeId != 4) {
        // 推荐关系人的会员卡的次一级会员类型
        MemberType memberType = remoteSystemServiceFeign.findSecondaryMemberTypeById(memberTypeId);
        if (memberType.getId() != 4) {
          memberInfo.setMemberTypeId(memberType.getId());
          return memberInfo;
        }
      }
    }
    // 普通会员
    return crtCommonMember(patientId);
  }

  private PatientMemberInfo crtCommonMember(Integer patientId) {
    PatientMemberInfo member = new PatientMemberInfo();
    member.setMemberTypeId(4);
    member.setPatientId(patientId);
    member.setPrincipalAmount(BigDecimal.ZERO);
    member.setBonusAmount(BigDecimal.ZERO);
    return member;
  }

  /**
   * 可用会员卡查询
   *
   * @param id
   * @return PatientPrepaymentBalanceVo
   */
  public List<MemberBaseInfoVo> balancePayment(Integer id) {
    List<MemberBaseInfoVo> resultList = new ArrayList<>();
    // 患者本人会员卡
    MemberBaseInfoVo memberBaseInfo = patientMemberInfoMapper.findMemberBaseInfo(id);
    if (null != memberBaseInfo && memberBaseInfo.getInservice() && memberBaseInfo.getMemberTypeId() != 4) {
      resultList.add(memberBaseInfo);
    }
    // 患者作为副卡人可用会员卡
    List<MemberBaseInfoVo> memberBaseInfoVoList =
        patientMemberInfoMapper.selectMemberRelationByMasterPatientId(id);
    List<MemberBaseInfoVo> memberBaseInfoVoList2 = memberBaseInfoVoList.stream().filter(m -> {
      return m.getInservice() && m.getMemberTypeId() != 4;
    }).collect(Collectors.toList());
    resultList.addAll(memberBaseInfoVoList2);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.removeIf(vo -> null == vo.getId());
    }
    resultList.forEach(
        vo -> {
          vo.setRemark(
              String.format("%s的会员卡（账户余额：%.2f）", vo.getName(), vo.getMemberCardMoneySum()));
          vo.setAccountItemId(MEMBER.getAccountItemId());
        });
    return resultList;
  }

  /**
   * 会员卡账单退费
   *
   * @param model
   */
  public void billRefund(MemberBillRechargeModel model) {
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectOneByCardNumber(model.getMemberId());
    if (patientMemberInfo != null) {

      if (model.getRechargePrincipal() != null) {
        patientMemberInfo.setPrincipalAmount(
            patientMemberInfo.getPrincipalAmount().add(model.getRechargePrincipal()));
      }
      if (model.getRechargeBonus() != null) {
        patientMemberInfo.setBonusAmount(
            patientMemberInfo.getBonusAmount().add(model.getRechargeBonus()));
      }
      patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
      MemberRechargeRecord memberRechargeRecord = new MemberRechargeRecord();
      BeanUtils.copyProperties(model, memberRechargeRecord);
      memberRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      memberRechargeRecord.setType(2);
      memberRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      memberRechargeRecord.setCrtName(BaseContextHandler.getName());
      memberRechargeRecord.setCurrentRechargePrincipal(patientMemberInfo.getPrincipalAmount());
      memberRechargeRecord.setCurrentRechargeBonus(patientMemberInfo.getBonusAmount());
      memberRechargeRecord.setOrderRecordId(model.getOrderRecordId());
      memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
      // 发送消息 账单退费
      sendMemberLogMessages(memberRechargeRecord.getId(), 0, 5);
    }
  }

  /**
   * 会员卡撤销收费
   *
   * @param model 撤销收费model
   * @return
   */
  public ResponseResult revocationFee(MemberRevocationFeeModel model) {
    MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
    memberExpendRecord.setMemberId(model.getMemberId());
    memberExpendRecord.setBillPayRecordId(model.getBillPayRecordId());
    memberExpendRecord.setInservice(true);
    MemberExpendRecord memberExpend = memberExpendRecordMapper.selectOne(memberExpendRecord);
    if (memberExpend != null) {
      memberExpend.setInservice(false);
      memberExpendRecordMapper.updateByPrimaryKeySelective(memberExpend);

      PatientMemberInfo patientMemberInfo =
          patientMemberInfoMapper.selectOneByCardNumber(model.getMemberId());
      if (patientMemberInfo != null) {
        if (memberExpend.getExpendPrincipal() != null) {
          patientMemberInfo.setPrincipalAmount(
              patientMemberInfo.getPrincipalAmount().add(memberExpend.getExpendPrincipal()));
        }
        if (memberExpend.getExpendGift() != null) {
          patientMemberInfo.setBonusAmount(
              patientMemberInfo.getBonusAmount().add(memberExpend.getExpendGift()));
        }
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        MemberRechargeRecord memberRechargeRecord = new MemberRechargeRecord();
        BeanUtils.copyProperties(model, memberRechargeRecord);
        // 撤销本金
        memberRechargeRecord.setRechargePrincipal(memberExpend.getExpendPrincipal());
        // 撤销赠金
        memberRechargeRecord.setRechargeBonus(memberExpend.getExpendGift());
        memberRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberRechargeRecord.setType(1);
        memberRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setCrtName(BaseContextHandler.getName());
        memberRechargeRecord.setCurrentRechargePrincipal(patientMemberInfo.getPrincipalAmount());
        memberRechargeRecord.setCurrentRechargeBonus(patientMemberInfo.getBonusAmount());
        memberRechargeRecord.setOrderRecordId(memberExpend.getOrderRecordId());
        memberRechargeRecord.setBillRecordId(memberExpend.getBillRecordId());
        memberRechargeRecord.setBillPayRecordId(memberExpend.getBillPayRecordId());
        memberRechargeRecord.setRemarks(null);
        memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
        // 发送消息 删除消费消息
        Integer expendId = updPatientMemberInfo(model);
        if (expendId != null) {
          remoteRabbitMqServiceFeign.sendMessage(
              expendId, MEMBER.getType(), 2, 2, MsgCategoryEnum.BasePatientMemberOccurLog);
        }
        // 发送会员卡撤销收费消息
        sendMemberLogMessages(memberRechargeRecord.getId(), 0, 4);
        return ResponseUtil.success();
      } else {
        return ResponseUtil.fail(
            OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到会员卡", patientMemberInfo);
      }
    }
    return ResponseUtil.fail(OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到消费记录", memberExpend);
  }

  /**
   * 撤销收费后 消费记录作废
   *
   * @param model 条件
   * @return Integer 消费记录id
   */
  public Integer updPatientMemberInfo(MemberRevocationFeeModel model) {
    MemberExpendRecord memberExpendRecordv = new MemberExpendRecord();
    memberExpendRecordv.setMemberId(model.getMemberId());
    memberExpendRecordv.setBillPayRecordId(model.getBillPayRecordId());
    MemberExpendRecord memberExpendRecordvo =
        memberExpendRecordMapper.selectOne(memberExpendRecordv);
    if (memberExpendRecordvo != null) {
      memberExpendRecordvo.setInservice(false);
      memberExpendRecordMapper.updateByPrimaryKeySelective(memberExpendRecordvo);
      return memberExpendRecordvo.getId();
    }
    return null;
  }

  /**
   * 根据会员卡号和账单记录ID查询支付详情（外部服务调用）
   *
   * @param query
   * @return 返回支付详情
   */
  public MemberExpendRecord memberPaymentRecordDetail(PaymentRecordDetailQuery query) {
    MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
    memberExpendRecord.setMemberId(query.getCardId());
    memberExpendRecord.setBillPayRecordId(query.getBillRecordId());
    MemberExpendRecord result = memberExpendRecordMapper.selectOne(memberExpendRecord);
    if (result == null) {
      result = new MemberExpendRecord();
    }
    return result;
  }

  /**
   * 根据支付方式统计会员充值和预付款充值的金额
   *
   * @param query
   * @return
   */
  public BigDecimal sumMemberAndPrepayRechargeCash(CashReceiptOrRefundQuery query) {
    return mapper.sumMemberAndPrepayRechargeCash(query);
  }

  /**
   * 根据条件查询预付款、会员卡退费
   *
   * @param query
   * @return
   */
  public BigDecimal sumMemberAndPrepaidRefundCash(CashReceiptOrRefundQuery query) {
    return mapper.sumMemberAndPrepaidRefundCash(query);
  }

  /**
   * 查询已绑定主卡信息
   *
   * @param patientId
   * @return
   */
  public List<PatientCardOwnerInfoVo> findPatientCardOwnerInfo(Integer patientId) {
    // 先查询是否已存在绑定关系（不管是什么类型）
    List<PatientCardOwnerInfoVo> patientCardOwnerInfoVoList =
        patientMemberRelationMapper.findPatientCardOwnerInfo(patientId);
    // 判断是否为空
    if (StringHelper.isNotEmpty(patientCardOwnerInfoVoList)) {
      // 不为空查询绑定关系（权益和余额一起查询）
      for (PatientCardOwnerInfoVo patientCardOwnerInfoVo : patientCardOwnerInfoVoList) {
        if (patientCardOwnerInfoVo.getMemberTypeId() != null) {
          // 获取会员卡名称
          MemberType memberType =
              this.remoteSystemServiceFeign.findMemberTypeById(
                  patientCardOwnerInfoVo.getMemberTypeId());
          if (memberType != null && memberType.getName() != null) {
            patientCardOwnerInfoVo.setMemberTypeName(memberType.getName());
          }
        }
        // 假如和卡主即绑定了权益又绑定了余额，就会存在两条消息 一条BindType为0 一条为1
        List<PatientMemberRelation> patientMemberRelationList =
            patientMemberRelationMapper.isBindMember(
                patientCardOwnerInfoVo.getMasterCardId(), patientId);
        // 判断是否为空
        if (StringHelper.isNotEmpty(patientMemberRelationList)) {
          // 循环判断绑定关系
          for (PatientMemberRelation patientMemberRelation : patientMemberRelationList) {
            // 如果存在权益绑定就设置为true,反之就是余额绑定,默认为false
            if (patientMemberRelation.getBindType() == 0) {
              patientCardOwnerInfoVo.setIsDiscount(true);
            } else {
              patientCardOwnerInfoVo.setIsMoney(true);
            }
          }
        }
      }
    }
    return patientCardOwnerInfoVoList;
  }

  /**
   * 消费记录-导出
   *
   * @param response
   * @param query
   */
  public void expendExport(HttpServletResponse response, MemberExpendRecordQueryForm query)
      throws IOException {
    query.setWhetherPage(false);
    PageInfo<MemberExpendRecordVo> workloadList = expendList(query);
    List<MemberExpendRecordVo> resultList = workloadList.getList();
    ExcelUtil<MemberExpendRecordVo> excelUtil = new ExcelUtil<>(MemberExpendRecordVo.class);
    String fileName = "消费记录";
    excelUtil.exportExcel(response, resultList, "消费记录", fileName);
  }

  /**
   * 充值记录-导出
   *
   * @param response 请求
   * @param query    条件
   */
  public void expendExportRechargeRecord(
      HttpServletResponse response, RechargeRecordQueryForm query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<RechargeRecordVo> workloadList = rechargeRecord(query);
    List<RechargeRecordVo> resultList = workloadList.getList();
    ExcelUtil<RechargeRecordVo> excelUtil = new ExcelUtil<>(RechargeRecordVo.class);
    String fileName = "充值记录";
    excelUtil.exportExcel(response, resultList, "充值记录", fileName);
  }

  public void expendExportRechargeRecord2(
      HttpServletResponse response, RechargeRecordQueryForm query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<RechargeRecord2Vo> workloadList = rechargeRecord2(query);
    List<RechargeRecord2Vo> resultList = workloadList.getList();
    ExcelUtil<RechargeRecord2Vo> excelUtil = new ExcelUtil<>(RechargeRecord2Vo.class);
    String fileName = "赠金转账记录";
    excelUtil.exportExcel(response, resultList, "赠金转账记录", fileName);
  }

  /**
   * 退费记录-导出
   *
   * @param response 请求
   * @param query    条件
   */
  public void expendExportRefundList(
      HttpServletResponse response, MemberReturnRecordQueryForm query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<MemberReturnRecordVo> workloadList = refundList(query);
    List<MemberReturnRecordVo> resultList = workloadList.getList();
    ExcelUtil<MemberReturnRecordVo> excelUtil = new ExcelUtil<>(MemberReturnRecordVo.class);
    String fileName = "退费记录";
    excelUtil.exportExcel(response, resultList, "退费记录", fileName);
  }

  /**
   * 查询患者储蓄账号（会员卡or预付款）信息列表
   *
   * @param query
   * @return
   */
  public ClinicChargeItemVO findDepositAccountList(PatientDepositAccountQueryForm query) {
    ClinicChargeItemVO result = new ClinicChargeItemVO();
    Integer patientId = query.getPatientId();
    List<Integer> types = query.getTypes();
    List<Integer> spTypes = new ArrayList<>();
    types.forEach(
        type -> {
          if (MEMBER.equals(type)) {
            // 会员卡
            result.setMemberItems(balancePayment(patientId));
          } else if (NORMAL_PREPAYMENT.equals(type)) {
            // 预付款
            result.setPrepaymentItems(patientPrepaymentRelationBiz.balancePayment(patientId));
          } else if (PatientDepositAccountTypeEnum.isSpPrepaymentType(type)) {
            spTypes.add(type);
          }
        });
    if (StringHelper.isNotEmpty(spTypes)) {
      // 专项预付款
      List<PatientPrepaymentsInfoVo> spPrepayments =
          patientPrepaymentRelationBiz.balancePayment(patientId, spTypes);
      result.setSpPrepaymentItems(spPrepayments);
    }
    return result;
  }

  /**
   * 根据订单记录id查询会员卡or预付款账户的账单消费列表
   *
   * @param orderRecordId
   * @return
   */
  public List<PatientDepositAccountVO> findDepositAccountBillExpendList(Integer orderRecordId) {
    return mapper.selectDepositAccountBillExpendList(orderRecordId);
  }

  /**
   * 账单返点至会员卡
   *
   * @param model
   */
  public void billRebate2MemberAccount(BillRebate2MemberAccountModel model) {
    BigDecimal receivedAmount = model.getReceivedAmount();
    if (StringHelper.leZero(receivedAmount)) {
      throw new ClientServiceException("账单返点失败，返点金额不能为空", DATA_ERROR);
    }
    BigDecimal rebateRatio = new BigDecimal(100);
    Integer patientId = model.getPatientId();
    Integer acceptorId = null;
    if (model.getRebateRatioType().intValue() == 2) {
      // 患者消费时给亲密付主卡人或推荐关系人返点
      PatientMemberInfo memberInfo = patientMemberInfoMapper.selectPatientBindMemberInfo(patientId);
      if (StringHelper.isNotNull(memberInfo)) {
        acceptorId = memberInfo.getPatientId();
      } else {
        PatientKinRelationVo referrer = patientKinRelationBiz.findPatientKinReferrer(patientId);
        if (StringHelper.isNotNull(referrer)) {
          acceptorId = referrer.getPatientId();
        }
      }

      PatientOrigin patientOrigin = originBiz.findPatientOriginById(2);
      if (Objects.isNull(patientOrigin) || Objects.isNull(patientOrigin.getGiftRebateRate())) {
        return;
      }
      rebateRatio = patientOrigin.getGiftRebateRate();
    } else {
      PatientKinRelationVo referrer = patientKinRelationBiz.findPatientKinReferrer(patientId);
      if (StringHelper.isNotNull(referrer)) {
        acceptorId = referrer.getPatientId();
      }
    }

    if (acceptorId == null) {
      log.info("患者：{}，暂无亲密付主卡人或推荐关系人", patientId);
      return;
    }
    BigDecimal giftBonus = receivedAmount.multiply(rebateRatio.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
    PatientMemberInfo member = mapper.selectOneByPatientId(acceptorId);
    if (StringHelper.isNull(member)) {
      OpenCardModel openCardModel = new OpenCardModel();
      openCardModel.setPatientId(acceptorId);
      addMemberCard(openCardModel);
    }
    member = mapper.selectOneByPatientId(acceptorId);
    int userId = Integer.parseInt(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Date now = BaseContextHandler.getCurTime();
    BigDecimal principalAmount = member.getPrincipalAmount();
    BigDecimal bonusAmount = member.getBonusAmount();
//    if (StringHelper.gtZero(principal)) {
//      member.setPrincipalAmount(principalAmount.add(principal));
//    }
    if (StringHelper.gtZero(giftBonus)) {
      member.setBonusAmount(bonusAmount.add(giftBonus));
    }
    member.setUptId(userId);
    member.setUpdName(name);
    member.setUpdTime(now);

    // 更新会员卡账户信息
    Example condition = new Example(PatientMemberInfo.class);
    condition.createCriteria().andEqualTo("id", member.getId())
        .andEqualTo("principalAmount", principalAmount)
        .andEqualTo("bonusAmount", bonusAmount);
    int i = patientMemberInfoMapper.updateByExampleSelective(member, condition);
    if (i != 1) {
      throw new ClientServiceException("账单返点失败，请稍后再试", DATA_ERROR);
    }

    // 生成充值记录
    MemberRechargeRecord memberRechargeRecord = new MemberRechargeRecord();
    BeanUtils.copyProperties(model, memberRechargeRecord);
    memberRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    memberRechargeRecord.setCrtId(userId);
    memberRechargeRecord.setCrtName(name);
    memberRechargeRecord.setCrtTime(now);
    memberRechargeRecord.setUptId(userId);
    memberRechargeRecord.setUpdName(name);
    memberRechargeRecord.setUpdTime(now);
    memberRechargeRecord.setMemberId(member.getCardNumber());
    memberRechargeRecord.setRechargePrincipal(BigDecimal.ZERO);
    memberRechargeRecord.setRechargeBonus(giftBonus);
    memberRechargeRecord.setCurrentRechargePrincipal(member.getPrincipalAmount());
    memberRechargeRecord.setCurrentRechargeBonus(member.getBonusAmount());
    memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
    // 发送消息 账单退费
    sendMemberLogMessages(memberRechargeRecord.getId(), 0, model.getType());
  }

  /**
   * 患者个人累计数据统计
   *
   * @param patientId
   * @return
   */
  public PatientCumulativeInfoVO findPatientCumulativeInfo(Integer patientId) {
    PatientCumulativeInfoVO result = mapper.selectPatientCumulativeTotalInfo(patientId);
    if (StringHelper.isNull(result)) {
      result = new PatientCumulativeInfoVO();
    }
    BigDecimal cumulativeConsumption = treatmentServiceFeign.getCashInfo(patientId).getCumulativeConsumption();
    if (StringHelper.isNotNull(cumulativeConsumption)) {
      result.setCumulativeConsumption(cumulativeConsumption);
    }
    return result;
  }

  public ResponseResult checkBindingRelation(MemberBindingRelationInfoModel form) {
    if (form.getPatientId().equals(form.getSecondaryCardId())) {
      return ResponseUtil.fail(OperationCodeConstants.SAME_DATA_EXIST, "不能为患者本人！", "");
    }
    PatientMemberRelation isPatientMemberRelation =
            patientMemberRelationMapper.findMemberBindingRelation(form);
    if (isPatientMemberRelation != null) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "已存在绑定关系,不能双向绑定！", isPatientMemberRelation);
    }
    // 查询亲密付是否已存在其他有激活的绑定
    PatientMemberInfoQueryForm qq = new PatientMemberInfoQueryForm();
    qq.setPatientId(form.getSecondaryCardId());
    qq.setBindType(1);
    List<SecondaryMemberInfoVo> secondaryMemberInfoVos = patientMemberRelationMapper.findMemberInfo2(qq);
    if (secondaryMemberInfoVos.size() > 0) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "该紧密付已存在其他绑定关系！", isPatientMemberRelation);
    }

    // 是否普通会员
    PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOneByPatientId(form.getSecondaryCardId());
    if (patientMemberInfo != null && patientMemberInfo.getMemberTypeId() != 4) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "非普通会员无法绑定！", isPatientMemberRelation);
    }

    PatientMemberRelation MemberRelation =
            this.patientMemberRelationMapper.findBindingRelation(form);
    if (MemberRelation != null) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "该绑定已存在,不能重复绑定！", MemberRelation);
    }
    return ResponseUtil.success("ok");
  }

  public ResponseResult addMemberBindingRelation4Open(MemberBindingRelationInfoModel form, Integer orgId, Integer optId, String name) {
    if (form.getPatientId().equals(form.getSecondaryCardId())) {
      return ResponseUtil.fail(OperationCodeConstants.SAME_DATA_EXIST, "不能为患者本人！", "");
    }
    PatientMemberRelation isPatientMemberRelation =
            patientMemberRelationMapper.findMemberBindingRelation(form);
    if (isPatientMemberRelation != null) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "已存在绑定关系,不能双向绑定！", isPatientMemberRelation);
    }
    // 查询亲密付是否已存在其他有激活的绑定
    PatientMemberInfoQueryForm qq = new PatientMemberInfoQueryForm();
    qq.setPatientId(form.getSecondaryCardId());
    qq.setBindType(1);
    List<SecondaryMemberInfoVo> secondaryMemberInfoVos = patientMemberRelationMapper.findMemberInfo2(qq);
    if (secondaryMemberInfoVos.size() > 0) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "该紧密付已存在其他绑定关系！", isPatientMemberRelation);
    }
    // 删除该亲密付未激活的绑定
    patientMemberRelationMapper.deleteOtherMemberRelation(
            form.getSecondaryCardId(), new ArrayList<>());

    // 是否普通会员
    PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOneByPatientId(form.getSecondaryCardId());
    if (patientMemberInfo != null && patientMemberInfo.getMemberTypeId() != 4) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "非普通会员无法绑定！", isPatientMemberRelation);
    }

    PatientMemberRelation MemberRelation =
            this.patientMemberRelationMapper.findBindingRelation(form);
    if (MemberRelation != null) {
      return ResponseUtil.fail(
              OperationCodeConstants.SAME_DATA_EXIST, "该绑定已存在,不能重复绑定！", MemberRelation);
    } else {
      PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
      BeanUtils.copyProperties(form, patientMemberRelation);
      // type为0 添加会员卡权限绑定
      if (form.getBindType() == 0) {
        patientMemberRelation.setOrgId(orgId);
        patientMemberRelation.setCrtId(optId);
        patientMemberRelation.setCrtName(name);
        this.patientMemberRelationMapper.insertSelective(patientMemberRelation);
        // 发送消息
        remoteRabbitMqServiceFeign.sendMessage(
                patientMemberRelation.getId(), 0, 0, MsgCategoryEnum.BasePatientMemberRelation);
      }
      // type为1 添加会员卡共享值 双项绑定
      if (form.getBindType() == 1) {
        patientMemberRelation.setOrgId(orgId);
        patientMemberRelation.setCrtId(optId);
        patientMemberRelation.setCrtName(name);
        this.patientMemberRelationMapper.insertSelective(patientMemberRelation);
        // 添加会员双向关联
        remoteRabbitMqServiceFeign.sendMessage(
                patientMemberRelation.getId(), 0, 0, MsgCategoryEnum.BasePatientMemberRelation);
        int masterCardI = patientMemberRelation.getMasterCardId();
        patientMemberRelation.setMasterCardId(patientMemberRelation.getSecondaryCardId());
        patientMemberRelation.setSecondaryCardId(masterCardI);
        patientMemberRelation.setId(null);
        this.patientMemberRelationMapper.insertSelective(patientMemberRelation);
        // 添加会员双向关联
        sendMemberRelationMessages(patientMemberRelation.getId(), 0);
      }

      // 发送微信推送消息
      WxTemplateMsgModel wxTemplateMsgModel = new WxTemplateMsgModel();
      wxTemplateMsgModel.setPatientId(form.getMasterCardId());
      wxTemplateMsgModel.setTemplateEnum(TemplateEnum.BIND_SUCCESS);

      PatientBaseInfoVo patientBaseInfoVo =
              patientBaseInfoMapper.selectOneById(form.getSecondaryCardId());
      Map<String, Object> paramMap = new HashMap<>();
//      if (form.getBindType() == 0) {
//        paramMap.put("first", "您好，您的会员卡成功绑定副卡人，将享受您的会员卡折扣权益，副卡人信息如下：");
//      } else {
//        paramMap.put("first", "您好，您的会员卡成功绑定余额共享人，可使用您的会员卡余额，信息如下：");
//      }
      paramMap.put("first", "您好，您的会员卡成功绑定亲密付，可使用您的会员卡折扣权益和会员卡余额，信息如下：");

      if (patientBaseInfoVo != null) {
        paramMap.put("keyword1", patientBaseInfoVo.getName());
        paramMap.put("keyword2", patientBaseInfoVo.getMobile());
      } else {
        throw new ClientServiceException("无此患者信息", DATA_NOT_EXIST);
      }
      wxTemplateMsgModel.setParamMap(paramMap);
      remoteWechatServiceFeign.pushTemplate(wxTemplateMsgModel);

      return ResponseUtil.success();
    }
  }
}
