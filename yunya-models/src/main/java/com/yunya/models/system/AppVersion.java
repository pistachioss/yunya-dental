package com.yunya.models.system;

import java.util.Date;
import javax.persistence.*;

@Table(name = "app_version")
public class AppVersion {
    /**
     * 主键
     */
    @Id
    private Integer id;

    /**
     * 设备系统(IOS,Android)
     */
    @Column(name = "os_name")
    private String osName;

    /**
     * 当前最新版本号
     */
    @Column(name = "release_version")
    private String releaseVersion;

    /**
     * 内测版本号
     */
    @Column(name = "alpha_version")
    private String alphaVersion;

    /**
     * 正在公测的版本
     */
    @Column(name = "beta_version")
    private String betaVersion;

    /**
     * 是否强制更新 0-不强制更新；1-强制更新
     */
    @Column(name = "force_update")
    private Boolean forceUpdate;

    /**
     * 下载地址
     */
    @Column(name = "download_url")
    private String downloadUrl;

    /**
     * 更新内容
     */
    @Column(name = "update_content")
    private String updateContent;

    /**
     * 创建人
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建日期
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "update_name")
    private String updateName;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

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
     * 获取设备系统(IOS,Android)
     *
     * @return os_name - 设备系统(IOS,Android)
     */
    public String getOsName() {
        return osName;
    }

    /**
     * 设置设备系统(IOS,Android)
     *
     * @param osName 设备系统(IOS,Android)
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * 获取当前最新版本号
     *
     * @return release_version - 当前最新版本号
     */
    public String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * 设置当前最新版本号
     *
     * @param releaseVersion 当前最新版本号
     */
    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

    /**
     * 获取内测版本号
     *
     * @return alpha_version - 内测版本号
     */
    public String getAlphaVersion() {
        return alphaVersion;
    }

    /**
     * 设置内测版本号
     *
     * @param alphaVersion 内测版本号
     */
    public void setAlphaVersion(String alphaVersion) {
        this.alphaVersion = alphaVersion;
    }

    /**
     * 获取正在公测的版本
     *
     * @return beta_version - 正在公测的版本
     */
    public String getBetaVersion() {
        return betaVersion;
    }

    /**
     * 设置正在公测的版本
     *
     * @param betaVersion 正在公测的版本
     */
    public void setBetaVersion(String betaVersion) {
        this.betaVersion = betaVersion;
    }

    /**
     * 获取是否强制更新 0-不强制更新；1-强制更新
     *
     * @return force_update - 是否强制更新 0-不强制更新；1-强制更新
     */
    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    /**
     * 设置是否强制更新 0-不强制更新；1-强制更新
     *
     * @param forceUpdate 是否强制更新 0-不强制更新；1-强制更新
     */
    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    /**
     * 获取下载地址
     *
     * @return download_url - 下载地址
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * 设置下载地址
     *
     * @param downloadUrl 下载地址
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    /**
     * 获取更新内容
     *
     * @return update_content - 更新内容
     */
    public String getUpdateContent() {
        return updateContent;
    }

    /**
     * 设置更新内容
     *
     * @param updateContent 更新内容
     */
    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    /**
     * 获取创建人
     *
     * @return crt_name - 创建人
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人
     *
     * @param crtName 创建人
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
    }

    /**
     * 获取创建日期
     *
     * @return crt_time - 创建日期
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建日期
     *
     * @param crtTime 创建日期
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取更新人
     *
     * @return update_name - 更新人
     */
    public String getUpdateName() {
        return updateName;
    }

    /**
     * 设置更新人
     *
     * @param updateName 更新人
     */
    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AppVersion{" +
                "id=" + id +
                ", osName='" + osName + '\'' +
                ", releaseVersion='" + releaseVersion + '\'' +
                ", alphaVersion='" + alphaVersion + '\'' +
                ", betaVersion='" + betaVersion + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", updateContent='" + updateContent + '\'' +
                ", crtName='" + crtName + '\'' +
                ", crtTime=" + crtTime +
                ", updateName='" + updateName + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}