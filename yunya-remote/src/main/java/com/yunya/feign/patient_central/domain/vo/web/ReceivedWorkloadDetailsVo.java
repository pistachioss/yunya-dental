package com.yunya.feign.patient_central.domain.vo.web;

import com.alibaba.excel.annotation.ExcelProperty;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/3/25 13:59
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("已收工作量明细返回模板")
public class ReceivedWorkloadDetailsVo {

    /**
     * 订单id
     */
    @ApiModelProperty("订单id")
    private Integer billId;

    /**
     * 患者id
     */
    @ApiModelProperty("患者id")
    private String patientId;

    /**
     * 关联时间
     */
    @Excel(name = "关联时间")
    @ApiModelProperty("关联时间")
    private String relatedTime;

    /**
     * 患者姓名
     */
    @Excel(name = "患者")
    @ApiModelProperty("患者姓名")
    private String name;

    /**
     * 手机号码
     */
    @Excel(name = "手机号")
    @ApiModelProperty("手机号码")
    private String mobile;

    /**
     *  初诊日期
     */
    @Excel(name = "初诊日期")
    @ApiModelProperty("初诊日期")
    private String firstVisitDate;

    /**
     * 末诊日期
     */
    @Excel(name = "末诊日期")
    @ApiModelProperty("末诊日期")
    private String lastVisitDate;

    /**
     * 账单日期
     */
    @Excel(name = "账单日期")
    @ApiModelProperty("账单日期")
    private String orderDate;

    /**
     * 就诊门诊
     */
    @Excel(name = "就诊门诊")
    @ApiModelProperty("就诊门诊")
    private String abbreviation;

    /**
     * 挂号医生
     */
    @Excel(name = "挂号医生")
    @ApiModelProperty("挂号医生")
    private String employeeName;

    /**
     * 项目名称
     */
    @Excel(name = "项目名称")
    @ApiModelProperty("项目名称")
    private String itemName;

    /**
     * 已收工作量
     */
    @Excel(name = "金额")
    @ApiModelProperty("金额")
    private BigDecimal workload;

    /**
     * 是否改变更过
     */
    @ApiModelProperty("是否改变更过")
    private Boolean isChange;
}