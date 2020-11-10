package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 返回患者扩展资料信息模型
 *
 * @author: WY
 * @date 2020/8/31 11:30
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
@ApiModel("返回患者扩展资料信息模型")
public class PatientExpInfoVo implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 患者ID 患者ID
     */
    private Integer patientId;

    /**
     * 患者类型 患者类型对应字典ID
     */
    private Integer patientKind;

    /**
     * 患者类型 患者类型对应字典ID名称
     */
    private String patientKindName;

    /**
     * 常用电话 常用电话
     */
    private String usefulPhone;

    /**
     * 身份证号
     */
    private String identity;

    /**
     * 职业字典明细ID 职业对应字典ID
     */
    private Integer profession;

    /**
     * 遗传病史
     */
    private String heredity;

    /**
     * 其他健康情况
     */
    private String otherHealth;

    /**
     * 每日刷牙次数
     */
    private Integer brushTimes;

    /**
     * 每次刷牙时长
     */
    private Byte brushTime;

    /**
     * 刷毛硬度 0-软；1-中；2-硬
     */
    private Byte brushHardness;

    /**
     * 烟龄
     */
    private Byte smokingAge;

    /**
     * 每日吸烟数量
     */
    private Integer smokingNum;

    /**
     * 是否使用电动牙刷
     */
    private Boolean useElectricBrush;

    /**
     * 是否使用漱口水
     */
    private Boolean useCollutory;

    /**
     * 是否适用牙线
     */
    private Boolean useFloss;

    /**
     * 是否夜磨牙
     */
    private Boolean bruxism;

    /**
     * 家庭详细地址
     */
    private String address;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String country;

    /**
     * 备注 备注
     */
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    private Integer crtId;

    /**
     * 创建人姓名
     */
    private String crtName;

    /**
     * 创建时间
     */
    private Date crtTime;

    /**
     * 更新人ID
     */
    private Integer uptId;

    /**
     * 更新人姓名
     */
    private String updName;

    /**
     * 更新时间
     */
    private Date updTime;


}
