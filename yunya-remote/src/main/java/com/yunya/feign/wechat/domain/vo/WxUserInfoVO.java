package com.yunya.feign.wechat.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介：微信用户信息VO
 *
 * @author: chenlin
 * @Description: 微信用户信息VO
 * @Date: 2022/6/27 15:11
 * @since: 1.0.0
 */
@ApiModel("微信用户信息VO")
@Data
@ToString
public class WxUserInfoVO implements Serializable {

    /** 用户的唯一标识 */
    @ApiModelProperty("用户的唯一标识")
    private String openid;

    /** 用户昵称 */
    @ApiModelProperty("用户昵称")
    private String nickname;

    /** 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知 */
    @ApiModelProperty("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
    private Byte sex;

    /** 用户个人资料填写的省份 */
    @ApiModelProperty("用户个人资料填写的省份")
    private String province;

    /** 普通用户个人资料填写的城市 */
    @ApiModelProperty("普通用户个人资料填写的城市")
    private String city;

    /** 国家，如中国为CN */
    @ApiModelProperty("国家，如中国为CN")
    private String country;

    /** 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像 URL 将失效。*/
    @ApiModelProperty("用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像 URL 将失效。")
    private String headimgurl;

    /** 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）*/
    @ApiModelProperty("用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）")
    private List<String> privilege;

    /** 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。*/
    @ApiModelProperty("只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。")
    private String unionid;
}
