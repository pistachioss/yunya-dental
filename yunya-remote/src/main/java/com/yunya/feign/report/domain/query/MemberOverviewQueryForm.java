package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/10/27 13:48
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class MemberOverviewQueryForm implements Serializable {

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
    private List<Integer> orgIds;

    @ApiModelProperty(value = "患者条件", required = false)
    /** 患者条件 */
    private String combination;

    @ApiModelProperty(value = "充值开始日期", required = false)
    /** 充值开始日期 */
    private Date startDate;

    @ApiModelProperty(value = "充值结束日期", required = false)
    /** 充值结束日期 */
    private Date endDate;

    @ApiModelProperty(value = "会员类型（0：会员卡,1:预付款）", required = false)
    /** 会员卡类型 */
    private Integer type;

}