package com.yunya.feign.discount;

import com.yunya.feign.discount.domain.form.*;
import com.yunya.feign.system.factory.*;
import com.yunya.framework.common.annation.*;
import com.yunya.framework.common.constant.*;
import com.yunya.framework.common.model.*;
import io.swagger.annotations.*;
import org.springframework.cloud.openfeign.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_DISCOUNT,
        fallbackFactory = RemoteSystemServiceFallBackFactory.class)
public interface RemoteDiscountFeign {

    @ApiOperation(value = "患者档案-产品管理-激活-自有平台激活")
    @PutMapping("/patient/{id}/product/card/owner/activation")
    @CurrentUser
    public ResponseResult ownActiveCard(@PathVariable(value = "id") Integer patientId, @Valid @RequestBody OwnCardActiveForm form);
}
