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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="xml")
public class WxUserMsgModel {// 开发者微信号
    // 消息id
    private String MsgID;
    // 文本内容
    private String Content;
    // 图片链接（由系统生成）
    private String PicUrl;
    // 图片消息媒体id，可以调用多媒体文件下载接口拉取数据
    private String MediaId;
    // 语音格式：amr
    private String Format;
    // 语音识别结果，UTF8编码
    private String Recognition;
    // 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据
    private String ThumbMediaId;
    // 视频消息的标题
    private String Title;
    // 视频消息的描述
    private String Description;
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
    protected String Event;
    protected String Status;
}
