package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@ApiModel(value = "产品记录-产品激活记录VO")
@Data
public class CardActiveRecoedVO {

    @ApiModelProperty("激活日期")
    @ExcelProperty("激活日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date activeDate;
    @ApiModelProperty("激活门诊")
    @ExcelProperty("激活门诊")
    private String orgName;
    @ApiModelProperty("患者")
    @ExcelProperty("患者")
    private String patientName;
    @ApiModelProperty("渠道")
    @ExcelProperty("渠道")
    private String saleChannelName;
    @ApiModelProperty("产品名称")
    @ExcelProperty("产品名称")
    private String couponName;
    @ApiModelProperty("产品类型")
    @ExcelProperty("产品类型")
    private String couponType;
    @ApiModelProperty("卡号")
    @ExcelProperty("卡号")
    private String cardNumber;
    @ApiModelProperty("是否使用")
    @ExcelProperty("是否使用")
    private String status;
    @ApiModelProperty("操作人")
    @ExcelProperty("操作人")
    private String activeUserName;
}
