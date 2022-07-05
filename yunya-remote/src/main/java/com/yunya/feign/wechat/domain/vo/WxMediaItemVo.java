package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "返回的素材元素模型")
public class WxMediaItemVo {
    @ApiModelProperty(value = "id")
    private String media_id;
    @ApiModelProperty(value = "显示的名称")
    private String name;
    @ApiModelProperty(value = "更新时间")
    private long update_time;
    @ApiModelProperty(value = "可访问的url")
    private String url;
}
