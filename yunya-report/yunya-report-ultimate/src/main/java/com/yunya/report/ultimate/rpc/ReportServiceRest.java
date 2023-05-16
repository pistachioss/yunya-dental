package com.yunya.report.ultimate.rpc;

import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientBehaviorTagVO;
import com.yunya.feign.report.domain.vo.BenefitItemVo;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.models.report.CreditsShop;
import com.yunya.report.ultimate.biz.BaseTreatmentProcessBiz;
import com.yunya.report.ultimate.biz.DiscountBiz;
import com.yunya.report.ultimate.biz.PatientBaseInfoBiz;
import com.yunya.report.ultimate.biz.tag.PatientTreatmentTagBiz;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api("报表服务接口暴露")
@RestController
@RequestMapping("api")
public class ReportServiceRest {
    @Resource
    private DiscountBiz discountBiz;
    @Resource
    private BaseTreatmentProcessBiz baseTreatmentProcessBiz;
    @Resource
    private PatientBaseInfoBiz patientBaseInfoBiz;
    @Resource
    private PatientTreatmentTagBiz patientTreatmentTagBiz;

    @PostMapping("/card/{cardId}/item/usage")
    public List<BenefitItemVo> listWxCouponsUseItem(@PathVariable(value = "cardId") Integer cardId) {
        return discountBiz.listWxCouponsUseItem(cardId);
    }

    @PostMapping("/card/{noticeType}/push/list")
    List<WxTemplateMsgModel> listPushCard(@PathVariable(value = "noticeType") Integer noticeType) {
        return discountBiz.pushCard(noticeType);
    }

    @GetMapping("/appoint/confirm/push/list")
    List<WxTemplateMsgModel> listPushConfirmAppoint() {
        return baseTreatmentProcessBiz.listAppointConfirmPush();
    }

    @RequestMapping(value = "/patient/likePatient",method = RequestMethod.POST)
    List<PatientBaseInfoVo> findPatientLikePatientInfo(@RequestBody PatientLikeFinleQueryForm form) {
        return baseTreatmentProcessBiz.findPatientLikePatientInfo(form);
    }

    @GetMapping("/patient/{patientId}/lastPatientCredits")
    CreditsShop lastPatientCredits(@PathVariable("patientId") Integer patiendId) {
        return patientBaseInfoBiz.lastPatientCredits(patiendId);
    }

    /**
     * 患者的活跃度
     *
     * @return
     */
    @GetMapping("/patient/activity/day")
    public List<BasePatientBehaviorTagVO> findPatientDayOfLastVisit() {
        return patientTreatmentTagBiz.findPatientDayOfLastVisit();
    }


    /**
     * 患者的诊疗频率
     *
     * @param query
     * @return
     */
    @PostMapping("/patient/frequency-treatment")
    public List<BasePatientBehaviorTagVO> findPatientFrequencyOfTreatment(@RequestBody DateRangeQueryForm query) {
        return patientTreatmentTagBiz.findPatientFrequencyOfTreatment(query);
    }
}
