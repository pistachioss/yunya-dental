package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 完成随访
 * @author: LHB
 * @create: 2020-08-25 15:01
 **/
@ApiModel(value = "完成随访")
@Data
@ToString
public class FinishVisitingForm implements Serializable {
    /** 随访记录id */
    @ApiModelProperty(value = "随访记录id", required = true)
    @NotNull(message = "随访记录id不能为空！")
    private Integer id;

    /** 随访内容 */
    @ApiModelProperty(value = "随访内容", required = true)
    @NotBlank(message = "随访内容不能为空！")
    private String visitingContent;
}
