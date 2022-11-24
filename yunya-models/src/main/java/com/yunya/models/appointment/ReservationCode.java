package com.yunya.models.appointment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "reservation_code")
public class ReservationCode {
    @Id
    private String code;

    /**
     * 状态(默认0新建) 0：新建；1：预约；2：已激活；
     */
    @Column(name = "code_status")
    private Byte codeStatus;

    /**
     * 有效开始日期，包含
     */
    @Column(name = "code_start_date")
    private Date codeStartDate;

    /**
     * 有效结束日期，包含
     */
    @Column(name = "code_end_date")
    private Date codeEndDate;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    private Boolean inservice;

    /**
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取状态(默认0新建) 0：新建；1：预约；2：已激活；
     *
     * @return code_status - 状态(默认0新建) 0：新建；1：预约；2：已激活；
     */
    public Byte getCodeStatus() {
        return codeStatus;
    }

    /**
     * 设置状态(默认0新建) 0：新建；1：预约；2：已激活；
     *
     * @param codeStatus 状态(默认0新建) 0：新建；1：预约；2：已激活；
     */
    public void setCodeStatus(Byte codeStatus) {
        this.codeStatus = codeStatus;
    }

    /**
     * 获取有效开始日期，包含
     *
     * @return code_start_date - 有效开始日期，包含
     */
    public Date getCodeStartDate() {
        return codeStartDate;
    }

    /**
     * 设置有效开始日期，包含
     *
     * @param codeStartDate 有效开始日期，包含
     */
    public void setCodeStartDate(Date codeStartDate) {
        this.codeStartDate = codeStartDate;
    }

    /**
     * 获取有效结束日期，包含
     *
     * @return code_end_date - 有效结束日期，包含
     */
    public Date getCodeEndDate() {
        return codeEndDate;
    }

    /**
     * 设置有效结束日期，包含
     *
     * @param codeEndDate 有效结束日期，包含
     */
    public void setCodeEndDate(Date codeEndDate) {
        this.codeEndDate = codeEndDate;
    }

    /**
     * 获取是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @return inservice - 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * 设置是否有效，是否删除(默认有效) 1-有效；0删除
     *
     * @param inservice 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
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
     * 获取更新人名称
     *
     * @return upd_name - 更新人名称
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新人名称
     *
     * @param updName 更新人名称
     */
    public void setUpdName(String updName) {
        this.updName = updName;
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