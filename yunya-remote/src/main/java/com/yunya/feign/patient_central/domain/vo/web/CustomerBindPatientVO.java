package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2022/11/10 11:01
 * @description: 微信用户绑定患者数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("微信用户绑定患者数据模型")
public class CustomerBindPatientVO implements Serializable {

    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty("手机号")
    private String mobile;

    /** unionId */
    @ApiModelProperty("unionId")
    private String unionId;

    /** 绑定关系 */
    @ApiModelProperty("绑定关系")
    private String bindShipName;

    /** 亲属关系字典id */
    @ApiModelProperty("亲属关系字典id")
    private Integer shipId;
    
    /** 绑定时间 */
    @ApiModelProperty("绑定时间")
    private Date crtTime;
}
