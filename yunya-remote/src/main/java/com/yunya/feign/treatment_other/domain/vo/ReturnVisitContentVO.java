package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 回访内容数据模型
 * @author: LHB
 * @create: 2022-09-27 09:14
 **/
@ApiModel(value = "回访内容数据模型")
@Data
@ToString
public class ReturnVisitContentVO implements Serializable {

    /** 回访记录id */
    @ApiModelProperty("回访记录id")
    private Integer id;

    /**
     * 回访日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "回访日期")
    private Date returnDate;

    /**
     * 回访时间
     */
    @ApiModelProperty(value = "回访时间")
    @JsonFormat(pattern = "HH:mm",timezone = "GMT+8")
    private Date returnTime;

    /**
     * 回访原因
     */
    @ApiModelProperty(value = "回访原因")
    private String returnReason;

    /**
     * 回访内容
     */
    @ApiModelProperty(value = "回访内容")
    private String returnContent;
}
