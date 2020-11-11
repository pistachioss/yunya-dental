package com.yunya.modules.treatment.biz;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.form.BillPayDetailForm;
import com.yunya.feign.treatment.domain.model.PaymentModel;
import com.yunya.feign.treatment.domain.vo.BillPayDetailRecordVO;
import com.yunya.feign.treatment.domain.vo.BillPayRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.BillExceptionHandleDetailRecord;
import com.yunya.models.treatment.BillExceptionHandleRecord;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.modules.treatment.mapper.BillExceptionHandleDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillExceptionHandleRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBill;
import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_MEMBER;
import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_PREPARE;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_BILL_PAY_RECORD;

/**
 * 简介: 账单支付明细业务层
 *
 * @author: chow
 * @date: 2020/8/27 20:46
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillPayDetailRecordBiz
    extends BaseBiz<BillPayDetailRecordMapper, BillPayDetailRecord> {

  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;

  /** 收费记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;

  /** 账单异常处理记录 */
  @Autowired private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;

  /** 账单异常处理详情记录 */
  @Autowired private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;

  /**
   * 根据收费记录ID查询入账明细列表
   *
   * @param billPayRecordId 收费记录ID
   * @return
   */
  public BillPayRecordVO findBillPayDetailList(Integer billPayRecordId) {
    BillPayRecordVO resultData = new BillPayRecordVO();
    BillPayRecord billPayRecord = billPayRecordMapper.selectByPrimaryKey(billPayRecordId);
    if (null != billPayRecord) {
      if (billPayRecord.getInservice()) {
        resultData.setBillPayRecordId(billPayRecordId);
        resultData.setChargeDate(new DateTime(billPayRecord.getCrtTime()).toString("yyyy-MM-dd"));
        Integer orgId = billPayRecord.getOrgId();
        resultData.setOrgId(orgId);
        OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
        if (null != orgInfo) {
          resultData.setOrgName(orgInfo.getAbbreviation());
        }
        Integer payeeId = billPayRecord.getCrtId();
        resultData.setPayeeId(payeeId);
        SysEmployee employee = systemServiceFeign.findSysEmployeeById(payeeId);
        resultData.setPayeeName(employee.getName());
        resultData.setReceivedAmount(billPayRecord.getReceivedAmount());
        resultData.setStillOweAmount(billPayRecord.getStillOweAmount());
        List<BillPayDetailRecordVO> detailRecords = getBillPayDetailRecordList(billPayRecordId);
        resultData.setBillPayDetailRecords(detailRecords);
      }
    }
    return resultData;
  }

  /**
   * 根据支付记录ID查询支付详情信息列表
   *
   * @param billPayRecordId 支付记录ID
   * @return
   */
  public List<BillPayDetailRecordVO> findBillPayDetailRecordByBillPayRecordId(
      Integer billPayRecordId) {
    return getBillPayDetailRecordList(billPayRecordId);
  }

  /**
   * 根据账单支付记录ID查询账单支付详情列表
   *
   * @param billPayRecordId 账单支付记录ID
   * @return
   */
  private List<BillPayDetailRecordVO> getBillPayDetailRecordList(Integer billPayRecordId) {
    List<BillPayDetailRecordVO> detailRecords =
        mapper.selectBillPayDetailRecord(billPayRecordId, true);
    detailRecords = getBillPayDetailRecordVOS(detailRecords, systemServiceFeign);
    return detailRecords;
  }

  static List<BillPayDetailRecordVO> getBillPayDetailRecordVOS(
      List<BillPayDetailRecordVO> detailRecords, RemoteSystemServiceFeign systemServiceFeign) {
    if (StringHelper.isNotEmpty(detailRecords)) {
      detailRecords.forEach(
          detailRecord -> {
            Integer accountItemId = detailRecord.getAccountItemId();
            // todo 从缓存中查询支付方式
            AccountItem accountItem = systemServiceFeign.findAccountItemById(accountItemId);
            if (null != accountItem) {
              detailRecord.setAccountItemName(accountItem.getName());
            }
          });
    } else {
      detailRecords = new ArrayList<>();
    }
    return detailRecords;
  }

  /**
   * 调整收费记录入账方式
   *
   * @param billPayRecordId 账单收费记录ID
   * @param form 调整参数
   */
  public void adjustDetail(Integer billPayRecordId, BillPayDetailForm form) {
    String redisKey = LOCK_BILL_PAY_RECORD + billPayRecordId;
    String redisValue = redisUtils.get(redisKey);
    if (StringHelper.isNotBlank(redisValue)) {
      throw new ClientServiceException("调整账单入账方式失败，当前收费记录正在被操作，请稍后再试！", DATA_NOT_EXIST);
    }
    BillPayDetailRecord entity = new BillPayDetailRecord();
    entity.setBillPayRecordId(billPayRecordId);
    entity.setInservice(true);
    List<BillPayDetailRecord> billPayDetailRecords = mapper.select(entity);
    checkWhetherAllowAdjust(billPayDetailRecords);
    Set<PaymentModel> paymentModels = form.getPaymentModels();
    if (paymentModels.size() == billPayDetailRecords.size()) {
      compareParamsAndData(billPayRecordId, paymentModels);
    }
    BigDecimal totalAmount = calculateAndCheckPaymentModel(paymentModels);
    BillPayRecord billPayRecord = billPayRecordMapper.selectByPrimaryKey(billPayRecordId);
    BigDecimal receivedAmount = billPayRecord.getReceivedAmount();
    if (receivedAmount.compareTo(totalAmount) != 0) {
      throw new ClientServiceException(
          "调整账单入账方式失败，本次调整后的入账明细总额与调整前的入账明细总额不相等！", PARAMETERS_IS_ILLEGAL);
    }
    redisUtils.set(redisKey, billPayRecordId, 5);
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Integer patientId = billPayRecord.getPatientId();
    Integer treatmentRecordId = billPayRecord.getTreatmentRecordId();
    Integer orderRecordId = billPayRecord.getOrderRecordId();
    Integer billRecordId = billPayRecord.getBillRecordId();

    BillExceptionHandleRecord exceptionHandleRecord = new BillExceptionHandleRecord();
    exceptionHandleRecord.setOrgId(orgId);
    exceptionHandleRecord.setPatientId(patientId);
    exceptionHandleRecord.setTreatmentRecordId(treatmentRecordId);
    exceptionHandleRecord.setHandledRecordId(billPayRecordId);
    exceptionHandleRecord.setOperateType((byte) 0);
    exceptionHandleRecord.setRemark(form.getRemark());
    exceptionHandleRecord.setCrtId(userId);
    exceptionHandleRecord.setCrtName(name);
    // 上一次修改账单记录ID
    Integer preExceptionHandleRecordId =
        billExceptionHandleRecordMapper.selectPreExceptionHandleRecordId(billPayRecordId, (byte) 0);
    exceptionHandleRecord.setPreExceptionHandleRecordId(preExceptionHandleRecordId);
    billExceptionHandleRecordMapper.insertSelective(exceptionHandleRecord);

    Integer exceptionHandleRecordId = exceptionHandleRecord.getId();
    BillExceptionHandleDetailRecord handleDetail = new BillExceptionHandleDetailRecord();
    handleDetail.setBillHandleRecordId(exceptionHandleRecordId);
    handleDetail.setCrtId(userId);
    handleDetail.setCrtName(name);
    billPayDetailRecords.forEach(
        record -> {
          record.setInservice(false);
          record.setUpdId(userId);
          record.setUpdName(name);
          mapper.updateByPrimaryKeySelective(record);
          handleDetail.setAssociateRecordId(record.getId());
          billExceptionHandleDetailRecordMapper.insertSelective(handleDetail);
        });

    BillPayDetailRecord payDetail = new BillPayDetailRecord();
    paymentModels.forEach(
        model -> {
          payDetail.setOrgId(orgId);
          payDetail.setPatientId(patientId);
          payDetail.setTreatmentRecordId(treatmentRecordId);
          payDetail.setBillPayRecordId(billPayRecordId);
          payDetail.setOrderRecordId(orderRecordId);
          payDetail.setBillRecordId(billRecordId);
          payDetail.setAccountItemId(model.getAccountItemId());
          payDetail.setAmount(model.getAmount());
          payDetail.setType((byte) 2);
          payDetail.setCrtId(userId);
          payDetail.setCrtName(name);
          mapper.insertSelective(payDetail);
        });
    rabbitMqServiceFeign.sendMessage(orderRecordId, 1, BaseBill);
    redisUtils.delete(redisKey);
  }

  /**
   * 校验是否允许调整入账方式
   *
   * @param billPayDetailRecords 收费明细列表
   */
  private void checkWhetherAllowAdjust(List<BillPayDetailRecord> billPayDetailRecords) {
    if (StringHelper.isEmpty(billPayDetailRecords)) {
      throw new ClientServiceException("调整账单入账方式失败，该收费记录入账明细记录不存在！", QUERY_RESULT_INVALID);
    }
    billPayDetailRecords.stream()
        .map(BillPayDetailRecord::getType)
        .filter(type -> 0 == type || 1 == type)
        .forEach(
            type -> {
              throw new ClientServiceException(
                  "调整账单入账方式失败，不能调整使用会员卡或预付款的收费记录！", QUERY_RESULT_INVALID);
            });
  }

  /**
   * 比较当前收费明细与数据库收费明细
   *
   * @param billPayRecordId 账单收费记录ID
   * @param paymentModels 调整入账明细
   */
  private void compareParamsAndData(Integer billPayRecordId, Set<PaymentModel> paymentModels) {
    List<BillPayDetailRecord> detailRecords = new ArrayList<>();
    BillPayDetailRecord entity = new BillPayDetailRecord();
    entity.setBillPayRecordId(billPayRecordId);
    entity.setInservice(true);
    List<BillPayDetailRecord> billPayDetailRecords = mapper.select(entity);
    paymentModels.forEach(
        model ->
            billPayDetailRecords.stream()
                .filter(
                    record ->
                        model.getAccountItemId().equals(record.getAccountItemId())
                            && model.getAmount().equals(record.getAmount()))
                .map(record -> entity)
                .forEach(detailRecords::add));
    if (detailRecords.size() == paymentModels.size()) {
      throw new ClientServiceException("调整账单入账方式失败，当前入账方式或入账金额未作任何修改！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 计算并校验入账明细
   *
   * @param paymentModels 支付方式明细
   */
  private BigDecimal calculateAndCheckPaymentModel(Set<PaymentModel> paymentModels) {
    BigDecimal amount = BigDecimal.valueOf(0);
    if (StringHelper.isEmpty(paymentModels)) {
      throw new ClientServiceException("调整账单入账方式失败，调整入账方式提交参数不能为空！", PARAM_NOT_ALLOW_EMPTY);
    }
    for (PaymentModel model : paymentModels) {
      Integer accountItemId = model.getAccountItemId();
      if (ACCOUNT_ITEM_OF_MEMBER.equals(accountItemId)
          || ACCOUNT_ITEM_OF_PREPARE.equals(accountItemId)) {
        throw new ClientServiceException("调整账单入账方式失败，调整入账方式不能使用会员卡或预付款!", PARAMETERS_IS_ILLEGAL);
      }
      amount = amount.add(model.getAmount());
    }
    return amount;
  }
}
