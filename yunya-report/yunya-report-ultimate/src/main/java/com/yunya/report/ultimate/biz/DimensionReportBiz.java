package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.query.DentistDimensionQueryForm;
import com.yunya.feign.report.domain.query.PatientDimensionQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
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
    private BaseBillMapper baseBillBiz;
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
        Future<List<PatientBillItemVO>> itemFuture = multiFindBillItemNumByPatientId(patientIds);
        // 数据合并
        return patientDimensionMerge(pageInfo, treatNumFuture, firstVisitFuture,
                lastVisitFuture,consumeFuture, appointFuture, remindFuture, itemFuture);
    }

    private DynamicHeaderPageInfo<JSONObject> patientDimensionMerge(PageInfo<PatientManageVo> pageInfo, Future<Map<Integer, Integer>> treatNumFuture,
                                       Future<Map<Integer, String>> firstVisitFuture, Future<Map<Integer, String>> lastVisitFuture,
                                       Future<Map<Integer, PatientCostInfoVO>> consumeFuture, Future<Map<Integer, String>> appointFuture,
                                       Future<Map<Integer, String>> remindFuture, Future<List<PatientBillItemVO>> itemFuture) throws Exception {
        Map<Integer, Integer> treatNumMap = treatNumFuture.get();
        Map<Integer, String> firstVisitMap = firstVisitFuture.get();
        Map<Integer, String> lastVisitMap = lastVisitFuture.get();
        Map<Integer, PatientCostInfoVO> consumeArrearMap = consumeFuture.get();
        Map<Integer, String> appointMap = appointFuture.get();
        Map<Integer, String> remindMap = remindFuture.get();
        List<PatientBillItemVO> billItems = itemFuture.get();
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
            Integer age = vo.getAge();
            obj.put("age", age==null?"":age);
            String orionTypeName = vo.getPatientOrionTypeName();
            obj.put("orionTypeName", orionTypeName==null?"":orionTypeName);
            String memberTypeName = vo.getMemberTypeName();
            obj.put("memberTypeName", memberTypeName==null?"":memberTypeName);
            Integer treatNum = treatNumMap.get(patientId);
            obj.put("treatNum", treatNum==null?0:treatNum);
            PatientCostInfoVO costInfo = consumeArrearMap.get(patientId);
            BigDecimal totalConsume = new BigDecimal(0);
            BigDecimal totalArrear = new BigDecimal(0);
            if (!ObjectUtils.isEmpty(costInfo)) {
                totalConsume = costInfo.getReceivedAmount();
                totalArrear = costInfo.getTotalArrears();
            }
            obj.put("totalConsume", totalConsume);
            obj.put("totalArrear", totalArrear);
            String firstVisitDate = firstVisitMap.get(patientId);
            obj.put("firstVisitDate", StringHelper.isEmpty(firstVisitDate)?"":firstVisitDate);
            String lastVisitDate = lastVisitMap.get(patientId);
            obj.put("lastVisitDate", StringHelper.isEmpty(lastVisitDate)?"":lastVisitDate);
            String nextAppointDate = appointMap.get(patientId);
            obj.put("nextAppointDate", StringHelper.isEmpty(nextAppointDate)?"":nextAppointDate);
            String nextRemindDate = remindMap.get(patientId);
            obj.put("nextRemindDate", StringHelper.isEmpty(nextRemindDate)?"":nextRemindDate);
            specialMap.forEach((id, name)->{
                Integer num = billItemMap.get(id+"."+patientId);
                obj.put(id, num==null?0:num);
            });
            list.add(obj);
        });
        return convertPageInfo(pageInfo, list, specialMap);
    }

    private DynamicHeaderPageInfo<JSONObject> convertPageInfo(PageInfo<PatientManageVo> pageInfo, List<JSONObject> list, Map<String, String> specialMap) {
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

    private Future<List<PatientBillItemVO>> multiFindBillItemNumByPatientId(List<Integer> patientIds) {
        return threadPool.submit(()-> baseBillDetailBiz.findBillItemNumByPatientId(patientIds));
    }

    private Future<Map<Integer, PatientCostInfoVO>> multiFindPatientTotalConsumeArrear(List<Integer> patientIds) {
        return threadPool.submit(()->{
            List<PatientCostInfoVO> costInfos = baseBillBiz.selectPatientCostInfo(patientIds);
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
    public DynamicHeaderPageInfo<JSONObject> dentistDimensionStatistics(DentistDimensionQueryForm query) {
        DynamicHeaderPageInfo<JSONObject> result = new DynamicHeaderPageInfo<>();
        // 员工信息

        // 实收工作量

        // 初诊人数

        // 复诊人数

        // 就诊人次

        // 本月初诊且/复诊
        List<Integer> patientIds = new ArrayList<>();
        Map<String, Integer> reFirstVisit = inMonthReFirstVisit(patientIds);

        // 欠费总额

        // 无下次预约或提醒客户

        // 患者来源

        // 专科项目数量
        Map<String, Integer> itemMap = new HashMap<>();
        return result;
    }

    /**
     * 一个月内既有初诊，又有复诊的人数
     *
     * @return
     * @param patientIds
     */
    private Map<String, Integer> inMonthReFirstVisit(List<Integer> patientIds) {
        Map<String, Integer> result = new HashMap<>(16);
        List<InMonthReFirstVisitVO> visits = baseTreatmentProcessBiz.findInMonthReFirstVisit(patientIds);
        if (StringHelper.isNotEmpty(visits)) {
            visits.forEach(vo-> result.put(vo.getOrgId()+","+vo.getDentistId(), vo.getCount()));
        }
        return result;
    }

    private Map<String, Integer> specialIdNameMap(List<PatientBillItemVO> billItems, Map<String, String> specialMap) {
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
                            specialMap.put(id + "", vo.getSpecialistProjectName());
                        }
                        specialItemMap.put(key, id);
                    }
                }
                String tariffItemIds = vo.getTariffItemIds();
                if (StringHelper.isNotEmpty(tariffItemIds)) {
                    for (String tariffItemId : StringHelper.split(tariffItemIds, ",")) {
                        String key = "0,"+tariffItemId;
                        if (keys.contains(key) && !specialMap.containsKey(id)) {
                            specialMap.put(id+"", vo.getSpecialistProjectName());
                        }
                        specialItemMap.put(key, id);
                    }
                }
            }
        }
        billItems.forEach(vo->{
            Integer specialId = specialItemMap.get(vo.getItemType()+","+vo.getItemId());
            String key = specialId+"."+vo.getPatientId();
            Integer num = result.get(key);
            if (num == null) {
                num = 0;
            }
            result.put(key, num + vo.getQuantity());
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
    public void dentistDimensionStatisticsExport(DentistDimensionQueryForm query, HttpServletResponse response) throws IOException {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = dentistDimensionStatistics(query);
        List<JSONObject> list = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        excelUtil.setMergeRegion(collectMergeCell(pageInfo, 5));
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "医生维度统计");
        excelUtil.exportExcel(response, list, "医生维度统计", fileName, pageInfo.getMap());
    }

    /**
     * 收集合并单元格的位置
     *
     * @param pageInfo
     * @param lastCol
     * @return
     */
    private List<CellRangeAddress> collectMergeCell(DynamicHeaderPageInfo<JSONObject> pageInfo, int lastCol) {
        Map<String, String> title = pageInfo.getMap();
        List<JSONObject> list = pageInfo.getList();
        JSONObject secTitle = new JSONObject();
        title.forEach((key, value)-> secTitle.put(key, value));
        // 添加占位数据行
        list.add(0, secTitle);
        List<CellRangeAddress> region = new ArrayList<>();
        for (int i = 0; i < lastCol; i++) {
            CellRangeAddress crd = new CellRangeAddress(0,1, i , i);
            region.add(crd);
        }

        return region;
    }

    /**
     * 根据条件查询门诊维度统计表
     *
     * @param query
     * @return
     */
    public DynamicHeaderPageInfo<JSONObject> clinicDimensionStatistics(ClinicEmployeeWorkloadQuery query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        // 门诊员工信息
        Future<List<ClinicEmployeBonusCoefficientVO>> employeeFuture = employeeWorkloadBiz.findClinicEmployeeCartesianProduct(query);

        // 实收工作量
        Future<Map<String, BigDecimal>> workloadFuture = employeeWorkloadBiz.findClinicEmployeeReceivedWorkload(query);

        // 初诊人数
        Future<Map<String, Integer>> firstVisitFuture = multiFindFirstVisitNum(query);

        // 复诊人数
        Future<Map<String, Integer>> reVisitFuture = multiFindRepeatVisitNum(query);

        // 就诊人次
        Future<Map<String, Integer>> treatFuture = multiFindTreatVisitsTimes(query);
        // 本月初诊且/复诊
        List<Integer> patientIds = new ArrayList<>();
        Map<String, Integer> reFirstVisit = inMonthReFirstVisit(patientIds);

        // 欠费总额

        // 无下次预约或提醒客户

        // 患者来源

        // 专科项目数量
        Map<String, Integer> itemMap = new HashMap<>();
        return null;
    }

    private Future<Map<String, Integer>> multiFindTreatVisitsTimes(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findTreatVisitsTimes(query);
            return mapEmployeeCount(employees);
        });
    }

    private Future<Map<String, Integer>> multiFindRepeatVisitNum(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findPatientTreatNumGroupEmp(query, 1);
            return mapEmployeeCount(employees);
        });
    }

    private Future<Map<String, Integer>> multiFindFirstVisitNum(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeCountVO> employees = baseTreatmentProcessBiz.findPatientTreatNumGroupEmp(query, 0);
            return mapEmployeeCount(employees);
        });
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
     * 根据条件导出门诊维度统计表
     *
     * @param query
     * @param response
     * @throws IOException
     */
    public void clinicDimensionStatisticsExport(ClinicEmployeeWorkloadQuery query, HttpServletResponse response) throws IOException {
        query.setWhetherPage(false);
        DynamicHeaderPageInfo<JSONObject> pageInfo = clinicDimensionStatistics(query);
        List<JSONObject> list = pageInfo.getList();
        ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
        excelUtil.setMergeRegion(collectMergeCell(pageInfo, 6));
        String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "门诊维度统计");
        excelUtil.exportExcel(response, list, "门诊维度统计", fileName, pageInfo.getMap());
    }
}
