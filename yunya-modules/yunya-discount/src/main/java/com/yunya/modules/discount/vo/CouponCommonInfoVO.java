package com.yunya.modules.discount.vo;

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

    private Date crtTime;

    private String pName;

    private String path;
}
