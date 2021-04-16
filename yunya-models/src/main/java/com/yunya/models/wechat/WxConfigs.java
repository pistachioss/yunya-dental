package com.yunya.models.wechat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "wx_configs")
public class WxConfigs {
    /**
     * 开发者ID
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 开发者密码
     */
    @Column(name = "app_secret")
    private String appSecret;

    /**
     * 开发者微信号
     */
    private String account;

    /**
     * 服务器地址
     */
    private String url;

    /**
     * 令牌
     */
    private String token;

    /**
     * 消息加解密密钥
     */
    @Column(name = "encoding_aeskey")
    private String encodingAeskey;

    /**
     * 公众号资质认证状态
     */
    @Column(name = "qualification_verify")
    private Boolean qualificationVerify;

    /**
     * 公众号名称认证状态
     */
    @Column(name = "naming_verify")
    private Boolean namingVerify;

    /**
     * 是否需要年审
     */
    @Column(name = "annual_renew")
    private Boolean annualRenew;

    /**
     * 认证是否过期失效
     */
    @Column(name = "verify_expired")
    private Boolean verifyExpired;

    /**
     * 今日新增粉丝数
     */
    @Column(name = "today_newfans")
    private Integer todayNewfans;

    /**
     * 全部粉丝数
     */
    private Integer fans;

    /**
     * 绑定粉丝数
     */
    @Column(name = "bind_fans")
    private Integer bindFans;

    /**
     * ACCESS_TOKEN
     */
    @Column(name = "access_token")
    private String accessToken;

    /**
     * ACCESS_TOKEN过期时间
     */
    @Column(name = "expires_in")
    private Date expiresIn;

    /**
     * 客服咨询量提醒数
     */
    @Column(name = "warnchat_no")
    private Integer warnchatNo;

    /**
     * 客服忙时自动回复消息
     */
    @Column(name = "busy_auto_replaymsg")
    private String busyAutoReplaymsg;

    /**
     * 无客服在线时自动回复消息
     */
    @Column(name = "noonline_auto_replaymsg")
    private String noonlineAutoReplaymsg;

    /**
     * 欢迎语
     */
    @Column(name = "welcome_replayemsg")
    private String welcomeReplayemsg;

    /**
     * 结束语
     */
    @Column(name = "endchat_replaymsg")
    private String endchatReplaymsg;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 获取开发者ID
     *
     * @return app_id - 开发者ID
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置开发者ID
     *
     * @param appId 开发者ID
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取开发者密码
     *
     * @return app_secret - 开发者密码
     */
    public String getAppSecret() {
        return appSecret;
    }

    /**
     * 设置开发者密码
     *
     * @param appSecret 开发者密码
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    /**
     * 获取开发者微信号
     *
     * @return account - 开发者微信号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置开发者微信号
     *
     * @param account 开发者微信号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取服务器地址
     *
     * @return url - 服务器地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置服务器地址
     *
     * @param url 服务器地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取令牌
     *
     * @return token - 令牌
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置令牌
     *
     * @param token 令牌
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取消息加解密密钥
     *
     * @return encoding_aeskey - 消息加解密密钥
     */
    public String getEncodingAeskey() {
        return encodingAeskey;
    }

    /**
     * 设置消息加解密密钥
     *
     * @param encodingAeskey 消息加解密密钥
     */
    public void setEncodingAeskey(String encodingAeskey) {
        this.encodingAeskey = encodingAeskey;
    }

    /**
     * 获取公众号资质认证状态
     *
     * @return qualification_verify - 公众号资质认证状态
     */
    public Boolean getQualificationVerify() {
        return qualificationVerify;
    }

    /**
     * 设置公众号资质认证状态
     *
     * @param qualificationVerify 公众号资质认证状态
     */
    public void setQualificationVerify(Boolean qualificationVerify) {
        this.qualificationVerify = qualificationVerify;
    }

    /**
     * 获取公众号名称认证状态
     *
     * @return naming_verify - 公众号名称认证状态
     */
    public Boolean getNamingVerify() {
        return namingVerify;
    }

    /**
     * 设置公众号名称认证状态
     *
     * @param namingVerify 公众号名称认证状态
     */
    public void setNamingVerify(Boolean namingVerify) {
        this.namingVerify = namingVerify;
    }

    /**
     * 获取是否需要年审
     *
     * @return annual_renew - 是否需要年审
     */
    public Boolean getAnnualRenew() {
        return annualRenew;
    }

    /**
     * 设置是否需要年审
     *
     * @param annualRenew 是否需要年审
     */
    public void setAnnualRenew(Boolean annualRenew) {
        this.annualRenew = annualRenew;
    }

