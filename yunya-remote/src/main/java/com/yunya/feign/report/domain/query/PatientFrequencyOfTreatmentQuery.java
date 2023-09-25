package com.yunya.feign.report.domain.query;

import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author: chenlin
 * @date: 2023/7/31 11:05
 * @description: 患者诊疗频率查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者诊疗频率查询模型")
public class PatientFrequencyOfTreatmentQuery extends DateRangeQueryForm {

    /** 是否查询全部患者 */
    @ApiModelProperty(value = "是否查询全部患者", required = true)
    @NotNull(message = "是否查询全部患者不能为空")
    private Boolean isFull;
}
