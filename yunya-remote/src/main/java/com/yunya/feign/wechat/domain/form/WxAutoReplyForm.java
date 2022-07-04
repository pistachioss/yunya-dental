package com.yunya.feign.wechat.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 微信关键字自动回复消息配置参数
 * @author: zd.xie
 * @create: 20212-04-27
 */
@ApiModel(description = "微信关键字自动回复消息配置参数")
@Data
public class WxAutoReplyForm implements Serializable {

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字", required = true)
    @NotNull(message = "关键字不能为空")
    private String eventname;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "回复消息内容", required = true)
    @NotNull(message = "回复消息内容不能为空")
    private String msgtext;

    /**
     * 消息标题
     */
    @ApiModelProperty(value = "回复消息标题")
    private String msgtitle;

    /**
     * 消息跳转地址
     */
    @ApiModelProperty(value = "回复消息跳转地址")
    private String msgurl;

    /**
     * 消息图片地址
     */
    @ApiModelProperty(value = "回复消息图片地址")
    private String msgpicurl;
}
