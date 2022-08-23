package com.yunya.modules.system.mapper;

import com.yunya.models.system.ClinicLiveCode;
import com.yunya.modules.system.domain.query.ClinicLiveCodeQueryForm;
import com.yunya.modules.system.vo.ClinicLiveCodeVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicLiveCodeMapper extends Mapper<ClinicLiveCode> {
    /**
     * 条件查询门店店长活码列表
     *
     * @param query
     * @return
     */
    List<ClinicLiveCodeVO> selectClinicLiveCodeList(@Param("query") ClinicLiveCodeQueryForm query);
}