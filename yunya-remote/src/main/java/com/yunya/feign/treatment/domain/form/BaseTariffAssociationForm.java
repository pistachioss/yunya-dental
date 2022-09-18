package com.yunya.feign.treatment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 开单关联修改参数封装模型
 *
 * @author: chow
 * @date: 2020/8/8 16:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单关联修改参数封装模型")
@Data
@ToString
public class BaseTariffAssociationForm implements Serializable {
  /** 电子病历处理内容 */
  @ApiModelProperty("电子病历处理内容")
  @Size(max = 300, message = "'电子病历处理内容'不能超出300个字符！")
  private String emr;

  /** 注意事项 */
  @ApiModelProperty("注意事项")
  @Size(max = 300, message = "'注意事项'不能超出300个字符！")
  private String attention;

  /** 随访信息 */
  @ApiModelProperty("随访信息")
  @NotEmpty(message = "随访信息不能为空")
  private List<FellowUpInfoForm> fellowUpInfoForm;
}
