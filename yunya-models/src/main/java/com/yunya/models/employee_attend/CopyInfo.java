package com.yunya.models.employee_attend;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "copy_info")
public class CopyInfo {
    @Id
    private Integer id;

    /**
     * 申请类型对应表的Id
     */
    @Column(name = "apply_id")
    private Integer applyId;

    /**
     * 申请类型 0：请假 1：加班 2：外勤
     */
    @Column(name = "apply_type")
    private Integer applyType;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 是否已读
     */
    @Column(name = "had_read")
    private Boolean hadRead;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
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
     * 获取申请类型对应表的Id
     *
     * @return apply_id - 申请类型对应表的Id
     */
    public Integer getApplyId() {
        return applyId;
    }

    /**
     * 设置申请类型对应表的Id
     *
     * @param applyId 申请类型对应表的Id
     */
    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    /**
     * 获取申请类型 0：请假 1：加班 2：外勤
     *
     * @return apply_type - 申请类型 0：请假 1：加班 2：外勤
     */
    public Integer getApplyType() {
        return applyType;
    }

    /**
     * 设置申请类型 0：请假 1：加班 2：外勤
     *
     * @param applyType 申请类型 0：请假 1：加班 2：外勤
     */
    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getHadRead() {
        return hadRead;
    }

    public void setHadRead(Boolean hadRead) {
        this.hadRead = hadRead;
    }

    /**
     * 获取创建人
     *
     * @return crt_id - 创建人
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人
     *
     * @param crtId 创建人
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
     * 获取更新人
     *
     * @return upd_id - 更新人
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人
     *
     * @param updId 更新人
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新时间
     *
     * @return upd_time - 更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新时间
     *
     * @param updTime 更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}