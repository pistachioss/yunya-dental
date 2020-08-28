package com.yunya.models.clinic_base;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "specialized_subject_projec")
public class SpecializedSubjectProject {
    /**
     * 主键id
     */
    @Id
    private Integer id;

    /**
     * 专科项目名称
     */
    @Column(name = "specialized_subject_project_name")
    private String specializedSubjectProjectName;

    /**
     * 子项目
     */
    private String subitems;

    /**
     * 是否删除 0存在 1删除
     */
    private Integer inservice;

    @Column(name = "crt_id")
    private Integer crtId;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键id
     *
     * @return id - 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取专科项目名称
     *
     * @return Specialized_subject_projec_name - 专科项目名称
     */
    public String getSpecializedSubjectProjectName() {
        return specializedSubjectProjectName;
    }

    /**
     * 设置专科项目名称
     *
     * @param specializedSubjectProjectName 专科项目名称
     */
    public void setSpecializedSubjectProjectName(String specializedSubjectProjectName) {
        this.specializedSubjectProjectName = specializedSubjectProjectName;
    }

    /**
     * 获取子项目
     *
     * @return subitems - 子项目
     */
    public String getSubitems() {
        return subitems;
    }

    /**
     * 设置子项目
     *
     * @param subitems 子项目
     */
    public void setSubitems(String subitems) {
        this.subitems = subitems;
    }

    /**
     * 获取是否删除 0存在 1删除
     *
     * @return inservice - 是否删除 0存在 1删除
     */
    public Integer getInservice() {
        return inservice;
    }

    /**
     * 设置是否删除 0存在 1删除
     *
     * @param inservice 是否删除 0存在 1删除
     */
    public void setInservice(Integer inservice) {
        this.inservice = inservice;
    }

    /**
     * @return crt_id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * @param crtId
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * @return crt_time
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * @param crtTime
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
     * @return upd_time
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * @param updTime
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}