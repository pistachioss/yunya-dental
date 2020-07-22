package com.yunya.employee.expand.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Data
@ApiModel("员工登录组织模型")
public class BaseEmployeeLoginOrgRes {

    @ApiModelProperty(value = "组织名称")
    private String orgName;
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    @ApiModelProperty(value = "岗位名称")
    private String postName;
}
