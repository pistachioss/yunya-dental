package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：门店店长活码VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/5/18 16:38
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门店店长活码VO")
public class ClinicLiveCodeVO implements Serializable {

    /** 门诊id*/
    private Integer orgId;

    /** 店长活码*/
    @ApiModelProperty("店长活码")
    private String qrcode;

    /** 店长活码完整路径*/
    @ApiModelProperty("店长活码完整路径")
    private String qrFullPath;
}
