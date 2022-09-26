package com.yunya.feign.treatment_other.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 回访内容保存模型
 * @author: LHB
 * @create: 2022-09-27 09:14
 **/
@ApiModel(value = "回访内容保存模型")
@Data
@ToString
public class ReturnVisitingContentForm implements Serializable {

    /** 回访记录id */
    @ApiModelProperty("回访记录id，编辑时非空")
    private Integer id;

    /**
     * 回访日期
     */
    @ApiModelProperty(value = "回访日期", required = true)
    @NotEmpty(message = "回访日期不能为空!")
    private String returnDate;

    /**
     * 回访时间
     */
    @ApiModelProperty(value = "回访时间", required = true)
    @NotBlank(message = "回访时间不能为空！")
    private String returnTime;

    /**
     * 回访原因
     */
    @ApiModelProperty(value = "回访原因", required = true)
//    @NotBlank(message = "回访原因不能为空!")
    @Size(max = 50, message = "回访原因长度不能超过50个字符！")
    private String returnReason;

    /**
     * 回访内容
     */
    @ApiModelProperty(value = "回访内容", required = true)
    @Size(max = 50, message = "回访内容长度不能超过200个字符！")
//    @NotBlank(message = "回访内容不能为空!")
    private String returnContent;
}
