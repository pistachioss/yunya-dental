package com.yunya.modules.appointment.code;

import com.yunya.framework.common.constant.PreFixCode;

/**
 * @program: yunya-dental
 * @description: 预约中心异常信息
 * @author: LHB
 * @create: 2020-09-17 11:35
 **/
public enum AppointmentError {
    /**---------------------------------------- 预约中心异常提示 ----------------------------------------*/
    /** 11001-对象转换实体异常 */
    DATA_FROM_MODEL_EXP(1,"对象转换实体异常"),
    /** 11002-开始日期不能大于结束日期 */
    START_DATE_AFTER_END_DATE(2,"开始日期不能大于结束日期"),
    /** 11003-预约助手冲突 */
    APPOINT_ASSISTANT_EXIST(3,"【{0}】助手已在【{1}】门诊{2}时间段预约了【{3}】患者，是否仍要继续创建预约？"),
    /** 11004-分解助手冲突 */
    SPLIT_ASSISTANT_EXIST(4,"【{0}】助手已在【{1}】门诊{2}时间段预约了【{3}】患者，是否仍要继续创建预约？"),
    /** 11010-患者预约失败 */
    APPOINTMENT_FAIL(10,"患者预约失败"),
    /** 11011-预约分解失败 */
    APPOINTMENT_SPLIT_FAIL(11,"预约分解失败"),
    /** 11012-预约操作记录添加失败 */
    OPERATION_RECORD_FAIL(12,"预约操作记录添加失败"),
    /** 11013-预约医生在预约日期当天未排班，建议排班后再新增预约 */
    DENTIST_NOT_WORK(13,"预约医生在预约日期当天未排班，建议排班后再新增预约"),
    /** 11014-预约医生id不能为空 */
    DENTIST_NOT_ID(14,"预约医生id不能为空"),
    /** 11015-患者预约冲突 */
    APPOINT_PATIENT_EXIST(15,"【{0}】患者在【{1}】门诊{2}时间段已存在预约，是否继续添加预约？"),
    /** 11016-医生预约冲突 */
    APPOINT_DENTIST_EXIST(16,"【{0}】医生已在【{1}】门诊{2}时间段预约了{3}患者，是否仍要继续创建预约？"),
    /** 11017-设备预约冲突 */
    APPOINT_DEVICE_EXIST(17,"预约的设备已被{0}患者在{1}时间段预约使用，是否需要继续创建预约？"),
    /** 11018-医生在当前日期下没有排班 */
    DENTIST_NOT_SCHEDULE(18,"医生在当前日期下没有排班"),
    /** 11019-已经存在相同的预约 */
    APPOINT_EXIST(19,"已经存在相同的预约"),
    /** 11020-【预约未到】以外的情况预约不允许编辑 */
    APPOINT_NOT_ALLOW_EDIT_1(20,"【预约未到】以外的情况预约不允许编辑"),
    /** 11021-无效预约，不能进行编辑 */
    APPOINT_INVALID_NOT_ALLOW_EDIT(21,"无效预约，不能进行编辑"),
    /** 11022-编辑预约失败 */
    APPOINT_EDIT_FAIL(22,"编辑预约失败"),
    /** 11023-预约数据不存在 */
    APPOINT_DATA_NOT_EXIST(23,"预约数据不存在"),
    /** 11024-生成修改预约操作记录失败 */
    CREATE_OPERATION_RECORD_FAIL(24,"生成修改预约操作记录失败"),
    /** 11025-取消预约失败 */
    APPOINT_CANCEL_FAIL(25,"取消预约失败"),
    /** 11026-确认预约失败 */
    APPOINT_CONFIRM(26,"确认预约失败"),
    /** 11027-该条预约不允许取消 */
    APPOINT_NOT_ALLOW_CANCEL(27,"该条预约不允许取消"),
    /** 1128-文本长度超过了最大可输入长度,请重新输入 */
    TEXT_MAX_LENGTH_ERROR(28,"文本长度超过了最大可输入长度,请重新输入"),


    /**---------------------------------------- 外部服务异常提示 ----------------------------------------*/
    /** 11030-员工排班服务异常 */
    SCHEDULE_SERVER_ERR(40,"员工排班服务异常"),
    /**  */
    CLINIC_NOT_EXIST_ENABLE_APPOINT_DENTIST(41,"当前门诊没有可预约的医生"),
    /**---------------------------------------- 设置类异常提示 ----------------------------------------*/
    /** 11050-预约设置失败 */
    APPOINT_SETTING_FAIL(50,"预约设置失败"),
    /** 11051-预约单位设置错误 */
    APPOINT_SETTING_UNIT(51,"预约单位设置错误"),
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

    /**
     * 填充参数
     * @param args
     * @return
     */
    public String paddingParams(String ...args) {
        if (args.length > 0) {
            for (int i = 0; i< args.length; i++) {
                value = value.replace("{" +i+ "}",args[i]);
            }
        }
        return value;
    }
}
