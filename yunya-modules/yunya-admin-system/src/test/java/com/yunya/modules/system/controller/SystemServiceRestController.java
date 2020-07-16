package com.yunya.modules.system.controller;

import com.yunya.feign.system.form.BrandModel;
import com.yunya.modules.system.entity.Brand;
import com.yunya.modules.system.rpc.SystemServiceRest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介: 暴露接口测试
 *
 * @author: chow
 * @date: 2020/7/16 09:30
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SystemServiceRestController {

  /** 注入对象 */
  @Autowired private SystemServiceRest systemServiceRest;

  @Test
  public void test() {
    BrandModel model = new BrandModel();
    List<Brand> list = systemServiceRest.findBrandList(model);
    System.out.println(list);
  }
}
