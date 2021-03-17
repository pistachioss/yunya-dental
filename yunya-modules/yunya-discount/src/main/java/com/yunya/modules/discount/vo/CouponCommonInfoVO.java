package com.yunya.modules.discount.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 杨柳絮
 * @className CouponCommonInfoVO
 * @description
 * @date 2020/8/20 16:58
 */
@Data
public class CouponCommonInfoVO {

    private Integer id;

    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date crtTime;

    private String pName;

    private String path;

    private Boolean isShare;
}
