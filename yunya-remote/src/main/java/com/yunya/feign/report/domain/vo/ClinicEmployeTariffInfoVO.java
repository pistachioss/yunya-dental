package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：员工价目信息VO
 *
 * @author: chenlin
 * @Description: 员工价目信息VO
 * @Date: 2021/10/27 9:58
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工价目信息VO")
public class ClinicEmployeTariffInfoVO extends ClinicEmployeeReportVO implements Serializable {
    
    /** 项目ID */
    @ApiModelProperty("项目ID")
    private Integer itemId;

    /** 项目类型（0-价目表；1-商品） */
    @ApiModelProperty("项目类型（0-价目表；1-商品）")
    private Byte itemType;
}
