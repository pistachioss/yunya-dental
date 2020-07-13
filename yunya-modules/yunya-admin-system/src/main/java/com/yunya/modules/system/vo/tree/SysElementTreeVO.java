package com.yunya.modules.system.vo.tree;

import com.yunya.framework.common.model.TreeNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author user
 * @date 2020-07-02 10:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class SysElementTreeVO extends TreeNode implements Serializable {
    /** 资源编码 */
    private String code;
    /** 资源类型 */
    private String type;
    /** 资源名称 */
    private String name;
    /** 资源路径 */
    private String uri;
    /** 资源关联菜单 */
    private String menuId;
    /** 资源树状检索路径 */
    private String path;
    /** 资源请求类型 */
    private String method;
    /** 描述 */
    private String description;
    /** 是否启用 */
    private Boolean inservice;
}
