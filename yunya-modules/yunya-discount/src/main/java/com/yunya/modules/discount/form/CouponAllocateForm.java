package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className CouponAllocateForm
 * @description
 * @date 2020/8/21 10:30
 */
@Data
public class CouponAllocateForm {

    private Integer id;

    /**
     * 优惠券Id
     */
    @ApiModelProperty("优惠券Id")
    @NotNull(message = "优惠券Id不能为空")
    private Integer couponId;

    /**
     * 组织Id
     */
    @ApiModelProperty("组织Id")
    @NotNull(message = "组织Id不能为空")
    private Integer orgId;

    /**
     * 配给数量
     */
    @ApiModelProperty("配给数量")
    private Integer allocateNum = 0;

    /**
     * 配给时间
     */
    @ApiModelProperty("配给时间")
    private Date allocateDate;

    /**
     * 分配人Id
     */
    @ApiModelProperty("分配人Id")
    private Integer allocateUserId;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date crtTime;

    /**
     * 更新人
     */
    @ApiModelProperty("更新人")
    private Integer updId;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updTime;
}
