package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 操作标签记录Model
 *
 * @author: WY
 * @date: 2020/11/2 10:02
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("查询标签记录Model")
public class PatientLabelRecordQueryForm implements Serializable {

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

}