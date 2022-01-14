package com.yunya.feign.emr.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：症状设置查询模型
 *
 * @author: chenlin
 * @Description: 症状设置查询模型
 * @Date: 2022/1/9 13:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("症状设置查询模型")
public class SymptomConfigQuery extends PageQuery implements Serializable {

    /** 检查id*/
    @ApiModelProperty(value = "检查id", required = true)
    @NotNull(message = "检查id不能为空")
    private Integer checkId;
}
