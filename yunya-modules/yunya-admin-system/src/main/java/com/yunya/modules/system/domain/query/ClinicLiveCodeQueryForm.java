package com.yunya.modules.system.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：门店店长活码查询模型
 *
 * @author: chenlin
 * @Description: 门店店长活码查询模型
 * @Date: 2022/5/18 15:56
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门店店长活码查询模型")
public class ClinicLiveCodeQueryForm extends PageQuery implements Serializable {

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;
}
