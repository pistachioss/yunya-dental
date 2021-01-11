package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.StatementProductSoldDetailQuery;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.feign.report.domain.vo.StatementProductSoldDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseCard;
import com.yunya.report.ultimate.mapper.BaseCardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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
}
