package com.yunya.modules.patient_central.biz;

import com.google.common.collect.Maps;
import com.yunya.feign.patient_central.domain.vo.WxFansBindTagVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientExpInfo;
import com.yunya.models.patient_central.PatientExtInfo;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientExpInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;


/**
 * @auther: xy
 * @date: 2023/4/11
 */
@Service
public class ScrmTagBiz {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ScrmTagBiz.class);
    @Resource
    private PatientBaseInfoMapper patientBaseInfoMapper;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private WxFansBindBiz wxFansBindBiz;
    @Resource
    private PatientExtInfoBiz extInfoBiz;
    @Resource
    private PatientExpInfoMapper expInfoMapper;
    @Resource
    private RemoteReportServiceFeign remoteReportServiceFeign;

    public Map<String, Set<WxFansBindTagVO>> ageTag(TreeMap<Integer, String> ageMap) {
        List<PatientBaseInfo> baseInfos = getAll();
        if (CollectionUtils.isEmpty(baseInfos)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientWx(baseInfos);
        return baseInfos.stream()
                .filter(t -> Objects.nonNull(t.getBirthday()))
                .collect(groupingBy(t -> calAgeKey(ageMap, t), collectingAndThen(toList(), list -> list.stream().map(t1 -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t1.getId());
                    bindTagVO.setUnionId(patientWx.get(t1.getId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    public Map<String, Set<WxFansBindTagVO>> sexTag() {
        List<PatientBaseInfo> baseInfos = getAll();
        Map<Integer, String> patientWx = getPatientWx(baseInfos);
        List<Integer> sex = Arrays.asList(0, 1);
        Predicate<PatientBaseInfo> predicate = (t) -> Objects.nonNull(t.getGender())
                && sex.contains(t.getGender().intValue());
        return baseInfos.stream()
                .filter(predicate)
                .collect(groupingBy(t -> Objects.equals(0, t.getGender().intValue()) ? "男" : "女", collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getId());
                    bindTagVO.setUnionId(patientWx.get(t.getId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    public Map<String, Set<WxFansBindTagVO>> systemicDiseaseTag(Map<String, Long> tagMap) {
        Map<Integer, String> itemMap = mapDict("疾病史", tagMap);
        List<PatientExtInfo> extInfos = getExtAll(itemMap);
        if (CollectionUtils.isEmpty(extInfos)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientExtWx(extInfos);
        return extInfos.stream()
                .collect(groupingBy(t -> mergeTag(itemMap.get(t.getDictItemId()), tagMap), collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getPatientId());
                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    public Map<String, Set<WxFansBindTagVO>> marryTag() {
        List<PatientExpInfo> expInfos = getExpAll();
        if (CollectionUtils.isEmpty(expInfos)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientExpWx(expInfos);
        return expInfos.stream()
                .filter(t -> Objects.nonNull(t.getMarry()))
                .collect(groupingBy(t -> Objects.equals(0, t.getMarry()) ? "单身" : "已婚", collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getPatientId());
                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    public Map<String, Set<WxFansBindTagVO>> childTag() {
        List<PatientExpInfo> expInfos = getExpAll();
        if (CollectionUtils.isEmpty(expInfos)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientExpWx(expInfos);
        return expInfos.stream()
                .filter(t -> Objects.nonNull(t.getChildStatus()))
                .collect(groupingBy(t -> Objects.equals(0, t.getChildStatus()) ? "无孩子" : "有孩", collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getPatientId());
                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

  /**
   * 裂变能力
   * @return
   */
  public Map<String, Set<WxFansBindTagVO>> fissionTag() {
        List<PatientKinRecomVo> kinRecomVoList = getKinRecomQtyAll();
        if (CollectionUtils.isEmpty(kinRecomVoList)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientKinCommWx(kinRecomVoList);
        return kinRecomVoList.stream()
                .collect(groupingBy(t -> t.getSumQty() <= 2 ? "裂变能力弱" : t.getSumQty() >= 5 ? "koc" : "裂变能力一般", collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getPatientId());
                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    private String calAgeKey(TreeMap<Integer, String> ageMap, PatientBaseInfo baseInfo) {
        Date date = new Date();
        Integer between = DateUtil.differFromDate(baseInfo.getBirthday(), date);
        Integer age =  between < 1 ? 1 : between;
        // 获取最大的小于等于 num1 的 Key
        Integer key = ageMap.floorKey(age);
        if (Objects.isNull(key)) {
            log.error("患者年龄同步异常,生日:{},患者id:{}", baseInfo.getBirthday(), baseInfo.getId());
        }
        return ageMap.getOrDefault(key, "71");
    }

    private List<PatientBaseInfo> getAll() {
        return patientBaseInfoMapper.selectAll();
    }

    private List<PatientExtInfo> getExtAll(Map<Integer, String> itemMap) {
        return extInfoBiz.listByTagType(itemMap.keySet());
    }

    private List<PatientExpInfo> getExpAll() {
        return expInfoMapper.selectAll();
    }

    private List<PatientKinRecomVo> getKinRecomQtyAll() {
        return patientBaseInfoMapper.selectKinRecomByPatientId();
    }

    private Map<Integer, String> mapDict(String type, Map<String, Long> tagMap) {
        List<DictionaryItem> item = systemServiceFeign.findDictItemByTypeName(type);
        if (CollectionUtils.isEmpty(item)) {
            return Maps.newHashMap();
        }
        return item.stream().filter(t -> tagMap.containsKey(t.getName()) || filterTag(t, tagMap))
                .collect(toMap(DictionaryItem::getId, DictionaryItem::getName));
    }

    private boolean filterTag(DictionaryItem item, Map<String, Long> tagMap) {
        Set<String> tagNames = tagMap.keySet();
        String itemName = item.getName();
        return tagNames.stream().anyMatch(itemName::startsWith);
    }

    private String mergeTag(String name, Map<String, Long> tagMap) {
        Set<String> tagNames = tagMap.keySet();
        return tagNames.stream().filter(name::startsWith).findFirst().orElse("未知");
    }

    private Map<Integer, String> getPatientWx(List<PatientBaseInfo> baseInfos) {
        Set<Integer> patientIds = baseInfos.stream().map(PatientBaseInfo::getId).collect(toSet());
        return mapWxPatient(patientIds);
    }

    private Map<Integer, String> getPatientExtWx(List<PatientExtInfo> expInfos) {
        Set<Integer> patientIds = expInfos.stream().map(PatientExtInfo::getPatientId).collect(toSet());
        return mapWxPatient(patientIds);
    }

    private Map<Integer, String> getPatientExpWx(List<PatientExpInfo> expInfos) {
        Set<Integer> patientIds = expInfos.stream().map(PatientExpInfo::getPatientId).collect(toSet());
        return mapWxPatient(patientIds);
    }

    private Map<Integer, String> getPatientKinCommWx(List<PatientKinRecomVo> kinRecomVoList) {
        Set<Integer> patientIds = kinRecomVoList.stream().map(PatientKinRecomVo::getPatientId).collect(toSet());
        return mapWxPatient(patientIds);
    }

    private Map<Integer, String> mapWxPatient(Set<Integer> patientIds) {
        Map<Integer, String> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(patientIds)) {
            List<WxFansBind> wxFansBinds = wxFansBindBiz.listWxByPatientIds(patientIds);
            map = wxFansBinds.stream().collect(toMap(WxFansBind::getPatientId, WxFansBind::getUnionId, (o, v) -> o));
        }
        return map;
    }

    /**
     * 活跃度标签待打患者
     *
     * @return
     */
    public Map<String, Set<WxFansBindTagVO>> activityDegreeTag() {
        List<BasePatientBehaviorTagVO> patients = remoteReportServiceFeign.findPatientDayOfLastVisit();
        Map<Integer, String> patientWx = mapWxPatient(patients.stream().map(BasePatientBehaviorTagVO::getPatientId).collect(toSet()));
        return patients.stream()
                .collect(
                    groupingBy(
                        patient->patient.getTagName(),
                        collectingAndThen(toList(), list -> list.stream().map(t -> {
                            WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                            bindTagVO.setPatientId(t.getPatientId());
                            bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                            return bindTagVO;
                        }).collect(toSet()))
                    ));
    }

    /**
     * 诊疗频率标签待打的患者列表
     *
     * @param query
     * @return
     */
    public Map<String, Set<WxFansBindTagVO>> frequencyOfTreatmentTag(DateRangeQueryForm query) {
        List<BasePatientBehaviorTagVO> patients = remoteReportServiceFeign.findPatientFrequencyOfTreatment(query);
        Map<Integer, String> patientWx = mapWxPatient(patients.stream().map(BasePatientBehaviorTagVO::getPatientId).collect(toSet()));
        return patients.stream()
                .collect(
                        groupingBy(
                                patient->patient.getTagName(),
                                collectingAndThen(toList(), list -> list.stream().map(t -> {
                                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                                    bindTagVO.setPatientId(t.getPatientId());
                                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                                    return bindTagVO;
                                }).collect(toSet()))
                        ));
    }
}
