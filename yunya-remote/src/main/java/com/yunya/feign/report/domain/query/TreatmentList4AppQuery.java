package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 手机端就诊列表查询参数
 * @author: LHB
 * @create: 2020-12-23 09:58
 **/
@ApiModel(value = "TreatmentList4AppQuery",description = "手机端就诊列表查询参数")
@Data
public class TreatmentList4AppQuery extends PageQuery implements Serializable {
    @ApiModelProperty(value = "门诊id", required = false)
    /** 门诊id */
    private Integer orgId;

    @ApiModelProperty(value = "当前日期", required = true)
    /** 当前日期 */
    private String queryDate;

    @ApiModelProperty(value = "医生ID", required = false)
    /** 医生ID */
    private Integer dentistId;
}
