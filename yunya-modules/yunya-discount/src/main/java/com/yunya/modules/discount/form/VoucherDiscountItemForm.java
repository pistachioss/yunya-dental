package com.yunya.modules.discount.form;

import com.yunya.framework.common.context.BaseContextHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * @author 杨柳絮
 * @className VoucherDiscountItemForm
 * @description
 * @date 2020/8/20 15:21
 */
@Data
public class VoucherDiscountItemForm {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 优惠券id
     */
    @NotNull(message = "优惠券id不能为空")
    @ApiModelProperty("优惠券id")
    private Integer couponId;

    /**
     * 项目类型  0:基础价目表,1:基础商品表
     */
    @NotNull(message = "项目类型不能为空")
    @ApiModelProperty("项目类型  0:基础价目表,1:基础商品表")
    private Integer type;

    /**
     * 选择范围  0:全选 1:选项目分类 2:选项目明细
     */
    @NotNull(message = "选择范围不能为空")
    @ApiModelProperty("选择范围  0:全选 1:选项目分类 2:选项目明细")
    private Byte choiceRangType;

    /**
     * 明细ID
     */
    @ApiModelProperty("明细ID")
    private Integer itemId;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

}
