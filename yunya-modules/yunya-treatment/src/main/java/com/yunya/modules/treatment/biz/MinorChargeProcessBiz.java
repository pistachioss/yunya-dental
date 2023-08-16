package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.model.BillRebate2MemberAccountModel;
import com.yunya.feign.patient_central.domain.model.MemberExpendRecordModel;
import com.yunya.feign.patient_central.domain.model.PrepaidExpendRecordModel;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.treatment.domain.model.*;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordVO;
import com.yunya.feign.wechat.RemoteWechatServiceFeign;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.feign.wechat.enums.TemplateEnum;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
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
            // 保存订单项目收费分摊明细
            billPayShareDetailBiz.saveItemPaySharedAmount(totalCharge, payments, billPayRecord);
            // 调用保存优惠明细接口
            savePrivilegeDetail(discountType, billPayRecord.getPatientId(), orderRecordId, model);
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
            if (StringHelper.gtZero(totalCharge)) {
                TimeUnit.SECONDS.sleep(3);
                patientFeign.autoUpdateMemberType(billPayRecord.getPatientId());
            }
            completedChargeLog(billPayRecord.getId());
        } catch (Exception e) {
            log.error("MinorChargeProcessBize asyncProcessCharge error: {}", e);
            errorChargeLog(billPayRecord.getId(), ExceptionUtils.getFullStackTrace(e));
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
    private void expendRebateReferees(BillPayRecord billPayRecord, BigDecimal totalPrincipal) {
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
     * @param discountType 优惠类型
     * @param patientId 患者ID
     * @param orderRecordId 订单记录ID
     * @param model 收费添加模型
     */
    public void savePrivilegeDetail(
            Byte discountType,
            Integer patientId,
            Integer orderRecordId,
            TreatTollModel model) {
        switch (discountType) {
            case 1:
                GeneralDiscountModel generalDiscount = model.getGeneralDiscountModel();
                if (StringHelper.isNotNull(generalDiscount)) {
                    saveCouponPrivilege(patientId, orderRecordId, generalDiscount);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 保存优惠券使用优惠明细
     *
     * @param patientId 患者ID
     * @param orderRecordId 订单记录ID
     * @param generalDiscount 优惠列表
     */
    private void saveCouponPrivilege(
            Integer patientId, Integer orderRecordId, GeneralDiscountModel generalDiscount) {
        PatientOrderBenefitModel benefitModel = new PatientOrderBenefitModel();
        benefitModel.setOrderId(orderRecordId);
        benefitModel.setPatientId(patientId);
        benefitModel.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
        benefitModel.setMemberCardId(generalDiscount.getMemberTypeId());
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
        System.out.println(JSON.toJSON(benefitModel));
        ResponseResult responseResult = discountFeign.saveCardBenefit(benefitModel);
        if (responseResult.getStatus() > 0) {
            throw new ClientServiceException(responseResult.getMsg(), responseResult.getStatus());
        }
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
}
