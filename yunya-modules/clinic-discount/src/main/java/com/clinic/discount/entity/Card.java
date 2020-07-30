package com.clinic.discount.entity;

import java.util.Date;
import javax.persistence.*;

public class Card {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 关联*_clinic的中ID
     */
    @Column(name = "relevance_id")
    private Integer relevanceId;

    /**
     * 门诊ID
     */
    @Column(name = "clinic_id")
    private Integer clinicId;

    /**
     * 卡号
     */
    @Column(name = "card_number")
    private String cardNumber;

    /**
     * 卡密
     */
    private String password;

    /**
     * 卡类型 0:代金券,1:折扣券,2:套餐券
     */
    @Column(name = "card_type")
    private Integer cardType;

    /**
     * 是否短信通知,0:不通知,1:通知
     */
    @Column(name = "sms_type")
    private Boolean smsType;

    /**
     * 出售类型 0:售卖,1:赠送:2:置换
     */
    @Column(name = "selling_type")
    private Integer sellingType;

    /**
     * 买家姓名
     */
    @Column(name = "buyer_name")
    private String buyerName;

    /**
     * 买家电话
     */
    @Column(name = "buyer_phone")
    private String buyerPhone;

    /**
     * 操作 0:取消售出,1:正常
     */
    private Boolean operate;

    /**
     * 状态 0:待售出,1:售出,2:待激活,3:已激活,4:撤回售出
     */
    private Integer status;

    /**
     * 是否收款 0:未收费,1:收费
     */
    private Boolean charge;

    /**
     * 入账方式
     */
    @Column(name = "accounting_id")
    private Integer accountingId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

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
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 乐观锁
     */
    private Integer revision;

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
     * 获取关联*_clinic的中ID
     *
     * @return relevance_id - 关联*_clinic的中ID
     */
    public Integer getRelevanceId() {
        return relevanceId;
    }

    /**
     * 设置关联*_clinic的中ID
     *
     * @param relevanceId 关联*_clinic的中ID
     */
    public void setRelevanceId(Integer relevanceId) {
        this.relevanceId = relevanceId;
    }

    /**
     * 获取门诊ID
     *
     * @return clinic_id - 门诊ID
     */
    public Integer getClinicId() {
        return clinicId;
    }

    /**
     * 设置门诊ID
     *
     * @param clinicId 门诊ID
     */
    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
    }

    /**
     * 获取卡号
     *
     * @return card_number - 卡号
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * 设置卡号
     *
     * @param cardNumber 卡号
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * 获取卡密
     *
     * @return password - 卡密
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置卡密
     *
     * @param password 卡密
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取卡类型 0:代金券,1:折扣券,2:套餐券
     *
     * @return card_type - 卡类型 0:代金券,1:折扣券,2:套餐券
     */
    public Integer getCardType() {
        return cardType;
    }

    /**
     * 设置卡类型 0:代金券,1:折扣券,2:套餐券
     *
     * @param cardType 卡类型 0:代金券,1:折扣券,2:套餐券
     */
    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    /**
     * 获取是否短信通知,0:不通知,1:通知
     *
     * @return sms_type - 是否短信通知,0:不通知,1:通知
     */
    public Boolean getSmsType() {
        return smsType;
    }

    /**
     * 设置是否短信通知,0:不通知,1:通知
     *
     * @param smsType 是否短信通知,0:不通知,1:通知
     */
    public void setSmsType(Boolean smsType) {
        this.smsType = smsType;
    }

    /**
     * 获取出售类型 0:售卖,1:赠送:2:置换
     *
     * @return selling_type - 出售类型 0:售卖,1:赠送:2:置换
     */
    public Integer getSellingType() {
        return sellingType;
    }

    /**
     * 设置出售类型 0:售卖,1:赠送:2:置换
     *
     * @param sellingType 出售类型 0:售卖,1:赠送:2:置换
     */
    public void setSellingType(Integer sellingType) {
        this.sellingType = sellingType;
    }

    /**
     * 获取买家姓名
     *
     * @return buyer_name - 买家姓名
     */
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * 设置买家姓名
     *
     * @param buyerName 买家姓名
     */
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    /**
     * 获取买家电话
     *
     * @return buyer_phone - 买家电话
     */
    public String getBuyerPhone() {
        return buyerPhone;
    }

    /**
     * 设置买家电话
     *
     * @param buyerPhone 买家电话
     */
    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    /**
     * 获取操作 0:取消售出,1:正常
     *
     * @return operate - 操作 0:取消售出,1:正常
     */
    public Boolean getOperate() {
        return operate;
    }

    /**
     * 设置操作 0:取消售出,1:正常
     *
     * @param operate 操作 0:取消售出,1:正常
     */
    public void setOperate(Boolean operate) {
        this.operate = operate;
    }

    /**
     * 获取状态 0:待售出,1:售出,2:待激活,3:已激活,4:撤回售出
     *
     * @return status - 状态 0:待售出,1:售出,2:待激活,3:已激活,4:撤回售出
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态 0:待售出,1:售出,2:待激活,3:已激活,4:撤回售出
     *
     * @param status 状态 0:待售出,1:售出,2:待激活,3:已激活,4:撤回售出
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取是否收款 0:未收费,1:收费
     *
     * @return charge - 是否收款 0:未收费,1:收费
     */
    public Boolean getCharge() {
        return charge;
    }

    /**
     * 设置是否收款 0:未收费,1:收费
     *
     * @param charge 是否收款 0:未收费,1:收费
     */
    public void setCharge(Boolean charge) {
        this.charge = charge;
    }

    /**
     * 获取入账方式
     *
     * @return accounting_id - 入账方式
     */
    public Integer getAccountingId() {
        return accountingId;
    }

    /**
     * 设置入账方式
     *
     * @param accountingId 入账方式
     */
    public void setAccountingId(Integer accountingId) {
        this.accountingId = accountingId;
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
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
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
     * @return upd_name - 更新时间
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新时间
     *
     * @param updName 更新时间
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

    /**
     * 获取乐观锁
     *
     * @return revision - 乐观锁
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * 设置乐观锁
     *
     * @param revision 乐观锁
     */
    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}