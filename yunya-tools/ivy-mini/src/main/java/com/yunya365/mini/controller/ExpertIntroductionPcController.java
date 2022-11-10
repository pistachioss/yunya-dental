package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionAddAndUpdateForm;
import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionForm;
import com.yunya.feign.ivy_mini.domain.vo.ExpertIntroductionVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.ExpertIntroductionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/10
 * @description: 专家介绍
 */
@RestController
@Api(tags = "后台-专家介绍")
@IgnoreUserToken
public class ExpertIntroductionPcController extends PcBaseController{

    @Resource
    private ExpertIntroductionServiceImpl expertIntroductionService;

    @PostMapping("/expertIntroduction/findlist")
    @ApiOperation("后台-专家介绍-列表")
    public ResponseResult<PageInfo<ExpertIntroductionVO>> findList(@RequestBody @Valid ExpertIntroductionForm form) {
        return ResponseUtil.success(expertIntroductionService.findList(form));
    }

    /**
     * 艾维动态/口腔科普 置顶列表更新
     *
     * @return ResponseResult
     */
    @ApiOperation("后台-艾维动态/口腔科普-置顶列表更新")
    @PutMapping("/expertIntroduction/updateList")
    public ResponseResult updateList(@RequestBody @Validated List<ExpertIntroductionVO> list) {
        expertIntroductionService.updateList(list);
        return ResponseUtil.success(null);
    }

    @PostMapping("/expertIntroduction/add")
    @ApiOperation("后台-专家介绍-新增")
    @RepeatSubmit
    @CurrentUser
    public ResponseResult add(@RequestBody @Valid ExpertIntroductionAddAndUpdateForm form) {
        expertIntroductionService.add(form);
        return ResponseUtil.success(null);
    }

    @ApiOperation("后台-专家介绍-修改/上下架/发布")
    @PutMapping("/expertIntroduction/update")
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated ExpertIntroductionAddAndUpdateForm form) {
        return expertIntroductionService.update(form);
    }

    @ApiOperation("后台-专家介绍-删除")
    @DeleteMapping("/expertIntroduction/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return expertIntroductionService.delete(id);
    }
}
