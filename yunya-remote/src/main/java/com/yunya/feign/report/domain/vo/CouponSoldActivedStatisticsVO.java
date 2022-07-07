package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：产品售卖激活数据统计VO
 *
 * @author: chenlin
 * @Description: 产品售卖激活数据统计VO
 * @Date: 2022/7/6 9:57
 * @since: 1.0.0
 */
@ApiModel("产品售卖激活数据统计VO")
@Data
@ToString
public class CouponSoldActivedStatisticsVO implements Serializable {

    /** 门诊名称 */
    @Excel(name = "门诊名称")
    @ApiModelProperty("门诊名称")
    private String abbreviation;

    /** 产品类型 */
    @Excel(name = "产品类型")
    @ApiModelProperty("产品类型")
    private String couponType;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String couponName;

    /** 售卖量 */
    @Excel(name = "售卖量")
    @ApiModelProperty("售卖量")
    private Integer soldNum;

    /** 已激活量 */
    @Excel(name = "已激活量")
    @ApiModelProperty("已激活量")
    private Integer activedNum;
}
