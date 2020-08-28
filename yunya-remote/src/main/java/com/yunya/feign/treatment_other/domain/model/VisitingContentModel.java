package com.yunya.feign.treatment_other.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 随访原因
 * @author: LHB
 * @create: 2020-08-27 09:14
 **/
@ApiModel(value = "随访原因")
@Data
@ToString
public class VisitingContentModel implements Serializable {
    /**
     * 随访日期
     */
    @ApiModelProperty(value = "随访日期", required = true)
    @NotNull(message = "随访日期不能为空!")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Future(message = "随访日期不能早于末诊日期！")
    private Date visitingDate;

    /**
     * 随访时间
     */
    @ApiModelProperty(value = "随访时间", required = true)
    @NotBlank(message = "随访时间不能为空！")
    private String visitingTime;

    /**
     * 随访原因 新建随访
     */
    @ApiModelProperty(value = "随访原因 新建随访", required = true)
    @NotBlank(message = "随访原因不能为空!")
    private String reason;
}
