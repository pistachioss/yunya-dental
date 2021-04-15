package com.yunya.feign.report.domain.vo;

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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date activeDate;
    @ApiModelProperty("激活门诊Id")
    private Integer orgId;
    @ApiModelProperty("激活门诊名称")
    private String orgName;
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("患者名称")
    private String patientName;
    @ApiModelProperty("渠道ID")
    private Integer saleChannelId;
    @ApiModelProperty("渠道名称")
    private String saleChannelName;
    @ApiModelProperty("卡券名称")
    private String couponName;
    @ApiModelProperty("卡券类型")
    private String couponType;
    @ApiModelProperty("卡号")
    private String cardNumber;
    @ApiModelProperty("是否使用 0:否 1:是")
    private Integer status;
    @ApiModelProperty("激活人ID")
    private Integer activeUserId;
    @ApiModelProperty("激活人姓名")
    private String activeUserName;
}
