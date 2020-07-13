package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 * 品牌VO类
 * @author: chow
 * @date: 2020/5/28 14:45
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class BrandVO implements Serializable {

    private Integer id;

    /**
     * 品牌
     */
    private String name;

    /**
     * 自定义排序
     */
    private Integer orderNum;

    /**
     * 是否启用
     */
    private Boolean inservice;
}