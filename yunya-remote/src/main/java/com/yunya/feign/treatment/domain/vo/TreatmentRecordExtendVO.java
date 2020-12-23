package com.yunya.feign.treatment.domain.vo;

import com.yunya.models.treatment.TreatmentRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 就诊记录扩展
 * @author: LHB
 * @create: 2020-12-10 16:18
 **/
@Data
@ApiModel(value = "TreatmentRecordExtendVO",description = "就诊记录扩展")
public class TreatmentRecordExtendVO extends TreatmentRecord implements Serializable {
    @ApiModelProperty("末次就诊日期")
    private Date lastTreatmentDate;
    @ApiModelProperty("挂号助手ID")
    private Integer regAssistantId;
}
