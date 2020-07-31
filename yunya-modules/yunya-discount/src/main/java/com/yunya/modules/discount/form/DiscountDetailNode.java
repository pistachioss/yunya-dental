package com.yunya.modules.discount.form;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-13 17:25
 */
@Data
public class DiscountDetailNode implements Serializable {
    private Integer categoryId;
    private List<Integer> tariffIds;
}
