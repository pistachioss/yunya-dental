package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br>充值记录QueryForm
 *
 * @author: WY
 * @date 2020/8/17 10:54
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "充值记录")
public class RechargeRecordQueryForm implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     *  会员卡
     */
    @ApiModelProperty(value = "会员卡号")
    private String cardNumber;

    /**
     * 门诊id
     */
    @ApiModelProperty(value = "门诊id")
    private Integer orgId;
}
