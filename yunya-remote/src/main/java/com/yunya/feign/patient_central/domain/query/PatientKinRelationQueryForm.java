package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 患者亲属关系信息查询QueryFrom
 *
 * @author: WY
 * @date 2020/7/29 11:08
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者亲属关系")
public class PatientKinRelationQueryForm implements Serializable {
    /**
     * 患者ID
     */
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
