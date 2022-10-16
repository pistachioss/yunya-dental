package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简单介绍:</br> 组织详情VO
 *
 * @author: chow
 * @date: 2020/6/4 17:03
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("全诊通认证门诊")
public class QztOrgVO {
    @ApiModelProperty("组织ID")
    private Integer id;
    @ApiModelProperty("组织全名")
    private String name;
    @ApiModelProperty("组织统一信用代码")
    private String qztInstitutionCode;
}
