package com.yunya365.mini.enums;

import com.yunya.framework.common.constant.PreFixCode;
import com.yunya.framework.common.model.RestError;

/**
 * @author xiangyang
 */

public enum IvyMiniError implements RestError {
    SUCCESS(0, "success"),
    JWT_NOT_LOGIN(1, "登录超时，请重新登录！"),
    JWT_ILLEGAL_ARGUMENT(2, "缺少token参数"),
    JWT_SIGNATURE(3, "不合法的token，请认真比对 token 的签名"),
    ACCOUNT_IS_LOGOUT(4, "用户已登出"),
    WX_SERVER_ERROR(5, "微信服务器异常"),
    MEMBER_NOT_EXIST(6, "用户不存在，请先注册"),
    RECEIVE_NOT_EXIST(7, "收货地址不存在"),
    FANS_NOT_EXIST(8, "用户不存在"),
    STOCK_LACK(9, "库存不足，无法下单"),
    PRODUCT_LACK(10, "产品不存在"),
    PICKUP_NOT_EXIST(11, "取件人信息不存在"),
    SIGN_TYPE_ERROR(12, "验证签名类错误：%s"),
    SIGNATURE_ERROR(13, "验签失败"),
    ORDER_ERROR(14, "订单异常"),
    CB_PAY_ERROR(16, "支付异常"),
    CB_NOTIFY_ERROR(17, "支付回调异常"),
    ORDER_DATA_ERROR(18, "订单数据异常"),
    CB_QUERY_ERROR(19, "支付查询异常"),
    ORDER_CONFIRM_STATUS_ERROR(20, "订单未发货"),
    ORDER_CONFIRM_ERROR(21, "不能确认他人订单"),
    ORDER_REFUND_STATUS_ERROR(22, "订单异常，退款失败"),
    ORDER_REFUND_ERROR(23, "不能退款他人订单"),
    ;
    private final Integer code;
    private final String value;

    IvyMiniError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }
    @Override
    public Integer getCode() {
        if (code.equals(0)) {
            return code;
        }
        return PreFixCode.IVY_MINI.getCode() * 1000 + code;
    }

    @Override
    public String getMessage() {
        return value;
    }
}
