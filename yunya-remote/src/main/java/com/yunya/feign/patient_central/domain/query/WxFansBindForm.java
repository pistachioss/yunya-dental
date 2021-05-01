package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 简介: 微信粉丝绑定患者参数模型
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
public class WxFansBindForm {

    @ApiModelProperty("公司微信公众号粉丝表Id")
    private Integer wxId;

    @ApiModelProperty("患者ID")
    private Integer patientId;

    @ApiModelProperty("openID")
    private String openId;

    @ApiModelProperty("患者关系Id")
    private Integer dictionaryId;

    @ApiModelProperty("患者关系名称 用来判断是否为本人 区分微信拥有者")
    private String dictionaryName;

}
