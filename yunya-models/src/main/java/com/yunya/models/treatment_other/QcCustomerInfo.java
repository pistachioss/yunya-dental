package com.yunya.models.treatment_other;

import javax.persistence.*;

@Table(name = "qc_customer_info")
public class QcCustomerInfo {
    /**
     * 全程就诊记录id
     */
    @Id
    @Column(name = "qc_treatment_id")
    private Integer qcTreatmentId;

    /**
     * 证件号码
     */
    @Column(name = "id_card")
    private String idCard;

    /**
     * 证件类型
     */
    @Column(name = "id_card_type")
    private Integer idCardType;

    /**
     * 证件类型名称
     */
    @Column(name = "id_card_type_desc")
    private String idCardTypeDesc;

    /**
     * 客户姓名
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 性别：
     */
    private Byte sex;

    /**
     * 获取全程就诊记录id
     *
     * @return
     */
    public Integer getQcTreatmentId() {
        return qcTreatmentId;
    }

    /**
     * 设置全程就诊记录id
     *
     * @param qcTreatmentId
     */
    public void setQcTreatmentId(Integer qcTreatmentId) {
        this.qcTreatmentId = qcTreatmentId;
    }

    /**
     * 获取证件号码
     *
     * @return id_card - 证件号码
     */
    public String getIdCard() {
        return idCard;
    }

    /**
     * 设置证件号码
     *
     * @param idCard 证件号码
     */
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * 获取证件类型
     *
     * @return id_card_type - 证件类型
     */
    public Integer getIdCardType() {
        return idCardType;
    }

    /**
     * 设置证件类型
     *
     * @param idCardType 证件类型
     */
    public void setIdCardType(Integer idCardType) {
        this.idCardType = idCardType;
    }

    /**
     * 获取证件类型名称
     *
     * @return id_card_type_desc - 证件类型名称
     */
    public String getIdCardTypeDesc() {
        return idCardTypeDesc;
    }

    /**
     * 设置证件类型名称
     *
     * @param idCardTypeDesc 证件类型名称
     */
    public void setIdCardTypeDesc(String idCardTypeDesc) {
        this.idCardTypeDesc = idCardTypeDesc;
    }

    /**
     * 获取客户姓名
     *
     * @return customer_name - 客户姓名
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 设置客户姓名
     *
     * @param customerName 客户姓名
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 获取手机号码
     *
     * @return mobile - 手机号码
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号码
     *
     * @param mobile 手机号码
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取生日
     *
     * @return birthday - 生日
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * 设置生日
     *
     * @param birthday 生日
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * 获取性别：
     *
     * @return sex - 性别：
     */
    public Byte getSex() {
        return sex;
    }

    /**
     * 设置性别：
     *
     * @param sex 性别：
     */
    public void setSex(Byte sex) {
        this.sex = sex;
    }
}