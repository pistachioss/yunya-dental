package com.yunya.employee.common.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Setter
@Getter
@ApiModel("公司端员工列表查询请求参数")
public class BaseEmployeePageReq extends PageReq{
    @ApiModelProperty("员工查询条件，员工姓名或电话")
    private String employeeKeywords;
    @ApiModelProperty(value = "岗位id数组")
    private List<Integer> postIds;
    @ApiModelProperty(value = "就职状态数组(就职状态三种：试用: 0 正式: 1 离职：2')")
    private List<Integer> types;

}
