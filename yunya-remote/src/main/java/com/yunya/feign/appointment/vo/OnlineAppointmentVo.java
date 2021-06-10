package com.yunya.feign.appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 在线预约参数
 * @author: LHB
 * @create: 2021-05-19 10:02
 **/
@ApiModel(value = "OnlineAppointmentVo",description = "在线预约返回参数")
@Data
public class OnlineAppointmentVo implements Serializable {
    /**
     * 预约申请ID (onlineAppointment表中主键ID)
     */
    @ApiModelProperty("预约申请ID")
    private Integer id;

    /**
     * 微信用户唯一标识
     */
    @ApiModelProperty("微信用户唯一标识")
    private String openId;

    /**
     * 预约ID (appointment表中主键ID)
     */
    @ApiModelProperty("预约ID,可能为空")
    private Integer appointmentId;

    /**
     * 门诊ID
     */
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /**
     * 门诊名称
     */
    @ApiModelProperty(value = "门诊名称")
    @Excel(name = "门诊名称")
    private String orgName;

    /**
     * 医生ID
     */
    @ApiModelProperty("医生ID")
    private Integer dentistId;

    /**
     * 医生名字
     */
    @Excel(name = "医生名字")
    @ApiModelProperty(value = "医生名字")
    private String dentistName;

    /**
     * 患者ID,可能为空
     */
    @ApiModelProperty("患者ID,可能为空")
    private Integer patientId;

    /**
     * 预约项目ID
     */
    @ApiModelProperty("预约项目ID")
    private Integer appointItemId;

    /**
     * 预约项目
     */
    @Excel(name = "预约项目名称")
    @ApiModelProperty(value = "预约项目名称")
    private String appointItemName;

    /**
     * 预约内容
     */
    @ApiModelProperty(value = "预约内容",hidden = true)
    private String appointContent;

    /**
     * 预约日期
     */
    @Excel(name = "预约日期")
    @ApiModelProperty("预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String appointDate;

    /**
     * 预约时间
     */
    @Excel(name = "预约时间")
    @ApiModelProperty("预约时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String appointTime;

    /**
     * 预约时长
     */
    @Excel(name = "预约时长")
    @ApiModelProperty("预约时长")
    private Integer duration;

    /**
     * 就诊患者名字
     */
    @Excel(name = "就诊患者名字")
    @ApiModelProperty("就诊患者名字")
    private String patientName;

    /**
     * 患者手机号
     */
    @Excel(name = "患者手机号")
    @ApiModelProperty("患者手机号")
    private String patientPhone;

    /**
     * 预约申请状态 0-申请中；1-通过；2-取消
     */
    @Excel(name = "线上预约状态",readConverterExp = "0=申请中,1=通过,2=取消")
    @ApiModelProperty("线上预约状态 0-申请中；1-通过；2-取消")
    private Byte status;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    @ApiModelProperty(value = "是否有效，是否删除(默认有效) 1-有效；0删除",hidden = true)
    private Boolean inservice;

    /**
     * 预约申请时间
     */
    @Excel(name = "预约申请时间")
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;

    /**
     * 病历编号
     */
    @Excel(name = "病历编号")
    @ApiModelProperty("病历编号")
    private String medicalNumber;

    /**
     * 预约确认状态
     */
    @Excel(name = "预状态约",readConverterExp = "0=未确认,1=已确认")
    @ApiModelProperty("预状态约 0=未确认,1=已确认")
    private Boolean confirmStatus;

    /**
     * 品牌名称
     */
    @ApiModelProperty("品牌名称")
    private String brandName;

    /**
     * 门诊地址
     */
    @ApiModelProperty("门诊地址")
    private String addr;

    /**
     * 上班时间
     */
    @ApiModelProperty("上班时间")
    private String workingHours;

    /**
     * 下班时间
     */
    @ApiModelProperty("下班时间")
    private String offworkingHours;
}
