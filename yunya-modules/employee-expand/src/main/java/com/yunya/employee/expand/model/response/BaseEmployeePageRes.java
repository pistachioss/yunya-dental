package com.yunya.employee.expand.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Getter
@Setter
@ApiModel("公司端员工列表返回对象")
public class BaseEmployeePageRes {

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("员工Id")
    private Integer employeeId;

    @ApiModelProperty("员工姓名")
    private String employeeName;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("手机号码")
    private String mobilePhone;

    /**
     * 员工岗位
     */
    @ApiModelProperty("员工岗位")
    private String employeePost;

    /**
     * 入职时间
     */
    @ApiModelProperty("入职时间")
    private LocalDate joinTime;


    /**
     * 离职时间
     */
    @ApiModelProperty("离职时间")
    private LocalDate leaveTime;

    @ApiModelProperty("就职状态")
    private String typeName;
}
