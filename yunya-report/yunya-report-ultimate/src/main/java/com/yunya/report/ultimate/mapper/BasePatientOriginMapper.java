package com.yunya.report.ultimate.mapper;

import com.yunya.feign.patient_central.domain.vo.web.ActivityVo;
import com.yunya.feign.report.domain.query.ClinicPerformanceBusinessQuery;
import com.yunya.models.report.BasePatientOrigin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author WY
 */
@Repository
public interface BasePatientOriginMapper extends Mapper<BasePatientOrigin> {

    /**
     * 获取活动列表
     * @return 活动列表
     */
    List<ActivityVo> selectActivityList();

    /**
     * 根据条件查询患者来源大类
     *
     * @param query
     * @return
     */
    List<BasePatientOrigin> selectPatientOriginList(@Param("query") ClinicPerformanceBusinessQuery query);
}