package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.form.CardRelationForm;
import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.model.openCardModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.MemberBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.MemberRelationVo;
import com.yunya.feign.patient_central.domain.vo.PatientMemberChangeLogVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientMemberChangeLog;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.patient_central.PatientMemberRelation;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.mapper.PatientMemberChangeLogMapper;
import com.yunya.modules.patient_central.mapper.PatientMemberInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientMemberRelationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    @Autowired private PatientMemberInfoMapper patientMemberInfoMapper;

    @Autowired private PatientMemberRelationMapper patientMemberRelationMapper;

    @Autowired private PatientMemberChangeLogMapper patientMemberChangeLogMapper;

    @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 根据患者id查询会员基本信息
     * @return MemberBaseInfoVO
     */
    public MemberBaseInfoVo findMemberBaseInfo(Integer id) {
        MemberBaseInfoVo memberBaseInfoVO = patientMemberInfoMapper.findMemberBaseInfo(id);
        MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberBaseInfoVO.getMemberTypeId());
        memberBaseInfoVO.setMemberCardName(memberType.getName());
        return memberBaseInfoVO;
    }

    /**
     * 查询会员卡关联关系
     * @param form
     * @return List<PatientMemberRelationVO>
     */
    public MemberRelationVo findMemberBindingRelation(PatientMemberRelationQueryForm form) {
        MemberRelationVo memberRelationVO = new MemberRelationVo();
        form.setBindType(0);
        memberRelationVO.setMemberRelationList(patientMemberInfoMapper.findMemberBindingRelation(form));
        form.setBindType(1);
        memberRelationVO.setMemberBalanceRelationList(patientMemberInfoMapper.findMemberBindingRelation(form));
        return memberRelationVO;
    }

    /**
     * 添加会员卡关联关系
     * @param form
     */
    public void addMemberBindingRelation(MemberBindingRelationInfoModel form) {
        PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
        BeanUtils.copyProperties(form, patientMemberRelation);
        if(form.getBindType() == 0){ //如果条件成立 代表是添加会员卡关联关系
            patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMemberRelation.setCrtName(BaseContextHandler.getName());
            patientMemberRelationMapper.insertSelective(patientMemberRelation);
        }else {
            patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMemberRelation.setCrtName(BaseContextHandler.getName());
            patientMemberRelationMapper.insertSelective(patientMemberRelation);
            patientMemberRelation.setMasterCardId(patientMemberRelation.getSecondaryCardId());
            patientMemberRelation.setSecondaryCardId(patientMemberRelation.getMasterCardId());
            patientMemberRelationMapper.insertSelective(patientMemberRelation);
        }

    }

    /**
     * 开卡
     * @param openCardModel
     * @return
     */
    public ResponseResult addMemberCard(openCardModel openCardModel) {
        PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
        patientMemberInfo.setPatientId(openCardModel.getPatientId());
        patientMemberInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientMemberInfo.setMemberTypeId(openCardModel.getMemberTypeId());
        patientMemberInfo.setCardNumber(getCardNumber(openCardModel));
        patientMemberInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberInfo.setCrtName(BaseContextHandler.getName());
        mapper.insertSelective(patientMemberInfo);
        return CardLog(patientMemberInfo,"开卡","");
    }

    /**
     * 生产会员卡号
     * @param openCardModel
     * @return Integer
w     */
    public String getCardNumber(openCardModel openCardModel){
        OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        String number = mapper.generateCardNumber(organizationInfo.getId()); //获取最后一条记录 病历号的后八位
        String suffix = String.format("%06d", Integer.parseInt(number) + 1); //后八位进行加1
        String cardNumber = "H"+organizationInfo.getClinicNumber()+suffix; // 会员卡号 = 门诊编号+后八位
        return cardNumber;
    }

    /**
     * 根据关联类型删除关系
     * @param id
     */
    public void deleteRelationById(CardRelationForm cardRelationForm) {
        if(cardRelationForm.getBindType() == 0){
            mapper.deleteByPrimaryKey(cardRelationForm.getId());
        }
        if(cardRelationForm.getBindType() == 1){
            PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
            patientMemberRelation.setId(cardRelationForm.getId());
            PatientMemberRelation MemberRelation = patientMemberRelationMapper.selectOne(patientMemberRelation);
            patientMemberRelationMapper.deleteMemberRelation(MemberRelation.getSecondaryCardId(),MemberRelation.getMasterCardId());
            patientMemberRelationMapper.delete(MemberRelation);
        }
    }

    /**
     * 开卡日志
     */

    public ResponseResult CardLog(PatientMemberInfo patientMemberInfo,String operationType,String isupt){
        PatientMemberChangeLog patientMemberChangeLog = new PatientMemberChangeLog(); //会员卡记录日志
        patientMemberChangeLog.setCardNumber(patientMemberInfo.getCardNumber());
        MemberType memberType =
                remoteSystemServiceFeign.findMemberTypeById(patientMemberInfo.getMemberTypeId());
        if (memberType.getName() == null) {
            return ResponseUtil.fail(500,"根据患者会员卡类型ID未查询到会员卡","");
        }
        patientMemberChangeLog.setPatientId(patientMemberInfo.getPatientId());
        patientMemberChangeLog.setMemberCardName(memberType.getName());
        patientMemberChangeLog.setMemberTypeId(patientMemberInfo.getMemberTypeId());
        patientMemberChangeLog.setOrgId(patientMemberInfo.getOrgId());
        OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(patientMemberInfo.getOrgId());//获取门诊简称
        if (organizationInfo == null) {
            return ResponseUtil.fail(500,"为获取到结果","");
        }
        patientMemberChangeLog.setOrgName(organizationInfo.getAbbreviation());
        patientMemberChangeLog.setOperatorId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberChangeLog.setOperatorName(BaseContextHandler.getName());
        patientMemberChangeLog.setOperatingTime(new Date());
        patientMemberChangeLog.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberChangeLog.setCrtName(BaseContextHandler.getName());
        patientMemberChangeLog.setOperationType(operationType);
        if(isupt != null){
            patientMemberChangeLog.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMemberChangeLog.setUptName(BaseContextHandler.getName());
            patientMemberChangeLog.setUptTime(new Date());
        }
        patientMemberChangeLogMapper.insertSelective(patientMemberChangeLog);
        return ResponseUtil.success();
    }

    /**
     * 修改会员卡类型
     * @param form
     */
    public ResponseResult changeType(CardTypeForm form) {
        PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
        BeanUtils.copyProperties(form,patientMemberInfo);
        PatientMemberInfo patientMember = patientMemberInfoMapper.selectOne(patientMemberInfo);
        patientMember.setMemberTypeId(form.getMemberTypeId());
        patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMember.setUpdName(BaseContextHandler.getName());
        patientMember.setUpdTime(new Date());
        mapper.updateByPrimaryKeySelective(patientMember);
        return CardLog(patientMember,"变更","更新");
    }

    /**
     * 变更记录
     * @param cardNumber
     * @return
     */
    public List<PatientMemberChangeLogVo> changeLog(String cardNumber) {
        return mapper.changeLog(cardNumber);
    }
}
