package com.yunya.feign.sms.model;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：短信签名添加模型
 *
 * @author: chenlin
 * @Description: 短信签名添加模型
 * @Date: 2020/12/10 20:50
 * @since: 1.0.0
 */
@ApiOperation("短信签名添加模型")
@ToString
@Data
public class SmsSignatureSetModel implements Serializable {

    /**
     * 组织id（门诊、公司）
     */
    @ApiModelProperty(value = "组织id（门诊、公司）", required = true)
    @NotNull
    private Integer orgId;

    /**
     * 签名名称
     */
    @ApiModelProperty(value = "签名名称", required = true)
    @NotBlank
    private String signName;

    /**
     * 签名申请说明
     */
    @ApiModelProperty(value = "签名申请说明",required = true)
    @NotBlank
    private String remark;

    /**
     * 签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。4：电商平台店铺名的全称或简称。5：商标名的全称或简称
     */
    @ApiModelProperty(value = "签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。4：电商平台店铺名的全称或简称。5：商标名的全称或简称", required = true)
    private Byte signSource;
}
