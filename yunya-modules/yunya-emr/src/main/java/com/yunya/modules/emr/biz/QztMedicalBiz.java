package com.yunya.modules.emr.biz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yunya.feign.emr.domain.vo.QztSyncMedicalVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.service.QztRestTemplateApi;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.system.QztSyncDoctor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
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
        QztSyncDoctor preTask = systemServiceFeign.getPreTask(1);
        Example example = new Example(MedicalCommonRecord.class);
        Example.Criteria criteria = example.createCriteria().andIn("status", Lists.newArrayList(0, 2));
        if (Objects.nonNull(preTask)) {
            criteria.andGreaterThan("id", preTask.getLastId());
        }
        example.orderBy("id").asc();
        List<MedicalCommonRecord> medicalCommonRecords = medicalCommonRecordBiz.selectByExample(example);
        String shortToken = redisUtils.get(RedisConstants.QZT_TOKEN);
        if (CollectionUtils.isEmpty(medicalCommonRecords) && StringUtils.isBlank(shortToken)) {
            return;
        }
        Set<Integer> treatIds = medicalCommonRecords.stream().map(MedicalCommonRecord::getTreatmentId).collect(Collectors.toSet());
        List<TreatmentRecordExtendVO> treatmentRecords = treatmentServiceFeign.findTreatmentRecordByIds(treatIds);
        List<OrganizationInfoDetail> infos = systemServiceFeign.findOrgInfoInIds(treatmentRecords.stream().map(TreatmentRecordExtendVO::getOrgId).collect(Collectors.toList()));
        Map<Integer, OrganizationInfoDetail> orgMap = infos.stream().collect(Collectors.toMap(t -> t.getId(), Function.identity()));
        List<QztSyncMedicalVO> commonRecords = medicalCommonRecords.stream().map(t -> {
            QztSyncMedicalVO medicalVO = new QztSyncMedicalVO();
            medicalVO.setEntid(t.getId());
//            medicalVO.setInstitutionId(t.getDepartId());
//            medicalVO.setInstitutionName(t.getDepartId());
//            medicalVO.setDepartmentName(t.getDepartName());
//            medicalVO.setDoctorId(t.getDepartId());
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
        systemServiceFeign.syncTask(medicalCommonRecords.get(medicalCommonRecords.size() - 1).getId(), 0);
    }

}
