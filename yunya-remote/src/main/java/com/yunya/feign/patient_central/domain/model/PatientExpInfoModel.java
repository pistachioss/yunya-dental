package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>患者其他信息
 *
 * @author: WY
 * @date 2020/8/26 12:56
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientExpInfoModel implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Integer id;

    /**
     * 患者ID 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 患者类型 患者类型对应字典ID
     */
    @ApiModelProperty(value = "患者类型 患者类型对应字典ID")
    private Integer patientKind;

    /**
     * 常用电话 常用电话
     */
    @ApiModelProperty(value = "常用电话 常用电话")
    private String usefulPhone;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String identity;

    /**
     * 职业字典明细ID 职业对应字典ID
     */
    @ApiModelProperty(value = "职业字典明细ID 职业对应字典ID")
    private Integer profession;

    /**
     * 遗传病史
     */
    @ApiModelProperty(value = "遗传病史")
    private String heredity;

    /**
     * 其他健康情况
     */
    @ApiModelProperty(value = "其他健康情况")
    private String otherHealth;

    /**
     * 每日刷牙次数
     */
    @ApiModelProperty(value = "每日刷牙次数")
    private Integer brushTimes;

    /**
     * 每次刷牙时长
     */
    @ApiModelProperty(value = "每次刷牙时长")
    private Byte brushTime;

    /**
     * 刷毛硬度 0-软；1-中；2-硬
     */
    @ApiModelProperty(value = "刷毛硬度 0-软；1-中；2-硬")
    private Byte brushHardness;

    /**
     * 烟龄
     */
    @ApiModelProperty(value = "烟龄")
    private Byte smokingAge;

    /**
     * 每日吸烟数量
     */
    @ApiModelProperty(value = "每日吸烟数量")
    private Integer smokingNum;

    /**
     * 是否使用电动牙刷
     */
    @ApiModelProperty(value = "是否使用电动牙刷")
    private Boolean useElectricBrush;

    /**
     * 是否使用漱口水
     */
    @ApiModelProperty(value = "是否使用漱口水")
    private Boolean useCollutory;

    /**
     * 是否适用牙线
     */
    @ApiModelProperty(value = "是否适用牙线")
    private Boolean useFloss;

    /**
     * 是否夜磨牙
     */
    @ApiModelProperty(value = "是否夜磨牙")
    private Boolean bruxism;

    /**
     * 家庭详细地址
     */
    @ApiModelProperty(value = "家庭详细地址")
    private String address;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private String province;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private String city;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private String country;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice;


}
