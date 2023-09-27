package com.yunya.modules.patient_central.biz;

import com.google.common.collect.Maps;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.emr.RemoteEmrServiceFeign;
import com.yunya.feign.patient_central.domain.vo.WxFansBindTagVO;
import com.yunya.feign.patient_central.domain.vo.web.PatientKinRecomVo;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.query.PatientFrequencyOfTreatmentQuery;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientBehaviorTagVO;
import com.yunya.feign.treatment.domain.vo.PatientCostInfoVO;
import com.yunya.feign.report.domain.vo.PatientHasBillItemVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.enums.ProfessionEnum;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientExpInfo;
import com.yunya.models.patient_central.PatientExtInfo;
import com.yunya.models.patient_central.WxFansBind;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.PatientBaseInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientExpInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    @Resource
    private RemoteEmrServiceFeign remoteEmrServiceFeign;
    @Resource
    private RemoteDiscountFeign remoteDiscountFeign;
    @Resource
    private PatientOriginBiz patientOriginBiz;
    @Resource
    private PatientExtInfoBiz patientExtInfoBiz;

    private static final  Map<String, String> location = new HashMap<>();
    static {
        location.put("330102", "杭州市上城区");
        location.put("330105", "杭州市拱墅区");
        location.put("330106", "杭州市西湖区");
        location.put("330108", "杭州市滨江区");
        location.put("330109", "杭州市萧山区");
        location.put("330110", "杭州市余杭区");
        location.put("330111", "杭州市富阳区");
        location.put("330112", "杭州市临安区");
        location.put("330113", "杭州市临平区");
        location.put("330114", "杭州市钱塘区");
        location.put("330122", "杭州市桐庐县");
        location.put("330127", "杭州市淳安县");
        location.put("330182", "杭州市建德市");
    }


    public Map<String, Set<WxFansBindTagVO>> ageTag(TreeMap<Integer, String> ageMap) {
        List<PatientBaseInfo> baseInfos = getAll();
        if (CollectionUtils.isEmpty(baseInfos)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientWx(baseInfos);
        Date now = new Date();
        return baseInfos.stream()
                .filter(t -> Objects.nonNull(t.getBirthday()) && t.getBirthday().before(now))
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

    /**
     * 裂变能力
     * @return
     */
    public Map<String, Set<WxFansBindTagVO>> costTag() {
        List<PatientCostInfoVO> costAll = getCostAll();
        if (CollectionUtils.isEmpty(costAll)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientCostWx(costAll);
        return costAll.stream()
                .collect(groupingBy(t -> t.getCumulativeConsumption().doubleValue() <= 20000 ? "低消费能力" : t.getCumulativeConsumption().doubleValue() > 50000 ? "高消费能力" : "中消费能力", collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getPatientId());
                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    /**
     * 定期维护
     * @return
     */
    public Map<String, Set<WxFansBindTagVO>> hasItemTag() {
        List<PatientHasBillItemVO> hasItemAll = getHasItemAll();
        if (CollectionUtils.isEmpty(hasItemAll)) {
            return Maps.newHashMap();
        }
        Map<String, Set<WxFansBindTagVO>> rst = new HashMap<>();
        List<PatientHasBillItemVO> hasItemAll1 = hasItemAll.stream().filter(t->t.getHad1() > 0).collect(toList());
        List<PatientHasBillItemVO> hasItemAll2 = hasItemAll.stream().filter(t->t.getHad2() > 0).collect(toList());

        if (!CollectionUtils.isEmpty(hasItemAll1)) {
            Map<Integer, String> patientWx1 = getPatientHasItemWx(hasItemAll1);
            rst.put("电动牙刷", hasItemAll1.stream()
                    .map(t -> {
                        WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                        bindTagVO.setPatientId(t.getPatientId());
                        bindTagVO.setUnionId(patientWx1.get(t.getPatientId()));
                        return bindTagVO;
                    }).collect(toSet()));
        }

        if (!CollectionUtils.isEmpty(hasItemAll2)) {
            List<WxFansBindTagVO> patientWx2 = getPatientHasItemWx2(hasItemAll2);
            rst.put("待洁牙", new HashSet<>(patientWx2));
        }
        return rst;
    }

    public Map<String, Set<WxFansBindTagVO>> locationTag() {
        List<PatientExpInfo> expInfos = getExpAll();
        if (CollectionUtils.isEmpty(expInfos)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientExpWx(expInfos);
        return expInfos.stream()
                .filter(t -> Objects.nonNull(t.getCountry()))
                .collect(groupingBy(t -> location.getOrDefault(t.getCountry(), "杭州市外"), collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getPatientId());
                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    public Map<String, Set<WxFansBindTagVO>> professionTag() {
        Map<Integer, String> itemMap = mapDict("职业", null);
        List<PatientExpInfo> expInfos = getExpAll();
        if (CollectionUtils.isEmpty(expInfos)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientExpWx(expInfos);
        return expInfos.stream()
                .filter(t -> Objects.nonNull(t.getProfession()) && itemMap.containsKey(t.getProfession()))
                .collect(groupingBy(t -> ProfessionEnum.getValue(itemMap.get(t.getProfession())), collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getPatientId());
                    bindTagVO.setUnionId(patientWx.get(t.getPatientId()));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    public Map<String, Set<WxFansBindTagVO>> dentalHistoryTag() {
        List<Integer> patientIds = remoteEmrServiceFeign.listOrthodonticsPatient();
        if (CollectionUtils.isEmpty(patientIds)) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = mapWxPatient(patientIds);
        return patientIds.stream()
                .collect(groupingBy(t -> "正畸史", collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t);
                    bindTagVO.setUnionId(patientWx.get(t));
                    return bindTagVO;
                }).collect(toSet()))));
    }

    public Map<String, Set<WxFansBindTagVO>> couponActiveTag() {
        List<PatientBaseInfo> baseInfos = getAll();
        Map<Integer, Long> patientMap = remoteDiscountFeign.listTwoYearsActive();
        if (patientMap.isEmpty()) {
            return Maps.newHashMap();
        }
        Map<Integer, String> patientWx = getPatientWx(baseInfos);
        return baseInfos.stream()
                .collect(groupingBy(t -> patientMap.containsKey(t.getId()) ? "敏感" : "不敏感", collectingAndThen(toList(), list -> list.stream().map(t -> {
                    WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
                    bindTagVO.setPatientId(t.getId());
                    bindTagVO.setUnionId(patientWx.get(t.getId()));
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

    private List<PatientCostInfoVO> getCostAll() {
        return remoteReportServiceFeign.getCostList();
    }

    private List<PatientHasBillItemVO> getHasItemAll() {
        return remoteReportServiceFeign.getHasItemList();
    }

    private Map<Integer, String> mapDict(String type, Map<String, Long> tagMap) {
        List<DictionaryItem> item = systemServiceFeign.findDictItemByTypeName(type);
        if (CollectionUtils.isEmpty(item)) {
            return Maps.newHashMap();
        }
        if (Objects.nonNull(tagMap)) {
            return item.stream().filter(t -> tagMap.containsKey(t.getName()) || filterTag(t, tagMap))
                    .collect(toMap(DictionaryItem::getId, DictionaryItem::getName));
        }
        return item.stream().collect(toMap(DictionaryItem::getId, DictionaryItem::getName));
    }

    private boolean filterTag(DictionaryItem item, Map<String, Long> tagMap) {
        Set<String> tagNames = tagMap.keySet();
        String itemName = item.getName();
        return tagNames.stream().anyMatch(itemName::startsWith);
    }

    private String mergeTag(String name, Map<String, Long> tagMap) {
        List<String> list = Arrays.asList("心绞痛、心肌梗塞、心脏瓣膜缺损、人工瓣膜、心脏起搏器".split("、"));
        if (list.contains(name)) {
            return "心脏病";
        }
        if (Objects.equals("糖尿病(I，II型）", name)) {
            return "糖尿病";
        }
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

    private Map<Integer, String> getPatientCostWx(List<PatientCostInfoVO> costInfoVOList) {
        Set<Integer> patientIds = costInfoVOList.stream().map(PatientCostInfoVO::getPatientId).collect(toSet());
        return mapWxPatient(patientIds);
    }

    private Map<Integer, String> getPatientHasItemWx(List<PatientHasBillItemVO> hasItemVOList) {
        Set<Integer> patientIds = hasItemVOList.stream().map(PatientHasBillItemVO::getPatientId).collect(toSet());
        return mapWxPatient(patientIds);
    }

    private List<WxFansBindTagVO> getPatientHasItemWx2(List<PatientHasBillItemVO> hasItemVOList) {
        Set<Integer> patientIds = hasItemVOList.stream().map(PatientHasBillItemVO::getPatientId).collect(toSet());
        return mapWxPatientNotIn(patientIds);
    }

    private Map<Integer, String> mapWxPatient(Collection<Integer> patientIds) {
        Map<Integer, String> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(patientIds)) {
            List<WxFansBind> wxFansBinds = wxFansBindBiz.listWxByPatientIds(patientIds);
            map = wxFansBinds.stream().filter(t -> StringUtils.isNotBlank(t.getUnionId()))
                    .collect(toMap(WxFansBind::getPatientId, WxFansBind::getUnionId, (o, v) -> o));
        }
        return map;
    }

    private List<WxFansBindTagVO> mapWxPatientNotIn(Set<Integer> patientIds) {
        List<WxFansBind> wxFansBinds = wxFansBindBiz.listWxByNotPatientIds(patientIds);
        List<WxFansBindTagVO> rst = wxFansBinds.stream().map(t -> {
            WxFansBindTagVO bindTagVO = new WxFansBindTagVO();
            bindTagVO.setPatientId(t.getPatientId());
            bindTagVO.setUnionId(t.getUnionId());
            return bindTagVO;
        }).collect(toList());
        return rst;
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
    public Map<String, Set<WxFansBindTagVO>> frequencyOfTreatmentTag(PatientFrequencyOfTreatmentQuery query) {
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

    /**
     * 根据条件查询患者的特定治疗项目标签
     *
     * @param query
     * @return
     */
    public Map<String, Set<WxFansBindTagVO>> treatmentTariffTag(DateRangeQueryForm query) {
        List<BasePatientBehaviorTagVO> patients = remoteReportServiceFeign.findPatientTreatmentTariffTag(query);
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

    public Map<String, Set<WxFansBindTagVO>> patientOriginTag(DateRangeQueryForm query) {
        List<BasePatientBehaviorTagVO> patients = patientOriginBiz.findPatientOriginChangeTag(query);
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

    public Map<String, Set<WxFansBindTagVO>> patientTreatIntentionTag(DateRangeQueryForm query) {
        List<BasePatientBehaviorTagVO> patients = patientExtInfoBiz.findPatientTreatIntentionChangeTag(query);
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
