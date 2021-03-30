package com.yunya.report.ultimate.mapper;

import com.yunya.models.report.BasePatientOrigin;
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
    List<BasePatientOrigin> selectActivityList();
}