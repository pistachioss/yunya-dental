package com.yunya.modules.system.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.BjDoctor;
import com.yunya.models.system.Company;
import com.yunya.models.system.QztDoctor;
import com.yunya.modules.system.biz.BjDoctorBiz;
import com.yunya.modules.system.biz.QztDoctorBiz;
import com.yunya.modules.system.domain.model.BjAddDoctorModel;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import com.yunya.modules.system.vo.BjDoctorDetailVO;
import com.yunya.modules.system.vo.QztDoctorDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "滨江麟康-用户")
@RestController
public class BjController {

    @Resource
    private BjDoctorBiz doctorBiz;

    @ApiOperation("完善医生认证信息")
    @PostMapping("/bj/doctor")
    @CurrentUser
    public ResponseResult<Boolean> complete(@RequestBody BjAddDoctorModel model) {
        doctorBiz.complete(model);
        return ResponseUtil.success();
    }


    @ApiOperation(value = "认证医生详情")
    @GetMapping("/bj/doctor/{userId}")
    public ResponseResult<BjDoctorDetailVO> certDetail(@PathVariable Integer userId) {
        return ResponseUtil.success(doctorBiz.detail(userId));
    }

    @GetMapping("/bj/doctor/list")
    public List<BjDoctor> certDoctors() {
        return doctorBiz.certDoctors();
    }

    @GetMapping("/bj/company/list")
    public List<Company> certCompanys() {
        return doctorBiz.certCompanys();
    }
}
