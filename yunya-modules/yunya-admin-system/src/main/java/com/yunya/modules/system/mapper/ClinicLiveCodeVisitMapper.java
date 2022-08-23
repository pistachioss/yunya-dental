package com.yunya.modules.system.mapper;

import com.yunya.models.system.ClinicLiveCodeVisit;
import com.yunya.modules.system.domain.query.ClinicLiveCodeVisitQueryForm;
import com.yunya.modules.system.vo.ClinicLiveCodeVisitVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicLiveCodeVisitMapper extends Mapper<ClinicLiveCodeVisit> {
    /**
     * 根据openId查询用户点击记录列表
     *
     * @param openId
     * @return
     */
    List<ClinicLiveCodeVisit> selectClinicLiveCodeVisitByOpenId(@Param("openId") String openId);

    /**
     * 条件查询
     *
     * @param query
     * @return
     */
    List<ClinicLiveCodeVisitVO> selectClinicLiveCodeVisitList(@Param("query") ClinicLiveCodeVisitQueryForm query);
}