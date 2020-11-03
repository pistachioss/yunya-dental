package com.yunya.modules.discount.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author 杨柳絮
 * @className CouponAllocateDetailForm
 * @description
 * @date 2020/9/1 18:08
 */
@Data
public class CouponAllocateDetailForm {

    /**
     * 卡券ID
     */
    @NotNull(message = "卡券ID不能为空")
    @ApiModelProperty("卡券ID")
    private Integer couponId;
    /**
     * 配给时间
     */
    @ApiModelProperty("配给时间不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date crtTime;
}
