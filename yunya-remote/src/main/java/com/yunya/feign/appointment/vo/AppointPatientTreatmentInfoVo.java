package com.yunya.feign.appointment.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.EOFException;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预约患者就诊模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 12:21
 * @update yunya-lihuibin    2020-08-10    新建
 */
@Data
@ToString
public class AppointPatientTreatmentInfoVo implements Serializable {

    /** 病历号 */
    @Excel(name = "病历号", targetAttr = "medicalNumber", type = Excel.Type.EXPORT)
    private String medicalNumber;

    /** 患者名字 */
    @Excel(name = "患者")
    private String patientName;

    /** 患者手机号 */
    @Excel(name = "手机号码")
    private String mobile;

    /** 助手 */
    @Excel(name = "助手")
    private String assistantName;

    /** 初复诊 */
    @Excel(name = "初复诊")
    private String appointType;

    /** 科室 */
    @Excel(name = "科室")
    private String clinicDeptRoomName;

    /** 预约时间 */
    @Excel(name = "预约时间")
    private String appointTime;

    /** 预约时长 */
    @Excel(name = "预约时长")
    private Integer appointDuration;

    /** 预约内容 */
    @Excel(name = "预约内容")
    private String appointContent;

    /** 欠费 */
    @Excel(name = "欠费")
    private BigDecimal arrears;

    /** 确认状态 */
    @Excel(name = "确认状态")
    private String confirmStatus;

    /** 预约备注 */
    @Excel(name = "预约备注")
    private String remarks;

    /** 档案备注 */
    @Excel(name = "档案备注")
    private String patientRemarks;

    /** 预约操作记录 */
    @Excel(name = "预约操作记录")
    private StringBuilder appointOperationRecord;

    /** 取消原因 */
    @Excel(name = "取消原因")
    private String cancleReasion;



}
