package com.yunya.middletable.service.patient;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.PatientDepositAccountTypeEnum;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.PatientMemberInfoMapper;
import com.yunya.middletable.dao.patient.PatientPrepaymentsInfoMapper;
import com.yunya.middletable.dao.report.BasePatientMemberMapper;
import com.yunya.middletable.dao.report.PatientManageMapper;
import com.yunya.middletable.dao.system.MemberTypeMapper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.patient_central.PatientPrepaymentsInfo;
import com.yunya.models.report.BasePatientMember;
import com.yunya.models.report.PatientManage;
import com.yunya.models.system.MemberType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

import static com.yunya.framework.common.enums.PatientDepositAccountTypeEnum.MEMBER;

/**
 * 简介: 报表服务患者会员/预付款信息同步
 *
 * @author: WY
 * @date: 2020/10/16 16:06
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BasePatientMemberBiz extends BaseBiz<BasePatientMemberMapper, BasePatientMember> {

  /** 注入会员对象 */
  @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

  /** 注入预付款对象 */
  @Autowired private PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

  /** 注入会员卡名称查询对象 */
  @Autowired private MemberTypeMapper memberTypeMapper;

  @Resource
  private PatientManageMapper patientManageMapper;

  /**
   * 患者会员/预付款信息操作
   *
   * @param msg 消息
   */
  public void operate(MessageModel msg) {
    Integer operateType = msg.getOperateType();
    Integer id = (Integer) msg.getParamMap().get("id");
    Integer type = (Integer) msg.getParamMap().get("type");
    switch (operateType) {
      case 0:
        addPatientMemberInfo(msg);
        break;
      case 1:
        BasePatientMember patientMemberInfo = getPatientMemberInfo(id, type);
        if (StringHelper.isNotNull(patientMemberInfo)) {
          mapper.updateByPrimaryKeySelective(patientMemberInfo);
        }
        break;
      case 2:
        if (MEMBER.equals(type)) {
          BasePatientMember member = getPatientMemberInfo(id, type);
          PatientMemberInfo memberInfo = patientMemberInfoMapper.selectByPrimaryKey(id);
          if (StringHelper.isNotNull(memberInfo) && StringHelper.isNotNull(member)) {
            mapper.delete(member);
            mapper.insert(member);
          }
          mapper.delete(member);
        }
        if (PatientDepositAccountTypeEnum.isPrepaymentType(type)) {
          BasePatientMember prepayments = getPatientMemberInfo(id, type);
          PatientPrepaymentsInfo patientPrepaymentsInfo =
              patientPrepaymentsInfoMapper.selectByPrimaryKey(id);
          if (StringHelper.isNotNull(patientPrepaymentsInfo)
              && StringHelper.isNotNull(prepayments)) {
            mapper.delete(prepayments);
            mapper.insert(prepayments);
          }
          mapper.delete(prepayments);
        }

        break;
      default:
        break;
    }
  }

  /**
   * 拉取某段时间内的组织数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullMemberData(PullForm form) {
    Integer dataType = form.getDataType();
    if (MEMBER.equals(dataType)) {
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(PatientBaseInfo.class);
      emp.createCriteria().andBetween("updTime", startDate, endDate);
      List<PatientMemberInfo> patientMemberInfos = patientMemberInfoMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(patientMemberInfos)) {
        patientMemberInfos.forEach(
            patientBaseInfo -> {
              Integer memberId = patientBaseInfo.getId();
              mapper.deleteByPrimaryKeyAndType(memberId, dataType);
              BasePatientMember basePatientMember = getPatientMemberInfo(memberId, dataType);
              if (basePatientMember != null) {
                mapper.insertSelective(basePatientMember);
              }
            });
      }
    }

    if (PatientDepositAccountTypeEnum.isPrepaymentType(dataType)) {
      String startDate = form.getStartDate();
      String endDate = form.getEndDate();
      Example emp = new Example(PatientBaseInfo.class);
      emp.createCriteria().andBetween("updTime", startDate, endDate);
      List<PatientPrepaymentsInfo> patientPrepaymentsInfos =
          patientPrepaymentsInfoMapper.selectByExample(emp);
      if (StringHelper.isNotEmpty(patientPrepaymentsInfos)) {
        patientPrepaymentsInfos.forEach(
            patientBaseInfo -> {
              Integer prepaymentsId = patientBaseInfo.getId();
              mapper.deleteByPrimaryKeyAndType(prepaymentsId, dataType);
              BasePatientMember basePatientMember = getPatientMemberInfo(prepaymentsId, dataType);
              if (basePatientMember != null) {
                mapper.insertSelective(basePatientMember);
              }
            });
      }
    }
  }

  /**
   * 添加会员卡/预付款信息
   *
   * @param msg 消息
   */
  public void addPatientMemberInfo(MessageModel msg) {
    Integer type = (Integer) msg.getParamMap().get("type");
    Integer id = (Integer) msg.getParamMap().get("id");
    // 会员卡
    if (MEMBER.equals(type)) {
      BasePatientMember basePatientMember = getPatientMemberInfo(id, type);
      mapper.deleteByPrimaryKey(basePatientMember);
      mapper.insert(basePatientMember);
    }
    if (PatientDepositAccountTypeEnum.isPrepaymentType(type)) {
      BasePatientMember basePatientMember = getPatientMemberInfo(id, type);
      mapper.delete(basePatientMember);
      mapper.insert(basePatientMember);
    }
  }

  /**
   * 获取会员卡信息
   *
   * @param id 会员卡id
   * @param type 会员卡类型
   * @return BasePatientMember
   */
  public BasePatientMember getPatientMemberInfo(Integer id, Integer type) {
    BasePatientMember basePatientMember = new BasePatientMember();
    // 会员卡信息
    if (MEMBER.equals(type)) {
      PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectByPrimaryKey(id);
      basePatientMember.setCardId(patientMemberInfo.getId());
      basePatientMember.setCardNumber(patientMemberInfo.getCardNumber());
      basePatientMember.setType(type);
      basePatientMember.setMemberLevelId(patientMemberInfo.getMemberTypeId());
      MemberType memberType =
          memberTypeMapper.selectByPrimaryKey(patientMemberInfo.getMemberTypeId());
      if (null != memberType) {
        basePatientMember.setMemberLevelName(memberType.getName());
      }
      basePatientMember.setPrincipalAmount(patientMemberInfo.getPrincipalAmount());
      basePatientMember.setBonusAmount(patientMemberInfo.getBonusAmount());
      basePatientMember.setPatientId(patientMemberInfo.getPatientId());
      basePatientMember.setCardOpeningDate(patientMemberInfo.getCrtTime());
      //更新患者管理信息
      this.updatePatientManage(basePatientMember);
      return basePatientMember;
    }
    // 预付款信息
    if (PatientDepositAccountTypeEnum.isPrepaymentType(type)) {
      PatientPrepaymentsInfo patientPrepaymentsInfo =
          patientPrepaymentsInfoMapper.selectByPrimaryKey(id);
      basePatientMember.setCardId(patientPrepaymentsInfo.getId());
      basePatientMember.setCardNumber(patientPrepaymentsInfo.getPrepaymentNumber());
      basePatientMember.setType(type);
      basePatientMember.setPrincipalAmount(patientPrepaymentsInfo.getPrepaymentPrincipal());
      basePatientMember.setBonusAmount(patientPrepaymentsInfo.getPrepaymentBonus());
      basePatientMember.setPatientId(patientPrepaymentsInfo.getPatientId());
      basePatientMember.setCardOpeningDate(patientPrepaymentsInfo.getCrtTime());
      return basePatientMember;
    }
    return null;
  }

  private void updatePatientManage(BasePatientMember basePatientMember) {
    Example example = new Example(PatientManage.class);
    example.createCriteria().andEqualTo("patientId", basePatientMember.getPatientId());
    PatientManage patientManage = patientManageMapper.selectOneByExample(example);
    if (patientManage != null) {
      patientManage.setMemberLevelId(basePatientMember.getMemberLevelId());
      patientManage.setMemberLevelName(basePatientMember.getMemberLevelName());
      patientManageMapper.updateByPrimaryKeySelective(patientManage);
    }
  }
}
