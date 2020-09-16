package com.yunya.feign.appointment.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预约列表视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 20:13
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "预约列表视图模型")
@Data
@ToString
public class AppointmentListItemVo implements Serializable {

    /** 预约ID */
    @ApiModelProperty(value = "预约ID")
    private Integer id;

    /** 诊所id */
    @ApiModelProperty(value = "诊所id")
    private Integer orgId;

    /******************************* 患者信息 ********************************/
    /** 患者id */
    @ApiModelProperty(value = "患者id")
    private Integer patientId;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /** 拼音名字 */
    @ApiModelProperty(value = "拼音名字")
    private String pinyinName;

    /** 患者手机号 */
    @ApiModelProperty(value = "患者手机号")
    private String mobile;

    /** 患者出生日期 */
    @ApiModelProperty(value = "患者出生日期")
    private String birthday;

    /** 性别 */
    @ApiModelProperty(value = "性别")
    private Byte gender;

    /** 年龄 */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /** 患者病历号 */
    @ApiModelProperty(value = "患者病历号")
    private String medicalNumber;

    /** 患者过敏原 */
    @ApiModelProperty(value = "患者过敏原")
    private String allergen;

    /** 欠费总额 */
    @ApiModelProperty(value = "欠费总额")
    private BigDecimal arrears;

    /** 会员图标 */
    @ApiModelProperty(value = "会员图标")
    private String memberIcon;

    /** 档案备注 */
    @ApiModelProperty(value = "档案备注")
    private String patientRemark;

    /********************************  预约信息 *********************************/

    /** 预约医生id */
    @ApiModelProperty(value = "预约医生id")
    private Integer dentistId;

    /** 预约医生姓名 */
    @ApiModelProperty(value = "预约医生姓名")
    private String dentistName;

    /** 预约助手id */
    @ApiModelProperty(value = "预约助手id")
    private Integer assistantId;

    /** 预约助手姓名 */
    @ApiModelProperty(value = "预约助手姓名")
    private String assistantName;

    /** 预约科室ID */
    @ApiModelProperty(value = "预约科室ID")
    private Integer clinicDeptRoomId;

    /** 预约科室名称 */
    @ApiModelProperty(value = "预约科室名称")
    private String clinicDeptRoomName;

    /** 预约时间 */
    @ApiModelProperty(value = "预约时间")
    private String appointTime;

    /** 预约时长 */
    @ApiModelProperty(value = "预约时长")
    private Integer appointDuration;

    /** 预约内容 */
    @ApiModelProperty(value = "预约内容")
    private String appointContent;

    /** 预约备注 */
    @ApiModelProperty(value = "预约备注")
    private String remarks;

    /** 预约类型（初/复诊） */
    @ApiModelProperty(value = "预约类型（初/复诊）")
    private Byte appointType;

    /** 预约状态 */
    @ApiModelProperty(value = "预约状态")
    private Byte appointStatus;

    /** 预约确认 */
    @ApiModelProperty(value = "预约确认")
    private Boolean confirmStatus;

    /** 预约日期 */
    @ApiModelProperty(value = "预约日期")
    private Date appointDate;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    /** 创建人 */
    @ApiModelProperty(value = "创建人")
    private String crtName;
}
