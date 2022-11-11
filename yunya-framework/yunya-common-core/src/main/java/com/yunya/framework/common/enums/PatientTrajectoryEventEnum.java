package com.yunya.framework.common.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author: chenlin
 * @date: 2022/11/7 17:29
 * @description: 患者轨迹事件
 * @since: 1.0.0
 */
@Getter
public enum PatientTrajectoryEventEnum {

    PATIENT_REGISTE("患者登记", "登记成为患者", 1),
    ADD_APPOINTMENT("添加预约", "添加预约", 2),
    REGIST_TREAT("挂号就诊", "挂号就诊", 3),
    KEEP_APPOINTMENT("履约", "履约", 4),
    MISS_APPOINTMENT("失约", "失约", 5),
    FINISH_TREATMENT("完成挂号就诊", "完成就诊", 6),
    ACTIVE_COUPON("激活产品", "激活了", 7);

    private String desc;
    private String content;
    private Integer eventCode;

    PatientTrajectoryEventEnum(String desc, String content, Integer eventCode) {
        this.desc = desc;
        this.content = content;
        this.eventCode = eventCode;
    }

    public static String getContentByCode(Integer eventCode) {
        if (eventCode != null) {
            for (PatientTrajectoryEventEnum item : values()) {
                if (Objects.equals(item.getEventCode(), eventCode)) {
                    return item.getContent();
                }
            }
        }
        return null;
    }

    public boolean equals(Integer eventCode)
    {
        return this.eventCode.equals(eventCode);
    }
}
