package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
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
    @ApiModelProperty(value = "预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date appointDate;

    /** 预约类型（0-初诊/1-复诊） */
    @ApiModelProperty(value = "预约类型（0-初诊/1-复诊）")
    private Byte appointType;

    /** 患者姓名/手机号/姓名拼音 */
    @ApiModelProperty(value = "患者姓名/手机号/姓名拼音")
    private String seach;

    /** 病例编号 */
    @ApiModelProperty(value = "病历编号")
    private String medicalNumber;

    /** 预约医生姓名 */
    @ApiModelProperty(value = "预约医生姓名")
    private String dentistName;


}
