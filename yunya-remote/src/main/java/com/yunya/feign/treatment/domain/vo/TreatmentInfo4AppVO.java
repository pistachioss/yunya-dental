package com.yunya.feign.treatment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.feign.appointment.vo.AppointmentSplitVo;
import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.internal.dynalink.linker.LinkerServices;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: App端个人就诊详细信息视图模型
 * @author: LHB
 * @create: 2020-12-16 09:54
 **/
@Data
@ApiModel(value = "TreatmentInfo4App", description = "App端个人就诊详细信息视图模型")
public class TreatmentInfo4AppVO implements Serializable {
    /** 预约ID */
    @ApiModelProperty("预约ID")
    private Integer appointId;
    /** 挂号ID */
    @ApiModelProperty("挂号ID")
    private Integer registedId;
    /** 账单编号 */
    @ApiModelProperty("账单编号")
    private String billNumber;
    /** 就诊记录ID */
    @ApiModelProperty("就诊记录ID")
    private Integer treatmentId;
    /** 患者ID */
    @ApiModelProperty("患者ID")
    private Integer patientId;
    /** 患者名字 */
    @ApiModelProperty("患者名字")
    private String patientName;
    @ApiModelProperty("预约门诊科室ID")
    private Integer deptRoomId;
    @ApiModelProperty("预约门诊科室名称")
    private String deptRoomName;
    @ApiModelProperty("预约设备ID")
    private Integer clinicDeviceItemId;
    @ApiModelProperty("预约设备名称")
    private String clinicDeviceItemName;
    @ApiModelProperty("预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date appointDate;
    @ApiModelProperty("预约时间")
    private String appointTime;
    @ApiModelProperty("预约时长")
    private Integer appointDuration;
    @ApiModelProperty("预约内容")
    private String appointContent;
    @ApiModelProperty("预约确认 0-未确认；1-确认")
    private Boolean confirmStatus;
    @ApiModelProperty("预约备注")
    private String remarks;
    @ApiModelProperty("预约类型 0-初诊预约； 1-复诊预约")
    private Byte appointType;
    @ApiModelProperty("预约分解列表")
    private List<AppointmentSplitVo> splits;
    /** ----------------------------挂号信息----------------------------------- */
    @ApiModelProperty("挂号医生")
    private Integer regDentistId;
    @ApiModelProperty("挂号医生名字")
    private String regDentistName;
    @ApiModelProperty("挂号助手")
    private Integer regAssistantId;
    @ApiModelProperty("挂号助手名字")
    private String regAssistantName;
    @ApiModelProperty("挂号日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date crtTime;
    /** ----------------------------账单信息------------------------------------------ */
    @ApiModelProperty("开单项目")
    private List<TreatmentOrderInfo4AppVO> orderItems;
    @ApiModelProperty("应收金额（消费总额）")
    private BigDecimal receivableAmount;
    @ApiModelProperty("本单优惠总额")
    private BigDecimal privilegeAmount;
    @ApiModelProperty("实际应收金额")
    private BigDecimal actualReceivableAmount;
    @ApiModelProperty("已收金额（本单收费总额）")
    private BigDecimal receivedAmount;
    @ApiModelProperty("欠费金额（本单欠费）")
    private BigDecimal debtAmount;
}
