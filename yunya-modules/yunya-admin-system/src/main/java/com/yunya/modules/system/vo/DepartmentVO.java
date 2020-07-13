package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: chow
 * @date: 2020/5/29 13:56
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class DepartmentVO implements Serializable {
    /**部门ID*/
    private Integer id;
    /**部门名称*/
    private String name;
    /**部门类型*/
    private Byte type;
    /**自定义部门排序*/
    private Integer orderNum;
    /**是否启用*/
    private Boolean inservice;
}