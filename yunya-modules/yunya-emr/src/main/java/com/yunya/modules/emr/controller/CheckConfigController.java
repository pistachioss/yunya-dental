package com.yunya.modules.emr.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.emr.domain.form.CheckConfigForm;
import com.yunya.feign.emr.domain.model.CheckConfigModel;
import com.yunya.feign.emr.domain.vo.CheckConfigVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.PageQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.emr.biz.CheckConfigBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author chenlin
 * @description: 检查设置控制器
 * @date 2022/1/07
 */
@Api(tags = {"检查设置控制器"})
@RestController
@RequestMapping("checkConfig")
public class CheckConfigController {

    @Autowired
    private CheckConfigBiz checkConfigBiz;

    @ApiOperation(value = "新增检查")
    @PostMapping("/add")
    @CurrentUser
    public ResponseResult add(@Valid @RequestBody CheckConfigModel model) {
        return ResponseUtil.success(checkConfigBiz.add(model));
    }

    @ApiOperation(value = "修改检查")
    @PostMapping("/update")
    @CurrentUser
    public ResponseResult update(@Valid @RequestBody CheckConfigForm form) {
        return ResponseUtil.success(checkConfigBiz.update(form));
    }

    @ApiOperation(value = "删除检查")
    @PutMapping("/delete/{id}")
    @CurrentUser
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        return ResponseUtil.success(checkConfigBiz.delete(id));
    }

    @ApiOperation(value = "根据id查询")
    @GetMapping("/one/{id}")
    public ResponseResult<CheckConfigVO> findOneById(@PathVariable(value = "id") Integer id) {
        return ResponseUtil.success(checkConfigBiz.findOneById(id));
    }

    @ApiOperation("分页查询")
    @PostMapping("/list")
    @CurrentUser
    public ResponseResult<PageInfo<CheckConfigVO>> findList(@Valid @RequestBody PageQuery query) {
        PageInfo<CheckConfigVO> page = checkConfigBiz.findCheckConfigList(query);
        return ResponseUtil.success(page);
    }
}
