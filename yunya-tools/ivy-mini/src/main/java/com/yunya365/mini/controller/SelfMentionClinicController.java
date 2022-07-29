package com.yunya365.mini.controller;

import com.yunya.feign.appointment.domain.query.ClinicListQuery;
import com.yunya.feign.ivy_mini.domain.vo.SelfMentionClinicVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.impl.SelfMentionClinicServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/28
 * @description:
 */
@RestController
@Api(tags = "小程序-可自提门诊管理")
public class SelfMentionClinicController  extends BaseController{

    @Resource
    private SelfMentionClinicServiceImpl selfMentionClinicService;

    @PostMapping("/selfMentionClinic/findlist")
    @ApiOperation("小程序-请选择可自提门诊")
    public ResponseResult<List<SelfMentionClinicVO>> findAppList(@RequestBody
                                                                  @Validated ClinicListQuery query) {
        return ResponseUtil.success(selfMentionClinicService.findAppList(query));
    }


}
