package com.yunya.modules.discount.controller;

import com.yunya.feign.discount.domain.vo.CardWxVO;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.CardBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/19
 * @description:
 */
@Api(value = "小程序端-我的-待使用产品",description = "小程序端-我的-待使用产品")
@RestController
@RequestMapping("/wx")
@IgnoreUserToken
public class CardWxController {
    @Resource
    private CardBiz cardBiz;
    /**
     * 我的-会员信息
     *
     * @param
     */
    @ApiOperation("小程序-我的-待使用产品列表")
    @GetMapping("/card/{patientId}")
    public ResponseResult<List<CardWxVO>> findCardWxList(@PathVariable("patientId") Integer patientId) {
        return ResponseUtil.success( cardBiz.findCardWxList(patientId));
    }
}
