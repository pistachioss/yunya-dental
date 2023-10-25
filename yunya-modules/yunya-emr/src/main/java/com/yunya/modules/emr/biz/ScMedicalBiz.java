package com.yunya.modules.emr.biz;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordExtendVO;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.constant.XhqConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.service.ScRestTemplateApi;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.QztXmlToMap;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.emr.MedicalCommonRecord;
import com.yunya.models.system.Company;
import com.yunya.models.system.QztDoctor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yunya.modules.emr.enums.EmrError.*;
import static java.util.stream.Collectors.*;

/**
 * @auther: xy
 * @date: 2023/3/21
 */
@Service
@Slf4j
public class ScMedicalBiz {
    private static final Map<String, String> scMap = Maps.newHashMap();

    static {
        scMap.put("MA27YRTQ333010490D1522", "杭州艾维乾元口腔门诊部");
        scMap.put("MA2B0EH6633010219D1522", "杭州艾维医疗投资管理有限公司春江花月口腔门诊部");
        scMap.put("31127261-X33010213D1522", "杭州艾维鲲鹏路口腔门诊部");
    }

    @Resource
    private MedicalCommonRecordBiz medicalCommonRecordBiz;
    @Resource
    private ScRestTemplateApi scRestTemplateApi;
    @Value("${sc.prefix}")
    private String scPrefix;
    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private RemotePatientCentralServiceFeign patientCentralServiceFeign;
    @Resource
    private RedisUtils redisUtils;

    public void sync() {
        //查询已认证的医生
        List<QztDoctor> qztDoctors = systemServiceFeign.certDoctors();
        //过滤的医生ids
        Map<Integer, QztDoctor> doctorMap = certDoctorIds(qztDoctors);
        List<Company> companies = certClinicIds();
        List<Integer> certClinicIds =  companies.stream().map(Company::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(certClinicIds)) {
            log.info("不存在医疗机构");
            return;
        }
        String preDay = LocalDate.now().minusDays(1).toString();
        Example example = new Example(MedicalCommonRecord.class);
        example.createCriteria().andIn("status", Lists.newArrayList(0, 2))
                .andIn("majorDentistId", doctorMap.keySet())
                .andGreaterThanOrEqualTo("crtTime", preDay);
        example.orderBy("id").asc();
        List<MedicalCommonRecord> medicalCommonRecords1 = medicalCommonRecordBiz.selectByExample(example);
        if (CollectionUtils.isEmpty(medicalCommonRecords1)) {
            log.info("上城区昨天：{}，没有新增病例:{}", preDay, medicalCommonRecords1.size());
            throw ClientServiceException.wrap(SC_DATA_NULL, preDay);
        }
        Map<Integer, List<MedicalCommonRecord>> medicalMap = Maps.newHashMap();
        Map<Integer, TreatmentRecordExtendVO> treatMap = treat(medicalCommonRecords1, medicalMap);
        //过滤未认证门诊
        filterClinic(medicalCommonRecords1, treatMap, certClinicIds);
        Map<Integer, String> collect = companies.stream().collect(toMap(Company::getId, Company::getScInstitutionCode));
        for (Integer certClinicId : certClinicIds) {
            String orgName = scMap.get(collect.get(certClinicId));
            String shortToken = redisUtils.get(RedisConstants.SC_TOKEN + orgName);
            List<MedicalCommonRecord> medicalCommonRecords = medicalMap.get(certClinicId);
            if (CollectionUtils.isEmpty(medicalCommonRecords) || StringUtils.isBlank(shortToken)) {
                log.info("昨天门诊：{}：{}，没有新增病例:{}", orgName, preDay, medicalCommonRecords.size());
                continue;
            }
            int medicalSize = medicalCommonRecords.size();
            Date date = new Date();
            //调用病例记录批次任务接口
            String pcTaskId = getTaskId(date, medicalSize, XhqConstants.MEDICAL_CODE, orgName,shortToken);
            Map<Integer, PatientBaseInfoVo> patientMap = transferPatient(medicalCommonRecords);
            Map<Integer, Company> orgMap = systemServiceFeign.certCompanys().stream().collect(toMap(Company::getId, Function.identity()));
            List<List<MedicalCommonRecord>> lists = Lists.partition(medicalCommonRecords, 1000);
            for (int i = 1; i <= lists.size(); i++) {
                String dcTaskId = getDcTaskId(date, XhqConstants.MEDICAL_CODE, pcTaskId, i,shortToken);
                //businessData
                Map<String, Object> medical = medical(medicalCommonRecords, patientMap, doctorMap, orgMap, treatMap);
                String xml = QztXmlToMap.mapToXml(medical, false);
                log.info("上城区门诊：{}，病例业务数据：{}", orgName, xml);
                String businessData = QztXmlToMap.stringGZIP(xml);
                String toXml = QztXmlToMap.mapToXml(QztXmlToMap.sjXmlToMap(XhqConstants.SJ_PATH
                        , dcTaskId, XhqConstants.MEDICAL_CODE, businessData), true);
                String sj_Medical = scRestTemplateApi.postObject(scPrefix + XhqConstants.SJ_URL + "?short-access-token=" + shortToken, toXml);
                log.info("上城区门诊：{}，病例业务数据上传成功，批次：{}，任务：{}", orgName, i, dcTaskId);
            }
            syncTreat(medicalCommonRecords, patientMap, doctorMap, orgMap, treatMap, orgName, shortToken);
            syncTreatDetail(medicalCommonRecords, patientMap, doctorMap, orgMap, treatMap, orgName, shortToken);
        }
    }

