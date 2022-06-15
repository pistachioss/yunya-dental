package com.yunya.feign.ivy_mini.domain.bo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
public class ProductBO {
    private Integer productType;
    private Integer productId;
    private String productName;
    private String productPic;
    private String productCode;
    private Integer stock;
    private BigDecimal productPrice;
    private Integer soldQuantity;
    private Integer categoryId;
    private String categoryName;

}
