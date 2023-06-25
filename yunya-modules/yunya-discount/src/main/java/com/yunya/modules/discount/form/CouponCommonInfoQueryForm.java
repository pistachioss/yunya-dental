package com.yunya.modules.discount.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className CouponCommonInfoQueryForm
 * @description
 * @date 2020/8/21 14:06
 */
@Data
public class CouponCommonInfoQueryForm {
    /**
     * 主键
     */
    @ApiModelProperty("卡券Id")
    private Integer id;

    /**
     * 产品分类ID
     */
    @ApiModelProperty("产品分类ID")
    private Integer productTypeId;

    /**
     * 卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券）
     */
    @ApiModelProperty("卡券类型（0-代金券；1-折扣券；2-兑换券；3-套餐券；4-充值券；4-划扣券）")
    @NotNull(message = "卡券类型不能为空")
    private Integer type;

    /**
     * 卡券名称
     */
    @ApiModelProperty("卡券名称")
    private String name;

    /**
     * 时间范围起始
     */
    @ApiModelProperty("时间范围起始")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /**
     * 时间范围结束
     */
    @ApiModelProperty("时间范围结束")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    /**
     * 是否线上售卖(0:否 1:是)
     */
    @ApiModelProperty("是否线上售卖(0:否 1:是)")
    private Boolean isOnlineSale;

}
