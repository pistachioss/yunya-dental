package com.yunya.feign.emr.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

/**
 * 简介：治疗计划查询模型
 *
 * @author: chenlin
 * @Description: 治疗计划查询模型
 * @Date: 2022/1/12 15:52
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划查询模型")
public class TreatPlanRecordQuery extends PageQuery implements Serializable {

    /** 患者id*/
    @ApiModelProperty(value = "患者id", required = true)
    private Integer patientId;

    /** 治疗计划名称*/
    @ApiModelProperty("治疗计划名称")
    private String planName;

    /** 时间类型 */
    @ApiModelProperty(value = "时间类型:0-日；1-月；2-年")
    private Byte dateType = 0;

    /** 查询时间 */
    @ApiModelProperty(value = "查询开始时间")
    private String startDate;

    /** 查询结束时间 */
    @ApiModelProperty(value = "查询结束时间")
    private String endDate;

    /** 治疗状态：0-未确认，1-已确认，2-进行中，3-已完成，4-提前终止*/
    @ApiModelProperty("治疗状态：0-未确认，1-已确认，2-进行中，3-已完成，4-提前终止")
    private Collection<Byte> status;
}
