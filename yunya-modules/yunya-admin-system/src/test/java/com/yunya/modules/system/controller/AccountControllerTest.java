package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.ClinicAccountItemConfigureQueryForm;
import com.yunya.feign.system.vo.AccountItemVO;
import com.yunya.feign.system.vo.ClinicAccountItemListVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.domain.query.ClinicAccountItemQueryForm;
import com.yunya.feign.system.vo.ClinicAccountItemVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 入账方式管理测试
 *
 * @author: chow
 * @date: 2020/9/29 11:18
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountControllerTest {
  @Autowired private ClinicAccountItemController clinicAccountItemController;

  @Test
  public void testFind() {
    ResponseResult<ClinicAccountItemVO> result = clinicAccountItemController.findById(128);
    System.out.println(result);
  }

  @Test
  public void list() {
    ClinicAccountItemQueryForm query = new ClinicAccountItemQueryForm();
    ResponseResult<PageInfo<ClinicAccountItemVO>> list =
        clinicAccountItemController.findList(query);
    System.out.println(list);
  }

  @Test
  public void findList() {
    ClinicAccountItemConfigureQueryForm form = new ClinicAccountItemConfigureQueryForm();
    form.setAccountItemId(62);
    ResponseResult<PageInfo<ClinicAccountItemVO>> result =
        clinicAccountItemController.configureClinicAccountItem(form);
    System.out.println(result);
  }

  @Test
  public void findClinicAccount() {
    ResponseResult<ClinicAccountItemListVO> result =
        clinicAccountItemController.clinicAccountItemList(156);
    ClinicAccountItemListVO data = result.getData();
    List<AccountItemVO> items = data.getClinicAccountItems();
    items.forEach(System.out::println);
    System.out.println(result);
  }
}
