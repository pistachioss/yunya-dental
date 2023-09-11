package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "划扣卡退费查询")
@Data
public class DeductionRefundQuery extends PageQuery {
    @ApiModelProperty(value = "退费开始时间")
    private String startDate;
    @ApiModelProperty(value = "退费结束时间")
    private String endDate;
    @ApiModelProperty(value = "产品分类Ids")
    private List<Integer> couponTypeIds;
    @ApiModelProperty(value = "产品名称")
    private String couponName;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "门诊")
    private List<Integer> orgIds;
}
