package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/2/28 13:06
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("成人患者登记添加模型")
public class AdultPatientRegistrationModel extends PatientRegistrationModel implements Serializable {

    /** 手机号码 长度14 */
    @ApiModelProperty(value = "手机号",required = true)
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /** 手机号所属人 手机号所属人字典ID */
    @ApiModelProperty(value = "手机号所属人字典ID",required = true)
    @NotNull(message = "手机号所属人不能为空")
    private Integer mobileOwner;

    /** 单位 */
    @ApiModelProperty("单位")
    @Size(max = 50, message = "单位长度不能超过50个字符！")
    private String employer;

    /** 患者来源类型 患者来源分类ID */
    @ApiModelProperty(value = "患者来源分类ID", required = true)
    @NotNull(message = "渠道来源不能为空")
    private Integer originType;

    /** 患者来源关联ID （二级患者来源ID）*/
    @ApiModelProperty(value = "渠道来源关联ID（二级患者来源ID）")
    private Integer originId;

    /** 渠道来源-推荐员工id*/
    @ApiModelProperty(value = "渠道来源-推荐员工id")
    private Integer employeeId;

    /** 渠道来源-介绍患者id*/
    @ApiModelProperty(value = "渠道来源-介绍患者id")
    private Integer patientId;

    /** 家族遗传史 */
    @ApiModelProperty("家族遗传史")
    @Size(max = 50, message = "家族遗传史长度不能超过50个字符！")
    private String heredity;

    /** 妊娠月数*/
    @ApiModelProperty("妊娠月数")
    private Integer pregnancyMonth;

    /** 是否哺乳 */
    @ApiModelProperty("是否哺乳")
    private Boolean feedBaby;

    /** 您认为还应该告诉医生的全身健康状况*/
    @ApiModelProperty("您认为还应该告诉医生的全身健康状况")
    @Size(max = 50, message = "全身健康状况长度不能超过50个字符！")
    private String otherHealth;

    /** 缺牙史 */
    @ApiModelProperty("是否缺牙史")
    private Boolean hadMissTooth;

    /** 缺牙原因字典id列表 */
    @ApiModelProperty("缺牙原因字典id列表")
    private List<Integer> missToothHistory;

    /** 填充治疗史 */
    @ApiModelProperty("是否填充治疗史")
    private Boolean hadFillTreat;

    /** 填充治疗（材料id列表）*/
    @ApiModelProperty("填充治疗（材料id列表）")
    private List<Integer> fillTreatHistory;

    /** 最近一次填充治疗日期 */
    @ApiModelProperty("最近一次填充治疗日期")
    private String fillTreatLastDate;

    /** 是否牙周手术 */
    @ApiModelProperty("是否牙周手术")
    private Boolean hadPeriodontalSurgery;

    /** 是否咬合调整 */
    @ApiModelProperty("是否咬合调整")
    private Boolean hadOcclusalAdjust;

    /** 是否修复义齿：0-否，1-是 */
    @ApiModelProperty("是否修复义齿")
    private Boolean hadRestorativeDentures;

    /** RPD部位 */
    @ApiModelProperty("RPD部位")
    @Size(max = 50, message = "RPD部位长度不能超过50个字符！")
    private String rpdPart;

    /** RPD戴用时间 */
    @ApiModelProperty("RPD戴用时间")
    private String rpdDate;

    /** LPD部位 */
    @ApiModelProperty("LPD部位")
    @Size(max = 50, message = "LPD部位长度不能超过50个字符！")
    private String lpdPart;

    /** LPD戴用时间 */
    @ApiModelProperty("LPD戴用时间")
    private String lpdDate;

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
    @Size(max = 50, message = "缺牙但不修复的原因长度不能超过50个字符！")
    private String missTeethUnrepeatCause;

    /** 是否正畸治疗*/
    @ApiModelProperty("是否正畸治疗")
    private Boolean hadOrthodontic;

    /** 正畸治疗开始日期*/
    @ApiModelProperty("正畸治疗开始日期")
    private String orthodonticStartDate;

    /** 正畸治疗结束日期*/
    @ApiModelProperty("正畸治疗结束日期")
    private String orthodonticEndDate;

    /** 是否接受过口腔卫生宣教*/
    @ApiModelProperty("是否接受过口腔卫生宣教")
    private Boolean hadHygieneEducation;

    /** 是否用过菌斑染色体*/
    @ApiModelProperty("是否用过菌斑染色体")
    private Boolean usedPlaqueDna;

    /** 每天刷牙次数*/
    @ApiModelProperty("每天刷牙次数")
    private Integer brushingTimes;

    /** 每次刷牙时长（min）*/
    @ApiModelProperty("每次刷牙时长（min）")
    private Byte brushingTime;

    /** 刷毛硬度：0-软，1-中，2-硬*/
    @ApiModelProperty("刷毛硬度：0-软，1-中，2-硬")
    private Byte brushHardness;

    /** 是否使用牙线*/
    @ApiModelProperty("是否使用牙线")
    private Boolean useFloss;

    /** 是否使用漱口水*/
    @ApiModelProperty("是否使用漱口水")
    private Boolean useCollutory;

    /** 是否夜磨牙*/
    @ApiModelProperty("是否夜磨牙")
    private Boolean bruxism;

    /** 烟龄（年）*/
    @ApiModelProperty("烟龄（年）")
    private Byte smokingAge;

    /** 每天吸烟数量（支）*/
    @ApiModelProperty("每天吸烟数量（支）")
    private Integer smokingNum;
}
