package com.yunya.feign.patient_central.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
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
public class PatientKinRelationQueryForm extends PageQuery implements Serializable {
    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空！")
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 关系区分: 0 亲属关系；1 推荐关系；
     */
    @ApiModelProperty(value = "关系区分: 0 亲属关系；1 推荐关系")
    private Byte type;
}
