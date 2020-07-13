package com.yunya.modules.system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel("系统功能按钮模型")
@Table(name = "sys_element")
public class SysElement {
    @Id
    @ApiModelProperty(hidden = true)
    private String id;

    /**
     * 资源编码
     */
    @ApiModelProperty("数据权限编码")
    @NotNull(message = "数据资源权限编码为空")
    private String code;

    /**
     * 资源类型 页面列表-uri;页面按钮-button
     */
    @ApiModelProperty("资源类型(页面列表-uri;页面按钮-button)")
    private String type;

    /**
     * 资源名称
     */
    @ApiModelProperty("资源名称")
    @NotNull(message = "资源名称不能为空")
    private String name;

    /**
     * 资源路径
     */
    @ApiModelProperty("资源路径")
    @NotNull(message = "资源路径不能为空")
    private String uri;

    /**
     * 资源关联菜单
     */
    @ApiModelProperty("资源关联菜单")
    @NotNull(message = "菜单ID为空")
    @Column(name = "menu_id")
    private Integer menuId;

    @ApiModelProperty("功能父级ID")
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 资源树状检索路径
     */
    @ApiModelProperty("资源树状检索路径")
    private String path;

    /**
     * 资源请求类型
     */
    @ApiModelProperty("资源请求类型get/post/put/delete")
    private String method;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "crt_time")
    private Date crtTime;

    @ApiModelProperty(hidden = true)
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改时间
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取资源编码
     *
     * @return code - 资源编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置资源编码
     *
     * @param code 资源编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取资源类型
     *
     * @return type - 资源类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置资源类型
     *
     * @param type 资源类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取资源名称
     *
     * @return name - 资源名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置资源名称
     *
     * @param name 资源名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取资源路径
     *
     * @return uri - 资源路径
     */
    public String getUri() {
        return uri;
    }

    /**
     * 设置资源路径
     *
     * @param uri 资源路径
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 获取资源关联菜单
     *
     * @return menu_id - 资源关联菜单
     */
    public Integer getMenuId() {
        return menuId;
    }

    /**
     * 设置资源关联菜单
     *
     * @param menuId 资源关联菜单
     */
    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    /**
     * @return parent_id
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * @param parentId
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取资源树状检索路径
     *
     * @return path - 资源树状检索路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置资源树状检索路径
     *
     * @param path 资源树状检索路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取资源请求类型
     *
     * @return method - 资源请求类型
     */
    public String getMethod() {
        return method;
    }

    /**
     * 设置资源请求类型
     *
     * @param method 资源请求类型
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取描述
     *
     * @return description - 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取是否启用
     *
     * @return inservice - 是否启用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否启用
     *
     * @param inservice 是否启用
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}