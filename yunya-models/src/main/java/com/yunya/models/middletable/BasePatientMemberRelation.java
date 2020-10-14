package com.yunya.models.middletable;

import javax.persistence.*;

@Table(name = "base_patient_member_relation")
public class BasePatientMemberRelation {
    /**
     * 关系ID
     */
    @Column(name = "relation_id")
    private Integer relationId;

    /**
     * 主卡人ID
     */
    @Column(name = "master_card_id")
    private Integer masterCardId;

    /**
     * 副卡人ID
     */
    @Column(name = "secondary_card_id")
    private Integer secondaryCardId;

    /**
     * 卡类型(0：会员卡；１：预付款)
     */
    private Byte type;

    /**
     * 关联类型(0-权益；1-余额)
     */
    @Column(name = "bind_type")
    private Byte bindType;

    /**
     * 获取关系ID
     *
     * @return relation_id - 关系ID
     */
    public Integer getRelationId() {
        return relationId;
    }

    /**
     * 设置关系ID
     *
     * @param relationId 关系ID
     */
    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    /**
     * 获取主卡人ID
     *
     * @return master_card_id - 主卡人ID
     */
    public Integer getMasterCardId() {
        return masterCardId;
    }

    /**
     * 设置主卡人ID
     *
     * @param masterCardId 主卡人ID
     */
    public void setMasterCardId(Integer masterCardId) {
        this.masterCardId = masterCardId;
    }

    /**
     * 获取副卡人ID
     *
     * @return secondary_card_id - 副卡人ID
     */
    public Integer getSecondaryCardId() {
        return secondaryCardId;
    }

    /**
     * 设置副卡人ID
     *
     * @param secondaryCardId 副卡人ID
     */
    public void setSecondaryCardId(Integer secondaryCardId) {
        this.secondaryCardId = secondaryCardId;
    }

    /**
     * 获取卡类型(0：会员卡；１：预付款)
     *
     * @return type - 卡类型(0：会员卡；１：预付款)
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置卡类型(0：会员卡；１：预付款)
     *
     * @param type 卡类型(0：会员卡；１：预付款)
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取关联类型(0-权益；1-余额)
     *
     * @return bind_type - 关联类型(0-权益；1-余额)
     */
    public Byte getBindType() {
        return bindType;
    }

    /**
     * 设置关联类型(0-权益；1-余额)
     *
     * @param bindType 关联类型(0-权益；1-余额)
     */
    public void setBindType(Byte bindType) {
        this.bindType = bindType;
    }
}