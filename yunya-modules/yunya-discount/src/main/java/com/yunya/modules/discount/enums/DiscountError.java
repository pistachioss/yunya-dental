package com.yunya.modules.discount.enums;

import com.yunya.framework.common.constant.PreFixCode;
import com.yunya.framework.common.model.*;

/**
 * @author xiangyang
 */

public enum DiscountError implements RestError {
    CARD_IS_GENERATED(1, "产品已生成卡券"),
    FAIL_TO_GENERATE(2, "卡券生成失败"),
    COUPON_NOT_ALLOCATE(3, "优惠券未分配，请先分配再生成"),
    ORG_NOT_ALLOCATE(4, "%s未分配该优惠券，不能生成分配"),
    NUM_NOT_EQUAL(5, "优惠券生成数量和分配数量不一致"),
    ORG_BATCH_ERROR(6, "数据异常，%s不属于当前生成批次"),
    COUPON_IS_LOCKED(7, "卡券正在生成分配中，无法提交"),
    CARD_SOLD_IS_LOCKED(8, "卡券正在售卖中，无法提交"),
    COUPON_NOT_EXIST(9, "优惠券不存在"),
    SOLD_DATE_RANGE_ERROR(10, "卡券不在售出时间范围内"),
    ORG_COUPON_NOT_ALLOCATE(11, "优惠券%s，%s未生成分配"),
    CARD_NOT_EXIST(12, "卡券不存在"),
    CARD_SOLD_STATUS_ERROR(13, "卡券售卖状态异常"),
    CARD_SOLD_OUT(14, "%s的%s已全部售出"),
    CARD_NOT_BELONG_ORG(16, "该卡券不属于%s"),
    CARD_NOT_BELONG_COUPON(17, "卡券是%s，请重新选择优惠券类型"),
    CARD_ACTIVE_IS_LOCKED(18, "卡券正在激活中，无法提交"),
    CARD_IS_ACTIVATED(19, "该卡券已被激活，无需再次激活！"),
    CARD_ACTIVE_STATUS_ERROR(20, "该卡券未售出，不可以激活！"),
    CARD_BEYOND_DEADLINE(21, "该卡券已过产品有效期，不可以激活！"),
    CARD_NOT_CHARGE(22, "该卡券未收费，请收费后再激活！"),
    CARD_IS_CHARGED(23, "卡券已收费"),
    OTHER_ALLOW_ACTIVE_OWN(24, "自有平台卡券不允许在第三方平台激活"),
    CARD_NOT_ACTIVATED(25, "卡券未激活"),
    COUPON_NOT_ALLOW_SHARE(26, "该优惠券不能与他人共享"),
    SHARER_NOT_ALLOW_OWNER(27, "配置共享人不能是患者自己"),
    BEYOND_CARD_LIMIT_NUM(28, "已超过卡券最大生成数量"),
    CARD_NOT_SOLD(29, "卡券未售卖"),
    PATIENT_NOT_OWN_MEMBER(30, "患者不能使用该会员卡"),
    PATIENT_NOT_OWN_DISCOUNT(31, "患者不能使用该折扣券"),
    PATIENT_NOT_OWN_EXCHANGE(32, "患者不能使用该兑换券%s，没有适用项目"),
    PATIENT_NOT_OWN_PACKAGE(33, "患者不能使用该套餐券%s，没有适用项目"),
    PATIENT_NOT_OWN_VOUCHER(34, "患者不能使用该代金券%s，没有适用项目"),
    COUPON_BEYOND_LIMIT_COUNT(35, "选中同产品的卡券%s已超过单个账单限制使用的卡券数量！"),
    CARD_HAS_CHOICE(36,"卡号是%s的卡券正在被使用，请取消使用该卡券！"),
    CANT_USE_BENEFIT(37, "患者没有可使用优惠券信息"),
    EMPLOYEE_NO_AUTH_DISCOUNT(38, "该员工没有授权折扣权限"),
    ORDER_NOT_EXIST(39, "未查询到订单对应的项目明细"),
    ORDER_ITEM_NOT_EXIST(40, "项目不存在"),
    AUTH_BENEFIT_AMOUNT_ERROR(41, "授权折扣项目优惠金额异常"),
    ORDER_ON_SUBMITTING(42, "订单正在使用优惠，请勿重复提交"),
    PATIENT_EXCHANGE_NULL(43, "患者没有可使用的兑换券"),
    PATIENT_PACKAGE_NULL(44, "患者没有可使用的套餐券"),
    PATIENT_VOUCHER_NULL(45, "患者没有可使用的代金券"),
    PATIENT_DISCOUNT_NULL(46, "患者没有可使用的折扣券"),
    PATIENT_MEMBER_NULL(47, "患者没有可使用的会员卡"),
    CARD_IS_ON_SALE(48,"卡号为%s卡券正在售出中，请稍后重试！"),
    CARD_NUMBER_ERROR(49,"卡号不存在，请输入正确的卡号！"),
    CARD_PASSWORD_ERROR(50,"卡密错误，请输入正确的卡密！"),
    ORDER_NO_BENEFIT(51,"订单没有优惠信息"),
    ORDER_HAS_BENEFIT(52,"订单已经使用过优惠，不能重复使用"),
    RECHARGE_NOT_ALLOW(53, "该卡券属于充值卡，不可以激活，需要充值！"),
    OTHER_CARD_NOT_ALLOW(54, "只有充值卡卡券才能充值，其它类型卡券不允许充值！"),
    RECHARGE_TIME_OUT(55, "该卡券已过充值截止时间，不可以充值！"),
    RECHARGE_HAS_RECHARGED(56,"该卡券已被充值，不可以再次充值！"),
    RECHARGE_NOT_SOLD(57, "该充值卡未售出，不可以充值！"),
    BENEFIT_PACKAGE_ITEM_EMPTY(58,"套餐券项目为空,请添加项目后再保存"),
    ;
    private Integer code;
    private String value;

    DiscountError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
    @Override
    public Integer getCode() {
        return PreFixCode.DISCOUNT.getCode() * 1000 + code;
    }

    @Override
    public String getMessage() {
        return value;
    }
}
