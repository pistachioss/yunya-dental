package com.yunya.feign.system.form;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 门诊科室配置查询参数模型
 *
 * @author: chow
 * @date: 2020/9/29 20:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊科室配置查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ClinicDeptRoomConfigureQueryForm extends PageQuery implements Serializable {
  /** 科室ID */
  @ApiModelProperty(value = "科室ID", required = true)
  @NotNull(message = "科室ID不能为空！")
  private Integer deptRoomId;
}
