package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：365产品激活明细VO
 *
 * @author: chenlin
 * @Description: 365产品激活明细VO
 * @Date: 2022/7/6 9:57
 * @since: 1.0.0
 */
@ApiModel("365产品激活明细VO")
@Data
@ToString
public class Coupon365ActivedDetailVO extends Coupon365DetailVO implements Serializable {

    /** 渠道 */
    @Excel(name = "渠道", sort = 1)
    @ApiModelProperty("渠道")
    private String saleChannelName;

    /** 门诊名称 */
    @Excel(name = "门诊名称", sort = 2)
    @ApiModelProperty("门诊名称")
    private String abbreviation;

    /** 产品名称 */
    @Excel(name = "产品名称", sort = 3)
    @ApiModelProperty("产品名称")
    private String couponName;

    /** 患者名称 */
    @Excel(name = "患者名称", sort = 4)
    @ApiModelProperty("患者名称")
    private String patientName;

    /** 初复诊 */
    @Excel(name = "初复诊", sort = 5)
    @ApiModelProperty("初复诊")
    private String firstVisit;

    /** 联系方式 */
    @Excel(name = "联系方式", sort = 6)
    @ApiModelProperty("联系方式")
    private String mobile;

    /** 累计激活次数 */
    @Excel(name = "激活次数", sort = 7)
    @ApiModelProperty("累计激活次数")
    private Integer accumulativeNum;

    /** 卡激活时间 */
    @Excel(name = "卡激活时间", sort = 9)
    @ApiModelProperty("卡激活时间")
    private String activeDate;

    /** 卡截止时间 */
    @Excel(name = "卡截止时间", sort = 10)
    @ApiModelProperty("卡截止时间")
    private String activationDeadline;
}
