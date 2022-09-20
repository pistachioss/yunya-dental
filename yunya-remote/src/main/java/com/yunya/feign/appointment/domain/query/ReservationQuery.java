package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 预约登记查询参数
 **/
@ApiModel(value = "ReservationQuery",description = "预约登记查询参数")
@Data
public class ReservationQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 就近门诊ID
     */
    @ApiModelProperty(value = "门诊ID")
    private Integer orgId;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目")
    private String appointItemName;

    /**
     * 渠道来源ID
     */
    @ApiModelProperty(value = "渠道来源ID" )
    private Integer reservationSourceId;

    /**
     * 就诊人姓名
     */
    @ApiModelProperty(value = "就诊人姓名")
    private String patientName;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String patientPhone;

    /**
     * 预约登记状态 0-新建；1-已预约；2-已挂号
     */
    @ApiModelProperty("预约登记状态 0-新建；1-已预约；2-已挂号")
    private Byte status;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    @ApiModelProperty(value = "是否有效，是否删除(默认有效) 1-有效；0删除",required = true)
    private Boolean inservice;

    /**
     * 预约开始日期
     */
    @ApiModelProperty(value = "预约开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String appointStartDate;

    /**
     * 预约结束日期
     */
    @ApiModelProperty(value = "预约结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private String appointEndDate;
}
