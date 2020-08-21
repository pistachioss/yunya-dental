package com.yunya.modules.treatment.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.OrderDetailBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介: 开单明细管理控制器
 *
 * @author: chow
 * @date: 2020/8/21 12:47
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "开单明细管理（新增、修改、删除、查询）")
@RestController
@RequestMapping("details")
public class OrderDetailController {
  /** 注入对象 */
  @Autowired private OrderDetailBiz orderDetailBiz;

  /**
   * 根据开单明细ID删除开单明细
   *
   * @param id 开单明细ID
   * @return
   */
  @ApiOperation("根据开单明细ID删除开单明细")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable(value = "id") Integer id) {
    orderDetailBiz.deleteById(id);
    return ResponseUtil.success();
  }
}
