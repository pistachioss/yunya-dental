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
 * 预约列表条件查询参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-27 15:12
 * @update yunya-lihuibin    2020-07-27    新建
 */
@ApiModel("预约列表条件查询参数封装")
@Data
@ToString
public class AppointListQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 预约日期 */
    @ApiModelProperty(value = "预约日期", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "预约日期不能为空！")
    private Date appointDate;

    /** 预约类型（0-初诊/1-复诊） */
    @ApiModelProperty(value = "预约类型（0-初诊/1-复诊）")
    private Byte firstVisit;

    /** 患者姓名/手机号/姓名拼音 */
    @ApiModelProperty(value = "患者姓名/手机号/姓名拼音")
    private String search;

    /** 病例编号 */
    @ApiModelProperty(value = "病历编号")
    private String medicalNumber;

    /** 预约医生姓名 */
    @ApiModelProperty(value = "预约医生姓名")
    private String dentistName;

    /** 组织ID */
    @ApiModelProperty(value = "组织ID")
    private Integer orgId;


}
