package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 返回卡主信息参数模型 主卡信息
 *
 * @author: WY
 * @date 2020/9/11 16:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回卡主信息参数模型")
public class MasertMemberInfoVo implements Serializable {

    /**
     *会员卡信息表主键id
     */
    private Integer id;

    /**
     *  积分
     */
    private Integer point;

    /**
     * 主卡人id
     */
    private Integer masterCardId;

    /**
     * 主卡人会员卡号
     */
    private String masterCardNumber;

    /**
     * 主卡人会员卡类型
     */
    private Integer masterCardTypeId;

    /**
     * 主卡人姓名
     */
    private String masterName;

    /**
     * 主卡人会员卡类型名称
     */
    private String masterMemberCardName;

    /**
     * 折扣率（价目表自动调价的折扣率）
     */
    private Float rate;

    /**
     * 会员卡图片
     */
    private String pictureCode;
}
