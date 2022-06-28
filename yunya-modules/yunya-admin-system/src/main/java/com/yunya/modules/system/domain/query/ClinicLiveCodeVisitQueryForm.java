package com.yunya.modules.system.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
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
public class ClinicLiveCodeVisitQueryForm extends PageQuery implements Serializable {

    /** 查询开始日期 */
    @ApiModelProperty(value = "查询开始日期", required = true, example = "yyyy-MM-dd")
    @NotBlank(message = "开始日期不能为空！")
    private String startDate;

    /** 查询结束日期 */
    @ApiModelProperty(value = "查询结束日期", required = true, example = "yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空！")
    private String endDate;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 是否首次访问 */
    @ApiModelProperty("是否首次访问")
    private Boolean isFirstVisit;
}
