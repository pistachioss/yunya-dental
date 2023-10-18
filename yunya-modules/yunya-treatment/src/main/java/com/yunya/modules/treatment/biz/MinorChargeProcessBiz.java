package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.model.AuthItemBenefitModel;
import com.yunya.feign.discount.domain.model.MixMatchBenefitModel;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.BillRebate2MemberAccountModel;
import com.yunya.feign.patient_central.domain.model.MemberExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.domain.form.QcTreatmentImportForm;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.feign.wechat.RemoteWechatServiceFeign;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.feign.wechat.enums.TemplateEnum;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.*;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.BillPayRecordLog;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.mapper.BillPayRecordLogMapper;
import com.yunya.modules.treatment.mapper.BillRecordMapper;
import com.yunya.modules.treatment.mapper.TreatmentRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.*;
import static com.yunya.framework.common.constant.BusinessConstants.FIRST_VISIT_REBATE_AMOUNT;

/**
 * @author: chenlin
 * @date: 2023/7/11 17:36
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class MinorChargeProcessBiz {

    /** 收费项目分摊明细 */
    @Autowired private BillPayShareDetailBiz billPayShareDetailBiz;
    @Autowired private RemoteDiscountFeign discountFeign;
    @Autowired private RemotePatientCentralServiceFeign patientFeign;
    @Autowired private RemoteWechatServiceFeign wechatServiceFeign;
    @Autowired private TreatmentRecordMapper treatmentRecordMapper;
    @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
    @Autowired private OrderDetailBiz orderDetailBiz;
    @Autowired private BillPayRecordLogMapper billPayRecordLogMapper;
    @Autowired private BillRecordMapper billRecordMapper;
    @Autowired private RemoteTreatmentOtherFeign treatmentOtherFeign;
    @Autowired private RemoteSystemServiceFeign systemServiceFeign;
    @Value("${sysconfig.memberSystemReleaseDate}")
    private String releaseDate;

    /**
     * 异步处理次要收费流程
     *
     * @param billPayRecord
     * @param model
     * @param totalCharge
     * @param totalPrincipal
     * @param isMqTreatment
     */
    @Async("asyncExecutor")
    protected void asyncProcessCharge(BillPayRecord billPayRecord, TreatTollModel model, BigDecimal totalCharge, BigDecimal totalPrincipal, Boolean isMqTreatment) {
        Integer orderRecordId = billPayRecord.getOrderRecordId();
        Set<PrepaymentAccountModel> prepaymentAccounts = model.getPrepaymentAccountModels();
        Set<MemberAccountModel> memberAccounts = model.getMemberAccountModels();
        Set<PaymentModel> payments = model.getPaymentModels();
        Byte discountType = model.getDiscountType();
        try {
            // 账单绑定全程医疗就诊单
            List<OrderDetailChargeVO> orderDetail = updateQcTreatmentAndItems(billPayRecord, model.getQcTreatmentIds());
            // 调用保存优惠明细接口
            savePrivilegeDetail(discountType, billPayRecord.getPatientId(), orderRecordId, model, orderDetail);
            // 保存订单项目收费分摊明细
            billPayShareDetailBiz.saveItemPaySharedAmount(totalCharge, payments, billPayRecord);
            // 调用预付款消费接口
            if (StringHelper.isNotEmpty(prepaymentAccounts)) {
                usePrepaymentAccount(
                        prepaymentAccounts,
                        billPayRecord);
            }
            // 调用会员卡消费接口
            if (StringHelper.isNotEmpty(memberAccounts)) {
                ResponseResult expend =
                        useMemberAccount(
                                memberAccounts,
                                billPayRecord);
                if (expend.getStatus() > 0) {
                    throw new ClientServiceException(expend.getMsg(), expend.getStatus());
                }
            }
            // 消费返点
            expendRebateReferees(billPayRecord, totalPrincipal);
            // 发送MQ消息
            chargedMQMiddleTable(billPayRecord, isMqTreatment);
            giftFirstVisitPackage(billPayRecord);
            autoUpdateMemberType(totalCharge, billPayRecord.getPatientId());
            completedChargeLog(billPayRecord.getId());
        } catch (Exception e) {
            log.error("MinorChargeProcessBize asyncProcessCharge error: {}", e);
            errorChargeLog(billPayRecord.getId(), ExceptionUtils.getFullStackTrace(e));
        }
    }

    /**
     * 用账单信息更新全程医疗就诊及其明细（绑定或更新实收）
     *
     * @param billPayRecord
     * @param qcTreatmentIds
     * @return
     */
    private List<OrderDetailChargeVO> updateQcTreatmentAndItems(BillPayRecord billPayRecord, List<Integer> qcTreatmentIds) {
        Integer orderRecordId = billPayRecord.getOrderRecordId();
        // 订单明细（项目实收）
        List<OrderDetailChargeVO> orderDetails = orderDetailBiz.getChargeOrderDetailList(orderRecordId);
        QcTreatmentImportForm form = new QcTreatmentImportForm();
        form.setBillPayId(billPayRecord.getId());
        form.setOrderRecordId(orderRecordId);
        form.setQcTreatmentIds(qcTreatmentIds);
        form.setOrderDetails(orderDetails);
        return treatmentOtherFeign.updateQcTreatmentAndItems(form);
    }

    /**
     * 会员根据就诊账单消费（特定入账方式）进行自动升级
     *
     * @param totalCharge
     * @param patientId
     */
    @Async("asyncExecutor")
    public void autoUpdateMemberType(BigDecimal totalCharge, Integer patientId) {
        if (StringHelper.gtZero(totalCharge)) {
            patientFeign.autoUpdateMemberType(patientId);
        }
    }

    /**
     * 错误异常信息记录
     *
     * @param billPayRecordId
     * @param message
     */
    private void errorChargeLog(Integer billPayRecordId, String message) {
        Example example = new Example(BillPayRecordLog.class);
        Example.Criteria c = example.createCriteria();
        c.andEqualTo("billPayRecordId", billPayRecordId);
        BillPayRecordLog entity = new BillPayRecordLog();
        entity.setErrMsg(message);
        billPayRecordLogMapper.updateByExampleSelective(entity, example);
    }

    /**
     * 收费完成记录，更新状态
     *
     * @param billPayRecordId
     */
    private void completedChargeLog(Integer billPayRecordId) {
        Example example = new Example(BillPayRecordLog.class);
        Example.Criteria c = example.createCriteria();
        c.andEqualTo("billPayRecordId", billPayRecordId);
        c.andEqualTo("status", 1);
        BillPayRecordLog entity = new BillPayRecordLog();
        entity.setStatus((byte) 2);
        entity.setUptTime(BaseContextHandler.getCurTime());
        billPayRecordLogMapper.updateByExampleSelective(entity, example);
    }

    /**
     * 给初诊患者赠予大礼包
     *
     * @param billPayRecord
     */
    private void giftFirstVisitPackage(BillPayRecord billPayRecord) {
    }

    /**
     * 给患者的亲密付主卡人或推荐关系人返点
     *
     * @param billPayRecord
     * @param totalPrincipal 消费本金总额
     */
    private void expendRebateReferees(BillPayRecord billPayRecord, BigDecimal totalPrincipal) throws InterruptedException {
////        BillPayRecordLog query = new BillPayRecordLog();
////        query.setBillPayRecordId(billPayRecord.getId());
////        int count = billPayRecordLogMapper.selectCount(query);
//        if (count > 0) {
        if (isNewMemberSystem(billPayRecord)) {
            if (StringHelper.gtZero(totalPrincipal)) {
                // 患者消费时给其推荐人（推荐关系）返点
                BillRebate2MemberAccountModel model = new BillRebate2MemberAccountModel();
                BeanUtil.copyProperties(billPayRecord, model);
                model.setBillPayRecordId(billPayRecord.getId());
                model.setType(6);
                model.setReceivedAmount(totalPrincipal);
                patientFeign.billRebate2MemberAccount(model);

                // 给初诊患者的推荐人返点
                TreatmentRecordVO treatment = treatmentRecordMapper.selectTreatmentInfoById(billPayRecord.getTreatmentRecordId());
                if (StringHelper.isNotNull(treatment) && treatment.getFirstVisit() == 0) {
                    // 睡眠1秒，避免两次返点的发生时间相同
                    TimeUnit.SECONDS.sleep(1);
                    BeanUtil.copyProperties(billPayRecord, model);
                    model.setBillPayRecordId(billPayRecord.getId());
                    model.setType(6);
                    model.setRebateRatioType((byte) 0);
                    model.setReceivedAmount(FIRST_VISIT_REBATE_AMOUNT);
                    patientFeign.billRebate2MemberAccount(model);
                }
            }
        }
    }

    /**
     * 判断账单日期是否在新会员体系发布之后
     *
     * @param billPayRecord
     * @return
     */
    private boolean isNewMemberSystem(BillPayRecord billPayRecord) {
        Integer billRecordId = billPayRecord.getBillRecordId();
        BillRecord billRecord = billRecordMapper.selectByPrimaryKey(billRecordId);
        Date date = DateUtil.parse2Date(releaseDate);
        return billRecord.getCrtTime().after(date);
    }

    /**
     * 收费后同步中间表
     *
     * @param billPayRecord
     * @param isMqTreatment
     */
    private void chargedMQMiddleTable(BillPayRecord billPayRecord, Boolean isMqTreatment) {
        rabbitMqServiceFeign.sendMessage(billPayRecord.getOrderRecordId(), 1, BaseBill);
        rabbitMqServiceFeign.sendMessage(billPayRecord.getId(), 0, BaseBillPay);
        rabbitMqServiceFeign.sendMessage(billPayRecord.getOrderRecordId(), 1, BaseBill);
        // 变更治疗计划详情的状态
        rabbitMqServiceFeign.sendMessage(billPayRecord.getTreatmentRecordId(), 0, TreatPlanDetail);
        if (isMqTreatment) {
            mqSendTreatMessage(billPayRecord.getTreatmentRecordId());
        }
    }

    /**
     * 同步BaseTreatmentProcess
     * @param treatmentRecordId
     */
    private void mqSendTreatMessage(Integer treatmentRecordId) {
        TreatmentRecord treatmentRecord = treatmentRecordMapper.selectByPrimaryKey(treatmentRecordId);
        Integer appointmentId = treatmentRecord.getAppointmentId();
        if (StringHelper.isNotNull(appointmentId)) {
            rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
        } else {
            rabbitMqServiceFeign.sendMessage(
                    treatmentRecord.getRegisteredId(), 1, 1, BaseTreatmentProcess);
        }
    }

    /**
     * 异步发送微信消费通知
     *
     * @param billPayRecord
     */
    @Async("treatmentThreadPool")
    public void asyncPushWxExpendMsg(BillPayRecord billPayRecord) {
        try {
            PatientBaseInfo patient = patientFeign.findPatientInfoById(billPayRecord.getPatientId());
            String itemName = assembleItemName(billPayRecord.getOrderRecordId());
            WxTemplateMsgModel model = new WxTemplateMsgModel();
            Map<String, Object> paramMap = Maps.newHashMap();
            paramMap.put("keyword1", itemName);
            paramMap.put("keyword2", billPayRecord.getReceivedAmount());
            paramMap.put("keyword3", patient.getName());
            model.setPatientId(billPayRecord.getPatientId());
            model.setTemplateEnum(TemplateEnum.MEMBER_PAY);
            model.setParamMap(paramMap);
            wechatServiceFeign.pushTemplate(model);
        } catch (Exception var6) {
            log.error("TreatTollBiz aynsPushWxExpendMsg error: {}", var6);
        }
    }

    /**
     * 查询并装配项目名称列表
     *
     * @param orderRecordId
     * @return
     */
    public String assembleItemName(Integer orderRecordId) {
        List<OrderDetailVO> orderDetails = orderDetailBiz.findOrderDetailList(orderRecordId, null);
        return orderDetails.stream()
                .filter(vo->StringHelper.isNotEmpty(vo.getBillingItemName()))
                .map(vo->vo.getBillingItemName())
                .collect(Collectors.joining("，"));
    }

    /**
     * 使用预付款账户付款
     *
     * @param prepaymentAccountModels 预付款账户列表
     * @param billPayRecord 账单收费记录
     */
    private void usePrepaymentAccount(
            Set<PrepaymentAccountModel> prepaymentAccountModels,
            BillPayRecord billPayRecord) {
        PrepaidExpendRecordModel prepaidExpendRecordModel = new PrepaidExpendRecordModel();
        prepaymentAccountModels.forEach(
                prepaymentAccountModel -> {
                    prepaidExpendRecordModel.setPatientId(billPayRecord.getPatientId());
                    prepaidExpendRecordModel.setPrepaidId(prepaymentAccountModel.getPrepaymentNum());
                    prepaidExpendRecordModel.setExpendTotal(prepaymentAccountModel.getAmount());
                    prepaidExpendRecordModel.setTreatmentRecordId(billPayRecord.getTreatmentRecordId());
                    prepaidExpendRecordModel.setOrderRecordId(billPayRecord.getOrderRecordId());
                    prepaidExpendRecordModel.setBillRecordId(billPayRecord.getBillRecordId());
                    prepaidExpendRecordModel.setBillPayRecordId(billPayRecord.getId());
                    prepaidExpendRecordModel.setPrincipalAmount(prepaymentAccountModel.getPrincipalAmount());
                    prepaidExpendRecordModel.setBonusAmount(prepaymentAccountModel.getBonusAmount());
                    ResponseResult result = patientFeign.expend(prepaidExpendRecordModel);
                    if (!result.getStatus().equals(0)) {
                        throw new ClientServiceException(result.getMsg(), result.hashCode());
                    }
                });
    }


    /**
     * 使用会员账户付款
     *
     * @param memberAccountModels 会员账户列表
     * @param billPayRecord 账单收费记录
     * @return 处理结果
     */
    private ResponseResult useMemberAccount(
            Set<MemberAccountModel> memberAccountModels,
            BillPayRecord billPayRecord) {
        MemberExpendRecordModel memberExpendRecordModel = new MemberExpendRecordModel();
        Iterator<MemberAccountModel> iterator = memberAccountModels.iterator();
        ResponseResult responseResult = null;
        while (iterator.hasNext()) {
            MemberAccountModel memberAccountModel = iterator.next();
            memberExpendRecordModel.setPatientId(billPayRecord.getPatientId());
            memberExpendRecordModel.setMemberId(memberAccountModel.getMemberNum());
            memberExpendRecordModel.setExpendTotal(memberAccountModel.getAmount());
            memberExpendRecordModel.setTreatmentRecordId(billPayRecord.getTreatmentRecordId());
            memberExpendRecordModel.setOrderRecordId(billPayRecord.getOrderRecordId());
            memberExpendRecordModel.setBillRecordId(billPayRecord.getBillRecordId());
            memberExpendRecordModel.setBillPayRecordId(billPayRecord.getId());
            memberExpendRecordModel.setPrincipalAmount(memberAccountModel.getPrincipalAmount());
            memberExpendRecordModel.setBonusAmount(memberAccountModel.getBonusAmount());
            ResponseResult expend = patientFeign.expend(memberExpendRecordModel);
            // 服务调用成功返回0，否则返回大于0的状态码
            if (expend.getStatus() > 0) {
                responseResult = expend;
                break;
            }
        }
        return responseResult != null ? responseResult : ResponseUtil.success();
    }


    /**
     * 保存优惠明细
     *
     * @param discountType  优惠类型
     * @param patientId     患者ID
     * @param orderRecordId 订单记录ID
     * @param model         收费添加模型
     * @param orderDetail
     */
    public void savePrivilegeDetail(
            Byte discountType,
            Integer patientId,
            Integer orderRecordId,
            TreatTollModel model, List<OrderDetailChargeVO> orderDetail) {
        if (discountType.intValue() == 1) {
            GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();
            AccreditDiscountModel accreditDiscountModel = model.getAccreditDiscountModel();
            saveOrderPrivilegeDetail(patientId, orderRecordId, generalDiscount, accreditDiscountModel, orderDetail);
        }
    }


    /**
     * 将订单明细匹配授权折扣（会员与授权折扣不能共存，卡券与授权折扣不能叠加）
     *
     * @param accreditDiscounts
     * @param warrantId
     * @param orderDetails
     */
    public BigDecimal orderDetailMatcAccreditDiscount(List<AccreditDiscountDetailModel> accreditDiscounts, Integer warrantId, List<OrderDetailChargeVO> orderDetails) {
        BigDecimal benefitTotalAmount = BigDecimal.ZERO;
        for (OrderDetailChargeVO item : orderDetails) {
            Integer itemId = item.getBillingItemId();
            Byte type = item.getType();
            Boolean canMatchAccreditDiscount = true;
            List<PrivilegeCouponInfoVO> discountAppliesCoupons = item.getDiscountAppliesCoupons();
            for (PrivilegeCouponInfoVO discountAppliesCoupon : discountAppliesCoupons) {
                if (!NumberUtil.betweenAnd(discountAppliesCoupon.getCouponType(), 99, 101)) {
                    canMatchAccreditDiscount = false;
                    break;
                }
            }
            if (canMatchAccreditDiscount) {
                for (AccreditDiscountDetailModel discountItem : accreditDiscounts) {
                    if (discountItem.getBillingItemId().equals(itemId) && discountItem.getType().equals(type)) {
                        BigDecimal receivableAmount = item.getReceivableAmount();
                        BigDecimal actualAmount = discountItem.getActualAmount();
                        // 订单明细ID相同，参数数量大于明细数量表示前端合并了相同项目，实收金额需重新计算
                        Integer quantity = item.getQuantity();
                        Integer modelQuantity = discountItem.getQuantity();
                        // 优惠单价
                        BigDecimal discountPrice =
                                actualAmount.divide(
                                        BigDecimal.valueOf(modelQuantity), 4, RoundingMode.HALF_UP);
                        actualAmount = discountPrice.multiply(BigDecimal.valueOf(quantity));
                        item.setActualAmount(actualAmount);
                        // 设置折扣率；折扣率 = 实收 / 原价 * 100
                        if (StringHelper.eqZero(receivableAmount)) {
                            BigDecimal discountRate =
                                    actualAmount
                                            .divide(receivableAmount, 4, RoundingMode.HALF_UP)
                                            .multiply(BigDecimal.valueOf(100));
                            item.setDiscountRate(discountRate);
                        }
                        // 设置优惠匹配信息
                        BigDecimal benefitAmount = receivableAmount.subtract(actualAmount);
                        if (StringHelper.gtZero(benefitAmount)) {
                            PrivilegeCouponInfoVO couponInfoVO = new PrivilegeCouponInfoVO();
                            couponInfoVO.setBenefitId(warrantId);
                            couponInfoVO.setCouponType(-1);
                            SysEmployee employee = systemServiceFeign.findSysEmployeeById(warrantId);
                            if (null != employee) {
                                couponInfoVO.setBenefitName(employee.getName());
                            }
                            couponInfoVO.setBenefitAmount(benefitAmount);
                            discountAppliesCoupons.add(couponInfoVO);
                            item.setPrivilegeAmount(benefitAmount);
                            benefitTotalAmount = benefitTotalAmount.add(benefitAmount);
                        }
                    }
                }
            }
        }
        return benefitTotalAmount;
    }

    /**
     * 保存订单使用优惠明细
     *
     * @param patientId       患者ID
     * @param orderRecordId   订单记录ID
     * @param generalDiscount 优惠列表
     * @param accreditDiscountModel 授权折扣列表
     * @param orderDetail 可优惠的订单明细
     */
    private void saveOrderPrivilegeDetail(
            Integer patientId, Integer orderRecordId, GeneralDiscountModel generalDiscount, AccreditDiscountModel accreditDiscountModel, List<OrderDetailChargeVO> orderDetail) {
        MixMatchBenefitModel benefitModel = new MixMatchBenefitModel();
        benefitModel.setOrderId(orderRecordId);
        benefitModel.setOrderDetail(orderDetail);
        benefitModel.setPatientId(patientId);
        benefitModel.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));

        benefitModel.setMemberCardId(findMemberTypeByDiscountType(patientId, generalDiscount));
        benefitModel.setDiscountId(generalDiscount.getDiscountCouponId());
        List<CouponDiscountInfoModel> coupons = generalDiscount.getCouponDiscountInfoModels();
        List<Integer> voucherIds = Lists.newArrayList();
        List<Integer> exchangeIds = Lists.newArrayList();
        List<Integer> packageIds = Lists.newArrayList();
        List<Integer> deductionIds = Lists.newArrayList();
        setCouponListValue(coupons, voucherIds, exchangeIds, packageIds, deductionIds);
        benefitModel.setExchangeIds(exchangeIds);
        benefitModel.setPackageIds(packageIds);
        benefitModel.setVoucherIds(voucherIds);
        benefitModel.setDeductionIds(deductionIds);

        // 授权折扣
        benefitModel.setAuthorizedId(accreditDiscountModel.getWarrantId());
        benefitModel.setRemark(accreditDiscountModel.getRemarks());
        benefitModel.setItemBenefits(convertAccreditBenefits(accreditDiscountModel, orderDetail));

        // TODO: 2023/10/13 打印
        System.out.println(JSON.toJSON(benefitModel));
        ResponseResult responseResult = discountFeign.saveMixMatchBenefit(benefitModel);
        if (responseResult.getStatus() > 0) {
            throw new ClientServiceException(responseResult.getMsg(), responseResult.getStatus());
        }
    }

    /**
     * 授权折扣明细类型转换
     *
     * @param accreditDiscountModel
     * @param orderDetail
     * @return
     */
    private List<AuthItemBenefitModel> convertAccreditBenefits(AccreditDiscountModel accreditDiscountModel, List<OrderDetailChargeVO> orderDetail) {
        List<AccreditDiscountDetailModel> accreditDiscounts = accreditDiscountModel.getAccreditDiscountDetailModels();
        if (StringHelper.isNotEmpty(accreditDiscounts)) {
            return accreditDiscounts.stream().filter(discount-> StringHelper.lt(discount.getDiscountRate(), BigDecimal.valueOf(100))).map(discount->{
                Byte type = discount.getType();
                Integer itemId = discount.getBillingItemId();
                AuthItemBenefitModel authDiscountModel = new AuthItemBenefitModel();
                authDiscountModel.setItemId(itemId);
                authDiscountModel.setType(type.intValue());
                orderDetail.forEach(item->{
                    if (item.getType().equals(type) && item.getBillingItemId().equals(itemId)) {
                        authDiscountModel.setOrderDetailId(item.getOrderDetailId());
                        authDiscountModel.setBenefitAmount(item.getReceivableAmount().subtract(item.getActualAmount()));
                    }
                });
                return authDiscountModel;
            }).collect(Collectors.toList());
        }
        return null;
    }


    /**
     * 将卡券列表进行分类
     *
     * @param coupons 优惠卡券
     * @param voucherIds 代金券
     * @param exchangeIds 兑换券
     * @param packageIds 套餐券
     */
    public void setCouponListValue(
            List<CouponDiscountInfoModel> coupons,
            List<Integer> voucherIds,
            List<Integer> exchangeIds,
            List<Integer> packageIds,
            List<Integer> deductionIds) {
        if (StringHelper.isNotEmpty(coupons)) {
            for (CouponDiscountInfoModel model : coupons) {
                Byte couponType = model.getCouponType();
                Integer couponCommonInfoId = model.getCouponCommonInfoId();
                switch (couponType) {
                    // 代金券
                    case 0:
                        voucherIds.add(couponCommonInfoId);
                        break;
                    // 兑换券
                    case 2:
                        exchangeIds.add(couponCommonInfoId);
                        break;
                    // 套餐券
                    case 3:
                        packageIds.add(couponCommonInfoId);
                        break;
                    // 划扣卡券
                    case 5:
                        deductionIds.add(couponCommonInfoId);
                    default:
                        break;
                }
            }
        }
    }

    public Integer findMemberTypeByDiscountType(Integer patientId, GeneralDiscountModel generalDiscountModel) {
        PatientMemberInfo memberInfo = findPatientMemberInfoByDiscountType(patientId, generalDiscountModel.getMemberDiscountType());
        if (StringHelper.isNotNull(memberInfo)) {
            return memberInfo.getMemberTypeId();
        }
        return null;
    }

    /**
     * 根据会员优惠类型匹配患者相关的会员信息
     *
     * @param patientId
     * @param memberDiscountType
     * @return
     */
    public PatientMemberInfo findPatientMemberInfoByDiscountType(Integer patientId, Integer memberDiscountType) {
        if (StringHelper.isAnyNull(patientId, memberDiscountType)) {
            return null;
        }
        return patientFeign.matchPatientMemberInfo(patientId, memberDiscountType);
    }
}
