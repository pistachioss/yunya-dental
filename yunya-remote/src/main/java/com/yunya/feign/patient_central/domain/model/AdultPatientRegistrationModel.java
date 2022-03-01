package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

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

    /** 家族遗传史 */
    @ApiModelProperty("家族遗传史")
    private String familyHistory;

    /** 妊娠月数*/
    @ApiModelProperty("妊娠月数")
    private Integer pregnancyMonth;

    /** 是否哺乳: 0-否，1-是 */
    @ApiModelProperty("是否哺乳: 0-否，1-是")
    private Byte inBreastFeeding;

    /** 您认为还应该告诉医生的全身健康状况*/
    @ApiModelProperty("您认为还应该告诉医生的全身健康状况")
    private String generalHealth;

    /** 缺牙史：0-否，1-是 */
    @ApiModelProperty("缺牙史：0-否，1-是")
    private Byte hadMissTooth;

    /** 缺牙史（牙位）*/
    @ApiModelProperty("缺牙史（牙位）")
    private Integer missToothHistory;

    /** 填充治疗史：0-否，1-是 */
    @ApiModelProperty("填充治疗史：0-否，1-是")
    private Byte hadFillTreat;

    /** 填充治疗（材料id）*/
    @ApiModelProperty("填充治疗（材料id）")
    private Integer fillTreatHistory;

    /** 最近一次填充治疗日期 */
    @ApiModelProperty("最近一次填充治疗日期")
    private Date fillTreatLastDate;

    /** 是否牙周手术：0-否，1-是 */
    @ApiModelProperty("是否牙周手术：0-否，1-是")
    private Byte hadPeriodontalSurgery;

    /** 是否牙周调整：0-否，1-是 */
    @ApiModelProperty("是否牙周调整：0-否，1-是")
    private Byte hadOcclusalAdjust;

    /** 是否修复义齿：0-否，1-是 */
    @ApiModelProperty("是否修复义齿：0-否，1-是")
    private Byte hadRestorativeDentures;

    /** RPD部位 */
    @ApiModelProperty("RPD部位")
    private String rpdPart;

    /** RPD戴用时间 */
    @ApiModelProperty("RPD戴用时间")
    private Date rpdDate;

    /** LPD部位 */
    @ApiModelProperty("LPD部位")
    private String lpdPart;

    /** LPD戴用时间 */
    @ApiModelProperty("LPD戴用时间")
    private Date lpdDate;

    /** 是否预防治疗：0-否，1-是 */
    @ApiModelProperty("是否预防治疗：0-否，1-是")
    private Byte hadPreventiveTreat;

    /** 预防治疗周期 */
    @ApiModelProperty("预防治疗周期")
    private Integer preventiveTreatCycle;

    /** 上次预防治疗距今 */
    @ApiModelProperty("上次预防治疗距今")
    private Integer preventiveTreatLastMonth;

    /** 是否有使用困难或不舒适的经历: 0-否，1-是 */
    @ApiModelProperty("是否有使用困难或不舒适的经历: 0-否，1-是")
    private Byte hadDiffcultTreat;

    /** 缺牙但不修复的原因*/
    @ApiModelProperty("缺牙但不修复的原因")
    private String missTeethUnrepeatCause;

    /** 是否正畸治疗：0-否，1-是*/
    @ApiModelProperty("是否正畸治疗：0-否，1-是")
    private Byte hadOrthodontic;

    /** 正畸治疗开始日期*/
    @ApiModelProperty("正畸治疗开始日期")
    private Date orthodonticStartDate;

    /** 正畸治疗结束日期*/
    @ApiModelProperty("正畸治疗结束日期")
    private Date orthodonticEndDate;

    /** 是否接受过口腔卫生宣教: 0-否，1-是*/
    @ApiModelProperty("是否接受过口腔卫生宣教: 0-否，1-是")
    private Byte hadHygieneEducation;

    /** 是否用过菌斑染色体: 0-否，1-是*/
    @ApiModelProperty("是否用过菌斑染色体: 0-否，1-是")
    private Byte usedPlaqueDna;

    /** 每天刷牙次数*/
    @ApiModelProperty("每天刷牙次数")
    private Integer brushingTimesByDay;

    /** 每次刷牙时长（min）*/
    @ApiModelProperty("每次刷牙时长（min）")
    private Integer brushingDurationByTimes;

    /** 刷毛硬度：0-软，1-中，2-硬*/
    @ApiModelProperty("刷毛硬度：0-软，1-中，2-硬")
    private Byte bristleHardness;

    /** 是否使用牙线：0-否，1-是*/
    @ApiModelProperty("是否使用牙线：0-否，1-是")
    private Byte usedDentalFloss;

    /** 是否使用漱口水：0-否，1-是*/
    @ApiModelProperty("是否使用漱口水：0-否，1-是")
    private Byte usedMouthwash;

    /** 是否夜磨牙：0-否，1-是*/
    @ApiModelProperty("是否夜磨牙：0-否，1-是")
    private Byte hadNightGrinding;

    /** 烟龄（年）*/
    @ApiModelProperty("烟龄（年）")
    private Integer tobacco_age;

    /** 每天吸烟数量（支）*/
    @ApiModelProperty("每天吸烟数量（支）")
    private Integer tobaccoByDay;
}
