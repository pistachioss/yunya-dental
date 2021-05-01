package com.yunya.models.patient_central;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_fans")
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

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public String getRegisterMobile() {
        return registerMobile;
    }

    public void setRegisterMobile(String registerMobile) {
        this.registerMobile = registerMobile;
    }

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
     * 获取用户标识码
     *
     * @return open_id - 用户标识码
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置用户标识码
     *
     * @param openId 用户标识码
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取粉丝昵称
     *
     * @return nick_name - 粉丝昵称
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 设置粉丝昵称
     *
     * @param nickName 粉丝昵称
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 获取性别：1男性，2女性，0未知
     *
     * @return sex - 性别：1男性，2女性，0未知
     */
    public Short getSex() {
        return sex;
    }

    /**
     * 设置性别：1男性，2女性，0未知
     *
     * @param sex 性别：1男性，2女性，0未知
     */
    public void setSex(Short sex) {
        this.sex = sex;
    }

    /**
     * 获取所在国家
     *
     * @return country - 所在国家
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置所在国家
     *
     * @param country 所在国家
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 获取所在省份
     *
     * @return province - 所在省份
     */
    public String getProvince() {
        return province;
    }

    /**
     * 设置所在省份
     *
     * @param province 所在省份
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * 获取所在城市
     *
     * @return city - 所在城市
     */
    public String getCity() {
        return city;
    }

    /**
     * 设置所在城市
     *
     * @param city 所在城市
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 获取语言，简体中文为zh_CN
     *
     * @return language - 语言，简体中文为zh_CN
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 设置语言，简体中文为zh_CN
     *
     * @param language 语言，简体中文为zh_CN
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * 获取头像
     *
     * @return head_imgurl - 头像
     */
    public String getHeadImgurl() {
        return headImgurl;
    }

    /**
     * 设置头像
     *
     * @param headImgurl 头像
     */
    public void setHeadImgurl(String headImgurl) {
        this.headImgurl = headImgurl;
    }

    /**
     * 获取是否订阅该公众号
     *
     * @return subscribe - 是否订阅该公众号
     */
    public Boolean getSubscribe() {
        return subscribe;
    }

    /**
     * 设置是否订阅该公众号
     *
     * @param subscribe 是否订阅该公众号
     */
    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    /**
     * 获取关注时间
     *
     * @return subscribe_time - 关注时间
     */
    public Date getSubscribeTime() {
        return subscribeTime;
    }

    /**
     * 设置关注时间
     *
     * @param subscribeTime 关注时间
     */
    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    /**
     * 获取只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     *
     * @return union_id - 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    public String getUnionId() {
        return unionId;
    }

    /**
     * 设置只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     *
     * @param unionId 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     */
    public void setUnionId(String unionId) {
        this.unionId = unionId;
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
     * 获取所在的分组ID
     *
     * @return group_id - 所在的分组ID
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * 设置所在的分组ID
     *
     * @param groupId 所在的分组ID
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * 获取标签ID列表
     *
     * @return tagid_list - 标签ID列表
     */
    public String getTagidList() {
        return tagidList;
    }

    /**
     * 设置标签ID列表
     *
     * @param tagidList 标签ID列表
     */
    public void setTagidList(String tagidList) {
        this.tagidList = tagidList;
    }

    /**
     * 获取是否绑定该公司患者
     *
     * @return bind - 是否绑定该公司患者
     */
    public Boolean getBind() {
        return bind;
    }

    /**
     * 设置是否绑定该公司患者
     *
     * @param bind 是否绑定该公司患者
     */
    public void setBind(Boolean bind) {
        this.bind = bind;
    }

    /**
     * 获取绑定时间
     *
     * @return bind_time - 绑定时间
     */
    public Date getBindTime() {
        return bindTime;
    }

    /**
     * 设置绑定时间
     *
     * @param bindTime 绑定时间
     */
    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    /**
     * 获取授权码
     *
     * @return code - 授权码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置授权码
     *
     * @param code 授权码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取微信用户登录token
     *
     * @return access_token - 微信用户登录token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置微信用户登录token
     *
     * @param accessToken 微信用户登录token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取卡主患者ID
     *
     * @return patient_id - 卡主患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置卡主患者ID
     *
     * @param patientId 卡主患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取上次沟通的客服ID
     *
     * @return lastchatkf_id - 上次沟通的客服ID
     */
    public String getLastchatkfId() {
        return lastchatkfId;
    }

    /**
     * 设置上次沟通的客服ID
     *
     * @param lastchatkfId 上次沟通的客服ID
     */
    public void setLastchatkfId(String lastchatkfId) {
        this.lastchatkfId = lastchatkfId;
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