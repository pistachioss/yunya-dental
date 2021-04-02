package com.yunya.models.patient_central;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_fans_bind")
@Data
public class WxFansBind {
    /**
     * 记录ID
     */
    @Id
    private Integer id;

    /**
     * 患者id
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * openid
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 是否绑定
     */
    private Boolean bind;

    /**
     * 绑定时间
     */
    @Column(name = "bind_time")
    private Date bindTime;

    /**
     * 是否是微信注册会员
     */
    @Column(name = "is_vip")
    private Boolean isVip;

    /**
     * 是否是微信拥有者
     */
    @Column(name = "is_owner")
    private Boolean isOwner;

    /**
     * 更新者
     */
    @Column(name = "upd_id")
    private Integer updId;
    /**
     * 绑定亲属关系
     */
    @Column(name = "dictionary_id")
    private Integer dictionaryId;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 创建者
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

}