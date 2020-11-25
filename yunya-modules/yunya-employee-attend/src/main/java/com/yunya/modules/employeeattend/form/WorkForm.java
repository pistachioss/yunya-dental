package com.yunya.modules.employeeattend.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class WorkForm {

    /**
     * 门诊id
     */
    @ApiModelProperty("门诊id")
    private Integer companyId;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

}
