package com.yunya365.wechat.enums;

import java.util.Objects;

/**
 * @description:
 * @author: xy
 * @date 2021/3/24 15:46
 **/
public enum NotifyEnum {
    //菜单点击事件
    CLICK("event", "click"),
    //关注
    SUBSCRIBE("event", "subscribe"),
    //取关
    UNSUBSCRIBE("event", "unsubscribe"),
    //已关注时的扫码事件
    SCAN("event", "scan"),
    //已关注时的扫码事件
    TEMPLATE("event", "TEMPLATESENDJOBFINISH"),
    //文字消息回复
    TEXT("text", null),
    //图片消息回复
    IMAGE("image", null),
    //语音消息回复
    VOICE("voice", null),
    //视频消息回复
    VIDEO("video", null);


    private String msgType;
    private String event;

    NotifyEnum(String msgType, String event) {
        this.msgType = msgType;
        this.event = event;
    }

    public String getMsgType() {
        return this.msgType;
    }

    public String getEvent() {
        return this.event;
    }

    /**
     * 解析事件类型
     *
     * @param msgType
     * @param event
     * @return
     */
    public static NotifyEnum resolveEvent(String msgType, String event) {
        for (NotifyEnum notifyEnum : NotifyEnum.values()) {
            if (Objects.equals(msgType, notifyEnum.getMsgType()) && Objects.equals(event, notifyEnum.getEvent())) {
                return notifyEnum;
            }
        }
        return null;
    }
}
