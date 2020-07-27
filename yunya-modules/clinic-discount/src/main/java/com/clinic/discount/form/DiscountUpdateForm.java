package com.clinic.discount.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-10 9:57
 */
@Data
@ApiModel("优惠产品修改表单")
public class DiscountUpdateForm implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty("主键ID")
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 实物卡 0:非实体卡,1:实体卡
     */
    @ApiModelProperty("实物卡 0:非实体卡,1:实体卡")
    private Boolean physicalCard;

    /**
     * 营销产品分类ID
     */
    @ApiModelProperty("营销产品分类ID")
    private Integer marketProductTypeId;

    /**
     * 面值
     */
    @ApiModelProperty("面值")
    private BigDecimal faceValue;

    /**
     * 售价
     */
    @ApiModelProperty("售价")
    private BigDecimal sellingPrice;

    /**
     * 图像
     */
    @ApiModelProperty("图像")
    private String icon;

    /**
     * 文档
     */
    @ApiModelProperty("文档")
    private String doc;

    /**
     * 售出开始日期
     */
    @ApiModelProperty("售出开始日期")
    private Date sellingStartDate;

    /**
     * 售出结束日期
     */
    @ApiModelProperty("售出结束日期")
    private Date sellingEndDate;

    /**
     * 激活截至日期(产品有效期)
     */
    @ApiModelProperty("激活截至日期(产品有效期)")
    private Date activationDeadline;

    /**
     * 激活后有效期
     */
    @ApiModelProperty("激活后有效期")
    private Integer effectiveDays;

    /**
     * 账单单次使用限制数量
     */
    @ApiModelProperty("账单单次使用限制数量")
    private Integer limitCount;

    /**
     * 是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用
     */
    @ApiModelProperty("是否可混合使用优惠 0.可以混合使用1.跟会员卡混合使用2.无法混合使用")
    private Integer mixable;

    /**
     * 是否可与他人共享
     */
    @ApiModelProperty("是否可与他人共享")
    private Boolean shareable;

    /**
     * 工作量比例
     */
    @ApiModelProperty("工作量比例")
    private Integer workloadRate;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 可使用门诊
     */
    @ApiModelProperty("可使用门诊ID列表")
    private String clinicIds;
}
