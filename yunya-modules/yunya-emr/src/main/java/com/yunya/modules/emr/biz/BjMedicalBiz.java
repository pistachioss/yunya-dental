package com.yunya.modules.emr.biz;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.emr.domain.vo.BjSyncItemDetailVO;
import com.yunya.feign.emr.domain.vo.BjSyncItemVO;
import com.yunya.feign.emr.domain.vo.BjSyncMedicalVO;
import com.yunya.feign.emr.domain.vo.QztSyncItemDetailVO;
import com.yunya.feign.emr.domain.vo.QztSyncItemVO;
import com.yunya.feign.emr.domain.vo.QztSyncMedicalVO;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.service.BjRestTemplateApi;
import com.yunya.framework.common.service.QztRestTemplateApi;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.system.BjDoctor;
import com.yunya.models.system.Company;
import com.yunya.models.system.QztDoctor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 全诊通病例
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class BjMedicalBiz {

    @Resource
    private MedicalCommonRecordBiz medicalCommonRecordBiz;
    @Resource
    private BjRestTemplateApi bjRestTemplateApi;
    @Resource
    private RedisUtils redisUtils;
    @Value("${bj.prefix}")
    private String qztPrefix;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private RemotePatientCentralServiceFeign patientCentralServiceFeign;

    public static final String MEDICAL_URL = "/api/outer/medicalrecord/save?his-auth=%s";
    public static final String TREATMENT_URL = "/api/outer/prescription/save?his-auth=%s";

    /**
     * 同步全诊通
     */
    public void sync() {
        //查询已认证的医生
        List<BjDoctor> qztDoctors = systemServiceFeign.certBjDoctors();
        //过滤的医生ids
        Map<Integer, BjDoctor> doctorMap = certDoctorIds(qztDoctors);
        List<Integer> certClinicIds = certClinicIds(qztDoctors);
        String preDay = LocalDate.now().minusDays(1).toString();
        Example example = new Example(MedicalCommonRecord.class);
        if (Objects.isNull(doctorMap) || doctorMap.isEmpty() || CollectionUtils.isEmpty(certClinicIds)) {
            log.info("滨江不存在认证医生:{}", qztDoctors);
            return;
        }
        example.createCriteria().andIn("status", Lists.newArrayList(0, 2))
                .andIn("majorDentistId", doctorMap.keySet())
                .andGreaterThanOrEqualTo("crtTime", preDay);
        example.orderBy("id").asc();
        List<MedicalCommonRecord> medicalCommonRecords = medicalCommonRecordBiz.selectByExample(example);
        String shortToken = redisUtils.get(RedisConstants.BJ_TOKEN);
        if (CollectionUtils.isEmpty(medicalCommonRecords) && StringUtils.isBlank(shortToken)) {
            log.info("滨江昨天：{}，没有新增病例:{}", preDay, medicalCommonRecords.size());
            return;
        }
        Set<Integer> treatIds = medicalCommonRecords.stream().map(MedicalCommonRecord::getTreatmentId).collect(Collectors.toSet());
        List<TreatmentRecordExtendVO> treatmentRecords = treatmentServiceFeign.findTreatmentRecordByIds(treatIds);
        Map<Integer, TreatmentRecordExtendVO> treatOrgMap = treatmentRecords.stream()
                .collect(Collectors.toMap(TreatmentRecordExtendVO::getId, Function.identity(), (o, v) -> o));
        //过滤未认证门诊
        filterClinic(medicalCommonRecords, treatOrgMap, certClinicIds);
        List<Company> companyList = systemServiceFeign.certBjCompanys();
        Map<Integer, Company> orgMap = companyList.stream().collect(Collectors.toMap(Company::getId, Function.identity()));
        List<PatientBaseInfoVo> patients = patientCentralServiceFeign.findPatientInfoByIds(medicalCommonRecords.stream().map(MedicalCommonRecord::getPatientId).collect(Collectors.toList()));
        Map<Integer, PatientBaseInfoVo> patientMap = patients.stream().collect(Collectors.toMap(PatientBaseInfoVo::getId, Function.identity()));
        List<BjSyncMedicalVO> commonRecords = medicalCommonRecords.stream().
                filter(t -> {
                    BjDoctor doctor = doctorMap.get(t.getMajorDentistId());
                    PatientBaseInfoVo patientBaseInfoVo = patientMap.get(t.getPatientId());
                    TreatmentRecordExtendVO treatmentRecordExtendVO = treatOrgMap.get(t.getTreatmentId());
                    if (Objects.isNull(patientBaseInfoVo) || Objects.isNull(doctor)
                            || Objects.isNull(treatmentRecordExtendVO) || Objects.isNull(orgMap.get(treatmentRecordExtendVO.getOrgId()))) {
                        log.info("b滨江病例同步数据异常：{}", JSONObject.toJSONString(treatmentRecordExtendVO));
                        return false;
                    }
                    return true;
                }).map(t -> {
                    BjDoctor doctor = doctorMap.get(t.getMajorDentistId());
                    PatientBaseInfoVo patientBaseInfoVo = patientMap.get(t.getPatientId());
                    TreatmentRecordExtendVO treatmentRecordExtendVO = treatOrgMap.get(t.getTreatmentId());
                    Company company = orgMap.get(treatmentRecordExtendVO.getOrgId());
                    BjSyncMedicalVO medicalVO = new BjSyncMedicalVO();
                    medicalVO.setMedicalRecordId(t.getId().toString());
                    medicalVO.setPatientName(patientBaseInfoVo.getName());
                    medicalVO.setPatientSex(getGender(patientBaseInfoVo.getGender()));
                    medicalVO.setOrganizationCode(company.getBjInstitutionCode());
                    medicalVO.setDepartmentName("口腔科");
                    medicalVO.setDoctorId(doctor.getId().toString());
                    medicalVO.setDoctorName(doctor.getDoctorName());
                    medicalVO.setMedicalTime(DateUtil.format(treatmentRecordExtendVO.getTreatStartTime(), "yyyy-MM-dd HH:mm:ss"));
                    medicalVO.setMedicalType(0);
                    medicalVO.setFirstVisit(t.getType());
                    medicalVO.setDiagnose(t.getChiefComplaint());
                    medicalVO.setIcd10("Z01.251");
                    medicalVO.setDiseaseDesc(t.getPrescription());
                    medicalVO.setPastHistory(t.getPastHistory());
                    String describe = JSONArray.parseArray(t.getTreatment()).getJSONObject(0).getString("describe");
                    medicalVO.setOperation(describe);
                    return medicalVO;
                }).collect(Collectors.toList());
        log.info("滨江病例数据同步开始，同步数量：{}", commonRecords.size());
        //全诊通单次同步上限500
        List<List<BjSyncMedicalVO>> partition = Lists.partition(commonRecords, 500);
        for (int i = 0; i < partition.size(); i++) {
            JSONObject jsonObject = bjRestTemplateApi.postObject(String.format(qztPrefix + MEDICAL_URL, shortToken), partition.get(i));
            log.info("第{}次滨江病例数据同步完成：同步结果：{}", i, jsonObject);
        }

        //同步诊疗项目
        List<BjSyncItemVO> itemList = Lists.newArrayListWithCapacity(commonRecords.size());
        for (BjSyncMedicalVO commonRecord : commonRecords) {
            BjSyncItemVO qztSyncItemVO = new BjSyncItemVO();
            qztSyncItemVO.setPrescriptionId(commonRecord.getMedicalRecordId());
            qztSyncItemVO.setMedicalRecordId(commonRecord.getMedicalRecordId());
            qztSyncItemVO.setOrganizationCode(commonRecord.getOrganizationCode());
            qztSyncItemVO.setPrescriptionTime(commonRecord.getMedicalTime());
            qztSyncItemVO.setPrescriptionCategory("牙科");
            BjSyncItemDetailVO itemDetailVO = new BjSyncItemDetailVO();
            itemDetailVO.setPrescriptionItemId(RandomUtil.randomNumbers(9));
            itemDetailVO.setGroupNo("001");
            itemDetailVO.setDrugName("牙科");
            itemDetailVO.setDrugGenericName("初诊检查");
            itemDetailVO.setDrugConsumption("1");
            itemDetailVO.setDrugSpecification("");
            itemDetailVO.setDrugManufacturer("");
            itemDetailVO.setDrugUsages("");
            itemDetailVO.setDrugUsagesDays("");
            itemDetailVO.setDrugUsagesFrequency("");
            itemDetailVO.setDrugConsumptionUnit("");
            qztSyncItemVO.setDrugList(Collections.singletonList(itemDetailVO));
            itemList.add(qztSyncItemVO);
        }
        log.info("滨江诊疗项目数据同步开始，同步数量：{}", itemList.size());
        List<List<BjSyncItemVO>> itemSync = Lists.partition(itemList, 500);
        for (int i = 0; i < itemSync.size(); i++) {
            JSONObject jsonObject = bjRestTemplateApi.postObject(String.format(qztPrefix + TREATMENT_URL, shortToken), itemSync.get(i));
            log.info("第{}次滨江诊疗项目数据同步完成：同步结果：{}", i, jsonObject);
        }
        log.info("{}，该天电子病例和诊疗项目已同步滨江", preDay);
    }

    private Map<Integer, BjDoctor> certDoctorIds(List<BjDoctor> qztDoctors) {
        return qztDoctors.stream()
                .reduce(Maps.newHashMap()
                        , (u, t) -> {
                            Set<Integer> userIds = Lists.newArrayList(Splitter.on(",").split(t.getRelateUserIds()))
                                    .stream()
                                    .map(Integer::valueOf).collect(Collectors.toSet());
                            u.putAll(userIds.stream().collect(Collectors.toMap(Function.identity(), a -> t, (o, n) -> o)));
                            return u;
                        }
                        , (u, t) -> u);
    }

    private List<Integer> certClinicIds(List<BjDoctor> qztDoctors) {
        return qztDoctors.stream()
                .reduce(Lists.newArrayList()
                        , (u, t) -> {
                            u.addAll(Lists.newArrayList(Splitter.on(",").split(t.getPracticeClinic())).stream().map(Integer::valueOf).collect(Collectors.toSet()));
                            return u;
                        }, (u, t) -> u);
    }

    private void filterClinic(List<MedicalCommonRecord> medicalCommonRecords, Map<Integer, TreatmentRecordExtendVO> treatOrgMap
            , List<Integer> certClinicIds) {
        medicalCommonRecords.removeIf(t -> {
            TreatmentRecordExtendVO treatmentRecordExtendVO = treatOrgMap.get(t.getTreatmentId());
            boolean b = Objects.isNull(treatmentRecordExtendVO) || !certClinicIds.contains(treatmentRecordExtendVO.getOrgId());
            if (b) {
                log.info("该门诊未开启同步：{}", t);
                return true;
            } else {
                return false;
            }
        });
    }

    private Integer getGender(Byte gender) {
        int qztGender = 1;
        //0-男；1-女；2-未知
        if (Objects.isNull(gender) || Objects.equals(1, gender.intValue())) {
            qztGender = 2;
        }
        return qztGender;
    }

}
