package com.yunya.modules.system.vo.tree;

import com.yunya.framework.common.model.TreeNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 系统菜单树VO类
 *
 * @author: chow
 * @date: 2020/6/19 13:49
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class SysMenuTreeVO extends TreeNode implements Serializable {
    /** 系统id */
    private Integer systemId;
    /** 菜单终端类型（0-web;1-app） */
    private Byte menuType;
    /** 路径编码 */
    private String code;
    /** 标题 */
    private String title;
    /** 资源路径 */
    private String href;
    /** 图标 */
    private String icon;
    /** 菜单类型 */
    private String type;
    /** 描述 */
    private String description;
    /** 菜单上下级关系 */
    private String path;
}
