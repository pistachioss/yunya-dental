package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2021/12/30
 * @description:
 */
@Data
public class CardDetailForm  extends PageQuery {
    @ApiModelProperty(value = "患者关键字")
    private String patientKeyword;
    @ApiModelProperty(value = "激活门诊")
    private List<Integer> activeOrgIds;
    @ApiModelProperty(value = "激活开始日期")
    private LocalDate activeStartDate;
    @ApiModelProperty(value = "激活结束日期")
    private LocalDate activeEndDate;
    @ApiModelProperty(value = "产品Id集合")
    private List<Integer> couponIds;
}
