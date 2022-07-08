package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：365产品售卖激活数据统计VO
 *
 * @author: chenlin
 * @Description: 365产品售卖激活数据统计VO
 * @Date: 2022/7/6 9:57
 * @since: 1.0.0
 */
@ApiModel("365产品售卖激活数据统计VO")
@Data
@ToString
public class Coupon365SoldActivedStatisticsVO implements Serializable {

    /** 门诊名称 */
    @Excel(name = "门诊名称")
    @ApiModelProperty("门诊名称")
    private String abbreviation;

    /** 售卖产品 */
    @Excel(name = "售卖产品")
    @ApiModelProperty("售卖产品")
    private String productName = "IVY365年卡";

    /** 售卖量 */
    @Excel(name = "售卖量")
    @ApiModelProperty("售卖量")
    private Integer soldNum;

    /** 已激活量 */
    @Excel(name = "已激活量")
    @ApiModelProperty("已激活量")
    private Integer activedNum;

    /** 售卖产品类型id */
    @ApiModelProperty("售卖产品类型id")
    private Integer productTypeId = 14;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;
}
