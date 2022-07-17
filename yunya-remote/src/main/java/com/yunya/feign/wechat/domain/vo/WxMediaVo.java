package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "返回的素材模型")
public class WxMediaVo {
    @ApiModelProperty(value = "总素材数")
    private int total_count;
    @ApiModelProperty(value = "当前素材数")
    private int item_count;
    @ApiModelProperty(value = "当前素材元素列表")
    private List<WxMediaItemVo> item;
}
