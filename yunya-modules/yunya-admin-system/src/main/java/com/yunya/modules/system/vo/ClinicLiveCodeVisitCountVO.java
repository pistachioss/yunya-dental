package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：门店活码访问数量VO
 *
 * @author: chenlin
 * @Description: 门店活码访问数量VO
 * @Date: 2022/6/28 13:29
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门店活码访问数量VO")
public class ClinicLiveCodeVisitCountVO implements Serializable {

    /** 今日点击量 */
    @ApiModelProperty("今日点击量")
    private Integer todayClickCount;

    /** 今日访客数 */
    @ApiModelProperty("今日访客数")
    private Integer todayCustomerCount;

    /** 总点击量 */
    @ApiModelProperty("总点击量")
    private Integer clickCount;

    /** 总访客数 */
    @ApiModelProperty("总访客数")
    private Integer CustomerCount;
}
