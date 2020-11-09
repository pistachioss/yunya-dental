package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.AttendanceDeviceBindingQueryForm;
import com.yunya.feign.employee_attend.vo.AttendanceDeviceBindingVO;
import com.yunya.models.employee_attend.AttendanceDeviceBinding;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttendanceDeviceBindingMapper extends Mapper<AttendanceDeviceBinding> {

    /**
     * 根据userid分页查询员工的考勤设备的绑定记录列表
     *
     * @param queryForm 查询参数
     * @return
     */
    List<AttendanceDeviceBindingVO> findBindingDeviceList(@Param("queryForm") AttendanceDeviceBindingQueryForm queryForm);
}