package com.yunya.modules.employeeattend.form;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 *
 * extra说明：
 * type: 1: 考勤提醒；
 * type: 两位数： 1* 请假, 2* 加班, 3* 外勤
 * type: 10:请假人发起请假，审批人收到的（待审批）；
 * type: 11:请假人发起请假，抄送人收到的（抄送审批）；
 * type: 12:审批人审核请假，请假人收到的已通过（已通过）；
 * type: 13:审批人审核请假，请假人收到的未通过（未通过）；
 * type: 14:请假人撤销请假，审批人收到的已撤销（已撤销）；
 * id: 申请单id；
 *
 */
@Data
public class EmployeePushData implements Serializable {
    /**
     * type: 1* 请假, 2* 加班, 3* 外勤；
     */
    private int type;
    /**
     * id: 申请单id；
     */
    private int id;
}
