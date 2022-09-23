package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.QztDoctorBiz;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(value = "全诊通")
@RestController
public class QztController {

    @Resource
    private QztDoctorBiz doctorBiz;

    @ApiOperation("新增认证医生")
    @PostMapping("/qzt/doctor")
    public ResponseResult<Boolean> add(@RequestBody QztAddDoctorModel model) {
        doctorBiz.add(model);
        return ResponseUtil.success();
    }

    @ApiOperation("全诊通下拉选项")
    @GetMapping("/qzt/select")
    public ResponseResult<Map<String, Map<String, String>>> select() {
        return ResponseUtil.success(doctorBiz.select());
    }


}
