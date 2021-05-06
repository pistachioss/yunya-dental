package com.yunya.feign.patient_central.domain.query;

import com.yunya.models.patient_central.WxFans;
import com.yunya.models.patient_central.WxFansBind;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

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
public class WxFansSaveForm {

    @ApiModelProperty("公司微信公众号粉丝信息")
    private WxFans wxFans;
    @ApiModelProperty("绑定关系")
    private List<WxFansBind> fansBind;

}
