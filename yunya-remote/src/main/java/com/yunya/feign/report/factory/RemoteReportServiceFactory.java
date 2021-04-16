package com.yunya.feign.report.factory;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.report.domain.vo.BenefitItemVo;

import java.util.List;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2020-12-23 12:41
 **/
public class RemoteReportServiceFactory implements RemoteReportServiceFeign {
    @Override
    public PageInfo treatmentList4App(TreatmentList4AppQuery query) {
        return null;
    }

    @Override
    public List<BenefitItemVo> listWxCouponsUseItem(Integer cardId) {
        return null;
    }
}
