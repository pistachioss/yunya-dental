package com.yunya.feign.ivy_mini.domain.form;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/17
 * @description:
 */
@Data
@ApiModel(value = "订单管理")
public class OrderForm extends PageQuery implements Serializable {
    @ApiModelProperty(value = "后端使用")
    private List<Integer> nameList;
    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款；6-退款成功
     */
    @ApiModelProperty(value = "0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款；6-退款成功；7-待使用")
    private Byte status;

    @ApiModelProperty(value = "核销状态 后台使用")
    private Byte activeStatus;

    /**
     * 订单类型：0->正常订单；1->秒杀订单；2-拼团订单
     */
    @ApiModelProperty(value = "订单类型：0->正常订单；1->秒杀订单；2-拼团订单")
    private Byte orderType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String crtTime;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private String startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "收货人信息")
    private String name;
}
