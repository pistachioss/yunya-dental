package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：账单项目VO
 *
 * @author: chenlin
 * @Description: 账单项目VO
 * @Date: 2021/12/7 17:36
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单项目VO")
public class PersonalBillItemVO implements Serializable {
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    @ApiModelProperty("患者ID/员工ID")
    private Integer personId;

    @ApiModelProperty("项目类型（0-价目表；1-商品）")
    private Integer itemType;

    @ApiModelProperty("项目ID")
    private Integer itemId;

    @ApiModelProperty("数量")
    private Integer quantity;
}
