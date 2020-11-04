package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 充值记录queryForm
 *
 * @author: WY
 * @date 2020/8/22 15:07
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("查询预付款充值记录参数模型")
public class PrepaidRechargeRecordQueryForm implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     *  预付款卡号
     */
    @ApiModelProperty(value = "预付款卡号",required = true)
    private String prepaidId;

    /**
     *  诊所id
     */
    @ApiModelProperty(value = "诊所id",required = false)
    private Integer orgId;

}
