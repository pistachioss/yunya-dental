package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 简介：门店活码访问添加模型
 *
 * @author: chenlin
 * @Description: 门店活码访问添加模型
 * @Date: 2022/6/27 16:23
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门店活码访问添加模型")
public class ClinicLiveCodeVisitModel implements Serializable {

    @ApiModelProperty("微信昵称")
    private String nickName;

    @ApiModelProperty("微信用户id")
    private String openId;

    @ApiModelProperty(value = "访问时间", required = true)
    @NotNull(message = "访问时间不能为空")
    private Date visitTime;

    @ApiModelProperty("经度")
    private String longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("浏览器")
    private String visitDevice;

    @ApiModelProperty("访问时长（秒）")
    @NotNull(message = "访问时长不能为空")
    private Integer visitDuration;

    @ApiModelProperty("意向门诊")
    private Integer intentionOrgId;
}
