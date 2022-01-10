package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/3/17 14:27
 **/
@ApiModel(value = "公司端,,门诊端-产品激活报表参数")
@Data
public class CouponActiveQuery extends PageQuery {
    @ApiModelProperty(value = "产品名称")
    private String couponName;
    @ApiModelProperty(value = "产品Id集合")
    private List<Integer> couponIds;
    @ApiModelProperty(value = "销售渠道")
    private List<Integer> soldChannelIds;
    @ApiModelProperty(value = "激活门诊")
    private List<Integer> activeOrgIds;
    @ApiModelProperty(value = "激活开始时间")
    private String activeStartDate;
    @ApiModelProperty(value = "激活结束时间")
    private String activeEndDate;

}
