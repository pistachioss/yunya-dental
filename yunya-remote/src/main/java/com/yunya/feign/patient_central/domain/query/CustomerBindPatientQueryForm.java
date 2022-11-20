package com.yunya.feign.patient_central.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2022/11/10 10:58
 * @description: 微信绑定患者查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("微信绑定患者查询模型")
public class CustomerBindPatientQueryForm extends PageQuery implements Serializable {

    /** unionid */
    @ApiModelProperty(value = "unionid", required = true)
    @NotEmpty(message = "unionid不能为空")
    private String unionid;
}
