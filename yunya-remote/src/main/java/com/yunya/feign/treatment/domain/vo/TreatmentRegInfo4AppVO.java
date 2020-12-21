package com.yunya.feign.treatment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: APP端, 就诊详情-挂号信息视图模型
 * @author: LHB
 * @create: 2020-12-17 20:11
 **/
@Data
@ApiModel(value = "TreatmentRegInfo4AppVO", description = "APP端, 就诊详情-挂号信息视图模型")
public class TreatmentRegInfo4AppVO implements Serializable {
    /** 挂号ID */
    @ApiModelProperty("挂号ID")
    private Integer registedId;
    /** 就诊记录ID */
    @ApiModelProperty("就诊记录ID")
    private Integer treatmentId;
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
}
