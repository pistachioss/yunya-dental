package com.yunya.models.patient_central;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_fans")
@Data
public class WxFans {
    @Id
    private Integer id;

    /**
     * 注册人姓名
     */
    @Column(name = "register_name")
    private String registerName;

    /**
     * 注册人手机号
     */
    @Column(name = "register_mobile")
    private String registerMobile;

    /**
     * 用户标识码
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 粉丝昵称
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 性别：1男性，2女性，0未知
     */
    private Short sex;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 语言，简体中文为zh_CN
     */
    private String language;

    /**
     * 头像
     */
    @Column(name = "head_imgurl")
    private String headImgurl;

    /**
     * 是否订阅该公众号
     */
    private Boolean subscribe;

    /**
     * 关注时间
     */
    @Column(name = "subscribe_time")
    private Date subscribeTime;

    /**
     * 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    @Column(name = "union_id")
    private String unionId;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 所在的分组ID
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 标签ID列表
     */
    @Column(name = "tagid_list")
    private String tagidList;

    /**
     * 是否绑定该公司患者
     */
    private Boolean bind;

    /**
     * 绑定时间
     */
    @Column(name = "bind_time")
    private Date bindTime;

    /**
     * 授权码
     */
    private String code;

    /**
     * 微信用户登录token
     */
    @Column(name = "access_token")
    private String accessToken;

    /**
     * 卡主患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 上次沟通的客服ID
     */
    @Column(name = "lastchatkf_id")
    private String lastchatkfId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;
}