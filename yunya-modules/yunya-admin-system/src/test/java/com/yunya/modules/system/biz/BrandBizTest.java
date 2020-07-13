package com.yunya.modules.system.biz;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.PageQueryParams;
import com.yunya.modules.system.entity.Brand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * 简介: 品牌业务层测试
 *
 * @author: chow
 * @date: 2020/7/12 14:58
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BrandBizTest {

  @Autowired private BrandBiz brandBiz;

  /** 根据条件查询 */
  @Test
  public void testList() {
    Map<String, Object> map = new HashMap<>();
    map.put("whetherPage",true);
    PageQueryParams query = new PageQueryParams(map);

    query.setPageNum(1);
    query.setPageSize(3);
    PageInfo<Brand> info = brandBiz.selectByQuery(query);
    System.out.println(info);
  }
}
