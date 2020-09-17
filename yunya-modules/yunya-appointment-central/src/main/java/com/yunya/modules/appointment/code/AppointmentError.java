package com.yunya.modules.appointment.code;

import com.yunya.framework.common.constant.PreFixCode;

/**
 * @program: yunya-dental
 * @description: 预约错误信息
 * @author: LHB
 * @create: 2020-09-17 11:35
 **/
public enum AppointmentError {
    DATA_FROM_MODEL_EXP(01,"对象转换实体异常"),
    APPOINTMENT_FAIL(10,"患者预约失败"),
    APPOINTMENT_SPLIT_FAIL(11,"预约分解失败"),
    OPERATION_RECORD_FAIL(12,"预约操作记录添加失败"),
    DENTIST_NOT_WORK(13,"预约医生在预约日期当天未排班，建议排班后再新增预约"),
    DENTIST_NOT_ID(14,"预约医生id不能为空"),
    APPOINT_PATIENT_EXIST(15,"患者预约冲突"),
    APPOINT_DENTIST_EXIST(16,"医生预约冲突"),
    APPOINT_DEVICE_EXIST(17,"设备预约冲突"),
    SCHEDULE_SERVER_ERR(30,"员工排班服务异常"),
    ;


    private Integer code;
    private String value;

    AppointmentError(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return PreFixCode.APPOINTMENT.getCode() * 1000 + code;
    }

    public String getMessage() {
        return value;
    }
}
