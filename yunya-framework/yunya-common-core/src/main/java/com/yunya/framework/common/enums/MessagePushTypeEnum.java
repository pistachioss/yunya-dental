package com.yunya.framework.common.enums;

import com.yunya.framework.common.utils.StringHelper;

import java.util.Objects;

/**
 * 简介：消息推送类型枚举
 *      1-考勤打卡，
 *
 *      10-请假审批申请，
 *      20-加班审批申请，
 *      30-外勤审批申请，
 *
 *      11-请假审批抄送，
 *      21-加班审批抄送，
 *      31-外勤审批抄送，
 *
 *      12-请假审批通过，
 *      22-加班审批通过，
 *      32-外勤审批通过，
 *
 *      13-请假审批未通过，
 *      23-加班审批未通过，
 *      33-外勤审批未通过，
 *
 *      14-请假审批撤销，
 *      24-加班审批撤销，
 *      34-外勤审批撤销
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/04/19 16:15
 * @since: 1.0.0
 */
public enum MessagePushTypeEnum {
    ATTENDANCE_PUNCH_HINT("考勤打卡提示", 1),
    
    LEAVE_APPROVE_APPLY("请假审批申请", 10),
    WORKOVER_APPROVE_APPLY("加班审批申请", 20),
    FIELD_APPROVE_APPLY("外勤审批申请", 30),

    LEAVE_APPROVE_COPY("请假审批抄送", 11),
    WORKOVER_APPROVE_COPY("加班审批抄送", 21),
    FIELD_APPROVE_COPY("外勤审批抄送", 31),

    LEAVE_APPROVE_PASS("请假审批通过", 12),
    WORKOVER_APPROVE_PASS("加班审批通过", 22),
    FIELD_APPROVE_PASS("外勤审批通过", 32),

    LEAVE_APPROVE_UNPASS("请假审批未通过", 13),
    WORKOVER_APPROVE_UNPASS("加班审批未通过", 23),
    FIELD_APPROVE_UNPASS("外勤审批未通过", 33),

    LEAVE_APPROVE_REVOKE("请假审批撤销", 14),
    WORKOVER_APPROVE_REVOKE("加班审批撤销", 24),
    FIELD_APPROVE_REVOKE("外勤审批撤销", 34);

    private final String value;
    private final Integer code;

    MessagePushTypeEnum(String value, Integer code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static String getValue(Integer code) {
        if (code != null) {
            for (MessagePushTypeEnum item : values()) {
                if (Objects.equals(item.getCode(), code)) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public Boolean inEnums(MessagePushTypeEnum... pushType) {
        if (StringHelper.isNotEmpty(pushType)) {
            for (MessagePushTypeEnum item : pushType) {
                if (equals(item.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean equals(Integer code)
    {
        return this.code.equals(code);
    }
}
