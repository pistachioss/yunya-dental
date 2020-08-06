package com.yunya.modules.patient_central.biz;

import com.yunya.feign.patient_central.domain.model.MemberBindingRelationInfoModel;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.MemberBaseInfoVO;
import com.yunya.feign.patient_central.domain.vo.MemberRelationVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.patient_central.PatientMemberRelation;
import com.yunya.models.system.MemberType;
import com.yunya.modules.patient_central.mapper.PatientMemberInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientMemberRelationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 根据患者id查询会员基本信息
     * @return MemberBaseInfoVO
     */
    public MemberBaseInfoVO findMemberBaseInfo(Integer id) {
        MemberBaseInfoVO memberBaseInfoVO = patientMemberInfoMapper.findMemberBaseInfo(id);
        MemberType memberType = remoteSystemServiceFeign.findMemberTypeById(memberBaseInfoVO.getMemberTypeId());
        memberBaseInfoVO.setMemberCardName(memberType.getName());
        return memberBaseInfoVO;
    }

    /**
     * 查询会员卡关联关系
     * @param form
     * @return List<PatientMemberRelationVO>
     */
    public MemberRelationVO findMemberBindingRelation(PatientMemberRelationQueryForm form) {
        MemberRelationVO memberRelationVO = new MemberRelationVO();
        form.setBindType(0);
        memberRelationVO.setMemberRelationList(patientMemberInfoMapper.findMemberBindingRelation(form));
        form.setBindType(1);
        patientMemberInfoMapper.findMemberBindingRelation(form);
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
            patientMemberRelationMapper.insert(patientMemberRelation);
        }else {
            patientMemberRelation.setOrgId(Integer.parseInt(BaseContextHandler.getOrgId()));
            patientMemberRelation.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
            patientMemberRelation.setCrtName(BaseContextHandler.getName());
            patientMemberRelationMapper.insert(patientMemberRelation);
            patientMemberRelation.setMasterCardId(patientMemberRelation.getSecondaryCardId());
            patientMemberRelation.setSecondaryCardId(patientMemberRelation.getMasterCardId());
            patientMemberRelationMapper.insert(patientMemberRelation);
        }

    }
}
