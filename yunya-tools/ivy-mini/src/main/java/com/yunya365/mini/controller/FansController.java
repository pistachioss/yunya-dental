package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.form.ModifyFansForm;
import com.yunya.feign.ivy_mini.domain.vo.FansDetailVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.IWxFansService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 用户收货地址表 前端控制器
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-20
 */
@RestController
@Api(tags = "用户模块")
public class FansController extends BaseController{

    @Resource
    private IWxFansService fansService;

    @PostMapping("/fans/detail")
    @ApiOperation("【小程序】用户详情")
    public ResponseResult<FansDetailVO> fansDetail(@RequestParam String openId) {
        FansDetailVO detailVO = fansService.fansDetail(openId);
        return ResponseUtil.success(detailVO);
    }

    @PostMapping("/fans/edit")
    @ApiOperation("【小程序】编辑用户信息")
    public ResponseResult<Boolean> modifyFans(@Valid @RequestBody ModifyFansForm form) {
        fansService.modifyFans(form);
        return ResponseUtil.success();
    }

}

