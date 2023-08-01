package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2023/07/31
 */
@Data
@ApiModel(value = "划扣赠与记录模型")
public class DeductionChangeRecordVO implements Serializable {
    @ApiModelProperty("产品名称")
    private String couponName;
    @ApiModelProperty("售卖单价")
    private BigDecimal saleAmount;
    @ApiModelProperty("购买患者")
    private String patientName;
    @ApiModelProperty("现持有患者")
    private String ownName;
    @ApiModelProperty("类型")
    private String type = "赠予";
    @ApiModelProperty("时间")
    private String date;
    @ApiModelProperty(value = "操作人")
    private String executorName;
    @ApiModelProperty(value = "备注")
    private String remark;
}
