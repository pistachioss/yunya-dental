package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.ClinicDeptRoomConfigureQueryForm;
import com.yunya.feign.system.vo.ClinicDepartmentRoomVO;
import com.yunya.feign.system.vo.ClinicDeptRoomListVO;
import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/7/28 09:33
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClinicDepartmentRoomControllerTest {
  /** 注入对象 */
  @Autowired private ClinicDepartmentRoomController clinicDepartmentRoomController;

  @Test
  public void testSwitch() {
    ResponseResult result = clinicDepartmentRoomController.switchDeptRoomDisable(107);
    System.out.println(result);
  }

  @Test
  public void saveAll() {
    ResponseResult result = clinicDepartmentRoomController.oneClickAdd(4);
    System.out.println(result);
  }

  @Test
  public void findList() {
    ClinicDeptRoomConfigureQueryForm form = new ClinicDeptRoomConfigureQueryForm();
    form.setDeptRoomId(1);
    ResponseResult<PageInfo<ClinicDepartmentRoomVO>> result =
        clinicDepartmentRoomController.configureClinicDeptRoom(form);
    System.out.println(result);
  }

  @Test
  public void testOrgList() {
    ResponseResult<ClinicDeptRoomListVO> result =
        clinicDepartmentRoomController.clinicDeptRoomList(35);
    System.out.println(result);
  }
}
