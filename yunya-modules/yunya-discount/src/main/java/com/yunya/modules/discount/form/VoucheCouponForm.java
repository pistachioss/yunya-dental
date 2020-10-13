package com.yunya.modules.discount.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.models.discount.FileInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className VoucheCouponForm
 * @description
 * @date 2020/8/18 16:16
 */
@Data
public class VoucheCouponForm {
  /**
   * 主键
   */
  @Id
  @ApiModelProperty("主键ID")
  @GeneratedValue(generator = "JDBC")
  private Integer id;

  /**
   * 名称
   */
  @ApiModelProperty("名称")
  @NotBlank(message = "名称不能为空")
  private String name;

  /**
   * 实物卡 0:非实体卡,1:实体卡
   */
  @ApiModelProperty("是否实物卡")
  @NotNull(message = "是否实物卡不能为空")
  private Boolean isMakePhysicalCard;

  /**
   * 营销产品分类ID
   */
  @NotNull(message = "营销产品分类ID不能为空")
  @ApiModelProperty("产品分类ID")
  private Integer productTypeId;

  /**
   * 面值
   */
  @NotNull(message = "面值不能为空")
  @ApiModelProperty("面值")
  private BigDecimal faceValue;

  /**
   * 售价
   */
  @NotNull(message = "售价不能为空")
  @ApiModelProperty("售价")
  private BigDecimal soldAmount;

  /**
   * 售出开始日期
   */
  @ApiModelProperty("售出开始日期")
  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
  private Date availableSaleStartDate;

  /**
   * 售出结束日期
   */
  @ApiModelProperty("售出结束日期")
  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
  private Date availableSaleEndDate;

  /**
   * 激活截至日期(产品有效期)
   */
  @ApiModelProperty("产品有效期")
  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
  private Date activationDeadline;

  /**
   * 激活后有效期
   */
  @ApiModelProperty("激活后有效期 只存天数，0代表永久有效")
  private Integer effectiveDays;

  /**
   * 账单单次使用限制数量
   */
  @NotNull(message = "单次限制数量不能为空")
  @ApiModelProperty("账单单词使用限制数量")
  private Integer limitCount;

  /**
   * 是否可混合使用优惠 0.不可以共用 1.可以共用
   */
  @NotNull(message = "是否混合使用选项不能为空")
  @ApiModelProperty("是否可混合使用优惠 0.不可以共用 1.可以共用")
  private Integer mixedUseType;

  /**
   * 是否可与他人共享
   */
  @NotNull(message = "可否与他人共享选项不能为空")
  @ApiModelProperty("是否与他人共享")
  private Boolean isShare;

  /**
   * 工作量比例
   */
  @ApiModelProperty("工作量比例")
  @NotNull(message = "工作量比例不能为空")
  private BigDecimal workloadRate;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String remark;

  /**
   * 可使用门诊
   */
  @ApiModelProperty("可使用门诊列表")
  private String useableClinci;




}
