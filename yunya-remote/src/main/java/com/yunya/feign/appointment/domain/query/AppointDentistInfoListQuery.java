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
 * 查询单个医生在指定时间段内的预约信息（单个医生维度）
 *
 * @author yunya-lihuibin
 * @create 2020-08-12 12:45
 * @update yunya-lihuibin    2020-08-12    新建
 */
@ApiModel(value = "查询单个医生在指定时间段内的预约信息（单个医生维度）")
@Data
@ToString
public class AppointDentistInfoListQuery implements Serializable {
    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 7;

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期", required = true)
    @NotNull(message = "开始日期不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期", required = true)
    @NotNull(message = "结束日期不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endDate;

    /** 医生id */
    @ApiModelProperty(value = "医生id", required = true)
    @NotNull(message = "医生id不能为空！")
    private Integer dentistId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空！")
    private Integer orgId;
}
