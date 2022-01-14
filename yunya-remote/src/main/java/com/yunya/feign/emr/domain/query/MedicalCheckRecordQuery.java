package com.yunya.feign.emr.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：普通电子病历-检查记录查询模型
 *
 * @author: chenlin
 * @Description: 普通电子病历-检查记录查询模型
 * @Date: 2022/1/10 15:13
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("普通电子病历-检查记录查询模型")
public class MedicalCheckRecordQuery extends PageQuery implements Serializable {
    /** 普通电子病历id */
    @ApiModelProperty(value = "普通电子病历id", required = true)
    @NotNull(message = "普通电子病历id不能为空")
    private Integer medicalId;
}
