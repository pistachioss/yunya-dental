package com.yunya.feign.wechat.domain.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description:
 * @author: xy
 * @date 2021/3/22 17:24
 **/
@Data
@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class WxBaseMsgModel {// 开发者微信号
    protected String FromUserName;
    // 发送方帐号（一个OpenID）
    protected String ToUserName;
    // 消息创建时间
    protected Long CreateTime;
    /**
     * 消息类型
     * text 文本消息
     * image 图片消息
     * voice 语音消息
     * video 视频消息
     * music 音乐消息
     */
    protected String MsgType;
}
