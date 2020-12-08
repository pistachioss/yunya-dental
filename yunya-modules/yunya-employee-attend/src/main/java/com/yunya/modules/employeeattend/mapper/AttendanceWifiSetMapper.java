package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendanceWifiSetQueryForm;
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

    /**
     * 根据id查询考勤Wifi设置信息
     *
     * @param id 主键id
     * @return ResponseResult<AttendanceWifiSetVO>
     */
    AttendanceWifiSetVO findAttendanceWifiSetById(@Param("id") Integer id);

    /**
     * 根据wifi的mac获取考勤WIFI设置
     *
     * @param macAddresss
     * @return
     */
    AttendanceWifiSetVO findAttendanceWifiSetByMac(@Param("macAddress") String macAddresss);
}