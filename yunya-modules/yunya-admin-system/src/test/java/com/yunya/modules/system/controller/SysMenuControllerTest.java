package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.modules.system.domain.form.MenuElementForm;
import com.yunya.modules.system.vo.tree.SysMenuElementTreeVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 系统菜单测试
 *
 * @author: chow
 * @date: 2021/1/8 17:02
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysMenuControllerTest {

  @Autowired private SysMenuController sysMenuController;

  @Test
  public void find() {
    MenuElementForm query = new MenuElementForm();
    query.setSystemId(0);
    query.setMenuType((byte) 1);
    ResponseResult<List<SysMenuElementTreeVO>> result = sysMenuController.getMenuElementTree(query);
    System.out.println(result);
  }
}
