package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @Class FellowUpInfoForm
 * @Description: 随访信息
 * @Author lihuibin
 * @Date 2022/8/11 22:35
 * @Version 1.0
 */
@ApiModel("随访信息参数模型")
@Data
@ToString
@Validated
public class FellowUpInfoForm implements Serializable {
    /** 随访信息ID */
    @ApiModelProperty("随访信息ID")
    private Integer id;
    /** 几天后随访 */
    @ApiModelProperty("几天后随访")
    @Min(value = 0, message = "最小值为0")
    private Integer fellowUp;

    /** 随访原因 */
    @ApiModelProperty("随访原因")
    @Min(value = 0, message = "最小值为0")
    @Length(max = 300,message = "随访原因最大支持300个字符")
    private String fellowUpCase;

    /** 提交类型 1-新增；2-修改；3-删除 */
    @ApiModelProperty("提交类型 1-新增；2-修改；3-删除")
    @NotNull(message ="提交类型不能为空")
    @Min(value = 1)
    @Max(value = 3)
    private Integer type;
}
