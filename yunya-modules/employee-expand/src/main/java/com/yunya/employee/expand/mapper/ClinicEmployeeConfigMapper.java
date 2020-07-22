package com.yunya.employee.expand.mapper;

import com.yunya.models.epcommon.ClinicEmployeeConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * @author bruce
 */
public interface ClinicEmployeeConfigMapper extends Mapper<ClinicEmployeeConfig> {

    /**
     * @param list
     */
    void insertBatch(@Param("list") List<ClinicEmployeeConfig> list);
}