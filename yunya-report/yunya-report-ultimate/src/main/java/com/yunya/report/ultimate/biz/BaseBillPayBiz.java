package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillPayRecordQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementBillChargeDetailInfoQuery;
import com.yunya.feign.report.domain.vo.BillOfPayRecordVO;
import com.yunya.feign.report.domain.vo.StatementBillChargeDetailVO;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.feign.report.domain.vo.TollDataStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillPay;
import com.yunya.report.ultimate.mapper.BaseBillPayDetailMapper;
import com.yunya.report.ultimate.mapper.BaseBillPayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介: 账单收费记录业务层
 *
 * @author: chow
 * @date: 2020/10/27 16:49
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseBillPayBiz extends BaseBiz<BaseBillPayMapper, BaseBillPay> {

  /** 收费记录明细 */
  @Autowired private BaseBillPayDetailMapper billPayDetailMapper;

  /**
   * 根据条件查询账单支付记录列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<BillOfPayRecordVO> findBillRecordOfPayList(BillPayRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillOfPayRecordVO> resultList = mapper.selectBillRecordOfPayList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单收费记录列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportBillOfPayRecord(HttpServletResponse response, BillPayRecordQuery query)
      throws IOException {
    List<BillOfPayRecordVO> list = mapper.selectBillRecordOfPayList(query);
    ExcelUtil<BillOfPayRecordVO> excelUtil = new ExcelUtil<>(BillOfPayRecordVO.class);
    excelUtil.exportExcel(response, list, "账单收费记录表");
  }

  /**
   * 根据条件查询门诊收费数据总览
   *
   * @param query 查询条件
   * @return TollDataStatisticsVO
   */
  public TollDataStatisticsVO findClinicTollDataStatistic(DataStatisticsQuery query) {
    TollDataStatisticsVO resultData = mapper.selectClinicTollDataStatistic(query);
    return resultData;
  }

  /**
   * 根据条件查询账单收费（本月账单本月首次收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> selectBillChargeDetailInfoList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList = mapper.selectBillChargeDetailInfoList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询账单收欠费（本月账单本月收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findBillCurrentCollectDebtDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
        mapper.selectBillCurrentChargeDebtDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询账单收欠费（非本月账单本月收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findOtherCollectDebtDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
            mapper.selectBillOtherChargeDebtDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 构建账单收费记录的支付方式明细信息
   *
   * @param resultList 账单收费记录列表
   */
  private void generateBillChargeAccountItemDetail(List<StatementBillChargeDetailVO> resultList) {
    if (StringHelper.isNotEmpty(resultList)) {
      // todo 补充会员卡本金/赠金；预付款本金/赠金收费方式入账金额
      for (StatementBillChargeDetailVO vo : resultList) {
        List<StatementPaymentVO> statementPayments =
            billPayDetailMapper.selectBillPayDetailList(vo.getBillPayId());
        if (null == statementPayments) {
          statementPayments = new ArrayList<>();
        }
        vo.setStatementPayments(statementPayments);
      }
    }
  }
}
