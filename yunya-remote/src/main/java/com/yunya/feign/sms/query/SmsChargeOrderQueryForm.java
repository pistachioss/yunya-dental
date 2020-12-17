package com.yunya.feign.sms.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介：短信充值订单查询模型
 *
 * @author: chenlin
 * @Description: 短信充值订单查询模型
 * @Date: 2020/12/12 13:54
 * @since: 1.0.0
 */
@ApiModel("短信充值订单查询模型")
@Data
@ToString
public class SmsChargeOrderQueryForm implements Serializable {
    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 充值订单id
     */
    @ApiModelProperty("充值订单id")
    private Integer id;


    /**
     * 组织id
     */
    @ApiModelProperty("组织id")
    private Integer orgId;

    /**
     * 采商订单号
     */
    @ApiModelProperty("采商订单号")
    private String cbOrderNo;

    /**
     * 订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭
     */
    @ApiModelProperty("订单状态：0-等待付款，1-付款成功，2-付款失败，3-已关闭")
    private Byte orderStatus;
}
