package com.yunya.modules.appointment.code;

import com.yunya.framework.common.constant.PreFixCode;

/**
 * @program: yunya-dental
 * @description: 预约错误信息
 * @author: LHB
 * @create: 2020-09-17 11:35
 **/
public enum AppointmentError {
    /** 01-对象转换实体异常 */
    DATA_FROM_MODEL_EXP(01,"对象转换实体异常"),
    /** 02-开始日期不能大于结束日期 */
    START_DATE_AFTER_END_DATE(02,"开始日期不能大于结束日期"),
    /** 10-患者预约失败 */
    APPOINTMENT_FAIL(10,"患者预约失败"),
    /** 11-预约分解失败 */
    APPOINTMENT_SPLIT_FAIL(11,"预约分解失败"),
    /** 12-预约操作记录添加失败 */
    OPERATION_RECORD_FAIL(12,"预约操作记录添加失败"),
    /** 13-预约医生在预约日期当天未排班，建议排班后再新增预约 */
    DENTIST_NOT_WORK(13,"预约医生在预约日期当天未排班，建议排班后再新增预约"),
    /** 14-预约医生id不能为空 */
    DENTIST_NOT_ID(14,"预约医生id不能为空"),
    /** 15-患者预约冲突 */
    APPOINT_PATIENT_EXIST(15,"患者预约冲突"),
    /** 16-医生预约冲突 */
    APPOINT_DENTIST_EXIST(16,"医生预约冲突"),
    /** 17-设备预约冲突 */
    APPOINT_DEVICE_EXIST(17,"设备预约冲突"),
    /** 18-医生在当前日期下没有排班 */
    DENTIST_NOT_SCHEDULE(18,"医生在当前日期下没有排班"),

    /** 30-员工排班服务异常 */
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
