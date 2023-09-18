package com.yunya.feign.middle.factory;

import com.yunya.feign.middle.RemoteMiddleServiceFeign;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.discount.CouponChangeRecord;
import com.yunya.models.report.CreditsShop;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2020-12-23 12:41
 **/
public class RemoteMiddleServiceFactory implements RemoteMiddleServiceFeign {

    @Override
    public ResponseResult<CreditsShop> lastPatientCredits(Integer patientId) {
        return null;
    }

    @Override
    public ResponseResult<Boolean> occur(CouponChangeRecord newBean) {
        return null;
    }

    @Override
    public ResponseResult<Boolean> occurDelete(CouponChangeRecord newBean) {
        return null;
    }
}
