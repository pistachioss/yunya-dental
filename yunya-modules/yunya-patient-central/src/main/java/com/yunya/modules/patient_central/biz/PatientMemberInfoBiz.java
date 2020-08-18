package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.CardRelationForm;
import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.AccountedWayModel;
import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.model.MemberRechargeModel;
import com.yunya.feign.patient_central.domain.model.OpenCardModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.query.RechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.mapper.*;
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

    @Autowired private MemberRechargeRecordMapper memberRechargeRecordMapper;

    @Autowired private MemberRechargeTollRecordMapper memberRechargeTollRecordMapper;



    /**
     * 根据患者id查询会员基本信息
     * @return MemberBaseInfoVO
     */
    public ResponseResult findMemberBaseInfo(Integer id) {
        MemberBaseInfoVo memberBaseInfoVO = patientMemberInfoMapper.findMemberBaseInfo(id);
        MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberBaseInfoVO.getMemberTypeId());
        if (memberType.getName() == null) {
            return ResponseUtil.fail(500,"根据患者会员卡类型ID未查询到会员卡","");
        }
        memberBaseInfoVO.setMemberCardName(memberType.getName());
        return ResponseUtil.success(memberBaseInfoVO);
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
        }
        if(form.getBindType() == 1) {
            patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMemberRelation.setCrtName(BaseContextHandler.getName());
            patientMemberRelationMapper.insertSelective(patientMemberRelation);
            int MasterCardI = patientMemberRelation.getMasterCardId();
            patientMemberRelation.setMasterCardId(patientMemberRelation.getSecondaryCardId());
            patientMemberRelation.setSecondaryCardId(MasterCardI);
            patientMemberRelationMapper.insertSelective(patientMemberRelation);
        }

    }

    /**
     * 开卡
     * @param openCardModel
     * @return
     */
    public ResponseResult addMemberCard(OpenCardModel openCardModel) {
        PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
        patientMemberInfo.setPatientId(openCardModel.getPatientId());
        patientMemberInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientMemberInfo.setMemberTypeId(openCardModel.getMemberTypeId());
        patientMemberInfo.setCardNumber(generateCardNumber("H"));
        if(patientMemberInfo.getCardNumber() == null){
            return ResponseUtil.fail(500,"生成会员卡号失败","");
        }
        patientMemberInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberInfo.setCrtName(BaseContextHandler.getName());
        patientMemberInfoMapper.insertSelective(patientMemberInfo);
        return CardLog(patientMemberInfo,"开卡","");
    }

    /**
     * 生产会员卡号
     * @param openCardModel
     * @return Integer
w     */
    public String generateCardNumber(String Mark){
        OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        if (organizationInfo == null) {
            return null;
        }
        System.out.println(Integer.parseInt(BaseContextHandler.getUserID()));
        String number = mapper.generateCardNumber(Integer.parseInt(BaseContextHandler.getOrgId())); //获取最后一条记录 病历号的后八位
        String suffix = String.format("%06d", Integer.parseInt(number) + 1); //后八位进行加1
        String cardNumber = Mark+"000"+suffix; // 会员卡号 = 门诊编号+后八位
        return cardNumber;
    }

    /**
     * 根据关联类型删除关系
     * @param id
     */
    public void deleteRelationById(CardRelationForm cardRelationForm) {
        if(cardRelationForm.getBindType() == 0){
            patientMemberRelationMapper.deleteByPrimaryKey(cardRelationForm.getId());
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
        if(isupt != null){ //如果是修改
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
        PatientMemberInfo patientMember = patientMemberInfoMapper.selectOneByCardNumber(form.getCardNumber());
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
        return patientMemberChangeLogMapper.changeLog(cardNumber);
    }

    /**
     * 充值
     * @param memberRechargeModel
     */
    public void Recharge(MemberRechargeModel model) {
        //添加会员卡充值记录
        MemberRechargeRecord memberRechargeRecord = new MemberRechargeRecord();
        BeanUtils.copyProperties(model,memberRechargeRecord);
        memberRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberRechargeRecord.setCrtName(BaseContextHandler.getName());
        memberRechargeRecordMapper.insertSelective(memberRechargeRecord);
        //添加会员卡充值收费记录
        if(model.getAccountedWayModelList().size()>0){
            for (AccountedWayModel accountedWayModel : model.getAccountedWayModelList()) {
                MemberRechargeTollRecord memberRechargeTollRecord = new MemberRechargeTollRecord();
                BeanUtils.copyProperties(accountedWayModel,memberRechargeTollRecord);
                memberRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
                memberRechargeTollRecord.setRechargeRecordId(memberRechargeRecord.getId());
                memberRechargeTollRecordMapper.insertSelective(memberRechargeTollRecord);
            }
        }

    }

    /**
     * 充值记录
     * @param cardNumber
     * @return
     */
    public PageInfo<RechargeRecordVo> RechargeRecord(RechargeRecordQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<RechargeRecordVo> resultList = memberRechargeRecordMapper.RechargeRecord(form);

        if(resultList.size()>0){
            for (RechargeRecordVo rechargeRecordVo : resultList) {
                OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(rechargeRecordVo.getOrgId());//获取门诊简称
                if (organizationInfo != null) {
                    rechargeRecordVo.setOrgName(organizationInfo.getAbbreviation());
                }
                List<MemberRechargeTollRecord> memberRechargeTollRecordList =  memberRechargeTollRecordMapper.selectMemberRechargeRecord(rechargeRecordVo.getId());
                StringBuilder labels = new StringBuilder(16);
                for (MemberRechargeTollRecord memberRechargeTollRecord : memberRechargeTollRecordList) {
                    AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(memberRechargeTollRecord.getPaymentId());
                    labels.append(accountItem.getName());
                    labels.append("、");
                }
                rechargeRecordVo.setPayment(labels.toString());
            }
        }
        return new PageInfo<>(resultList);
    }
}
