package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.ClinicLiveCodeBiz;
import com.yunya.modules.system.domain.model.ClinicLiveCodeModel;
import com.yunya.modules.system.domain.query.ClinicLiveCodeQueryForm;
import com.yunya.modules.system.vo.ClinicLiveCodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 简介：门店店长活码控制器
 *
 * @author: chenlin
 * @Description: 门店店长活码控制器
 * @Date: 2022/5/18 15:51
 * @since: 1.0.0
 */
@Api(tags = "门店店长活码控制器")
@RestController
@RequestMapping("/clinicLiveCode")
public class ClinicLiveCodeController {

    @Autowired
    private ClinicLiveCodeBiz clinicLiveCodeBiz;

    /**
     * 条件查询门店店长活码列表
     *
     * @param query
     * @return
     */
    @ApiOperation("条件查询门店店长活码列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<ClinicLiveCodeVO>> findList(@RequestBody @Validated ClinicLiveCodeQueryForm query) {
        PageInfo<ClinicLiveCodeVO> result = clinicLiveCodeBiz.findList(query);
        return ResponseUtil.success(result);
    }

    @ApiOperation("添加门店店长活码")
    @PostMapping("/add")
    public ResponseResult add(@RequestParam("file") final MultipartFile file, ClinicLiveCodeModel model) {
        clinicLiveCodeBiz.add(file, model);
        return ResponseUtil.success();
    }

}
