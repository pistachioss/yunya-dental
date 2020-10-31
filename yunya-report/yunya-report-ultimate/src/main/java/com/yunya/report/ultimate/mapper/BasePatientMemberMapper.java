package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.MemberOverviewQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberOverviewVo;
import com.yunya.feign.report.domain.vo.BasePatientMemberOverviewVo;
import com.yunya.models.report.BasePatientMember;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BasePatientMemberMapper extends Mapper<BasePatientMember> {

    /**
     * 查询会员卡概况
     * @param form 查询会员卡概况form
     * @param patientIds 患者ids
     * @return List<MemberOverviewVo>
     */
    List<BasePatientMemberOverviewVo> selectMemberOverviewList(@Param("form") MemberOverviewQueryForm form, @Param("patientIds") List<Integer> patientIds);

    /**
     * 会员卡概况
     * @return List<MemberOverviewVo>
     */
    List<BaseMemberOverviewVo> memberOverviewList();
}