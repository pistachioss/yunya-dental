package com.yunya.modules.treatment.controller.web;

import com.yunya.feign.treatment.domain.form.ModificationExecutorForm;
import com.yunya.feign.treatment.domain.model.GoodsDetailModel;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.OrderDetailBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
   * 根据账单（开单）记录ID查询商品开单详情列表
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  @ApiOperation("根据开单记录ID查询商品开单详情列表")
  @GetMapping("/goods/list/{orderRecordId}")
  public ResponseResult<List<OrderDetailVO>> findGoodsList(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    List<OrderDetailVO> resultList = orderDetailBiz.findGoodsDetailVOList(orderRecordId);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据账单（开单）记录ID查询开单详情列表
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  @CurrentUser
  @ApiOperation("根据开单记录ID查询开单详情列表（收费订单明细查询）")
  @GetMapping("/list/{orderRecordId}")
  public ResponseResult<List<OrderDetailChargeVO>> findChargeOrderList(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    List<OrderDetailChargeVO> resultList = orderDetailBiz.findChargeOrderDetailList(orderRecordId);
    return ResponseUtil.success(resultList);
  }

  /**
   * 添加商品
   *
   * @param model 商品参数
   * @return
   */
  @CurrentUser
  @ApiOperation("添加商品（收费界面用）")
  @PostMapping("/add/goods")
  public ResponseResult<T> addGoods(@RequestBody @Validated GoodsDetailModel model) {
    orderDetailBiz.addAndUpdGoodDetail(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据开单明细ID删除开单明细
   *
   * @param id 开单明细ID
   * @return
   */
  @CurrentUser
  @ApiOperation("根据开单明细ID删除开单明细")
  @DeleteMapping("/delete/{id}")
  public ResponseResult<T> delete(@PathVariable(value = "id") Integer id) {
    orderDetailBiz.deleteOrderDetailById(id);
    return ResponseUtil.success(null);
  }

  /**
   * 修改执行人（患者档案）
   *
   * @param form 修改表单
   * @return responseResult
   */
  @ApiOperation("修改执行人（患者档案）")
  @PutMapping("/modification/executor")
  @CurrentUser
  public ResponseResult<T> modificationExecutor(
      @RequestBody @Validated List<ModificationExecutorForm> form) {
    orderDetailBiz.modificationExecutor(form);
    return ResponseUtil.success(null);
  }
}
