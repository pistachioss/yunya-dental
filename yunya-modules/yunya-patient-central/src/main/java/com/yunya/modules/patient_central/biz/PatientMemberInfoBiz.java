package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.CardRelationForm;
import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  /** 注入会员卡Mapper */
  @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;
  /** 注入会员绑定关系Mapper */
  @Autowired private PatientMemberRelationMapper patientMemberRelationMapper;
  /** 注入会员变更记录Mapper */
  @Autowired private PatientMemberChangeLogMapper patientMemberChangeLogMapper;
  /** 注入系统feign对象 */
  @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;
  /** 注入会员充值Mapper */
  @Autowired private MemberRechargeRecordMapper memberRechargeRecordMapper;
  /** 注入会员充值明细Mapper */
  @Autowired private MemberRechargeTollRecordMapper memberRechargeTollRecordMapper;
  /** 注入会员退费Mapper */
  @Autowired private MemberReturnRecordMapper memberReturnRecordMapper;
  /** 注入会员消费记录Mapper */
  @Autowired private MemberExpendRecordMapper memberExpendRecordMapper;
  /** 注入服务 */
  @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;

  /**
   * 根据患者id查询会员基本信息
   *
   * @param id 患者id
   * @return MemberBaseInfoVo
   */
  public MemberBaseInfoVo findMemberBaseInfo(Integer id) {
    MemberBaseInfoVo memberBaseInfoVO = this.patientMemberInfoMapper.findMemberBaseInfo(id);
    if (memberBaseInfoVO.getId() != null) {
      // 获取会员卡名称
      MemberType memberType =
          this.remoteSystemServiceFeign.findMemberTypeById(memberBaseInfoVO.getMemberTypeId());
      if (memberType != null && memberType.getName() != null) {
        memberBaseInfoVO.setMemberCardName(memberType.getName());
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
    if (form.getPatientId().equals(form.getSecondaryCardId())) {
      return ResponseUtil.fail(OperationCodeConstants.SAME_DATA_EXIST, "副卡人不能为患者本人！", "");
    }
    PatientMemberRelation isPatientMemberRelation =
        patientMemberRelationMapper.findMemberBindingRelation(form);
    if (isPatientMemberRelation != null) {
      return ResponseUtil.fail(
          OperationCodeConstants.SAME_DATA_EXIST, "已存在绑定关系,不能双向绑定！", isPatientMemberRelation);
    }
    PatientMemberRelation MemberRelation =
        this.patientMemberRelationMapper.findBindingRelation(form);
    if (MemberRelation != null) {
      return ResponseUtil.fail(
          OperationCodeConstants.SAME_DATA_EXIST, "该副卡人已存在,不能重复绑定！", MemberRelation);
    } else {
      PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
      BeanUtils.copyProperties(form, patientMemberRelation);
      // type为0 添加会员卡权限绑定
      if (form.getBindType() == 0) {
        patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberRelation.setCrtName(BaseContextHandler.getName());
        this.patientMemberRelationMapper.insertSelective(patientMemberRelation);
        // 发送消息
        remoteRabbitMqServiceFeign.sendMessage(
            patientMemberRelation.getId(), 0, 0, MsgCategoryEnum.BasePatientMemberRelation);
      }
      // type为1 添加会员卡共享值 双项绑定
      if (form.getBindType() == 1) {
        patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberRelation.setCrtName(BaseContextHandler.getName());
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

      return ResponseUtil.success();
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
    map.put("type", 0);
    messageModel.setParamMap(map);
    messageModel.setOperateType(OperateType);
    messageModel.setMsgCategoryEnum(MsgCategoryEnum.BasePatientMemberRelation);
    remoteRabbitMqServiceFeign.sendMessage(messageModel);
  }

  /**
   * 会员操作消息 参数模板
   *
   * @param id 操作LogId
   * @param operateType 操作类型
   * @param type 会员类型
   * @param operationType Log类型
   */
  public void sendMemberLogMessages(
      Integer id, Integer operateType, Integer type, Integer operationType) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("id", id);
    paramMap.put("type", type);
    paramMap.put("operationType", operationType);
    remoteRabbitMqServiceFeign.sendMessage(
        paramMap, operateType, MsgCategoryEnum.BasePatientMemberOccurLog);
  }

  /**
   * 开卡
   *
   * @param openCardModel 开卡Model
   */
  public void addMemberCard(OpenCardModel openCardModel) {
    PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
    patientMemberInfo.setPatientId(openCardModel.getPatientId());
    patientMemberInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientMemberInfo.setMemberTypeId(openCardModel.getMemberTypeId());
    patientMemberInfo.setCardNumber(
        this.generateCardNumber("H", "patient_member_info", "card_number"));
    patientMemberInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientMemberInfo.setCrtName(BaseContextHandler.getName());
    this.patientMemberInfoMapper.insertSelective(patientMemberInfo);
    this.cardLog(patientMemberInfo, "开卡", "");
    remoteRabbitMqServiceFeign.sendMessage(
        patientMemberInfo.getId(), 0, 0, MsgCategoryEnum.BasePatientMember);
  }

  /**
   * 生产会员卡号
   *
   * @param mark 会员号标识 H：会员卡，Y：预付款
   * @param tableName 数据库表名
   * @param column 表中列的名称
   * @return String 卡号
   */
  public String generateCardNumber(String mark, String tableName, String column) {
    String number =
        this.mapper.generateCardNumber(
            Integer.parseInt(BaseContextHandler.getOrgId()), tableName, column);
    String suffix = String.format("%06d", Integer.parseInt(number) + 1);
    // 获取门诊简称
    OrganizationInfo organizationInfo =
        this.remoteSystemServiceFeign.findOrgInfoByOrgId(
            Integer.parseInt(BaseContextHandler.getOrgId()));
    if (organizationInfo != null) {
      return mark + organizationInfo.getClinicNumber() + suffix;
    }
    return null;
  }

  /**
   * 根据关联类型删除关系
   *
   * @param cardRelationForm 会员卡关系删除Form
   */
  public void deleteRelationById(CardRelationForm cardRelationForm) {
    // 权限绑定 单项删除
    if (cardRelationForm.getBindType() == 0) {
      this.patientMemberRelationMapper.deleteByPrimaryKey(cardRelationForm.getId());
      remoteRabbitMqServiceFeign.sendMessage(
          cardRelationForm.getId(), 0, 2, MsgCategoryEnum.BasePatientMemberRelation);
    }
    // 共享值绑定 双项删除
    if (cardRelationForm.getBindType() == 1) {
      PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
      patientMemberRelation.setId(cardRelationForm.getId());
      PatientMemberRelation memberRelation =
          this.patientMemberRelationMapper.selectOne(patientMemberRelation);
      if (memberRelation != null) {
        Integer relationId =
            patientMemberRelationMapper.selectMemberRelationId(
                memberRelation.getSecondaryCardId(), memberRelation.getMasterCardId());
        if (relationId != null) {
          this.patientMemberRelationMapper.deleteMemberRelation(
              memberRelation.getSecondaryCardId(), memberRelation.getMasterCardId());
          // 发送会员关联删除消息
          remoteRabbitMqServiceFeign.sendMessage(
              relationId, 0, 2, MsgCategoryEnum.BasePatientMemberRelation);
        }
        this.patientMemberRelationMapper.delete(memberRelation);
        // 发送会员关联删除消息
        sendMemberRelationMessages(memberRelation.getId(), 2);
      }
    }
  }

  /**
   * 开卡日志
   *
   * @param patientMemberInfo 患者会员信息
   * @param operationType 标识：开卡 or 变更
   * @param isUpd 是否修改
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

    patientMemberChangeLog.setPatientId(patientMemberInfo.getPatientId());
    patientMemberChangeLog.setMemberTypeId(patientMemberInfo.getMemberTypeId());
    patientMemberChangeLog.setOrgId(patientMemberInfo.getOrgId());
    // 获取门诊简称
    OrganizationInfo organizationInfo =
        this.remoteSystemServiceFeign.findOrgInfoByOrgId(patientMemberInfo.getOrgId());
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
    PatientMemberInfo patientMember =
        this.patientMemberInfoMapper.selectOneByCardNumber(form.getCardNumber());
    patientMember.setMemberTypeId(form.getMemberTypeId());
    patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientMember.setUpdName(BaseContextHandler.getName());
    patientMember.setUpdTime(new Date());
    patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    this.mapper.updateByPrimaryKeySelective(patientMember);
    this.cardLog(patientMember, "变更", "更新");
    remoteRabbitMqServiceFeign.sendMessage(
        patientMember.getId(), 0, 1, MsgCategoryEnum.BasePatientMember);
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
  public void recharge(MemberRechargeModel model) {
    // 查询会员余额 余额增加
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectCardNumber(model.getMemberId(), model.getPatientId());
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
      memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
      sendMemberLogMessages(memberRechargeRecord.getId(), 0, 0, 1);
      // 添加会员卡充值收费记录
      if (StringHelper.isNotNull(model.getAccountedWayModel())) {
          MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
          BeanUtils.copyProperties(model.getAccountedWayModel(), memberRechargeTollRecord);
          memberRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
          memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord.getId());
          memberRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
          memberRechargeTollRecord.setCrtName(BaseContextHandler.getName());
          memberRechargeTollRecordMapper.insertSelective(memberRechargeTollRecord);
        }
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
    form.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    List<RechargeRecordVo> resultList = memberRechargeRecordMapper.RechargeRecord(form);
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
            AccountItem accountItem =
                remoteSystemServiceFeign.findAccountItemById(
                    memberRechargeTollRecord.getPaymentId());
            if (accountItem != null) {
              labels.append(accountItem.getName());
            }
          }
        }
        rechargeRecordVo.setPayment(labels.toString());
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 退费
   *
   * @param model 会员卡退费Model
   */
  public ResponseResult refund(MemberReturnRecordModel model) {
    // 查询会员余额 退减余额和赠金
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectCardNumber(model.getMemberId(), model.getPatientId());
    if (patientMemberInfo != null) {
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
      memberReturnRecordMapper.insertSelective(memberReturnRecord);
      sendMemberLogMessages(memberReturnRecord.getId(), 0, 0, 3);
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
   * 消费
   *
   * @param model 消费记录Model
   * @return ResponseResult
   */
  public ResponseResult expend(MemberExpendRecordModel model) {
    PatientMemberInfo patientMemberInfo =
        patientMemberInfoMapper.selectCardNumber(model.getMemberId(), model.getPatientId());
    if (patientMemberInfo != null) {
      if (model.getType() <= 0) {
        if (model.getPrincipalAmount() != null){
          patientMemberInfo.setPrincipalAmount(
                  patientMemberInfo.getPrincipalAmount().add(model.getPrincipalAmount()));
        }
        if (model.getBonusAmount() != null){
          patientMemberInfo.setBonusAmount(
                  patientMemberInfo.getBonusAmount().add(model.getBonusAmount()));
        }
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
        BeanUtils.copyProperties(model, memberExpendRecord);
        // 撤销本金
        memberExpendRecord.setExpendPrincipal(model.getPrincipalAmount());
        // 撤销赠金
        memberExpendRecord.setExpendGift(model.getBonusAmount());
        memberExpendRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberExpendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberExpendRecord.setCrtName(BaseContextHandler.getName());
        memberExpendRecord.setCurrentPrincipal(patientMemberInfo.getPrincipalAmount());
        memberExpendRecord.setCurrentBonus(patientMemberInfo.getBonusAmount());
        memberExpendRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberExpendRecord.setUpdName(BaseContextHandler.getName());
        memberExpendRecordMapper.insertSelective(memberExpendRecord);
        // 发送会员卡撤销收费消息
        sendMemberLogMessages(memberExpendRecord.getId(), 0, 0, 4);
        return ResponseUtil.success();
      }
      BigDecimal num =
          patientMemberInfo.getPrincipalAmount().add(patientMemberInfo.getBonusAmount());
      // 如果本金+赠金 小于 消费金额
      if (num.compareTo(model.getExpendTotal()) < 0) {
        return ResponseUtil.fail(
            OperationCodeConstants.BALANCE_INSUFFICIENT, "会员卡余额不足", patientMemberInfo);
      }
      spending(model, patientMemberInfo);
    } else {
      return ResponseUtil.fail(
          OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到会员卡", patientMemberInfo);
    }
    return ResponseUtil.success();
  }

  /**
   * 消费
   *
   * @param model 消费记录Model
   * @param patientMemberInfo 会员卡信息
   */
  public void spending(MemberExpendRecordModel model, PatientMemberInfo patientMemberInfo) {
    // 消费本金
    BigDecimal costPrincipal;
    // 消费赠金
    BigDecimal costBonus;
    // 账户本金
    BigDecimal principalAmount;
    // 账户赠金
    BigDecimal bonusAmount;
    // 创建消费记录对象
    MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
    BeanUtils.copyProperties(model, memberExpendRecord);
    // 会员卡余额 小于 消费金额
    if (patientMemberInfo.getPrincipalAmount().compareTo(model.getExpendTotal()) < 0) {
      // 小于的情况下 依然先用本金去抵扣消费金额
      // 获取本金
      principalAmount = patientMemberInfo.getPrincipalAmount();
      // 本金-消费总额
      BigDecimal surplus = patientMemberInfo.getPrincipalAmount().subtract(model.getExpendTotal());
      // 本金已用完
      patientMemberInfo.setPrincipalAmount(new BigDecimal(0));
      // 获取消费本金
      memberExpendRecord.setExpendPrincipal(principalAmount);
      // 获取赠金
      bonusAmount = patientMemberInfo.getBonusAmount();
      // 用赠金去抵扣
      patientMemberInfo.setBonusAmount(patientMemberInfo.getBonusAmount().add(surplus));
      // 原账户赠金-抵扣后赠金余额 = 用了多少赠金
      costBonus = bonusAmount.subtract(patientMemberInfo.getBonusAmount());
      // 获取消费赠金
      memberExpendRecord.setExpendGift(costBonus);
    } else {
      principalAmount = patientMemberInfo.getPrincipalAmount();
      patientMemberInfo.setPrincipalAmount(
          patientMemberInfo.getPrincipalAmount().subtract(model.getExpendTotal()));
      costPrincipal = principalAmount.subtract(patientMemberInfo.getPrincipalAmount()); // 消费金额
      memberExpendRecord.setExpendPrincipal(costPrincipal); // 获取消费本金
    }
    patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
    // 添加消费记录
    memberExpendRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    memberExpendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    memberExpendRecord.setCrtName(BaseContextHandler.getName());
    memberExpendRecord.setCurrentPrincipal(patientMemberInfo.getPrincipalAmount());
    memberExpendRecord.setCurrentBonus(patientMemberInfo.getBonusAmount());
    memberExpendRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    memberExpendRecord.setUpdName(BaseContextHandler.getName());
    memberExpendRecordMapper.insertSelective(memberExpendRecord);
    // 发送预付款消费消息
    sendMemberLogMessages(memberExpendRecord.getId(), 0, 0, 2);
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
    return new PageInfo<>(resultList);
  }

  /**
   * 查询会员卡绑定信息
   *
   * @param form 查询患者会员信息form
   * @return List<MemberInfoVo>
   */
  public MemberInfoVo findMemberInfo(PatientMemberInfoQueryForm form) {
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
        if (memberType != null) {
          secondaryMemberInfoVo.setMemberCardName(memberType.getName());
        }
      }
    }
    memberInfoVo.setSecondaryMemberInfoVos(secondaryMemberInfoVos);
    return memberInfoVo;
  }

  /**
   * 预付款付款余额查询
   *
   * @param id
   * @return PatientPrepaymentBalanceVo
   */
  public PatientMemberBalanceVo balancePayment(Integer id) {
    MemberBaseInfoVo memberBaseInfo = patientMemberInfoMapper.findMemberBaseInfo(id);
    if (null != memberBaseInfo) {
      PatientMemberBalanceVo patientMemberBalanceVo = new PatientMemberBalanceVo();
      patientMemberBalanceVo.setMemberBaseInfoVo(memberBaseInfo);

      List<MemberBaseInfoVo> memberBaseInfoVoList =
          patientMemberInfoMapper.selectMemberRelationByMasterPatientId(id);
      if (StringHelper.isNotNull(memberBaseInfoVoList)) {
        patientMemberBalanceVo.setMemberBaseInfoVoList(memberBaseInfoVoList);
      }
      return patientMemberBalanceVo;
    }
    return null;
  }
}
