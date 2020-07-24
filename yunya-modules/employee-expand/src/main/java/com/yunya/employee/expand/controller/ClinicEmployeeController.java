package com.yunya.employee.expand.controller;


import com.yunya.employee.expand.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.employee.expand.model.request.ClinicEmployeeConfigReq;
import com.yunya.employee.expand.model.response.ClinicEmployeeConfigRes;
import com.yunya.employee.expand.model.response.EnableEmployeeRes;
import com.yunya.employee.expand.service.ClinicEmployeeConfigBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author bruce
 * @date 2020/7/10
 */
@Api(tags = {"门诊端员工管理"})
@RestController
public class ClinicEmployeeController {

    @Resource
    private ClinicEmployeeConfigBiz clinicEmployeeConfigBiz;

    /**
     *
     * 门诊员工配置
     *
     * @return
     */
    @ApiOperation("门诊端-诊所设置-员工设置-员工配置")
    @PutMapping("clinic/employee/config/{clinicId}/{employeeId}")
    public ResponseResult modifyEmployeeConfig(@PathVariable("clinicId") Integer clinicId,
                                               @PathVariable("employeeId") Integer employeeId,
                                               @RequestBody ClinicEmployeeConfigReq configRequest) {
        clinicEmployeeConfigBiz.modifyClinicEmployeeConfig(employeeId, clinicId, configRequest);
        return ResponseUtil.success();
    }

}
