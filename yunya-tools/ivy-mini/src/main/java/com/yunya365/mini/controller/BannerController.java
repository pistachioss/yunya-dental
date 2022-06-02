package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.BannerAddForm;
import com.yunya.feign.ivy_mini.domain.vo.BannerVO;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.BannerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/13
 * @description:
 */
@RestController
@Api(tags = "后台-banner管理")
public class BannerController extends PcBaseController{

    @Resource
    private BannerServiceImpl bannerService;

    @PostMapping("/wechat/banner/findlist")
    @ApiOperation("后台-banner-列表")
    public ResponseResult<List<BannerVO>> findList() {
        return ResponseUtil.success(bannerService.findList());
    }

    @PostMapping("/wechat/banner/add")
    @ApiOperation("后台-banner-新增")
    @RepeatSubmit
    public ResponseResult add(@RequestBody @Valid BannerAddForm form) {
        bannerService.add(form);
        return ResponseUtil.success(null);
    }

    @PutMapping("/wechat/banner/update")
    @ApiOperation("后台-banner-修改")
    @RepeatSubmit
    public ResponseResult update(@RequestBody @Valid BannerAddForm form) {
        return ResponseUtil.success( bannerService.update(form));
    }

    @ApiOperation("后台-banner-删除")
    @DeleteMapping("/wechat/banner/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return ResponseUtil.success(bannerService.deleteById(id));
    }
}
