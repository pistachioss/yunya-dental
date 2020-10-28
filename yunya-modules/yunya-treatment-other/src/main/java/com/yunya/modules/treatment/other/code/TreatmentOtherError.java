package com.yunya.modules.treatment.other.code;

import com.yunya.framework.common.constant.PreFixCode;

/**
 * @program: yunya-dental
 * @description: 就诊扩展错误信息
 * @author: LHB
 * @create: 2020-09-17 11:35
 **/
public enum TreatmentOtherError {
    /** 01-对象转换实体异常 */
    DATA_FROM_MODEL_EXP(01,"对象转换实体异常"),
    /** 02-开始日期不能大于结束日期 */
    START_DATE_AFTER_END_DATE(02,"开始日期不能大于结束日期"),

    /** 10-新增随访失败*/
    INSERT_VISITING_RECORD_ERR(10,"新增随访失败"),
    /** 11-{0}的随访有冲突,请修改后重新提交*/
    VISITING_CONFIICT_EXP(11,"随访有冲突,请修改后重新提交"),

    /** 20-该条记录不允许编辑*/
    EDIT_NOT_ALLOWED(20,"该条记录正在被修改中,不允许编辑"),


    /** 30-员工排班服务异常 */
    SCHEDULE_SERVER_ERR(30,"员工排班服务异常"),
    ;


    private Integer code;
    private String value;

    TreatmentOtherError(Integer code, String value) {
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
