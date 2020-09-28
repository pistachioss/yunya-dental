package com.yunya.feign.expand.feign;

import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author bruce
 * @date 2020/7/24
 */
@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_EMPLOYEE_EXPAND)
public interface ClinicEmployeeConfigFeign {

}
