package com.yunya.models.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@ApiModel("系统菜单参数封装模型")
@Table(name = "sys_menu")
public class SysMenu {
    @ApiModelProperty(hidden = true)
    @Id
    private Integer id;

    /**
     * 系统id
     */
    @ApiModelProperty("菜单所属系统（0-管理后台接口;1-前台接口）")
    @Column(name = "system_id")
    private Integer systemId;

    /**
     * 菜单类型 web;app
     */
    @ApiModelProperty("菜单类型（0-web;1-app）")
    @Column(name = "menu_type")
    private Byte menuType;

    /**
     * 路径编码
     */
    @ApiModelProperty("路径编码")
    private String code;

    /**
     * 标题
     */
    @ApiModelProperty("菜单（目录名称）")
    private String title;

    /**
     * 父级节点
     */
    @ApiModelProperty("上级节点")
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * 资源路径
     */
    @ApiModelProperty("页面资源路径")
    private String href;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String icon;

    /**
     * 菜单类型
     */
    @ApiModelProperty(hidden = true)
    private String type;

    /**
     * 排序
     */
    @ApiModelProperty("自定义排序")
    @Column(name = "order_num")
    private Integer orderNum;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 菜单上下级关系
     */
    @ApiModelProperty("菜单上下级关系")
    private String path;

    /**
     * 启用禁用
     */
    @ApiModelProperty("启用禁用")
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人名称
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改人名称
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "upd_name")
    private String updName;

    /**
     * 修改时间
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取系统id
     *
     * @return system_id - 系统id
     */
    public Integer getSystemId() {
        return systemId;
    }

    /**
     * 设置系统id
     *
     * @param systemId 系统id
     */
    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    /**
     * 获取菜单类型
     *
     * @return menuType - 菜单类型 (0-web;1-app)
     */
    public Byte getMenuType() {
        return menuType;
    }

    /**
     * 设置菜单类型
     *
     * @param menuType 菜单类型(0-web;1-app)
     */
    public void setMenuType(Byte menuType) {
        this.menuType = menuType;
    }

    /**
     * 获取路径编码
     *
     * @return code - 路径编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置路径编码
     *
     * @param code 路径编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取标题
     *
     * @return title - 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取父级节点
     *
     * @return parent_id - 父级节点
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 设置父级节点
     *
     * @param parentId 父级节点
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取资源路径
     *
     * @return href - 资源路径
     */
    public String getHref() {
        return href;
    }

    /**
     * 设置资源路径
     *
     * @param href 资源路径
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * 获取图标
     *
     * @return icon - 图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置图标
     *
     * @param icon 图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取菜单类型
     *
     * @return type - 菜单类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置菜单类型
     *
     * @param type 菜单类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取排序
     *
     * @return order_num - 排序
     */
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 设置排序
     *
     * @param orderNum 排序
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
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
     * 获取菜单上下级关系
     *
     * @return path - 菜单上下级关系
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置菜单上下级关系
     *
     * @param path 菜单上下级关系
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取启用禁用
     *
     * @return inservice - 启用禁用
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置启用禁用
     *
     * @param inservice 启用禁用
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
     * 获取创建人名称
     *
     * @return crt_name - 创建人名称
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人名称
     *
     * @param crtName 创建人名称
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
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
     * 获取修改人名称
     *
     * @return upd_name - 修改人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置修改人名称
     *
     * @param updName 修改人名称
     */
    public void setUpdName(String updName) {
        this.updName = updName;
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