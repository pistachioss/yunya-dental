package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 会员卡信息参数模型
 *
 * @author: chow
 * @date: 2020/7/22 15:50
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员卡信息新增参数模型")
public class MemberTypeModel implements Serializable {
  /** 会员卡名称 */
  @ApiModelProperty(value = "会员卡名称", required = true)
  @NotBlank(message = "会员卡名称不能为空！")
  @Size(max = 25, message = "会员卡名称长度不能超过25")
  private String name;
  /** 对应藤卡会员 */
  @ApiModelProperty(value = "对应藤卡会员", required = true)
  @Size(max = 25, message = "对应藤卡会员长度不能超过25")
  private String oldName;
  /** 次一等级会员ID */
  @ApiModelProperty("次一等级会员ID")
  private Integer nextLevelId;
  /** 类型,0:普通,1:VIP */
  @ApiModelProperty(value = "会员类型", required = true)
  @NotNull(message = "会员类型不能为空！")
  private Byte type;
  /** 续费金额 */
  @ApiModelProperty(value = "会员卡名续费金额", required = true)
  @NotNull(message = "会员卡名续费金额不能为空！")
  @Min(value = 0, message = "会员卡名续费金额不能小于0")
  private BigDecimal renewalAmount;
  /** 充值达标获卡金额 */
  @ApiModelProperty(value = "充值达标获卡金额", required = true)
  @NotNull(message = "充值达标获卡金额不能为空！")
  @Min(value = 0, message = "充值达标获卡金额不能小于0")
  private BigDecimal rechargeMaxAmount;
  /** 累计消费达标获卡金额 */
  @ApiModelProperty(value = "累计消费达标获卡金额", required = true)
  @NotNull(message = "累计消费达标获卡金额不能为空！")
  @Min(value = 0, message = "累计消费达标获卡金额不能小于0")
  private BigDecimal totalAmount;
  /** 差额补齐获卡金额 */
  @ApiModelProperty(value = "差额补齐获卡金额", required = true)
  @NotNull(message = "差额补齐获卡金额不能为空！")
  @Min(value = 0, message = "差额补齐获卡金额不能小于0")
  private BigDecimal rechargeSubAmount;
  /** 会员等级对应充值起充额 */
  @ApiModelProperty(value = "会员等级对应充值起充额", required = true)
  @NotNull(message = "会员等级对应充值起充额不能为空！")
  @Min(value = 0, message = "会员等级对应充值起充额不能小于0")
  private BigDecimal rechargeMinAmount;
  /** 年限 */
  @ApiModelProperty(value = "会员卡名续费有效年限", required = true)
  @NotNull(message = "会员卡名续费有效年限不能为空!")
  @Min(value = 1, message = "会员卡名续费有效年限不能小于0")
  private Integer ageLimit;
  /** 折扣率（价目表自动调价的折扣率） */
  @ApiModelProperty(value = "会员卡折扣率", required = true)
  @NotNull(message = "会员卡折扣率不能为空！")
  @Min(value = 0, message = "会员卡折扣率不能小于0")
  @Max(value = 100, message = "会员卡折扣率不能大于100%")
  private Float rate;
  /** 图标 */
  @ApiModelProperty(value = "会员卡图标", required = true)
  @NotNull(message = "会员卡图标不能为空！")
  private String icon;
  /** 会员卡图片获取码 */
  @ApiModelProperty(value = "会员卡图片获取码", required = true)
  @NotBlank(message = "会员卡图片样式不能为空！")
  private String pictureCode;
  /** 会员卡描述（青藤、银藤、金藤、艾维会员） */
  @ApiModelProperty("描述")
  private String description;
}
