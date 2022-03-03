package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
     * 患者ID
     */
    @ApiModelProperty("患者ID")
    private Integer patientId;

    /**
     * 患者类型 患者类型对应字典ID
     */
    @ApiModelProperty("患者类型")
    private Integer patientKind;

    /**
     * 患者类型 患者类型对应字典ID名称
     */
    @ApiModelProperty("患者类型")
    private String patientKindName;

    /**
     * 常用电话
     */
    @ApiModelProperty("常用电话")
    private String usefulPhone;

    /**
     * 身份证号
     */
    @ApiModelProperty("身份证号")
    private String identity;

    /**
     * 职业字典明细ID 职业对应字典ID
     */
    @ApiModelProperty("职业字典明细ID")
    private Integer profession;

    /**
     * 遗传病史
     */
    @ApiModelProperty("家族遗传病史")
    private String heredity;

    /**
     * 其他健康状况
     */
    @ApiModelProperty("其他健康状况")
    private String otherHealth;

    /**
     * 每日刷牙次数
     */
    @ApiModelProperty("每日刷牙次数")
    private Integer brushTimes;

    /**
     * 每次刷牙时长
     */
    @ApiModelProperty("每次刷牙时长")
    private Byte brushTime;

    /**
     * 刷毛硬度 0-软；1-中；2-硬
     */
    @ApiModelProperty("刷毛硬度 0-软；1-中；2-硬")
    private Byte brushHardness;

    /**
     * 烟龄
     */
    @ApiModelProperty("烟龄")
    private Byte smokingAge;

    /**
     * 每日吸烟数量
     */
    @ApiModelProperty("每日吸烟数量")
    private Integer smokingNum;

    /**
     * 是否使用电动牙刷
     */
    @ApiModelProperty("是否使用电动牙刷")
    private Boolean useElectricBrush;

    /**
     * 是否使用漱口水
     */
    @ApiModelProperty("是否使用漱口水")
    private Boolean useCollutory;

    /**
     * 是否使用牙线
     */
    @ApiModelProperty("是否使用牙线")
    private Boolean useFloss;

    /**
     * 是否夜磨牙
     */
    @ApiModelProperty("是否夜磨牙")
    private Boolean bruxism;

    /**
     * 家庭详细地址
     */
    @ApiModelProperty("家庭详细地址")
    private String address;

    /**
     * 省
     */
    @ApiModelProperty("省")
    private String province;

    /**
     * 市
     */
    @ApiModelProperty("市")
    private String city;

    /**
     * 区
     */
    @ApiModelProperty("区")
    private String country;

    /**
     * 紧急联系人（手机）
     */
    @ApiModelProperty("紧急联系人")
    private String emergencyPhone;

    /**
     * 电子邮箱
     */
    @ApiModelProperty(name = "E_mail")
    private String eMail;

    /**
     * 单位
     */
    @ApiModelProperty("单位")
    private String employer;

    /**
     * 国籍代码
     */
    @ApiModelProperty("国籍代码")
    private String state;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
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

    /** 妊娠月数*/
    @ApiModelProperty("妊娠月数")
    private Integer pregnancyMonth;

    /** 是否哺乳 */
    @ApiModelProperty("是否哺乳")
    private Boolean feedBaby;

    /** 牙齿信息记录id*/
    @ApiModelProperty("牙齿信息记录id")
    private Integer toothRecordId;

    /** 缺牙史 */
    @ApiModelProperty("是否缺牙史")
    private Boolean hadMissTooth;

    /** 缺牙原因字典id列表*/
    @ApiModelProperty("缺牙原因字典id列表）")
    private List<Integer> missToothHistory;

    /** 填充治疗史 */
    @ApiModelProperty("是否填充治疗史")
    private Boolean hadFillTreat;

    /** 填充治疗（材料id列表）*/
    @ApiModelProperty("填充治疗（材料字典id列表）")
    private List<Integer> fillTreatHistory;

    /** 最近一次填充治疗日期 */
    @ApiModelProperty("最近一次填充治疗日期")
    private Date fillTreatLastDate;

    /** 是否牙周手术 */
    @ApiModelProperty("是否牙周手术")
    private Boolean hadPeriodontalSurgery;

    /** 是否牙周调整 */
    @ApiModelProperty("是否牙周调整")
    private Boolean hadOcclusalAdjust;

    /** 是否修复义齿：0-否，1-是 */
    @ApiModelProperty("是否修复义齿")
    private Boolean hadRestorativeDentures;

    /** RPD部位 */
    @ApiModelProperty("RPD部位")
    private String rpdPart;

    /** RPD戴用时间 */
    @ApiModelProperty("RPD戴用时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date rpdDate;

    /** LPD部位 */
    @ApiModelProperty("LPD部位")
    private String lpdPart;

    /** LPD戴用时间 */
    @ApiModelProperty("LPD戴用时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date lpdDate;

    /** 是否预防治疗 */
    @ApiModelProperty("是否预防治疗")
    private Boolean hadPreventiveTreat;

    /** 预防治疗周期 */
    @ApiModelProperty("预防治疗周期（月）")
    private Short preventiveTreatCycle;

    /** 上次预防治疗距今 */
    @ApiModelProperty("上次预防治疗距今")
    private Short preventiveTreatLastMonth;

    /** 是否有使用困难或不舒适的经历*/
    @ApiModelProperty("是否有使用困难或不舒适的经历")
    private Boolean hadDiffcultTreat;

    /** 缺牙但不修复的原因*/
    @ApiModelProperty("缺牙但不修复的原因")
    private String missTeethUnrepeatCause;

    /** 是否正畸治疗*/
    @ApiModelProperty("是否正畸治疗")
    private Boolean hadOrthodontic;

    /** 正畸治疗开始日期*/
    @ApiModelProperty("正畸治疗开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date orthodonticStartDate;

    /** 正畸治疗结束日期*/
    @ApiModelProperty("正畸治疗结束日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date orthodonticEndDate;

    /** 是否接受过口腔卫生宣教*/
    @ApiModelProperty("是否接受过口腔卫生宣教")
    private Boolean hadHygieneEducation;

    /** 是否用过菌斑染色体*/
    @ApiModelProperty("是否用过菌斑染色体")
    private Boolean usedPlaqueDna;

    /** 治疗意见*/
    @ApiModelProperty("治疗意见")
    private String treatAdvice;
}
