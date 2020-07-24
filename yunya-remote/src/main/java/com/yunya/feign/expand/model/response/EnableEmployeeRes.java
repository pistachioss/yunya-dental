package com.yunya.feign.expand.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author bruce
 * @date 2020/7/23
 */
@Setter
@Getter
@ApiModel("可预约，挂号员工对象")
public class EnableEmployeeRes {
    /**
     * 可预约医生
     */
    @ApiModelProperty("可预约员工")
    private List<EnableChooseEmployeeRes> enableAppointList;
    /**
     * 可挂号医生
     */
    @ApiModelProperty("可挂号员工")
    private List<EnableChooseEmployeeRes> enableRegistryList;
}
