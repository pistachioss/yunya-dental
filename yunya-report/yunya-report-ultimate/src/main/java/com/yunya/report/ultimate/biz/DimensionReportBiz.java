package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.query.base.DoubleDateRangeQueryForm;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.DENTIST_GROUP_ID;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * 简介：维度报表业务层
 *
 * @author: chenlin
 * @Description: 维度报表业务层
 * @Date: 2021/12/7 13:34
 * @since: 1.0.0
 */
@Service
public class DimensionReportBiz {

    /*就诊流程*/
    @Autowired
    private BaseTreatmentProcessBiz baseTreatmentProcessBiz;
    /*门诊基础服务*/
    @Autowired
    private RemoteClinicBaseServiceFeign clinicBaseServiceFeign;
    /*患者基础信息*/
    @Autowired
    private PatientBaseInfoBiz patientBaseInfoBiz;
    /*随访提醒*/
    @Autowired
    private BaseVisitRemindBiz baseVisitRemindBiz;
    /*账单收费时统计*/
    @Autowired
    private StatEmpPayBiz statEmpPayBiz;
    /*就诊完成时统计*/
    @Autowired
    private StatEmpTreatBiz statEmpTreatBiz;
    /*账单生成时统计*/
    @Autowired
    private StatEmpBillBiz statEmpBillBiz;
    /*账单详情*/
    @Autowired
    private BaseBillDetailBiz baseBillDetailBiz;
    /*账单*/
    @Autowired
    private BaseBillBiz baseBillBiz;
    /*员工工作量*/
    @Autowired
    private EmployeeWorkloadBiz employeeWorkloadBiz;
    /*组织*/
    @Autowired
    private BaseOrganizationBiz baseOrganizationBiz;
    /*卡券*/
    @Autowired
    private BaseCardBiz baseCardBiz;
    /*产品*/
    @Autowired
    private BaseCouponBiz baseCouponBiz;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Resource(name = "customizeThreadPool")
    private ThreadPoolExecutor threadPool;

    /**
     * 根据条件查询患者维度统计表
     *
     * @param query
     * @return
     */
    public DynamicHeaderPageInfo<JSONObject> patientDimensionStatistics(PatientDimensionQueryForm query) throws Exception {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        // 患者信息（姓名,年龄,患者来源类型,会员等级）
        List<PatientManageVo> patients = patientBaseInfoBiz.findPatientInfoList(query);
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>(patients);
        if (StringHelper.isEmpty(patients)) {
            return pageInfo;
        }
        List<Integer> patientIds = patients.stream().map(PatientManageVo::getPatientId).collect(Collectors.toList());

        // 就诊次数
        Future<Map<Integer, Integer>> treatNumFuture = multiFindPatientTreatNum(patientIds);
        // 初诊日期
        Future<Map<Integer, String>> firstVisitFuture = multiFindFirstVisitDateByPatientId(patientIds);
        // 末次就诊日期
        Future<Map<Integer, String>> lastVisitFuture = multiFindLastVisitDateByPatientId(patientIds);
        // 累计消费、欠费总额
        Future<Map<Integer, PatientCostInfoVO>> consumeFuture = multiFindPatientTotalConsumeArrear(patientIds);
        // 下次预约
        Future<Map<Integer, String>> appointFuture = multiFindNextAppointDateByPatientId(patientIds);
        // 下次提醒
        Future<Map<Integer, String>> remindFuture = multiFindNextRemindDateByPatientId(patientIds);
        // 账单项目
        Future<List<PersonalBillItemVO>> itemFuture = multiFindBillItemNumByPatientId(patientIds);
        // 数据合并
        return patientDimensionMerge(pageInfo, treatNumFuture, firstVisitFuture,
                lastVisitFuture,consumeFuture, appointFuture, remindFuture, itemFuture);
    }

    private DynamicHeaderPageInfo<JSONObject> patientDimensionMerge(PageInfo<PatientManageVo> pageInfo, Future<Map<Integer, Integer>> treatNumFuture,
                                       Future<Map<Integer, String>> firstVisitFuture, Future<Map<Integer, String>> lastVisitFuture,
                                       Future<Map<Integer, PatientCostInfoVO>> consumeFuture, Future<Map<Integer, String>> appointFuture,
                                       Future<Map<Integer, String>> remindFuture, Future<List<PersonalBillItemVO>> itemFuture) throws Exception {
        Map<Integer, Integer> treatNumMap = treatNumFuture.get();
        Map<Integer, String> firstVisitMap = firstVisitFuture.get();
        Map<Integer, String> lastVisitMap = lastVisitFuture.get();
        Map<Integer, PatientCostInfoVO> consumeArrearMap = consumeFuture.get();
        Map<Integer, String> appointMap = appointFuture.get();
        Map<Integer, String> remindMap = remindFuture.get();
        List<PersonalBillItemVO> billItems = itemFuture.get();
        // 专科项目数量
        Map<String, String> specialMap = new LinkedHashMap<>(16);
        Map<String, Integer> billItemMap = specialIdNameMap(billItems, specialMap);
        List<JSONObject> list = new ArrayList<>();
        List<PatientManageVo> patients = pageInfo.getList();
        patients.forEach(vo->{
            Integer patientId = vo.getPatientId();
            JSONObject obj = new JSONObject();
//            obj.put("patientId", vo.getPatientId());
            obj.put("patientName", vo.getPatientName());
            obj.put("age", defaultValue(vo.getAge()));
            obj.put("orionTypeName", defaultValue(vo.getPatientOrionTypeName()));
            obj.put("memberTypeName", defaultValue(vo.getMemberTypeName()));
            obj.put("treatNum", defaultValue(treatNumMap.get(patientId)));
            PatientCostInfoVO costInfo = consumeArrearMap.get(patientId);
            BigDecimal totalConsume = new BigDecimal(0.00);
            BigDecimal totalArrear = new BigDecimal(0.00);
            if (!ObjectUtils.isEmpty(costInfo)) {
                totalConsume = costInfo.getReceivedAmount();
                totalArrear = costInfo.getTotalArrears();
            }
            obj.put("totalConsume", totalConsume);
            obj.put("totalArrear", totalArrear);
            obj.put("firstVisitDate", defaultValue(firstVisitMap.get(patientId)));
            obj.put("lastVisitDate", defaultValue(lastVisitMap.get(patientId)));
            obj.put("nextAppointDate", defaultValue(appointMap.get(patientId)));
            obj.put("nextRemindDate", defaultValue(remindMap.get(patientId)));
            specialMap.forEach((id, name)->{
                obj.put(id, defaultValue(billItemMap.get(id+"."+patientId)));
            });
            list.add(obj);
        });
        return convertPatientDimensionPageInfo(pageInfo, list, specialMap);
    }

    private DynamicHeaderPageInfo<JSONObject> convertPatientDimensionPageInfo(PageInfo<PatientManageVo> pageInfo, List<JSONObject> list, Map<String, String> specialMap) {
        DynamicHeaderPageInfo<JSONObject> result = new DynamicHeaderPageInfo<>();
        result.setPageSize(pageInfo.getPageSize());
        result.setPageNum(pageInfo.getPageNum());
        result.setTotal(pageInfo.getTotal());
        result.setList(list);
        Map<String, String> title = new LinkedHashMap<>(16);
        title.put("patientName","患者");
        title.put("age","年龄");
        title.put("orionTypeName","患者类型");
        title.put("memberTypeName","会员等级");
        title.put("treatNum","就诊次数");
        title.put("totalConsume","累计消费");
        title.put("totalArrear","欠费总额");
        title.put("firstVisitDate","初诊日期");
        title.put("lastVisitDate","末次就诊日期");
        title.put("nextAppointDate","下次预约");
        title.put("nextRemindDate","下次提醒");
        title.putAll(specialMap);
        result.setMap(title);
        return result;
    }

    private Future<List<PersonalBillItemVO>> multiFindBillItemNumByPatientId(List<Integer> patientIds) {
        return threadPool.submit(()-> baseBillDetailBiz.findBillItemNumByPatientId(patientIds));
    }

    private Future<Map<Integer, PatientCostInfoVO>> multiFindPatientTotalConsumeArrear(List<Integer> patientIds) {
        return threadPool.submit(()->{
            List<PatientCostInfoVO> costInfos = baseBillBiz.findPatientCostInfo(patientIds);
            return costInfos.stream().collect(toMap(PatientCostInfoVO::getPatientId, Function.identity()));
        });
    }

    private Future<Map<Integer, Integer>> multiFindPatientTreatNum(List<Integer> patientIds) {
        return threadPool.submit(()->{
            List<PatientCountVO> patients = baseTreatmentProcessBiz.findPatientTreatNum(patientIds);
            return mapPatientCount(patients);
        });
    }

    /**
     * 转换成患者数量map
     *
     * @param list
     * @return
     */
    private Map<Integer, Integer> mapPatientCount(List<PatientCountVO> list) {
        if (StringHelper.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(toMap(PatientCountVO::getPatientId,PatientCountVO::getCount));
    }

    /**
     * 转换成患者日期map
     *
     * @param list
     * @return
     */
    private Map<Integer, String> mapPatientDate(List<PatientDateVO> list) {
        if (StringHelper.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(toMap(PatientDateVO::getPatientId, PatientDateVO::getDate));
    }

    private Future<Map<Integer, String>> multiFindLastVisitDateByPatientId(List<Integer> patientIds) {
        return threadPool.submit(()->{
            List<PatientDateVO> patients = baseTreatmentProcessBiz.findLastVisitDateByPatientId(patientIds);
            return mapPatientDate(patients);
        });
    }

    private Future<Map<Integer, String>> multiFindFirstVisitDateByPatientId(List<Integer> patientIds) {
        return threadPool.submit(()->{
           List<PatientDateVO> patients = baseTreatmentProcessBiz.findFirstVisitDateByPatientId(patientIds);
           return mapPatientDate(patients);
        });
    }

    private Future<Map<Integer, String>> multiFindNextAppointDateByPatientId(List<Integer> patientIds) {
        return threadPool.submit(()->{
            List<PatientDateVO> patients = baseTreatmentProcessBiz.findNextAppointDateByPatientId(patientIds);
            return mapPatientDate(patients);
        });
    }

    private Future<Map<Integer, String>> multiFindNextRemindDateByPatientId(List<Integer> patientIds) {
        return threadPool.submit(()->{
            List<PatientDateVO> reminds = baseVisitRemindBiz.findNextRemindByPatientId(patientIds);
            return mapPatientDate(reminds);
        });
    }

    /**
     * 根据条件导出患者维度统计表
     *
     * @param query
     * @param response
     * @throws IOException
     */
    public void patientDimensionStatisticsExport(PatientDimensionQueryForm query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = patientDimensionStatistics(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "患者维度统计");
        excelUtil.exportExcel(response, result, "患者维度统计", fileName, pageInfo.getMap());
    }

    /**
     * 根据条件查询医生维度统计表
     *
     * @param query
     * @return
     */
    public DynamicHeaderPageInfo<JSONObject> dentistDimensionStatistics(ClinicEmployeeWorkloadQuery query) throws Exception {
        return clinicDimensionStatistics(query, false);
    }

    /**
     * 一个月内既有初诊，又有复诊的人数
     *
     * @return
     * @param query
     * @param groupByOrgId
     */
    private Future<Map<String, Integer>> multiFindinMonthReFirstVisit(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findInMonthReFirstVisit(query, groupByOrgId);
            return mapEmployeeCount(employees);
        });
    }

    private Map<String, Integer> specialIdNameMap(List<PersonalBillItemVO> billItems, Map<String, String> specialMap) {
        Set<String> keys = new HashSet<>();
        billItems.forEach(vo-> keys.add(vo.getItemType() + "," + vo.getItemId()));
        SpecialistProjectQuery queryForm = new SpecialistProjectQuery();
        queryForm.setWhetherPage(false);
        List<SpecialistProjectVO> specialis = clinicBaseServiceFeign.specialProjectList(queryForm).getList();
        Map<String, Integer> result = new HashMap<>(16);
        Map<String, List<Integer>> specialItemMap = new HashMap<>();
        if (StringHelper.isNotEmpty(specialis)) {
            for (int i = 0; i < specialis.size(); i++) {
                SpecialistProjectVO vo = specialis.get(i);
                Integer id = vo.getId();
                String oralIds = vo.getOralIds();
                if (StringHelper.isNotEmpty(oralIds)) {
                    for (String oralId : StringHelper.split(oralIds, ",")) {
                        String key = "1,"+oralId;
                        if (keys.contains(key) && !specialMap.containsKey(id)) {
                            specialMap.put("S"+id, vo.getSpecialistProjectName());
                        }
                        List<Integer> list = specialItemMap.get(key);
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(id);
                        specialItemMap.put(key, list);
                    }
                }
                String tariffItemIds = vo.getTariffItemIds();
                if (StringHelper.isNotEmpty(tariffItemIds)) {
                    for (String tariffItemId : StringHelper.split(tariffItemIds, ",")) {
                        String key = "0,"+tariffItemId;
                        if (keys.contains(key) && !specialMap.containsKey(id)) {
                            specialMap.put("S"+id, vo.getSpecialistProjectName());
                        }
                        List<Integer> list = specialItemMap.get(key);
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add(id);
                        specialItemMap.put(key, list);
                    }
                }
            }
        }
        billItems.forEach(vo->{
            List<Integer> specialIds = specialItemMap.get(vo.getItemType()+","+vo.getItemId());
            if (StringHelper.isNotEmpty(specialIds)) {
                specialIds.forEach(specialId->{
                    String key = "S" +specialId + "." + vo.getPersonId();
                    if (!ObjectUtils.isEmpty(vo.getOrgId())) {
                        key += "," + vo.getOrgId();
                    }
                    Integer num = result.get(key);
                    if (num == null) {
                        num = 0;
                    }
                    result.put(key, num + vo.getQuantity());
                });
            }
        });
        return result;
    }

