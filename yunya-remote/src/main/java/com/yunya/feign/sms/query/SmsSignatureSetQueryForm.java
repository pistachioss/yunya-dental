package com.yunya.feign.sms.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：短信签名设置查询参数模型
 *
 * @author: chenlin
 * @Description: 短信签名设置查询参数模型
 * @Date: 2020/12/11 9:35
 * @since: 1.0.0
 */
@ApiModel("短信签名设置查询参数模型")
@ToString
@Data
public class SmsSignatureSetQueryForm implements Serializable {

    @ApiModelProperty("是否分页,默认true")
    private Boolean whetherPage = true;

    @ApiModelProperty("页码，默认第一页")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示条数，默认10条")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 组织id
     */
    @ApiModelProperty("组织id")
    private Integer orgId;

    /**
     * 签名名称
     */
    @ApiModelProperty("签名名称")
    private String signName;

    /**
     * 审批状态：0-审核中，1-审核通过，2-审核失败
     */
    @ApiModelProperty("审批状态：0-审核中，1-审核通过，2-审核失败")
    private Byte signStatus;

    /**
     * 审批状态列表：0-审核中，1-审核通过，2-审核失败
     */
    @ApiModelProperty("审批状态列表：0-审核中，1-审核通过，2-审核失败")
    private Byte[] signStatusList;

    /**
     * 签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。4：电商平台店铺名的全称或简称。5：商标名的全称或简称
     */
    @ApiModelProperty("签名来源。0：企事业单位的全称或简称。1：工信部备案网站的全称或简称。2：APP应用的全称或简称。3：公众号或小程序的全称或简称。4：电商平台店铺名的全称或简称。5：商标名的全称或简称")
    private Byte signSource;
}
