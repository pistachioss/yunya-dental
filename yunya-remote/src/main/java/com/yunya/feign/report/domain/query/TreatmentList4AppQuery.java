package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 手机端就诊列表查询参数
 * @author: LHB
 * @create: 2020-12-23 09:58
 **/
@ApiModel(value = "TreatmentList4AppQuery",description = "手机端就诊列表查询参数")
@Data
public class TreatmentList4AppQuery implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "门诊id", required = false)
    /** 门诊id */
    private Integer orgId;

    @ApiModelProperty(value = "当前日期", required = false)
    /** 当前日期 */
    private String currentDate;

    @ApiModelProperty(value = "医生ID", required = false)
    /** 医生ID */
    private String dentistId;
}
