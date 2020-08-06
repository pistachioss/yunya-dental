package com.yunya.feign.expand.feign;

import com.yunya.feign.expand.model.request.ClinicEmployeeConfigQueryReq;
import com.yunya.feign.expand.model.response.ClinicEmployeeConfigRes;
import com.yunya.feign.expand.model.response.EnableEmployeeRes;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author bruce
 * @date 2020/7/24
 */
@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_EMPLOYEE_EXPAND)
public interface ClinicEmployeeConfigFeign {

    /**
     * 门诊员工配置详情
     * @param req
     * @return
     */
    @PostMapping("clinic/employee/config/detail")
    public ClinicEmployeeConfigRes getEmployeeConfigRecord(@Valid @RequestBody ClinicEmployeeConfigQueryReq req);

    /**
     * 查询可预约，可挂号医生
     * @param clinicId
     * @return
     */
    @GetMapping("clinic/employee/{clinicId}/config/list")
    public EnableEmployeeRes getEnableEmployeeList(@PathVariable(value = "clinicId") Integer clinicId);
}
