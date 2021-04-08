package com.yunya.modules.patient_central.controller.web;

import com.yunya.feign.patient_central.domain.query.WxFansBindForm;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.WxFansBindBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:微信公众号粉丝与患者绑定关系业务层
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(value = "WxFanBindController",description = "微信公众号粉丝与患者绑定关系业务层")
@RestController
@RequestMapping("wxFansBind")
public class WxFanBindController {

    @Autowired private WxFansBindBiz wxFansBindBiz;

    /**
     * 客服中心-用户管理-绑定患者
     *
     * @param
     * @return
     */
    @ApiOperation("客服中心-用户管理-绑定患者")
    @PostMapping("/bind")
    @CurrentUser
    public ResponseResult<Integer> bind(
            @RequestBody @Validated WxFansBindForm wxFansBindForm) {
        return ResponseUtil.success(wxFansBindBiz.bind(wxFansBindForm));
    }


}
