package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/26
 */
@ApiModel(value = "产品售出激活统计参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponStatisticsQuery extends PageQuery implements Serializable {
  @ApiModelProperty(value = "产品名称")
  private String couponName;

  @ApiModelProperty(value = "产品分类")
  private List<Integer> couponCategoryIds;

  @ApiModelProperty(value = "产品类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）")
  private List<Integer> couponTypes;
}
