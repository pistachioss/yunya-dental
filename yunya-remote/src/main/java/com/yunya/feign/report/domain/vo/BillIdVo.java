package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/31 10:47
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel(value = "订单集合信息模板")
public class BillIdVo {

    /**
     * 订单id
     */
    private Integer billId;

    /**
     * 推荐人id
     */
    private Integer originId;
}