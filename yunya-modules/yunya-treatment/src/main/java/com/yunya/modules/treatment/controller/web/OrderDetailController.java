package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillCategoryIncomeQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.vo.CategoryInfoIncomeVO;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.feign.treatment.domain.form.BillPrintInfoForm;
import com.yunya.feign.treatment.domain.form.ModificationExecutorForm;
import com.yunya.feign.treatment.domain.model.GoodsDetailModel;
import com.yunya.feign.treatment.domain.vo.BillPrintInfoVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.OrderDetailBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
@Slf4j
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

  /**
   * 打印账单信息
   * @return 返回账单信息
   */
  @ApiOperation("打印账单信息")
  @PostMapping("/bill/print")
  public ResponseResult<BillPrintInfoVO> billPrintInfo(
          @RequestBody @Validated BillPrintInfoForm billPrintInfoForm
          ){
  BillPrintInfoVO billPrintInfoVO = orderDetailBiz.billPrintInfo(billPrintInfoForm);
    if (billPrintInfoVO == null) {
      log.info("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓账单打印异常信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
      log.info("==> path:/details/bill/print/{}/{}",billPrintInfoForm.getPatientId(),billPrintInfoForm.getBillNumber());
      log.info("==> param:patientId={},billNumber={}",billPrintInfoForm.getPatientId(),billPrintInfoForm.getBillNumber());
      log.info("==> Msg:没有查询到账单信息");
      log.info("==> status:{}",OperationCodeConstants.DATA_NOT_EXIST);
      log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
      return ResponseUtil.fail(OperationCodeConstants.DATA_NOT_EXIST,"没有查询到账单信息",null);
    }
    return ResponseUtil.success(billPrintInfoVO);
  }

  /**
   * 根据条件查询专科数量目标
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-专科数量目标")
  @PostMapping(value = "/specialist/completed/list", name = "根据条件查询专科数量目标")
  public ResponseResult<PageInfo<SpecialistProjectCompletedInfoVO>>
  specialistProjectTargetCompletedList(
          @RequestBody @Validated DataStatisticsQuery query) {
    PageInfo<SpecialistProjectCompletedInfoVO> resultList =
            orderDetailBiz.specialistProjectTargetCompletedList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询专科数量目标导出
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-专科数量目标导出")
  @PostMapping(value = "/specialist/completed/export", name = "根据条件查询专科数量目标导出")
  public ResponseResult<PageInfo<SpecialistProjectCompletedInfoVO>>
  specialistProjectTargetCompletedExport(
          HttpServletResponse response, @RequestBody @Validated DataStatisticsQuery query) throws IOException {
    orderDetailBiz.specialistProjectTargetCompletedExport(query, response);
    return ResponseUtil.success(null);
  }


  /**
   * 根据条件查询项目分类收入汇总列表
   *
   * @param query 查询条件
   * @return PageInfo<CategoryInfoIncomeVO>
   */
  @ApiOperation("公司端报表-财务报表-分类收入汇总")
  @PostMapping(value = "/category/income/list", name = "billDetailBiz")
  public ResponseResult<PageInfo<CategoryInfoIncomeVO>> categoryIncomeList(
          @RequestBody @Validated BillCategoryIncomeQuery query) throws Exception {
    PageInfo<CategoryInfoIncomeVO> resultList = orderDetailBiz.findCategoryIncomeList(query);
    return ResponseUtil.success(resultList);
  }


  /**
   * 根据条件导出项目分类收入汇总列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-财务报表-分类收入汇总-导出")
  @PostMapping(value = "/category/income/export", name = "根据条件导出项目分类收入汇总列表")
  public ResponseResult<T> exportCategoryIncome(
          HttpServletResponse response, @RequestBody @Validated BillCategoryIncomeQuery query)
          throws Exception {
    orderDetailBiz.exportCategoryIncome(response, query);
    return ResponseUtil.success(null);
  }
}
