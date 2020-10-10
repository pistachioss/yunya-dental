package com.yunya.feign.expand;

import com.yunya.feign.expand.model.response.EnableEmployeeRes;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author bruce
 * @date 2020/7/24
 */
@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_EMPLOYEE_EXPAND)
public interface RemoteClinicEmployeeConfigFeign {

    /**
     * 查询可预约，可挂号医生
     * @param clinicId 组织id
     * @return EnableEmployeeRes
     */
    @GetMapping("/api/{clinicId}/config/list")
    public EnableEmployeeRes getEnableEmployeeList(@PathVariable(value = "clinicId") Integer clinicId);
}