    /**
     * 获取认证是否过期失效
     *
     * @return verify_expired - 认证是否过期失效
     */
    public Boolean getVerifyExpired() {
        return verifyExpired;
    }

    /**
     * 设置认证是否过期失效
     *
     * @param verifyExpired 认证是否过期失效
     */
    public void setVerifyExpired(Boolean verifyExpired) {
        this.verifyExpired = verifyExpired;
    }

    /**
     * 获取今日新增粉丝数
     *
     * @return today_newfans - 今日新增粉丝数
     */
    public Integer getTodayNewfans() {
        return todayNewfans;
    }

    /**
     * 设置今日新增粉丝数
     *
     * @param todayNewfans 今日新增粉丝数
     */
    public void setTodayNewfans(Integer todayNewfans) {
        this.todayNewfans = todayNewfans;
    }

    /**
     * 获取全部粉丝数
     *
     * @return fans - 全部粉丝数
     */
    public Integer getFans() {
        return fans;
    }

    /**
     * 设置全部粉丝数
     *
     * @param fans 全部粉丝数
     */
    public void setFans(Integer fans) {
        this.fans = fans;
    }

    /**
     * 获取绑定粉丝数
     *
     * @return bind_fans - 绑定粉丝数
     */
    public Integer getBindFans() {
        return bindFans;
    }

    /**
     * 设置绑定粉丝数
     *
     * @param bindFans 绑定粉丝数
     */
    public void setBindFans(Integer bindFans) {
        this.bindFans = bindFans;
    }

    /**
     * 获取ACCESS_TOKEN
     *
     * @return access_token - ACCESS_TOKEN
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置ACCESS_TOKEN
     *
     * @param accessToken ACCESS_TOKEN
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取ACCESS_TOKEN过期时间
     *
     * @return expires_in - ACCESS_TOKEN过期时间
     */
    public Date getExpiresIn() {
        return expiresIn;
    }

    /**
     * 设置ACCESS_TOKEN过期时间
     *
     * @param expiresIn ACCESS_TOKEN过期时间
     */
    public void setExpiresIn(Date expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * 获取客服咨询量提醒数
     *
     * @return warnchat_no - 客服咨询量提醒数
     */
    public Integer getWarnchatNo() {
        return warnchatNo;
    }

    /**
     * 设置客服咨询量提醒数
     *
     * @param warnchatNo 客服咨询量提醒数
     */
    public void setWarnchatNo(Integer warnchatNo) {
        this.warnchatNo = warnchatNo;
    }

    /**
     * 获取客服忙时自动回复消息
     *
     * @return busy_auto_replaymsg - 客服忙时自动回复消息
     */
    public String getBusyAutoReplaymsg() {
        return busyAutoReplaymsg;
    }

    /**
     * 设置客服忙时自动回复消息
     *
     * @param busyAutoReplaymsg 客服忙时自动回复消息
     */
    public void setBusyAutoReplaymsg(String busyAutoReplaymsg) {
        this.busyAutoReplaymsg = busyAutoReplaymsg;
    }

    /**
     * 获取无客服在线时自动回复消息
     *
     * @return noonline_auto_replaymsg - 无客服在线时自动回复消息
     */
    public String getNoonlineAutoReplaymsg() {
        return noonlineAutoReplaymsg;
    }

    /**
     * 设置无客服在线时自动回复消息
     *
     * @param noonlineAutoReplaymsg 无客服在线时自动回复消息
     */
    public void setNoonlineAutoReplaymsg(String noonlineAutoReplaymsg) {
        this.noonlineAutoReplaymsg = noonlineAutoReplaymsg;
    }

    /**
     * 获取欢迎语
     *
     * @return welcome_replayemsg - 欢迎语
     */
    public String getWelcomeReplayemsg() {
        return welcomeReplayemsg;
    }

    /**
     * 设置欢迎语
     *
     * @param welcomeReplayemsg 欢迎语
     */
    public void setWelcomeReplayemsg(String welcomeReplayemsg) {
        this.welcomeReplayemsg = welcomeReplayemsg;
    }

    /**
     * 获取结束语
     *
     * @return endchat_replaymsg - 结束语
     */
    public String getEndchatReplaymsg() {
        return endchatReplaymsg;
    }

    /**
     * 设置结束语
     *
     * @param endchatReplaymsg 结束语
     */
    public void setEndchatReplaymsg(String endchatReplaymsg) {
        this.endchatReplaymsg = endchatReplaymsg;
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
}