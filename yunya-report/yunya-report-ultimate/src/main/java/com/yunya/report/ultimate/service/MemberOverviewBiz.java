package com.yunya.report.ultimate.service;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.report.domain.query.MemberOverviewQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberOverviewVo;
import com.yunya.feign.report.domain.vo.BasePatientMemberOverviewVo;
import com.yunya.feign.report.domain.vo.MemberInfoSumVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BasePatientMember;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BasePatientMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介: 会员卡概况业务层
 *
 * @author: WY
 * @date: 2020/10/27 10:10
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOverviewBiz extends BaseBiz<BasePatientMemberMapper, BasePatientMember> {

    @Autowired private MemberOccurLogBiz memberOccurLogBiz;

    @Autowired private BasePatientMapper basePatientMapper;


    /**
     * 会员卡概况查询
     * @param form 概况查询form
     * @return List<MemberOverviewVo>
     */
    public List<BasePatientMemberOverviewVo> patientOverviewList(MemberOverviewQueryForm form) throws ParseException {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BasePatientMemberOverviewVo> basePatientMemberOverviewVoList = mapper.selectMemberOverviewList(form,patientIds);
        return basePatientMemberOverviewVoList;
    }


    /**
     * 会员卡概况
     * @return  Map<String,Object>
     */
    public Map<String,Object> memberList() {
        Map<String,Object> map = new HashMap<String,Object>(16);
        Integer sumAmount = 0;
        BigDecimal sumPrincipalAmount = new BigDecimal(0);
        BigDecimal sumBonusAmount = new BigDecimal(0);
        List<BaseMemberOverviewVo> baseMemberOverviewVos = mapper.memberOverviewList();
        for(BaseMemberOverviewVo baseMemberOverviewVo : baseMemberOverviewVos){
            if (baseMemberOverviewVo.getAmount() > 0 ){
                sumAmount = sumAmount +  baseMemberOverviewVo.getAmount();
            }
            if (baseMemberOverviewVo.getPrincipalAmount().compareTo(new BigDecimal(0)) > 0){
                sumPrincipalAmount = sumPrincipalAmount.add(baseMemberOverviewVo.getPrincipalAmount());
            }
            if (baseMemberOverviewVo.getBonusAmount().compareTo(new BigDecimal(0)) > 0){
                sumBonusAmount =  sumBonusAmount.add(baseMemberOverviewVo.getBonusAmount());
            }
        };
        map.put("baseMemberOverviewVoList",baseMemberOverviewVos);
        map.put("sumAmount",sumAmount);
        map.put("sumPrincipalAmount",sumPrincipalAmount);
        map.put("sumBonusAmount",sumBonusAmount);
        return map;
    }
}