package com.yunya.feign.appointment.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预约列表导出vo
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 14:55
 * @update yunya-lihuibin    2020-07-28    新建
 */
@ApiModel("预约列表导出vo")
@Data
@ToString
public class AppointListExportVo implements Serializable {

    /** 医生id */
    private Integer dentistId;

    /** 医生名字 */
    @Excel(name = "医生", height=20, type = Excel.Type.EXPORT)
    private String dentistName;

    /** 病历号 */
    @Excel(name = "病历号",height=20, type = Excel.Type.EXPORT)
    private String medicalNumber;

    /** 患者名字 */
    @Excel(name = "患者",height=20, type = Excel.Type.EXPORT)
    private String patientName;

    /** 患者手机号 */
    @Excel(name = "手机号码",height=20,type = Excel.Type.EXPORT)
    private String mobile;

    /** 助手 */
    @Excel(name = "助手",height=20, type = Excel.Type.EXPORT)
    private String assistantName;

    /** 初复诊 */
    @Excel(name = "初复诊",height=20, readConverterExp="0=初诊,1=复诊", type = Excel.Type.EXPORT)
    private Byte appointType;

    /** 科室 */
    @Excel(name = "科室",height=20, type = Excel.Type.EXPORT)
    private String clinicDeptRoomName;

    /** 预约时间 */
    @Excel(name = "预约时间",height=20, type = Excel.Type.EXPORT)
    private String appointTime;

    /** 预约时长 */
    @Excel(name = "预约时长",height=20, type = Excel.Type.EXPORT)
    private Integer appointDuration;

    /** 预约内容 */
    @Excel(name = "预约内容",height=20, type = Excel.Type.EXPORT)
    private String appointContent;

    /** 欠费 */
    @Excel(name = "欠费",height=20, type = Excel.Type.EXPORT)
    private BigDecimal arrears;

    /** 确认状态 */
    @Excel(name = "确认状态",height=20, readConverterExp = "false=未确认,true=确认", type = Excel.Type.EXPORT)
    private Boolean confirmStatus;

    /** 预约备注 */
    @Excel(name = "预约备注",height=20, type = Excel.Type.EXPORT)
    private String remarks;

    /** 档案备注 */
    @Excel(name = "档案备注",height=20, type = Excel.Type.EXPORT)
    private String patientRemarks;

    /** 预约操作记录 */
    @Excel(name = "预约操作记录",height=20, type = Excel.Type.EXPORT)
    private String appointOperationRecord;

    /** 取消原因 */
    @Excel(name = "取消原因",height=20, type = Excel.Type.EXPORT)
    private String cancleReasion;

}
