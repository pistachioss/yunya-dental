package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.vo.RefundReasonVO;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.IOrderReturnReasonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 退货原因表 前端控制器
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
@RestController
@Api(tags = "退款原因")
@IgnoreUserToken
public class OrderReturnReasonController extends BaseController {

    @Resource
    private IOrderReturnReasonService reasonService;

    @PostMapping("/refund/reason/list")
    @ApiOperation("【小程序】退款原因列表")
    public ResponseResult<List<RefundReasonVO>> list() {
        return ResponseUtil.success(reasonService.reasonList());
    }
}

