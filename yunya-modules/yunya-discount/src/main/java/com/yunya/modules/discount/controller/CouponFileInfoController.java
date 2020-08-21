package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.CouponFileInfoBiz;
import com.yunya.modules.discount.form.FileForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author 杨柳絮
 * @className CouponFileInfoController
 * @description
 * @date 2020/8/21 10:21
 */
@Api(tags = "文件")
@RestController
@RequestMapping("/coupon_file")
public class CouponFileInfoController {
    @Autowired
    private CouponFileInfoBiz couponFileInfoBiz;

    /**
     * 新增或修改文件信息
     *
     * @param fileForm
     * @return
     */
    @PostMapping("/updateFile")
    @ApiOperation("新增或修改文件信息，无论新增修改都传所有的文件信息，没有修改过的也要传")
    @CurrentUser
    public ResponseResult saveFile(@RequestBody @Valid FileForm fileForm) {
        return ResponseUtil.success(couponFileInfoBiz.saveOrUpdateFile(fileForm));
    }

    /**
     * 根据优惠券ID获取文件信息
     * @param fileForm
     * @return
     */
    @PostMapping("/findFile")
    @ApiOperation("根据优惠券ID获取文件信息")
    @CurrentUser
    public ResponseResult findFile(@RequestBody @Valid FileForm fileForm){
        return ResponseUtil.success(couponFileInfoBiz.findFile(fileForm));
    }
}
