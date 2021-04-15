package com.yunya.feign.wechat.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/3/26 9:38
 **/
@Data
public class WxKfListVo {
    //完整客服帐号，格式为：帐号前缀@公众号微信号
    private String kf_account;
    //客服昵称
    private String kf_nick;
    //客服编号
    private String kf_id;
    //客服头像
    private String kf_headimgurl;
    //如果客服帐号已绑定了客服人员微信号， 则此处显示微信号
    private String kf_wx;
    //如果客服帐号尚未绑定微信号，但是已经发起了一个绑定邀请， 则此处显示绑定邀请的微信号
    private String invite_wx;
    //如果客服帐号尚未绑定微信号，但是已经发起过一个绑定邀请， 邀请的过期时间，为unix 时间戳
    private String invite_expire_time;
    //邀请的状态，有等待确认“waiting”，被拒绝“rejected”， 过期“expired”
    private String invite_status;
}
