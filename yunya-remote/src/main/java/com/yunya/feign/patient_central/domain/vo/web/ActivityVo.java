package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author YK
 */
@Data
@ApiModel(value = "活动列表返回模型")
public class ActivityVo {
    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private Integer id;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String name;

    /**
     * 限制开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date limitStartDate;

    /**
     * 限制时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date limitEndDate;

    /**
     * 是否有时间限制（0-否；1-是））
     */
    @ApiModelProperty(value = "是否有时间限制")
    private Integer timeLimit;

}