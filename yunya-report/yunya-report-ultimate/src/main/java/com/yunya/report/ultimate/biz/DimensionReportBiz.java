package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.query.PatientDimensionQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    private BaseTreatmentProcessBiz baseTreatmentProcessBiz;
    @Autowired
    private RemoteClinicBaseServiceFeign clinicBaseServiceFeign;
    @Autowired
    private PatientBaseInfoBiz patientBaseInfoBiz;
    @Autowired
    private BaseVisitRemindBiz baseVisitRemindBiz;
    @Autowired
    private BaseBillPayBiz baseBillPayBiz;
    @Autowired
    private BaseBillDetailBiz baseBillDetailBiz;
    @Autowired
    private BaseBillBiz baseBillBiz;
    @Autowired
    private EmployeeWorkloadBiz employeeWorkloadBiz;
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
        PageInfo<PatientManageVo> pageInfo = new PageInfo<>(patients);
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
            obj.put("age", defIntVal(vo.getAge()));
            obj.put("orionTypeName", defValue(vo.getPatientOrionTypeName()));
            obj.put("memberTypeName", defValue(vo.getMemberTypeName()));
            obj.put("treatNum", defValue(treatNumMap.get(patientId)));
            PatientCostInfoVO costInfo = consumeArrearMap.get(patientId);
            BigDecimal totalConsume = new BigDecimal(0);
            BigDecimal totalArrear = new BigDecimal(0);
            if (!ObjectUtils.isEmpty(costInfo)) {
                totalConsume = costInfo.getReceivedAmount();
                totalArrear = costInfo.getTotalArrears();
            }
            obj.put("totalConsume", totalConsume);
            obj.put("totalArrear", totalArrear);
            obj.put("firstVisitDate", defValue(firstVisitMap.get(patientId)));
            obj.put("lastVisitDate", defValue(lastVisitMap.get(patientId)));
            obj.put("nextAppointDate", defValue(appointMap.get(patientId)));
            obj.put("nextRemindDate", defValue(remindMap.get(patientId)));
            specialMap.forEach((id, name)->{
                obj.put(id, defIntVal(billItemMap.get(id+"."+patientId)));
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
        Map<String, Integer> specialItemMap = new HashMap<>();
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
                        specialItemMap.put(key, id);
                    }
                }
                String tariffItemIds = vo.getTariffItemIds();
                if (StringHelper.isNotEmpty(tariffItemIds)) {
                    for (String tariffItemId : StringHelper.split(tariffItemIds, ",")) {
                        String key = "0,"+tariffItemId;
                        if (keys.contains(key) && !specialMap.containsKey(id)) {
                            specialMap.put("S"+id, vo.getSpecialistProjectName());
                        }
                        specialItemMap.put(key, id);
                    }
                }
            }
        }
        billItems.forEach(vo->{
            Integer specialId = specialItemMap.get(vo.getItemType()+","+vo.getItemId());
            if (!ObjectUtils.isEmpty(specialId)) {
                String key = "S" +specialId + "." + vo.getPersonId();
                if (!ObjectUtils.isEmpty(vo.getOrgId())) {
                    key += "," + vo.getOrgId();
                }
                Integer num = result.get(key);
                if (num == null) {
                    num = 0;
                }
                result.put(key, num + vo.getQuantity());
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
        excelUtil.setMergeRegion(collectMergeCell(pageInfo, 6));
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
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        // 门诊员工信息
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
        return mergeClinicDimension(employees, workloadFuture, firstVisitFuture, reFirstVisitFuture,
                treatFuture, debtFuture, noAppointAndRemindFuture, patientOrginFuture, billItemFuture, reVisitFuture, groupByOrgId);
    }

    private void updEmployeeId2Query(List<ClinicEmployeBonusCoefficientVO> employees, ClinicEmployeeWorkloadQuery query) {
        List<Integer> employeeIds = new ArrayList<>();
        employees.forEach(vo->{
            Integer employeeId = vo.getEmployeeId();
            if (employeeIds.contains(employeeId)) {
                employeeIds.add(employeeId);
            }
        });
        query.setEmployeeIds(employeeIds.toArray(new Integer[0]));
    }

    private DynamicHeaderPageInfo<JSONObject> mergeClinicDimension(List<ClinicEmployeBonusCoefficientVO> employees,
            Future<Map<String, BigDecimal>> workloadFuture, Future<Map<String, Integer>> firstVisitFuture,
            Future<Map<String, Integer>> reFirstVisitFuture, Future<Map<String, Integer>> treatFuture,
            Future<Map<String, BigDecimal>> debtFuture, Future<Map<String, Integer>> noAppointAndRemindFuture,
            Future<List<EmployeeFirstVisitOriginTypeVO>> patientOrginFuture, Future<List<PersonalBillItemVO>> billItemFuture,
            Future<Map<String, Integer>> reVisitFuture, boolean groupByOrgId) throws Exception {
        Map<String, BigDecimal> workloadMap = workloadFuture.get();
        Map<String, Integer> firstVisitMap = firstVisitFuture.get();
        Map<String, Integer> reFirstVisitMap = reFirstVisitFuture.get();
        Map<String, Integer> treatTimesMap = treatFuture.get();
        Map<String, BigDecimal> debtMap = debtFuture.get();
        Map<String, Integer> noARMap = noAppointAndRemindFuture.get();
        List<EmployeeFirstVisitOriginTypeVO> originTypes = patientOrginFuture.get();
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
        List<PersonalBillItemVO> billItems = billItemFuture.get();
        Map<String, String> specialMap = new LinkedHashMap<>(16);
        Map<String, Integer> billItemMap = specialIdNameMap(billItems, specialMap);
        if (StringHelper.isNotEmpty(specialMap)) {
            title.put("specialProjectNames", specialMap.values().stream().collect(Collectors.toList()));
        }
        List<JSONObject> list = new ArrayList<>();
        Map<String, Integer> reVisitMap = reVisitFuture.get();
        employees.forEach(vo->{
            Integer employeeId = vo.getEmployeeId();
            Integer orgId = vo.getOrgId();
            String key = employeeId + "," + orgId;
            JSONObject obj = new JSONObject();
//            obj.put("employeeId", employeeId);
//            obj.put("orgId", orgId);
            if (groupByOrgId) {
                obj.put("abbreviation", defValue(vo.getAbbreviation()));
            }
            obj.put("employeeName", defValue(vo.getEmployeeName()));
            obj.put("workload", defDecVal(workloadMap.get(key)));
            obj.put("firstVisitCount", defIntVal(firstVisitMap.get(key)));
            obj.put("reVisitCount", defIntVal(reVisitMap.get(key)));
            obj.put("treatTimes", defIntVal(treatTimesMap.get(key)));
            obj.put("reFirstVisitCount", defIntVal(reFirstVisitMap.get(key)));
            obj.put("debtAmount", defDecVal(debtMap.get(key)));
            obj.put("hasntAppointAndRemind", defIntVal(noARMap.get(key)));
            originTypeMap.forEach((originTypeId, name)->{
                Integer num = originDataMap.get(key + "," + originTypeId);
                obj.put(originTypeId, defIntVal(num));
            });
            specialMap.forEach((id, name)->{
                Integer num = billItemMap.get(id+"."+employeeId+","+orgId);
                obj.put(id, defIntVal(num));
            });
            list.add(obj);
        });
        return convertClinicDimensionPageInfo(list, title, originTypeMap, specialMap, groupByOrgId);
    }

    private DynamicHeaderPageInfo<JSONObject> convertClinicDimensionPageInfo(List<JSONObject> list, Map<String, List<String>> contextMap, Map<String, String> originTypeMap, Map<String, String> specialMap, boolean groupByOrgId) {
        DynamicHeaderPageInfo<JSONObject> result = new DynamicHeaderPageInfo<>(list);
        Map<String, String> title = new LinkedHashMap<>(16);
        String[] header = {"医生", "工作量", "初诊人数", "复诊人数", "就诊人次", "本月初诊且复诊", "患者来源", "", "", "", "", "", "", "欠费总额", "无下次预约或提醒客户", "专科数量"};
        if (groupByOrgId) {
            title.put("abbreviation", "门诊");
            header = new String[]{"门诊", "医生", "工作量", "初诊人数", "复诊人数", "就诊人次", "本月初诊且复诊", "患者来源", "", "", "", "", "", "", "欠费总额", "无下次预约或提醒客户", "专科数量"};
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
        result.setHeader(header);
        return result;
    }

    private Object defValue(Object value) {
        return defValue(value, String.class);
    }

    private Object defIntVal(Object value) {
        return defValue(value, Integer.class);
    }

    private Object defDecVal(Object value) {
        return defValue(value, BigDecimal.class);
    }

    private Object defValue(Object value, Class<?> clzz) {
        if (!ObjectUtils.isEmpty(value)) {
            return value;
        }
        if (clzz == Integer.class) {
            return 0;
        } else if (clzz == BigDecimal.class) {
            return new BigDecimal("0.00");
        } else {
            return "";
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
        list.forEach(vo-> result.put(vo.getEmployeeId()+","+vo.getOrgId(), vo.getCount()));
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
        int index = lastCol + originTypeNames.size()-1;
        region.add(new CellRangeAddress(0,0, lastCol, index++));
        region.add(new CellRangeAddress(0,1, index, index++));
        region.add(new CellRangeAddress(0,1, index, index++));
        region.add(new CellRangeAddress(0,0, index, index + specialProjectNames.size()-1));
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
        excelUtil.setMergeRegion(collectMergeCell(pageInfo, 7));
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "门诊维度统计");
        excelUtil.exportExcel(response, list, "门诊维度统计", fileName, pageInfo.getHeader(), pageInfo.getMap());
    }
}
