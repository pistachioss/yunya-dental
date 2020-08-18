package com.yunya.models.discount;

import java.util.Date;
import javax.persistence.*;

@Table(name = "coupon_file_info")
public class CouponFileInfo {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 卡券公共信息ID
     */
    @Column(name = "coupon_id")
    private Integer couponId;

    /**
     * 文件类型（0-图片；1-文档）
     */
    @Column(name = "file_type")
    private Byte fileType;

    /**
     * 文件地址
     */
    private String path;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取卡券公共信息ID
     *
     * @return coupon_id - 卡券公共信息ID
     */
    public Integer getCouponId() {
        return couponId;
    }

    /**
     * 设置卡券公共信息ID
     *
     * @param couponId 卡券公共信息ID
     */
    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    /**
     * 获取文件类型（0-图片；1-文档）
     *
     * @return file_type - 文件类型（0-图片；1-文档）
     */
    public Byte getFileType() {
        return fileType;
    }

    /**
     * 设置文件类型（0-图片；1-文档）
     *
     * @param fileType 文件类型（0-图片；1-文档）
     */
    public void setFileType(Byte fileType) {
        this.fileType = fileType;
    }

    /**
     * 获取文件地址
     *
     * @return path - 文件地址
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置文件地址
     *
     * @param path 文件地址
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
     * 获取更新人ID
     *
     * @return upd_id - 更新人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人ID
     *
     * @param updId 更新人ID
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