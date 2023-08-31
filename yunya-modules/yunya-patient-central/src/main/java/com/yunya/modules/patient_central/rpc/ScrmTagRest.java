package com.yunya.modules.patient_central.rpc;

import com.yunya.feign.patient_central.domain.vo.WxFansBindTagVO;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginTreeVo;
import com.yunya.feign.report.domain.query.PatientFrequencyOfTreatmentQuery;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.modules.patient_central.biz.PatientOriginBiz;
import com.yunya.modules.patient_central.biz.ScrmTagBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @auther: xy
 * @date: 2023/4/11
 */
@RestController
public class ScrmTagRest {

    @Resource
    private ScrmTagBiz scrmTagBiz;
    @Resource
    private PatientOriginBiz patientOriginBiz;

    @PostMapping(value = "/white/patient/tag/age")
    public Map<String, Set<WxFansBindTagVO>> ageTag(@RequestBody TreeMap<Integer, String> ageMap) {
        return scrmTagBiz.ageTag(ageMap);
    }

    @PostMapping(value = "/white/patient/tag/sex")
    public Map<String, Set<WxFansBindTagVO>> sexTag() {
        return scrmTagBiz.sexTag();
    }

    @PostMapping(value = "/white/patient/tag/disease")
    public Map<String, Set<WxFansBindTagVO>> diseaseTag(@RequestBody Map<String, Long> diseaseMap) {
        return scrmTagBiz.systemicDiseaseTag(diseaseMap);
    }

    @PostMapping(value = "/white/patient/tag/marry")
    public Map<String, Set<WxFansBindTagVO>> marryTag() {
        return scrmTagBiz.marryTag();
    }

    @PostMapping(value = "/white/patient/tag/child")
    public Map<String, Set<WxFansBindTagVO>> childTag() {
        return scrmTagBiz.childTag();
    }

    @PostMapping(value = "/white/patient/tag/fission")
    public Map<String, Set<WxFansBindTagVO>> fissionTag() {
        return scrmTagBiz.fissionTag();
    }

    @GetMapping("/white/patient/tag/activity-degree")
    public Map<String, Set<WxFansBindTagVO>> activityDegreeTag() {
        return scrmTagBiz.activityDegreeTag();
    }

    @PostMapping("/white/patient/tag/frequency-treatment")
    public Map<String, Set<WxFansBindTagVO>> frequencyOfTreatmentTag(@RequestBody PatientFrequencyOfTreatmentQuery query) {
        return scrmTagBiz.frequencyOfTreatmentTag(query);
    }

    @PostMapping(value = "/white/patient/tag/cost")
    public Map<String, Set<WxFansBindTagVO>> costTag() {
        return scrmTagBiz.costTag();
    }

    @PostMapping(value = "/white/patient/tag/hasitem")
    public Map<String, Set<WxFansBindTagVO>> hasItemTag() {
        return scrmTagBiz.hasItemTag();
    }

    @PostMapping(value = "/white/patient/tag/location")
    public Map<String, Set<WxFansBindTagVO>> locationTag() {
        return scrmTagBiz.locationTag();
    }

    @PostMapping(value = "/white/patient/tag/profession")
    public Map<String, Set<WxFansBindTagVO>> professionTag() {
        return scrmTagBiz.professionTag();
    }

    @PostMapping(value = "/white/patient/tag/dental")
    public Map<String, Set<WxFansBindTagVO>> dentalHistoryTag() {
        return scrmTagBiz.dentalHistoryTag();
    }

    @PostMapping(value = "/white/patient/tag/couponActive")
    public Map<String, Set<WxFansBindTagVO>> couponActiveTag() {
        return scrmTagBiz.couponActiveTag();
    }

    @PostMapping(value = "/white/patient/tag/treatment-tariff")
    public Map<String, Set<WxFansBindTagVO>> treatmentTariffTag(@RequestBody DateRangeQueryForm query) {
        return scrmTagBiz.treatmentTariffTag(query);
    }

    @PostMapping("/white/patient/tag/origin")
    public Map<String, Set<WxFansBindTagVO>> patientOriginTag(@RequestBody DateRangeQueryForm query) {
        return scrmTagBiz.patientOriginTag(query);
    }

    @PostMapping("/white/patient/tag/treatIntention")
    public Map<String, Set<WxFansBindTagVO>> patientTreatIntentionTag(@RequestBody DateRangeQueryForm query) {
        return scrmTagBiz.patientTreatIntentionTag(query);
    }

    @GetMapping("/white/patient/origin/tree")
    public List<PatientOriginTreeVo> findPatientOrignTree() {
        return patientOriginBiz.findPatientOriginTree();
    }
}
