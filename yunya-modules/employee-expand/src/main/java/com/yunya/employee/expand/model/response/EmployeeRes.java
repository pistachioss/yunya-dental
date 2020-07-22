package com.yunya.employee.expand.model.response;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-05-27 11:18
 */
@Data
public class EmployeeRes {
    private Integer id;

    private Integer employeeId;

    private Integer clinicId;

    private String name;

    /**
     * 手机
     */
    private String mobilePhone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 入职时间
     */
    private Date joinTime;

    /**
     * 离职时间
     */
    private Date leaveTime;

    private Integer assistantEmployeeId;

    private String assistantEmployeeName;

    private Integer clinicDepartmentRoomId;

    private String clinicDepartmentRoomName;

    private Boolean enableAppoint;

    private Boolean enableRegistry;

    private Boolean enableDownload;
}
