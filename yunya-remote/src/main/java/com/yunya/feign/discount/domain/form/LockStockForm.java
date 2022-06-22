package com.yunya.feign.discount.domain.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: xy
 * @date 2022/6/17 11:24
 **/
@Data
public class LockStockForm {
    @NotNull
    private Integer productId;
    @NotNull
    private  Integer quantity;
}
