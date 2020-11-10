package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.vo.BillPayRecordVO;
import com.yunya.feign.treatment.domain.vo.BillPaymentAdjustDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.treatment.BillExceptionHandleDetailRecord;
import com.yunya.models.treatment.BillExceptionHandleRecord;
import com.yunya.modules.treatment.mapper.BillExceptionHandleRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 账单异常处理业务层
 *
 * @author: chow
 * @date: 2020/9/24 20:34
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillExceptionHandleRecordBiz
    extends BaseBiz<BillExceptionHandleRecordMapper, BillExceptionHandleRecord> {

  /** 账单异常处理详情 */
  @Autowired private BillExceptionHandleDetailRecordBiz billExceptionHandleDetailRecordBiz;
  /** 账单支付记录详情 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;
  /** 账单退费 */
  @Autowired private BillRefundRecordBiz billRefundRecordBiz;

  /**
   * 根据账单异常处理记录查询处理详情
   *
   * @param billHandleRecordId 账单异常处理记录ID
   * @return map
   */
  public Map<String, Object> findBillHandleDetail(Integer billHandleRecordId) {
    BillExceptionHandleRecord handleRecord = mapper.selectByPrimaryKey(billHandleRecordId);
    if (null == handleRecord) {
      throw new ClientServiceException("请选择正确的账单异常处理记录！", PARAMETERS_IS_ILLEGAL);
    }
    // 被处理数据ID
    Integer handledRecordId = handleRecord.getHandledRecordId();
    // 上一条异常处理记录ID
    Integer preExceptionHandleRecordId = handleRecord.getPreExceptionHandleRecordId();
    // 异常处理类型
    Byte operateType = handleRecord.getOperateType();
    Map<String, Object> resultMap = new HashMap<>(16);
    switch (operateType) {
        // 调整入账方式
      case 0:
        BillPaymentAdjustDetailVO billPaymentAdjustDetail =
            billExceptionHandleDetailRecordBiz.findBillPaymentAdjustDetail(
                handledRecordId, billHandleRecordId, preExceptionHandleRecordId);
        resultMap.put("billPaymentAdjustDetail", billPaymentAdjustDetail);
        break;
        // 账单撤销
      case 1:
        BillPayRecordVO billPayDetails = new BillPayRecordVO();
        BillExceptionHandleDetailRecord entity = new BillExceptionHandleDetailRecord();
        entity.setBillHandleRecordId(billHandleRecordId);
        BillExceptionHandleDetailRecord billExceptionHandleDetailRecord =
            billExceptionHandleDetailRecordBiz.selectOne(entity);
        if (null != billExceptionHandleDetailRecord) {
          Integer associateRecordId = billExceptionHandleDetailRecord.getAssociateRecordId();
          billPayDetails = billPayDetailRecordBiz.findBillPayDetailList(associateRecordId);
        }
        resultMap.put("revokeBillPayRecord", billPayDetails);
        break;
        // 账单调整
      case 2:
        resultMap =
            billExceptionHandleDetailRecordBiz.findBillOrderDetailAdjustDetails(
                handledRecordId, billHandleRecordId, preExceptionHandleRecordId);
        break;
        // 账单退费
      case 3:
        resultMap = billRefundRecordBiz.findBillRefundRecordInfo(billHandleRecordId);
        break;
      default:
        break;
    }
    return resultMap;
  }
}
