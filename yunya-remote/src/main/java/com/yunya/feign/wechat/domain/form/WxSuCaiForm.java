package com.yunya.feign.wechat.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 微信公众号获取素材列表参数
 * @author: zd.xie
 * @create: 2022-07-05
 */
@ApiModel(description = "微信公众号获取素材列表参数")
@Data
public class WxSuCaiForm {
    @ApiModelProperty(value = "类型:素材的类型，图片（image）、视频（video）、语音 （voice）、图文（news）", required = true)
    @NotNull(message = "类型不能为空")
    private String type;
    @ApiModelProperty(value = "从全部素材的该偏移位置开始返回，0表示从第一个素材 返回")
    private int offset;
    @ApiModelProperty(value = "返回素材的数量，取值在1到20之间")
    private int count;
}
