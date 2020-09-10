package com.yunya.feign.cash_balance.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@ApiModel(value = "导出业务目标")
public class BusinessTargetExportVo {
    @Excel(name ="目标实收金额")
    private BigDecimal targetCash;
    @Excel(name ="完成实收金额")
    private BigDecimal completeCash;
    @Excel(name ="实收金额百分比")
    private String percentCash;
    @Excel(name ="目标工作量")
    private BigDecimal targetNum;
    @Excel(name ="完成实收金额")
    private BigDecimal completeNum;
    @Excel(name ="百分比实收金额")
    private String percentNum;
    @Excel(name ="目标初诊人数")
    private Integer targetFirstVisit;
    @Excel(name ="完成实收金额")
    private Integer completeFirstVisit;
    @Excel(name ="完成实收金额百分比")
    private String percentVisit;
    @Excel(name ="目标就诊人次")
    private Integer targetPatientNum;
    @Excel(name ="完成实收金额")
    private Integer completePatientNum;
    @Excel(name ="完成实收金额百分比")
    private String percentPatientNum;
    @Excel(name ="日期")
    private Date date;

}
