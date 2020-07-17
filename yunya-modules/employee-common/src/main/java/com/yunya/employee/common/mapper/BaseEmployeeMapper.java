package com.yunya.employee.common.mapper;

import com.yunya.clinic.employee.common.entity.BaseEmployee;
import com.yunya.clinic.employee.common.model.response.BaseEmployeePageRes;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseEmployeeMapper extends Mapper<BaseEmployee> {
    /**
     * 查询
     *
     * @return
     */
    List<BaseEmployee> selectByNameAndTelAndType(@Param("name") String name, @Param("mobilePhone") String mobilePhone, @Param("type") Integer type);

    /**
     * 查询公司端员工列表
     * @param employeeKeyword
     * @param postUserIds
     * @param types
     * @return
     */
    List<BaseEmployeePageRes> getBaseEmployeeList(@Param("employeeKeyword") String employeeKeyword, @Param("postUserIds") List<Integer> postUserIds, @Param("types") List<Integer> types);

    /**
     * 查询员工姓名存在的数量（不包含自己）
     * @param employeeId
     * @param employeeName
     * @return
     */
    int countByEmployeeName(@Param("employeeId") Integer employeeId, @Param("employeeName") String employeeName);

    /**
     * 查询员工身份证号码存在的数量（不包含自己）
     * @param employeeId
     * @param idCard
     * @return
     */
    int countByIdCard(@Param("employeeId") Integer employeeId, @Param("idCard") String idCard);

    /**
     * 查询员工电话号码存在的数量（不包含自己）
     * @param employeeId
     * @param mobilePhone
     * @return
     */
    int countByMobilePhone(@Param("employeeId") Integer employeeId, @Param("mobilePhone") String mobilePhone);
}