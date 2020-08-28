package com.yunya.feign.cash_balance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@ApiModel(value = "业务目标分页查询模型")
public class BusinessTargetVo {


    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;
    @ApiModelProperty(value = "目标实收金额",required = true)
    private Integer targetCash;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private Integer completeCash;
    @ApiModelProperty(value = "目标工作量",required = true)
    private Integer tragetNum;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private Integer completeNum;
    @ApiModelProperty(value = "目标初诊人数",required = true)
    private Integer targetFirstVisit;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private Integer completeFirstVisit;
    @ApiModelProperty(value = "目标就诊人次",required = true)
    private Integer targetPatientNum;
    @ApiModelProperty(value = "完成实收金额",required = true)
    private Integer completePatientNum;
    @ApiModelProperty(value = "日期",required = true)
    private Date date;

    //分页查询条件
    @ApiModelProperty(required = true)
    private Integer pageNum;
    @ApiModelProperty(required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;


}
