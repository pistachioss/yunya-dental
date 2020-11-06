package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendanceWifiSetQueryForm;
import com.yunya.feign.employee_attend.vo.AttendanceAddressSetVO;
import com.yunya.feign.employee_attend.vo.AttendanceWifiSetVO;
import com.yunya.models.employee_attend.AttendanceWifiSet;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttendanceWifiSetMapper extends Mapper<AttendanceWifiSet> {
    
    /**
     * 分页查询考勤Wifi设置列表
     *
     * @param queryForm 查询参数
     * @return 
     */
    List<AttendanceWifiSetVO> findAttendanceWifiSetList(@Param("queryForm") AttendanceWifiSetQueryForm queryForm);


    AttendanceWifiSetVO findAttendanceWifiSetById(@Param("id") Integer id);
}