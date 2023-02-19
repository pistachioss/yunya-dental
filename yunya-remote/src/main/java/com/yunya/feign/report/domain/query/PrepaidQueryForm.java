package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 会员卡Occur日志QueryForm
 *
 * @author: WY
 * @date: 2020/10/24 13:49
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class PrepaidQueryForm implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "门诊id", required = false)
    /** 门诊id */
    private Integer orgId;

    @ApiModelProperty(value = "患者条件", required = false)
    /** 患者条件 */
    private String combination;

    @ApiModelProperty(value = "卡类型(１：预付款（普通），2-正畸预付款，3-美白预付款)", required = true)
    /** 卡类型(１：预付款（普通），2-正畸预付款，3-美白预付款) */
    private Integer type;

    @ApiModelProperty(value = "预付款账号", required = false)
    /** 预付款账号 */
    private String cardNumber;

    @ApiModelProperty(value = "充值开始日期", required = false)
    /** 充值开始日期 */
    private String startDate;

    @ApiModelProperty(value = "充值结束日期", required = false)
    /** 充值结束日期 */
    private String endDate;

    @ApiModelProperty(value = "充值方式", required = false)
    /** 充值方式 */
    private List<Integer> rechargeMethods;

}