package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
@ToString
@ApiModel("公司微信公众号粉丝VO")
public class WxFansVo implements Serializable {
    @ApiModelProperty("粉丝ID")
    private Integer id;
    @ApiModelProperty("openID")
    private String openId;
    @ApiModelProperty("unionId")
    private String unionId;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("是否关注")
    private String subscribe;
    @ApiModelProperty("昵称")
    private String nickName;
    @ApiModelProperty("头像")
    private String headImgurl;
    @ApiModelProperty("患者姓名")
    private String name;
    @ApiModelProperty("手机号")
    private String mobile;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("注册日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date crtTime;
    @ApiModelProperty("绑定状态0:未绑定患者 1:已绑定患者")
    private Integer isBind;
    @ApiModelProperty("最后一次登录地址")
    private String lastLoginAddress;
}

