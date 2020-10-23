package com.yunya.modules.discount.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className RechargeCardForm
 * @description
 * @date 2020/8/28 12:55
 */
@Data
public class RechargeCardForm {
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

    //------

    /**
     * 面值
     */
    @ApiModelProperty("面值")
    @NotNull(message = "面值不能为空")
    private BigDecimal faceValue;

    /**
     * 赠金
     */
    @ApiModelProperty("赠金")
    private BigDecimal bonus;

    /**
     * 充值截止时间
     */
    @ApiModelProperty("充值截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date rechargeDeadline;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否分配")
    private Boolean isDistribution = true;
}
