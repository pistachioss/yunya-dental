package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
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
@ApiModel("微信管理-地图范围内用户VO")
public class WxWechatMapFansVo {
    @ApiModelProperty("粉丝ID")
    private Integer id;
    @ApiModelProperty("经度")
    private Double longitude;
    @ApiModelProperty("纬度")
    private Double latitude;
    @ApiModelProperty("绑定状态0:未绑定患者 1:已绑定患者")
    private Integer isBind;
    @ApiModelProperty("昵称")
    private String nickName;
    @ApiModelProperty("头像")
    private String headImgurl;
//    @ApiModelProperty("患者名称")
//    private String name;
    @ApiModelProperty("绑定患者数量")
    private Integer numBind;
    @ApiModelProperty("定位时间")
    private Date lastLoginDate;
    @ApiModelProperty("患者关系列表")
    private List<WxWechatbindListVO> bindPantlist;

}
