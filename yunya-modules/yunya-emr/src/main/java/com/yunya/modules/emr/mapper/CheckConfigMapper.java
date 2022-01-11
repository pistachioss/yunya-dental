package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.vo.CheckConfigVO;
import com.yunya.framework.common.model.PageQuery;
import com.yunya.models.emr.CheckConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CheckConfigMapper extends Mapper<CheckConfig> {
    List<CheckConfigVO> selectCheckConfigList(@Param("query") PageQuery query);
}