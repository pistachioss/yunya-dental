package com.yunya.modules.emr.constant;

import com.yunya.framework.common.model.*;

public enum EmrError implements RestError {
    OK(200, "ok"),
    NO_PERMISSION_OPERATION(30001, "无权限操作"),
    KEY_IS_LOCKED(30002, "草稿病例已被锁定，无法提交"),
    APPLY_APPROVE_PENDING(30003, "申请正在审批中，请勿重复申请"),
    DATA_IS_EXISTED(30004, "数据已存在"),
    NO_ALLOW_REPEAT_APPLY(30005,"待审核状态不允许重复申请"),
    AUDIT_IS_PASS(30006, "草稿病例审批已通过"),
    MODIFY_APPLY_TIMEOUT(30007, "申请修改病例超时"),
    CHANGE_APPLY_REJECTED(30007, "申请修改病例超时"),
    MEDICAL_IS_EXIST(30008, "就诊记录已生成电子病例"),
    AUDIT_PENDING(30009, "该病历未经过主诊医生审核，不能申请修改！"),
    NOT_NEED_APPLY(30010, "不需要申请修改，可以直接修改此病历！"),
    NO_AUTH_MODIFY_MED(30011, "无权限申请修改此病历，请联系新增病历医生申请修改！"),
    REJECTED_NO_NEED_APPLY(30012, "该病历已审核拒绝且可以修改，无须申请修改"),
    DATA_NOT_EXISTED(30013, "数据不存在"),
    MEDICAL_ALREADY_AUDITED(30014, "病历已被审核"),
    TREATMENT_NOT_EXIST(30015,"就诊记录不存在"),
    NORMAL_MEDICAL_NO_PERMISSION(30016,"普通病例不允许审批"),
    MEDICAL_STATUS_ERROR(30017,"电子病例审核状态异常"),
    ;
    private final Integer code;
    private final String message;

    EmrError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
