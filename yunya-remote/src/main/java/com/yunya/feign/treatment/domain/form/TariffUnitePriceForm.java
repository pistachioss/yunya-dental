package com.yunya.feign.treatment.domain.form;

import com.yunya.feign.treatment.domain.model.TariffUniteModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * 简介: 商品项目价格统一修改参数模型
 *
 * @author: chow
 * @date: 2021/9/23 16:54
 * @description:
 * @since: 1.0.0
 */
@Data
@ApiModel("价目表（商品）项目价格统一修改参数模型")
public class TariffUnitePriceForm implements Serializable {
  /** 门诊ID列表 */
  @ApiModelProperty(value = "门诊ID列表", required = true)
  @Size(min = 1, message = "请至少选择一个门诊")
  private Set<Integer> orgIds;
  /** 统一价格项目列表 */
  @Size(min = 1, message = "请至少选择一个项目")
  private Set<TariffUniteModel> tariffUniteModels;
}
