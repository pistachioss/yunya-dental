package com.yunya.employee.common.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bruce
 * @date 2020/7/15
 */
@Setter
@Getter
@ApiModel("员工详情对象")
public class BaseEmployeeDetailRes {

    @ApiModelProperty("员工基本信息")
    private BaseEmployeeBasicRes basicInfo;
}
