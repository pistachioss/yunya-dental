package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：Netty服务器信息VO
 *
 * @author: chenlin
 * @Description: Netty服务器信息VO
 * @Date: 2022/4/15 9:42
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("Netty服务器信息VO")
@AllArgsConstructor
@NoArgsConstructor
public class NettyChatInfoVO implements Serializable {
    /** netty聊天服务器ip*/
    @ApiModelProperty("netty聊天服务器ip")
    private String protocol;

    /** 聊天连接路径*/
    @ApiModelProperty("聊天连接路径")
    private String path;
}
