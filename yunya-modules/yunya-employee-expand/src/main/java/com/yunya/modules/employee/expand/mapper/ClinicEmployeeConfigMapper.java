package com.yunya.modules.employee.expand.mapper;

import com.yunya.models.expand.ClinicEmployeeConfig;
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

    /**
     * 批量更新数据
     * @param list
     */
    Integer updateBatch(@Param("list") List<ClinicEmployeeConfig> list);
}