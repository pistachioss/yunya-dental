package com.yunya.feign.discount.factory;

import com.yunya.feign.discount.*;
import com.yunya.feign.discount.domain.form.*;
import com.yunya.framework.common.model.*;

import javax.validation.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
public class RemoteDiscountFallBackFactory implements RemoteDiscountFeign {
    @Override
    public ResponseResult ownActiveCard(Integer patientId, @Valid OwnCardActiveForm form) {
        return null;
    }
}
