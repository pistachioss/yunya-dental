package com.yunya.feign.ivy_mini.domain.form;

import com.yunya.feign.ivy_mini.domain.bo.WeChatSessionBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/12/20 15:59
 **/
@Data
public class WxSaveFansForm {
    private Integer fansId;
    private WxUserInfoForm userInfo;
    private WeChatSessionBO sessionBO;
}
