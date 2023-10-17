package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: chenlin
 * @date: 2023/10/16 17:58
 * @description: 员工扩展信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("员工扩展信息数据模型")
public class SysEmployeeExtVO implements Serializable {
    
    /** 员工id */
    @ApiModelProperty("员工id")
    private Integer userId;

    /** 员工姓名 */
    @ApiModelProperty("员工姓名")
    private String employeeName;
    
    /** 授权折扣最高折扣 */
    @ApiModelProperty("授权折扣最高折扣")
    private BigDecimal discountRate;
    
    /** 授权折扣年度额度 */
    @ApiModelProperty("授权折扣年度额度")
    private BigDecimal discountAmount;

    /** 年度折扣金额累计 */
    @ApiModelProperty("年度折扣金额累计")
    private BigDecimal usedDiscountAmount;
}
