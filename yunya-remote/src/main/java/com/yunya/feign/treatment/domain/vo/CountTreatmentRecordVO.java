package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 统计就诊记录视图模型
 * @author: LHB
 * @create: 2021-01-14 10:29
 **/
@Data
@ApiModel(value = "CountTreatmentRecordVO",description = "统计就诊记录视图模型")
public class CountTreatmentRecordVO implements Serializable {
    @ApiModelProperty("预约未到人数统计")
    private Integer appointNotArrived;
    @ApiModelProperty("侯诊中人数统计")
    private Integer waitingForTreat;
    @ApiModelProperty("就诊中人数统计")
    private Integer treatReceiving;
    @ApiModelProperty("接诊完成人数统计")
    private Integer treatCompleted;
    @ApiModelProperty("已结账人数统计")
    private Integer checkedOut;
    @ApiModelProperty("未结账人数统计")
    private Integer unCheckedOut;
}
