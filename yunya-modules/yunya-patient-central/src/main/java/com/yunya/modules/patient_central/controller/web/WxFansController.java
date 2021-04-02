package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.WxFansQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.WxFansVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.WxFansBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:公司微信公众号粉丝控制层
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Api(value = "WxFansController",description = "公司微信公众号粉丝控制层")
@RestController
@RequestMapping("wxFans")
public class WxFansController {

    @Autowired private WxFansBiz wxFansBiz;

    /**
     * 客服中心-用户管理列表
     *
     * @param
     * @return ResponseResult<PageInfo<WxFansVo>>
     */
    @ApiOperation("客服中心-用户管理列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<WxFansVo>> findList(
            @RequestBody @Validated WxFansQueryForm wxFansQueryForm) {
        return ResponseUtil.success(
               wxFansBiz.findList(wxFansQueryForm));
    }

}
