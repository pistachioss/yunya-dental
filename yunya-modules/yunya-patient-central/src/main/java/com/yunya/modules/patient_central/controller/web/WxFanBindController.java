package com.yunya.modules.patient_central.controller.web;

import com.yunya.feign.patient_central.domain.query.WxFansBindForm;
import com.yunya.feign.patient_central.domain.query.WxUserQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
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

import java.util.List;

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
@IgnoreUserToken
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
    public ResponseResult<Integer> bind(
            @RequestBody @Validated WxFansBindForm wxFansBindForm) {
        return ResponseUtil.success(wxFansBindBiz.bind(wxFansBindForm));
    }
    /**
     * 客服中心-用户管理-绑定患者
     *
     * @param
     * @return
     */
    @ApiOperation("客服中心-用户管理-绑定患者-该患者是否已经绑定")
    @PostMapping("/isBind")
    public ResponseResult<Integer> isBind(
            @RequestBody @Validated WxFansBindForm wxFansBindForm) {
        return ResponseUtil.success(wxFansBindBiz.isBind(wxFansBindForm));
    }

    /**
     * 客服中心-用户管理-解除绑定
     *
     * @param
     * @return
     */
    @ApiOperation("客服中心-用户管理-解除绑定")
    @PostMapping("/unbind")
    public ResponseResult<Integer> unbind(
            @RequestBody @Validated WxFansBindForm wxFansBindForm) {
        return ResponseUtil.success(wxFansBindBiz.unbind(wxFansBindForm));
    }

    /**
     * 根据openId或患者id查询其绑定的患者列表
     *
     * @param query
     * @return
     */
    @ApiOperation("根据unionId或患者id查询其绑定的患者列表")
    @PostMapping("/bindPatientList")
    public ResponseResult<List<PatientBaseInfoVo>> wxFansBindPatientList(@RequestBody WxUserQuery query) {
        return ResponseUtil.success(wxFansBindBiz.wxFansBindPatientList(query));
    }
}
