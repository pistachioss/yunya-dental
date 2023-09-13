package com.yunya.feign.report;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.report.domain.query.CategoryIncomeQuery;
import com.yunya.feign.report.domain.query.TreatmentList4AppQuery;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.report.factory.RemoteReportServiceFactory;
import com.yunya.feign.treatment.domain.vo.PatientCostInfoVO;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.CreditsShop;
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

    @GetMapping("/api/appoint/confirm/push/list")
    List<WxTemplateMsgModel> listPushConfirmAppoint();

    @RequestMapping(value = "/api/patient/likePatient",method = RequestMethod.POST)
    List<PatientBaseInfoVo> findPatientLikePatientInfo(@RequestBody PatientLikeFinleQueryForm form);

    @RequestMapping(value = "/api/patient/{patientId}/lastPatientCredits",method = RequestMethod.GET)
    CreditsShop lastPatientCredits(@PathVariable("patientId")Integer patiendId);

    /**
     * 患者的距末次就诊天数
     *
     * @return
     */
    @GetMapping("api/patient/activity/day")
    List<BasePatientBehaviorTagVO> findPatientDayOfLastVisit();

    /**
     * 患者的诊疗频率
     *
     * @param query
     * @return
     */
    @PostMapping("/api/patient/frequency-treatment")
    List<BasePatientBehaviorTagVO> findPatientFrequencyOfTreatment(@RequestBody DateRangeQueryForm query);

    @RequestMapping(value = "/api/patient/bill/costlist",method = RequestMethod.POST)
    List<PatientCostInfoVO> getCostList();

    @RequestMapping(value = "/api/patient/bill/hasitemlist",method = RequestMethod.POST)
    List<PatientHasBillItemVO> getHasItemList();

    @PostMapping(value = "/api/bill/detail/deduction")
    List<BaseBillDetail> billDeduction(@RequestBody CategoryIncomeQuery query);
}

