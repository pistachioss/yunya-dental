package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 根据排班开始结束日期/门诊id/医生id查询预约可视图（患者维度）
 *
 * @author yunya-lihuibin
 * @create 2020-08-11 20:08
 * @update yunya-lihuibin    2020-08-11    新建
 */
@ApiModel(value = "PatientDimensionByDayQuery",description = "根据排班开始结束日期/门诊id/医生id查询预约可视图（活动天）（患者维度）")
@Data
@ToString
public class PatientDimensionByDayQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 7;

    @ApiModelProperty("排序字段（默认按患者预约数量） patientNum-患者预约数量；date-日期")
    private String order = "patientNum";

    @ApiModelProperty("排序规则（默认降序） asc-升序；desc-降序")
    private String orderBy = "desc";

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "开始日期不能为空！")
    private Date startDate;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "结束日期不能为空！")
    private Date endDate;

    /** 医生id */
    @ApiModelProperty(value = "医生id", required = true)
    private Integer dentistId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空！")
    private Integer orgId;

}
