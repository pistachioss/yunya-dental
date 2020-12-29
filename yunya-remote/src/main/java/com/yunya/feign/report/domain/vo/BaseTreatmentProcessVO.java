package com.yunya.feign.report.domain.vo;

import com.yunya.models.report.BaseTreatmentProcess;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 就诊中间表信息
 * @author: LHB
 * @create: 2020-12-23 13:05
 **/
@ApiModel(value = "BaseTreatmentProcessVO",description = "就诊中间表信息")
@Data
public class BaseTreatmentProcessVO extends BaseTreatmentProcess implements Serializable {
    /**
     * 患者名字
     */
    @ApiModelProperty("患者名字")
    private String patientName;
    /**
     * 患者年龄
     */
    @ApiModelProperty("患者年龄")
    private Integer age;
    /**
     * 患着性别
     */
    @ApiModelProperty("患着性别")
    private Byte gender;
    /**
     * 预约医生名字
     */
    @ApiModelProperty("预约医生名字")
    private String appointDentistName;
    /**
     * 挂号医生名字
     */
    @ApiModelProperty("挂号医生名字")
    private String regDentistName;
    /**
     * 诊疗状态0-挂号；1-接诊；2-开单；3-治疗完成；4-已结账）
     */
    @ApiModelProperty("诊疗状态1-预约未到；2-候诊中；3-就诊中；4-治疗完成；5-已结账）")
    private Byte treatStatus;


}
