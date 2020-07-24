package com.clinic.discount.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-13 12:44
 */
@Data
@ApiModel("优惠活动")
public class DiscountVO implements Serializable {
    @ApiModelProperty("主键ID")
    private int id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("产品名称")
    private String markerProductName;
    @ApiModelProperty("封面文件")
    private String icon;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;
}
