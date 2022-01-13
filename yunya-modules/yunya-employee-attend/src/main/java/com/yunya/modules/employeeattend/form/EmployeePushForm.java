package com.yunya.modules.employeeattend.form;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class EmployeePushForm implements Serializable {
    private Set<Integer> empId;
    private List<String> userList;
    private int platform;
    private String title;
    private String content;
    private String showName;
    private Object pushData;
    private Boolean isSchedule;
    private String scheTime;
}
