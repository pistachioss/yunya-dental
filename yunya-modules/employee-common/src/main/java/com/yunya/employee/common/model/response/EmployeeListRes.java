package com.yunya.employee.common.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 描述:
 *
 * @author bruce
 * @create 2020-07-10
 */
@Data
public class EmployeeListRes {
    private Integer id;

    private Integer employeeId;

    private String employeeName;

    /**
     * 性别
     */
    private String sex;

    /**
     * 手机
     */
    private String mobilePhone;

    /**
     * 员工岗位
     */
    private String employeePost;

    /**
     * 入职时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date joinTime;


    /**
     * 离职时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveTime;

}
