package com.yunya.feign.tariff.domain.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/5 17:15
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BaseTariffImportModel implements Serializable {

    /** 价目表编码 */
    private String itemNumber;

    /** 项目名称 */
    private String name;

    /** 项目分类编号 */
    private String tariffCategoryNumber;

    /** 项目分类名称 */
    private String tariffCategoryName;

    /** 英文名称 */
    private String englishName;

    /** 单位 */
    private String unit;

    /** 价格 */
    private BigDecimal price;

}