package com.yunya.modules.emr.mapper;

import com.yunya.feign.emr.domain.query.SymptomConfigQuery;
import com.yunya.feign.emr.domain.vo.SymptomConfigVO;
import com.yunya.models.emr.SymptomConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SymptomConfigMapper extends Mapper<SymptomConfig> {
    List<SymptomConfigVO> selectSymptomConfigList(@Param("query") SymptomConfigQuery query);
}