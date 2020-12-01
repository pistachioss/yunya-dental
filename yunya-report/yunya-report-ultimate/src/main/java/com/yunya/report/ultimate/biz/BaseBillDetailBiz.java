package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.report.ultimate.mapper.BaseBillDetailMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 账单开单详情业务层
 *
 * @author: chow
 * @date: 2020/10/29 11:18
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseBillDetailBiz extends BaseBiz<BaseBillDetailMapper, BaseBillDetail> {

  /**
   * 根据条件查询账单收入详情列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<BillTariffIncomeDetailVO> findBillDetailIncomeList(
      BillDetailIncomeDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillTariffIncomeDetailVO> resultList = mapper.selectBillDetailIncomeList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 导出项目收入明细列表
   *
   * @param response 响应
   * @param query 查询条件
   * @throws IOException
   */
  public void exportBillDetailIncome(
      HttpServletResponse response, BillDetailIncomeDetailQuery query) throws IOException {
    List<BillTariffIncomeDetailVO> list = mapper.selectBillDetailIncomeList(query);
    ExcelUtil<BillTariffIncomeDetailVO> excelUtil = new ExcelUtil<>(BillTariffIncomeDetailVO.class);
    excelUtil.exportExcel(response, list, "项目收入明细表");
  }

  /**
   * 根据条件查询员工工作量报表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<EmployeeWorkloadVO> findEmployeeWorkloadList(EmployeeWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Byte dateType = query.getDateType();
    List<EmployeeWorkloadVO> resultList;
    if (dateType == 0) {
      resultList = mapper.selectEmployeeWorkloadListByMonth(query);
    } else {
      resultList = mapper.selectEmployeeWorkloadListByYear(query);
    }
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            // 奖金系数
            BigDecimal bonusCoefficient = vo.getBonusCoefficient();
            // 补入工作量
            BigDecimal supplementWorkload = vo.getSupplementWorkload();
            // 实收工作量
            BigDecimal actualWorkload = vo.getActualWorkload();
            // 退费工作量
            BigDecimal refundWorkload = vo.getRefundWorkload();
            // 加工费
            BigDecimal processingFee = vo.getProcessingFee();
            // 大额材料费
            BigDecimal largeMaterialCost = vo.getLargeMaterialCost();
            // 基础工作量
            BigDecimal baseWorkload = vo.getBaseWorkload();
            // 已收工作量
            BigDecimal receivedWorkload = vo.getReceivedWorkload();
            BigDecimal actualBonusBase =
                actualWorkload
                    .add(supplementWorkload)
                    .subtract(refundWorkload)
                    .subtract(processingFee)
                    .subtract(largeMaterialCost)
                    .subtract(baseWorkload);
            vo.setActualBonusBase(actualBonusBase);
            vo.setActualBonus(actualBonusBase.multiply(bonusCoefficient));
            BigDecimal receivedBonusBase =
                receivedWorkload
                    .add(supplementWorkload)
                    .subtract(refundWorkload)
                    .subtract(processingFee)
                    .subtract(largeMaterialCost)
                    .subtract(baseWorkload);
            vo.setReceivedBonusBase(receivedBonusBase);
            vo.setReceivedBonus(receivedBonusBase.multiply(bonusCoefficient));
          });
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工工作量列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeeWorkloadList(HttpServletResponse response, EmployeeWorkloadQuery query)
      throws IOException {
    PageInfo<EmployeeWorkloadVO> workloadList = findEmployeeWorkloadList(query);
    List<EmployeeWorkloadVO> resultList = workloadList.getList();
    ExcelUtil<EmployeeWorkloadVO> excelUtil = new ExcelUtil<>(EmployeeWorkloadVO.class);
    excelUtil.exportExcel(response, resultList, "应收账款余额表");
  }

  /**
   * 根据条件查询项目分类收入汇总列表
   *
   * @param query 查询条件
   * @return PageInfo<CategoryInfoIncomeVO>
   */
  public PageInfo<CategoryInfoIncomeVO> findCategoryIncomeList(BillCategoryIncomeQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<CategoryInfoIncomeVO> resultList = mapper.selectCategoryIncomeList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 公司端报表-财务报表-分类收入汇总-导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCategoryIncome(HttpServletResponse response, BillCategoryIncomeQuery query)
      throws IOException {
    List<CategoryInfoIncomeVO> list = mapper.selectCategoryIncomeList(query);
    ExcelUtil<CategoryInfoIncomeVO> excelUtil = new ExcelUtil<>(CategoryInfoIncomeVO.class);
    excelUtil.exportExcel(response, list, "员工工作量统计");
  }

  /**
   * 根据条件查询员工个人实收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalActualWorkloadDetailVO>
   */
  public PageInfo<EmployeePersonalActualWorkloadDetailVO>
      findEmployeePersonalActualWorkloadDetailList(EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Byte dateType = query.getDateType();
    List<EmployeePersonalActualWorkloadDetailVO> resultList;
    if (dateType == 0) {
      resultList = mapper.selectEmployeePersonalActualWorkloadDetailListByMonth(query);
    } else {
      resultList = mapper.selectEmployeePersonalActualWorkloadDetailListByYear(query);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工个人实收工作量明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeePersonalActualWorkloadDetailList(
      HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) throws IOException {
    PageInfo<EmployeePersonalActualWorkloadDetailVO> pageInfo =
        findEmployeePersonalActualWorkloadDetailList(query);
    List<EmployeePersonalActualWorkloadDetailVO> resultList = pageInfo.getList();
    ExcelUtil<EmployeePersonalActualWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(EmployeePersonalActualWorkloadDetailVO.class);
    excelUtil.exportExcel(response, resultList, "员工个人实收工作量明细列表");
  }

  /**
   * 根据条件查询员工工作量开单明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeOrderDetailWorkloadVO>
   */
  public PageInfo<EmployeeOrderDetailWorkloadVO> findOrderDetailWorkloadList(
      EmployeeWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeOrderDetailWorkloadVO> resultList =
        mapper.selectEmployeeOrderDetailWorkloadList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询员工个人已收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalReceivedWorkloadDetailVO>
   */
  public PageInfo<EmployeePersonalReceivedWorkloadDetailVO>
      findEmployeePersonalReceivedWorkloadDetailList(EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Byte dateType = query.getDateType();
    List<EmployeePersonalReceivedWorkloadDetailVO> resultList;
    if (dateType == 0) {
      resultList = mapper.selectEmployeePersonalReceivedWorkloadDetailListByMonth(query);
    } else {
      resultList = mapper.selectEmployeePersonalReceivedWorkloadDetailByYear(query);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工个人已收工作量明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeePersonalReceivedWorkloadDetailList(
      HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) throws IOException {
    PageInfo<EmployeePersonalReceivedWorkloadDetailVO> pageInfo =
        findEmployeePersonalReceivedWorkloadDetailList(query);
    List<EmployeePersonalReceivedWorkloadDetailVO> resultList = pageInfo.getList();
    ExcelUtil<EmployeePersonalReceivedWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(EmployeePersonalReceivedWorkloadDetailVO.class);
    excelUtil.exportExcel(response, resultList, "员工个人已收工作量明细列表");
  }

  /**
   * 根据条件查询员工已收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeReceivedDetailWorkloadVO>
   */
  public PageInfo<EmployeeReceivedDetailWorkloadVO> findReceivedDetailList(
      EmployeeWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeReceivedDetailWorkloadVO> resultList =
        mapper.selectEmployeeReceivedDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询员工补入工作量明细
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalSupplyWorkloadDetailVO>
   */
  public PageInfo<EmployeePersonalSupplyWorkloadDetailVO> findSupplyWorkloadDetailList(
      EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Byte dateType = query.getDateType();
    List<EmployeePersonalSupplyWorkloadDetailVO> resultList;
    if (dateType == 0) {
      resultList = mapper.selectEmployeePersonalSupplyWorkloadDetailListByMonth(query);
    } else {
      resultList = mapper.selectEmployeePersonalSupplyWorkloadDetailByYear(query);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工个人补入工作量明细
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeePersonalSupplyWorkloadDetailList(
      HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) {

  }
}
