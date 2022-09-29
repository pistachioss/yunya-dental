package com.yunya.modules.system.controller;

import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.QztDoctor;
import com.yunya.modules.system.biz.QztDoctorBiz;
import com.yunya.modules.system.domain.model.QztAddDoctorModel;
import com.yunya.modules.system.vo.QztDoctorDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "全诊通-用户")
@RestController
public class QztController {

    @Resource
    private QztDoctorBiz doctorBiz;

    @ApiOperation("完善医生认证信息")
    @PostMapping("/qzt/doctor")
    @CurrentUser
    public ResponseResult<Boolean> complete(@RequestBody QztAddDoctorModel model) {
        doctorBiz.complete(model);
        return ResponseUtil.success();
    }

    @ApiOperation(value = "全诊通下拉选项，Map<k ,Map<k,v>>", notes = "返回key：technicalTitle-职称，scopePractice-职业范围，antibiosisAuthority-抗菌药物处方权")
    @GetMapping("/qzt/select")
    public ResponseResult<Map<String, Map<String, String>>> select() {
        return ResponseUtil.success(doctorBiz.select());
    }

    @ApiOperation(value = "认证医生详情")
    @GetMapping("/qzt/doctor/{userId}")
    public ResponseResult<QztDoctorDetailVO> certDetail(@PathVariable Integer userId) {
        return ResponseUtil.success(doctorBiz.detail(userId));
    }

    @GetMapping("/qzt/doctor/list")
    public List<QztDoctor> certDoctors() {
        return doctorBiz.certDoctors();
    }
}
