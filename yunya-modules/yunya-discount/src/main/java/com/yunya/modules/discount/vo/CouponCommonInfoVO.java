package com.yunya.modules.discount.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author 杨柳絮
 * @className CouponCommonInfoVO
 * @description
 * @date 2020/8/20 16:58
 */
@Data
@ApiModel(value = "卡券列表VO类")
public class CouponCommonInfoVO {

    private Integer id;

    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date crtTime;

    private String pName;

    private String path;

    private Boolean isShare;

    @ApiModelProperty("是否启用")
    private Boolean isInservice;

    /**
     * 是否线上售卖(0:否 1:是)
     */
    @ApiModelProperty("是否线上售卖(0:否 1:是)")
    private Boolean isOnlineSale;
}
