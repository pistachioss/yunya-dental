package com.yunya.middletable.service.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.PatientDepositAccountTypeEnum;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.*;
import com.yunya.middletable.dao.report.BasePatientMemberMapper;
import com.yunya.middletable.dao.report.BasePatientMemberOccurLogMapper;
import com.yunya.middletable.dao.system.AccountItemMapper;
import com.yunya.models.patient_central.*;
import com.yunya.models.report.BasePatientMember;
import com.yunya.models.report.BasePatientMemberOccurLog;
import com.yunya.models.system.AccountItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

import static com.yunya.framework.common.enums.PatientDepositAccountTypeEnum.MEMBER;

/**
 * 简介:报表服务患者会员/预付款发生操作事件同步
 *
 * @author: WY
 * @date: 2020/10/16 16:23
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BasePatientMemberOccurLogBiz
    extends BaseBiz<BasePatientMemberOccurLogMapper, BasePatientMemberOccurLog> {

  /** 注入会员充值日志对象 */
  @Resource private MemberRechargeRecordMapper memberRechargeRecordMapper;

  /** 注入会员消费日志对象 */
  @Resource private MemberExpendRecordMapper memberExpendRecordMapper;

  /** 注入会员退款日志对象 */
  @Resource private MemberReturnRecordMapper memberReturnRecordMapper;

  /** 注入预付充值作日志对象 */
  @Resource private PrepaidRechargeRecordMapper prepaidRechargeRecordMapper;

  /** 注入预付消费作日志对象 */
  @Resource private PrepaidExpendRecordMapper prepaidExpendRecordMapper;

  /** 注入预付退款作日志对象 */
  @Resource private PrepaidReturnRecordMapper prepaidReturnRecordMapper;

  /** 注入会员卡mapper */
  @Resource private PatientMemberInfoMapper patientMemberInfoMapper;

  /** 注入会员卡充值明细mapper */
  @Resource private MemberRechargeTollRecordMapper memberRechargeTollRecordMapper;

  /** 注入预付款信息mapper */
  @Resource private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  /** 注入系统字典服务 */
  @Resource private AccountItemMapper accountItemMapper;

  /** 注入预付款充值明细Mapper */
  @Resource private PrepaidRechargeTollRecordMapper prepaidRechargeTollRecordMapper;

  @Resource private BasePatientMemberBiz basePatientMemberBiz;
  @Resource private BasePatientMemberMapper basePatientMemberMapper;

  @Resource private PatientTransferRecordMapper patientTransferRecordMapper;

  /**
   * 中间表-会员-预付款 信息操作源头
   *
   * @param msg 消息
   */
  public void operate(MessageModel msg) {
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        addPatientMemberOccurLog(msg);
        break;
      case 1:
        // 操作记录 没有修改接口
        break;
      case 2:
        BasePatientMemberOccurLog memberOccurLog = getMemberOccurLog(msg);
        if (null != memberOccurLog) {
          memberOccurLog.setInservice(false);
          mapper.deleteByPrimaryKey(memberOccurLog);
          BasePatientMember patientMemberInfo = basePatientMemberBiz.getPatientMemberInfo(memberOccurLog.getCardId(), memberOccurLog.getType().intValue());
          if (StringHelper.isNotNull(patientMemberInfo)) {
            basePatientMemberMapper.updateByPrimaryKeySelective(patientMemberInfo);
          }
        }
        break;
      default:
        break;
    }
  }

  /**
   * 添加中间表信息-会员/预付款-转
   *
   * @param msg 消息
   */
  private void addPatientMemberOccurLog(MessageModel msg) {
    // 操作id
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer type = (Integer) msg.getParamMap().get("type");
    Integer operationType = (Integer) msg.getParamMap().get("operationType");
    // 会员卡操作日志
    if (MEMBER.equals(type)) {
      Integer cardId = addMemberOccurLog(id, type, operationType);
      if (cardId != null){
        BasePatientMember patientMemberInfo = basePatientMemberBiz.getPatientMemberInfo(cardId, type);
        if (StringHelper.isNotNull(patientMemberInfo)) {
          basePatientMemberMapper.updateByPrimaryKeySelective(patientMemberInfo);
        }
      }
      if (operationType.equals(20) || operationType.equals(21)){
        Integer id2 = (Integer) msg.getParamMap().get("id2");
        Integer operationType2 = (Integer) msg.getParamMap().get("operationType2");
        Integer cardId2 = addMemberOccurLog(id2, type, operationType2);
        if (cardId2 != null){
          BasePatientMember patientMemberInfo2 = basePatientMemberBiz.getPatientMemberInfo(cardId2, type);
          if (StringHelper.isNotNull(patientMemberInfo2)) {
            basePatientMemberMapper.updateByPrimaryKeySelective(patientMemberInfo2);
          }
        }
      }
    }
    // 预付款操作日志
    if (PatientDepositAccountTypeEnum.isPrepaymentType(type)) {
      Integer cardId = addPrepaymentOccurLog(id, type, operationType);
      if (cardId != null){
        BasePatientMember patientMemberInfo = basePatientMemberBiz.getPatientMemberInfo(cardId, type);
        if (StringHelper.isNotNull(patientMemberInfo)) {
          basePatientMemberMapper.updateByPrimaryKeySelective(patientMemberInfo);
        }
      }
    }
  }

  /**
   * 获取会员操作记录
   *
   * @param msg 消息
   * @return BasePatientMemberOccurLog
   */
  private BasePatientMemberOccurLog getMemberOccurLog(MessageModel msg) {
    Integer type = (Integer) msg.getParamMap().get("type");
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer operationType = (Integer) msg.getParamMap().get("operationType");
    if (MEMBER.equals(type)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog =
          mapper.selectOneByPrimaryKeyAndtype(id, type, operationType);
      if (basePatientMemberOccurLog != null) {
        return basePatientMemberOccurLog;
      }
    }
    if (PatientDepositAccountTypeEnum.isPrepaymentType(type)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog =
          mapper.selectOneByPrimaryKeyAndtype(id, type, operationType);
      if (basePatientMemberOccurLog != null) {
        return basePatientMemberOccurLog;
      }
    }
    return null;
  }

  /**
   * 拉取某段时间内的组织数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullOccurLogData(PullForm form) {
    Integer type = form.getDataType();
    if (MEMBER.equals(type)) {
      // （基础表-充值表）充值
      pullMemberRecharge(form, type,1,0);
      // （基础表-充值表）撤销
      pullMemberRecharge(form, type,4,1);
      // （基础表-充值表）账单退费
      pullMemberRecharge(form, type,5,2);
      // 消费
      pullMemberExpendRecord(form, type, 2);
      // 退费
      pullMemberReturnRecord(form, type,3);
    }
    if (PatientDepositAccountTypeEnum.isPrepaymentType(type)) {
      // （基础表-充值表）充值
      pullPrepaymentRecharge(form, type,1,0);
      // （基础表-充值表）撤销
      pullPrepaymentRecharge(form, type,4,1);
      // （基础表-充值表）账单退费
      pullPrepaymentRecharge(form, type,5,2);
      // 消费
      pullPrepaymentExpendRecord(form, type, 2);
      // 退费
      pullPrepaymentReturn(form, type,3);
    }
  }

  /**
   * 会员-批量拉取-充值记录
   * @param form 条件
   * @param type 会员类型
   * @param occurType 会员log操作类型
   * @param rechargeType 充值表中 充值分类类型
   */
  public void pullMemberRecharge(PullForm form, Integer type, Integer occurType,Integer rechargeType) {
    List<MemberRechargeRecord> memberRechargeRecordList =
        (List<MemberRechargeRecord>) getMemberInfoLog(form, occurType,rechargeType);
    if (StringHelper.isNotEmpty(memberRechargeRecordList)) {
      memberRechargeRecordList.forEach(
          memberRechargeRecord -> {
            Integer id = memberRechargeRecord.getId();
            mapper.deleteByPrimaryKeyAndtype(id, type, occurType);
            BasePatientMemberOccurLog memberOccurLog = getMemberRechargeRecordInfoLog(id, type, occurType);
            if (memberOccurLog != null) {
              mapper.insertSelective(memberOccurLog);
            }
          });
    }
  }

  /**
   * 会员-批量拉取-消费记录
   *
   * @param form 条件
   * @param type 类型
   */
  public void pullMemberExpendRecord(PullForm form, Integer type, Integer occurType) {
    List<MemberExpendRecord> memberExpendRecordList =
        (List<MemberExpendRecord>) getMemberInfoLog(form, occurType,null);
    if (StringHelper.isNotEmpty(memberExpendRecordList)) {
      memberExpendRecordList.forEach(
          memberExpendRecord -> {
            Integer id = memberExpendRecord.getId();
            mapper.deleteByPrimaryKeyAndtype(id, type, occurType);
            BasePatientMemberOccurLog memberOccurLog =
                getMemberExpendAndRevocation(id, type, occurType);
            if (memberOccurLog != null) {
              mapper.insertSelective(memberOccurLog);
            }
          });
    }
  }

  /**
   * 会员-批量拉取-退费记录
   *
   * @param form
   * @param type
   */
  public void pullMemberReturnRecord(PullForm form, Integer type,Integer occurType) {
    List<MemberReturnRecord> memberReturnRecordList =
        (List<MemberReturnRecord>) getMemberInfoLog(form,occurType,null);
    if (StringHelper.isNotEmpty(memberReturnRecordList)) {
      memberReturnRecordList.forEach(
          memberReturnRecord -> {
            Integer id = memberReturnRecord.getId();
            mapper.deleteByPrimaryKeyAndtype(id, type, 3);
            BasePatientMemberOccurLog memberReturnInfoLog = getMemberReturnInfoLog(id, type, 3);
            if (memberReturnInfoLog != null) {
              mapper.insertSelective(memberReturnInfoLog);
            }
          });
    }
  }

  /**
   * 会员-获取操作集合List 1.充值 2.消费 3.退费 4.撤销 5.账单退费
   *
   * @param form 拉取时间和type
   * @return List<MemberRechargeRecord>
   */
  public List<? extends Object> getMemberInfoLog(PullForm form, Integer occurType,Integer rechargeType) {
    if (occurType == 1 || occurType == 4 || occurType == 5) {
      Example emp = new Example(MemberRechargeRecord.class);
      Example example = getRevocationExample(form, emp, rechargeType);
      List<MemberRechargeRecord> memberRechargeRecordList =
          memberRechargeRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberRechargeRecordList)) {
          return memberRechargeRecordList;
      }
      return null;
    }

    if (occurType == 2) {
      Example emp = new Example(MemberExpendRecord.class);
      Example example = getRevocationExample(form, emp, 0);
      List<MemberExpendRecord> memberExpendRecordList =
          memberExpendRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberExpendRecordList)) {
        return memberExpendRecordList;
      }
      return null;
    }

    if (occurType == 3) {
      Example emp = new Example(MemberReturnRecord.class);
      Example example = getExample(form, emp);
      List<MemberReturnRecord> memberRechargeRecordList =
          memberReturnRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberRechargeRecordList)) {
        return memberRechargeRecordList;
      }
      return null;
    }
    return null;
  }

  /**
   * 会员卡-查询转账记录-充值log
   *
   * @param id 充值id
   * @param type 会员类型
   * @param occurType 操作类型
   * @return BasePatientMemberOccurLog
   */
  public BasePatientMemberOccurLog getMemberRechargeRecordInfoLog(
      Integer id, Integer type, Integer occurType) {
    MemberRechargeRecord memberRechargeRecord = memberRechargeRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(memberRechargeRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(memberRechargeRecord.getId());
      // 通过会员卡号 获取会员id
      PatientMemberInfo memberInfo = new PatientMemberInfo();
      memberInfo.setCardNumber(memberRechargeRecord.getMemberId());
      PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOne(memberInfo);
      if (StringHelper.isNotNull(patientMemberInfo)) {
        basePatientMemberOccurLog.setCardId(patientMemberInfo.getId());
        basePatientMemberOccurLog.setPatientId(patientMemberInfo.getPatientId());
      }
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) occurType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(memberRechargeRecord.getRechargePrincipal());
      basePatientMemberOccurLog.setBonusAmount(memberRechargeRecord.getRechargeBonus());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(
          memberRechargeRecord.getCurrentRechargePrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(
          memberRechargeRecord.getCurrentRechargeBonus());
      basePatientMemberOccurLog.setRechargeMethod((byte)0);
      if (occurType == 1){
        MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
        memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord.getId());
        MemberRechargeTollRecord memberRechargeToll =
                memberRechargeTollRecordMapper.selectOne(memberRechargeTollRecord);
        if (StringHelper.isNotNull(memberRechargeToll)) {
          basePatientMemberOccurLog.setPaymentId(memberRechargeToll.getPaymentId());
          AccountItem accountItem =
                  accountItemMapper.selectByPrimaryKey(memberRechargeToll.getPaymentId());
          if (StringHelper.isNotNull(accountItem)) {
            basePatientMemberOccurLog.setPaymentManner(accountItem.getName());
          }
        }
        if (memberRechargeToll.getCreditAmount() != null){
          basePatientMemberOccurLog.setCreditAmount(memberRechargeToll.getCreditAmount());
        }
      } else if (occurType == 20) {
        basePatientMemberOccurLog.setPaymentManner("赠金转账");
      } else if (occurType == 21) {
        basePatientMemberOccurLog.setPaymentManner("赠金充值");
      } else {
        basePatientMemberOccurLog.setBillId(memberRechargeRecord.getOrderRecordId());
      }
      basePatientMemberOccurLog.setOperatorUserId(memberRechargeRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(memberRechargeRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(memberRechargeRecord.getCrtTime());
      basePatientMemberOccurLog.setOrgId(memberRechargeRecord.getOrgId());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /**
   * 会员卡-查询单条转账记录-充值log
   *
   * @param id 充值id
   * @param type 会员类型
   * @param occurType 操作类型
   * @return BasePatientMemberOccurLog
   */
  public BasePatientMemberOccurLog getMemberTransferRecordInfoLog(
      Integer id, Integer type, Integer occurType) {
    PatientTransferRecord entity = patientTransferRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(entity)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(entity.getId());
      // 通过会员卡号 获取会员id
      PatientMemberInfo memberInfo = new PatientMemberInfo();
      memberInfo.setCardNumber(entity.getMainNumber());
      PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOne(memberInfo);
      if (StringHelper.isNotNull(patientMemberInfo)) {
        basePatientMemberOccurLog.setCardId(patientMemberInfo.getId());
        basePatientMemberOccurLog.setPatientId(patientMemberInfo.getPatientId());
        basePatientMemberOccurLog.setCurrentRechargePrincipal(
                patientMemberInfo.getPrincipalAmount());
        basePatientMemberOccurLog.setCurrentRechargeBonus(
                patientMemberInfo.getBonusAmount());
      }
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) occurType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(entity.getPrincipal());
      basePatientMemberOccurLog.setBonusAmount(entity.getBonus());
      basePatientMemberOccurLog.setRechargeMethod((byte)0);
      if (occurType == 1){
        MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
        memberRechargeTollRecord.setRechargeRecordId(entity.getId());
        MemberRechargeTollRecord memberRechargeToll =
                memberRechargeTollRecordMapper.selectOne(memberRechargeTollRecord);
        if (StringHelper.isNotNull(memberRechargeToll)) {
          basePatientMemberOccurLog.setPaymentId(memberRechargeToll.getPaymentId());
          AccountItem accountItem =
                  accountItemMapper.selectByPrimaryKey(memberRechargeToll.getPaymentId());
          if (StringHelper.isNotNull(accountItem)) {
            basePatientMemberOccurLog.setPaymentManner(accountItem.getName());
          }
        }
        if (memberRechargeToll.getCreditAmount() != null){
          basePatientMemberOccurLog.setCreditAmount(memberRechargeToll.getCreditAmount());
        }
      }
      basePatientMemberOccurLog.setOperatorUserId(entity.getCrtId());
      basePatientMemberOccurLog.setRemarks(entity.getRemark());
      basePatientMemberOccurLog.setOccurDate(entity.getCrtTime());
      basePatientMemberOccurLog.setOrgId(entity.getOrgId());
      basePatientMemberOccurLog.setRechargeCardNumber(entity.getMinorNumber());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /**
   * 会员卡-查询单条-消费/撤销
   *
   * @param id 操作id
   * @param occurType 操作类型
   * @param type 会员类型
   * @return BasePatientMemberOccurLog
   */
  public BasePatientMemberOccurLog getMemberExpendAndRevocation(
      Integer id, Integer type, Integer occurType) {
    MemberExpendRecord memberExpendRecord = memberExpendRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(memberExpendRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(memberExpendRecord.getId());
      // 通过会员卡号 获取会员id
      PatientMemberInfo memberInfo = new PatientMemberInfo();
      memberInfo.setCardNumber(memberExpendRecord.getMemberId());
      PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOne(memberInfo);
      if (StringHelper.isNotNull(patientMemberInfo)) {
        basePatientMemberOccurLog.setCardId(patientMemberInfo.getId());
      }
      basePatientMemberOccurLog.setPatientId(memberExpendRecord.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) occurType.intValue());
      if(memberExpendRecord.getTreatmentRecordId() == null) {
        basePatientMemberOccurLog.setOccurType((byte)22);
      }
      basePatientMemberOccurLog.setPrincipalAmount(memberExpendRecord.getExpendPrincipal());
      basePatientMemberOccurLog.setBonusAmount(memberExpendRecord.getExpendGift());
      basePatientMemberOccurLog.setOperatorUserId(memberExpendRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(memberExpendRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(memberExpendRecord.getCrtTime());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(
          memberExpendRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(memberExpendRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(memberExpendRecord.getOrgId());
      basePatientMemberOccurLog.setBillId(memberExpendRecord.getOrderRecordId());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /**
   * 会员卡-查询单条-退费log
   *
   * @param id 退费log id
   * @param type 会员类型
   * @param occurType 操作类型
   * @return BasePatientMemberOccurLog
   */
  public BasePatientMemberOccurLog getMemberReturnInfoLog(
      Integer id, Integer type, Integer occurType) {
    MemberReturnRecord memberReturnRecord = memberReturnRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(memberReturnRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(memberReturnRecord.getId());
      // 通过会员卡号 获取会员id
      PatientMemberInfo memberInfo = new PatientMemberInfo();
      memberInfo.setCardNumber(memberReturnRecord.getMemberId());
      PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectOne(memberInfo);
      if (StringHelper.isNotNull(patientMemberInfo)) {
        basePatientMemberOccurLog.setCardId(patientMemberInfo.getId());
      }
      basePatientMemberOccurLog.setPatientId(memberReturnRecord.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) occurType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(memberReturnRecord.getReturnPrincipalAmount());
      basePatientMemberOccurLog.setBonusAmount(memberReturnRecord.getReturnGiftAmount());
      basePatientMemberOccurLog.setPaymentId(memberReturnRecord.getReturnWayId());
      basePatientMemberOccurLog.setPaymentManner(memberReturnRecord.getReturnWayType());
      basePatientMemberOccurLog.setOperatorUserId(memberReturnRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(memberReturnRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(memberReturnRecord.getCrtTime());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(
          memberReturnRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(memberReturnRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(memberReturnRecord.getOrgId());
      basePatientMemberOccurLog.setCreditAmount(memberReturnRecord.getActualReturnAmount());
      return basePatientMemberOccurLog;
    }

    return null;
  }

  /**
   * 会员卡-中间表统一添加-根据类型添加
   *
   * @param id 操作方式id
   * @param operationType 操作类型(1充值2消费3退款4撤销收费)
   */
  private Integer addMemberOccurLog(Integer id, Integer type, Integer operationType) {
    switch (operationType) {
        // 充值、撤销收费、账单退费、账单返点
      case 1:
      case 4:
      case 5:
      case 6:
      case 7:
      case 20:
      case 21:
        BasePatientMemberOccurLog memberRechargeLog =
            getMemberRechargeRecordInfoLog(id, type, operationType);
        if (StringHelper.isNotNull(memberRechargeLog)) {
          mapper.deleteByPrimaryKey(memberRechargeLog);
          mapper.insertSelective(memberRechargeLog);
          return memberRechargeLog.getCardId();
        }
        break;
        // 消费
      case 2:
        BasePatientMemberOccurLog memberExpendLog =
            getMemberExpendAndRevocation(id, type, operationType);
        if (StringHelper.isNotNull(memberExpendLog)) {
          mapper.deleteByPrimaryKey(memberExpendLog);
          mapper.insertSelective(memberExpendLog);
          return memberExpendLog.getCardId();
        }
        break;
        // 退款
      case 3:
        BasePatientMemberOccurLog memberReturnInfoLog =
            getMemberReturnInfoLog(id, type, operationType);
        if (StringHelper.isNotNull(memberReturnInfoLog)) {
          mapper.deleteByPrimaryKey(memberReturnInfoLog);
          mapper.insertSelective(memberReturnInfoLog);
          return memberReturnInfoLog.getCardId();
        }
        break;
        // 转账-转入、转账转出
      case 8:
      case 9:
        BasePatientMemberOccurLog memberTransferLog =
                getMemberTransferRecordInfoLog(id, type, operationType);
        if (StringHelper.isNotNull(memberTransferLog)) {
          mapper.deleteByPrimaryKey(memberTransferLog);
          mapper.insertSelective(memberTransferLog);
          return memberTransferLog.getCardId();
        }
        break;
      default:
        break;
    }
    return null;
  }

  /**
   * 预付款-批量添加-充值log
   *
   * @param form 拉取时间
   * @param type 会员类型
   */
  private void  pullPrepaymentRecharge(PullForm form, Integer type,Integer occurType,Integer rechargeType) {

    List<PrepaidRechargeRecord> prepaidRechargeRecordList =
        (List<PrepaidRechargeRecord>) getPrepaymentInfoLog(form, occurType,rechargeType);
    if (StringHelper.isNotEmpty(prepaidRechargeRecordList)) {
      prepaidRechargeRecordList.forEach(
          prepaidRechargeRecord -> {
            Integer id = prepaidRechargeRecord.getId();
            mapper.deleteByPrimaryKeyAndtype(id, type, occurType);
            BasePatientMemberOccurLog  memberOccurLog = getPrepaidRechargeRecord(id, type, occurType);
            if (memberOccurLog != null) {
              mapper.insertSelective(memberOccurLog);
            }
          });
    }
  }

  /**
   * 预付款-批量添加-消费/撤销log
   *
   * @param form
   * @param type
   */
  private void pullPrepaymentExpendRecord(PullForm form, Integer type, Integer occurType) {
    List<PrepaidExpendRecord> prepaidExpendRecordList =
        (List<PrepaidExpendRecord>) getPrepaymentInfoLog(form, occurType,null);
    if (StringHelper.isNotEmpty(prepaidExpendRecordList)) {
      prepaidExpendRecordList.forEach(
          prepaidExpendRecord -> {
            Integer id = prepaidExpendRecord.getId();
            mapper.deleteByPrimaryKeyAndtype(id, type, occurType);
            BasePatientMemberOccurLog memberOccurLog =
                getPrepaidExpendAndRevocation(id, type, occurType);
            if (memberOccurLog != null) {
              mapper.insertSelective(memberOccurLog);
            }
          });
    }
  }

  /**
   * 预付款-批量添加-退款log
   *
   * @param form
   * @param type
   */
  private void pullPrepaymentReturn(PullForm form, Integer type,Integer occurType) {
    List<PrepaidReturnRecord> prepaidReturnRecordList =
        (List<PrepaidReturnRecord>) getPrepaymentInfoLog(form, occurType,null);
    if (StringHelper.isNotEmpty(prepaidReturnRecordList)) {
      prepaidReturnRecordList.forEach(
          prepaidReturnRecord -> {
            Integer id = prepaidReturnRecord.getId();
            mapper.deleteByPrimaryKeyAndtype(id, type, 3);
            BasePatientMemberOccurLog memberOccurLog = getPrepaidReturnRecordInfo(id, type, 3);
            if (memberOccurLog != null) {
              mapper.insertSelective(memberOccurLog);
            }
          });
    }
  }

  /**
   * 预付款-获取操作集合List 1.充值 2.消费 3.退费 4.撤销 4.账单退费
   *
   * @param form 拉取时间和type
   * @return List<MemberRechargeRecord>
   */
  public List<? extends Object> getPrepaymentInfoLog(PullForm form, Integer occurType,Integer rechargeType) {
    if (occurType == 1 || occurType == 4 || occurType == 5) {
      Example emp = new Example(PrepaidRechargeRecord.class);
      Example example = getRevocationExample(form, emp, rechargeType);
      List<PrepaidRechargeRecord> prepaidRechargeRecordList =
          prepaidRechargeRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(prepaidRechargeRecordList)) {
        return prepaidRechargeRecordList;
      }
      return null;
    }

    if (occurType == 2) {
      Example emp = new Example(PrepaidExpendRecord.class);
      Example example = getRevocationExample(form, emp, 0);
      List<PrepaidExpendRecord> prepaidExpendRecordList =
          prepaidExpendRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(prepaidExpendRecordList)) {
        return prepaidExpendRecordList;
      }
      return null;
    }

    if (occurType == 3) {
      Example emp = new Example(PrepaidReturnRecord.class);
      Example example = getExample(form, emp);
      List<PrepaidReturnRecord> prepaidReturnRecordList =
          prepaidReturnRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(prepaidReturnRecordList)) {
        return prepaidReturnRecordList;
      }
      return null;
    }
    return null;
  }

  /**
   * 预付款-查询转账记录-充值log
   *
   * @param id 充值记录id
   * @param type 会员类型
   * @param operationType 操作类型
   */
  public BasePatientMemberOccurLog getPrepaidTransferRecord(
      Integer id, Integer type, Integer operationType) {
    PatientTransferRecord transferRecord =
        patientTransferRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(transferRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(transferRecord.getId());
      // 通过会员卡号 获取会员id
      PatientPrepaymentsInfo prepaymentsInfo = new PatientPrepaymentsInfo();
      prepaymentsInfo.setPrepaymentNumber(transferRecord.getMainNumber());
      PatientPrepaymentsInfo patientPrepaymentsInfo =
          patientPrepaymentsInfoMapper.selectOne(prepaymentsInfo);
      if (StringHelper.isNotNull(patientPrepaymentsInfo)) {
        basePatientMemberOccurLog.setCardId(patientPrepaymentsInfo.getId());
        basePatientMemberOccurLog.setCurrentRechargePrincipal(
                patientPrepaymentsInfo.getPrepaymentPrincipal());
        basePatientMemberOccurLog.setCurrentRechargeBonus(
                patientPrepaymentsInfo.getPrepaymentBonus());
        basePatientMemberOccurLog.setPatientId(patientPrepaymentsInfo.getPatientId());
      }
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) operationType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(transferRecord.getPrincipal());
      basePatientMemberOccurLog.setBonusAmount(transferRecord.getBonus());
      if (operationType == 1){
        PrepaidRechargeTollRecord memberRechargeTollRecord = new PrepaidRechargeTollRecord();
        memberRechargeTollRecord.setRechargeRecordId(transferRecord.getId());
        PrepaidRechargeTollRecord prepaidRechargeTollRecord =
                prepaidRechargeTollRecordMapper.selectOne(memberRechargeTollRecord);
        if (StringHelper.isNotNull(prepaidRechargeTollRecord)) {
          basePatientMemberOccurLog.setPaymentId(prepaidRechargeTollRecord.getPaymentId());
          AccountItem accountItem =
                  accountItemMapper.selectByPrimaryKey(prepaidRechargeTollRecord.getPaymentId());
          if (StringHelper.isNotNull(accountItem)) {
            basePatientMemberOccurLog.setPaymentManner(accountItem.getName());
          }
        }
        if (prepaidRechargeTollRecord.getCreditAmount() != null){
          basePatientMemberOccurLog.setCreditAmount(prepaidRechargeTollRecord.getCreditAmount());
        }
      }
      basePatientMemberOccurLog.setOperatorUserId(transferRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(transferRecord.getRemark());
      basePatientMemberOccurLog.setOccurDate(transferRecord.getCrtTime());
      basePatientMemberOccurLog.setRechargeMethod((byte) 1);
      basePatientMemberOccurLog.setRechargeCardNumber(transferRecord.getMinorNumber());
      basePatientMemberOccurLog.setOrgId(transferRecord.getOrgId());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /**
   * 预付款-查询单条-充值log
   *
   * @param id 充值记录id
   * @param type 会员类型
   * @param operationType 操作类型
   */
  public BasePatientMemberOccurLog getPrepaidRechargeRecord(
      Integer id, Integer type, Integer operationType) {
    PrepaidRechargeRecord prepaidRechargeRecord =
        prepaidRechargeRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(prepaidRechargeRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(prepaidRechargeRecord.getId());
      // 通过会员卡号 获取会员id
      PatientPrepaymentsInfo prepaymentsInfo = new PatientPrepaymentsInfo();
      prepaymentsInfo.setPrepaymentNumber(prepaidRechargeRecord.getPrepaidId());
      PatientPrepaymentsInfo patientPrepaymentsInfo =
          patientPrepaymentsInfoMapper.selectOne(prepaymentsInfo);
      if (StringHelper.isNotNull(patientPrepaymentsInfo)) {
        basePatientMemberOccurLog.setCardId(patientPrepaymentsInfo.getId());
      }
      basePatientMemberOccurLog.setPatientId(patientPrepaymentsInfo.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) operationType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(prepaidRechargeRecord.getRechargePrincipal());
      basePatientMemberOccurLog.setBonusAmount(prepaidRechargeRecord.getRechargeBonus());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(
          prepaidRechargeRecord.getCurrentRechargePrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(
          prepaidRechargeRecord.getCurrentRechargeBonus());
      if (operationType == 1){
        PrepaidRechargeTollRecord memberRechargeTollRecord = new PrepaidRechargeTollRecord();
        memberRechargeTollRecord.setRechargeRecordId(prepaidRechargeRecord.getId());
        PrepaidRechargeTollRecord prepaidRechargeTollRecord =
                prepaidRechargeTollRecordMapper.selectOne(memberRechargeTollRecord);
        if (StringHelper.isNotNull(prepaidRechargeTollRecord)) {
          basePatientMemberOccurLog.setPaymentId(prepaidRechargeTollRecord.getPaymentId());
          AccountItem accountItem =
                  accountItemMapper.selectByPrimaryKey(prepaidRechargeTollRecord.getPaymentId());
          if (StringHelper.isNotNull(accountItem)) {
            basePatientMemberOccurLog.setPaymentManner(accountItem.getName());
          }
        }
        if (prepaidRechargeTollRecord.getCreditAmount() != null){
          basePatientMemberOccurLog.setCreditAmount(prepaidRechargeTollRecord.getCreditAmount());
        }
      }else {
          basePatientMemberOccurLog.setBillId(prepaidRechargeRecord.getOrderRecordId());
      }
      basePatientMemberOccurLog.setOperatorUserId(prepaidRechargeRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(prepaidRechargeRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(prepaidRechargeRecord.getCrtTime());
      basePatientMemberOccurLog.setRechargeCardNumber(
          prepaidRechargeRecord.getRechargeCardNumber());
      basePatientMemberOccurLog.setOrgId(prepaidRechargeRecord.getOrgId());
      basePatientMemberOccurLog.setRechargeMethod(prepaidRechargeRecord.getRechargeType());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /*

  * */

  /**
   * 预付款-查询单条-消费/撤销
   *
   * @param id 操作id
   * @param operationType 操作类型
   */
  public BasePatientMemberOccurLog getPrepaidExpendAndRevocation(
      Integer id, Integer type, Integer operationType) {
    PrepaidExpendRecord prepaidExpendRecord = prepaidExpendRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(prepaidExpendRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(prepaidExpendRecord.getId());
      // 通过会员卡号 获取会员id
      PatientPrepaymentsInfo prepaymentsInfo = new PatientPrepaymentsInfo();
      prepaymentsInfo.setPrepaymentNumber(prepaidExpendRecord.getPrepaidId());
      PatientPrepaymentsInfo patientPrepaymentsInfo =
          patientPrepaymentsInfoMapper.selectOne(prepaymentsInfo);
      if (StringHelper.isNotNull(patientPrepaymentsInfo)) {
        basePatientMemberOccurLog.setCardId(patientPrepaymentsInfo.getId());
      }
      basePatientMemberOccurLog.setPatientId(prepaidExpendRecord.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) operationType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(prepaidExpendRecord.getExpendPrincipal());
      basePatientMemberOccurLog.setBonusAmount(prepaidExpendRecord.getExpendGift());
      basePatientMemberOccurLog.setOperatorUserId(prepaidExpendRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(prepaidExpendRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(prepaidExpendRecord.getCrtTime());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(
          prepaidExpendRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(prepaidExpendRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(prepaidExpendRecord.getOrgId());
      basePatientMemberOccurLog.setBillId(prepaidExpendRecord.getOrderRecordId());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /**
   * 预付款-查询单条-退款log
   *
   * @param id 操作id
   * @param type 会员类型
   * @param occurType 操作类型
   * @return BasePatientMemberOccurLog
   */
  public BasePatientMemberOccurLog getPrepaidReturnRecordInfo(
      Integer id, Integer type, Integer occurType) {
    PrepaidReturnRecord prepaidReturnRecord = prepaidReturnRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(prepaidReturnRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(prepaidReturnRecord.getId());
      // 通过预付款卡号 获取会员id
      PatientPrepaymentsInfo prepaymentsInfo = new PatientPrepaymentsInfo();
      prepaymentsInfo.setPrepaymentNumber(prepaidReturnRecord.getPrepaidId());
      PatientPrepaymentsInfo patientPrepaymentsInfo =
          patientPrepaymentsInfoMapper.selectOne(prepaymentsInfo);
      if (StringHelper.isNotNull(patientPrepaymentsInfo)) {
        basePatientMemberOccurLog.setCardId(patientPrepaymentsInfo.getId());
      }
      basePatientMemberOccurLog.setPatientId(prepaidReturnRecord.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) occurType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(prepaidReturnRecord.getReturnPrincipalAmount());
      basePatientMemberOccurLog.setBonusAmount(prepaidReturnRecord.getReturnGiftAmount());
      basePatientMemberOccurLog.setPaymentId(prepaidReturnRecord.getReturnWayId());
      basePatientMemberOccurLog.setPaymentManner(prepaidReturnRecord.getReturnWayType());
      basePatientMemberOccurLog.setOperatorUserId(prepaidReturnRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(prepaidReturnRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(prepaidReturnRecord.getCrtTime());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(
          prepaidReturnRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(prepaidReturnRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(prepaidReturnRecord.getOrgId());
      basePatientMemberOccurLog.setCreditAmount(prepaidReturnRecord.getActualReturnAmount());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /**
   * 预付款-中间表统一添加--根据类型添加
   *
   * @param id 操作方式id
   * @param operationType 操作类型(1充值2消费3退款4撤销收费)
   */
  private Integer addPrepaymentOccurLog(Integer id, Integer type, Integer operationType) {
    switch (operationType) {
        // 充值、撤销、账单退费、账单返点
      case 1:
      case 4:
      case 5:
      case 6:
      case 7:
        BasePatientMemberOccurLog prepaidRechargeRecord =
            getPrepaidRechargeRecord(id, type, operationType);
        if (StringHelper.isNotNull(prepaidRechargeRecord)) {
          mapper.deleteByPrimaryKeyAndtype(
              prepaidRechargeRecord.getOccurLogId(),
              prepaidRechargeRecord.getType().intValue(),
              prepaidRechargeRecord.getOccurType().intValue());
          mapper.insertSelective(prepaidRechargeRecord);
          return prepaidRechargeRecord.getCardId();
        }
        break;
        // 消费
      case 2:
        BasePatientMemberOccurLog memberOccurLog =
            getPrepaidExpendAndRevocation(id, type, operationType);
        if (StringHelper.isNotNull(memberOccurLog)) {
          mapper.delete(memberOccurLog);
          mapper.insertSelective(memberOccurLog);
          return memberOccurLog.getCardId();
        }
        break;
        // 退款
      case 3:
        BasePatientMemberOccurLog prepaidReturnInfo =
            getPrepaidReturnRecordInfo(id, type, operationType);
        if (StringHelper.isNotNull(prepaidReturnInfo)) {
          mapper.delete(prepaidReturnInfo);
          mapper.insertSelective(prepaidReturnInfo);
          return prepaidReturnInfo.getCardId();
        }
        break;
      case 8:
      case 9:
        BasePatientMemberOccurLog prepaidTransferRecord =
                getPrepaidTransferRecord(id, type, operationType);
        if (StringHelper.isNotNull(prepaidTransferRecord)) {
          mapper.deleteByPrimaryKeyAndtype(
                  prepaidTransferRecord.getOccurLogId(),
                  prepaidTransferRecord.getType().intValue(),
                  prepaidTransferRecord.getOccurType().intValue());
          mapper.insertSelective(prepaidTransferRecord);
          return prepaidTransferRecord.getCardId();
        }
        break;
      default:
        break;
    }
    return null;
  }

  /**
   * 获取操作对象模板
   *
   * @param form 拉去时间
   * @return emp
   */
  public Example getExample(PullForm form, Example emp) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    emp.createCriteria().andBetween("updTime", startDate, endDate);
    return emp;
  }

  /**
   * 获取撤销操作对象模板
   *
   * @param form 拉去时间
   * @return emp
   */
  public Example getRevocationExample(PullForm form, Example emp, Integer type) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    emp.createCriteria().andBetween("updTime", startDate, endDate).andEqualTo("type", type);
    return emp;
  }
}
