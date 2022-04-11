package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 聊天消息体
 *
 * @author Administrator
 */
@Data
@ToString
@ApiModel("聊天消息体")
public class ChatMessage implements Serializable {

    public ChatMessage() {

    }

    public ChatMessage(Integer sendId, Integer receiveId, String content, Integer type) {
        this.sendId = sendId;
        this.receiveId = receiveId;
        this.content = content;
        this.type = type;
    }

    /**
     * 发送者id
     */
    @ApiModelProperty("发送者id")
    private Integer sendId;

    /**
     * 接收者id
     */
    @ApiModelProperty("接收者id, 非空单发，为空则群发")
    private Integer receiveId;

    /**
     * 消息id
     */
    @ApiModelProperty("消息id")
    private Integer id;

    /**
     * 消息文本
     */
    @ApiModelProperty("消息文本")
    private String content;

    /**
     * 类型 1 上线 2发消息
     */
    @ApiModelProperty("类型 1 上线 2发消息")
    private int type;
}