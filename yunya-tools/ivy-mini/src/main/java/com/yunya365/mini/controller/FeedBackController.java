package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.FeedBackForm;
import com.yunya.feign.ivy_mini.domain.vo.FeedBackVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.FeedBackServiceImpl;
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
 * @date: 2022/5/16
 * @description:
 */
@RestController
@Api(tags = "后台-意见反馈管理")
public class FeedBackController extends PcBaseController{

    @Resource
    private FeedBackServiceImpl feedBackService;

    @PostMapping("/feedback/findlist")
    @ApiOperation("后台-意见反馈-列表")
    public ResponseResult<PageInfo<FeedBackVO>> findList(@RequestBody @Valid FeedBackForm form) {
        return ResponseUtil.success(feedBackService.findList(form));
    }
    @ApiOperation("后台-意见反馈-删除")
    @DeleteMapping("/feedback/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return feedBackService.delete(id);
    }

}
