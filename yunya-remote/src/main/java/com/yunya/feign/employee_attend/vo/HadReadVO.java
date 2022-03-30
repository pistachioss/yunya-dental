package com.yunya.feign.employee_attend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：是否已读VO
 *
 * @author: chenlin
 * @Description: 是否已读VO
 * @Date: 2022/3/24 15:48
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("是否已读VO")
public class HadReadVO implements Serializable {

    /** 是否已读*/
    @ApiModelProperty("是否已读")
    private Boolean hadRead;
}
