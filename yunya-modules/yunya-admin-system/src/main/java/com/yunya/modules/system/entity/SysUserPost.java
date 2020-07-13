package com.yunya.modules.system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel("用户可登陆组织")
@Table(name = "sys_user_post")
public class SysUserPost {
    @ApiModelProperty(hidden = true)
    @Id
    private Integer id;

    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "可登录组织ID",required = true)
    @NotNull(message = "组织ID不能为空")
    @Column(name = "company_id")
    private Integer companyId;

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "组织部门ID", required = true)
    @NotNull(message = "组织部门ID不能为空")
    @Column(name = "department_id")
    private Integer departmentId;

    /**
     * 岗位ID
     */
    @ApiModelProperty(value = "岗位ID", required = true)
    @NotNull(message = "岗位ID不能为空")
    @Column(name = "post_id")
    private Integer postId;

    /**
     * 系统终端ID
     */
    @ApiModelProperty("系统终端ID")
    @Column(name = "system_id")
    private Integer systemId;

    /**
     * 角色组ID
     */
    @ApiModelProperty("岗位组ID")
    @Column(name = "group_id")
    private Integer groupId;

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

    @ApiModelProperty(hidden = true)
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
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return company_id
     */
    public Integer getCompanyId() {
        return companyId;
    }

    /**
     * @param companyId
     */
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    /**
     * 获取部门ID
     *
     * @return department_id - 部门ID
     */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置部门ID
     *
     * @param departmentId 部门ID
     */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * 获取岗位ID
     *
     * @return post_id - 岗位ID
     */
    public Integer getPostId() {
        return postId;
    }

    /**
     * 设置岗位ID
     *
     * @param postId 岗位ID
     */
    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    /**
     * 获取系统终端ID
     *
     * @return system_id - 系统终端ID
     */
    public Integer getSystemId() {
        return systemId;
    }

    /**
     * 设置系统终端ID
     *
     * @param systemId 系统终端ID
     */
    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    /**
     * 获取角色组ID
     *
     * @return group_id - 角色组ID
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * 设置角色组ID
     *
     * @param groupId 角色组ID
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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