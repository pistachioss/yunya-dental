package com.yunya365.mini.controller;

import com.yunya.feign.ivy_mini.domain.vo.CustomerOneVO;
import com.yunya.feign.ivy_mini.domain.vo.CustomerVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.CustomerServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/20
 * @description:
 */
@RestController
@Api(tags = "客服链接Controller")
public class CustomerController extends BaseController {
    @Autowired
    private CustomerServiceImpl customerService;

    @PostMapping("/customer/list")
    @ApiOperation("客服列表")
    public ResponseResult<List<CustomerVO>> customerList() {
        return ResponseUtil.success(customerService.customerList());
    }

    @PostMapping("/customer/findOne")
    @ApiOperation("单个客服链接")
    public ResponseResult<CustomerOneVO> customerFindOne() {
        return ResponseUtil.success(customerService.customerFindOne());
    }

}
