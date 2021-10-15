package com.yunya.modules.employee.expand;

import com.yunya.modules.employee.expand.controller.ClinicEmployeeController;
import com.yunya.modules.employee.expand.model.request.ClinicEmployeeConfigReq;
import com.yunya.modules.employee.expand.model.response.EnableChooseEmployeeRes;
import com.yunya.modules.employee.expand.service.ClinicEmployeeConfigBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeCommonApplicationTests {
  @Autowired private ClinicEmployeeController clinicEmployeeController;
  @Resource private ClinicEmployeeConfigBiz clinicEmployeeConfigBiz;

  @Test
  public void contextLoads() {}

  @Test
  public void test1() {
    ClinicEmployeeConfigReq req = new ClinicEmployeeConfigReq();
    req.setAssistantEmployeeId(null);
    req.setClinicDepartmentRoomId(1);
    req.setEnableAppoint(1);
    req.setEnableRegistry(1);
    clinicEmployeeController.modifyEmployeeConfig(35, 569, req);
  }

  @Test
  public void find() {
    List<EnableChooseEmployeeRes> list =
        clinicEmployeeConfigBiz.findEnableAppointEmployeeList(26, false);
    System.out.println(list);
  }
}