    /**
     * 根据条件导出医生维度统计表
     *
     * @param query
     * @param response
     * @throws IOException
     */
    public void dentistDimensionStatisticsExport(ClinicEmployeeWorkloadQuery query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = dentistDimensionStatistics(query);
        List<JSONObject> list = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        if (StringHelper.isNotEmpty(pageInfo.getContextMap())) {
            excelUtil.setMergeRegion(collectMergeCell(pageInfo, 6));
        }
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "医生维度统计");
        excelUtil.exportExcel(response, list, "医生维度统计", fileName, pageInfo.getHeader(), pageInfo.getMap());
    }

    /**
     * 根据条件查询门诊维度统计表
     *
     * @param query
     * @return
     */
    public DynamicHeaderPageInfo<JSONObject> clinicDimensionStatistics(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) throws Exception {
        filterDentistPost(query);
        List<ClinicEmployeBonusCoefficientVO> employees = employeeWorkloadBiz.findClinicEmployeeCartesianProduct(query, groupByOrgId);
        updEmployeeId2Query(employees, query);

        // 实收工作量
        Future<Map<String, BigDecimal>> workloadFuture = employeeWorkloadBiz.findClinicEmployeeReceivedWorkload(query, groupByOrgId);

        // 初诊人数
        Future<Map<String, Integer>> firstVisitFuture = multiFindFirstVisitNum(query, groupByOrgId);

        // 复诊人数
        Future<Map<String, Integer>> reVisitFuture = multiFindRepeatVisitNum(query, groupByOrgId);

        // 就诊人次
        Future<Map<String, Integer>> treatFuture = multiFindTreatVisitsTimes(query, groupByOrgId);

        // 本月初诊且/复诊
        Future<Map<String, Integer>> reFirstVisitFuture = multiFindinMonthReFirstVisit(query, groupByOrgId);

        // 欠费总额
        Future<Map<String, BigDecimal>> debtFuture = multiFindPatientDebtAmount(query, groupByOrgId);

        // 无下次预约或提醒客户
        Future<Map<String, Integer>> noAppointAndRemindFuture = multiFindHasntAppointAndRemind(query, groupByOrgId);

        // 患者来源
        Future<List<EmployeeFirstVisitOriginTypeVO>> patientOrginFuture = multiFindFirstVisitPatientOriginType(query, groupByOrgId);

        // 专科项目
        Future<List<PersonalBillItemVO>> billItemFuture = multiFindExecutorBillItem(query, groupByOrgId);
        return mergeClinicDimension(employees, workloadFuture.get(), firstVisitFuture.get(), reFirstVisitFuture.get(),
                treatFuture.get(), debtFuture.get(), noAppointAndRemindFuture.get(), patientOrginFuture.get(),
                billItemFuture.get(), reVisitFuture.get(), groupByOrgId);
    }

    private void filterDentistPost(ClinicEmployeeWorkloadQuery query) {
        Integer[] employeeIds = query.getEmployeeIds();
        if (StringHelper.isEmpty(employeeIds)) {
            SysUserEmployeeModel empQuery = new SysUserEmployeeModel();
            empQuery.setPostGroupId(Collections.singletonList(DENTIST_GROUP_ID));
            Integer[] workStatus = query.getWorkStatus();
            if (StringHelper.isNotEmpty(workStatus)) {
                Byte[] wStatus = new Byte[workStatus.length];
                for (int i = 0; i < workStatus.length; i++) {
                    wStatus[i] = (byte) workStatus[i].intValue();
                }
                empQuery.setWorkStatus(wStatus);
            }
            empQuery.setWhetherPage(false);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(empQuery);
            employeeIds = employees.stream().map(SysUserInfoDetail::getEmployeeId).toArray(Integer[]::new);
            query.setEmployeeIds(employeeIds);
        }
    }

    private void updEmployeeId2Query(List<ClinicEmployeBonusCoefficientVO> employees, ClinicEmployeeWorkloadQuery query) {
        List<Integer> employeeIds = new ArrayList<>();
        employees.forEach(vo->{
            Integer employeeId = vo.getEmployeeId();
            if (!employeeIds.contains(employeeId)) {
                employeeIds.add(employeeId);
            }
        });
        query.setEmployeeIds(employeeIds.toArray(new Integer[0]));
    }

    private DynamicHeaderPageInfo<JSONObject> mergeClinicDimension(List<ClinicEmployeBonusCoefficientVO> employees,
            Map<String, BigDecimal> workloadMap, Map<String, Integer> firstVisitMap, Map<String, Integer> reFirstVisitMap,
            Map<String, Integer> treatTimesMap, Map<String, BigDecimal> debtMap, Map<String, Integer> noARMap,
            List<EmployeeFirstVisitOriginTypeVO> originTypes, List<PersonalBillItemVO> billItems, Map<String, Integer> reVisitMap, boolean groupByOrgId) throws Exception {
        Map<String, List<String>> title = new HashMap<>(16);
        Map<String, Integer> originDataMap = new HashMap<>(16);
        Map<String, String> originTypeMap = new LinkedHashMap<>(16);
        if (StringHelper.isNotEmpty(originTypes)) {
            originTypes.forEach(vo->{
                Integer originTypeId = vo.getOriginTypeId();
                originDataMap.put(vo.getEmployeeId()+","+vo.getOrgId()+",T"+originTypeId, vo.getCount());
                String originTypeName = vo.getOriginTypeName();
                if (!originTypeMap.containsKey(originTypeId+"")) {
                    originTypeMap.put("T"+originTypeId, originTypeName);
                }
            });
            title.put("originTypeNames", originTypeMap.values().stream().collect(Collectors.toList()));
        }
        Map<String, String> specialMap = new LinkedHashMap<>(16);
        Map<String, Integer> billItemMap = specialIdNameMap(billItems, specialMap);
        if (StringHelper.isNotEmpty(specialMap)) {
            title.put("specialProjectNames", specialMap.values().stream().collect(Collectors.toList()));
        }
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo(employees);
        if (StringHelper.isNotEmpty(employees)) {
            List<JSONObject> list = new ArrayList<>();
            employees.forEach(vo -> {
                Integer employeeId = vo.getEmployeeId();
                String orgId = ObjectUtils.isEmpty(vo.getOrgId())?"":","+vo.getOrgId();
                String key = employeeId + orgId;
                JSONObject obj = new JSONObject();
//            obj.put("employeeId", employeeId);
//            obj.put("orgId", orgId);
                if (groupByOrgId) {
                    obj.put("abbreviation", defaultValue(vo.getAbbreviation()));
                }
                obj.put("employeeName", defaultValue(vo.getEmployeeName()));
                obj.put("workload", defaultValue(workloadMap.get(key)));
                obj.put("firstVisitCount", defaultValue(firstVisitMap.get(key)));
                obj.put("reVisitCount", defaultValue(reVisitMap.get(key)));
                obj.put("treatTimes", defaultValue(treatTimesMap.get(key)));
                obj.put("reFirstVisitCount", defaultValue(reFirstVisitMap.get(key)));
                obj.put("debtAmount", defaultValue(debtMap.get(key)));
                obj.put("hasntAppointAndRemind", defaultValue(noARMap.get(key)));
                originTypeMap.forEach((originTypeId, name) -> {
                    Integer num = originDataMap.get(key + "," + originTypeId);
                    obj.put(originTypeId, defaultValue(num));
                });
                specialMap.forEach((id, name) -> {
                    String itemKey = id + "." + employeeId + orgId;
                    obj.put(id, defaultValue(billItemMap.get(itemKey)));
                });
                list.add(obj);
            });
            pageInfo.setList(list);
            convertClinicDimensionPageInfo(pageInfo, title, originTypeMap, specialMap, groupByOrgId);
        }
        return pageInfo;
    }

    private void convertClinicDimensionPageInfo(DynamicHeaderPageInfo<JSONObject> result, Map<String, List<String>> contextMap, Map<String, String> originTypeMap, Map<String, String> specialMap, boolean groupByOrgId) {
        Map<String, String> title = new LinkedHashMap<>(16);
        if (groupByOrgId) {
            title.put("abbreviation", "门诊");
        }
        title.put("employeeName", "医生");
        title.put("workload", "工作量");
        title.put("firstVisitCount", "初诊人数");
        title.put("reVisitCount", "复诊人数");
        title.put("treatTimes", "就诊人次");
        title.put("reFirstVisitCount", "本月初诊且复诊");
//        title.put("originTypeNames", "患者来源");
        title.putAll(originTypeMap);
        title.put("debtAmount", "欠费总额");
        title.put("hasntAppointAndRemind", "无下次预约或提醒客户");
//        title.put("specialProjectNames", "专科数量");
        title.putAll(specialMap);
        result.setMap(title);
        result.setContextMap(contextMap);
        result.setHeader(clinicDimensionHeader(contextMap, groupByOrgId));
    }

    private String[] clinicDimensionHeader(Map<String, List<String>> contextMap, boolean groupByOrgId) {
        int size = 0;
        for (Map.Entry<String, List<String>> entry : contextMap.entrySet()) {
            size += entry.getValue().size();
        }
        String[] header = new String[size+8];
        int index = 0;
        if (groupByOrgId) {
            header = new String[size+9];
            header[index++] = "门诊";
        }
        header[index++] = "医生";
        header[index++] = "工作量";
        header[index++] = "初诊人数";
        header[index++] = "复诊人数";
        header[index++] = "就诊人次";
        header[index++] = "本月初诊且复诊";
        List<String> originTypeNames = contextMap.get("originTypeNames");
        if (StringHelper.isNotEmpty(originTypeNames)) {
            for (int i = 0; i < originTypeNames.size(); i++) {
                if (i == 0) {
                    header[index++] = "患者来源";
                } else {
                    header[index++] = "";
                }
            }
        }
        header[index++] = "欠费总额";
        header[index++] = "无下次预约或提醒客户";
        List<String> specialProjectNames = contextMap.get("specialProjectNames");
        if (StringHelper.isNotEmpty(specialProjectNames)) {
            for (int i = 0; i < specialProjectNames.size(); i++) {
                if (i == 0) {
                    header[index++] = "专科数量";
                } else {
                    header[index++] = "";
                }
            }
        }
        return header;
    }

    private String defaultValue(String value) {
        if (!ObjectUtils.isEmpty(value)) {
            return value;
        } else {
            return "";
        }
    }

    private int defaultValue(Integer value) {
        if (!ObjectUtils.isEmpty(value)) {
            return value;
        } else {
            return 0;
        }
    }

    private BigDecimal defaultValue(BigDecimal value) {
        if (!ObjectUtils.isEmpty(value)) {
            return value.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal("0.00");
        }
    }

    private Future<List<PersonalBillItemVO>> multiFindExecutorBillItem(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()-> {
            List<PersonalBillItemVO> list = baseBillDetailBiz.findExecutorBillItem(query, groupByOrgId);
            return list;
        });
    }

    private Future<List<EmployeeFirstVisitOriginTypeVO>> multiFindFirstVisitPatientOriginType(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()-> baseTreatmentProcessBiz.findFirstVisitPatientOriginType(query, groupByOrgId));
    }

    private Future<Map<String, Integer>> multiFindHasntAppointAndRemind(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findHasntAppointAndRemind(query, groupByOrgId);
           return mapEmployeeCount(employees);
        });
    }

    private Future<Map<String, BigDecimal>> multiFindPatientDebtAmount(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()->{
            List<EmployeeAmountVO> employees = baseBillBiz.findPatientDebAmount(query, groupByOrgId);
           return mapEmployeeAmount(employees);
        });
    }

    private Future<Map<String, Integer>> multiFindTreatVisitsTimes(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findTreatVisitsTimes(query, groupByOrgId);
            return mapEmployeeCount(employees);
        });
    }

    private Future<Map<String, Integer>> multiFindRepeatVisitNum(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findPatientTreatNumGroupEmp(query, 1, groupByOrgId);
            return mapEmployeeCount(employees);
        });
    }

    private Future<Map<String, Integer>> multiFindFirstVisitNum(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findPatientTreatNumGroupEmp(query, 0, groupByOrgId);
            return mapEmployeeCount(employees);
        });
    }

    /**
     * 转换成员工金额map
     *
     * @param list
     * @return
     */
    private Map<String, BigDecimal> mapEmployeeAmount(List<EmployeeAmountVO> list) {
        if (StringHelper.isEmpty(list)) {
            return new HashMap<>();
        }
        Map<String, BigDecimal> result = new HashMap<>(16);
        list.forEach(vo-> result.put(vo.getEmployeeId()+","+vo.getOrgId(), vo.getAmount()));
        return result;
    }

    /**
     * 转换成员工数量map
     *
     * @param list
     * @return
     */
    private Map<String, Integer> mapEmployeeCount(List<EmployeeCountVO> list) {
        if (StringHelper.isEmpty(list)) {
            return new HashMap<>();
        }
        Map<String, Integer> result = new HashMap<>(16);
        list.forEach(vo->{
            String key = vo.getEmployeeId() + "";
            Integer orgId = vo.getOrgId();
            if (!ObjectUtils.isEmpty(orgId)) {
                key += "," + orgId;
            }
            result.put(key, vo.getCount());
        });
        return result;
    }

    /**
     * 收集合并单元格的位置
     *
     * @param pageInfo
     * @param lastCol
     * @return
     */
    private List<CellRangeAddress> collectMergeCell(DynamicHeaderPageInfo<JSONObject> pageInfo, int lastCol) {
        Map<String, List<String>> contextMap = pageInfo.getContextMap();
        List<String> originTypeNames = contextMap.get("originTypeNames");
        List<String> specialProjectNames = contextMap.get("specialProjectNames");
        Map<String, String> title = pageInfo.getMap();
        List<JSONObject> data = pageInfo.getList();
        JSONObject secTitle = new JSONObject();
        title.forEach((key, value)->{
            if (key.startsWith("S") || key.startsWith("T")) {
                secTitle.put(key, value);
            }
        });
        // 添加占位数据行
        data.add(0, secTitle);
        List<CellRangeAddress> region = new ArrayList<>();
        // 纵向合并单元格
        for (int i = 0; i < lastCol; i++) {
            CellRangeAddress crd = new CellRangeAddress(0,1, i , i);
            region.add(crd);
        }
        // 横向合并单元格
        int index = lastCol;
        if (StringHelper.isNotEmpty(originTypeNames) && originTypeNames.size()>1) {
            index += originTypeNames.size()-1;
            region.add(new CellRangeAddress(0,0, lastCol, index));
        }
        region.add(new CellRangeAddress(0,1, ++index, index));
        region.add(new CellRangeAddress(0,1, ++index, index));
        if (StringHelper.isNotEmpty(specialProjectNames) && specialProjectNames.size()>1) {
            region.add(new CellRangeAddress(0, 0, ++index, index + specialProjectNames.size() - 1));
        }
        return region;
    }


    /**
     * 根据条件导出门诊维度统计表
     *
     * @param query
     * @param response
     * @throws IOException
     */
    public void clinicDimensionStatisticsExport(ClinicEmployeeWorkloadQuery query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = clinicDimensionStatistics(query, true);
        List<JSONObject> list = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        if (StringHelper.isNotEmpty(pageInfo.getContextMap())) {
            excelUtil.setMergeRegion(collectMergeCell(pageInfo, 7));
        }
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "门诊维度统计");
        excelUtil.exportExcel(response, list, "门诊维度统计", fileName, pageInfo.getHeader(), pageInfo.getMap());
    }

    /**
     * 根据条件查询门诊初诊来源占比表
     *
     * @param query
     * @return
     */
    public DynamicHeaderPageInfo<JSONObject> clinicFirstVisitSourceRatio(MultiClinicDateRangeQueryForm query) {
        ClinicPerformanceBusinessQuery queryFrom = new ClinicPerformanceBusinessQuery();
        queryFrom.setOrgIds(query.getOrgIds());
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<BaseOrganization> orgs = baseOrganizationBiz.getOrganization(queryFrom);
        DynamicHeaderPageInfo result = new DynamicHeaderPageInfo<>(orgs);
        ClinicPerformanceBusinessQuery clinicQuery = new ClinicPerformanceBusinessQuery();
        clinicQuery.setWhetherPage(false);
        clinicQuery.setOrgIds(query.getOrgIds());
        clinicQuery.setDateType(query.getDateType());
        clinicQuery.setStartDate(query.getStartDate());
        clinicQuery.setEndDate(query.getEndDate());
        List<BaseTreatmentProcessVO> firstVisitPatients = patientBaseInfoBiz.firstVisitPatientList(clinicQuery);
        Map<Integer, Integer> firstVisitMap = mapClinicFirstVisitCount(firstVisitPatients);
        List<PatientFirstVisitSourceVO> patients = baseBillDetailBiz.multiFirstVisitPatientSourceList(clinicQuery, firstVisitPatients);
        if (StringHelper.isNotEmpty(orgs)) {
            Map<String, Integer> originDataMap = new HashMap<>(16);
            Map<String, String> originMap = new LinkedHashMap<>(16);
            if (StringHelper.isNotEmpty(patients)) {
                patients.forEach(vo->{
                    originMap.put(vo.getOriginType()+"", vo.getOriginTypeName());
                    originDataMap.put(vo.getOrgId()+","+vo.getOriginType(), vo.getFirstVisitCount());
                });
            }
            List<JSONObject> list = new ArrayList<>();
            orgs.forEach(vo->{
                Integer orgId = vo.getOrgId();
                JSONObject obj = new JSONObject();
                obj.put("abbreviation", defaultValue(vo.getAbbreviation()));
                obj.put("date", baseBillDetailBiz.doDateStyle(query.getStartDate(),
                        query.getEndDate()));
                obj.put("firstVisitCount", defaultValue(firstVisitMap.get(orgId)));
                originMap.forEach((originTypeId, name)-> obj.put(originTypeId, defaultValue(originDataMap.get(orgId+","+originTypeId))));
                list.add(obj);
            });
            result.setList(list);
            result.setMap(originTitleMap(originMap));
        }
        return result;
    }

    /**
     * 门诊初诊来源占比表标题
     *
     * @param originMap
     * @return
     */
    private Map<String, String> originTitleMap(Map<String, String> originMap) {
        Map<String, String> title = new LinkedHashMap<>(16);
        title.put("abbreviation", "门诊");
        title.put("date", "日期");
        title.put("firstVisitCount", "初诊人数");
        title.putAll(originMap);
        return title;
    }

    /**
     * 统计门诊的初诊人数
     *
     * @param firstVisitPatients
     * @return
     */
    private Map<Integer, Integer> mapClinicFirstVisitCount(List<BaseTreatmentProcessVO> firstVisitPatients) {
        Map<Integer, Integer> result = new HashMap<>(16);
        if (StringHelper.isNotEmpty(firstVisitPatients)) {
            firstVisitPatients.forEach(vo->{
                Integer orgId = vo.getOrgId();
                Integer count = result.get(orgId);
                if (count == null) {
                    count = 0;
                }
                result.put(orgId, count+1);
            });
        }
        return result;
    }

    /**
     * 根据条件导出门诊初诊来源占比表
     *
     * @param query
     * @param response
     */
    public void clinicFirstVisitSourceRatioExport(MultiClinicDateRangeQueryForm query, HttpServletResponse response) throws IOException {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = clinicFirstVisitSourceRatio(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "初诊来源占比表");
        excelUtil.exportExcel(response, result, "初诊来源占比表", fileName, pageInfo.getMap());
    }

    /**
     * 根据条件查询门诊专科工作量占比
     *
     * @param query
     * @return
     * @throws Exception
     */
    public DynamicHeaderPageInfo<JSONObject> clinicSpecialProjectWorkloadRatio(MultiClinicDateRangeQueryForm query) throws Exception {
        ClinicPerformanceBusinessQuery queryFrom = new ClinicPerformanceBusinessQuery();
        queryFrom.setOrgIds(query.getOrgIds());
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<BaseOrganization> orgs = baseOrganizationBiz.getOrganization(queryFrom);
        Integer[] orgIds = orgs.stream().map(BaseOrganization::getOrgId).toArray(Integer[]::new);
        ClinicEmployeeWorkloadQuery workloadQuery = new ClinicEmployeeWorkloadQuery();
        workloadQuery.setWhetherPage(false);
        workloadQuery.setDateType(query.getDateType());
        workloadQuery.setStartDate(query.getStartDate());
        workloadQuery.setEndDate(query.getEndDate());
        workloadQuery.setOrgIds(orgIds);
        SpecialistProjectQuery queryForm = new SpecialistProjectQuery();
        queryForm.setWhetherPage(false);
        List<SpecialistProjectVO> specialis = clinicBaseServiceFeign.specialProjectList(queryForm).getList();
        Set<Integer> itemIds = new HashSet<>();
        Set<Integer> oralIds = new HashSet<>();
        specialis.forEach(vo->{
            String tariffIdStr = vo.getTariffItemIds();
            if (StringHelper.isNotEmpty(tariffIdStr)) {
                itemIds.addAll(StringHelper.split2IntList(tariffIdStr, ","));
            }
            String oralIdStr = vo.getOralIds();
            if (StringHelper.isNotEmpty(oralIdStr)) {
                oralIds.addAll(StringHelper.split2IntList(oralIdStr, ","));
            }
        });
        // 门诊的实收工作量
        Future<Map<String, BigDecimal>> workloadFuture = employeeWorkloadBiz.findClinicEmployeeReceivedWorkload(workloadQuery, true);
        BillItemTollWorkloadQuery itemQuery = new BillItemTollWorkloadQuery();
        itemQuery.setWhetherPage(false);
        itemQuery.setDateType(query.getDateType());
        itemQuery.setStartDate(query.getStartDate());
        itemQuery.setEndDate(query.getEndDate());
        itemQuery.setOrgIds(orgIds);
        itemQuery.setItemIds(itemIds);
        // 价目的实收工作量
        Future<Map<String, EmployeeTariffWorkloadVO>> tariffWorkload = employeeWorkloadBiz.findClinicExecutorTariffReceivedWorkload(itemQuery);
        BillItemTollWorkloadQuery oralQuery = new BillItemTollWorkloadQuery();
        oralQuery.setWhetherPage(false);
        oralQuery.setDateType(query.getDateType());
        oralQuery.setStartDate(query.getStartDate());
        oralQuery.setEndDate(query.getEndDate());
        oralQuery.setOrgIds(orgIds);
        oralQuery.setItemType((byte) 1);
        oralQuery.setItemIds(oralIds);
        // 商品的实收工作量
        Future<Map<String, EmployeeTariffWorkloadVO>> oralWorkload = employeeWorkloadBiz.findClinicExecutorTariffReceivedWorkload(oralQuery);
        return mergeSpecialProjectWorkloadRatio(orgs, baseBillDetailBiz.doDateStyle(query.getStartDate(), query.getEndDate()),
                workloadFuture.get(), tariffWorkload.get(), oralWorkload.get(), specialis);
    }

    /**
     * 专科工作量占比数据合并
     *
     * @param orgs
     * @param date
     * @param workloadMap
     * @param tariffWorkload
     * @param oralWorkload
     * @param specialis
     * @return
     */
    private DynamicHeaderPageInfo<JSONObject> mergeSpecialProjectWorkloadRatio(List<BaseOrganization> orgs, String date,
               Map<String, BigDecimal> workloadMap, Map<String, EmployeeTariffWorkloadVO> tariffWorkload,
               Map<String, EmployeeTariffWorkloadVO> oralWorkload, List<SpecialistProjectVO> specialis) {
        DynamicHeaderPageInfo result = new DynamicHeaderPageInfo<>(orgs);
        List<JSONObject> list = new ArrayList<>();
        Map<String, String> specialMap = new LinkedHashMap<>(16);
        if (StringHelper.isNotEmpty(orgs)) {
            Map<Integer, BigDecimal> orgWorkloadMap = emp2OrgWorkloadMap(workloadMap);
            Map<String, List<Integer>> item2Special = new HashMap<>(16);
            specialis.forEach(vo->{
                Integer id = vo.getId();
                putItem2Special(vo.getTariffItemIds(), "0", id, item2Special);
                putItem2Special(vo.getOralIds(), "1", id, item2Special);
                specialMap.put(""+id, vo.getSpecialistProjectName());
            });
            Map<String, BigDecimal> orgItemWorkloadMap = item2SpecialWorkloadMap(tariffWorkload, oralWorkload, item2Special);
            orgs.forEach(org->{
                Integer orgId = org.getOrgId();
                JSONObject obj = new JSONObject();
                obj.put("abbreviation", org.getAbbreviation());
                obj.put("date", date);
                BigDecimal workload = defaultValue(orgWorkloadMap.get(orgId));
                obj.put("workload", workload);
                specialMap.forEach((specialId, name)->{
                    BigDecimal itemWorkload = defaultValue(orgItemWorkloadMap.get(orgId + "," + specialId));
                    BigDecimal percentage = new BigDecimal("0");
                    if (workload.compareTo(BigDecimal.ZERO)!=0) {
                        percentage = itemWorkload
                                .divide(workload, 2, BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal(100));

                    }
                    obj.put(specialId, percentage + "%");
                });
                list.add(obj);
            });
        }
        result.setList(list);
        result.setMap(specialTitleMap(specialMap));
        return result;
    }

    /**
     * 项目映射关系转换
     * @param itemIds 项目id列表字符串形式
     * @param itemType 项目类型
     * @param specialId 专科项目id
     * @param item2Special 转换结果
     */
    private void putItem2Special(String itemIds, String itemType, Integer specialId, Map<String, List<Integer>> item2Special) {
        if (StringHelper.isNotEmpty(itemIds)) {
            String[] itemIdStr = StringHelper.split(itemIds, ",");
            for (String itemId : itemIdStr) {
                String key = itemType + "," + itemId;
                List<Integer> ids = item2Special.get(key);
                if (ids == null) {
                    ids = new ArrayList<>();
                }
                ids.add(specialId);
                item2Special.put(key, ids);
            }
        }
    }

    private Map<String, String> specialTitleMap(Map<String, String> specialMap) {
        Map<String, String> title = new LinkedHashMap<>(16);
        title.put("abbreviation", "门诊");
        title.put("date", "日期");
        title.put("workload", "工作量");
        title.putAll(specialMap);
        return title;
    }

    /**
     * 员工工作量统计转换为门诊工作量统计
     * @param workloadMap
     * @return
     */
    private Map<Integer, BigDecimal> emp2OrgWorkloadMap(Map<String, BigDecimal> workloadMap) {
        Map<Integer, BigDecimal> orgWorkloadMap = new HashMap<>(16);
        workloadMap.forEach((keyStr, workload)->{
            String[] keys = StringHelper.split(keyStr, ",");
            Integer orgId = Integer.parseInt(keys[1]);
            BigDecimal totalWorkload = orgWorkloadMap.get(orgId);
            if (totalWorkload == null) {
                totalWorkload = new BigDecimal("0.00");
            }
            orgWorkloadMap.put(orgId, totalWorkload.add(workload));
        });
        return orgWorkloadMap;
    }

    /**
     * 员工项目工作量统计转换为门诊专科工作量统计
     *
     * @param tariffWorkload
     * @param oralWorkload
     * @param item2Special
     * @return
     */
    private Map<String, BigDecimal> item2SpecialWorkloadMap(Map<String, EmployeeTariffWorkloadVO> tariffWorkload,
                                Map<String, EmployeeTariffWorkloadVO> oralWorkload, Map<String, List<Integer>> item2Special) {
        Map<String, BigDecimal> orgItemWorkloadMap = new HashMap<>(16);
        if (StringHelper.isNotEmpty(tariffWorkload)) {
            tariffWorkload.forEach((keyStr, vo) -> cumulation(keyStr, item2Special, vo, orgItemWorkloadMap, "0"));
        }
        if (StringHelper.isNotEmpty(oralWorkload)) {
            oralWorkload.forEach((keyStr, vo) -> cumulation(keyStr, item2Special, vo, orgItemWorkloadMap, "1"));
        }
        return orgItemWorkloadMap;
    }

    /**
     * 累加
     *
     * @param keyStr
     * @param item2Special
     * @param vo
     * @param orgItemWorkloadMap
     */
    private void cumulation(String keyStr, Map<String, List<Integer>> item2Special, EmployeeTariffWorkloadVO vo, Map<String, BigDecimal> orgItemWorkloadMap, String itemKey) {
        String[] keys = StringHelper.substringsBetween(keyStr, ",", ".");
        List<Integer> specialIds = item2Special.get(itemKey+","+vo.getItemId());
        if (StringHelper.isNotEmpty(specialIds)) {
            specialIds.forEach(specialId->{
                String key = keys[0] + "," + specialId;
                BigDecimal workload = orgItemWorkloadMap.get(key);
                if (workload == null) {
                    workload = new BigDecimal("0.00");
                }
                orgItemWorkloadMap.put(key, workload.add(vo.getWorkload()));
            });
        }
    }

    /**
     * 根据条件导出门诊专科工作量占比
     *
     * @param query
     * @param response
     * @throws Exception
     */
    public void clinicSpecialProjectWorkloadRatioExport(MultiClinicDateRangeQueryForm query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = clinicSpecialProjectWorkloadRatio(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "专科占比表");
        excelUtil.exportExcel(response, result, "专科占比表", fileName, pageInfo.getMap());
    }

    /**
     * 根据条件查询门诊统计表（实收工作量、初诊人数、复诊人数）
     *
     * @param query
     * @return
     */
    public DynamicHeaderPageInfo<JSONObject> clinicWorkloadVisitStatistics(MultiClinicDateRangeQueryForm query) throws Exception {
        Byte dateType = query.getDateType();
        if (dateType.intValue() != 2) {
            throw new ClientServiceException("请选择年份！",PARAMETERS_IS_ILLEGAL);
        }
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        ClinicPerformanceBusinessQuery clinicQuery = new ClinicPerformanceBusinessQuery();
        clinicQuery.setOrgIds(query.getOrgIds());
        List<BaseOrganization> orgs = baseOrganizationBiz.getOrganization(clinicQuery);
        Future<Map<String, BigDecimal>> workloadFuture = multiFindClinicReceivedWorkload(query, query.getOrgIds(), null);
        Future<Map<String, StatEmpTreat>> treatNumFuture = multiFindClinicTreatVisitNum(query, query.getOrgIds(), null);
        return mergeClinicWorkloadVisitStatistice(query, orgs, workloadFuture.get(), treatNumFuture.get());
    }

    /**
     * 合并门诊工作量就诊统计
     *
     * @param query
     * @param orgs
     * @param workloadMap
     * @param treatNumMap
     * @return
     */
    private DynamicHeaderPageInfo<JSONObject> mergeClinicWorkloadVisitStatistice(DateRangeQueryForm query,
                                                                                 List<BaseOrganization> orgs, Map<String, BigDecimal> workloadMap, Map<String, StatEmpTreat> treatNumMap) {
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>(orgs);
        if (StringHelper.isNotEmpty(orgs)) {
            String startDate = query.getStartDate();
            String endDate = query.getEndDate();
            List<String> years = DateUtil.sliceUpDateRange(startDate, endDate);
            int size = Integer.parseInt(endDate) - Integer.parseInt(startDate) + 1;
            List<JSONObject> list = new ArrayList<>();
            Map<String, String> title = new LinkedHashMap<>(16);
            BigDecimal[][] total = new BigDecimal[13][years.size() * 3];
            orgs.forEach(vo -> putObject(vo.getOrgId(), vo.getAbbreviation(), size, years, workloadMap, treatNumMap, title, list, total));
            putTotalObj(total, years, list);
            pageInfo.setMap(title);
            pageInfo.setList(list);
            pageInfo.setTotal(pageInfo.getTotal());
            pageInfo.setPageNum(query.getPageNum());
            pageInfo.setPageSize(query.getPageSize());
        }
        return pageInfo;
    }

    /**
     * 统计年份下的工作量、初诊人数、就诊人数
     *
     * @param total
     * @param years
     * @param list
     */
    private void putTotalObj(BigDecimal[][] total, List<String> years, List<JSONObject> list) {
        for (int i = 0; i < total.length; i++) {
            String month = String.valueOf(i + 1);
            if (i == total.length-1) {
                month = "合计";
            }
            JSONObject obj = initMonthObj("合计", month);
            int fInx = years.size();
            int rInx = years.size() * 2;
            for (int wInx = 0; wInx < years.size();) {
                String year = years.get(wInx);
                obj.put("W" + year, total[i][wInx++]);
                obj.put("F" + year, total[i][fInx++]);
                obj.put("R" + year, total[i][rInx++]);
            }
            list.add(obj);
        }
    }

    private BigDecimal[][] putObject(Integer keyId, String name, int size, List<String> years,
                                     Map<String, BigDecimal> workloadMap, Map<String, StatEmpTreat> treatNumMap,
                                     Map<String, String> title, List<JSONObject> list, BigDecimal[][] total) {
        BigDecimal[] totalWorkload = new BigDecimal[size];
        for (int i = 0; i < size; i++) {
            totalWorkload[i] = new BigDecimal("0.00");
        }
        int[] totalFirstVisitCount = new int[size];
        int[] totalTreatVisitCount = new int[size];
        // 门诊的每年每月统计
        for (int i = 1; i <=12; i++) {
            JSONObject obj = initMonthObj(name, i+"");
            int fInx = years.size();
            int rInx = years.size() * 2;
            for (int wInx = 0; wInx < years.size(); wInx++,fInx++,rInx++) {
                String year = years.get(wInx);
                String key = keyId + "," + year;
                if (i < 10) {
                    key += "0" + i;
                } else {
                    key += i;
                }
                BigDecimal workload = defaultValue(workloadMap.get(key));
                obj.put("W"+year, workload);
                StatEmpTreat statEmpTreat = treatNumMap.get(key);
                int firstVisitCount = 0;
                int treatVisitCount = 0;
                if (!ObjectUtils.isEmpty(statEmpTreat)) {
                    firstVisitCount = statEmpTreat.getFirstVisitCount();
                    treatVisitCount = statEmpTreat.getReVisitCount() + firstVisitCount;
                }
                obj.put("F"+year, firstVisitCount);
                obj.put("R"+year, treatVisitCount);
                totalWorkload[wInx] = totalWorkload[wInx].add(workload);
                totalFirstVisitCount[wInx] += firstVisitCount;
                totalTreatVisitCount[wInx] += treatVisitCount;
                cumulation(i-1, wInx, fInx, rInx, total, workload, firstVisitCount, treatVisitCount);
            }
            list.add(obj);
        }
        title.put("name", "门诊");
        title.put("Wmonth", "月份");
        years.forEach(year-> title.put("W"+year, year));
        title.put("Fmonth", "月份");
        years.forEach(year-> title.put("F"+year, year));
        title.put("Rmonth", "月份");
        years.forEach(year-> title.put("R"+year, year));
        JSONObject totalObj = initMonthObj(defaultValue(name), "合计");
        int fInx = years.size();
        int rInx = years.size() * 2;
        for (int wInx = 0; wInx < years.size(); wInx++,fInx++,rInx++) {
            String year = years.get(wInx);
            BigDecimal workload = defaultValue(totalWorkload[wInx]);
            totalObj.put("W"+year, workload);
            int firstVisitCount = defaultValue(totalFirstVisitCount[wInx]);
            totalObj.put("F"+year, firstVisitCount);
            int treatVisitCount = defaultValue(totalTreatVisitCount[wInx]);
            totalObj.put("R"+year, treatVisitCount);
            cumulation(12, wInx, fInx, rInx, total, workload, firstVisitCount, treatVisitCount);
        }
        list.add(totalObj);
        return total;
    }

    /**
     * 累加12个月的工作量、就诊次数
     *
     * @param col
     * @param wInx
     * @param fInx
     * @param rInx
     * @param total
     * @param workload
     * @param firstVisitCount
     * @param treatVisitCount
     */
    private void cumulation(int col, int wInx, int fInx, int rInx, BigDecimal[][] total, BigDecimal workload, int firstVisitCount, int treatVisitCount) {
        if (total[col][wInx] == null) {
            total[col][wInx] = new BigDecimal("0.00");
        }
        if (total[col][fInx] == null) {
            total[col][fInx] = new BigDecimal("0");
        }
        if (total[col][rInx] == null) {
            total[col][rInx] = new BigDecimal("0");
        }
        total[col][wInx] = total[col][wInx].add(workload);
        total[col][fInx] = total[col][fInx].add(new BigDecimal(firstVisitCount));
        total[col][rInx] = total[col][rInx].add(new BigDecimal(treatVisitCount));
    }

    private JSONObject initMonthObj(String name, String month) {
        JSONObject obj = new JSONObject();
        obj.put("name", defaultValue(name));
        obj.put("Wmonth", month);
        obj.put("Fmonth", month);
        obj.put("Rmonth", month);
        return obj;
    }

    /**
     * 统计就诊次数
     *
     * @param query
     * @param orgIds 非空时，根据门诊分组统计
     * @param employeeIds 非空时，根据员工分组统计
     * @return
     */
    private Future<Map<String, StatEmpTreat>> multiFindClinicTreatVisitNum(DateRangeQueryForm query, List<Integer> orgIds, List<Integer> employeeIds) {
        return threadPool.submit(()->{
            Map<String, StatEmpTreat> result = new HashMap<>(16);
            List<StatEmpTreat> data = statEmpTreatBiz.findClinicTreatVisitNum(query, orgIds, employeeIds);
            if (StringHelper.isNotEmpty(data)) {
                if (StringHelper.isNotEmpty(orgIds)) {
                    data.forEach(vo -> result.put(vo.getOrgId() + "," + vo.getTreatDate(), vo));
                } else {
                    data.forEach(vo -> result.put(vo.getDentistId() + "," + vo.getTreatDate(), vo));
                }
            }
            return result;
        });
    }

    /**
     * 统计实收工作量
     *
     * @param query
     * @param orgIds 非空时，根据门诊分组统计
     * @param employeeIds 非空时，根据员工分组统计
     * @return
     */
    private Future<Map<String, BigDecimal>> multiFindClinicReceivedWorkload(final DateRangeQueryForm query, List<Integer> orgIds, List<Integer> employeeIds) {
        return threadPool.submit(()->{
            Map<String, BigDecimal> result = new HashMap<>(16);
            List<StatEmpPay> data = statEmpPayBiz.findClinicReceivedWorkload(query, orgIds, employeeIds);
            if (StringHelper.isNotEmpty(data)) {
                if (StringHelper.isNotEmpty(orgIds)) {
                    data.forEach(vo -> result.put(vo.getOrgId() + "," + vo.getPayDate(), vo.getReceivedWorkload()));
                } else {
                    data.forEach(vo -> result.put(vo.getDentistId() + "," + vo.getPayDate(), vo.getReceivedWorkload()));
                }
            }
            return result;
        });
    }

    /**
     * 根据条件导出门诊统计表
     *
     * @param query
     * @param response
     * @throws Exception
     */
    public void clinicWorkloadVisitStatisticsExport(MultiClinicDateRangeQueryForm query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = clinicWorkloadVisitStatistics(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        List<CellRangeAddress> crds = workloadVisitMergeRegiion(
                pageInfo,
                query,
                "门诊","工作量","初诊人数","就诊人数");
        excelUtil.setMergeRegion(crds);
        String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", "门诊统计表");
        excelUtil.exportExcel(response, result, "门诊统计表", fileName, pageInfo.getHeader(), pageInfo.getMap());
    }

    /**
     * 门诊统计表/医生统计表单元格合并
     * @param pageInfo
     * @param query
     * @param title
     * @return
     */
    private List<CellRangeAddress> workloadVisitMergeRegiion(DynamicHeaderPageInfo<JSONObject> pageInfo,
                                                             DateRangeQueryForm query, String... title) {
        List<CellRangeAddress> result = new ArrayList<>();
        int size = Integer.parseInt(query.getEndDate()) - Integer.parseInt(query.getStartDate()) + 2;
        List<JSONObject> list = pageInfo.getList();
        Map<String, String> map = pageInfo.getMap();
        String[] header = new String[size*3 + 1];
        // 工作量、初诊人数、复诊人数横向表头
        result.add(new CellRangeAddress(0,1,0,0));
        result.add(new CellRangeAddress(0,0,1,1 + size - 1));
        int index = 0;
        int colIndex = 0;
        header[index++] = title[colIndex++];
        header[index++] = title[colIndex++];
        for (int i = 1; i < header.length; i++,index++) {
            if (i % size == 0) {
                result.add(new CellRangeAddress(0, 0, i+1, i + size));
                if (index+1 < header.length) {
                    header[index] = title[colIndex++];
                }
            } else {
                header[index] = "";
            }
        }
        // 门诊/医生纵向表头
        if (StringHelper.isNotEmpty(list)) {
            for (int i = 2; i < list.size(); ++i) {
                result.add(new CellRangeAddress(i, i+=12, 0, 0));
            }
        }
        JSONObject obj = new JSONObject();
        map.forEach((key, value)->obj.put(key, value));
        list.add(0, obj);
        pageInfo.setHeader(header);
        return result;
    }

    /**
     * 根据条件查询医生统计表
     *
     * @param query
     * @return
     * @throws Exception
     */
    public DynamicHeaderPageInfo<JSONObject> dentistWorkloadVisitStatistics(EmployeeWorkStatusQueryForm query) throws Exception {
        Byte dateType = query.getDateType();
        if (dateType.intValue() != 2) {
            throw new ClientServiceException("请选择年份！",PARAMETERS_IS_ILLEGAL);
        }
        // 门诊员工信息
        ClinicEmployeeWorkloadQuery queryForm = new ClinicEmployeeWorkloadQuery();
        queryForm.setWorkStatus(query.getWorkStatus());
        queryForm.setEmployeeIds(query.getEmployeeIds());
        queryForm.setWhetherPage(query.getWhetherPage());
        queryForm.setPageNum(query.getPageNum());
        queryForm.setPageSize(query.getPageSize());
        List<ClinicEmployeBonusCoefficientVO> employees = employeeWorkloadBiz.findClinicEmployeeCartesianProduct(queryForm, false);
        List<Integer> employeeIds = employees.stream().map(ClinicEmployeeReportVO::getEmployeeId).collect(Collectors.toList());
        Future<Map<String, BigDecimal>> workloadFuture = multiFindClinicReceivedWorkload(query, null, employeeIds);
        Future<Map<String, StatEmpTreat>> treatNumFuture = multiFindClinicTreatVisitNum(query, null, employeeIds);
        return mergeDentistWorkloadVisitStatistice(query, employees, workloadFuture.get(), treatNumFuture.get());
    }

    /**
     * 医生统计表数据合并
     *
     * @param query
     * @param employees
     * @param workloadMap
     * @param treatNumMap
     * @return
     */
    private DynamicHeaderPageInfo<JSONObject> mergeDentistWorkloadVisitStatistice(DateRangeQueryForm query,
              List<ClinicEmployeBonusCoefficientVO> employees, Map<String, BigDecimal> workloadMap, Map<String, StatEmpTreat> treatNumMap) {
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>(employees);
        if (StringHelper.isNotEmpty(employees)) {
            String startDate = query.getStartDate();
            String endDate = query.getEndDate();
            List<String> years = DateUtil.sliceUpDateRange(startDate, endDate);
            int size = Integer.parseInt(endDate) - Integer.parseInt(startDate) + 1;
            List<JSONObject> list = new ArrayList<>();
            Map<String, String> title = new LinkedHashMap<>(16);
            BigDecimal[][] total = new BigDecimal[13][years.size() * 3];
            employees.forEach(vo -> putObject(vo.getEmployeeId(), vo.getEmployeeName(), size, years, workloadMap, treatNumMap, title, list, total));
            putTotalObj(total, years, list);
            pageInfo.setMap(title);
            pageInfo.setList(list);
            pageInfo.setTotal(pageInfo.getTotal());
            pageInfo.setPageNum(query.getPageNum());
            pageInfo.setPageSize(query.getPageSize());
        }
        return pageInfo;
    }

    /**
     * 根据条件导出医生统计表
     *
     * @param query
     * @param response
     * @throws Exception
     */
    public void dentistWorkloadVisitStatisticsExport(EmployeeWorkStatusQueryForm query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = dentistWorkloadVisitStatistics(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        List<CellRangeAddress> crds = workloadVisitMergeRegiion(
                pageInfo,
                query,
                "医生","工作量","初诊人数","就诊人数");
        excelUtil.setMergeRegion(crds);
        String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", "医生统计表");
        excelUtil.exportExcel(response, result, "医生统计表", fileName, pageInfo.getHeader(), pageInfo.getMap());
    }

    /**
     * 根据条件导出每日业绩汇总表
     *
     * @param query
     * @param response
     */
    public void clinicAchievementStatisticsExport(MultiClinicDateRangeQueryForm query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<ClinicAchievementVO> pageInfo = clinicAchievementStatistics(query);
        List<ClinicAchievementVO> result = pageInfo.getList();
        ExcelUtil<ClinicAchievementVO> excelUtil = new ExcelUtil(ClinicAchievementVO.class);
        List<CellRangeAddress> crds = clinicAchievementMergeRegiion(pageInfo.getContextMap());
        excelUtil.setMergeRegion(crds);
        String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", "每日业绩汇总表");
        excelUtil.exportExcel(response, result, "每日业绩汇总表", fileName);
    }

    /**
     * 院区业绩汇总表单元格合并
     *
     * @param contextMap
     * @return
     */
    private List<CellRangeAddress> clinicAchievementMergeRegiion(Map<String, List<String>> contextMap) {
        List<CellRangeAddress> result = new ArrayList<>();
        contextMap.remove("合计");
        // 院区列纵向合并
        int index = 1;
        for (Map.Entry<String, List<String>> entry : contextMap.entrySet()) {
            List<String> list = entry.getValue();
            result.add(new CellRangeAddress(index, index += list.size() - 1 , 0, 0));
            index++;
        }
        // 合计行横向合并
        result.add(new CellRangeAddress(index,index,0,1));
        return result;
    }

    /**
     * 根据条件查询每日业绩汇总表
     *
     * @param query
     * @return
     * @throws Exception
     */
    public DynamicHeaderPageInfo<ClinicAchievementVO> clinicAchievementStatistics(MultiClinicDateRangeQueryForm query) throws Exception {
        // 院区门诊
        Future<List<BaseOrganizationVO>> orgFuture = multiFindOrganizationWithParent(query);
        // 目标值
        Future<Map<Integer, BigDecimal>> goalFuture = multiFindBusinessGoal(query);
        // 实际值
        Future<Map<String, BigDecimal>> workloadFuture = multiFindClinicReceivedWorkload(query, query.getOrgIds(), null);
        MultiClinicDateRangeQueryForm todayQuery = new MultiClinicDateRangeQueryForm();
        BeanUtils.copyProperties(query,todayQuery);
        String today = new DateTime().toDateTime().toString("yyyy-MM-dd");
        todayQuery.setStartDate(today);
        todayQuery.setEndDate(today);
        // 今日完成
        Future<Map<String, BigDecimal>> todayFuture = multiFindClinicReceivedWorkload(todayQuery, query.getOrgIds(), null);
        MultiClinicDateRangeQueryForm chainQuery = new MultiClinicDateRangeQueryForm();
        BeanUtils.copyProperties(query,chainQuery);
        chainQuery.setStartDate(DateUtil.chainDate(chainQuery.getStartDate()));
        chainQuery.setEndDate(DateUtil.chainDate(chainQuery.getEndDate()));
        // 同比
        Future<Map<String, BigDecimal>> preYearFuture = multiFindClinicReceivedWorkload(chainQuery, query.getOrgIds(), null);
        return mergeClinicAchievementStatistics(goalFuture.get(), orgFuture.get(), workloadFuture.get(), todayFuture.get(), preYearFuture.get());
    }

    /**
     * 每日业绩汇总表数据合并
     *
     * @param goalMap
     * @param orgs
     * @param workloadMap
     * @param todayWorkloadMap
     * @param preYearWorkloadMap
     * @return
     */
    private DynamicHeaderPageInfo<ClinicAchievementVO> mergeClinicAchievementStatistics(
            Map<Integer, BigDecimal> goalMap, List<BaseOrganizationVO> orgs,
            Map<String, BigDecimal> workloadMap, Map<String, BigDecimal> todayWorkloadMap,
            Map<String, BigDecimal> preYearWorkloadMap) {
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo(orgs);
        List<ClinicAchievementVO> result = new ArrayList<>();
        Map<String, List<String>> map = new LinkedHashMap<>(16);
        Map<Integer, BigDecimal> orgWorkloadMap = statisticOrgWorkload(workloadMap);
        Map<Integer, BigDecimal> orgTodayMap = statisticOrgWorkload(todayWorkloadMap);
        Map<Integer, BigDecimal> orgPreYearMap = statisticOrgWorkload(preYearWorkloadMap);
        String pName = orgs.get(0).getParentName();
        BigDecimal[] total = {
                new BigDecimal("0.00"), // 实际值
                new BigDecimal("0.00"), // 目标值
                new BigDecimal("0.00"), // 完成度
                new BigDecimal("0.00"), // 今日完成
                new BigDecimal("0.00")};// 同比
        BigDecimal[] campusStat = {
                new BigDecimal("0.00"),
                new BigDecimal("0.00"),
                new BigDecimal("0.00"),
                new BigDecimal("0.00"),
                new BigDecimal("0.00")};
        for (BaseOrganizationVO org : orgs) {
            Integer parentId = org.getParentId();
            String parentName = org.getParentName();
            if (!parentName.equals(pName)) {// 统计院区各项指标
                result.add(initAchievementVO(parentName, campusStat));
                // 更新
                pName = parentName;
                campusStat = new BigDecimal[]{
                        new BigDecimal("0.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("0.00")};
            }
            Integer orgId = org.getOrgId();
            String abbreviation = org.getAbbreviation();
            List<String> list = map.get(parentName);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(abbreviation);
            map.put(parentName, list);

            BigDecimal actualWorkload = defaultValue(orgWorkloadMap.get(orgId));
            BigDecimal goalWorkload = defaultValue(goalMap.get(orgId));
            BigDecimal completed = new BigDecimal("0.00");
            if (goalWorkload.compareTo(BigDecimal.ZERO) != 0) {
                completed = actualWorkload
                        .divide(goalWorkload, 2, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                campusStat[2] = campusStat[2].add(completed);
                total[2] = total[2].add(completed);
            }
            BigDecimal todayCompleted = defaultValue(orgTodayMap.get(orgId));
            BigDecimal preYear = defaultValue(orgPreYearMap.get(orgId));
            ClinicAchievementVO vo = initAchievementVO(orgId, abbreviation, parentName,
                    actualWorkload, goalWorkload, completed, todayCompleted, preYear);
            result.add(vo);

            campusStat[0] = campusStat[0].add(actualWorkload);
            campusStat[1] = campusStat[1].add(goalWorkload);
            campusStat[3] = campusStat[3].add(todayCompleted);
            campusStat[4] = campusStat[4].add(preYear);
            total[0] = total[0].add(actualWorkload);
            total[1] = total[1].add(goalWorkload);
            total[3] = total[3].add(todayCompleted);
            total[4] = total[4].add(preYear);
        }
        result.add(initAchievementVO(pName, campusStat));
        map.forEach((parentName, list)->{
            list.add("合计");
        });
        result.add(initAchievementVO("合计", total));
        map.put("合计", Arrays.asList("合计"));
        pageInfo.setList(result);
        pageInfo.setContextMap(map);
        return pageInfo;
    }

    private ClinicAchievementVO initAchievementVO(String parentName, BigDecimal...total) {
        return initAchievementVO(null,"合计",parentName, total);
    }

    /**
     * 生成每日业绩实体
     * @param orgId
     * @param abbreviation
     * @param parentName
     * @param total
     * @return
     */
    private ClinicAchievementVO initAchievementVO(Integer orgId, String abbreviation, String parentName, BigDecimal...total) {
        ClinicAchievementVO vo = new ClinicAchievementVO();
        vo.setOrgId(orgId);
        vo.setCampus(parentName);
        vo.setAbbreviation(abbreviation);
        vo.setActualWorkload(total[0]);
        vo.setBusinessGoal(total[1]);
        vo.setCompletedPer(total[2]+"%");
        vo.setTodayCompleted(total[3]);
        vo.setCmpPreYear(total[4]);
        return vo;
    }

    /**
     * 统计门诊的实收工作量
     *
     * @param workloadMap
     * @return
     */
    private Map<Integer, BigDecimal> statisticOrgWorkload(Map<String, BigDecimal> workloadMap) {
        Map<Integer, BigDecimal> result = new HashMap<>(16);
        if (StringHelper.isNotEmpty(workloadMap)) {
            workloadMap.forEach((key, workload)->{
                String[] keys = StringHelper.split(key, ",");
                Integer orgId = Integer.parseInt(keys[0]);
                BigDecimal totalWorkload = result.get(orgId);
                if (totalWorkload == null) {
                    totalWorkload = new BigDecimal("0.00");
                }
                result.put(orgId, totalWorkload.add(workload));
            });
        }
        return result;
    }

    /**
     * 统计门诊的初诊、复诊人数
     *
     * @param treatNumMap
     * @return
     */
    private Map<Integer, StatEmpTreat> statisticOrgStatEmpTreat(Map<String, StatEmpTreat> treatNumMap) {
        Map<Integer, StatEmpTreat> result = new HashMap<>(16);
        if (StringHelper.isNotEmpty(treatNumMap)) {
            treatNumMap.forEach((key, vo)->{
                String[] keys = StringHelper.split(key, ",");
                Integer orgId = Integer.parseInt(keys[0]);
                StatEmpTreat statEmpTreat = result.get(orgId);
                if (statEmpTreat == null) {
                    result.put(orgId, vo);
                } else {
                    statEmpTreat.setFirstVisitCount(statEmpTreat.getFirstVisitCount() + vo.getFirstVisitCount());
                    statEmpTreat.setReVisitCount(statEmpTreat.getReVisitCount() + vo.getReVisitCount());
                    result.put(orgId, statEmpTreat);
                }
            });
        }
        return result;
    }

    /**
     * 查询门诊及其院区列表
     *
     * @param query
     * @return
     */
    private Future<List<BaseOrganizationVO>> multiFindOrganizationWithParent(MultiClinicDateRangeQueryForm query) {
        return threadPool.submit(()->{
            ClinicPerformanceBusinessQuery clinicQuery = new ClinicPerformanceBusinessQuery();
            clinicQuery.setWhetherPage(query.getWhetherPage());
            clinicQuery.setPageNum(query.getPageNum());
            clinicQuery.setPageSize(query.getPageSize());
            clinicQuery.setOrgIds(query.getOrgIds());
            return baseOrganizationBiz.getOrganizationWithParent(clinicQuery);
        });
    }

    /**
     * 统计门诊工作量目标
     *
     * @param query
     * @return
     */
    private Future<Map<Integer, BigDecimal>> multiFindBusinessGoal(MultiClinicDateRangeQueryForm query) {
        return threadPool.submit(()-> baseBillDetailBiz.workloadMonthGoal(query.getDateType(),
                query.getStartDate(), query.getEndDate()));
    }

    /**
     * 根据条件查询专科数量同比
     *
     * @param query
     * @return
     * @throws Exception
     */
    public DynamicHeaderPageInfo<JSONObject> clinicSpecialProjectNumCompare(DoubleDateRangeQueryForm query) throws Exception {
        checkSpecialNumQuery(query);
        // 门诊
        ClinicPerformanceBusinessQuery queryFrom = new ClinicPerformanceBusinessQuery();
        List<BaseOrganization> orgs = baseOrganizationBiz.getOrganization(queryFrom);
        List<Integer> orgIds = orgs.stream().map(BaseOrganization::getOrgId).collect(toList());
        // 专科项目
        Future<List<SpecialistProjectVO>> specialFuture = multiFindSpecialProjectList();
        // 第一个日期的专科项目数量
        MultiClinicDateRangeQueryForm queryForm1 = doubleDateRange2SingleDateRangeQuery(query, orgIds, (doubleQuery, queryForm)->{
            queryForm.setStartDate(doubleQuery.getStartDate1());
            queryForm.setEndDate(doubleQuery.getEndDate1());
            return null;
        });
        Future<List<StatEmpBill>> itemFuture = multiFindClinicBillItemNum(queryForm1);
        // 对比日期的专科项目数量
        MultiClinicDateRangeQueryForm queryForm2 = doubleDateRange2SingleDateRangeQuery(query, orgIds, (doubleQuery, queryForm)->{
            queryForm.setStartDate(doubleQuery.getStartDate2());
            queryForm.setEndDate(doubleQuery.getEndDate2());
            return null;
        });
        Future<List<StatEmpBill>> itemFuture2 = multiFindClinicBillItemNum(queryForm2);
        return mergeClinicSpecialProjectNumCompare(query, orgs, specialFuture.get(), itemFuture.get(), itemFuture2.get());
    }

    private MultiClinicDateRangeQueryForm doubleDateRange2SingleDateRangeQuery(DoubleDateRangeQueryForm query, List<Integer> orgIds,
                   BiFunction<DoubleDateRangeQueryForm, MultiClinicDateRangeQueryForm, Integer> func) {
        MultiClinicDateRangeQueryForm queryForm = new MultiClinicDateRangeQueryForm();
        queryForm.setOrgIds(orgIds);
        queryForm.setDateType(query.getDateType());
        queryForm.setIsAsc(query.getIsAsc());
        queryForm.setOrderByColumn(query.getOrderByColumn());
        queryForm.setPageNum(query.getPageNum());
        queryForm.setPageSize(query.getPageSize());
        queryForm.setWhetherPage(query.getWhetherPage());
        func.apply(query,queryForm);
        return queryForm;
    }

    private void checkSpecialNumQuery(DoubleDateRangeQueryForm query) {
        if (query.getStartDate1().equals(query.getStartDate2()) && query.getEndDate1().equals(query.getEndDate2())) {
            throw new ClientServiceException("请勿选择相同日期时段！", PARAMETERS_IS_ILLEGAL);
        }
    }

    /**
     * 专科数量同比的数据合并
     *
     * @param query
     * @param orgs
     * @param specials
     * @param statEmpBills
     * @param cmpStatEmpBills
     * @return
     */
    private DynamicHeaderPageInfo<JSONObject> mergeClinicSpecialProjectNumCompare(DoubleDateRangeQueryForm query, List<BaseOrganization> orgs,
                                                                                  List<SpecialistProjectVO> specials, List<StatEmpBill> statEmpBills, List<StatEmpBill> cmpStatEmpBills) {
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>(orgs);
        List<JSONObject> list = new ArrayList<>();
        Map<String, String> specialMap = new LinkedHashMap<>(16);
        Map<String, List<String>> contextMap = new LinkedHashMap<>(16);
        Map<String, String> title = new LinkedHashMap<>(16);
        Map<String, Integer> specialNumMap = new HashMap<>(16);
        if (StringHelper.isNotEmpty(specials)) {
            Map<String, List<Integer>> specialItemMap = item2SpecialNumMap(specials, specialMap);
            Map<String, Integer> itemDataMap1 = sumBillItemNum(statEmpBills, specialItemMap);
            Map<String, Integer> itemDataMap2 = sumBillItemNum(cmpStatEmpBills, specialItemMap);
            orgs.forEach(org->{
                Integer orgId = org.getOrgId();
                JSONObject obj = new JSONObject();
                obj.put("abbreviation", defaultValue(org.getAbbreviation()));
                title.put("abbreviation", "门诊");
                specialMap.forEach((specialId, name)->{
                    String key = orgId + "," + specialId;
                    int itemNum1 = defaultValue(itemDataMap1.get(key));
                    int itemNum2 = defaultValue(itemDataMap2.get(key));
                    BigDecimal cmpNum = computePercentage(itemNum1, itemNum2);
                    String key1 = "date"+specialId;
                    String key2 = "cmpDate"+specialId;
                    String key3 = "cmpNum"+specialId;
                    int num1 = defaultValue(specialNumMap.get(key1));
                    specialNumMap.put(key1, num1 + itemNum1);
                    int num2 = defaultValue(specialNumMap.get(key2));
                    specialNumMap.put(key2, num2 + itemNum2);
                    obj.put(key1, itemNum1);
                    obj.put(key2, itemNum2);
                    obj.put(key3, cmpNum + "%");
                    title.put(key1, baseBillDetailBiz.doDateStyle(query.getStartDate1(), query.getEndDate1()));
                    title.put(key2, baseBillDetailBiz.doDateStyle(query.getStartDate2(), query.getEndDate2()));
                    title.put(key3, "同比");
                    contextMap.put(name, Arrays.asList(key1, key2, key3));
                });
                list.add(obj);
            });
            JSONObject totalObj = new JSONObject();
            totalObj.put("abbreviation", "合计");
            specialMap.forEach((specialId, name)->{
                int num1 = defaultValue(specialNumMap.get("date"+specialId));
                int num2 = defaultValue(specialNumMap.get("cmpDate"+specialId));
                totalObj.put("date"+specialId, num1);
                totalObj.put("cmpDate"+specialId, num2);
                totalObj.put("cmpNum"+specialId,  computePercentage(num1, num2) + "%");
            });
            list.add(totalObj);
        }
        pageInfo.setMap(title);
        pageInfo.setContextMap(contextMap);
        pageInfo.setList(list);
        return pageInfo;
    }

    /**
     * 返回价目表、商品表与专科项目id的关联map
     *
     * @param specials
     * @param specialMap
     * @return
     */
    private Map<String, List<Integer>> item2SpecialNumMap(List<SpecialistProjectVO> specials, Map<String, String> specialMap) {
        Map<String, List<Integer>> result = new HashMap<>(16);
        if (StringHelper.isNotEmpty(specials)) {
            specials.forEach(vo -> {
                Integer specialId = vo.getId();
                specialMap.put(specialId + "", vo.getSpecialistProjectName());
                putItem2Special(vo.getTariffItemIds(), "0", specialId, result);
                putItem2Special(vo.getOralIds(), "1", specialId, result);
            });
        }
        return result;
    }

    /**
     * 计算百分比
     *
     * @param num1
     * @param num2
     * @return
     */
    private BigDecimal computePercentage(BigDecimal num1, BigDecimal num2) {
        if (num1==null || num2==null || num2.compareTo(BigDecimal.ZERO)==0) {
            return new BigDecimal("0.00");
        }
        return num1.divide(num2,2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
    }


    private BigDecimal computePercentage(Integer num1, Integer num2) {
        if (num1==null || num2==null || num2==0) {
            return new BigDecimal("0.00");
        }
        return new BigDecimal(num1)
                .divide(new BigDecimal(num2),2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
    }

    /**
     * 根据门诊和专科项目统计数量
     *
     * @param statEmpBills
     * @param specialItemMap
     * @return
     */
    private Map<String, Integer> sumBillItemNum(List<StatEmpBill> statEmpBills, Map<String, List<Integer>> specialItemMap) {
        Map<String, Integer> result = new HashMap<>(16);
        statEmpBills.forEach(vo -> {
            List<Integer> specialIds = specialItemMap.get(vo.getItemType() + "," + vo.getItemId());
            specialIds.forEach(specialId->{
                String key = vo.getOrgId() + "," + specialId;
                Integer itemNum = result.get(key);
                if (itemNum == null) {
                    itemNum = 0;
                }
                result.put(key, itemNum + vo.getQuantity());
            });
        });
        return result;
    }

    /**
     * 查询专科项目列表
     * @return
     */
    private Future<List<SpecialistProjectVO>> multiFindSpecialProjectList() {
        return threadPool.submit(()->{
            SpecialistProjectQuery queryForm = new SpecialistProjectQuery();
            queryForm.setWhetherPage(false);
            return clinicBaseServiceFeign.specialProjectList(queryForm).getList();
        });
    }

    /**
     * 查询账单时项目数量
     *
     * @param query
     * @return
     */
    private Future<List<StatEmpBill>> multiFindClinicBillItemNum(MultiClinicDateRangeQueryForm query) {
        return threadPool.submit(()-> statEmpBillBiz.findBillItemNum(query));
    }

    /**
     * 根据条件导出专科数量同比
     *
     * @param query
     * @param response
     * @throws Exception
     */
    public void clinicSpecialProjectNumCompareExport(DoubleDateRangeQueryForm query, HttpServletResponse response) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = clinicSpecialProjectNumCompare(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        excelUtil.setMergeRegion(specialProjectNumCmpMergeRegiion(pageInfo));
        String fileName = excelUtil.getFileName(query.getStartDate1()+"", query.getStartDate2()+"", "", "专科数量同比");
        excelUtil.exportExcel(response, result, "专科数量同比", fileName, pageInfo.getHeader(), pageInfo.getMap());
    }

    /**
     * 专科数量同比单元格合并
     *
     * @param pageInfo
     * @return
     */
    private List<CellRangeAddress> specialProjectNumCmpMergeRegiion(DynamicHeaderPageInfo<JSONObject> pageInfo) {
        List<CellRangeAddress> result = new ArrayList<>();
        List<JSONObject> list = pageInfo.getList();
        Map<String, String> title = pageInfo.getMap();
        JSONObject obj = new JSONObject();
        title.forEach((id, name)->obj.put(id, name));
        list.add(0, obj);
        // 专科项目行横向合并
        Map<String, List<String>> contextMap = pageInfo.getContextMap();
        int size = contextMap.size();
        String[] header = new String[size*3+1];
        int i = 0;
        int n = 0;
        for (Map.Entry<String, List<String>> entry : contextMap.entrySet()) {
            if (i == 0) {
                result.add(new CellRangeAddress(0, 0, i, i+3));
                header[n++] = entry.getKey();
                header[n++] = "";
                header[n++] = "";
                header[n++] = "";
            } else {
                result.add(new CellRangeAddress(0, 0, i+1, i+3));
                header[n++] = entry.getKey();
                header[n++] = "";
                header[n++] = "";
            }
            i += 3;
        }
        pageInfo.setHeader(header);
        return result;
    }

    /**
     * 根据条件查询院区业绩统计表
     *
     * @param query
     * @return
     * @throws Exception
     */
    public DynamicHeaderPageInfo<JSONObject> campusAchievementStatistics(MultiClinicDateRangeQueryForm query) throws Exception {
        // 院区门诊
        Future<List<BaseOrganizationVO>> orgFuture = multiFindOrganizationWithParent(query);
        // 工作量
        Future<Map<String, BigDecimal>> workloadFuture = multiFindClinicReceivedWorkload(query, query.getOrgIds(), null);
        // 初诊人数、复诊人数
        Future<Map<String, StatEmpTreat>> treatNumFuture = multiFindClinicTreatVisitNum(query, query.getOrgIds(), null);
        // 项目数量
        Future<List<StatEmpBill>> itemNumFuture = multiFindClinicBillItemNum(query);
        // 专科项目
        Future<List<SpecialistProjectVO>> specialFuture = multiFindSpecialProjectList();
        return mergeCampusAchievementStatistics(orgFuture.get(), workloadFuture.get(), treatNumFuture.get(), itemNumFuture.get(), specialFuture.get());
    }

    /**
     * 院区业绩统计表数据合并
     *
     * @param orgs
     * @param workloadMap
     * @param treatNumMap
     * @param statEmpBills
     * @param specials
     * @return
     */
    private DynamicHeaderPageInfo<JSONObject> mergeCampusAchievementStatistics(List<BaseOrganizationVO> orgs,
            Map<String, BigDecimal> workloadMap, Map<String, StatEmpTreat> treatNumMap,
           List<StatEmpBill> statEmpBills, List<SpecialistProjectVO> specials) {
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>(orgs);
        Map<String, String> specialMap = new LinkedHashMap<>(16);
        Map<String, String> title = new LinkedHashMap<>(16);
        Map<Integer, BigDecimal> orgWorkloadMap = statisticOrgWorkload(workloadMap);
        Map<Integer, StatEmpTreat> orgTreatNumMap = statisticOrgStatEmpTreat(treatNumMap);
        Map<String, List<Integer>> specialItemMap = item2SpecialNumMap(specials, specialMap);
        Map<String, Integer> itemNumMap = sumBillItemNum(statEmpBills, specialItemMap);
        Map<Integer, JSONObject> campus = new LinkedHashMap<>(16);
        JSONObject totalObj = initCampusAchievement("合计");
        orgs.forEach(org->{
            Integer parentId = org.getParentId();
            JSONObject obj = campus.get(parentId);
            if (obj == null) {
                obj = initCampusAchievement(org.getParentName());
            }
            Integer orgId = org.getOrgId();
            BigDecimal workload = defaultValue(orgWorkloadMap.get(orgId));
            obj.put("workload", obj.getDoubleValue("workload") + workload.doubleValue());
            totalObj.put("workload", totalObj.getDoubleValue("workload") + workload.doubleValue());
            StatEmpTreat statEmpTreat = orgTreatNumMap.get(orgId);
            int firstVisitCount = 0;
            int treatVisitCount = 0;
            if (statEmpTreat != null) {
                firstVisitCount = statEmpTreat.getFirstVisitCount();
                treatVisitCount = firstVisitCount + statEmpTreat.getReVisitCount();
            }
            obj.put("firstVisitCount", obj.getIntValue("firstVisitCount") + firstVisitCount);
            obj.put("treatVisitCount", obj.getIntValue("treatVisitCount") + treatVisitCount);
            totalObj.put("firstVisitCount", totalObj.getIntValue("firstVisitCount") + firstVisitCount);
            totalObj.put("treatVisitCount", totalObj.getIntValue("treatVisitCount") + treatVisitCount);
            for (Map.Entry<String, String> entry : specialMap.entrySet()) {
                String specialId = entry.getKey();
                int specialNum = defaultValue(itemNumMap.get(orgId + "," + specialId));
                obj.put(specialId, defaultValue(obj.getIntValue(specialId)) + specialNum);
                totalObj.put(specialId, defaultValue(totalObj.getIntValue(specialId)) + specialNum);
            }
            campus.put(parentId, obj);
        });
        campus.put(-1, totalObj);
        title.put("campusName", "院区");
        title.put("workload", "工作量");
        title.put("firstVisitCount", "初诊人数");
        title.put("treatVisitCount", "就诊人数");
        title.putAll(specialMap);
        pageInfo.setList(new ArrayList<>(campus.values()));
        pageInfo.setMap(title);
        return pageInfo;
    }

    /**
     * 生成院区业绩统计表的行数据
     *
     * @param name
     * @return
     */
    private JSONObject initCampusAchievement(String name) {
        JSONObject obj = new JSONObject();
        obj.put("campusName", defaultValue(name));
        obj.put("workload", new BigDecimal("0.00"));
        obj.put("firstVisitCount", 0);
        obj.put("treatVisitCount", 0);
        return obj;
    }

    /**
     * 根据条件导出院区业绩汇总表
     *
     * @param query
     * @param response
     * @throws Exception
     */
    public void campusAchievementStatisticsExport(MultiClinicDateRangeQueryForm query, HttpServletResponse response) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = campusAchievementStatistics(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", "院区业绩汇总表");
        excelUtil.exportExcel(response, result, "院区业绩汇总表", fileName, pageInfo.getMap());
    }

    /**
     * 根据条件查询院区业绩同比
     *
     * @param query
     * @return
     * @throws Exception
     */
    public PageInfo<CampusAchievementCompareVO> campusAchievementCompare(MultiClinicDateRangeQueryForm query) throws Exception {
        // 院区门诊
        Future<List<BaseOrganizationVO>> orgFuture = multiFindOrganizationWithParent(query);
        // 工作量
        Future<Map<String, BigDecimal>> workloadFuture = multiFindClinicReceivedWorkload(query, query.getOrgIds(), null);
        // 初诊人数、复诊人数
        Future<Map<String, StatEmpTreat>> treatNumFuture = multiFindClinicTreatVisitNum(query, query.getOrgIds(), null);
        return mergetCampusAchievementCompare(orgFuture.get(), workloadFuture.get(), treatNumFuture.get());
    }

    private PageInfo<CampusAchievementCompareVO> mergetCampusAchievementCompare(List<BaseOrganizationVO> orgs,
            Map<String, BigDecimal> workloadMap, Map<String, StatEmpTreat> treatNumMap) {
        PageInfo<CampusAchievementCompareVO> pageInfo = new PageInfo<>();
        Map<Integer, BigDecimal> orgWorkloadMap = statisticOrgWorkload(workloadMap);
        Map<Integer, StatEmpTreat> orgTreatNumMap = statisticOrgStatEmpTreat(treatNumMap);
        Map<Integer, CampusAchievementCompareVO> campus = new LinkedHashMap<>(16);
        CampusAchievementCompareVO totalObj = new CampusAchievementCompareVO();
        totalObj.setCampusName("合计");
        BigDecimal totalWorkload = new BigDecimal("0.00");
        int totalFirstVisit = 0;
        int totalTreatVisit = 0;
        for (BaseOrganizationVO org : orgs) {
            Integer parentId = org.getParentId();
            CampusAchievementCompareVO obj = campus.get(parentId);
            if (obj == null) {
                obj = new CampusAchievementCompareVO();
                obj.setCampusName(org.getParentName());
            }
            Integer orgId = org.getOrgId();
            BigDecimal workload = defaultValue(orgWorkloadMap.get(orgId));
            obj.setWorkload(obj.getWorkload().add(workload));
            totalWorkload = totalWorkload.add(workload);
            StatEmpTreat statEmpTreat = orgTreatNumMap.get(orgId);
            int firstVisitCount = 0;
            int treatVisitCount = 0;
            if (statEmpTreat != null) {
                firstVisitCount = statEmpTreat.getFirstVisitCount();
                treatVisitCount = firstVisitCount + statEmpTreat.getReVisitCount();
            }
            obj.setFirstVisitCount(obj.getFirstVisitCount() + firstVisitCount);
            obj.setTreatVisitCount(obj.getTreatVisitCount() + treatVisitCount);
            totalFirstVisit += firstVisitCount;
            totalTreatVisit += treatVisitCount;
            campus.put(parentId, obj);
        }
        totalObj.setWorkload(totalWorkload);
        totalObj.setWorkloadRatio(totalWorkload.compareTo(BigDecimal.ZERO)!=0?"100.00%":"0.00%");
        totalObj.setFirstVisitCount(totalFirstVisit);
        totalObj.setFirstVisitCountRatio(totalFirstVisit!=0?"100.00%":"0.00%");
        totalObj.setTreatVisitCount(totalTreatVisit);
        totalObj.setTreatVisitCountRatio(totalTreatVisit!=0?"100.00%":"0.00%");
        List<CampusAchievementCompareVO> list = new ArrayList<>();
        for (Map.Entry<Integer, CampusAchievementCompareVO> entry : campus.entrySet()) {
            CampusAchievementCompareVO vo = entry.getValue();
            vo.setWorkloadRatio(computePercentage(vo.getWorkload(),totalWorkload)+"%");
            vo.setFirstVisitCountRatio(computePercentage(vo.getFirstVisitCount(),totalFirstVisit)+"%");
            vo.setTreatVisitCountRatio(computePercentage(vo.getTreatVisitCount(),totalTreatVisit)+"%");
            list.add(vo);
        }
        list.add(totalObj);
        campus.put(-1, totalObj);
        pageInfo.setList(list);
        return pageInfo;
    }

    /**
     * 根据条件导出院区业绩同比
     *
     * @param query
     * @param response
     * @throws Exception
     */
    public void campusAchievementCompareExport(MultiClinicDateRangeQueryForm query, HttpServletResponse response) throws Exception {
        PageInfo<CampusAchievementCompareVO> pageInfo = campusAchievementCompare(query);
        List<CampusAchievementCompareVO> result = pageInfo.getList();
        ExcelUtil<CampusAchievementCompareVO> excelUtil = new ExcelUtil(CampusAchievementCompareVO.class);
        String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", "院区业绩同比");
        excelUtil.exportExcel(response, result, "院区业绩同比", fileName);
    }

    /**
     * 根据条件查询产品卡券使用统计
     *
     * @param query
     * @return
     */
    public DynamicHeaderPageInfo<JSONObject> cardCouponUsedStatistics(CardCouponUsedQueryForm query) throws Exception {
        // 门诊
        ClinicPerformanceBusinessQuery queryFrom = new ClinicPerformanceBusinessQuery();
        queryFrom.setOrgIds(query.getOrgIds());
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<BaseOrganization> orgs = baseOrganizationBiz.getOrganization(queryFrom);
        query.setOrgIds(orgs.stream().map(BaseOrganization::getOrgId).collect(toList()));
        // 产品
        List<BaseCoupon> coupons = baseCouponBiz.findBaseCouponListByCouponId(query.getCouponIds());
        query.setCouponIds(coupons.stream().map(BaseCoupon::getCouponId).collect(Collectors.toSet()));
        // 卡券
        Future<List<BaseCard>> cardFuture = multiFindCardCouponSoldStatistics(query);
        return mergeCardCouponUsedStatistics(orgs, coupons, cardFuture.get());
    }

    /**
     * 产品卡券使用统计数据合并
     *
     * @param orgs
     * @param coupons
     * @param cards
     * @return
     */
    private DynamicHeaderPageInfo<JSONObject> mergeCardCouponUsedStatistics(List<BaseOrganization> orgs, List<BaseCoupon> coupons, List<BaseCard> cards) {
        DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo(orgs);
        if (StringHelper.isNotEmpty(orgs)) {
            // 患者激活卡片次数
            Map<String, Set<LocalDateTime>> patientActiveDates = new HashMap<>(16);
            // 销售数量
            Map<String, Integer> soldNumMap = new HashMap<>(16);
            // 激活数量
            Map<String, Integer> activeNumMap = new HashMap<>(16);
            // 购买产品的患者数量
            Map<String, Set<Integer>> patients = new HashMap<>(16);
            Set<Integer> couponIds = statisticCouponCardNum(cards, patients, patientActiveDates, soldNumMap, activeNumMap);
            coupons = coupons.stream().filter(vo->couponIds.contains(vo.getCouponId())).collect(toList());
            int[] total = new int[coupons.size()*5];
            Map<String, Integer> repurchaseMap = patientRepurchaseMap(patientActiveDates);
            List<JSONObject> list = new ArrayList<>();
            for (BaseOrganization org : orgs) {
                JSONObject obj = new JSONObject();
                Integer orgId = org.getOrgId();
                obj.put("abbreviation", defaultValue(org.getAbbreviation()));
                obj.put("orgId", orgId);
                for (int i = 0; i < coupons.size(); i++) {
                    BaseCoupon coupon = coupons.get(i);
                    Integer couponId = coupon.getCouponId();
                    String key = orgId + "," + couponId;
                    int soldNum = defaultValue(soldNumMap.get(key));
                    obj.put("S-" + couponId, soldNum);
                    int activeNum = defaultValue(activeNumMap.get(key));
                    obj.put("A-" + couponId, activeNum);
                    int unActiveNum = soldNum - activeNum;
                    obj.put("U-" + couponId, unActiveNum);
                    int repurchaseNum = defaultValue(repurchaseMap.get(key));
                    obj.put("R-" + couponId, repurchaseNum);
                    // 激活率 = 激活数/销售数
                    obj.put("T-" + couponId, computePercentage(activeNum,soldNum)+"%");
                    // 复购率 = 复购数/购买产品的患者人数
                    int patientNum = 0;
                    Set<Integer> patientIds = patients.get(key);
                    if (StringHelper.isNotEmpty(patientIds)) {
                        patientNum = patientIds.size();
                    }
                    obj.put("V-" + couponId, computePercentage(repurchaseNum, patientNum)+"%");
                    total[i*5] += soldNum;
                    total[i*5+1] += activeNum;
                    total[i*5+2] += unActiveNum;
                    total[i*5+3] += repurchaseNum;
                    total[i*5+4] += patientNum;
                }
                list.add(obj);
            }
            list.add(totalCardCouponObj(total, coupons));
            pageInfo.setList(list);
            cardCouponUsedTitle(pageInfo, coupons);
        }
        return pageInfo;
    }

    /**
     * 统计产品的售出数、激活数、复购数
     *
     * @param cards
     * @param patients
     * @param patientActiveDates
     * @param soldNumMap
     * @param activeNumMap
     * @return
     */
    private Set<Integer> statisticCouponCardNum(List<BaseCard> cards, Map<String, Set<Integer>> patients, Map<String, Set<LocalDateTime>> patientActiveDates,
                                                             Map<String, Integer> soldNumMap, Map<String, Integer> activeNumMap) {
        Set<Integer> couponIds = new HashSet<>();
        cards.forEach(card->{
            Integer status = card.getStatus();
            Integer couponId = card.getCouponId();
            Integer activeOrgId = card.getActiveOrgId();
            Integer patientId = card.getPatientId();
            String key = activeOrgId + "," + couponId;
            incrementOne(key, soldNumMap);
            if (status > 1) {// 已激活
                String patientKey = key + "," + patientId;
                Set<LocalDateTime> dates = patientActiveDates.get(patientKey);
                if (dates == null) {
                    dates = new HashSet<>();
                }
                dates.add(card.getActiveDate());
                patientActiveDates.put(patientKey, dates);
                incrementOne(key, activeNumMap);
            }
            Set<Integer> patientIds = patients.get(key);
            if (patientIds == null) {
                patientIds = new HashSet<>();
            }
            patientIds.add(patientId);
            patients.put(key, patientIds);
            couponIds.add(couponId);
        });
        return couponIds;
    }

    /**
     * 产品卡券使用的标题行
     *
     * @param pageInfo
     * @param coupons
     */
    private void cardCouponUsedTitle(DynamicHeaderPageInfo pageInfo, List<BaseCoupon> coupons) {
        Map<String, List<String>> contextMap = new LinkedHashMap<>(16);
        Map<String, String> title = new LinkedHashMap<>(16);
        title.put("abbreviation","门诊");
        coupons.forEach(coupon->{
            Integer couponId = coupon.getCouponId();
            title.put("S-"+couponId, "销售");
            title.put("A-"+couponId, "激活");
            title.put("U-"+couponId, "未激活");
            title.put("R-"+couponId, "复购");
            title.put("T-"+couponId, "激活率");
            title.put("V-"+couponId, "复购率");
            contextMap.put(coupon.getCouponName(),
                    Arrays.asList("S-"+couponId, "A-"+couponId, "U-"+couponId, "R-"+couponId, "T-"+couponId, "V-"+couponId));
        });
        pageInfo.setMap(title);
        pageInfo.setContextMap(contextMap);
    }

    /**
     * 生成产品卡券使用统计的合计行
     *
     * @param total
     * @param coupons
     * @return
     */
    private JSONObject totalCardCouponObj(int[] total, List<BaseCoupon> coupons) {
        JSONObject totalObj = new JSONObject();
        totalObj.put("abbreviation", "合计");
        totalObj.put("orgId", "");
        for (int i = 0; i < coupons.size(); i++) {
            Integer couponId = coupons.get(i).getCouponId();
            int soldNum = total[i*5];
            int activeNum = total[i*5+1];
            int unActiveNum = total[i*5+2];
            int repurchaseNum = total[i*5+3];
            int patientNum = total[i*5+4];
            totalObj.put("S-"+couponId, soldNum);
            totalObj.put("A-"+couponId, activeNum);
            totalObj.put("U-"+couponId, unActiveNum);
            totalObj.put("R-"+couponId, repurchaseNum);
            totalObj.put("T-"+couponId, computePercentage(activeNum,soldNum)+"%");
            totalObj.put("V-"+couponId, computePercentage(repurchaseNum,patientNum)+"%");
        }
        return totalObj;
    }

    /**
     * 数量累加1
     *
     * @param key
     * @param map
     */
    private void incrementOne(String key, Map<String, Integer> map) {
        Integer num = map.get(key);
        if (num == null) {
            num = 0;
        }
        map.put(key, num + 1);
    }

    /**
     * 统计复购产品的患者人数
     *
     * @param patientActiveDates
     * @return
     */
    private Map<String, Integer> patientRepurchaseMap(Map<String, Set<LocalDateTime>> patientActiveDates) {
        Map<String, Integer> result = new HashMap<>(16);
        if (StringHelper.isNotEmpty(patientActiveDates)) {
            patientActiveDates.forEach((key, set)->{
                if (set.size() > 1) {
                    incrementOne(key.substring(0, key.lastIndexOf(",")),result);
                }
            });
        }
        return result;
    }

    private Future<List<BaseCard>> multiFindCardCouponSoldStatistics(CardCouponUsedQueryForm query) {
        return threadPool.submit(()-> baseCardBiz.findCardCouponSoldStatistics(query));
    }

    /**
     * 根据条件导出产品卡券使用统计
     *
     * @param query 查询条件
     * @return
     */
    public void cardCouponUsedStatisticsExport(CardCouponUsedQueryForm query, HttpServletResponse response) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = cardCouponUsedStatistics(query);
        List<JSONObject> result = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        excelUtil.setMergeRegion(cardCouponUsedMergeRegiion(pageInfo));
        String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", "产品卡券使用统计");
        excelUtil.exportExcel(response, result, "产品卡券使用统计", fileName, pageInfo.getHeader(), pageInfo.getMap());
    }

    /**
     * 产品卡券使用单元格合并
     *
     * @param pageInfo
     * @return
     */
    private List<CellRangeAddress> cardCouponUsedMergeRegiion(DynamicHeaderPageInfo<JSONObject> pageInfo) {
        List<CellRangeAddress> result = new ArrayList<>();
        Map<String, String> map = pageInfo.getMap();
        JSONObject title = new JSONObject();
        map.forEach((key, name)->title.put(key, name));
        List<JSONObject> list = pageInfo.getList();
        list.add(0, title);
        // 产品行横向合并
        Map<String, List<String>> contextMap = pageInfo.getContextMap();
        String[] header = new String[contextMap.size()*6+1];
        int index = 0;
        int colInx = 0;
        result.add(new CellRangeAddress(0,0,colInx,colInx+=6));
        for (Map.Entry<String, List<String>> entry : contextMap.entrySet()) {
            header[index++] = entry.getKey();
            if (index == 1) {
                header[index++] = "";
            } else {
                result.add(new CellRangeAddress(0,0,colInx,colInx+=5));
            }
            colInx++;
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i != 0) {
                    header[index++] = "";
                }
            }
        }
        pageInfo.setHeader(header);
        return result;
    }

    /**
     * 根据条件导出产品卡券使用统计-激活/复购明细
     *
     * @param query 查询条件
     * @return
     */
    public void cardCouponUsedStatisticsDetailExport(CardCouponUsedDetailQueryForm query, HttpServletResponse response) throws Exception {
        query.setWhetherPage(false);
        PageInfo<CardCouponUsedDetailVO> pageInfo = cardCouponUsedStatisticsDetail(query);
        List<CardCouponUsedDetailVO> result = pageInfo.getList();
        ExcelUtil<CardCouponUsedDetailVO> excelUtil = new ExcelUtil(CardCouponUsedDetailVO.class);
        String sheetName = "产品卡券使用统计";
        String fileName = excelUtil.getFileName(query.getStartDate()+"", query.getEndDate()+"", "", sheetName);
        excelUtil.exportExcel(response, result, sheetName, fileName);
    }

    /**
     * 根据条件查询产品卡券使用统计-激活/复购明细
     *
     * @param query 查询条件
     * @return
     */
    public PageInfo<CardCouponUsedDetailVO> cardCouponUsedStatisticsDetail(CardCouponUsedDetailQueryForm query) {
        List<CardCouponUsedDetailVO> list = baseCardBiz.findCardCouponUsedDetail(query);
        if (query.getDetailType() == 1) {
            list = repurchaseFilter(list);
        }
        return PageUtl.doPage(query.getPageNum(), query.getPageSize(), list);
    }

    /**
     * 复购过滤（只保留该产品每人第二次购买的）
     *
     * @param list
     * @return
     */
    private List<CardCouponUsedDetailVO> repurchaseFilter(List<CardCouponUsedDetailVO> list) {
        if (StringHelper.isEmpty(list)) {
            return list;
        }
        List<CardCouponUsedDetailVO> result = new ArrayList<>();
        Map<Integer, Boolean> map = new LinkedHashMap<>(16);
        list.forEach(vo->{
            Integer patientId = vo.getPatientId();
            Boolean hasSecond = map.get(patientId);
            if (hasSecond == null) {// 第一次购买
                map.put(patientId, false);
            } else if (!hasSecond) {// 已经有第一次了
                map.put(patientId, true);
                result.add(vo);
            }
        });
        return result;
    }
}
