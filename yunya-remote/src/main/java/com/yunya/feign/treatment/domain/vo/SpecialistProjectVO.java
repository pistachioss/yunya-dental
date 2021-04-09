package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 专科项目分析VO
 * @author: LHB
 * @create: 2021-04-07 09:50
 **/
@Data
public class SpecialistProjectVO implements Serializable {
    @ApiModelProperty("数量")
    private Integer quantity;
    @ApiModelProperty("门诊ID")
    private Integer orgId;
    @ApiModelProperty("创建日期")
    private Date crtTime;
    @ApiModelProperty("项目ID")
    private Integer billingItemId;
}
