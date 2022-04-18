package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息体
 *
 * @author Administrator
 */
@Data
@ToString
@ApiModel("聊天消息体")
public class ChatMessageBody implements Serializable {

    public ChatMessageBody() {

    }

    public ChatMessageBody(String msgCode, Integer sendId, Integer receiveId, String content, Integer type) {
        this.msgCode = msgCode;
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.content = content;
        this.type = type;
    }

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Integer id;

    /**
     * 发送者id
     */
    @ApiModelProperty(value = "发送者id")
    private Integer sendId;

    /**
     * 发送者
     */
    @ApiModelProperty("发送人")
    private String sendUser;

    /**
     * 接收者id
     */
    @ApiModelProperty("接收者id, 非空单发，为空则群发")
    private Integer receiveId;

    /**
     * 接收者
     */
    private String receiveUser;

    /**
     * 消息code
     */
    @ApiModelProperty("消息code")
    private String msgCode;

    /**
     * 消息文本
     */
    @ApiModelProperty("消息文本")
    private String content;

    /**
     * 类型:0-心跳or上线, 1-查询未读消息, 2-发消息, 3-挂号提醒
     */
    @ApiModelProperty("类型：0-心跳or上线, 1-查询未读消息, 2-发消息, 3-挂号提醒")
    private Integer type;

    /**
     * 发送时间
     */
    @ApiModelProperty("发送时间")
    private Date sendTime;

    /**
     * 读取消息的时间
     */
    @ApiModelProperty("读取消息的时间")
    private Date readTime;

    /**
     * 是否确认已读
     */
    @ApiModelProperty("是否确认已读")
    private Boolean ackRead;
}