    private void syncTreat(List<MedicalCommonRecord> medicalCommonRecords, Map<Integer, PatientBaseInfoVo> patientMap
            , Map<Integer, QztDoctor> doctorMap, Map<Integer, Company> orgMap, Map<Integer, TreatmentRecordExtendVO> treatMap
                ,String orgName,String token) {

        int medicalSize = medicalCommonRecords.size();
        Date date = new Date();
        //调用病例记录批次任务接口
        String pcTaskId = getTaskId(date, medicalSize, XhqConstants.TREAT_CODE, orgName, token);
        List<List<MedicalCommonRecord>> lists = Lists.partition(medicalCommonRecords, 1000);
        for (int i = 1; i <= lists.size(); i++) {
            String dcTaskId = getDcTaskId(date, XhqConstants.TREAT_CODE, pcTaskId, i, token);
            //businessData
            Map<String, Object> treat = treat(medicalCommonRecords, patientMap, doctorMap, orgMap, treatMap);
            String xml = QztXmlToMap.mapToXml(treat, false);
            log.info("上城区门诊：{}，就诊业务数据：{}", orgName,xml);
            String businessData = QztXmlToMap.stringGZIP(xml);
            String toXml = QztXmlToMap.mapToXml(QztXmlToMap.sjXmlToMap(XhqConstants.SJ_PATH
                    , dcTaskId, XhqConstants.TREAT_CODE, businessData), true);
            String sj_Medical = scRestTemplateApi.postObject(scPrefix + XhqConstants.SJ_URL + "?short-access-token=" + token, toXml);
            log.info("上城区门诊：{}，就诊业务数据上传成功，批次：{}，任务：{}", orgName,i, dcTaskId);
        }
    }

//    public static void main(String[] args) {
//        QztXmlToMap.stringGZIP(xml)
//    }

    private void syncTreatDetail(List<MedicalCommonRecord> medicalCommonRecords, Map<Integer, PatientBaseInfoVo> patientMap
            , Map<Integer, QztDoctor> doctorMap, Map<Integer, Company> orgMap, Map<Integer, TreatmentRecordExtendVO> treatMap
            ,String orgName,String token) {

        int medicalSize = medicalCommonRecords.size();
        Date date = new Date();
        //调用病例记录批次任务接口
        String pcTaskId = getTaskId(date, medicalSize, XhqConstants.TREAT_DETAIL_CODE,orgName, token);
        List<List<MedicalCommonRecord>> lists = Lists.partition(medicalCommonRecords, 1000);
        for (int i = 1; i <= lists.size(); i++) {
            String dcTaskId = getDcTaskId(date, XhqConstants.TREAT_DETAIL_CODE, pcTaskId, i, token);
            //businessData
            Map<String, Object> treat = treatDetail(medicalCommonRecords, patientMap, doctorMap, orgMap, treatMap);
            String xml = QztXmlToMap.mapToXml(treat, false);
            log.info("上城区门诊：{}，就诊详情业务数据：{}", orgName,xml);
            String businessData = QztXmlToMap.stringGZIP(xml);
            String toXml = QztXmlToMap.mapToXml(QztXmlToMap.sjXmlToMap(XhqConstants.SJ_PATH
                    , dcTaskId, XhqConstants.TREAT_DETAIL_CODE, businessData), true);
            String sj_treatDetail = scRestTemplateApi.postObject(scPrefix + XhqConstants.SJ_URL + "?short-access-token=" + token, toXml);
            log.info("上城区门诊：{}，就诊详情业务数据上传成功，批次：{}，任务：{}", orgName, i, dcTaskId);
        }
    }

    private String getTaskId(Date date, int size, String code,String orgName,String token) {
        //调用病例记录批次任务接口
        String pc_Task = scRestTemplateApi.postObject(scPrefix + XhqConstants.PC_URL + "?short-access-token=" + token
                , QztXmlToMap.mapToXml(QztXmlToMap.pcXmlToMap(XhqConstants.PC_PATH
                        , date, size, code), true));
        String pcTaskId = QztXmlToMap.getNode(pc_Task, "taskid").getTextContent();
        if (StringUtils.isBlank(pcTaskId)) {
            throw ClientServiceException.wrap(SC_PC_ERROR, orgName);
        }
        return pcTaskId;
    }

    private String getDcTaskId(Date date, String code, String pcTaskId, int i,String token) {
        String dc_Task = scRestTemplateApi.postObject(scPrefix + XhqConstants.DC_URL + "?short-access-token=" + token
                , QztXmlToMap.mapToXml(QztXmlToMap.dcXmlToMap(XhqConstants.DC_PATH
                        , date, code, pcTaskId, i), true));
        String dcTaskId = QztXmlToMap.getNode(dc_Task, "taskid").getTextContent();
        if (StringUtils.isBlank(dcTaskId)) {
            throw ClientServiceException.wrap(SC_DC_ERROR);
        }
        return dcTaskId;
    }

