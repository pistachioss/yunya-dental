package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.form.FansPickUpForm;
import com.yunya.feign.ivy_mini.domain.model.FansPickUpModel;
import com.yunya.feign.ivy_mini.domain.vo.FansPickUpVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.FansPickUpServiceImpl;
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
 * @date: 2022/6/16
 * @description:
 */
@RestController
@Api(tags = "小程序端-取货人信息Controller")
public class FansPickUpController  extends BaseController{
    @Resource
    private FansPickUpServiceImpl fansPickUpService;

    @PostMapping("/pickup/add")
    @ApiOperation("小程序-添加取货人信息")
    @ResponseBody
    public ResponseResult add(@Valid @RequestBody FansPickUpModel model) {
        fansPickUpService.add(model);
        return ResponseUtil.success();
    }

    @PutMapping("/pickup/update")
    @ApiOperation("小程序-修改取货人信息")
    public ResponseResult update(@Valid @RequestBody FansPickUpForm form) {
        fansPickUpService.update(form);
        return ResponseUtil.success();
    }

    @PostMapping("/pickup/list")
    @ApiOperation("小程序-查询取货人信息")
    @ResponseBody
    public ResponseResult<List<FansPickUpVO>> findList() {
        return ResponseUtil.success(fansPickUpService.findList());
    }

    @DeleteMapping("/pickup/delete/{id}")
    @ApiOperation("小程序-删除取货人信息")
    @ResponseBody
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        fansPickUpService.deleteById(id);
        return ResponseUtil.success();
    }
}
