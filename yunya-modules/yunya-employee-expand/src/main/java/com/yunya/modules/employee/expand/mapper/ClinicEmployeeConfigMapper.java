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

    /**
     * 根据员工ID和门诊ID删除配置信息
     * @param employeeId 员工ID
     * @param clinicId  门诊ID
     * @return
     */
    Integer deleteEmployeeConfig(@Param("employeeId") Integer employeeId, @Param("clinicId") Integer clinicId);

    /**
     * 批量查询
     * @param orgId   组织ID
     * @param userIds 用户ID列表
     * @return
     */
    List<ClinicEmployeeConfig> findBatch(@Param("orgId") Integer orgId, @Param("userIds") List<Integer> userIds);
}