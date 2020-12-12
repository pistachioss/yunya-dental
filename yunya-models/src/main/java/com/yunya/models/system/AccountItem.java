package com.yunya.models.system;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "account_item")
public class AccountItem {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 入账分类id
     */
    @Column(name = "account_type_id")
    private Integer accountTypeId;

    /**
     * 入账方式名称
     */
    private String name;

    /**
     * 类型,0.现金,1.预售,2.优惠,3平台结算
     */
    private Byte type;

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
     * 创建人名称
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改人名称
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 修改时间
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
     * 获取入账分类id
     *
     * @return account_type_id - 入账分类id
     */
    public Integer getAccountTypeId() {
        return accountTypeId;
    }

    /**
     * 设置入账分类id
     *
     * @param accountTypeId 入账分类id
     */
    public void setAccountTypeId(Integer accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    /**
     * 获取入账分类名称
     *
     * @return name - 入账分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置入账分类名称
     *
     * @param name 入账分类名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取类型,0.现金,1.预售,2.优惠,3平台结算
     *
     * @return type - 类型,0.现金,1.预售,2.优惠,3平台结算
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置类型,0.现金,1.预售,2.优惠,3平台结算
     *
     * @param type 类型,0.现金,1.预售,2.优惠,3平台结算
     */
    public void setType(Byte type) {
        this.type = type;
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