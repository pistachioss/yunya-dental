package com.yunya.modules.treatment.biz;

import com.google.common.base.Joiner;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_NOT_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 账单记录业务层
 *
 * @author: chow
 * @date: 2020/8/27 20:43
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillRecordBiz extends BaseBiz<BillRecordMapper, BillRecord> {

  /** 系统服务 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 开单记录 */
  @Autowired private OrderRecordBiz orderRecordBiz;
  /** 开单详情 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 账单支付记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单支付明细 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;
  /** 账单明细收费记录 */
  @Autowired private OrderDetailPayRecordMapper orderDetailPayRecordMapper;
  /** 账单异常处理记录 */
  @Autowired private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;
  /** 账单异常处理详情记录 */
  @Autowired private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;
  /** 账单退费记录 */
  @Autowired private BillRefundRecordMapper billRefundRecordMapper;
  /** 账单退费开单明细 */
  @Autowired private BillRefundOrderDetailMapper billRefundOrderDetailMapper;
  /** 账单退费付款明细记录 */
  @Autowired private BillRefundPayDetailRecordMapper billRefundPayDetailRecordMapper;

  /**
   * 生成账单编号
   *
   * @param orgId 组织ID
   * @return String 账单编号
   */
  public String generateBillNumber(Integer orgId) {
    String number = mapper.selectBillNumberByOrgId(orgId, new Date(System.currentTimeMillis()));
    String suffix = String.format("%04d", Integer.parseInt(number) + 1);
    return String.format(
        "ZD%s%s%s", String.format("%04d", orgId), new DateTime().toString("yyMMdd"), suffix);
  }

  /**
   * 根据就诊记录查询账单详情信息
   *
   * @param orderRecordId 就诊记录ID
   * @return resultData 账单详情信息
   */
  public BillDetailGroupVO findOrderDetailAndBillDetail(Integer orderRecordId) {
    BillDetailGroupVO resultData = new BillDetailGroupVO();
    List<OrderDetailVO> orderDetails = orderDetailBiz.findOrderDetailVOList(orderRecordId);
    if (StringHelper.isEmpty(orderDetails)) {
      orderDetails = new ArrayList<>();
    }
    resultData.setOrderDetails(orderDetails);

    List<BillPayRecordVO> billPayRecords = mapper.selectBillPayRecord(orderRecordId);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      billPayRecords.forEach(
          billPayRecord -> {
            Integer orgId = billPayRecord.getOrgId();
            // todo 从缓存中查询诊所信息
            OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
            if (null != orgInfo) {
              billPayRecord.setOrgName(orgInfo.getAbbreviation());
            }
            Integer billPayRecordId = billPayRecord.getBillPayRecordId();
            List<BillPayDetailRecordVO> billPayDetailRecords =
                billPayDetailRecordBiz.findBillPayDetailRecordByBillPayRecordId(billPayRecordId);
            billPayRecord.setBillPayDetailRecords(billPayDetailRecords);
          });
    } else {
      billPayRecords = new ArrayList<>();
    }
    resultData.setBillPayRecords(billPayRecords);
    // 账单异常记录
    List<BillHandleRecordVO> billHandleRecords = new ArrayList<>();
    resultData.setBillHandleRecords(billHandleRecords);
    return resultData;
  }

  /**
   * 账单退费
   *
   * @param model 退费参数
   */
  public void refund(BillRefundModel model) {
    Integer treatmentRecordId = model.getTreatmentRecordId();
    BillRecord entity = new BillRecord();
    entity.setTreatmentRecordId(treatmentRecordId);
    entity.setInservice(true);
    BillRecord billRecord = mapper.selectOne(entity);
    if (null == billRecord) {
      throw new ClientServiceException("账单退费失败，当前就诊账单已修改或未收费！", DATA_NOT_EXIST);
    }

    List<RefundOrderDetailModel> refundOrderDetailModels = model.getRefundOrderDetailModels();
    MemberRefundModel memberRefundModel = model.getMemberRefundModel();
    PrepaymentRefundModel prepaymentRefundModel = model.getPrepaymentRefundModel();
    List<PaymentModel> refundPaymentModels = model.getRefundPaymentModels();
    String refundReason = model.getRefundReason();
    List<String> refundAnnex = model.getRefundAnnex();

    Integer billRecordId = billRecord.getId();
    Integer patientId = billRecord.getPatientId();
    Integer orderRecordId = billRecord.getOrderRecordId();

    BigDecimal refundOrderDetailAmount = calculateRefundOrderDetailAmount(refundOrderDetailModels);
    BigDecimal refundTotalAmount =
        calculateRefundAmount(memberRefundModel, prepaymentRefundModel, refundPaymentModels);
    if (refundOrderDetailAmount.compareTo(refundTotalAmount) != 0) {
      throw new ClientServiceException("账单退费失败，退费总额与退费项目金额总和不相等！", PARAMETERS_IS_ILLEGAL);
    }

    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    String name = BaseContextHandler.getName();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());

    BillRefundRecord billRefundRecord = new BillRefundRecord();
    billRefundRecord.setOrgId(orgId);
    billRefundRecord.setTreatmentRecordId(treatmentRecordId);
    billRefundRecord.setOrderRecordId(orderRecordId);
    billRefundRecord.setReason(refundReason);
    billRefundRecord.setTotalRefundAmount(refundTotalAmount);
    Joiner joiner = Joiner.on(",");
    billRefundRecord.setRefundCertificate(joiner.join(refundAnnex));
    billRefundRecord.setCrtId(userId);
    billRefundRecord.setCrtName(name);
    billRefundRecordMapper.insertSelective(billRefundRecord);

    Integer billRefundRecordId = billRefundRecord.getId();
    BillRefundOrderDetail refundOrderDetail = new BillRefundOrderDetail();
    refundOrderDetail.setCrtId(userId);
    refundOrderDetail.setCrtName(name);
    refundOrderDetailModels.forEach(
        detailModel -> {
          refundOrderDetail.setOrderDetailId(detailModel.getOrderDetailId());
          refundOrderDetail.setRefundAmout(detailModel.getRefundAmount());
          refundOrderDetail.setBillRefundRecordId(billRefundRecordId);
          billRefundOrderDetailMapper.insertSelective(refundOrderDetail);
        });

    BillRefundPayDetailRecord refundPayDetailRecord = new BillRefundPayDetailRecord();
    refundPayDetailRecord.setBillRefundRecordId(billRefundRecordId);
    refundPayDetailRecord.setCrtId(userId);
    refundPayDetailRecord.setCrtName(name);
    if (null != memberRefundModel) {
      refundPayDetailRecord.setAccountItemId(memberRefundModel.getAccountItemId());
      refundPayDetailRecord.setRemark(memberRefundModel.getMemberAccountId().toString());
      BigDecimal principalAmount = memberRefundModel.getPrincipalAmount();
      BigDecimal giftAmount = memberRefundModel.getGiftAmount();
      refundPayDetailRecord.setRefundPayAmount(principalAmount.add(giftAmount));
      billRefundPayDetailRecordMapper.insertSelective(refundPayDetailRecord);
    }

    if (null != prepaymentRefundModel) {
      refundPayDetailRecord.setAccountItemId(prepaymentRefundModel.getAccountItemId());
      refundPayDetailRecord.setRemark(prepaymentRefundModel.getPrepaymentAccountId().toString());
      BigDecimal principalAmount = prepaymentRefundModel.getPrincipalAmount();
      BigDecimal giftAmount = prepaymentRefundModel.getGiftAmount();
      refundPayDetailRecord.setRefundPayAmount(principalAmount.add(giftAmount));
      billRefundPayDetailRecordMapper.insertSelective(refundPayDetailRecord);
    }

    if (StringHelper.isNotEmpty(refundPaymentModels)) {
      refundPaymentModels.forEach(
          paymentModel -> {
            refundPayDetailRecord.setAccountItemId(paymentModel.getAccountItemId());
            refundPayDetailRecord.setRemark(paymentModel.getAccountItemId().toString());
            BigDecimal amount = paymentModel.getAmount();
            refundPayDetailRecord.setRefundPayAmount(amount);
            billRefundPayDetailRecordMapper.insertSelective(refundPayDetailRecord);
          });
    }

    BillExceptionHandleRecord exceptionHandleRecord = new BillExceptionHandleRecord();
    exceptionHandleRecord.setOrgId(orgId);
    exceptionHandleRecord.setPatientId(patientId);
    exceptionHandleRecord.setTreatmentRecordId(treatmentRecordId);
    exceptionHandleRecord.setHandleRecordId(billRecordId);
    exceptionHandleRecord.setOperateType((byte) 3);
    exceptionHandleRecord.setCrtId(userId);
    exceptionHandleRecord.setCrtName(name);
    billExceptionHandleRecordMapper.insertSelective(exceptionHandleRecord);

    Integer exceptionHandleRecordId = exceptionHandleRecord.getId();
    BillExceptionHandleDetailRecord handleDetailRecord = new BillExceptionHandleDetailRecord();
    handleDetailRecord.setBillHandleRecordId(exceptionHandleRecordId);
    handleDetailRecord.setAssociatRecordId(billRefundRecordId);
    handleDetailRecord.setCrtId(userId);
    handleDetailRecord.setCrtName(name);
    billExceptionHandleDetailRecordMapper.insertSelective(handleDetailRecord);
  }

  /**
   * 计算退费订单明细总额
   *
   * @param refundOrderDetailModels 退费订单明细列表
   * @return refundOrderDetailAmount 退费订单明细总额
   */
  private BigDecimal calculateRefundOrderDetailAmount(
      List<RefundOrderDetailModel> refundOrderDetailModels) {
    BigDecimal refundOrderDetailAmount = BigDecimal.valueOf(0);
    if (StringHelper.isNotEmpty(refundOrderDetailModels)) {
      OrderDetailPayRecord orderDetailPayrecord = new OrderDetailPayRecord();
      for (RefundOrderDetailModel model : refundOrderDetailModels) {
        Integer orderDetailId = model.getOrderDetailId();
        OrderDetail orderDetail = orderDetailBiz.selectById(orderDetailId);
        if (null == orderDetail) {
          throw new ClientServiceException("账单退费失败，请选择正确的订单明细进行操作！", PARAMETERS_IS_ILLEGAL);
        }
        // 比较退费金额与订单入账金额
        orderDetailPayrecord.setOrderDetailId(orderDetailId);
        orderDetailPayrecord.setInservice(true);
        OrderDetailPayRecord detailPayRecord =
            orderDetailPayRecordMapper.selectOne(orderDetailPayrecord);
        BigDecimal refundAmount = model.getRefundAmount();
        if (null != detailPayRecord) {
          BigDecimal receivedAmount = detailPayRecord.getReceivedAmount();
          if (refundAmount.compareTo(receivedAmount) > 0) {
            throw new ClientServiceException("账单退费失败，项目退费金额不能超过该项目实收金额！", PARAMETERS_IS_ILLEGAL);
          }
        }
        refundOrderDetailAmount = refundOrderDetailAmount.add(refundAmount);
      }
    }
    return refundOrderDetailAmount;
  }

  /**
   * 计算退费总额
   *
   * @param memberAccountModel 会员退费
   * @param prepaymentAccountModel 预付款退费
   * @param refundPaymentModels 其他方式退费
   * @return totalAmount 退费总额
   */
  private BigDecimal calculateRefundAmount(
      MemberRefundModel memberAccountModel,
      PrepaymentRefundModel prepaymentAccountModel,
      List<PaymentModel> refundPaymentModels) {
    BigDecimal totalAmount = BigDecimal.valueOf(0);
    if (null != memberAccountModel) {
      BigDecimal principalAmount = memberAccountModel.getPrincipalAmount();
      BigDecimal giftAmount = memberAccountModel.getGiftAmount();
      totalAmount = totalAmount.add(principalAmount).add(giftAmount);
    }
    if (null != prepaymentAccountModel) {
      BigDecimal principalAmount = prepaymentAccountModel.getPrincipalAmount();
      BigDecimal giftAmount = prepaymentAccountModel.getGiftAmount();
      totalAmount = totalAmount.add(principalAmount).add(giftAmount);
    }
    if (StringHelper.isNotEmpty(refundPaymentModels)) {
      for (PaymentModel model : refundPaymentModels) {
        totalAmount = totalAmount.add(model.getAmount());
      }
    }
    return totalAmount;
  }

  /**
   * 调整已收费账单信息
   *
   * @param model 参数信息
   */
  public void adjust(BillAdjustDetailModel model) {
    Integer billRecordId = model.getBillRecordId();
    BillRecord entity = new BillRecord();
    entity.setId(billRecordId);
    entity.setInservice(true);
    BillRecord billRecord = mapper.selectOne(entity);
    if (null == billRecord) {
      throw new ClientServiceException("调整账单失败，请选择正确的就诊记录进行账单调整！", PARAMETERS_IS_ILLEGAL);
    }

    BillPayRecord billPayRecord = new BillPayRecord();
    billPayRecord.setBillRecordId(billRecordId);
    billPayRecord.setInservice(true);
    int billPayRecordCount = billPayRecordMapper.selectCount(billPayRecord);
    if (billPayRecordCount > 0) {
      throw new ClientServiceException("调整账单失败，当前账单存在位撤销的支付记录！", PARAMETERS_IS_ILLEGAL);
    }

    Integer orderRecordId = billRecord.getOrderRecordId();
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetail.setInservice(true);
    List<OrderDetail> orderDetailsData = orderDetailBiz.selectList(orderDetail);
    List<OrderDetailModel> detailModels = model.getOrderDetailModels();
    if (orderDetailsData.size() == detailModels.size()) {
      compareOrderDetails(orderDetailsData, detailModels);
    }

    Integer patientId = billRecord.getPatientId();
    Integer treatmentRecordId = billRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();

    BillExceptionHandleRecord exceptionHandleRecord = new BillExceptionHandleRecord();
    exceptionHandleRecord.setOrgId(orgId);
    exceptionHandleRecord.setPatientId(patientId);
    exceptionHandleRecord.setTreatmentRecordId(treatmentRecordId);
    exceptionHandleRecord.setHandleRecordId(billRecordId);
    exceptionHandleRecord.setOperateType((byte) 2);
    exceptionHandleRecord.setRemark(model.getRemark());
    exceptionHandleRecord.setCrtId(userId);
    exceptionHandleRecord.setCrtName(name);
    billExceptionHandleRecordMapper.insertSelective(exceptionHandleRecord);

    // todo 将优惠置为不可用

    Integer handleRecordId = exceptionHandleRecord.getId();
    BillExceptionHandleDetailRecord handleDetailRecord = new BillExceptionHandleDetailRecord();
    handleDetailRecord.setBillHandleRecordId(handleRecordId);
    handleDetailRecord.setCrtId(userId);
    handleDetailRecord.setCrtName(name);
    orderDetailsData.forEach(
        detail -> {
          detail.setInservice(false);
          detail.setUpdId(userId);
          detail.setUptName(name);
          handleDetailRecord.setAssociatRecordId(detail.getId());
          billExceptionHandleDetailRecordMapper.insertSelective(handleDetailRecord);
        });

    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setId(orderRecordId);
    orderRecord.setInservice(false);
    orderRecord.setUpdId(userId);
    orderRecord.setUpdName(name);
    orderRecordBiz.updateSelectiveById(orderRecord);

    billRecord.setInservice(false);
    billRecord.setUpdId(userId);
    billRecord.setUpdName(name);
    mapper.updateByPrimaryKeySelective(billRecord);

    List<OrderDetail> orderDetails =
        orderDetailBiz.transferModelToEntity(orgId, treatmentRecordId, detailModels);
    BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);
    orderRecord.setId(null);
    orderRecord.setPatientId(patientId);
    orderRecord.setTreatmentRecordId(treatmentRecordId);
    String orderRecordNumber = orderRecordBiz.generateOrderRecordNumber(orgId);
    orderRecord.setOrderRecordNum(orderRecordNumber);
    orderRecord.setTotalAmount(totalAmount);
    orderRecord.setInservice(true);
    orderRecord.setCrtId(userId);
    orderRecord.setCrtName(name);
    orderRecordBiz.insertSelective(orderRecord);

    orderRecordId = orderRecord.getId();
    for (OrderDetail detail : orderDetails) {
      detail.setOrderRecordId(orderRecordId);
      orderDetailBiz.insertSelective(detail);
    }
  }

  /**
   * 比较调整开单明细与数据库开单明细
   *
   * @param orderDetailsData 数据库开单明细
   * @param detailModels 调整开单明细
   */
  private void compareOrderDetails(
      List<OrderDetail> orderDetailsData, List<OrderDetailModel> detailModels) {
    List<OrderDetail> details = new ArrayList<>();
    orderDetailsData.forEach(
        detail ->
            detailModels.stream()
                .filter(
                    model ->
                        detail.getBillingItemId().equals(model.getBillingItemId())
                            && detail.getQuantity().equals(model.getQuantity()))
                .map(model -> detail)
                .forEachOrdered(details::add));
    if (detailModels.size() == details.size()) {
      throw new ClientServiceException("调整账单失败，当前账单开单明细的项目与数量未发生任何变动！", PARAMETERS_IS_ILLEGAL);
    }
  }
}
