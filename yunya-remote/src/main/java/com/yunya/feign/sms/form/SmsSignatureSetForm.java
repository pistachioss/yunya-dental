package com.yunya.feign.sms.form;

import com.yunya.models.sms.SmsSignatureFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介：短信签名设置修改模型
 *
 * @author: chenlin
 * @Description: 短信签名设置修改模型
 * @Date: 2020/12/11 9:20
 * @since: 1.0.0
 */
@ApiModel("短信签名设置修改模型")
@ToString
@Data
public class SmsSignatureSetForm implements Serializable {
    private static final long serialVersionUID = 246508109517106151L;
    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Integer id;

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
    @ApiModelProperty("签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。4：电商平台店铺名的全称或简称。5：商标名的全称或简称")
    private Byte signSource;

    /**
     * 签名资质证明文件列表
     */
    @ApiModelProperty(value = "签名资质证明文件列表")
    List<SmsSignatureFile> smsSignatureFiles;
}
