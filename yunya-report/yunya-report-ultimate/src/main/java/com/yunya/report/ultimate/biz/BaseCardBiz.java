package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.query.base.MultiClinicEmloyeeDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseCard;
import com.yunya.models.report.BaseCoupon;
import com.yunya.models.report.BaseTariffInfo;
import com.yunya.report.ultimate.mapper.BaseCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.*;

/**
 * 简介: 卡券基础信息业务层
 *
 * @author: chow
 * @date: 2021/1/11 15:59
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseCardBiz extends BaseBiz<BaseCardMapper, BaseCard> {

  /** 365卡系列*/
  private static final Byte PROD_TYPE_365 = 14;
  /** 商品：IVY365年卡*/
  private static final String IVY_365CARD = "365年卡";
  /** 产品 */
  @Autowired private BaseCouponBiz baseCouponBiz;
  /** 门诊 */
  @Autowired private BaseOrganizationBiz baseOrganizationBiz;
  /** 价目or商品表 */
  @Autowired private BaseTariffInfoBiz baseTariffInfoBiz;

  /**
   * 根据条件查询门诊产品售出明细列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementProductSoldDetailVO>
   */
  public PageInfo<StatementProductSoldDetailVO> findProductSoldDetailList(
      StatementProductSoldDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementProductSoldDetailVO> resultList = mapper.selectProductSoldDetailList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (StatementProductSoldDetailVO vo : resultList) {
        StatementPaymentVO statementPaymentVO =
            mapper.selectStatementPaymentByCardId(vo.getCardId());
        if (null != statementPaymentVO) {
          setProductSoldAmountValue(statementPaymentVO, vo);
        }
      }
    }
    return new PageInfo<>(resultList);
  }

    public PageInfo<StatementDeductionSoldDetailVO> deductionSoldDetailList(
            StatementDeductionSoldDetailQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<StatementDeductionSoldDetailVO> resultList = mapper.selectDeductionSoldDetailList(query);
        return new PageInfo<>(resultList);
    }

    public PageInfo<StatementDeductionRefundDetailVO> deductionRefundDetailList(
            StatementDeductionRefundDetailQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<StatementDeductionRefundDetailVO> resultList = mapper.selectDeductionRefundDetailList(query);
        return new PageInfo<>(resultList);
    }

  /**
   * 设置产品售出支付方式金额
   *
   * @param statementPaymentVO 产品售出支付方式
   * @param vo 产品售出明细
   */
  private void setProductSoldAmountValue(
      StatementPaymentVO statementPaymentVO, StatementProductSoldDetailVO vo) {
    String accountItemName = statementPaymentVO.getAccountItemName();
    BigDecimal totalAmount = vo.getSoldAmount();
    switch (accountItemName) {
      case ACCOUNT_ITEM_OF_CASH:
        vo.setCashAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_WECHAT:
        vo.setWeChatAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_ALIPAY:
        vo.setAliPayAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_BANK:
        vo.setBankAmount(totalAmount);
        break;
      default:
        break;
    }
  }

  /**
   * 根据条件导出产品售出记录明细列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportProductSoldDetailList(
      HttpServletResponse response, StatementProductSoldDetailQuery query) throws IOException {
    PageInfo<StatementProductSoldDetailVO> pageInfo = findProductSoldDetailList(query);
    List<StatementProductSoldDetailVO> list = pageInfo.getList();
    ExcelUtil<StatementProductSoldDetailVO> excelUtil =
        new ExcelUtil<>(StatementProductSoldDetailVO.class);
    excelUtil.exportExcel(response, list, "产品售出记录明细列表");
  }

    public void exportDeductionSoldDetailList(
            HttpServletResponse response, StatementDeductionSoldDetailQuery query) throws IOException {
        PageInfo<StatementDeductionSoldDetailVO> pageInfo = deductionSoldDetailList(query);
        List<StatementDeductionSoldDetailVO> list = pageInfo.getList();
        ExcelUtil<StatementDeductionSoldDetailVO> excelUtil =
                new ExcelUtil<>(StatementDeductionSoldDetailVO.class);
        excelUtil.exportExcel(response, list, "划扣卡预付款入账明细列表","划扣卡预付款入账明细列表");
    }

    public void exportDeductionRefundDetailList(
            HttpServletResponse response, StatementDeductionRefundDetailQuery query) throws IOException {
        PageInfo<StatementDeductionRefundDetailVO> pageInfo = deductionRefundDetailList(query);
        List<StatementDeductionRefundDetailVO> list = pageInfo.getList();
        ExcelUtil<StatementDeductionRefundDetailVO> excelUtil =
                new ExcelUtil<>(StatementDeductionRefundDetailVO.class);
        excelUtil.exportExcel(response, list, "划扣卡预付款出账明细列表","划扣卡预付款出账明细列表");
    }

  public List<BaseCard> findCardCouponSoldStatistics(CardCouponUsedQueryForm query) {
    return mapper.selectCardCouponSoldList(query);
  }

  /**
   * 查询产品卡券使用统计
   *
   * @param query
   * @return
   */
  public List<CardCouponUsedDetailVO> findCardCouponUsedDetail(CardCouponUsedDetailQueryForm query) {
    return mapper.selectCardCouponUsedDetail(query);
  }

  public List<BaseCard> findProductSoldList(CardCouponUsedQueryForm query) {
    return mapper.selectProductSoldList(query);
  }

  /**
   * 365卡产品售出激活统计表
   *
   * @param query
   * @return
   */
  public PageInfo<Coupon365SoldActivedStatisticsVO> coupon365SoldActivedStatistics(Coupon365SoldActivedStatisticsQuery query) {
    // 售卖量：开单时商品为 "Xxx365年卡"的售卖量 + 卡券售出产品分类id=14的卡售卖量
    // 激活量：第三方或自有平台激活后的数量
    query.setOralIds(findCard365OralIds(IVY_365CARD));
    query.setCouponIds(findCard365CouponIds(PROD_TYPE_365));
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<Coupon365SoldActivedStatisticsVO> result = mapper.selectCoupon365SoldActivedStatstics(query);
    return new PageInfo<>(result);
  }

  /**
   * 查询商品中的365年卡
   *
   * @param ivy365card
   * @return
   */
  private List<Integer> findCard365OralIds(String ivy365card) {
    List<BaseTariffInfo> orals = baseTariffInfoBiz.findOralItemListLikeName(ivy365card);
    return orals.stream().map(BaseTariffInfo::getItemId).collect(Collectors.toList());
  }

  private List<Integer> findCard365CouponIds(Byte prodType365) {
    BaseCouponQueryForm queryFrom = new BaseCouponQueryForm();
    queryFrom.setProductTypeIds(Collections.singleton(prodType365));
    List<BaseCoupon> coupons = baseCouponBiz.findBaseCouponList(queryFrom);
    return coupons.stream().map(BaseCoupon::getCouponId).collect(Collectors.toList());
  }

  /**
   * 导出365卡产品售出激活统计表
   *
   * @param query
   * @param response
   */
  public void coupon365SoldActivedStatisticsExport(Coupon365SoldActivedStatisticsQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<Coupon365SoldActivedStatisticsVO> list = coupon365SoldActivedStatistics(query).getList();
    ExcelUtil<Coupon365SoldActivedStatisticsVO> excelUtil =
            new ExcelUtil<>(Coupon365SoldActivedStatisticsVO.class);
    String name = "365卡产品售出激活统计表";
    excelUtil.exportExcel(response, list, name, name);
  }

  /**
   * 根据条件查询365卡产品售出明细表
   *
   * @param query
   * @return
   */
  public PageInfo<Coupon365SoldDetailVO> findCoupon365SoldDetail(Coupon365SoldDetailQuery query) {
//    开单时商品为IVY365年卡 + 卡券售出产品分类id=14的卡
    query.setOralIds(findCard365OralIds(IVY_365CARD));
    query.setCouponIds(findCard365CouponIds(PROD_TYPE_365));
    List<Coupon365SoldDetailVO> result = mapper.selectCoupon365SoldDetail(query);
    accumulativeNum(result);
    return PageUtl.doPage(query, result);
  }

  /**
   * 累计数量
   *
   * @param result
   */
  private void accumulativeNum(List<? extends Coupon365DetailVO> result) {
    Map<String, Integer> patientCountMap = new HashMap<>();
    // 统计患者+产品下的所有卡券列表
    result.forEach(vo-> accumulate(vo, patientCountMap, 1));
    // 递减式更新返回数据中的累计数
    result.forEach(vo->{
      Integer count = accumulate(vo, patientCountMap, -1);
      vo.setAccumulativeNum(count);
    });
  }

  private Integer accumulate(Coupon365DetailVO vo, Map<String, Integer> patientCountMap, Integer num) {
    String key = StringHelper.joinWith(",", vo.getSaleType(), vo.getPatientIdentifies(), vo.getCouponId());
    Integer count = patientCountMap.get(key);
    if (count == null) {
      count = 0;
    }
    patientCountMap.put(key, count + num);
    return count;
  }

  /**
   * 根据条件导出365卡产品售出明细表
   *
   * @param query
   * @param response
   * @throws IOException
   */
  public void findCoupon365SoldDetailExport(Coupon365SoldDetailQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<Coupon365SoldDetailVO> list = findCoupon365SoldDetail(query).getList();
    ExcelUtil<Coupon365SoldDetailVO> excelUtil =
            new ExcelUtil<>(Coupon365SoldDetailVO.class);
    String name = "365卡产品售出明细表";
    excelUtil.exportExcel(response, list, name, name);
  }

  /**
   * 根据条件查询365卡产品激活明细表
   *
   * @param query
   * @return
   */
  public PageInfo<Coupon365ActivedDetailVO> findCoupon365ActivedDetail(Coupon365ActivedDetailQuery query) {
    List<Integer> couponId365 = findCard365CouponIds(PROD_TYPE_365);
    Collection<Integer> couponIds = query.getCouponIds();
    if (StringHelper.isEmpty(couponIds)) {
      query.setCouponIds(couponId365);
    } else {
      if (!couponId365.containsAll(couponIds)) {
        throw new ClientServiceException("请选择365产品！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
    }
    List<Coupon365ActivedDetailVO> result = mapper.selectCoupon365ActivedDetail(query);
    accumulativeNum(result);
    return PageUtl.doPage(query, result);
  }

  /**
   * 根据条件导出365卡产品激活明细表
   *
   * @param query
   * @param response
   * @throws IOException
   */
  public void findCoupon365ActivedDetailExport(Coupon365ActivedDetailQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<Coupon365ActivedDetailVO> list = findCoupon365ActivedDetail(query).getList();
    ExcelUtil<Coupon365ActivedDetailVO> excelUtil =
            new ExcelUtil<>(Coupon365ActivedDetailVO.class);
    String name = "365卡产品激活明细表";
    excelUtil.exportExcel(response, list, name, name);
  }

  /**
   * 根据条件查询员工授权折扣表
   *
   * @param query
   * @return
   */
  public PageInfo<EmployeeAccreditDiscountVO> findEmployeeAccreditDiscountList(MultiClinicEmloyeeDateRangeQueryForm query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeAccreditDiscountVO> result = mapper.selectEmployeeAccreditDiscountList(query);
    return new PageInfo<>(result);
  }

  /**
   * 根据条件导出员工授权折扣表
   *
   * @param response
   * @param query
   * @throws IOException
   */
  public void exportEmployeeAccreditDiscountList(HttpServletResponse response, MultiClinicEmloyeeDateRangeQueryForm query) throws IOException {
    query.setWhetherPage(false);
    List<EmployeeAccreditDiscountVO> result = findEmployeeAccreditDiscountList(query).getList();
    ExcelUtil<EmployeeAccreditDiscountVO> excelUtil = new ExcelUtil<>(EmployeeAccreditDiscountVO.class);
    String name = "员工授权折扣表";
    excelUtil.exportExcel(response, result, name, name);
  }
}
