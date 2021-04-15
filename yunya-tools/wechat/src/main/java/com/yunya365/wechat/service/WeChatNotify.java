package com.yunya365.wechat.service;

import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxSendMsgVo;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 17:11
 **/
public interface WeChatNotify {
    /**
     * 上层事件推送策略抽象接口
     *
     * @param msgReq 微信推送的参数数据
     * @return  返回给微信的回复信息
     * @throws Exception
     */
    WxSendMsgVo weChatNotify(WxUserMsgModel msgReq) throws Exception;
}
