package com.yunya.modules.discount.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.SalesSource;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.SalesSourceBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.yunya.framework.common.constant.OperationCodeConstants.DELETE_NOT_ALLOW;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/11/29
 * @description:
 */
@Api(tags = "销售来源")
@RestController
@RequestMapping("/sales_source")
@CurrentUser
public class SalesSourceController {
    @Autowired
    private SalesSourceBiz salesSourceBiz;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;

    /**
     * 新增销售来源
     *
     * @param
     * @return
     */
    @PostMapping
    @ApiOperation("新增销售来源")
    public ResponseResult save(@RequestBody @Valid SalesSource salesSource) {
        salesSourceBiz.saveSalesSource(salesSource);
        return ResponseUtil.success();
    }

    /**
     * 修改销售来源
     *
     * @param
     * @return
     */
    @PutMapping
    @ApiOperation("修改销售来源")
    public ResponseResult update(@RequestBody SalesSource salesSource) {
        salesSourceBiz.updateSalesChannel(salesSource);
        return ResponseUtil.success();
    }

    /**
     * 获取销售来源
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取销售来源")
    public ResponseResult find(@PathVariable(name = "id") Integer id) {
        return ResponseUtil.success(salesSourceBiz.selectById(id));
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("列表")
    public ResponseResult list() {
        SalesSource salesSource = new SalesSource();
        salesSource.setInservice(true);
        return ResponseUtil.success(salesSourceBiz.selectList(salesSource));
    }

    /**
     * 删除
     *
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除")
    public ResponseResult delete(@PathVariable(name = "id") Integer id) {
        CouponCommonInfo card = new CouponCommonInfo();
        card.setSalesSourceId(id);
        Long num = couponCommonInfoBiz.selectCount(card);
        if(num>0){
            throw new ClientServiceException("销售来源已经被使用，不允许删除！", DELETE_NOT_ALLOW);
        }else{
            salesSourceBiz.deleteById(id);
        }
        return ResponseUtil.success();
    }
    
}
