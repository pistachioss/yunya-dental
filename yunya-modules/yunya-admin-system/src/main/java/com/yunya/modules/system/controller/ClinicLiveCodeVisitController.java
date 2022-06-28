package com.yunya.modules.system.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.ClinicLiveCode;
import com.yunya.models.system.ClinicLiveCodeVisit;
import com.yunya.modules.system.biz.ClinicLiveCodeVisitBiz;
import com.yunya.modules.system.domain.form.ClinicLiveCodeForm;
import com.yunya.modules.system.domain.model.ClinicLiveCodeModel;
import com.yunya.modules.system.domain.model.ClinicLiveCodeVisitModel;
import com.yunya.modules.system.domain.query.ClinicLiveCodeQueryForm;
import com.yunya.modules.system.domain.query.ClinicLiveCodeVisitQueryForm;
import com.yunya.modules.system.vo.ClinicLiveCodeVO;
import com.yunya.modules.system.vo.ClinicLiveCodeVisitCountVO;
import com.yunya.modules.system.vo.ClinicLiveCodeVisitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 简介：门店店长活码访问控制器
 *
 * @author: chenlin
 * @Description: 门店店长活码访问控制器
 * @Date: 2022/6/27 15:51
 * @since: 1.0.0
 */
@Api(tags = "门店店长活码访问控制器")
@RestController
@RequestMapping("/clinicLiveCodeVisit")
public class ClinicLiveCodeVisitController {

    @Autowired
    private ClinicLiveCodeVisitBiz clinicLiveCodeVisitBiz;

    /**
     * 条件查询门店店长活码访问记录列表
     *
     * @param query
     * @return
     */
    @ApiOperation("条件查询门店店长活码访问记录列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<ClinicLiveCodeVisitVO>> findList(@RequestBody @Validated ClinicLiveCodeVisitQueryForm query) {
        PageInfo<ClinicLiveCodeVisitVO> result = clinicLiveCodeVisitBiz.findList(query);
        return ResponseUtil.success(result);
    }

    /**
     * 长按识别二维码后在关闭页面时调用该接口
     *
     * @param model
     * @return
     */
    @ApiOperation("长按识别二维码后在关闭页面时调用该接口")
    @PostMapping("/click")
    public ResponseResult click(@Validated @RequestBody ClinicLiveCodeVisitModel model) {
        clinicLiveCodeVisitBiz.click(model);
        return ResponseUtil.success();
    }

    /**
     * 条件查询门店店长活码访问数量
     *
     * @return
     */
    @ApiOperation("条件查询门店店长活码访问数量")
    @PostMapping("/count")
    public ResponseResult<ClinicLiveCodeVisitCountVO> findCount() {
        ClinicLiveCodeVisitCountVO result = clinicLiveCodeVisitBiz.findCount();
        return ResponseUtil.success(result);
    }
}
