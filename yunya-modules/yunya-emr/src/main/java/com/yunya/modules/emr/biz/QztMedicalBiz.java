package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.emr.domain.vo.QztSyncMedicalVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.service.QztRestTemplateApi;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.MedicalCommonRecord;
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

    public static final String DOCTOR_URL = "/docking/api/medical/addMedical?short-access-token=%s";

    /**
     * 同步全诊通
     */
    public void sync() {
        //查询已认证的医生
        List<QztDoctor> qztDoctors = systemServiceFeign.certDoctors();
        //过滤的医生ids
        Map<Integer, Integer> doctorMap = certDoctorIds(qztDoctors);
        List<Integer> certClinicIds = certClinicIds(qztDoctors);
        String preDay = LocalDate.now().minusDays(1).toString();
        Example example = new Example(MedicalCommonRecord.class);
        if (Objects.isNull(doctorMap) || doctorMap.isEmpty() || CollectionUtils.isEmpty(certClinicIds)) {
            log.info("不存在认证医生:{}", doctorMap);
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
        List<OrganizationInfoDetail> infos = systemServiceFeign.findOrgInfoInIds(treatmentRecords.stream().map(TreatmentRecordExtendVO::getOrgId).collect(Collectors.toList()));
        Map<Integer, OrganizationInfoDetail> orgMap = infos.stream().collect(Collectors.toMap(t -> t.getId(), Function.identity()));
        List<QztSyncMedicalVO> commonRecords = medicalCommonRecords.stream().map(t -> {
            QztSyncMedicalVO medicalVO = new QztSyncMedicalVO();
            medicalVO.setEntid(t.getId());
            //todo 先写死
            medicalVO.setInstitutionId("00025526");
            medicalVO.setInstitutionName("古墩路口腔门诊部");
            medicalVO.setDepartmentName("口腔科");
//            medicalVO.setDoctorId();
//            medicalVO.setDoctorName(t.getDepartId());
//            medicalVO.setUid(t.getDepartId());
//            medicalVO.setName(t.getDepartId());
//            medicalVO.setGender(t.getDepartId());
//            medicalVO.setAge(t.getDepartId());
//            medicalVO.setVisitDate(t.getDepartId());
//            medicalVO.setComplain(t.getDepartId());
//            medicalVO.setDiagnosis(t.getDepartId());
//            medicalVO.setDiagnosisIcd10(t.getDepartId());
//            medicalVO.setUpdateTm(t.getDepartId());
//            medicalVO.setCreateTm(t.getDepartId());
            return medicalVO;
        }).collect(Collectors.toList());
        log.info("全诊通医生数据同步开始，同步数量：{}", commonRecords.size());
        JSONObject jsonObject = qztRestTemplateApi.postObject(String.format(qztPrefix + DOCTOR_URL, shortToken), commonRecords);
        log.info("全诊通医生数据同步完成：{}", jsonObject);
//        systemServiceFeign.syncTask(medicalCommonRecords.get(medicalCommonRecords.size() - 1).getId(), 0);
    }

    private Map<Integer, Integer> certDoctorIds(List<QztDoctor> qztDoctors) {
        return qztDoctors.stream()
                .reduce(Maps.newHashMap()
                        , (u, t) -> {
                            Set<Integer> userIds = Lists.newArrayList(Splitter.on(",").split(t.getRelateUserIds()))
                                    .stream()
                                    .map(Integer::valueOf).collect(Collectors.toSet());
                            u.putAll(userIds.stream().collect(Collectors.toMap(Function.identity(), a -> t.getId(), (o, n) -> o)));
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
                        }
                        , (u, t) -> u);
    }

}
