package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.model.PatientPrepaymentRelationModel;
import com.yunya.feign.patient_central.domain.model.PrepaidMeturnRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidRechargeModel;
import com.yunya.feign.patient_central.domain.query.PrepaidMeturnRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PrepaidRechargeRecordQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientPrepaymentRelationVo;
import com.yunya.feign.patient_central.domain.vo.PatientPrepaymentsInfoVo;
import com.yunya.feign.patient_central.domain.vo.PrepaidMeturnRecordVo;
import com.yunya.feign.patient_central.domain.vo.PrepaidRechargeRecordVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.patient_central.*;
import com.yunya.models.system.AccountItem;
import com.yunya.modules.patient_central.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void addPrepaymentLink(PatientPrepaymentRelationModel model) {
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
            AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(prepaidRechargeRecordVo.getPaymentId()); // todo 充值记录 待确认
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
        AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(prepaidReturnRecord.getReturnWayId()); // todo 充值记录 待确认
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
            AccountItem accountItem = remoteSystemServiceFeign.findAccountItemById(prepaidMeturnRecordVo.getReturnWayId()); // todo 充值记录 待确认
            if(accountItem != null){
                prepaidMeturnRecordVo.setReturnWayType(accountItem.getName()); //获取支付方式名称
            }
        }
        return new PageInfo<>(resultList);
    }
}
