package com.yunya.feign.wechat.enums;
public enum TemplateEnum {
    APPOINT_CONFIRM("预约确认通知"),
    BIND_SUCCESS("绑定成功通知"),
    UNBIND_SUCCESS("解绑成功通知"),
    APPOINT_SUCCESS("预约成功提醒"),
    APPOINT_CHANGE("预约变更成功通知"),
    APPOINT_CANCEL("预约取消提醒"),
    MEMBER_OPEN_CARD("会员卡开卡通知"),
    RECHARGE_SUCCESS("充值成功提醒"),
    MEMBER_CONSUME("会员消费提醒"),
    MEMBER_PAY("缴费成功提醒"),
    ACTIVATED_UNUSED("次卡使用提醒"),
    CARD_EXPIRING("授权到期提醒"),
    APPOINT_EXPIRED("服务到期提醒"),
    ;

    private String title;

    TemplateEnum(String templateId) {
        this.title = templateId;
    }
    public String getTemplateId() {
        return title;
    }
}
