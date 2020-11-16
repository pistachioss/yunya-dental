package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PaymentRecordDetailQuery;
import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.feign.report.enums.MsgCategoryEnum;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunya.framework.common.constant.BusinessConstants.*;

/**
 * 简单介绍:</br> 患者预付款
 *
 * @author: WY
 * @date 2020/7/31 9:31
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientPrepaymentRelationBiz
    extends BaseBiz<PatientPrepaymentRelationMapper, PatientPrepaymentRelation> {

  /** 注入预付款关联Mapper */
  @Autowired PatientPrepaymentRelationMapper patientPrepaymentRelationMapper;

  /** 注入付款基本信息Mapper */
  @Autowired PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  /** 注入预付款充值记录Mapper */
  @Autowired PrepaidRechargeTollRecordMapper prepaidRechargeTollRecordMapper;

  /** 注入预付款充值记录明细Mapper */
  @Autowired PrepaidRechargeRecordMapper prepaidRechargeRecordMapper;

  /** 注入患者信息Mapper */
  @Autowired PatientBaseInfoMapper patientBaseInfoMapper;

  /** 注入系统服务 */
  @Autowired RemoteSystemServiceFeign remoteSystemServiceFeign;

  /** 注入服务 */
  @Autowired RemoteDiscountFeign remoteDiscountFeign;

  /** 注入预付款退款信息Mapper */
  @Autowired PrepaidReturnRecordMapper prepaidReturnRecordMapper;

  /** 注入预付款消费记录Mapper */
  @Autowired PrepaidExpendRecordMapper prepaidExpendRecordMapper;

  /** 注入服务 */
  @Autowired private RemoteRabbitMqServiceFeign remoteRabbitMqServiceFeign;
  /** 注入会员卡消费记录Mapper */
  @Autowired private MemberExpendRecordMapper memberExpendRecordMapper;

  /**
   * 患者预付款基本信息查询
   *
   * @return PatientPrepaymentRelationVo
   */
  public PatientPrepaymentsInfoVo findPrepaymentInfo(Integer id) {
    return patientPrepaymentsInfoMapper.findPrepaymentInfo(id);
  }

  /**
   * 预付款关联
   *
   * @param model 新增关联
   * @return ResponseResult
   */
  public ResponseResult addPrepaymentLink(PatientPrepaymentRelationModel model) {
    if (model.getMasterCardId().equals(model.getSecondaryCardId())) {
      return ResponseUtil.fail(OperationCodeConstants.OBJECT_EDIT_FAIL, "副卡人不能为患者本人", "");
    }
    PatientPrepaymentRelation patientPrepaymentRelation =
        this.patientPrepaymentRelationMapper.findBindingRelation(model);
    if (patientPrepaymentRelation != null) {
      return ResponseUtil.error("该副卡人已存在,不能重复绑定！", patientPrepaymentRelation);
    }
    PatientPrepaymentRelation patientPrepaymentRelationyi = new PatientPrepaymentRelation();
    BeanUtils.copyProperties(model, patientPrepaymentRelationyi);
    patientPrepaymentRelationyi.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientPrepaymentRelationyi.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientPrepaymentRelationyi.setCrtName(BaseContextHandler.getName());
    patientPrepaymentRelationMapper.insertSelective(patientPrepaymentRelationyi);
    // 发送预付款关联消息
    remoteRabbitMqServiceFeign.sendMessage(
        patientPrepaymentRelationyi.getId(), 1, 0, MsgCategoryEnum.BasePatientMemberRelation);
    PatientPrepaymentRelation patientPrepaymentRelationer = new PatientPrepaymentRelation();
    patientPrepaymentRelationer.setMasterCardId(model.getSecondaryCardId());
    patientPrepaymentRelationer.setSecondaryCardId(model.getMasterCardId());
    patientPrepaymentRelationer.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    patientPrepaymentRelationer.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    patientPrepaymentRelationer.setCrtName(BaseContextHandler.getName());
    patientPrepaymentRelationMapper.insertSelective(patientPrepaymentRelationer);
    // 发送预付款关联消息
    sendPrepaidRelationMessages(patientPrepaymentRelationer.getId(), 0);
    return ResponseUtil.success();
  }

  /**
   * 预付款关联消息 参数模板
   *
   * @param id 操作
   * @param OperateType 操作类型
   */
  public void sendPrepaidRelationMessages(Integer id, Integer OperateType) {
    MessageModel messageModel = new MessageModel();
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id", id);
    map.put("type", 1);
    messageModel.setParamMap(map);
    messageModel.setOperateType(OperateType);
    messageModel.setMsgCategoryEnum(MsgCategoryEnum.BasePatientMemberRelation);
    remoteRabbitMqServiceFeign.sendMessage(messageModel);
  }

  /**
   * 预付款操作消息 参数模板
   *
   * @param id 操作LogId
   * @param operateType 操作类型
   * @param type 会员类型
   * @param operationType Log类型
   */
  public void sendPrepaidLogMessages(
      Integer id, Integer operateType, Integer type, Integer operationType) {
    Map<String, Object> paramMap = new HashMap<String, Object>();
    paramMap.put("id", id);
    paramMap.put("type", type);
    paramMap.put("operationType", operationType);
    remoteRabbitMqServiceFeign.sendMessage(
        paramMap, operateType, MsgCategoryEnum.BasePatientMemberOccurLog);
  }

  /**
   * 查询预付款关联
   *
   * @param id 卡主本人id
   * @return List<PatientPrepaymetRelation>
   */
  public List<PatientPrepaymentRelationVo> findPrepaymentLink(Integer id) {
    List<PatientPrepaymentRelationVo> prepaymentLinkList =
        patientPrepaymentRelationMapper.findPrepaymentLinkList(id);
    if (!StringHelper.isEmpty(prepaymentLinkList)) {
      for (PatientPrepaymentRelationVo patientPrepaymentRelationVo : prepaymentLinkList) {
        PatientBaseInfo patientBaseInfo =
            patientBaseInfoMapper.selectByPrimaryKey(
                patientPrepaymentRelationVo.getSecondaryCardId());
        if (patientBaseInfo != null) {
          patientPrepaymentRelationVo.setSecondaryCardName(patientBaseInfo.getName());
        }
      }
    }
    return prepaymentLinkList;
  }

  /**
   * 删除预付款关联(双向删除)
   *
   * @param id 关联关系id
   */
  public void deletePrepaymentLink(Integer id) {
    PatientPrepaymentRelation patientPrepaymentRelation =
        patientPrepaymentRelationMapper.selectByPrimaryKey(id);
    if (patientPrepaymentRelation != null) {
      int prepaymentRelationId =
          patientPrepaymentRelationMapper.selectPrepaymentRelationId(patientPrepaymentRelation);
      patientPrepaymentRelationMapper.deletePrepaymentRelation(patientPrepaymentRelation);
      // 发送预付款删除消息
      remoteRabbitMqServiceFeign.sendMessage(
          prepaymentRelationId, 1, 2, MsgCategoryEnum.BasePatientMemberRelation);
      patientPrepaymentRelationMapper.delete(patientPrepaymentRelation);
      // 发送预付款删除消息
      sendPrepaidRelationMessages(patientPrepaymentRelation.getId(), 2);
    }
  }

  /**
   * 充值
   *
   * @param model
   */
  public ResponseResult recharge(PrepaidRechargeModel model) {
    // 查询预付款余额 增加余额
    PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidCard());
    if (patientPrepaymentsInfo != null) {
      BigDecimal rechargePrincipal = model.getRechargePrincipal();
      if (rechargePrincipal == null) {
        rechargePrincipal = new BigDecimal(0);
      }
      BigDecimal rechargeBonus = model.getRechargeBonus();
      if (rechargeBonus == null) {
        rechargeBonus = new BigDecimal(0);
      }
      patientPrepaymentsInfo.setPrepaymentPrincipal(
          patientPrepaymentsInfo.getPrepaymentPrincipal().add(rechargePrincipal));
      patientPrepaymentsInfo.setPrepaymentBonus(
          patientPrepaymentsInfo.getPrepaymentBonus().add(rechargeBonus));
      patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);

      // 添加预付款充值记录
      PrepaidRechargeRecord prepaidRechargeRecord = new PrepaidRechargeRecord();
      BeanUtils.copyProperties(model, prepaidRechargeRecord);
      prepaidRechargeRecord.setPrepaidId(model.getPrepaidCard());
      prepaidRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      prepaidRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidRechargeRecord.setCrtName(BaseContextHandler.getName());
      prepaidRechargeRecord.setCurrentRechargePrincipal(
          patientPrepaymentsInfo.getPrepaymentPrincipal());
      prepaidRechargeRecord.setCurrentRechargeBonus(patientPrepaymentsInfo.getPrepaymentBonus());
      prepaidRechargeRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidRechargeRecord.setUpdName(BaseContextHandler.getName());
      prepaidRechargeRecord.setRemarks(model.getPrepaidRechargeTollRecordModel().getRemarks());
      prepaidRechargeRecordMapper.insertSelective(prepaidRechargeRecord);

      // 添加预付款充值收费记录
      PrepaidRechargeTollRecord prepaidRechargeTollRecord = new PrepaidRechargeTollRecord();
      BeanUtils.copyProperties(
          model.getPrepaidRechargeTollRecordModel(), prepaidRechargeTollRecord);
      if (prepaidRechargeRecord.getRechargeBonus() == null){
        prepaidRechargeRecord.setRechargeBonus(new BigDecimal(0));
      }
      prepaidRechargeTollRecord.setCreditAmount(prepaidRechargeRecord.getRechargePrincipal().add(prepaidRechargeRecord.getRechargeBonus()));
      prepaidRechargeTollRecord.setRechargeRecordId(prepaidRechargeRecord.getId());
      prepaidRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      prepaidRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidRechargeTollRecord.setCrtName(BaseContextHandler.getName());
      prepaidRechargeTollRecordMapper.insertSelective(prepaidRechargeTollRecord);
      if (patientPrepaymentsInfo.getPatientId() != null && model.getCardId() != null){
        OwnCardActiveForm ownCardActiveForm = new OwnCardActiveForm();
        ownCardActiveForm.setCardId(model.getCardId());
        ResponseResult result = remoteDiscountFeign.ownActiveCard(patientPrepaymentsInfo.getPatientId(), ownCardActiveForm);
        if (!result.getStatus().equals(ZERO)) {
          return result;
        }
      }
      // 发送消息 预付款充值
      sendPrepaidLogMessages(prepaidRechargeRecord.getId(), 0, 1, 1);
    }
    return ResponseUtil.fail(OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到预付款记录", "");
  }

  /**
   * 充值记录
   *
   * @param form 充值记录queryForm
   * @return PageInfo<PrepaidRechargeRecordVo>
   */
  public PageInfo<PrepaidRechargeRecordVo> rechargeRecord(PrepaidRechargeRecordQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    form.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    List<PrepaidRechargeRecordVo> resultList = prepaidRechargeRecordMapper.RechargeRecord(form);
    if (!StringHelper.isEmpty(resultList)) {
      for (PrepaidRechargeRecordVo prepaidRechargeRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(
                prepaidRechargeRecordVo.getOrgId());
        if (organizationInfo != null) {
          prepaidRechargeRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
        if (prepaidRechargeRecordVo.getPaymentId() != null){
          AccountItem accountItem =
                  remoteSystemServiceFeign.findAccountItemById(prepaidRechargeRecordVo.getPaymentId());
          if (accountItem != null) {
            // 获取支付方式名称
            prepaidRechargeRecordVo.setPaymentName(accountItem.getName());
          }
        }
      }

    }
    return new PageInfo<>(resultList);
  }
  /**
   * 预付款退费
   *
   * @param model 预付款退费
   */
  public void refund(PrepaidMeturnRecordModel model) {
    // 查询会员余额 退减余额
    PatientPrepaymentsInfo patientPrepaymentsInfo =
        patientPrepaymentsInfoMapper.selectOneByPrepaymentNumberAndPatientId(
            model.getPrepaidId(), model.getPatientId());
    if (patientPrepaymentsInfo != null) {
      BigDecimal prepaymentPrincipal = patientPrepaymentsInfo.getPrepaymentPrincipal();
      BigDecimal returnPrincipalAmount = model.getReturnPrincipalAmount();
      BigDecimal principalSubtract = prepaymentPrincipal.subtract(returnPrincipalAmount);
      if (principalSubtract.intValue() < 0) {
        throw new ClientServiceException("预付款本金余额不足",OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
      patientPrepaymentsInfo.setPrepaymentPrincipal(principalSubtract);
      BigDecimal prepaymentBonus = patientPrepaymentsInfo.getPrepaymentBonus();
      BigDecimal returnGiftAmount = model.getReturnGiftAmount();
      BigDecimal bonusSubtract = prepaymentBonus.subtract(returnGiftAmount);
      if (bonusSubtract.intValue() < 0) {
        throw new ClientServiceException("预付款赠金余额不足",OperationCodeConstants.OBJECT_EDIT_FAIL);
      }
      patientPrepaymentsInfo.setPrepaymentBonus(bonusSubtract);
      patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
      // 添加会员卡退费记录
      PrepaidReturnRecord prepaidReturnRecord = new PrepaidReturnRecord();
      BeanUtils.copyProperties(model, prepaidReturnRecord);
      prepaidReturnRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
      AccountItem accountItem =
          remoteSystemServiceFeign.findAccountItemById(prepaidReturnRecord.getReturnWayId());
      if (accountItem != null) {
        prepaidReturnRecord.setReturnWayType(accountItem.getName()); // 获取退费方式类型名称
      }
      prepaidReturnRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidReturnRecord.setCrtName(BaseContextHandler.getName());
      prepaidReturnRecord.setCurrentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
      prepaidReturnRecord.setCurrentBonus(patientPrepaymentsInfo.getPrepaymentBonus());
      prepaidReturnRecord.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
      prepaidReturnRecord.setUpdName(BaseContextHandler.getName());
      prepaidReturnRecordMapper.insertSelective(prepaidReturnRecord);

      // 发送消息 退费
      sendPrepaidLogMessages(prepaidReturnRecord.getId(), 0, 1, 3);
    }
  }

  /**
   * 退费记录列表
   *
   * @param queryForm 查询条件
   * @return MemberReturnRecordVo
   */
  public PageInfo<PrepaidMeturnRecordVo> refundList(PrepaidMeturnRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<PrepaidMeturnRecordVo> resultList = prepaidReturnRecordMapper.refundList(queryForm);
    if (!StringHelper.isEmpty(resultList)) {
      for (PrepaidMeturnRecordVo prepaidMeturnRecordVo : resultList) {
        // 获取门诊简称
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(prepaidMeturnRecordVo.getOrgId());
        if (organizationInfo != null) {
          prepaidMeturnRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
        AccountItem accountItem =
            remoteSystemServiceFeign.findAccountItemById(prepaidMeturnRecordVo.getReturnWayId());
        if (accountItem != null) {
          // 获取支付方式名称
          prepaidMeturnRecordVo.setReturnWayType(accountItem.getName());
        }
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 消费记录
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<PrepaidExpendRecordVo> expendList(PrepaidExpendRecordQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<PrepaidExpendRecordVo> resultList = prepaidExpendRecordMapper.expendList(queryForm);
    if (!StringHelper.isEmpty(resultList)) {
      for (PrepaidExpendRecordVo prepaidExpendRecordVo : resultList) {
        OrganizationInfo organizationInfo =
            remoteSystemServiceFeign.findOrgInfoByOrgId(prepaidExpendRecordVo.getOrgId()); // 获取门诊简称
        if (organizationInfo != null) {
          prepaidExpendRecordVo.setOrgName(organizationInfo.getAbbreviation());
        }
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据账单记录ID和预付款ID查询支付记录详细
   * @param query
   * @return 预付款支付记录
   */
  public PrepaidExpendRecord prePaidPaymentRecordDetail(PaymentRecordDetailQuery query) {
    PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord();
    prepaidExpendRecord.setBillPayRecordId(query.getBillRecordId());
    prepaidExpendRecord.setPrepaidId(query.getCardId());
    return prepaidExpendRecordMapper.selectOne(prepaidExpendRecord);
  }


  /**
   * 预付款消费记录
   *
   * @param model 预付款消费Model
   * @return ResponseResult
   */
  public ResponseResult expend(PrepaidExpendRecordModel model) {
    PatientPrepaymentsInfo patientPrepaymentsInfo =
        patientPrepaymentsInfoMapper.selectOneByPrepaymentNumberAndPatientId(
            model.getPrepaidId(), model.getPatientId());
    if (patientPrepaymentsInfo != null) {
      if (patientPrepaymentsInfo
              .getPrepaymentPrincipal()
              .add(patientPrepaymentsInfo.getPrepaymentBonus())
              .compareTo(model.getExpendTotal())
          < 0) { // 如果本金+赠金 小于 消费金额
        return ResponseUtil.fail(
            OperationCodeConstants.BALANCE_INSUFFICIENT, "预付款余额不足", patientPrepaymentsInfo);
      }
      spending(model, patientPrepaymentsInfo);
    }
    return ResponseUtil.success();
  }

  /**
   * 消费
   *
   * @param model 消费参数
   * @param patientPrepaymentsInfo 预付款信息
   */
  public void spending(
      PrepaidExpendRecordModel model, PatientPrepaymentsInfo patientPrepaymentsInfo) {
    // 消费本金
    BigDecimal expendePrincipal = null;
    // 消费赠金
    BigDecimal expendeBonus = null;
    // 账户本金
    BigDecimal principalAmount = null;
    // 账户赠金
    BigDecimal bonusAmount = null;

    PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord(); // 创建消费记录对象
    BeanUtils.copyProperties(model, prepaidExpendRecord);
    // 会员卡余额 小于 消费金额
    if (patientPrepaymentsInfo.getPrepaymentPrincipal().compareTo(model.getExpendTotal()) < 0) {
      // 小于的情况下 依然先用本金去抵扣消费金额
      // 获取本金
      principalAmount = patientPrepaymentsInfo.getPrepaymentPrincipal();
      // 本金-消费总额
      BigDecimal surplus =
          patientPrepaymentsInfo.getPrepaymentPrincipal().subtract(model.getExpendTotal());
      // 本金已用完
      patientPrepaymentsInfo.setPrepaymentPrincipal(new BigDecimal(0));
      // 获取消费本金
      prepaidExpendRecord.setExpendPrincipal(principalAmount);
      // 获取赠金
      bonusAmount = patientPrepaymentsInfo.getPrepaymentBonus();
      // 用赠金去抵扣
      patientPrepaymentsInfo.setPrepaymentBonus(
          patientPrepaymentsInfo.getPrepaymentBonus().add(surplus));
      // 原账户赠金-抵扣后赠金余额 = 用了多少赠金
      expendeBonus = bonusAmount.subtract(patientPrepaymentsInfo.getPrepaymentBonus());
      // 获取消费赠金
      prepaidExpendRecord.setExpendGift(expendeBonus);
    } else {
      principalAmount = patientPrepaymentsInfo.getPrepaymentPrincipal();
      patientPrepaymentsInfo.setPrepaymentPrincipal(
          patientPrepaymentsInfo.getPrepaymentPrincipal().subtract(model.getExpendTotal()));
      // 消费金额
      expendePrincipal = principalAmount.subtract(patientPrepaymentsInfo.getPrepaymentPrincipal());
      // 获取消费本金
      prepaidExpendRecord.setExpendPrincipal(expendePrincipal);
    }
    patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
    // 添加消费记录
    prepaidExpendRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
    prepaidExpendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
    prepaidExpendRecord.setCrtName(BaseContextHandler.getName());
    prepaidExpendRecord.setCurrentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
    prepaidExpendRecord.setCurrentBonus(patientPrepaymentsInfo.getPrepaymentBonus());
    prepaidExpendRecord.setCurrentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
    prepaidExpendRecord.setCurrentBonus(patientPrepaymentsInfo.getPrepaymentBonus());
    prepaidExpendRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
    prepaidExpendRecord.setUpdName(BaseContextHandler.getName());
    prepaidExpendRecordMapper.insertSelective(prepaidExpendRecord);
    // 发送消息 预付款消费
    sendPrepaidLogMessages(prepaidExpendRecord.getId(), 0, 1, 2);
  }

  /**
   * 预付款付款余额查询
   *
   * @param id
   * @return PatientPrepaymentBalanceVo
   */
  public PatientPrepaymentBalanceVo balancePayment(Integer id) {
    PatientPrepaymentsInfoVo prepaymentInfo = patientPrepaymentsInfoMapper.findPrepaymentInfo(id);
    if (null != prepaymentInfo) {
      PatientPrepaymentBalanceVo patientPrepaymentBalanceVo = new PatientPrepaymentBalanceVo();
      patientPrepaymentBalanceVo.setPatientPrepaymentsInfoVo(prepaymentInfo);

      List<PatientPrepaymentsInfoVo> prepaymentsInfoVoList =
          patientPrepaymentsInfoMapper.selectPrepaymentRelationByMasterPatientId(id);
      if (StringHelper.isNotNull(prepaymentsInfoVoList)) {
        patientPrepaymentBalanceVo.setPrepaymentsInfoVoList(prepaymentsInfoVoList);
      }
      return patientPrepaymentBalanceVo;
    }
    return null;
  }

  /**
   * 账单退费
   * @param model 退费model
   */
    public void billRefund(PrepaidBillRechargeModel model) {
      PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidId());
      if (patientPrepaymentsInfo != null){

        if (model.getRechargePrincipal() != null){
          patientPrepaymentsInfo.setPrepaymentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal().add(model.getRechargePrincipal()));
        }
        if (model.getRechargeBonus() != null){
          patientPrepaymentsInfo.setPrepaymentBonus(patientPrepaymentsInfo.getPrepaymentBonus().add(model.getRechargeBonus()));
        }
        patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
        PrepaidRechargeRecord prepaidRechargeRecord = new PrepaidRechargeRecord();
        BeanUtils.copyProperties(model,prepaidRechargeRecord);
        prepaidRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        prepaidRechargeRecord.setType(2);
        prepaidRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeRecord.setCrtName(BaseContextHandler.getName());
        prepaidRechargeRecord.setCurrentRechargePrincipal(prepaidRechargeRecord.getRechargePrincipal());
        prepaidRechargeRecord.setCurrentRechargeBonus(prepaidRechargeRecord.getRechargeBonus());
        prepaidRechargeRecordMapper.insertSelective(prepaidRechargeRecord);
        // 发送消息 撤销收费
        sendPrepaidLogMessages(prepaidRechargeRecord.getId(), 0, 1, 5);
      }
    }

  /**
   * 预付款撤销收费
   * @param model 撤销model
   */
  public ResponseResult revocationFee(PrepaidRevocationFeeModel model) {
    PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord();
    prepaidExpendRecord.setPrepaidId(model.getPrepaidCard());
    prepaidExpendRecord.setBillPayRecordId(model.getBillPayRecordId());
    prepaidExpendRecord.setInservice(true);
    PrepaidExpendRecord prepaidExpend = prepaidExpendRecordMapper.selectOne(prepaidExpendRecord);
    if (prepaidExpend != null){
      prepaidExpend.setInservice(false);
      prepaidExpendRecordMapper.updateByPrimaryKeySelective(prepaidExpend);

      PatientPrepaymentsInfo patientPrepaymentsInfo =
              patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidCard());
      if (patientPrepaymentsInfo != null) {
        // 撤销消费
        patientPrepaymentsInfo.setPrepaymentPrincipal(
                patientPrepaymentsInfo.getPrepaymentPrincipal().add(prepaidExpend.getExpendPrincipal()));
        patientPrepaymentsInfo.setPrepaymentBonus(
                patientPrepaymentsInfo.getPrepaymentBonus().add(prepaidExpend.getExpendGift()));
        patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
        // 创建消费记录对象
        PrepaidRechargeRecord prepaidRechargeRecord = new PrepaidRechargeRecord();
        BeanUtils.copyProperties(model, prepaidRechargeRecord);
        // 撤销本金
        prepaidRechargeRecord.setRechargePrincipal(prepaidExpend.getExpendPrincipal());
        // 撤销赠金
        prepaidRechargeRecord.setRechargeBonus(prepaidExpend.getExpendGift());
        prepaidRechargeRecord.setInservice(false);
        prepaidRechargeRecord.setType(1);
        prepaidRechargeRecord.setPrepaidId(model.getPrepaidCard());
        prepaidRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        prepaidRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeRecord.setCrtName(BaseContextHandler.getName());
        prepaidRechargeRecord.setCurrentRechargePrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal());
        prepaidRechargeRecord.setCurrentRechargeBonus(patientPrepaymentsInfo.getPrepaymentBonus());
        prepaidRechargeRecord.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeRecord.setUpdName(BaseContextHandler.getName());
        prepaidRechargeRecordMapper.insertSelective(prepaidRechargeRecord);
        // 发送消息 撤销收费
        sendPrepaidLogMessages(prepaidRechargeRecord.getId(), 0, 1, 4);
        return ResponseUtil.success();
      }else {
        return ResponseUtil.fail(OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到预付款", patientPrepaymentsInfo);
      }
    }
    return ResponseUtil.fail(OperationCodeConstants.RETURN_MOBILE_ISNULL, "未查询到预付款消费记录", prepaidExpend);
  }

}
