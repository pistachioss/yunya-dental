package com.yunya.feign.patient_central.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：自助登记患者查询模型
 *
 * @author: chenlin
 * @Description: 自助登记患者查询模型
 * @Date: 2022/3/2 14:56
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("自助登记患者查询模型")
public class SelfRegistrationPatientQuery extends PageQuery implements Serializable {

    @ApiModelProperty(value = "组织ID（门诊）", required = true)
    @NotNull(message = "组织ID不能为空！")
    private Integer orgId;

    @ApiModelProperty(value = "当前日期（yyyy-MM-dd）", required = true)
    @NotBlank(message = "查询日期不能为空！")
    private String currentDate;
}
