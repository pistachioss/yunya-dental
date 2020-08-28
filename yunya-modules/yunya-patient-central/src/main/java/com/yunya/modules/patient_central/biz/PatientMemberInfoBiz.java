package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.CardRelationForm;
import com.yunya.feign.patient_central.domain.form.CardTypeForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.MemberExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.MemberReturnRecordQueryForm;
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

import java.math.BigDecimal;
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

    @Autowired private MemberReturnRecordMapper memberReturnRecordMapper;

    @Autowired private MemberExpendRecordMapper memberExpendRecordMapper;



    /**
     * 根据患者id查询会员基本信息
     * @return MemberBaseInfoVO
     */
    public MemberBaseInfoVo findMemberBaseInfo(Integer id) {
        MemberBaseInfoVo memberBaseInfoVO = this.patientMemberInfoMapper.findMemberBaseInfo(id);
        if (memberBaseInfoVO != null) {
            MemberType memberType = this.remoteSystemServiceFeign.findMemberTypeById(memberBaseInfoVO.getMemberTypeId());
            if (memberType != null && memberType.getName() != null) {
                memberBaseInfoVO.setMemberCardName(memberType.getName());
            }
        }

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
        memberRelationVO.setMemberRelationList(this.patientMemberInfoMapper.findMemberBindingRelation(form));
        form.setBindType(1);
        memberRelationVO.setMemberBalanceRelationList(this.patientMemberInfoMapper.findMemberBindingRelation(form));
        return memberRelationVO;
    }

    /**
     * 添加会员卡关联关系
     * @param form
     */
    public ResponseResult addMemberBindingRelation(MemberBindingRelationInfoModel form) {
        if (form.getPatientId() == form.getSecondaryCardId()) {
            return ResponseUtil.error("副卡人不能为患者本人！", "");
        } else {
            PatientMemberRelation MemberRelation = this.patientMemberRelationMapper.findBindingRelation(form);
            if (MemberRelation != null) {
                return ResponseUtil.error("该副卡人已存在,不能重复绑定！", MemberRelation);
            } else {
                PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
                BeanUtils.copyProperties(form, patientMemberRelation);
                if (form.getBindType() == 0) { //type为0 添加会员卡权限绑定
                    patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
                    patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
                    patientMemberRelation.setCrtName(BaseContextHandler.getName());
                    this.patientMemberRelationMapper.insertSelective(patientMemberRelation);
                }

                if (form.getBindType() == 1) { //type为1 添加会员卡共享值 双项绑定
                    patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
                    patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
                    patientMemberRelation.setCrtName(BaseContextHandler.getName());
                    this.patientMemberRelationMapper.insertSelective(patientMemberRelation);
                    int MasterCardI = patientMemberRelation.getMasterCardId();
                    patientMemberRelation.setMasterCardId(patientMemberRelation.getSecondaryCardId());
                    patientMemberRelation.setSecondaryCardId(MasterCardI);
                    this.patientMemberRelationMapper.insertSelective(patientMemberRelation);
                }

                return ResponseUtil.success();
            }
        }
    }

    /**
     * 开卡
     * @param openCardModel
     * @return
     */
    public void addMemberCard(OpenCardModel openCardModel) {
        PatientMemberInfo patientMemberInfo = new PatientMemberInfo();
        patientMemberInfo.setPatientId(openCardModel.getPatientId());
        patientMemberInfo.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientMemberInfo.setMemberTypeId(openCardModel.getMemberTypeId());
        patientMemberInfo.setCardNumber(this.generateCardNumber("H", "patient_member_info", "card_number"));
        patientMemberInfo.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberInfo.setCrtName(BaseContextHandler.getName());
        this.patientMemberInfoMapper.insertSelective(patientMemberInfo);
        this.CardLog(patientMemberInfo, "开卡", "");
    }

    /**
     * 生产会员卡号
     * @param openCardModel
     * @return Integer
     */
    public String generateCardNumber(String Mark, String tableName, String column) {
        String number = this.mapper.generateCardNumber(Integer.parseInt(BaseContextHandler.getOrgId()), tableName, column);
        String suffix = String.format("%06d", Integer.parseInt(number) + 1);
        String cardNumber = Mark + "000" + suffix;
        return cardNumber;
    }

    /**
     * 根据关联类型删除关系
     * @param id
     */
    public void deleteRelationById(CardRelationForm cardRelationForm) {
        if (cardRelationForm.getBindType() == 0) { //权限绑定 单项删除
            this.patientMemberRelationMapper.deleteByPrimaryKey(cardRelationForm.getId());
        }

        if (cardRelationForm.getBindType() == 1) { //共享值绑定 双项删除
            PatientMemberRelation patientMemberRelation = new PatientMemberRelation();
            patientMemberRelation.setId(cardRelationForm.getId());
            PatientMemberRelation MemberRelation = (PatientMemberRelation)this.patientMemberRelationMapper.selectOne(patientMemberRelation);
            this.patientMemberRelationMapper.deleteMemberRelation(MemberRelation.getSecondaryCardId(), MemberRelation.getMasterCardId());
            this.patientMemberRelationMapper.delete(MemberRelation);
        }

    }

    /**
     * 开卡日志
     * @param patientMemberInfo
     * @param operationType
     * @param isupt
     */
    public void CardLog(PatientMemberInfo patientMemberInfo, String operationType, String isupt) {
        PatientMemberChangeLog patientMemberChangeLog = new PatientMemberChangeLog(); //会员卡记录日志
        patientMemberChangeLog.setCardNumber(patientMemberInfo.getCardNumber());
        MemberType memberType = this.remoteSystemServiceFeign.findMemberTypeById(patientMemberInfo.getMemberTypeId());
        if (memberType.getName() != null) {
            patientMemberChangeLog.setMemberCardName(memberType.getName());
        }

        patientMemberChangeLog.setPatientId(patientMemberInfo.getPatientId());
        patientMemberChangeLog.setMemberTypeId(patientMemberInfo.getMemberTypeId());
        patientMemberChangeLog.setOrgId(patientMemberInfo.getOrgId());
        OrganizationInfo organizationInfo = this.remoteSystemServiceFeign.findOrgInfoByOrgId(patientMemberInfo.getOrgId()); //获取门诊简称
        if (organizationInfo != null) {
            patientMemberChangeLog.setOrgName(organizationInfo.getAbbreviation());
        }

        patientMemberChangeLog.setOperatorId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberChangeLog.setOperatorName(BaseContextHandler.getName());
        patientMemberChangeLog.setOperatingTime(new Date());
        patientMemberChangeLog.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMemberChangeLog.setCrtName(BaseContextHandler.getName());
        patientMemberChangeLog.setOperationType(operationType);
        if (isupt != null) { //不为空就是修改
            patientMemberChangeLog.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMemberChangeLog.setUptName(BaseContextHandler.getName());
            patientMemberChangeLog.setUptTime(new Date());
        }

        this.patientMemberChangeLogMapper.insertSelective(patientMemberChangeLog);
    }

    /**
     * 修改会员卡类型
     * @param form
     */
    public void changeType(CardTypeForm form) {
        PatientMemberInfo patientMember = this.patientMemberInfoMapper.selectOneByCardNumber(form.getCardNumber());
        patientMember.setMemberTypeId(form.getMemberTypeId());
        patientMember.setUptId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientMember.setUpdName(BaseContextHandler.getName());
        patientMember.setUpdTime(new Date());
        patientMember.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        ((PatientMemberInfoMapper)this.mapper).updateByPrimaryKeySelective(patientMember);
        this.CardLog(patientMember, "变更", "更新");
    }

    /**
     * 变更记录
     * @param cardNumber
     * @return
     */
    public List<PatientMemberChangeLogVo> changeLog(String cardNumber) {
        return this.patientMemberChangeLogMapper.changeLog(cardNumber);
    }

    /**
     * 充值
     * @param memberRechargeModel
     */
    public void Recharge(MemberRechargeModel model) {
        //查询会员余额 余额增加
        PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectCardNumber(model.getMemberId(),model.getPatientId());
        patientMemberInfo.setPrincipalAmount(patientMemberInfo.getPrincipalAmount().add(model.getRechargePrincipal()));
        patientMemberInfo.setBonusAmount(patientMemberInfo.getBonusAmount().add(model.getRechargeBonus()));
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        //添加会员卡充值记
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
                memberRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
                memberRechargeTollRecord.setCrtName(BaseContextHandler.getName());
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
        form.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
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
                    AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(memberRechargeTollRecord.getPaymentId()); // todo 充值记录 待确认
                    if(accountItem != null){
                        labels.append(accountItem.getName());
                    }
                }
                rechargeRecordVo.setPayment(labels.toString());
            }
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 退费
     * @param form
     */
    public void refund(MemberReturnRecordModel model) {
        //查询会员余额 退减余额和赠金
        PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectCardNumber(model.getMemberId(),model.getPatientId());
        patientMemberInfo.setPrincipalAmount(patientMemberInfo.getPrincipalAmount().subtract(model.getReturnPrincipalAmount()));
        patientMemberInfo.setBonusAmount(patientMemberInfo.getBonusAmount().subtract(model.getReturnGiftAmount()));
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        //添加会员卡退费记录
        MemberReturnRecord memberReturnRecord = new MemberReturnRecord();
        BeanUtils.copyProperties(model,memberReturnRecord);
        memberReturnRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(memberReturnRecord.getReturnWayId()); // todo 充值记录 待确认
        if(accountItem != null){
            memberReturnRecord.setReturnWayType(accountItem.getName());//获取退费方式类型名称
        }
        memberReturnRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberReturnRecord.setCrtName(BaseContextHandler.getName());
        memberReturnRecordMapper.insertSelective(memberReturnRecord);
    }

    /**
     * 退费记录列表
     * @param queryForm
     * @return MemberReturnRecordVo
     */
    public PageInfo<MemberReturnRecordVo> refundList(MemberReturnRecordQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<MemberReturnRecordVo> resultList =  memberReturnRecordMapper.refundList(form);
        for (MemberReturnRecordVo memberReturnRecordVo : resultList) {
            OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(memberReturnRecordVo.getOrgId());//获取门诊简称
            if (organizationInfo != null) {
                memberReturnRecordVo.setOrgName(organizationInfo.getAbbreviation());
            }
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 消费
     * @param model
     */
    public ResponseResult expend(MemberExpendRecordModel model) {
        PatientMemberInfo patientMemberInfo = patientMemberInfoMapper.selectCardNumber(model.getMemberId(), model.getPatientId());
        if(patientMemberInfo.getPrincipalAmount().add(patientMemberInfo.getBonusAmount()).compareTo(model.getExpendTotal()) == -1 ){ //如果本金+赠金 小于 消费金额
            return ResponseUtil.error("会员卡余额不足",patientMemberInfo);
        }else{
            if(patientMemberInfo.getPrincipalAmount().compareTo(model.getExpendTotal()) == -1 ) { //会员卡余额 小于 消费金额
                //会员本金减去消费金额
                BigDecimal surplus = patientMemberInfo.getPrincipalAmount().subtract(model.getExpendTotal());
                if(surplus.compareTo(new BigDecimal(0)) == -1){

                }
            }else {
                patientMemberInfo.setPrincipalAmount(patientMemberInfo.getPrincipalAmount().subtract(model.getExpendTotal()));
            }
        }
        patientMemberInfoMapper.updateByPrimaryKeySelective(patientMemberInfo);
        //添加消费记录
        MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
        BeanUtils.copyProperties(model,memberExpendRecord);
        memberExpendRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        memberExpendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        memberExpendRecord.setCrtName(BaseContextHandler.getName());
        memberExpendRecordMapper.insertSelective(memberExpendRecord);
        return ResponseUtil.success();
    }

    /**
     * 消费记录
     * @param queryForm
     * @return MemberExpendRecordVo
     */
    public PageInfo<MemberExpendRecordVo> expendList(MemberExpendRecordQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<MemberExpendRecordVo> resultList = memberExpendRecordMapper.expendList(queryForm);
        if(resultList.size()>0){
            for (MemberExpendRecordVo memberExpendRecordVo : resultList) {
                OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(memberExpendRecordVo.getOrgId());//获取门诊简称
                if (organizationInfo != null) {
                    memberExpendRecordVo.setOrgName(organizationInfo.getAbbreviation());
                }
            }
        }
        return new PageInfo<>(resultList);
    }
}
