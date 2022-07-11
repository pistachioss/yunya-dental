package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：365卡券基础信息
 *
 * @author: chenlin
 * @Description: 365卡券基础信息
 * @Date: 2022/7/8 11:25
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("365卡券基础信息")
public class Coupon365DetailVO implements Serializable {
    /** 卡id */
    @ApiModelProperty("卡id")
    private Integer cardId;

    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 卡券id */
    @ApiModelProperty("卡券id")
    private Integer couponId;

    /** 累计购买次数 */
    @Excel(name = "累计购买次数", sort = 8)
    @ApiModelProperty("累计购买次数")
    private Integer accumulativeNum;
}
