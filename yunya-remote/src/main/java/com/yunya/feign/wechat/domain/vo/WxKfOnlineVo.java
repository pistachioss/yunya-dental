package com.yunya.feign.wechat.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/3/26 9:38
 **/
@Data
public class WxKfOnlineVo {
    //完整客服帐号，格式为：帐号前缀@公众号微信号
    private String kf_account;
    //客服在线状态，目前为：1、web 在线
    private Integer status;
    //客服编号
    private String kf_id;
    //客服当前正在接待的会话数
    private Integer accepted_case;
}
