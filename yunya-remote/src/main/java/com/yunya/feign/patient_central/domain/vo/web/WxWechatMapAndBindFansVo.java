package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/20
 * @description:
 */
@Data
@ToString
@ApiModel("微信管理-地图范围内全部数据VO")
public class WxWechatMapAndBindFansVo {
    @ApiModelProperty("范围内用户信息集合")
    private List<WxWechatMapFansVo> mapFansVoList;
    @ApiModelProperty("范围内用户数量")
    private WxWechatMapBindNumFansVo wxWechatMapBindNumFansVo;
}
