package com.yunya.feign.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：短信签名设置响应模式
 *
 * @author: chenlin
 * @Description: 短信签名设置响应模式
 * @Date: 2020/12/11 9:15
 * @since: 1.0.0
 */
@ApiModel("短信签名设置响应模式")
@ToString
@Data
public class SmsSignatureSetVO implements Serializable {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Integer id;

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty(value = "组织id（门诊、公司）")
    private Integer orgId;

    /**
     * 签名名称
     */
    @ApiModelProperty(value = "签名名称")
    private String signName;

    /**
     * 签名申请说明
     */
    @ApiModelProperty(value = "签名申请说明")
    private String remark;

    /**
     * 签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。4：电商平台店铺名的全称或简称。5：商标名的全称或简称
     */
    @ApiModelProperty("签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。4：电商平台店铺名的全称或简称。5：商标名的全称或简称")
    private Byte signSource;

    /**
     * 签名审核状态：0：审核中。1：审核通过。2：审核失败
     */
    @ApiModelProperty("签名审核状态：0：审核中。1：审核通过。2：审核失败")
    private Byte signStatus;

    /**
     * 提交人
     */
    @ApiModelProperty("提交人")
    private String crtUser;
}
