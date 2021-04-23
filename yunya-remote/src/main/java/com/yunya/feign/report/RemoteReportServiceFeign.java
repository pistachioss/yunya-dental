package com.yunya.feign.report;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.report.domain.vo.BaseTreatmentProcessVO;
import com.yunya.feign.report.domain.vo.BenefitItemVo;
import com.yunya.feign.report.factory.RemoteReportServiceFactory;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_REPORT_ULTIMATE,
        fallbackFactory = RemoteReportServiceFactory.class)
public interface RemoteReportServiceFeign {
    /**
     * APP端就诊列表
     * @param query 查询参数
     * @return 分页实体
     */
    @Deprecated
    @RequestMapping(value = "api/app/treatment/list", method = RequestMethod.POST)
    PageInfo<BaseTreatmentProcessVO> treatmentList4App(@RequestBody TreatmentList4AppQuery query);

    @PostMapping("/api/card/{cardId}/item/usage")
    List<BenefitItemVo> listWxCouponsUseItem(@PathVariable(value = "cardId") Integer cardId);

    @PostMapping("/api/card/{noticeType}/push/list")
    List<WxTemplateMsgModel> listPushCard(@PathVariable(value = "noticeType") Integer noticeType);
}
