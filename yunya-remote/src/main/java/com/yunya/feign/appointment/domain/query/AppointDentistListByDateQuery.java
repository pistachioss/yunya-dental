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
 * 预约可视图列表（医生维度）查询参数
 *
 * @author yunya-lihuibin
 * @create 2020-08-12 11:00
 * @update yunya-lihuibin    2020-08-12    新建
 */
@ApiModel(value = "预约可视图列表（医生维度）查询参数")
@Data
@ToString
public class AppointDentistListByDateQuery implements Serializable {
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

    /** 日期 */
    @ApiModelProperty(value = "日期", required = true)
    @NotNull(message = "日期不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;

    /** 医生id */
    @ApiModelProperty(value = "医生id")
    private Integer dentistId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空！")
    private Integer orgId;

}
