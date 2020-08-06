package com.yunya.feign.appointment.domain.form;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改项目适用门诊
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 15:15
 * @update yunya-lihuibin    2020-07-31    新建
 */
@ApiModel(value = "门诊可预约项目")
@Data
@ToString
public class ClinicAppointItemForm implements Serializable {

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空")
    private Integer orgId;

    /** 预约项目id */
    @ApiModelProperty(value = "预约项目id", required = true)
    @NotNull(message = "预约项目id不能为空")
    private Integer appointItemId;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /** 是否启用 是否有效 */
    @ApiModelProperty(value = "是否启用 是否有效", required = true)
    @NotNull(message = "是否启用 是否有效不能为空")
    private Boolean inservice;

}
