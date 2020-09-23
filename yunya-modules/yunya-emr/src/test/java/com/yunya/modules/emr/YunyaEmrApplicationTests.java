package com.yunya.modules.emr;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.employee_attend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.EmployeeScheduleResultVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YunyaEmrApplicationTests {
    @Autowired
    private EmployeeAttendServiceFeign employeeAttendServiceFeign;

    @Test
    void contextLoads() {
        EmployeeScheduleQueryForm employeeScheduleQueryForm = new EmployeeScheduleQueryForm();
        employeeScheduleQueryForm.setStartDate("2020-09-24");
        employeeScheduleQueryForm.setEndDate("2020-09-24");
        employeeScheduleQueryForm.setClinicId(42);
        EmployeeScheduleResultVO vo = new EmployeeScheduleResultVO();
        vo = employeeAttendServiceFeign.findList(employeeScheduleQueryForm);
        System.out.println(vo);
    }


}
