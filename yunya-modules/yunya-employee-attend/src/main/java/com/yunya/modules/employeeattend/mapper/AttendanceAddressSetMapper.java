package com.yunya.modules.employeeattend.mapper;


import com.yunya.feign.employee_attend.form.AttendanceAddressSetQueryForm;
import com.yunya.feign.employee_attend.vo.AttendanceAddressSetVO;
import com.yunya.models.employee_attend.AttendanceAddressSet;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttendanceAddressSetMapper extends Mapper<AttendanceAddressSet> {
    
    /**
     * 分页查询考勤地址设置列表
     *
     * @param queryForm 查询参数
     * @return 
     */
    List<AttendanceAddressSetVO> findAttendanceAddressSetList(@Param("queryForm") AttendanceAddressSetQueryForm queryForm);

    /**
     * 根据id查询考勤地址设置信息
     *
     * @param id 主键id
     * @return
     */
    AttendanceAddressSetVO findAttendanceAddressSetById(@Param("id") Integer id);

    /**
     * 根据orgId查询考勤地址列表
     *
     * @param orgIds
     * @return
     */
    List<AttendanceAddressSetVO> selectAttendanceAddressByOrgId(@Param("orgIds") List<Integer> orgIds);
}