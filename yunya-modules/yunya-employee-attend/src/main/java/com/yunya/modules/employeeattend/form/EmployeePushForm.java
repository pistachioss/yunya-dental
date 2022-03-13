package com.yunya.modules.employeeattend.form;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class EmployeePushForm implements Serializable {
    /**
     * 要推送的员工帐号
     */
    private Set<Integer> empId;
    /**
     * 推送设备列表
     */
    private List<String> userList;
    private int platform;
    private String title;
    private String content;
    private String showName;
    private Boolean isSchedule;
    private String scheTime;
    private int id;
}