    private List<Company> certClinicIds() {
        List<Company> companies = systemServiceFeign.scCompanys();
        if (CollectionUtils.isEmpty(companies)) {
            return Lists.newArrayList();
        }
        return companies;
//        return qztDoctors.stream()
//                .reduce(Lists.newArrayList()
//                        , (u, t) -> {
//                            u.addAll(Lists.newArrayList(Splitter.on(",").split(t.getPracticeClinic())).stream().map(Integer::valueOf).collect(Collectors.toSet()));
//                            return u;
//                        }, (u, t) -> u);
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

    private Map<Integer, PatientBaseInfoVo> transferPatient(List<MedicalCommonRecord> medicalCommonRecords) {
        List<PatientBaseInfoVo> patients = patientCentralServiceFeign.findPatientInfoByIds(medicalCommonRecords.stream()
                .map(MedicalCommonRecord::getPatientId).collect(Collectors.toList()));
        return patients.stream().collect(toMap(PatientBaseInfoVo::getId, Function.identity()));
    }

//    private Map<Integer, SysUserInfoDetail> transferUser() {
//        SysUserEmployeeModel model = new SysUserEmployeeModel();
//        model.setWhetherPage(false);
//        List<SysUserInfoDetail> userList = systemServiceFeign.findSysUserEmployeeInfoList(model);
//        return userList.stream().collect(toMap(SysUserInfoDetail::getUserId, Function.identity()));
//    }

    private Map<Integer, TreatmentRecordExtendVO> treat(List<MedicalCommonRecord> medicalCommonRecords
            ,Map<Integer, List<MedicalCommonRecord>> medicalMap) {
        Set<Integer> treatIds = medicalCommonRecords.stream().map(MedicalCommonRecord::getTreatmentId).collect(Collectors.toSet());
        List<TreatmentRecordExtendVO> treatmentRecords = treatmentServiceFeign.findTreatmentRecordByIds(treatIds);
        Map<Integer, TreatmentRecordExtendVO> treatMap = treatmentRecords.stream()
                .collect(toMap(TreatmentRecordExtendVO::getId, Function.identity(), (o, v) -> o));
        Map<Integer, Integer> treatOrgMap = treatmentRecords.stream()
                .collect(toMap(TreatmentRecordExtendVO::getId, TreatmentRecordExtendVO::getOrgId, (o, v) -> o));
        Map<Integer, List<MedicalCommonRecord>> collect = medicalCommonRecords.stream().filter(t -> treatOrgMap.containsKey(t.getTreatmentId()))
                .collect(groupingBy(t -> treatOrgMap.get(t.getTreatmentId()), toList()));
        medicalMap.putAll(collect);
        return treatMap;
    }


    private Map<String, Object> medical(List<MedicalCommonRecord> records, Map<Integer, PatientBaseInfoVo> patientMap
            , Map<Integer, QztDoctor> doctorMap, Map<Integer, Company> orgMap, Map<Integer, TreatmentRecordExtendVO> treatMap) {
        Map<String, Object> dmp = new HashMap<>();
        Map<String, Object> datasetss = new HashMap<>();
        Map<String, Object> datasets = new HashMap<>();
        List<Map<String, Object>> list = Lists.newArrayListWithCapacity(records.size());
        dmp.put("dmp", datasetss);
        datasetss.put("datasets", datasets);
        datasets.put("setcode", XhqConstants.MEDICAL_CODE);
        for (MedicalCommonRecord record : records) {
            PatientBaseInfoVo patient = patientMap.get(record.getPatientId());
            QztDoctor user = doctorMap.getOrDefault(record.getMajorDentistId(), new QztDoctor());
            TreatmentRecordExtendVO treat = treatMap.getOrDefault(record.getTreatmentId(), new TreatmentRecordExtendVO());
            Company org = orgMap.getOrDefault(treat.getOrgId(), new Company());
            Map<String, String> sex = getSex(patient.getGender());
            Map<String, Object> setdetailss = new HashMap<>();
            Map<String, Object> setdetails = new HashMap<>();
            setdetailss.put("setdetails", setdetails);

            setdetails.put("BUSINESS_ID", "");
            setdetails.put("ORGANIZATION_CODE", "");
            setdetails.put("UPDATE_DATE", "");
            setdetails.put("DATAGENERATE_DATE", "");
            setdetails.put("ORGANIZATION_NAME", "");
            setdetails.put("BASIC_ACTIVE_ID", "");
            setdetails.put("RECORD_IDEN", "");
            setdetails.put("DOMAIN_CODE", "");
            setdetails.put("SERIALNUM_ID", "");
            setdetails.put("ARCHIVE_DATE", "");
            setdetails.put("CREATE_DATE", "");
            setdetails.put("BATCH_NUM", "");
            setdetails.put("LOCAL_ID", "");
            setdetails.put("TASK_ID", "");
            setdetails.put("WS02_01_026_01", "");
            setdetails.put("WS02_01_032_02", "");
            setdetails.put("WS99_99_026_01", "");
            setdetails.put("WS99_99_026_02", "");
            setdetails.put("WS99_99_903_40", "");
            setdetails.put("WS99_99_902_07", "");
            setdetails.put("CT99_99_902_07", "");
            setdetails.put("WS02_01_005_01_01", "");
            setdetails.put("WS04_10_242_01", "");
            setdetails.put("WS04_10_167_01", "");
            setdetails.put("WS04_10_188_01", "");
            setdetails.put("WS99_99_020_14", "");
            setdetails.put("WS02_01_015_01", "");
            setdetails.put("CT02_01_015_01", "");
            setdetails.put("WS02_01_025_01", "");
            setdetails.put("CT02_01_025_01", "");
            setdetails.put("WS02_01_018_01", "");
            setdetails.put("CT02_01_018_01", "");
            setdetails.put("WS02_01_052_01", "");
            setdetails.put("WS99_99_902_38", "");
            setdetails.put("CT02_01_052_01", "");
            setdetails.put("CT99_99_902_38", "");
            setdetails.put("WS08_10_007_01", "");
            setdetails.put("WS99_99_902_39", "");
            setdetails.put("WS02_01_900_01", "");
            setdetails.put("CT02_01_900_01", "");
            setdetails.put("CT99_99_902_39", "");
            setdetails.put("WS02_01_901_11", "");
            setdetails.put("WS02_01_011_02", "");
            setdetails.put("WS99_99_902_41", "");
            setdetails.put("CT99_99_902_41", "");
            setdetails.put("CT02_01_011_02", "");
            setdetails.put("WS02_01_010_02", "");
            setdetails.put("WS04_01_120_01", "");
            setdetails.put("WS09_00_056_01", "");
            setdetails.put("WS04_01_116_01", "");
            setdetails.put("WS99_99_902_42", "");
            setdetails.put("CT99_99_902_42", "");
            setdetails.put("CT04_01_116_01", "");
            setdetails.put("WS04_01_115_01", "");
            setdetails.put("WS04_01_018_01", "");
            setdetails.put("WS05_10_033_01", "");
            setdetails.put("WS05_10_014_01", "");
            setdetails.put("WS02_10_023_01", "");
            setdetails.put("WS02_10_022_02", "");
            setdetails.put("WS02_01_039_013", "");
            setdetails.put("WS02_10_024_05", "");
            setdetails.put("WS99_99_902_45", "");
            setdetails.put("CT99_99_902_45", "");
            setdetails.put("CT02_10_024_05", "");
            setdetails.put("WS06_00_936_01", "");
            setdetails.put("WS02_10_028_04", "");
            setdetails.put("WS05_10_130_11", "");
            setdetails.put("WS99_99_902_44", "");
            setdetails.put("CT05_10_130_11", "");
            setdetails.put("CT99_99_902_44", "");
            setdetails.put("WS06_00_300_01", "");
            setdetails.put("WS99_99_902_43", "");
            setdetails.put("CT06_00_300_01", "");
            setdetails.put("CT99_99_902_43", "");
            setdetails.put("WS99_99_902_09", "");
            setdetails.put("CT99_99_902_09", "");
            setdetails.put("WS05_10_130_03", "");
            setdetails.put("WS99_99_902_88", "");
            setdetails.put("CT05_10_130_03", "");
            setdetails.put("CT99_99_902_88", "");
            setdetails.put("WS05_10_130_04", "");
            setdetails.put("WS99_99_902_89", "");
            setdetails.put("CT05_10_130_04", "");
            setdetails.put("CT99_99_902_89", "");
            setdetails.put("WS05_10_132_01", "");
            setdetails.put("WS04_30_010_01", "");
            setdetails.put("WS06_00_087_01", null);
            setdetails.put("WS06_00_174_01", null);
            setdetails.put("WS08_10_052_50", null);
            setdetails.put("CT08_10_052_50", null);
            setdetails.put("CT08_10_025_09", null);
            setdetails.put("WS08_10_025_09", null);
            setdetails.put("WS99_99_902_11", null);
            setdetails.put("CT99_99_902_11", null);
            setdetails.put("WS08_10_052_07", null);
            setdetails.put("CT08_10_052_07", null);
            setdetails.put("CT08_10_025_08", null);
            setdetails.put("WS08_10_025_08", null);
            setdetails.put("WS99_99_902_12", null);
            setdetails.put("CT99_99_902_12", null);
            setdetails.put("WS06_00_175_01", null);
            setdetails.put("WS06_00_176_01", null);
            setdetails.put("WS05_10_117_03", null);
            setdetails.put("WS06_00_177_01", null);
            setdetails.put("WS06_00_066_01", null);
            setdetails.put("WS01_00_008_03", null);
            setdetails.put("WS04_10_174_01", null);
            setdetails.put("WS04_10_176_01", null);
            setdetails.put("WS99_99_010_48", null);
            setdetails.put("WS99_99_034_259", null);
            setdetails.put("WJ01_01_001_011", null);
            setdetails.put("WJ01_01_001_012", null);

            setdetails.put("WS02_01_039_001", patient.getName());
            setdetails.put("WS02_01_040_01", sex.keySet().toArray()[0]);
            setdetails.put("CT02_01_040_01", sex.values().toArray()[0]);
            setdetails.put("WS02_01_031_01", "01");
            setdetails.put("CT02_01_031_01", "居民身份证");
            setdetails.put("WS02_01_030_01", "342626198208160971");
            setdetails.put("WS01_00_001_02", patient.getMedicalNumber());
            setdetails.put("WS01_00_010_01", patient.getId());
            setdetails.put("WS09_00_904_01", "1");
            setdetails.put("WS06_00_196_01", record.getType());
            setdetails.put("WS06_00_062_01", DateUtil.format(record.getCrtTime(), "yyyyMMddHHmmss"));
            setdetails.put("WS04_01_119_01", "<![CDATA[" + record.getChiefComplaint() + "]]>");
            setdetails.put("WS02_10_071_01", "<![CDATA[" + record.getPresentIllness() + "]]>");
            setdetails.put("WS02_10_099_01", "<![CDATA[" + record.getPastHistory() + "]]>");
            setdetails.put("WS04_10_258_01", "<![CDATA[" + record.getExamination() + "]]>");
            setdetails.put("WS06_00_159_02", "<![CDATA[" + record.getTreatment() + "]]>");
            setdetails.put("WS06_00_179_01", "<![CDATA[" + record.getPlan() + "]]>");
            setdetails.put("WS05_01_024_16", "K12.106");
            setdetails.put("WS99_99_902_87", "K12.106");
            setdetails.put("CT05_01_024_16", "K12.106");
            setdetails.put("CT99_99_902_87", "K12.106");
            setdetails.put("WS06_00_185_01", "xx");
            setdetails.put("WS04_30_030_02", "<![CDATA[" + record.getPlan() + "]]>");
            setdetails.put("WS02_01_910_01", record.getMajorDentistId());
            setdetails.put("WS02_01_039_054", Objects.isNull(user.getDoctorName()) ? "佚名" : user.getDoctorName());
            setdetails.put("WS02_01_910_02", Objects.isNull(user.getIdCard()) ? "342626198208160971" : user.getIdCard());
            setdetails.put("WS02_01_925_17", record.getMajorDentistId());
            setdetails.put("WS02_01_039_031", Objects.isNull(user.getDoctorName()) ? "佚名" : user.getDoctorName());
            setdetails.put("WS02_01_910_03", Objects.isNull(user.getIdCard()) ? "342626198208160971" : user.getIdCard());
            setdetails.put("WS08_10_025_01", XhqConstants.KE_SHI_CODE);
            setdetails.put("CT08_10_025_01", "口腔科");
            setdetails.put("WS99_99_902_08", "口腔科");
            setdetails.put("CT99_99_902_08", "口腔科");
            setdetails.put("CT08_01_025_28", "口腔科");
            setdetails.put("WS08_01_025_28", XhqConstants.KE_SHI_CODE_1);
            setdetails.put("WS99_99_902_680", org.getCreditCode());
            setdetails.put("WS08_10_052_01", org.getScInstitutionCode());
            setdetails.put("CT08_10_052_01", scMap.get(org.getScInstitutionCode()));
            setdetails.put("WS08_10_903_01", "0");
            setdetails.put("WS08_10_903_02", org.getName());
            setdetails.put("WS99_99_902_622", "9");
            setdetails.put("CT99_99_902_622", "未知");
            setdetails.put("WJ01_01_001_001", record.getId());
            setdetails.put("WJ01_01_001_002", XhqConstants.DEPART_ID);
            setdetails.put("WJ01_01_001_003", "口腔科");
            setdetails.put("WJ01_01_001_004", user.getQualification());
            setdetails.put("WJ01_01_001_005", user.getPracticeClinic());
            setdetails.put("WJ01_01_001_006", "04");
            setdetails.put("WJ01_01_001_007", "007");
            setdetails.put("WJ01_01_001_008", "0");
            setdetails.put("WJ01_01_001_009", "0");
            setdetails.put("WJ01_01_001_010", record.getMajorDentistId());
            list.add(setdetailss);
        }
        datasets.put("setdetails", list);
        return dmp;
    }

    private Map<String, Object> treat(List<MedicalCommonRecord> records, Map<Integer, PatientBaseInfoVo> patientMap
            , Map<Integer, QztDoctor> doctorMap, Map<Integer, Company> orgMap, Map<Integer, TreatmentRecordExtendVO> treatMap) {
        Map<String, Object> dmp = new HashMap<>();
        Map<String, Object> datasetss = new HashMap<>();
        Map<String, Object> datasets = new HashMap<>();
        List<Map<String, Object>> list = Lists.newArrayListWithCapacity(records.size());
        dmp.put("dmp", datasetss);
        datasetss.put("datasets", datasets);
        datasets.put("setcode", XhqConstants.TREAT_CODE);
        for (MedicalCommonRecord record : records) {
            PatientBaseInfoVo patient = patientMap.get(record.getPatientId());
            QztDoctor user = doctorMap.getOrDefault(record.getMajorDentistId(), new QztDoctor());
            TreatmentRecordExtendVO treat = treatMap.getOrDefault(record.getTreatmentId(), new TreatmentRecordExtendVO());
            Company org = orgMap.getOrDefault(treat.getOrgId(), new Company());
            Map<String, String> sex = getSex(patient.getGender());
            Map<String, Object> setdetailss = new HashMap<>();
            Map<String, Object> setdetails = new HashMap<>();
            setdetailss.put("setdetails", setdetails);

            setdetails.put("BUSINESS_ID", "");
            setdetails.put("ORGANIZATION_CODE", "");
            setdetails.put("UPDATE_DATE", "");
            setdetails.put("DATAGENERATE_DATE", "");
            setdetails.put("ORGANIZATION_NAME", "");
            setdetails.put("BASIC_ACTIVE_ID", "");
            setdetails.put("RECORD_IDEN", "");
            setdetails.put("DOMAIN_CODE", "");
            setdetails.put("SERIALNUM_ID", "");
            setdetails.put("ARCHIVE_DATE", "");
            setdetails.put("CREATE_DATE", "");
            setdetails.put("BATCH_NUM", "");
            setdetails.put("LOCAL_ID", "");
            setdetails.put("TASK_ID", "");

            setdetails.put("WS02_01_026_01", "");
            setdetails.put("WS02_01_032_02", "");
            setdetails.put("WS02_01_005_01_01", "");
            setdetails.put("WS99_99_903_36", "");
            setdetails.put("WS99_99_999_09", "");
            setdetails.put("WS02_01_018_01", "");
            setdetails.put("CT02_01_018_01", "");
            setdetails.put("WS04_10_188_01", "");
            setdetails.put("WS05_10_130_01", "");
            setdetails.put("WS99_99_902_80", "");
            setdetails.put("CT05_10_130_01", "");
            setdetails.put("CT99_99_902_80", "");
            setdetails.put("WS99_99_902_81", "");
            setdetails.put("WS05_10_130_02", "");
            setdetails.put("CT99_99_902_81", "");
            setdetails.put("CT05_10_130_02", "");
            setdetails.put("WS06_00_300_01", "");
            setdetails.put("WS99_99_902_43", "");
            setdetails.put("CT99_99_902_43", "");
            setdetails.put("CT06_00_300_01", "");
            setdetails.put("WS05_10_130_11", "");
            setdetails.put("WS99_99_902_44", "");
            setdetails.put("CT99_99_902_44", "");
            setdetails.put("CT05_10_130_11", "");
            setdetails.put("WS01_00_918_01", "");
            setdetails.put("WS02_01_039_113", "");
            setdetails.put("WS06_00_933_01", "");
            setdetails.put("WS99_99_241_04", "");
            setdetails.put("WS06_00_179_01", "");
            setdetails.put("WS02_01_039_088", "");
            setdetails.put("WS02_01_030_06", "");
            setdetails.put("WS99_99_241_25", "");
            setdetails.put("WS05_01_024_41", "");
            setdetails.put("CT05_01_024_41", "");
            setdetails.put("WS06_00_164_01", "");
            setdetails.put("WS99_99_902_70", "");
            setdetails.put("CT06_00_164_01", "");
            setdetails.put("CT99_99_902_70", "");
            setdetails.put("WS99_99_020_01", "");
            setdetails.put("WS08_50_047_01", "");
            setdetails.put("WS06_00_136_03", "");
            setdetails.put("WS08_50_050_01", "");
            setdetails.put("WS08_50_049_01", "");
            setdetails.put("WS02_01_039_022", "");
            setdetails.put("WS02_01_039_023", "");
            setdetails.put("WS02_01_039_025", "");
            setdetails.put("WS99_99_999_01", "");
            setdetails.put("WS99_99_999_02", "");
            setdetails.put("WS99_99_999_03", "");
            setdetails.put("WS99_99_999_04", "");
            setdetails.put("WS99_99_999_05", "");
            setdetails.put("WS99_99_010_48", "");

            setdetails.put("WS02_01_039_001", patient.getName());
            setdetails.put("WS02_01_040_01", sex.keySet().toArray()[0]);
            setdetails.put("CT02_01_040_01", sex.values().toArray()[0]);
            setdetails.put("WS99_99_903_40", patient.getMedicalNumber());
            setdetails.put("WS99_99_902_07", "xx");
            setdetails.put("CT99_99_902_07", "01");
            setdetails.put("WS02_01_031_01", "01");
            setdetails.put("CT02_01_031_01", "居民身份证");
            setdetails.put("WS02_01_906_01", "330106201705020046");
            String regex = "1[3456789]\\d{9}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(patient.getMobile());
            setdetails.put("WS02_01_010_26", matcher.matches() ? patient.getMobile() : "15876985563");
            setdetails.put("WS01_00_001_02", patient.getMedicalNumber());
            setdetails.put("WS01_00_010_01", treat.getId());
            setdetails.put("WS01_00_020_01", treat.getId());
            setdetails.put("WS99_99_020_265", "3");
            setdetails.put("WS99_99_999_08", "0");
            setdetails.put("WS06_00_901_01", "0");
            setdetails.put("WS01_00_919_01", "1");
            setdetails.put("WS05_01_024_01", "K12.106");
            setdetails.put("CT05_01_024_01", "K12.106");
            setdetails.put("WS99_99_902_09", "K12.106");
            setdetails.put("CT99_99_902_09", "K12.106");
            setdetails.put("WS08_50_032_02", "1");
            setdetails.put("WS99_99_902_82", "1");
            setdetails.put("CT08_50_032_02", "普通处方");
            setdetails.put("CT99_99_902_82", "普通处方");
            setdetails.put("WS08_50_033_01", DateUtil.format(treat.getCrtTime(), "yyyyMMddHHmmss"));
            setdetails.put("WS02_01_039_021", user.getDoctorName());
            setdetails.put("WS99_99_909_24", record.getMajorDentistId());
            setdetails.put("WS99_99_999_13", Objects.isNull(user.getIdCard()) ? "342626198208160971" : user.getIdCard());
            setdetails.put("WS08_10_025_04", XhqConstants.KE_SHI_CODE);
            setdetails.put("WS99_99_902_83", "院内处方开立科室代码");
            setdetails.put("CT08_10_025_04", "口腔科");
            setdetails.put("CT99_99_902_83", "口腔科");
            setdetails.put("WS07_00_908_01", "60.0000");
            setdetails.put("WS06_00_294_01", "1");
            setdetails.put("WS05_01_024_16", "K12.106");
            setdetails.put("CT05_01_024_16", "K12.106");
            setdetails.put("WS02_01_039_024", user.getDoctorName());
            setdetails.put("WS06_00_940_13", DateUtil.format(treat.getCrtTime(), "yyyyMMddHHmmss"));
            setdetails.put("WS08_10_900_01", "001");
            setdetails.put("WS08_10_900_02", "口腔科");
            setdetails.put("WS08_50_936_01", "00");
            setdetails.put("CT08_50_936_01", "正常");
            setdetails.put("WS99_99_902_623", "2");
            setdetails.put("CT99_99_902_623", "成药");
            setdetails.put("WS99_99_902_08", "A12");
            setdetails.put("CT99_99_902_08", "口腔科");
            setdetails.put("CT08_01_025_28", "口腔科");
            setdetails.put("WS08_01_025_28", XhqConstants.KE_SHI_CODE_1);
            setdetails.put("WS99_99_902_680", org.getCreditCode());
            setdetails.put("WS08_10_052_01", org.getScInstitutionCode());
            setdetails.put("CT08_10_052_01", scMap.get(org.getScInstitutionCode()));
            setdetails.put("WS08_10_903_01", "001");
            setdetails.put("WS08_10_903_02", scMap.get(org.getScInstitutionCode()));
            setdetails.put("WS09_00_916_01", DateUtil.format(treat.getCrtTime(), "yyyyMMddHHmmss"));
            setdetails.put("WJ01_02_001_001", treat.getId());
            setdetails.put("WJ01_02_001_002", record.getId());
            setdetails.put("WJ01_02_001_003", patient.getId());
            setdetails.put("WJ01_02_001_004", 1);
            list.add(setdetailss);
            datasets.put("setdetails", list);
        }
        return dmp;
    }

    private Map<String, Object> treatDetail(List<MedicalCommonRecord> records, Map<Integer, PatientBaseInfoVo> patientMap
            , Map<Integer, QztDoctor> doctorMap, Map<Integer, Company> orgMap, Map<Integer, TreatmentRecordExtendVO> treatMap) {
        Map<String, Object> dmp = new HashMap<>();
        Map<String, Object> datasetss = new HashMap<>();
        Map<String, Object> datasets = new HashMap<>();
        List<Map<String, Object>> list = Lists.newArrayListWithCapacity(records.size());
        dmp.put("dmp", datasetss);
        datasetss.put("datasets", datasets);
        datasets.put("setcode", XhqConstants.TREAT_DETAIL_CODE);
        for (MedicalCommonRecord record : records) {
            PatientBaseInfoVo patient = patientMap.get(record.getPatientId());
            TreatmentRecordExtendVO treat = treatMap.getOrDefault(record.getTreatmentId(), new TreatmentRecordExtendVO());
            Company org = orgMap.getOrDefault(treat.getOrgId(), new Company());
            Map<String, Object> setdetailss = new HashMap<>();
            Map<String, Object> setdetails = new HashMap<>();
            setdetailss.put("setdetails", setdetails);
            setdetails.put("BUSINESS_ID", "");
            setdetails.put("ORGANIZATION_CODE", "");
            setdetails.put("UPDATE_DATE", "");
            setdetails.put("DATAGENERATE_DATE", "");
            setdetails.put("ORGANIZATION_NAME", "");
            setdetails.put("BASIC_ACTIVE_ID", "");
            setdetails.put("RECORD_IDEN", "");
            setdetails.put("DOMAIN_CODE", "");
            setdetails.put("SERIALNUM_ID", "");
            setdetails.put("ARCHIVE_DATE", "");
            setdetails.put("CREATE_DATE", "");
            setdetails.put("BATCH_NUM", "");
            setdetails.put("LOCAL_ID", "");
            setdetails.put("TASK_ID", "");

            setdetails.put("WS02_01_039_001", "");
            setdetails.put("WS02_01_040_01", "");
            setdetails.put("CT02_01_040_01", "");
            setdetails.put("WS02_01_026_01", "");
            setdetails.put("WS02_01_032_02", "");
            setdetails.put("WS99_99_903_40", "");
            setdetails.put("WS99_99_902_07", "");
            setdetails.put("CT99_99_902_07", "");
            setdetails.put("WS02_01_005_01_01", "");
            setdetails.put("WS02_01_031_01", "");
            setdetails.put("CT02_01_031_01", "");
            setdetails.put("WS99_99_903_36", "");
            setdetails.put("WS01_00_907_01", "");
            setdetails.put("WS99_99_900_26", "");
            setdetails.put("WS99_99_902_129", "");
            setdetails.put("CT99_99_902_129", "");
            setdetails.put("WS08_50_025_01", "");
            setdetails.put("WS99_99_902_71", "");
            setdetails.put("CT08_50_025_01", "");
            setdetails.put("CT99_99_902_71", "");
            setdetails.put("WS08_50_926_03", "");
            setdetails.put("WS99_99_020_02", "");
            setdetails.put("WS99_99_020_269", "");
            setdetails.put("WS08_50_022_100", "");
            setdetails.put("WS06_00_147_01", "");
            setdetails.put("WS06_00_135_01", "");
            setdetails.put("WS99_99_242_05", "");
            setdetails.put("CT99_99_242_05", "");
            setdetails.put("WS99_99_241_01", "");
            setdetails.put("WS04_01_912_74", null);
            setdetails.put("WS07_00_904_01", null);
            setdetails.put("WS07_00_908_01", null);
            setdetails.put("WS08_50_900_01", null);
            setdetails.put("WS99_99_010_02", null);
            setdetails.put("WS99_99_925_05", null);
            setdetails.put("WS99_99_013_05", null);
            setdetails.put("WS99_99_903_35", null);
            setdetails.put("WS99_99_010_04", null);
            setdetails.put("WS99_99_034_02", null);
            setdetails.put("WS99_99_925_04", null);
            setdetails.put("WS99_99_925_03", null);
            setdetails.put("WS99_99_925_02", null);
            setdetails.put("WS99_99_925_01", "01");
            setdetails.put("WS02_01_912_01", null);
            setdetails.put("WS04_30_909_01", null);
            setdetails.put("WS06_00_903_01", null);
            setdetails.put("WS06_00_148_01", null);
            setdetails.put("WS99_99_999_01", null);
            setdetails.put("WS99_99_999_02", null);
            setdetails.put("WS99_99_999_03", null);
            setdetails.put("WS99_99_999_04", null);
            setdetails.put("WS99_99_999_05", null);
            setdetails.put("WS99_99_010_48", null);
            setdetails.put("WJ01_02_001_010", null);
            setdetails.put("WJ01_02_001_011", null);
            setdetails.put("WJ01_02_001_012", null);
            setdetails.put("WJ01_02_001_013", null);
            setdetails.put("WJ01_02_001_014", null);

            setdetails.put("WS02_01_906_01", "342626198208160971");
            setdetails.put("WS01_00_001_02", patient.getMedicalNumber());
            setdetails.put("WS01_00_010_01", treat.getId());
            setdetails.put("WS01_00_020_01", treat.getId());
            setdetails.put("WS99_99_903_08", treat.getId());
            setdetails.put("WS99_99_903_120", "001");
            setdetails.put("WS09_00_064_01", "初诊检查");
            setdetails.put("WS99_99_020_266", "3");
            setdetails.put("WS99_99_999_14", "999");
            setdetails.put("WS08_50_056_01", "002");
            setdetails.put("WS06_00_900_01", "1");
            setdetails.put("WS01_00_950_05", "063");
            setdetails.put("WS08_50_022_01", "063");
            setdetails.put("CT08_50_022_01", "超声波洁牙");
            setdetails.put("WS99_99_902_79", "063");
            setdetails.put("CT99_99_902_79", "超声波洁牙");
            setdetails.put("WS08_50_902_02", "超声波洁牙");
            setdetails.put("WS99_99_242_03", "1");
            setdetails.put("WS99_99_242_02", "次");
            setdetails.put("WS99_99_020_03", 1);
            setdetails.put("WS99_99_020_268", 9);
            setdetails.put("WS99_99_020_04", 1);
            setdetails.put("WS99_99_999_110", 0);
            setdetails.put("WS99_99_020_270", 9);
            setdetails.put("CT99_99_020_270", "其他");
            setdetails.put("WS99_99_020_267", "1");
            setdetails.put("WS99_99_020_11", "1");
            setdetails.put("WS01_00_919_01", "1");
            setdetails.put("WS08_50_902_01", "艾维口腔");
            setdetails.put("WS08_10_042_03", "杭州");
            setdetails.put("WS08_50_043_01", "0.25g");
            setdetails.put("WS08_50_011_01", "00");
            setdetails.put("WS99_99_902_73", "001");
            setdetails.put("CT08_50_011_01", "原料");
            setdetails.put("CT99_99_902_73", "护齿");
            setdetails.put("WS06_00_133_01", "qd");
            setdetails.put("WS99_99_902_77", "qd");
            setdetails.put("CT06_00_133_01", "每天 1 次");
            setdetails.put("CT99_99_902_77", "每天 1 次");
            setdetails.put("WS99_99_902_74", "xx");
            setdetails.put("WS06_00_134_01", "1");
            setdetails.put("CT99_99_902_74", "洁牙");
            setdetails.put("CT06_00_134_01", "口服");
            setdetails.put("WS08_50_023_01", 1);
            setdetails.put("WS99_99_902_624", "01");
            setdetails.put("CT99_99_902_624", "瓶");
            setdetails.put("WS99_99_242_04", "瓶");
            setdetails.put("WS08_50_024_01", "瓶");
            setdetails.put("WS99_99_925_06", "01");
            setdetails.put("CT99_99_925_06", "瓶");
            setdetails.put("WS99_99_241_08", 1);
            setdetails.put("CT99_99_925_01", "瓶");
            setdetails.put("WS04_50_900_02", "0");
            setdetails.put("WS08_50_033_01", DateUtil.format(treat.getCrtTime(), "yyyyMMddHHmmss"));
            setdetails.put("CT99_99_902_08", "A12");
            setdetails.put("WS99_99_902_08", "口腔科");
            setdetails.put("CT08_01_025_28", "12.08");
            setdetails.put("WS08_01_025_28", "口腔种植专业");
            setdetails.put("WS99_99_902_680", org.getCreditCode());
            setdetails.put("WS08_10_052_01", org.getScInstitutionCode());
            setdetails.put("CT08_10_052_01", scMap.get(org.getScInstitutionCode()));
            setdetails.put("WS08_10_903_01", org.getId());
            setdetails.put("WS08_10_903_02", org.getName());
            setdetails.put("WS06_00_901_01", "0");
            setdetails.put("WS09_00_916_01", DateUtil.format(treat.getCrtTime(), "yyyyMMddHHmmss"));
            setdetails.put("WJ01_02_001_001", treat.getId());
//            setdetails.put("WJ01_02_001_002", "001");
//            setdetails.put("WJ01_02_001_003", "001");
            setdetails.put("WJ01_02_001_004", 1);
//            setdetails.put("WJ01_02_001_005", 1);
//            setdetails.put("WJ01_02_001_006", 9);
            setdetails.put("WJ01_02_001_007", 1);
            setdetails.put("WJ01_02_001_008", 1);
//            setdetails.put("WJ01_02_001_009", 3);
            list.add(setdetailss);
            datasets.put("setdetails", list);
        }
        return dmp;
    }

    private Map<String, String> getSex(Byte sex) {
        Map<String, String> sexMap = Maps.newHashMap();
        //性别 0-男；1-女；2-未知
        if (Objects.equals((byte)1, sex)) {
            sexMap.put("2", "女性");
        } else if (Objects.equals((byte)0, sex)) {
            sexMap.put("1", "男性");
        } else {
            sexMap.put("0", "未知的性别");
        }
        return sexMap;
    }


}
