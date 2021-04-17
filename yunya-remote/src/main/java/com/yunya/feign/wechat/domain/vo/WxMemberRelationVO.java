package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/4/7 17:37
 **/
@Data
@ApiModel(value = "会员卡关联人返回")
public class WxMemberRelationVO {
    @ApiModelProperty(value = "副卡人")
    private List<String> viceCarder;
    @ApiModelProperty(value = "余额共享人")
    private List<String> balanceSharer;
}
