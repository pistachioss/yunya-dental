package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.model.PatientPrepaymentRelationModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidMeturnRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidRechargeModel;
import com.yunya.feign.patient_central.domain.query.PrepaidExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
public class PatientPrepaymentRelationBiz extends BaseBiz<PatientPrepaymentRelationMapper, PatientPrepaymentRelation> {

    @Autowired PatientPrepaymentRelationMapper patientPrepaymentRelationMapper;

    @Autowired PatientPrepaymentsInfoMapper patientPrepaymentsInfoMapper;

    @Autowired PrepaidRechargeTollRecordMapper prepaidRechargeTollRecordMapper;

    @Autowired PrepaidRechargeRecordMapper prepaidRechargeRecordMapper;

    @Autowired PatientBaseInfoMapper patientBaseInfoMapper;

    @Autowired RemoteSystemServiceFeign remoteSystemServiceFeign;

    @Autowired PrepaidReturnRecordMapper prepaidReturnRecordMapper;

    @Autowired PrepaidExpendRecordMapper prepaidExpendRecordMapper;

    /**
     * 患者预付款基本信息查询
     * @return PatientPrepaymentRelationVo
     */
    public PatientPrepaymentsInfoVo findPrepaymentInfo(Integer id) {
        return patientPrepaymentsInfoMapper.findPrepaymentInfo(id);
    }

    /**
     * 预付款关联
     * @param model
     */
    public ResponseResult addPrepaymentLink(PatientPrepaymentRelationModel model) {
        if (model.getMasterCardId() == model.getSecondaryCardId()) {
            return ResponseUtil.error("副卡人不能为患者本人！", "");
        }
        PatientPrepaymentRelation patientPrepaymentRelation = this.patientPrepaymentRelationMapper.findBindingRelation(model);
        if (patientPrepaymentRelation != null) {
            return ResponseUtil.error("该副卡人已存在,不能重复绑定！", patientPrepaymentRelation);
        }
        PatientPrepaymentRelation patientPrepaymentRelationyi = new PatientPrepaymentRelation();
        BeanUtils.copyProperties(model,patientPrepaymentRelationyi);
        patientPrepaymentRelationyi.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientPrepaymentRelationyi.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientPrepaymentRelationyi.setCrtName(BaseContextHandler.getName());
        patientPrepaymentRelationMapper.insertSelective(patientPrepaymentRelationyi);
        PatientPrepaymentRelation patientPrepaymentRelationer = new PatientPrepaymentRelation();
        patientPrepaymentRelationer.setMasterCardId(model.getSecondaryCardId());
        patientPrepaymentRelationer.setSecondaryCardId(model.getMasterCardId());
        patientPrepaymentRelationer.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        patientPrepaymentRelationer.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        patientPrepaymentRelationer.setCrtName(BaseContextHandler.getName());
        patientPrepaymentRelationMapper.insertSelective(patientPrepaymentRelationer);
        return ResponseUtil.success();
    }

    /**
     * 查询预付款关联
     * @param id
     * @return List<PatientPrepaymetRelation>
     */
    public List<PatientPrepaymentRelationVo> findPrepaymentLink(Integer id) {
        List<PatientPrepaymentRelationVo> prepaymentLinkList = patientPrepaymentRelationMapper.findPrepaymentLinkList(id);
        if(prepaymentLinkList.size() > 0){
            for (PatientPrepaymentRelationVo patientPrepaymentRelationVo : prepaymentLinkList) {
                PatientBaseInfo patientBaseInfo = patientBaseInfoMapper.selectByPrimaryKey(patientPrepaymentRelationVo.getSecondaryCardId());
                if(patientBaseInfo != null){
                    patientPrepaymentRelationVo.setSecondaryCardName(patientBaseInfo.getName());
                }
            }
        }
        return prepaymentLinkList;
    }

    /**
     * 删除预付款关联(双向删除)
     * @param id
     */
    public void deletePrepaymentLink(Integer id) {
        PatientPrepaymentRelation patientPrepaymentRelation = patientPrepaymentRelationMapper.selectByPrimaryKey(id);
        patientPrepaymentRelationMapper.deletePrepaymentRelation(patientPrepaymentRelation);
        patientPrepaymentRelationMapper.delete(patientPrepaymentRelation);
    }

