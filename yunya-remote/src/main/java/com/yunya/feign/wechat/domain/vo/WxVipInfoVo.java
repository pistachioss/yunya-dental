package com.yunya.feign.wechat.domain.vo;

import com.yunya.feign.discount.domain.vo.*;
import io.swagger.annotations.*;
import lombok.*;

import java.math.*;
import java.util.*;

/**
 * @description:
 * @author: xy
 * @date 2021/4/7 17:37
 **/
@Data
public class WxVipInfoVo {
    @ApiModelProperty(value = "注册用户名")
    private String registerName;
    @ApiModelProperty(value = "注册手机号")
    private String registerMobile;
    @ApiModelProperty(value = "头像")
    private String headImgUrl;
    @ApiModelProperty(value = "会员卡类型（1-金藤卡，2-银藤卡，3-青藤卡，4-艾维会员）")
    private Integer memberType;
    @ApiModelProperty(value = "会员卡余额")
    private BigDecimal memberBalance;
    @ApiModelProperty(value = "是否有预付款")
    private Boolean existPrePayment;
    @ApiModelProperty(value = "是否有会员卡")
    private Boolean existMemberCard;
    @ApiModelProperty(value = "患者拥有礼包")
    private List<WxPatientEffectiveVo> cardList;

}
