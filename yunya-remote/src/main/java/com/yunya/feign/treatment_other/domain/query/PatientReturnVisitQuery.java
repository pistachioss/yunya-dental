package com.yunya.feign.treatment_other.domain.query;

import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/9/27 15:28
 * @description: 患者档案-回访查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者档案-回访查询模型")
public class PatientReturnVisitQuery extends MultiClinicDateRangeQueryForm implements Serializable {

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;
}
