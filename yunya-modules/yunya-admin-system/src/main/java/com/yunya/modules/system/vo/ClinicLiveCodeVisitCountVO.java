package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
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

    private Integer todayClickCount;

    private Integer todayCustomerCount;

    private Integer clickCount;

    private Integer CustomerCount;
}
