package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
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
@ApiModel("微信管理列表VO")
public class WxWechatFansVo implements Serializable {
    @ApiModelProperty("粉丝ID")
    private Integer id;
    @ApiModelProperty("openID")
    private String openId;
    @ApiModelProperty("昵称")
    private String nickName;
//    @ApiModelProperty("患者姓名")
//    private String name;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty("注册日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;
    @ApiModelProperty("绑定状态0:未绑定患者 1:已绑定患者")
    private Integer isBind;
    @ApiModelProperty("常驻地址")
    private String address;
    @ApiModelProperty("绑定患者")
    private String bindPant;
    @ApiModelProperty("用户来源")
    private Integer sourceType;
    @ApiModelProperty("用户来源名称")
    private String sourceTypeName;
    @ApiModelProperty("用户状态（0-注销 1-禁用 2-正常 3-注销中）")
    private Integer fansStatus;
    @ApiModelProperty("患者关系列表，用于解绑")
    private List<WxWechatbindListVO> bindPantlist;
}
