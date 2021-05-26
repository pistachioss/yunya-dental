package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 患者积分记录查询
 * @author: LHB
 * @create: 2021-04-23 10:42
 **/
@ApiModel(value = "PatientCreditsRecordQuery", description = "患者积分记录查询")
@Data
public class PatientCreditsRecordQuery extends PageQuery implements Serializable {
    @ApiModelProperty(value = "患者ID",required = true)
    @NotNull(message = "患者ID不能为空")
    private Integer patientId;

}
