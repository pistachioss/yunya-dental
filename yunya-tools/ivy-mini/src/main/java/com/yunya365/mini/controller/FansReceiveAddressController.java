package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.form.ModifyAddressForm;
import com.yunya.feign.ivy_mini.domain.model.AddAddressModel;
import com.yunya.feign.ivy_mini.domain.vo.AddressListVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.IFansReceiveAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户收货地址表 前端控制器
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-20
 */
@RestController
@Api(tags = "用户收货地址")
public class FansReceiveAddressController extends BaseController{

    @Resource
    private IFansReceiveAddressService receiveAddressService;

    @PostMapping("/add/address")
    @ApiOperation("【小程序】添加地址")
    public ResponseResult<Boolean> addAddress(@Valid @RequestBody AddAddressModel model) {
        receiveAddressService.addAddress(model);
        return ResponseUtil.success();
    }

    @PostMapping("/modify/address")
    @ApiOperation("【小程序】编辑地址")
    public ResponseResult<Boolean> modifyAddress(@Valid @RequestBody ModifyAddressForm form) {
        receiveAddressService.modifyAddress(form);
        return ResponseUtil.success();
    }

    @PostMapping("/list/address")
    @ApiOperation("【小程序】用户地址列表")
    public ResponseResult<List<AddressListVO>> listAddress() {
        List<AddressListVO> list = receiveAddressService.listAddress();
        return ResponseUtil.success(list);
    }

    @PostMapping("/delete/address/{receiveId}")
    @ApiOperation("【小程序】删除地址")
    public ResponseResult<Boolean> deleteAddress(@PathVariable Integer receiveId) {
        receiveAddressService.deleteAddress(receiveId);
        return ResponseUtil.success();
    }

}

