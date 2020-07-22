package com.yunya.employee.expand.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author bruce
 * @date 2020/7/11
 */
@Setter
@Getter
@ApiModel("门诊端员工列表查询请求参数")
public class ClinicEmployeePageReq extends PageReq{
    @ApiModelProperty("员工查询条件，员工姓名或电话")
    private String employeeKeywords;
    @ApiModelProperty(value = "岗位id数组")
    private List<Integer> postIds;
}
