package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：365产品售卖明细VO
 *
 * @author: chenlin
 * @Description: 365产品售卖明细VO
 * @Date: 2022/7/6 9:57
 * @since: 1.0.0
 */
@ApiModel("365产品售卖明细VO")
@Data
@ToString
public class Coupon365SoldDetailVO extends Coupon365DetailVO implements Serializable {

    /** 门诊名称 */
    @Excel(name = "门诊名称", sort = 1)
    @ApiModelProperty("门诊名称")
    private String abbreviation;

    /** 售卖类型*/
    @Excel(name = "售卖类型", sort = 2)
    @ApiModelProperty("售卖类型")
    private String soldType;

    /** 售出产品 */
    @Excel(name = "售出产品", sort = 3)
    @ApiModelProperty("售出产品")
    private String couponName;

    /** 售出对象 */
    @Excel(name = "售出对象", sort = 4)
    @ApiModelProperty("售出对象")
    private String soldTarget;

    /** 售出时间 */
    @Excel(name = "售出时间", sort = 5)
    @ApiModelProperty("售出时间")
    private String soldDate;

    /** 初复诊 */
    @Excel(name = "初复诊", sort = 6)
    @ApiModelProperty("初复诊")
    private String firstVisit;

    /** 联系方式 */
    @Excel(name = "联系方式", sort = 7)
    @ApiModelProperty("联系方式")
    private String mobile;
}
