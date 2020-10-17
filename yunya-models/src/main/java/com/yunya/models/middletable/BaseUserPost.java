package com.yunya.models.middletable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "base_user_post")
public class BaseUserPost {
    /**
     * 用户可登陆组织ID
     */
    @Id
    @Column(name = "user_post_id")
    private Integer userPostId;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 组织ID
     */
    @Column(name = "org_id")
    private Integer orgId;

    /**
     * 角色组ID(岗位组)
     */
    @Column(name = "group_id")
    private Integer groupId;

    /**
     * 岗位组名称
     */
    @Column(name = "post_group_name")
    private String postGroupName;

    /**
     * 岗位ID
     */
    @Column(name = "post_id")
    private Integer postId;

    /**
     * 岗位名称
     */
    @Column(name = "post_name")
    private String postName;

    /**
     * 是否可预约
     */
    @Column(name = "enable_appoint")
    private Boolean enableAppoint;

    /**
     * 是否可挂号
     */
    @Column(name = "enable_registry")
    private Boolean enableRegistry;

    /**
     * 获取用户可登陆组织ID
     *
     * @return user_post_id - 用户可登陆组织ID
     */
    public Integer getUserPostId() {
        return userPostId;
    }

    /**
     * 设置用户可登陆组织ID
     *
     * @param userPostId 用户可登陆组织ID
     */
    public void setUserPostId(Integer userPostId) {
        this.userPostId = userPostId;
    }

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取组织ID
     *
     * @return org_id - 组织ID
     */
    public Integer getOrgId() {
        return orgId;
    }

    /**
     * 设置组织ID
     *
     * @param orgId 组织ID
     */
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    /**
     * 获取角色组ID(岗位组)
     *
     * @return group_id - 角色组ID(岗位组)
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * 设置角色组ID(岗位组)
     *
     * @param groupId 角色组ID(岗位组)
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * 获取岗位组名称
     *
     * @return post_group_name - 岗位组名称
     */
    public String getPostGroupName() {
        return postGroupName;
    }

    /**
     * 设置岗位组名称
     *
     * @param postGroupName 岗位组名称
     */
    public void setPostGroupName(String postGroupName) {
        this.postGroupName = postGroupName;
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
     * 获取岗位名称
     *
     * @return post_name - 岗位名称
     */
    public String getPostName() {
        return postName;
    }

    /**
     * 设置岗位名称
     *
     * @param postName 岗位名称
     */
    public void setPostName(String postName) {
        this.postName = postName;
    }

    /**
     * 获取是否可预约
     *
     * @return enable_appoint - 是否可预约
     */
    public Boolean getEnableAppoint() {
        return enableAppoint;
    }

    /**
     * 设置是否可预约
     *
     * @param enableAppoint 是否可预约
     */
    public void setEnableAppoint(Boolean enableAppoint) {
        this.enableAppoint = enableAppoint;
    }

    /**
     * 获取是否可挂号
     *
     * @return enable_registry - 是否可挂号
     */
    public Boolean getEnableRegistry() {
        return enableRegistry;
    }

    /**
     * 设置是否可挂号
     *
     * @param enableRegistry 是否可挂号
     */
    public void setEnableRegistry(Boolean enableRegistry) {
        this.enableRegistry = enableRegistry;
    }
}