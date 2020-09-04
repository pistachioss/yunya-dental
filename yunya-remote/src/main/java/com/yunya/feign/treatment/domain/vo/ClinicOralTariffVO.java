package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 简介: 门诊商品项目信息V0
 *
 * @author: chow
 * @date: 2020/8/6 12:54
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class ClinicOralTariffVO implements Serializable {
    /** 门诊商品项目ID */
    private Integer id;

    /** 诊所ID */
    private Integer orgId;

    /** 商品分类ID */
    private Integer oralTariffCategoryId;

    /** 商品分类名称 */
    private String oralTariffCategoryName;

    /** 商品分类编号 */
    private String oralTariffCategoryNumber;

    /** 基础商品项目ID */
    private Integer oralTariffId;

    /** 基础商品项目名称 */
    private String name;

    /** 基础商品项目名称 */
    private String englishName;

    /** 基础商品项目编号 */
    private String number;

    /** 单位 */
    private String unit;

    /** 门诊商品项目单价 */
    private BigDecimal price;

    /** 会员价 */
    private Map<Integer, Object> memberPrices;

    /** 是否启用 */
    private Boolean inservice;

}