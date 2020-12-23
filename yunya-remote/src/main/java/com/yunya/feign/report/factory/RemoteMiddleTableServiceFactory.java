package com.yunya.feign.report.factory;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.RemoteMiddleTableServiceFeign;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.models.report.BaseTreatmentProcess;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2020-12-23 12:41
 **/
public class RemoteMiddleTableServiceFactory implements RemoteMiddleTableServiceFeign {
    @Override
    public PageInfo treatmentList4App(TreatmentList4AppQuery query) {
        return null;
    }
}
