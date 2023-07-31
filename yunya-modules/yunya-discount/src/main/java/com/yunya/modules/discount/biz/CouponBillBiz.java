package com.yunya.modules.discount.biz;

import cn.hutool.core.date.DateTime;
import com.yunya.feign.discount.domain.model.*;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.BillRebate2MemberAccountModel;
import com.yunya.feign.patient_central.domain.model.MemberExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.discount.CouponBill;
import com.yunya.models.discount.CouponBillPay;
import com.yunya.models.discount.CouponBillPayDetail;
import com.yunya.models.discount.CouponOrder;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.modules.discount.mapper.CouponBillMapper;
import com.yunya.modules.discount.mapper.CouponBillPayDetailMapper;
import com.yunya.modules.discount.mapper.CouponBillPayMapper;
import com.yunya.modules.discount.mapper.CouponOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.BusinessConstants.MEDICAL_APPLY_LOCK_SEC;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static com.yunya.modules.discount.enums.CouponOrderError.*;

/**
 * @auther: xy
 * @date: 2023/6/26
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CouponBillBiz {

    @Resource
    private CouponOrderMapper couponOrderMapper;
    @Resource
    private CouponBillMapper billMapper;
    @Resource
    private CouponBillPayMapper billPayMapper;
    @Resource
    private CouponBillPayDetailMapper billPayDetailMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private CouponOrderBiz orderBiz;
    @Resource
    private RemotePatientCentralServiceFeign patientCentralServiceFeign;


    public void charge(CouponBillModel model) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer orderId = model.getOrderId();
        boolean locked = false;
        String lockVal = String.valueOf(loginUserId);
        String lockKey = String.valueOf(orderId);
        Date date = new Date();
        try {
            // 1. 锁定产品
            locked = redisUtils.setLock(String.valueOf(orderId), lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            Set<CardPaymentModel> paymentModels = model.getPaymentModels();
            Set<CardPrepaymentModel> prepaymentAccountModels = model.getPrepaymentAccountModels();
            Set<CardMemberModel> memberAccountModels = model.getMemberAccountModels();
            CouponBill bill = getBill(orderId);
            if (Objects.nonNull(bill)) {
                throw ClientServiceException.wrap(ORDER_BILL);
            }
            CardInvoiceModel invoiceModel = model.getInvoiceModel();
            if (invoiceModel.getInvoice()) {
                if (StringHelper.isBlank(invoiceModel.getInvoiceNumber())) {
                    throw new ClientServiceException("收费失败，未填写发票编号！", PARAMETERS_IS_ILLEGAL);
                }
            }
            BigDecimal totalCharge = calculateTotalCharge(prepaymentAccountModels
                    , memberAccountModels, paymentModels);
            CouponOrder order = couponOrderMapper.selectByPrimaryKey(orderId);
            if (Objects.isNull(order) || order.getStatus() != 0) {
                throw ClientServiceException.wrap(COUPON_ORDER_ERROR);
            }
            BigDecimal receivableAmount = order.getReceivableAmount();
            if (totalCharge.compareTo(receivableAmount) < 0) {
                throw ClientServiceException.wrap(RECEIVED_LACK);
            }
            CouponBill couponBill = generateBillRecord(order, date, totalCharge,invoiceModel);
            CouponBillPay billPay = new CouponBillPay();
            // 如果当前组织是公司，收费门诊则是开单门诊
            billPay.setOrgId(order.getOrgId());
            billPay.setPatientId(order.getPatientId());
            billPay.setOrderId(order.getId());
            billPay.setBillId(couponBill.getId());
            if (totalCharge.compareTo(order.getReceivableAmount()) >= 0) {
                billPay.setReceivedAmount(totalCharge);
                billPay.setOweAmount(BigDecimal.valueOf(0));
            } else {
                billPay.setReceivedAmount(totalCharge);
                billPay.setOweAmount(order.getReceivableAmount().subtract(totalCharge));
            }
            billPay.setCrtId(loginUserId);
            // 首次收费时间与账单时间保持一致
            billPay.setCrtTime(date);
            billPay.setUpdId(loginUserId);
            // 首次收费时间与账单时间保持一致
            billPay.setUpdTime(date);
            billPayMapper.insertSelective(billPay);
            // 保存收费明细
            saveBillPayDetailRecord(billPay, prepaymentAccountModels, memberAccountModels, paymentModels, date);
//            orderBiz.occur(billPay, 1, date);
            orderBiz.updateOrder(orderId, totalCharge);
            // 扣除预付款、会员卡余额
            if (CollectionUtils.isNotEmpty(prepaymentAccountModels)) {
                usePrepaymentAccount(
                        prepaymentAccountModels, billPay);
            }
            if (CollectionUtils.isNotEmpty(memberAccountModels)) {
                ResponseResult expend =
                        useMemberAccount(memberAccountModels, billPay);
                if (expend.getStatus() > 0) {
                    throw new ClientServiceException(expend.getMsg(), expend.getStatus());
                }
            }
            //返点
            returnGift(billPay);
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    private ResponseResult useMemberAccount(Set<CardMemberModel> memberAccountModels, CouponBillPay billPay) {
        MemberExpendRecordModel memberExpendRecordModel = new MemberExpendRecordModel();
        Iterator<CardMemberModel> iterator = memberAccountModels.iterator();
        ResponseResult responseResult = null;
        while (iterator.hasNext()) {
            CardMemberModel memberAccountModel = iterator.next();
            memberExpendRecordModel.setPatientId(billPay.getPatientId());
            memberExpendRecordModel.setMemberId(memberAccountModel.getMemberNum());
            memberExpendRecordModel.setExpendTotal(memberAccountModel.getAmount());
            memberExpendRecordModel.setPrincipalAmount(memberAccountModel.getPrincipalAmount());
            memberExpendRecordModel.setBonusAmount(memberAccountModel.getBonusAmount());
            memberExpendRecordModel.setTreatmentRecordId(billPay.getBillId());
            memberExpendRecordModel.setOrderRecordId(billPay.getOrderId());
            memberExpendRecordModel.setBillRecordId(billPay.getBillId());
            memberExpendRecordModel.setBillPayRecordId(billPay.getId());
            ResponseResult expend = patientCentralServiceFeign.expend(memberExpendRecordModel);
            // 服务调用成功返回0，否则返回大于0的状态码
            if (expend.getStatus() > 0) {
                responseResult = expend;
                break;
            }
        }
        return responseResult != null ? responseResult : ResponseUtil.success();
    }

    private void usePrepaymentAccount(Set<CardPrepaymentModel> prepaymentAccountModels, CouponBillPay billPay) {
        PrepaidExpendRecordModel prepaidExpendRecordModel = new PrepaidExpendRecordModel();
        prepaymentAccountModels.forEach(
                prepaymentAccountModel -> {
                    prepaidExpendRecordModel.setPatientId(billPay.getPatientId());
                    prepaidExpendRecordModel.setPrepaidId(prepaymentAccountModel.getPrepaymentNum());
                    prepaidExpendRecordModel.setExpendTotal(prepaymentAccountModel.getAmount());
                    prepaidExpendRecordModel.setPrincipalAmount(prepaymentAccountModel.getPrincipalAmount());
                    prepaidExpendRecordModel.setBonusAmount(prepaymentAccountModel.getBonusAmount());
                    prepaidExpendRecordModel.setTreatmentRecordId(billPay.getBillId());
                    prepaidExpendRecordModel.setOrderRecordId(billPay.getOrderId());
                    prepaidExpendRecordModel.setBillRecordId(billPay.getBillId());
                    prepaidExpendRecordModel.setBillPayRecordId(billPay.getId());
                    ResponseResult result = patientCentralServiceFeign.expend(prepaidExpendRecordModel);
                    if (!result.getStatus().equals(0)) {
                        throw new ClientServiceException(result.getMsg(), result.hashCode());
                    }
                });
    }

    private void saveBillPayDetailRecord(
            CouponBillPay billPay,
            Set<CardPrepaymentModel> prepaymentAccountModels,
            Set<CardMemberModel> memberAccountModels,
            Set<CardPaymentModel> paymentModels, Date date) {
        if (!CollectionUtils.isEmpty(prepaymentAccountModels)) {
            // 预付款
            prepaymentAccountModels.forEach(
                    prepaymentAccountModel -> {
                        if (prepaymentAccountModel.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                            CouponBillPayDetail billPayDetail =
                                    setBillPayRecordDetailValue(
                                            billPay,
                                            prepaymentAccountModel.getAccountItemId(),
                                            prepaymentAccountModel.getAmount(),
                                            (byte) 0,
                                            null, date);
                            billPayDetail.setPatientNum(prepaymentAccountModel.getPrepaymentNum());
                            billPayDetail.setAccountItemName("预付款");
                            billPayDetailMapper.insertSelective(billPayDetail);
                        }
                    });
        }
        if (!CollectionUtils.isEmpty(memberAccountModels)) {
            // 会员卡
            memberAccountModels.forEach(
                    memberAccountModel -> {
                        if (memberAccountModel.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                            CouponBillPayDetail billPayDetail =
                                    setBillPayRecordDetailValue(
                                            billPay,
                                            memberAccountModel.getAccountItemId(),
                                            memberAccountModel.getAmount(),
                                            (byte) 1,
                                            null, date);
                            billPayDetail.setPatientNum(memberAccountModel.getMemberNum());
                            billPayDetail.setAccountItemName("会员卡");
                            billPayDetailMapper.insertSelective(billPayDetail);
                        }
                    });
        }
        if (!CollectionUtils.isEmpty(paymentModels)) {
            // 其他支付方式
            paymentModels.forEach(
                    paymentModel -> {
                        if (paymentModel.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                            CouponBillPayDetail billPayDetail =
                                    setBillPayRecordDetailValue(
                                            billPay,
                                            paymentModel.getAccountItemId(),
                                            paymentModel.getAmount(),
                                            (byte) 2,
                                            paymentModel.getRemarks(), date);
                            billPayDetail.setAccountItemName(paymentModel.getAccountItemName());
                            billPayDetailMapper.insertSelective(billPayDetail);
                        }
                    });
        }
    }

    private BigDecimal calculateTotalCharge(
            Set<CardPrepaymentModel> prepaymentAccountModels,
            Set<CardMemberModel> memberAccountModels,
            Set<CardPaymentModel> paymentModels) {
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        // 预付款入账总额
        if (StringHelper.isNotEmpty(prepaymentAccountModels)) {
            for (CardPrepaymentModel prepaymentAccountModel : prepaymentAccountModels) {
                BigDecimal amount = prepaymentAccountModel.getAmount();
                if (null == amount) {
                    amount = BigDecimal.valueOf(0);
                }
                totalAmount = totalAmount.add(amount);
            }
        }
        // 会员卡入账总额
        if (StringHelper.isNotEmpty(memberAccountModels)) {
            for (CardMemberModel memberAccountModel : memberAccountModels) {
                BigDecimal amount = memberAccountModel.getAmount();
                if (null == amount) {
                    amount = BigDecimal.valueOf(0);
                }
                totalAmount = totalAmount.add(amount);
            }
        }
        // 其他方式入账总额
        if (StringHelper.isNotEmpty(paymentModels)) {
            for (CardPaymentModel paymentModel : paymentModels) {
                BigDecimal amount = paymentModel.getAmount();
                if (null == amount) {
                    amount = BigDecimal.valueOf(0);
                }
                totalAmount = totalAmount.add(amount);
            }
        }
        return totalAmount;
    }

    public CouponBill generateBillRecord(CouponOrder order, Date date, BigDecimal totalCharge,CardInvoiceModel invoiceModel) {
        Integer loginUserId = Integer.valueOf(BaseContextHandler.getUserID());
        CouponBill couponBill = BeanCopierUtils.generalCopyBean(order, CouponBill.class);
        couponBill.setOrderRecordId(order.getId());
        couponBill.setBillNumber(generateBillNumber(order.getOrgId()));
        couponBill.setReceivedAmount(totalCharge);
        couponBill.setDebtAmount(couponBill.getReceivableAmount().subtract(totalCharge));
        couponBill.setPrice(order.getTotalAmount());
        couponBill.setCrtId(loginUserId);
        couponBill.setCrtTime(date);
        couponBill.setUpdId(loginUserId);
        couponBill.setUpdTime(date);
        couponBill.setInvoice(invoiceModel.getInvoice());
        couponBill.setInvoiceNumber(invoiceModel.getInvoiceNumber());
        billMapper.insertSelective(couponBill);
        return couponBill;
    }

    public synchronized String generateBillNumber(Integer orgId) {
        String number = billMapper.selectBillNumberByOrgId(orgId, new Date(System.currentTimeMillis()));
        String suffix = String.format("%04d", Integer.parseInt(number) + 1);
        return String.format(
                "ZD%s%s%s", String.format("%04d", orgId), new DateTime().toString("yyMMdd"), suffix);
    }

    private CouponBillPayDetail setBillPayRecordDetailValue(
            CouponBillPay billPay,
            Integer accountItemId,
            BigDecimal amount,
            Byte type,
            String remarks, Date date) {
        CouponBillPayDetail couponBillPayDetail = new CouponBillPayDetail();
        couponBillPayDetail.setOrgId(billPay.getOrgId());
        couponBillPayDetail.setPatientId(billPay.getPatientId());
        couponBillPayDetail.setOrderId(billPay.getOrderId());
        couponBillPayDetail.setBillId(billPay.getBillId());
        couponBillPayDetail.setBillPayId(billPay.getId());
        couponBillPayDetail.setAccountItemId(accountItemId);
        couponBillPayDetail.setAmount(amount);
        couponBillPayDetail.setType(type);
        couponBillPayDetail.setRemark(remarks);
        couponBillPayDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        couponBillPayDetail.setCrtTime(date);
        couponBillPayDetail.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        return couponBillPayDetail;
    }

    public CouponBill getBill(Integer orderId) {
        Example example = new Example(CouponBill.class);
        example.createCriteria().andEqualTo("orderRecordId", orderId)
                .andEqualTo("inservice", true);
        return billMapper.selectOneByExample(example);
    }

    private void returnGift(CouponBillPay billPay) {
        PatientBaseInfo patientBaseInfo = patientCentralServiceFeign.findPatientInfoById(billPay.getPatientId());
        if (Objects.nonNull(patientBaseInfo) && Objects.equals(2, patientBaseInfo.getOriginType())) {
            BillRebate2MemberAccountModel model1 = new BillRebate2MemberAccountModel();
            model1.setOrderRecordId(billPay.getOrderId());
            model1.setBillRecordId(billPay.getBillId());
            model1.setBillPayRecordId(billPay.getId());
            model1.setOrgId(billPay.getOrgId());
            model1.setAcceptorId(patientBaseInfo.getOriginId());
            model1.setReceivedAmount(billPay.getReceivedAmount());
            patientCentralServiceFeign.billRebate2MemberAccount(model1);
        }

    }

    public List<CouponBillPayDetail> listPayDetail(Integer orderId) {
        Example example = new Example(CouponBillPayDetail.class);
        example.createCriteria().andEqualTo("orderId", orderId)
                .andEqualTo("inservice", true);
        return billPayDetailMapper.selectByExample(example);
    }
}
