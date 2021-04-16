package com.yunya365.wechat.service.impl;

import com.yunya.feign.wechat.domain.model.WxUserMsgModel;
import com.yunya.feign.wechat.domain.vo.WxKfOnlineVo;
import com.yunya.feign.wechat.domain.vo.WxSendMsgVo;
import com.yunya365.wechat.config.NotifyType;
import com.yunya365.wechat.enums.NotifyEnum;
import com.yunya365.wechat.service.WeChatNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description:
 * @author: xy
 * @date 2021/3/25 10:27
 **/
@Slf4j
@Service
@NotifyType({NotifyEnum.TEXT, NotifyEnum.IMAGE, NotifyEnum.VOICE, NotifyEnum.VIDEO})
public class KfReply  extends AbstractWxBaseApi implements WeChatNotify {

    @Override
    public WxSendMsgVo weChatNotify(WxUserMsgModel msg) throws Exception{
        log.info("消息接受：{}，时间：{}", msg, LocalDateTime.now());
        //创建消息响应对象
        WxSendMsgVo out = new WxSendMsgVo();
        //把原来的发送方设置为接收方
        out.setToUserName(msg.getFromUserName());
        //把原来的接收方设置为发送方
        out.setFromUserName(msg.getToUserName());
        //获取接收的消息类型
        String msgType = msg.getMsgType();
        //设置消息创建时间
        out.setCreateTime(new Date().getTime());
        //设置消息的响应类型
        out.setMsgType("transfer_customer_service");
//        out.setContent("您好");
        WxKfOnlineVo kfOnlineRes = super.listOnlineKf();
        if (kfOnlineRes != null) {
            out.setKfAccount(kfOnlineRes.getKf_account());
        }
        log.info("回复消息：{}，时间：{}", out, LocalDateTime.now());
        return out;
    }

}
