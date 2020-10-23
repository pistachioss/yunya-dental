package com.yunya.middletable.service.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.*;
import com.yunya.middletable.dao.report.BasePatientMemberOccurLogMapper;
import com.yunya.middletable.dao.system.AccountItemMapper;
import com.yunya.models.middletable.BasePatientMemberOccurLog;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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
  @Autowired private MemberRechargeRecordMapper memberRechargeRecordMapper;

  /** 注入会员消费日志对象 */
  @Autowired private MemberExpendRecordMapper memberExpendRecordMapper;

  /** 注入会员退款日志对象 */
  @Autowired private MemberReturnRecordMapper memberReturnRecordMapper;

  /** 注入预付充值作日志对象 */
  @Autowired private PrepaidRechargeRecordMapper prepaidRechargeRecordMapper;

  /** 注入预付消费作日志对象 */
  @Autowired private PrepaidExpendRecordMapper prepaidExpendRecordMapper;

  /** 注入预付退款作日志对象 */
  @Autowired private PrepaidReturnRecordMapper prepaidReturnRecordMapper;

  /** 注入会员卡mapper */
  @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

  /** 注入会员卡充值明细mapper */
  @Autowired private MemberRechargeTollRecordMapper memberRechargeTollRecordMapper;

  /** 注入预付款信息mapper */
  @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  /** 注入系统字典服务 */
  @Autowired private AccountItemMapper accountItemMapper;

  /** 注入预付款充值明细Mapper */
  @Autowired private PrepaidRechargeTollRecordMapper prepaidRechargeTollRecordMapper;

  /**
   * 中间表-会员-预付款 信息操作源头
   *
   * @param msg
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
          mapper.delete(memberOccurLog);
        }
        break;
      default:
        break;
    }
  }


  /**
   * 添加中间表信息-会员/预付款-转
   *
   * @param msg
   */
  private void addPatientMemberOccurLog(MessageModel msg) {
    // 操作id
    Integer operationType = (Integer) msg.getParamMap().get("operationType");
    Integer type = (Integer) msg.getParamMap().get("type");
    Integer id = (Integer) msg.getParamMap().get("id");
    // 会员卡操作日志
    if (type == 0) {
      addMemberOccurLog(id,type,operationType);
    }
    // 预付款操作日志
    if (type == 1) {
      addPrepaymentOccurLog(id,type,operationType);
    }
  }



  /**
   * 会员修改-查询
   *
   * @param msg 消息
   * @return BasePatientMemberOccurLog
   */
  private BasePatientMemberOccurLog getMemberOccurLog(MessageModel msg) {
    Integer type = (Integer) msg.getParamMap().get("type");
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer operationType = (Integer) msg.getParamMap().get("operationType");
    if (type == 0) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = mapper.selectOneByPrimaryKeyAndtype(id, type, operationType);
      if (basePatientMemberOccurLog != null) {
        return basePatientMemberOccurLog;
      }
    }
    if (type == 1) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = mapper.selectOneByPrimaryKeyAndtype(id, type, operationType);
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
    if (type == 0) {
      pullMemberRecharge(form, type);
      pullMemberExpendRecord(form,type,2);
      pullMemberReturnRecord(form,type);
      pullMemberExpendRecord(form,type,4);
    }
    if (type == 1){
      pullPrepaymentRecharge(form, type);
      pullPrepaymentExpendRecord(form,type,2);
      pullPrepaymentReturn(form,type);
      pullPrepaymentExpendRecord(form,type,4);
    }
  }



  /**
   * 会员-批量拉取-充值记录
   *
   * @param form
   * @param type
   */
  public void pullMemberRecharge(PullForm form, Integer type) {
    List<MemberRechargeRecord> memberRechargeRecordList =
        (List<MemberRechargeRecord>) getMemberInfoLog(form, 1);
    if (StringHelper.isNotEmpty(memberRechargeRecordList)) {
      memberRechargeRecordList.forEach(
          memberRechargeRecord -> {
            Integer id = memberRechargeRecord.getId();
            mapper.deleteByPrimaryKeyAndtype(id, type, 1);
            BasePatientMemberOccurLog memberOccurLog = getMemberRechargeRecordInfoLog(id, type, 1);
            if (memberOccurLog != null) {
              mapper.insertSelective(memberOccurLog);
            }
          });
    }
  }


  /**
   * 会员-批量拉取-消费记录
   *
   * @param form
   * @param type
   */
  public void pullMemberExpendRecord(PullForm form, Integer type,Integer occurType ) {
    List<MemberExpendRecord> memberExpendRecordList =
            (List<MemberExpendRecord>) getMemberInfoLog(form, occurType);
    if (StringHelper.isNotEmpty(memberExpendRecordList)) {
      memberExpendRecordList.forEach(
              memberExpendRecord -> {
                Integer id = memberExpendRecord.getId();
                mapper.deleteByPrimaryKeyAndtype(id, type, occurType);
                BasePatientMemberOccurLog memberOccurLog = getMemberExpendAndRevocation(id, type, occurType);
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
  public void pullMemberReturnRecord(PullForm form, Integer type) {
    List<MemberReturnRecord> memberReturnRecordList =
            (List<MemberReturnRecord>) getMemberInfoLog(form, 3);
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
   * 会员-获取操作集合List
   * 1.充值 2.消费 3.退费 4.撤销
   * @param form 拉取时间和type
   * @return List<MemberRechargeRecord>
   */
  public List<? extends Object> getMemberInfoLog(PullForm form, Integer occurType) {
    if (occurType == 1) {
      Example emp = new Example(MemberRechargeRecord.class);
      Example example = getExample(form, emp);
      List<MemberRechargeRecord> memberRechargeRecordList =
              memberRechargeRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberRechargeRecordList)) {
        return memberRechargeRecordList;
      }
      return null;
    }

    if (occurType == 2) {
      Example emp = new Example(MemberExpendRecord.class);
      Example example = getRevocationExample(form,emp,0);
      List<MemberExpendRecord> memberExpendRecordList =
              memberExpendRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberExpendRecordList)) {
        return memberExpendRecordList;
      }
      return null;
    }

    if (occurType == 3) {
      Example emp = new Example(MemberReturnRecord.class);
      Example example = getExample(form,emp);
      List<MemberReturnRecord> memberRechargeRecordList =
              memberReturnRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberRechargeRecordList)) {
        return memberRechargeRecordList;
      }
      return null;
    }

    if (occurType == 4) {
      Example emp = new Example(MemberExpendRecord.class);
      Example example = getRevocationExample(form,emp,1);
      List<MemberExpendRecord> memberExpendRecordList =
              memberExpendRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberExpendRecordList)) {
        return memberExpendRecordList;
      }
      return null;
    }

    return null;
  }


  /**
   * 会员卡-查询单条-充值log
   *
   * @param id 充值id
   * @param type 会员类型
   * @param occurType 操作类型
   * @return BasePatientMemberOccurLog
   */
  public BasePatientMemberOccurLog getMemberRechargeRecordInfoLog(Integer id,Integer type,Integer occurType){
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
      }
      basePatientMemberOccurLog.setPatientId(patientMemberInfo.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) occurType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(memberRechargeRecord.getRechargePrincipal());
      basePatientMemberOccurLog.setBonusAmount(memberRechargeRecord.getRechargeBonus());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(
              memberRechargeRecord.getCurrentRechargePrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(
              memberRechargeRecord.getCurrentRechargeBonus());
      MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
      memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord.getId());
      MemberRechargeTollRecord  memberRechargeToll = memberRechargeTollRecordMapper.selectOne(memberRechargeTollRecord);
      if (StringHelper.isNotNull(memberRechargeToll)){
        basePatientMemberOccurLog.setPaymentId(memberRechargeToll.getPaymentId());
        AccountItem accountItem = accountItemMapper.selectByPrimaryKey(memberRechargeToll.getPaymentId());
        if (StringHelper.isNotNull(accountItem)){
          basePatientMemberOccurLog.setPaymentManner(accountItem.getName());
        }
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
      basePatientMemberOccurLog.setPrincipalAmount(memberExpendRecord.getExpendPrincipal());
      basePatientMemberOccurLog.setBonusAmount(memberExpendRecord.getExpendGift());
      basePatientMemberOccurLog.setPaymentId(memberExpendRecord.getId());
      basePatientMemberOccurLog.setOperatorUserId(memberExpendRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(memberExpendRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(memberExpendRecord.getCrtTime());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(memberExpendRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(memberExpendRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(memberExpendRecord.getOrgId());
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
      basePatientMemberOccurLog.setCurrentRechargePrincipal(memberReturnRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(memberReturnRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(memberReturnRecord.getOrgId());
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
  private void addMemberOccurLog(Integer id, Integer type,Integer operationType) {
    switch (operationType) {
      // 充值
      case 1:
        BasePatientMemberOccurLog memberRechargeLog = getMemberRechargeRecordInfoLog(id, type, operationType);
        if (StringHelper.isNotNull(memberRechargeLog)){
          mapper.delete(memberRechargeLog);
          mapper.insert(memberRechargeLog);
        }
        break;
      // 消费
      case 2: case 4:
        BasePatientMemberOccurLog memberExpendLog = getMemberExpendAndRevocation(id, type,operationType);
        if (StringHelper.isNotNull(memberExpendLog)){
          mapper.delete(memberExpendLog);
          mapper.insert(memberExpendLog);
        }
        break;
      // 退款
      case 3:
        BasePatientMemberOccurLog memberReturnInfoLog = getMemberReturnInfoLog(id, type, operationType);
        if (StringHelper.isNotNull(memberReturnInfoLog)){
          mapper.delete(memberReturnInfoLog);
          mapper.insert(memberReturnInfoLog);
        }
        break;
      // 撤销
      default:
        break;
    }
  }

  /**
   * 预付款-批量添加-充值log------------------------------------------------------------------------------------------------------------------------------------------
   *
   * @param form 拉取时间
   * @param type 会员类型
   */
  private void pullPrepaymentRecharge(PullForm form, Integer type) {
    List<PrepaidRechargeRecord> prepaidRechargeRecordList =
            (List<PrepaidRechargeRecord>) getPrepaymentInfoLog(form, 1);
    if (StringHelper.isNotEmpty(prepaidRechargeRecordList)) {
      prepaidRechargeRecordList.forEach(
              prepaidRechargeRecord -> {
                Integer id = prepaidRechargeRecord.getId();
                mapper.deleteByPrimaryKeyAndtype(id, type, 1);
                BasePatientMemberOccurLog memberOccurLog = getPrepaidRechargeRecord(id, type, 1);
                if (memberOccurLog != null) {
                  mapper.insertSelective(memberOccurLog);
                }
              });
    }
  }

  /**
   * 预付款-批量添加-消费/撤销log
   * @param form
   * @param type
   */
  private void pullPrepaymentExpendRecord(PullForm form, Integer type,Integer occurType) {
    List<PrepaidExpendRecord> prepaidExpendRecordList =
            (List<PrepaidExpendRecord>) getPrepaymentInfoLog(form, occurType);
    if (StringHelper.isNotEmpty(prepaidExpendRecordList)) {
      prepaidExpendRecordList.forEach(
              prepaidExpendRecord -> {
                Integer id = prepaidExpendRecord.getId();
                mapper.deleteByPrimaryKeyAndtype(id, type, occurType);
                BasePatientMemberOccurLog memberOccurLog = getPrepaidExpendAndRevocation(id, type, occurType);
                if (memberOccurLog != null) {
                  mapper.insertSelective(memberOccurLog);
                }
              });
    }
  }

  /**
   * 预付款-批量添加-退款log
   * @param form
   * @param type
   */
  private void pullPrepaymentReturn(PullForm form, Integer type) {
    List<PrepaidReturnRecord> prepaidReturnRecordList =
            (List<PrepaidReturnRecord>) getPrepaymentInfoLog(form, 3);
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
   * 预付款-获取操作集合List
   * 1.充值 2.消费 3.退费 4.撤销
   * @param form 拉取时间和type
   * @return List<MemberRechargeRecord>
   */
  public List<? extends Object> getPrepaymentInfoLog(PullForm form, Integer occurType) {
    if (occurType == 1) {
      Example emp = new Example(PrepaidRechargeRecord.class);
      Example example = getExample(form,emp);
      List<PrepaidRechargeRecord> prepaidRechargeRecordList = prepaidRechargeRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(prepaidRechargeRecordList)) {
        return prepaidRechargeRecordList;
      }
      return null;
    }

    if (occurType == 2) {
      Example emp = new Example(PrepaidExpendRecord.class);
      Example example = getRevocationExample(form,emp,0);
      List<PrepaidExpendRecord> prepaidExpendRecordList =
              prepaidExpendRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(prepaidExpendRecordList)) {
        return prepaidExpendRecordList;
      }
      return null;
    }

    if (occurType == 3) {
      Example emp = new Example(PrepaidReturnRecord.class);
      Example example = getExample(form,emp);
      List<PrepaidReturnRecord> prepaidReturnRecordList =
              prepaidReturnRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(prepaidReturnRecordList)) {
        return prepaidReturnRecordList;
      }
      return null;
    }

    if (occurType == 4) {
      Example emp = new Example(PrepaidExpendRecord.class);
      Example example = getRevocationExample(form,emp,1);
      List<PrepaidExpendRecord> memberRechargeRecordList = prepaidExpendRecordMapper.selectByExample(example);
      if (StringHelper.isNotEmpty(memberRechargeRecordList)) {
        return memberRechargeRecordList;
      }
      return null;
    }

    return null;
  }

  /**
   * 预付款-查询单条-充值log
   * @param id 充值记录id
   * @param type 会员类型
   * @param operationType 操作类型
   */
  public BasePatientMemberOccurLog getPrepaidRechargeRecord(Integer id,Integer type, Integer operationType){
    PrepaidRechargeRecord prepaidRechargeRecord =
            prepaidRechargeRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(prepaidRechargeRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(prepaidRechargeRecord.getId());
      // 通过会员卡号 获取会员id
      PatientPrepaymentsInfo prepaymentsInfo = new PatientPrepaymentsInfo();
      prepaymentsInfo.setPrepaymentNumber(prepaidRechargeRecord.getPrepaidId());
      PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOne(prepaymentsInfo);
      if (StringHelper.isNotNull(patientPrepaymentsInfo)) {
        basePatientMemberOccurLog.setCardId(patientPrepaymentsInfo.getId());
      }
      basePatientMemberOccurLog.setPatientId(patientPrepaymentsInfo.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) operationType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(
              prepaidRechargeRecord.getRechargePrincipal());
      basePatientMemberOccurLog.setBonusAmount(prepaidRechargeRecord.getRechargeBonus());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(prepaidRechargeRecord.getCurrentRechargePrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(prepaidRechargeRecord.getCurrentRechargeBonus());
      PrepaidRechargeTollRecord memberRechargeTollRecord = new PrepaidRechargeTollRecord();
      memberRechargeTollRecord.setRechargeRecordId(prepaidRechargeRecord.getId());
      PrepaidRechargeTollRecord prepaidRechargeTollRecord = prepaidRechargeTollRecordMapper.selectOne(memberRechargeTollRecord);
      if (StringHelper.isNotNull(prepaidRechargeTollRecord)){
        basePatientMemberOccurLog.setPaymentId(prepaidRechargeTollRecord.getPaymentId());
        AccountItem accountItem = accountItemMapper.selectByPrimaryKey(prepaidRechargeTollRecord.getPaymentId());
        if (StringHelper.isNotNull(accountItem)){
          basePatientMemberOccurLog.setPaymentManner(accountItem.getName());
        }
      }
      basePatientMemberOccurLog.setOperatorUserId(prepaidRechargeRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(prepaidRechargeRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(prepaidRechargeRecord.getCrtTime());
      basePatientMemberOccurLog.setRechargeCardNumber(prepaidRechargeRecord.getRechargeCardNumber());
      basePatientMemberOccurLog.setOrgId(prepaidRechargeRecord.getOrgId());
      return basePatientMemberOccurLog;
  }
    return null;
  }

  /**
   * 预付款-查询单条-消费/撤销
   *
   * @param id 操作id
   * @param operationType 操作类型
   */
  public BasePatientMemberOccurLog getPrepaidExpendAndRevocation(Integer id,Integer type,Integer operationType) {
    PrepaidExpendRecord prepaidExpendRecord = prepaidExpendRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(prepaidExpendRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(prepaidExpendRecord.getId());
      // 通过会员卡号 获取会员id
      PatientPrepaymentsInfo prepaymentsInfo = new PatientPrepaymentsInfo();
      prepaymentsInfo.setPrepaymentNumber(prepaidExpendRecord.getPrepaidId());
      PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOne(prepaymentsInfo);
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
      basePatientMemberOccurLog.setCurrentRechargePrincipal(prepaidExpendRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(prepaidExpendRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(prepaidExpendRecord.getOrgId());
      return basePatientMemberOccurLog;
    }
    return null;
  }


  /**
   * 预付款-查询单条-退款log
   * @param id 操作id
   * @param type 会员类型
   * @param occurType 操作类型
   * @return BasePatientMemberOccurLog
   */
  public BasePatientMemberOccurLog getPrepaidReturnRecordInfo(Integer id,Integer type,Integer occurType){
    PrepaidReturnRecord prepaidReturnRecord = prepaidReturnRecordMapper.selectByPrimaryKey(id);
    if (StringHelper.isNotNull(prepaidReturnRecord)) {
      BasePatientMemberOccurLog basePatientMemberOccurLog = new BasePatientMemberOccurLog();
      basePatientMemberOccurLog.setOccurLogId(prepaidReturnRecord.getId());
      // 通过预付款卡号 获取会员id
      PatientPrepaymentsInfo prepaymentsInfo = new PatientPrepaymentsInfo();
      prepaymentsInfo.setPrepaymentNumber(prepaidReturnRecord.getPrepaidId());
      PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOne(prepaymentsInfo);
      if (StringHelper.isNotNull(patientPrepaymentsInfo)) {
        basePatientMemberOccurLog.setCardId(patientPrepaymentsInfo.getId());
      }
      basePatientMemberOccurLog.setPatientId(prepaidReturnRecord.getPatientId());
      basePatientMemberOccurLog.setType((byte) type.intValue());
      basePatientMemberOccurLog.setOccurType((byte) occurType.intValue());
      basePatientMemberOccurLog.setPrincipalAmount(
              prepaidReturnRecord.getReturnPrincipalAmount());
      basePatientMemberOccurLog.setBonusAmount(prepaidReturnRecord.getReturnGiftAmount());
      basePatientMemberOccurLog.setPaymentId(prepaidReturnRecord.getReturnWayId());
      basePatientMemberOccurLog.setPaymentManner(prepaidReturnRecord.getReturnWayType());
      basePatientMemberOccurLog.setOperatorUserId(prepaidReturnRecord.getCrtId());
      basePatientMemberOccurLog.setRemarks(prepaidReturnRecord.getRemarks());
      basePatientMemberOccurLog.setOccurDate(prepaidReturnRecord.getCrtTime());
      basePatientMemberOccurLog.setCurrentRechargePrincipal(prepaidReturnRecord.getCurrentPrincipal());
      basePatientMemberOccurLog.setCurrentRechargeBonus(prepaidReturnRecord.getCurrentBonus());
      basePatientMemberOccurLog.setOrgId(prepaidReturnRecord.getOrgId());
      return basePatientMemberOccurLog;
    }
    return null;
  }

  /**
   * 预付款-中间表统一添加--根据类型添加
   * @param id 操作方式id
   * @param operationType 操作类型(1充值2消费3退款4撤销收费)
   */
  private void addPrepaymentOccurLog(Integer id,Integer type,Integer operationType) {
    switch (operationType) {
      // 充值
      case 1:
        BasePatientMemberOccurLog prepaidRechargeRecord = getPrepaidRechargeRecord(id,type,operationType);
        if (StringHelper.isNotNull(prepaidRechargeRecord)){
          mapper.deleteByPrimaryKeyAndtype(prepaidRechargeRecord.getOccurLogId(),prepaidRechargeRecord.getType().intValue(), prepaidRechargeRecord.getOccurType().intValue());
          mapper.insert(prepaidRechargeRecord);
        }
        break;
      // 消费
      case 2: case 4:
        BasePatientMemberOccurLog memberOccurLog = getPrepaidExpendAndRevocation(id,type,operationType);
        if (StringHelper.isNotNull(memberOccurLog)) {
          mapper.delete(memberOccurLog);
          mapper.insert(memberOccurLog);
        }
        break;
      // 退款
      case 3:
        BasePatientMemberOccurLog prepaidReturnInfo = getPrepaidReturnRecordInfo(id, type, operationType);
        if (StringHelper.isNotNull(prepaidReturnInfo)) {
          mapper.delete(prepaidReturnInfo);
          mapper.insert(prepaidReturnInfo);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 获取操作对象模板-------------------------------------------------------------------------------------------------------------------------------------------------
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
  public Example getRevocationExample(PullForm form,Example emp,Integer type) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    emp.createCriteria().andBetween("updTime", startDate, endDate).andEqualTo("type", type);
    return emp;
  }

}


