package com.yunya.modules.patient_central.rpc;

import com.yunya.feign.patient_central.domain.vo.WxFansBindTagVO;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.modules.patient_central.biz.ScrmTagBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @PostMapping("/white/patient/tag/activity-degree")
    public Map<String, Set<WxFansBindTagVO>> activityDegreeTag() {
        return scrmTagBiz.activityDegreeTag();
    }

    @PostMapping("/white/patient/tag/frequency-treatment")
    public Map<String, Set<WxFansBindTagVO>> frequencyOfTreatmentTag(DateRangeQueryForm query) {
        return scrmTagBiz.frequencyOfTreatmentTag(query);
    }
}
