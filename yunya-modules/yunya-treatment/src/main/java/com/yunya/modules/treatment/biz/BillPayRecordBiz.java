package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.BillExceptionHandleDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillExceptionHandleRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_BILL_PAY_RECORD;

/**
 * 简介: 账单支付记录业务层
 *
 * @author: chow
 * @date: 2020/8/27 20:44
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillPayRecordBiz extends BaseBiz<BillPayRecordMapper, BillPayRecord> {

  /** 缓存调用 */
  @Autowired private RedisUtils redisUtils;

  /** 账单记录 */
  @Autowired private BillRecordBiz billRecordBiz;

  /** 账单支付明细 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;

  /** 账单异常处理记录 */
  @Autowired private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;

  /** 账单异常处理详情 */
  @Autowired private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;

  /**
   * 根据账单收费记录ID撤销账单收费记录
   *
   * @param billPayRecordId 账单收费记录ID
   */
  public void revoke(Integer billPayRecordId) {
    BillPayRecord billPayRecord = checkRevokeBillPayRecordWhetherAllow(billPayRecordId);

    redisUtils.set(LOCK_BILL_PAY_RECORD + billPayRecordId, billPayRecordId, 5);
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Integer patientId = billPayRecord.getPatientId();
    Integer treatmentRecordId = billPayRecord.getTreatmentRecordId();
    Integer billRecordId = billPayRecord.getBillRecordId();
    BigDecimal receivedAmount = billPayRecord.getReceivedAmount();

    BillExceptionHandleRecord handleRecord = new BillExceptionHandleRecord();
    handleRecord.setOrgId(orgId);
    handleRecord.setPatientId(patientId);
    handleRecord.setTreatmentRecordId(treatmentRecordId);
    handleRecord.setHandledRecordId(billPayRecordId);
    handleRecord.setOperateType((byte) 1);
    handleRecord.setCrtId(userId);
    handleRecord.setCrtName(name);
    billExceptionHandleRecordMapper.insertSelective(handleRecord);

    Integer handleRecordId = handleRecord.getId();
    BillExceptionHandleDetailRecord handleDetailRecord = new BillExceptionHandleDetailRecord();
    handleDetailRecord.setBillHandleRecordId(handleRecordId);
    handleDetailRecord.setAssociateRecordId(billPayRecordId);
    handleDetailRecord.setCrtId(userId);
    handleDetailRecord.setCrtName(name);
    billExceptionHandleDetailRecordMapper.insertSelective(handleDetailRecord);

    BillRecord billRecord = billRecordBiz.selectById(billRecordId);
    BigDecimal currentReceivedAmount = billRecord.getReceivedAmount();
    billRecord.setReceivableAmount(currentReceivedAmount.subtract(receivedAmount));
    BigDecimal debtAmount = billRecord.getDebtAmount();
    billRecord.setDebtAmount(debtAmount.add(receivedAmount));
    billRecord.setUpdId(userId);
    billRecord.setUpdName(name);
    billRecordBiz.updateSelectiveById(billRecord);

    billPayRecord.setInservice(false);
    billPayRecord.setUpdId(userId);
    billPayRecord.setUpdName(name);
    mapper.updateByPrimaryKeySelective(billPayRecord);

    BillPayDetailRecord billPayDetail = new BillPayDetailRecord();
    billPayDetail.setBillPayRecordId(billPayRecordId);
    List<BillPayDetailRecord> payDetailRecords = billPayDetailRecordBiz.selectList(billPayDetail);
    if (StringHelper.isNotEmpty(payDetailRecords)) {
      // todo 会员卡、预付款需退还到原先账号
      payDetailRecords.forEach(
          detailRecord -> {
            detailRecord.setInservice(false);
            detailRecord.setUpdId(userId);
            detailRecord.setUpdName(name);
            billPayDetailRecordBiz.updateSelectiveById(detailRecord);
          });
    }

    redisUtils.delete(LOCK_BILL_PAY_RECORD + billPayRecordId);
  }

  /**
   * 校验是否允许撤销收费记录
   *
   * @param billPayRecordId 收费记录ID
   */
  private BillPayRecord checkRevokeBillPayRecordWhetherAllow(Integer billPayRecordId) {
    String str = redisUtils.get(LOCK_BILL_PAY_RECORD + billPayRecordId);
    if (StringHelper.isNotBlank(str)) {
      throw new ClientServiceException("收费记录撤销失败，当前收费记录正在被操作，请稍后再试！", QUERY_RESULT_INVALID);
    }

    BillPayRecord billPayRecord = mapper.selectByPrimaryKey(billPayRecordId);
    if (null == billPayRecord) {
      throw new ClientServiceException("撤销收费失败，收费记录不存在！", QUERY_RESULT_INVALID);
    }

    Boolean inservice = billPayRecord.getInservice();
    if (!inservice) {
      throw new ClientServiceException("撤销收费失败，该条记录已被撤销！", QUERY_RESULT_INVALID);
    }

    Integer orderRecordId = billPayRecord.getOrderRecordId();
    BillPayRecord entity = new BillPayRecord();
    entity.setOrderRecordId(orderRecordId);
    entity.setInservice(true);
    List<BillPayRecord> billPayRecords = mapper.select(entity);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      Date billPayRecordCrtTime = billPayRecord.getCrtTime();
      billPayRecords.forEach(
          payRecord -> {
            Integer payRecordId = payRecord.getId();
            if (!billPayRecordId.equals(payRecordId)) {
              Date payRecordCrtTime = payRecord.getCrtTime();
              if (payRecordCrtTime.after(billPayRecordCrtTime)) {
                throw new ClientServiceException("撤销收费失败，请从时间最新的一条收费开始撤销！", PARAMETERS_IS_ILLEGAL);
              }
            }
          });
    }
    return billPayRecord;
  }
}
