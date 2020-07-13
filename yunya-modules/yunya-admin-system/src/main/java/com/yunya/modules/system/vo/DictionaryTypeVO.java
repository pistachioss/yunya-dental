package com.yunya.modules.system.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: chow
 * @date: 2020/6/3 17:48
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
public class DictionaryTypeVO implements Serializable {
    /** 字典类型ID */
    private Integer id;
    /** 字典类型名称 */
    private String name;
    /***/
    private Boolean inservice;
}