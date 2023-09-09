package com.yunya.modules.treatment.other.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment_other.domain.form.TariffPackageDetailForm;
import com.yunya.feign.treatment_other.domain.form.TariffPackageForm;
import com.yunya.feign.treatment_other.domain.model.TariffPackageDetailModel;
import com.yunya.feign.treatment_other.domain.model.TariffPackageModel;
import com.yunya.feign.treatment_other.domain.query.TariffPackageDetailQuery;
import com.yunya.feign.treatment_other.domain.query.TariffPackageQuery;
import com.yunya.feign.treatment_other.domain.vo.TariffPackageDetailVO;
import com.yunya.feign.treatment_other.domain.vo.TariffPackageVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.biz.TariffPackageBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:06
 * @description: 价目组合控制器
 * @since: 1.0.0
 */
@Api(tags = "价目组合控制器")
@RestController
@RequestMapping("/tariffPackage")
public class TariffPackageController {

    @Autowired
    private TariffPackageBiz tariffPackageBiz;

    /**
     * 添加
     *
     * @param model
     * @return
     */
    @CurrentUser
    @RepeatSubmit
    @ApiOperation("添加")
    @PostMapping("/add")
    public ResponseResult add(@RequestBody @Validated TariffPackageModel model) {
        tariffPackageBiz.save(null, model);
        return ResponseUtil.success();
    }

    /**
     * 修改
     *
     * @param form
     * @return
     */
    @CurrentUser
    @ApiOperation("修改")
    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody @Validated TariffPackageForm form) {
        tariffPackageBiz.save(form.getId(), form);
        return ResponseUtil.success();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable(value = "id") Integer id) {
        tariffPackageBiz.delWithDetailById(id);
        return ResponseUtil.success();
    }

    /**
     * 条件查询价目组合
     *
     * @return
     */
    @ApiOperation("条件查询价目组合")
    @PostMapping("/list")
    public ResponseResult<PageInfo<TariffPackageVO>> findList(@RequestBody @Validated TariffPackageQuery query) {
        PageInfo<TariffPackageVO> page = tariffPackageBiz.findList(query);
        return ResponseUtil.success(page);
    }

    /**
     * 添加明细
     *
     * @param packageId
     * @param models
     * @return
     */
    @CurrentUser
    @RepeatSubmit
    @ApiOperation("添加明细")
    @PostMapping("/detail/{packageId}")
    public ResponseResult addDetail(@PathVariable(value = "packageId") Integer packageId, @RequestBody @Validated List<TariffPackageDetailModel> models) {
        tariffPackageBiz.insertDetail(packageId, models);
        return ResponseUtil.success();
    }

    /**
     * 修改数量
     *
     * @param form
     * @return
     */
    @CurrentUser
    @ApiOperation("修改数量")
    @PutMapping("/detail/edit")
    public ResponseResult editDetail(@RequestBody @Validated TariffPackageDetailForm form) {
        tariffPackageBiz.editDetail(form);
        return ResponseUtil.success();
    }


    /**
     * 删除明细
     *
     * @param id
     * @return
     */
    @ApiOperation("删除明细")
    @DeleteMapping("/detail/{id}")
    public ResponseResult deleteDetail(@PathVariable(value = "id") Integer id) {
        tariffPackageBiz.delDetailById(id);
        return ResponseUtil.success();
    }


    /**
     * 条件查询价目组合明细列表
     *
     * @return
     */
    @ApiOperation("条件查询价目组合明细列表")
    @PostMapping("/detail/list")
    public ResponseResult<PageInfo<TariffPackageDetailVO>> findDetailList(@RequestBody @Validated TariffPackageDetailQuery query) {
        PageInfo<TariffPackageDetailVO> page = tariffPackageBiz.findDetailList(query);
        return ResponseUtil.success(page);
    }
}
