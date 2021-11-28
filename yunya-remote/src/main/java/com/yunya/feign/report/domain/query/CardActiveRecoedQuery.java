package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
@ApiModel(value = "产品激活记录参数模型")
public class CardActiveRecoedQuery extends PageQuery {

    @ApiModelProperty("激活门诊ID集合")
    @NotNull(message = "请选择激活门诊")
    private List<Integer> activeOrgIds;
    @ApiModelProperty(value = "激活日期开始时间")
    @NotNull(message = "请选择激活日期范围")
    private LocalDate rechargeStartDate;
    @ApiModelProperty(value = "激活日期结束时间")
    @NotNull(message = "请选择激活日期范围")
    private LocalDate rechargeEndDate;
    @ApiModelProperty("产品ID")
    private List<Integer> couponIds;
    @ApiModelProperty("渠道ID")
    private List<Integer> saleChannelIds;

}