    /**
     * 充值
     * @param model
     */
    public void Recharge(PrepaidRechargeModel model) {
        //查询会员余额 增加余额
        PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidId(),model.getPatientId());
        patientPrepaymentsInfo.setPrepaymentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal().add(model.getRechargePrincipal()));
        patientPrepaymentsInfo.setPrepaymentBonus(patientPrepaymentsInfo.getPrepaymentBonus().add(model.getRechargeBonus()));
        patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
        //添加预付款充值记录
        PrepaidRechargeRecord prepaidRechargeRecord = new PrepaidRechargeRecord();
        BeanUtils.copyProperties(model,prepaidRechargeRecord);
        prepaidRechargeRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        prepaidRechargeRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeRecord.setCrtName(BaseContextHandler.getName());
        prepaidRechargeRecordMapper.insertSelective(prepaidRechargeRecord);
        //添加预付款充值收费记录
        PrepaidRechargeTollRecord prepaidRechargeTollRecord = new PrepaidRechargeTollRecord();
        BeanUtils.copyProperties(model.getPrepaidRechargeTollRecordModel(),prepaidRechargeTollRecord);
        prepaidRechargeTollRecord.setRechargeRecordId(prepaidRechargeRecord.getId());
        prepaidRechargeTollRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        prepaidRechargeTollRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidRechargeTollRecord.setCrtName(BaseContextHandler.getName());
        prepaidRechargeTollRecordMapper.insertSelective(prepaidRechargeTollRecord);
    }

    /**
     * 充值记录
     * @param form
     * @return
     */
    public PageInfo<PrepaidRechargeRecordVo> RechargeRecord(PrepaidRechargeRecordQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        form.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        List<PrepaidRechargeRecordVo> resultList = prepaidRechargeRecordMapper.RechargeRecord(form);
        for (PrepaidRechargeRecordVo prepaidRechargeRecordVo : resultList) {
            OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(prepaidRechargeRecordVo.getOrgId());//获取门诊简称
            if (organizationInfo != null) {
                prepaidRechargeRecordVo.setOrgName(organizationInfo.getAbbreviation());
            }
            AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(prepaidRechargeRecordVo.getPaymentId());
            if(accountItem != null){
                prepaidRechargeRecordVo.setPaymentName(accountItem.getName()); //获取支付方式名称
            }
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 预付款退费
     * @param model
     */
    public void refund(PrepaidMeturnRecordModel model) {
        //查询会员余额 退减余额
        PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidId(),model.getPatientId());
        patientPrepaymentsInfo.setPrepaymentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal().subtract(model.getReturnPrincipalAmount()));
        patientPrepaymentsInfo.setPrepaymentBonus(patientPrepaymentsInfo.getPrepaymentBonus().subtract(model.getReturnGiftAmount()));
        patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
        //添加会员卡退费记录
        PrepaidReturnRecord prepaidReturnRecord = new PrepaidReturnRecord();
        BeanUtils.copyProperties(model,prepaidReturnRecord);
        prepaidReturnRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(prepaidReturnRecord.getReturnWayId());
        if(accountItem != null){
            prepaidReturnRecord.setReturnWayType(accountItem.getName());//获取退费方式类型名称
        }
        prepaidReturnRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidReturnRecord.setCrtName(BaseContextHandler.getName());
        prepaidReturnRecordMapper.insertSelective(prepaidReturnRecord);
    }


    /**
     * 退费记录列表
     * @param queryForm
     * @return MemberReturnRecordVo
     */
    public PageInfo<PrepaidMeturnRecordVo> refundList(PrepaidMeturnRecordQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<PrepaidMeturnRecordVo> resultList = prepaidReturnRecordMapper.refundList(queryForm);
        for (PrepaidMeturnRecordVo prepaidMeturnRecordVo : resultList) {
            OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(prepaidMeturnRecordVo.getOrgId());//获取门诊简称
            if (organizationInfo != null) {
                prepaidMeturnRecordVo.setOrgName(organizationInfo.getAbbreviation());
            }
            AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(prepaidMeturnRecordVo.getReturnWayId());
            if(accountItem != null){
                prepaidMeturnRecordVo.setReturnWayType(accountItem.getName()); //获取支付方式名称
            }
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 消费记录
     * @param queryForm
     * @return
     */
    public PageInfo<PrepaidExpendRecordVo> expendList(PrepaidExpendRecordQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<PrepaidExpendRecordVo> resultList = prepaidExpendRecordMapper.expendList(queryForm);
        if(resultList.size()>0){
            for (PrepaidExpendRecordVo prepaidExpendRecordVo : resultList) {
                OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(prepaidExpendRecordVo.getOrgId());//获取门诊简称
                if (organizationInfo != null) {
                    prepaidExpendRecordVo.setOrgName(organizationInfo.getAbbreviation());
                }
            }
        }
        return new PageInfo<>(resultList);
    }

    /**
     * 预付款消费记录
     * @param model
     * @return ResponseResult
     */
    public ResponseResult expend(PrepaidExpendRecordModel model) {
        PatientPrepaymentsInfo patientPrepaymentsInfo = patientPrepaymentsInfoMapper.selectOneByCardNumber(model.getPrepaidId(), model.getPatientId());
        if(patientPrepaymentsInfo.getPrepaymentPrincipal().add(patientPrepaymentsInfo.getPrepaymentBonus()).compareTo(model.getExpendTotal()) == -1 ){ //如果本金+赠金 小于 消费金额
            return ResponseUtil.error("预付款余额不足",patientPrepaymentsInfo);
        }
        spending(model,patientPrepaymentsInfo);
        return ResponseUtil.success();
    }

    /**
     * 消费抵扣
     * @param model
     * @param patientMemberInfo
     */
    public void spending(PrepaidExpendRecordModel model, PatientPrepaymentsInfo patientPrepaymentsInfo){
        BigDecimal expendePrincipal = null; //消费本金
        BigDecimal expendeBonus = null; //消费赠金
        BigDecimal principalAmount = null; //账户本金
        BigDecimal bonusAmount = null; //账户赠金
        PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord(); //创建消费记录对象
        BeanUtils.copyProperties(model,prepaidExpendRecord);
        if(patientPrepaymentsInfo.getPrepaymentPrincipal().compareTo(model.getExpendTotal()) == -1 ) { //会员卡余额 小于 消费金额
            //小于的情况下 依然先用本金去抵扣消费金额
            principalAmount = patientPrepaymentsInfo.getPrepaymentPrincipal(); //获取本金
            BigDecimal surplus = patientPrepaymentsInfo.getPrepaymentPrincipal().subtract(model.getExpendTotal()); //本金-消费总额
            patientPrepaymentsInfo.setPrepaymentPrincipal(new BigDecimal(0)); //本金已用完
            prepaidExpendRecord.setExpendPrincipal(principalAmount); //获取消费本金
            bonusAmount = patientPrepaymentsInfo.getPrepaymentBonus(); //获取赠金
            patientPrepaymentsInfo.setPrepaymentBonus(patientPrepaymentsInfo.getPrepaymentBonus().add(surplus)); //用赠金去抵扣
            expendeBonus = bonusAmount.subtract(patientPrepaymentsInfo.getPrepaymentBonus());//原账户赠金-抵扣后赠金余额 = 用了多少赠金
            prepaidExpendRecord.setExpendGift(expendeBonus);//获取消费赠金
        }else {
            principalAmount = patientPrepaymentsInfo.getPrepaymentPrincipal();
            patientPrepaymentsInfo.setPrepaymentPrincipal(patientPrepaymentsInfo.getPrepaymentPrincipal().subtract(model.getExpendTotal()));
            expendePrincipal = principalAmount.subtract(patientPrepaymentsInfo.getPrepaymentPrincipal());//消费金额
            prepaidExpendRecord.setExpendPrincipal(expendePrincipal); //获取消费本金
        }
        patientPrepaymentsInfoMapper.updateByPrimaryKeySelective(patientPrepaymentsInfo);
        //添加消费记录
        prepaidExpendRecord.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
        prepaidExpendRecord.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
        prepaidExpendRecord.setCrtName(BaseContextHandler.getName());
        prepaidExpendRecordMapper.insertSelective(prepaidExpendRecord);
    }

}
