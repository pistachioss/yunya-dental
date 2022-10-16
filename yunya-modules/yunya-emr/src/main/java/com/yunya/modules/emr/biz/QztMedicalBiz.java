package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.emr.domain.vo.*;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.service.QztRestTemplateApi;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.MedicalCommonRecord;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 全诊通病例
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class QztMedicalBiz {

    @Resource
    private MedicalCommonRecordBiz medicalCommonRecordBiz;
    @Resource
    private QztRestTemplateApi qztRestTemplateApi;
    @Resource
    private RedisUtils redisUtils;
    @Value("${qzt.prefix}")
    private String qztPrefix;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private RemotePatientCentralServiceFeign patientCentralServiceFeign;

    public static final String MEDICAL_URL = "/docking/api/medical/addMedical?short-access-token=%s";
    public static final String TREATMENT_URL = "/docking/api/treatment/addTreatment?short-access-token=%s";

    /**
     * 同步全诊通
     */
    public void sync() {
        //查询已认证的医生
        List<QztDoctor> qztDoctors = systemServiceFeign.certDoctors();
        //过滤的医生ids
        Map<Integer, QztDoctor> doctorMap = certDoctorIds(qztDoctors);
        List<Integer> certClinicIds = certClinicIds(qztDoctors);
        String preDay = LocalDate.now().minusDays(1).toString();
        Example example = new Example(MedicalCommonRecord.class);
        if (Objects.isNull(doctorMap) || doctorMap.isEmpty() || CollectionUtils.isEmpty(certClinicIds)) {
            log.info("不存在认证医生:{}", qztDoctors);
            return;
        }
        example.createCriteria().andIn("status", Lists.newArrayList(0, 2))
                .andIn("majorDentistId", doctorMap.keySet())
                .andGreaterThanOrEqualTo("crtTime", preDay);
        example.orderBy("id").asc();
        List<MedicalCommonRecord> medicalCommonRecords = medicalCommonRecordBiz.selectByExample(example);
        String shortToken = redisUtils.get(RedisConstants.QZT_TOKEN);
        if (CollectionUtils.isEmpty(medicalCommonRecords) && StringUtils.isBlank(shortToken)) {
            log.info("昨天：{}，没有新增病例:{}", preDay, medicalCommonRecords.size());
            return;
        }
        Set<Integer> treatIds = medicalCommonRecords.stream().map(MedicalCommonRecord::getTreatmentId).collect(Collectors.toSet());
        List<TreatmentRecordExtendVO> treatmentRecords = treatmentServiceFeign.findTreatmentRecordByIds(treatIds);
        Map<Integer, TreatmentRecordExtendVO> treatOrgMap = treatmentRecords.stream()
                .collect(Collectors.toMap(TreatmentRecordExtendVO::getId, Function.identity(), (o, v) -> o));
        //过滤未认证门诊
        filterClinic(medicalCommonRecords, treatOrgMap, certClinicIds);
        List<Company> companyList = systemServiceFeign.certCompanys();
        Map<Integer, Company> orgMap = companyList.stream().collect(Collectors.toMap(Company::getId, Function.identity()));
        List<PatientBaseInfoVo> patients = patientCentralServiceFeign.findPatientInfoByIds(medicalCommonRecords.stream().map(MedicalCommonRecord::getPatientId).collect(Collectors.toList()));
        Map<Integer, PatientBaseInfoVo> patientMap = patients.stream().collect(Collectors.toMap(PatientBaseInfoVo::getId, Function.identity()));
        List<QztSyncMedicalVO> commonRecords = medicalCommonRecords.stream().
                filter(t -> {
                    QztDoctor doctor = doctorMap.get(t.getMajorDentistId());
                    PatientBaseInfoVo patientBaseInfoVo = patientMap.get(t.getPatientId());
                    TreatmentRecordExtendVO treatmentRecordExtendVO = treatOrgMap.get(t.getTreatmentId());
                    if (Objects.isNull(patientBaseInfoVo) || Objects.isNull(doctor)
                            || Objects.isNull(treatmentRecordExtendVO) || Objects.isNull(orgMap.get(treatmentRecordExtendVO.getOrgId()))) {
                        log.info("全诊通病例同步数据异常：{}", JSONObject.toJSONString(treatmentRecordExtendVO));
                        return false;
                    }
                    return true;
                }).map(t -> {
                    QztDoctor doctor = doctorMap.get(t.getMajorDentistId());
                    PatientBaseInfoVo patientBaseInfoVo = patientMap.get(t.getPatientId());
                    TreatmentRecordExtendVO treatmentRecordExtendVO = treatOrgMap.get(t.getTreatmentId());
                    Company company = orgMap.get(treatmentRecordExtendVO.getOrgId());
                    QztSyncMedicalVO medicalVO = new QztSyncMedicalVO();
                    medicalVO.setEntid(t.getId().toString());
                    medicalVO.setInstitutionId(company.getQztInstitutionCode());
                    medicalVO.setInstitutionName(company.getName());
                    medicalVO.setDepartmentName("口腔科");
                    medicalVO.setDoctorId(doctor.getId().toString());
                    medicalVO.setDoctorName(doctor.getDoctorName());
                    medicalVO.setUid(patientBaseInfoVo.getId().toString());
                    medicalVO.setName(patientBaseInfoVo.getName());
                    medicalVO.setGender(getGender(patientBaseInfoVo.getGender()));
                    medicalVO.setAge(Objects.isNull(patientBaseInfoVo.getAge()) ? 0 : patientBaseInfoVo.getAge());
                    medicalVO.setVisitDate(DateUtil.format(treatmentRecordExtendVO.getTreatStartTime()));
                    medicalVO.setComplain(t.getChiefComplaint());
                    medicalVO.setDiagnosis(t.getDiagnosis());
                    medicalVO.setDiagnosisIcd10("Z01.251");
                    medicalVO.setUpdateTm(DateUtil.format(t.getCrtTime(), "yyyy-MM-dd HH:mm:ss"));
                    medicalVO.setCreateTm(DateUtil.format(t.getCrtTime(), "yyyy-MM-dd HH:mm:ss"));
                    medicalVO.setTreatment(t.getTreatment());
                    return medicalVO;
                }).collect(Collectors.toList());
        log.info("全诊通病例数据同步开始，同步数量：{}", commonRecords.size());
        //全诊通单次同步上限500
        List<List<QztSyncMedicalVO>> partition = Lists.partition(commonRecords, 500);
        for (int i = 0; i < partition.size(); i++) {
            JSONObject jsonObject = qztRestTemplateApi.postObject(String.format(qztPrefix + MEDICAL_URL, shortToken), partition.get(i));
            log.info("第{}次全诊通病例数据同步完成：同步结果：{}", i, jsonObject);
        }

        //同步诊疗项目
        List<QztSyncItemVO> itemList = Lists.newArrayListWithCapacity(commonRecords.size());
        for (QztSyncMedicalVO commonRecord : commonRecords) {
            List<QztSyncItemDetailVO> treatmentList = JSONArray.parseArray(commonRecord.getTreatment()).stream().map(tt -> {
                String describe = ((JSONObject) tt).getString("describe");
                return StringUtils.substring(describe,0, 100);
            }).filter(StringUtils::isNotBlank).map(QztSyncItemDetailVO::new).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(treatmentList)) {
                continue;
            }
            QztSyncItemVO qztSyncItemVO = new QztSyncItemVO();
            qztSyncItemVO.setEntid(commonRecord.getEntid());
            qztSyncItemVO.setMedicalId(commonRecord.getEntid());
            qztSyncItemVO.setUid(commonRecord.getUid());
            qztSyncItemVO.setUserName(commonRecord.getName());
            qztSyncItemVO.setInstitutionId(commonRecord.getInstitutionId());
            qztSyncItemVO.setTreatmentItem(treatmentList);
            itemList.add(qztSyncItemVO);
        }
        log.info("全诊通诊疗项目数据同步开始，同步数量：{}", itemList.size());
        List<List<QztSyncItemVO>> itemSync = Lists.partition(itemList, 500);
        for (int i = 0; i < itemSync.size(); i++) {
            JSONObject jsonObject = qztRestTemplateApi.postObject(String.format(qztPrefix + TREATMENT_URL, shortToken), itemSync.get(i));
            log.info("第{}次全诊通诊疗项目数据同步完成：同步结果：{}", i, jsonObject);
        }
        log.info("{}，该天电子病例和诊疗项目已同步全诊通", preDay);
    }

    private Map<Integer, QztDoctor> certDoctorIds(List<QztDoctor> qztDoctors) {
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

    private List<Integer> certClinicIds(List<QztDoctor> qztDoctors) {
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
