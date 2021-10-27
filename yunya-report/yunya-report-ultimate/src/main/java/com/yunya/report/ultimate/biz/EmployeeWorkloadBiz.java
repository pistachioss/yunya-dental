package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.query.MultiClinicEmployeeQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.SortUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.EmployeeWorkloadCost;
import com.yunya.report.ultimate.mapper.BaseBillDetailMapper;
import com.yunya.report.ultimate.mapper.BaseEmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 简介：员工工作量服务层
 *
 * @author: chenlin
 * @Description: 员工工作量服务层
 * @Date: 2021/10/25 14:29
 * @since: 1.0.0
 */
@Service
public class EmployeeWorkloadBiz {

    @Resource(name = "customizeThreadPool")
    private ExecutorService threadPool;

    @Autowired
    private BaseEmployeeMapper baseEmployeeMapper;

    @Autowired
    private BaseBillDetailMapper baseBillDetailMapper;

    @Autowired
    private EmployeeWorkloadCostBiz employeeWorkloadCostBiz;


    /**
     * 根据条件查询员工工作量列表
     *
     * @param query
     * @return
     */
    public PageInfo<ClinicEmployeeWorkloadOfOperationVO> findEmployeeWorkloadListOfOperation(ClinicEmployeeWorkloadQuery query) throws Exception {
        List<ClinicEmployeeWorkloadOfOperationVO> result = findEmployeeWorkloadList(query);
        // 分页
        return PageUtl.doPage(query.getPageNum(), query.getPageSize(), result, query.getWhetherPage());
    }

    /**
     * 根据条件查询员工工作量列表
     *
     * @param query
     * @return
     */
    private List<ClinicEmployeeWorkloadOfOperationVO> findEmployeeWorkloadList(ClinicEmployeeWorkloadQuery query) throws Exception {
        // 主数据：门诊 + 员工
        Future<List<ClinicEmployeBonusCoefficientVO>> employee = findClinicEmployeeCartesianProduct(query);

        // 门诊员工的应收工作量
        Future<Map<String, BigDecimal>> receivableWorkload = findClinicEmployeeReceivableWorkload(query);

        // 门诊员工的实收工作量
        Future<Map<String, BigDecimal>> receivedWorkload = findClinicEmployeeReceivedWorkload(query);

        // 门诊员工的补入工作量
        Future<Map<String, BigDecimal>> supplementWorkload = findClinicEmployeeSupplementWorkload(query);

        // 门诊员工的免单支付工作量
        Future<Map<String, BigDecimal>> freePaymentWorkload = findClinicEmployeeFreePaymentWorkload(query);

        // 门诊员工的退费工作量
        Future<Map<String, BigDecimal>> refundWorkload = findClinicEmployeeRefundWorkload(query);

        // 门诊员工的加工费、正畸加工费、大额材料费
        Future<Map<String, EmployeeWorkloadCost>> fee = findClinicEmployeeWorkCost(query);

        // 数据组装
        List<ClinicEmployeeWorkloadOfOperationVO> result = mergeEmployeeWorkload(employee, receivableWorkload,
                receivedWorkload, supplementWorkload, freePaymentWorkload, refundWorkload, fee);
        // 过滤
        result = enableFilter(query.getEnableFilter(), result);
        // 排序
        return SortUtil.sort(result, comparatorList());
    }

    /**
     * 员工工作量数据过滤
     *
     * @param enableFilter
     * @param result
     */
    private List<ClinicEmployeeWorkloadOfOperationVO> enableFilter(Byte enableFilter, List<ClinicEmployeeWorkloadOfOperationVO> result) {
        if (enableFilter.intValue() == 1) {// 过滤
            return result.stream().filter(vo->isGreaterThanZero(vo)).collect(Collectors.toList());
        }
        return result;
    }

    private boolean isGreaterThanZero(ClinicEmployeeWorkloadOfOperationVO vo) {
        BigDecimal actualWorkload = vo.getActualWorkload();
        BigDecimal receivedWorkload = vo.getReceivedWorkload();
        BigDecimal freePaymentWorkload = vo.getFreePaymentWorkload();
        BigDecimal supplementWorkload = vo.getSupplementWorkload();
        BigDecimal refundWorkload = vo.getRefundWorkload();
        BigDecimal processingFee = vo.getProcessingFee();
        BigDecimal baseWorkload = vo.getBaseWorkload();
        BigDecimal largeMaterialCost = vo.getLargeMaterialCost();
        BigDecimal orthodonticsFee = vo.getOrthodonticsFee();
        if (actualWorkload.compareTo(BigDecimal.ZERO)>0
        ||receivedWorkload.compareTo(BigDecimal.ZERO)>0
        ||freePaymentWorkload.compareTo(BigDecimal.ZERO)>0
        ||supplementWorkload.compareTo(BigDecimal.ZERO)>0
        ||refundWorkload.compareTo(BigDecimal.ZERO)>0
        ||processingFee.compareTo(BigDecimal.ZERO)>0
        ||baseWorkload.compareTo(BigDecimal.ZERO)>0
        ||largeMaterialCost.compareTo(BigDecimal.ZERO)>0
        ||orthodonticsFee.compareTo(BigDecimal.ZERO)>0) {
            return true;
        }
        return false;
    }

    /**
     * 返回员工工作量的排序规则
     *
     * @return
     */
    private Comparator comparatorList() {
        return SortUtil.comparing(
                ClinicEmployeeWorkloadOfOperationVO::getActualWorkload,
                ClinicEmployeeWorkloadOfOperationVO::getReceivedWorkload,
                ClinicEmployeeWorkloadOfOperationVO::getSupplementWorkload,
                ClinicEmployeeWorkloadOfOperationVO::getFreePaymentWorkload,
                ClinicEmployeeWorkloadOfOperationVO::getRefundWorkload,
                ClinicEmployeeWorkloadOfOperationVO::getProcessingFee,
                ClinicEmployeeWorkloadOfOperationVO::getOrthodonticsFee,
                ClinicEmployeeWorkloadOfOperationVO::getBaseWorkload,
                ClinicEmployeeWorkloadOfOperationVO::getLargeMaterialCost
                ).reversed();
    }

    /**
     * 合并组装员工工作量
     *
     * @param empFuture
     * @param receivableWorkload
     * @param receivedWorkload
     * @param supplementWorkload
     * @param freePaymentWorkload
     * @param refundWorkload
     * @param fee
     * @return
     */
    private List<ClinicEmployeeWorkloadOfOperationVO> mergeEmployeeWorkload(Future<List<ClinicEmployeBonusCoefficientVO>> empFuture,
            Future<Map<String, BigDecimal>> receivableWorkload, Future<Map<String, BigDecimal>> receivedWorkload,
            Future<Map<String, BigDecimal>> supplementWorkload, Future<Map<String, BigDecimal>> freePaymentWorkload,
            Future<Map<String, BigDecimal>> refundWorkload, Future<Map<String, EmployeeWorkloadCost>> fee) throws Exception {
        List<ClinicEmployeBonusCoefficientVO> employees = empFuture.get();
        Map<String, BigDecimal> receivableMap = receivableWorkload.get();
        Map<String, BigDecimal> receivedMap = receivedWorkload.get();
        Map<String, BigDecimal> suppleMap = supplementWorkload.get();
        Map<String, BigDecimal> freePaymentMap = freePaymentWorkload.get();
        Map<String, BigDecimal> refundMap = refundWorkload.get();
        Map<String, EmployeeWorkloadCost> feeMap = fee.get();
        List<ClinicEmployeeWorkloadOfOperationVO> result = new ArrayList<>();
        employees.forEach(employee->{
            ClinicEmployeeWorkloadOfOperationVO vo = new ClinicEmployeeWorkloadOfOperationVO();
            vo.setEmployeeName(employee.getEmployeeName());
            vo.setAbbreviation(employee.getAbbreviation());
            vo.setBonusCoefficient(employee.getBonusCoefficient());
            Integer employeeId = employee.getEmployeeId();
            Integer orgId = employee.getOrgId();
            String key = employeeId + "," + orgId;
            vo.setEmployeeId(employeeId);
            vo.setOrgId(orgId);
            vo.setActualWorkload(ifAbsent(receivableMap,key));
            vo.setReceivedWorkload(ifAbsent(receivedMap,key));
            vo.setSupplementWorkload(ifAbsent(suppleMap,key));
            vo.setFreePaymentWorkload(ifAbsent(freePaymentMap,key));
            vo.setRefundWorkload(ifAbsent(refundMap,key));
            EmployeeWorkloadCost employeeWorkloadCost = feeMap.get(key);
            BigDecimal baseWorkload = BigDecimal.ZERO;
            BigDecimal processingFee = BigDecimal.ZERO;
            BigDecimal orthodonticsFee = BigDecimal.ZERO;
            BigDecimal materialFee = BigDecimal.ZERO;
            if (employeeWorkloadCost != null) {
                baseWorkload = employeeWorkloadCost.getBaseWorkload();
                processingFee = employeeWorkloadCost.getProcessingFee();
                orthodonticsFee = employeeWorkloadCost.getOrthodonticsFee();
                materialFee = employeeWorkloadCost.getMaterialFee();
            }
            vo.setOrthodonticsFee(orthodonticsFee);
            vo.setLargeMaterialCost(materialFee);
            vo.setBaseWorkload(baseWorkload);
            vo.setProcessingFee(processingFee);
            result.add(vo);
        });
        return result;
    }

    /**
     * 如果给定值为空或小于0，则返回默认值： 0
     *
     * @param valueMap
     * @return
     */
    private BigDecimal ifAbsent(Map<String, BigDecimal> valueMap, String key) {
        BigDecimal value = valueMap.get(key);
        if (value==null || value.compareTo(BigDecimal.ZERO)<0) {
            value = BigDecimal.ZERO;
        }
        return value;
    }

    /**
     * 多线程查询员工的加工费、正畸加工费、大额材料费
     *
     * @param query
     * @return
     */
    private Future<Map<String, EmployeeWorkloadCost>> findClinicEmployeeWorkCost(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeWorkloadCost> fee = employeeWorkloadCostBiz.findClinicEmployeeWorkCost(query);
            Map<String, EmployeeWorkloadCost> result = new HashMap<>();
            fee.forEach(vo-> result.put(vo.getUserId()+","+vo.getOrgId(), vo));
            return result;
        });
    }

    /**
     * 多线程查询门诊员工的退费工作量
     *
     * @param query
     * @return
     */
    private Future<Map<String, BigDecimal>> findClinicEmployeeRefundWorkload(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeWorkloadVO> workloads = baseBillDetailMapper.selectClinicEmployeeRefundWorkload(query);
            return mapEmployeeWorkload(workloads);
        });
    }

    /**
     * 多线程查询门诊员工的免单支付工作量
     *
     * @param query
     * @return
     */
    private Future<Map<String, BigDecimal>> findClinicEmployeeFreePaymentWorkload(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeWorkloadVO> workloads = baseBillDetailMapper.selectClinicEmployeeFreePaymentWorkload(query);
            return mapEmployeeWorkload(workloads);
        });
    }

    /**
     * 多线程查询门诊员工的补入工作量
     *
     * @param query
     * @return
     */
    private Future<Map<String, BigDecimal>> findClinicEmployeeSupplementWorkload(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeWorkloadVO> workloads = baseBillDetailMapper.selectClinicEmployeeSupplementWorkload(query);
            return mapEmployeeWorkload(workloads);
        });
    }

    /**
     * 多线程查询门诊员工的实收工作量
     * @param query
     * @return
     */
    private Future<Map<String, BigDecimal>> findClinicEmployeeReceivedWorkload(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeWorkloadVO> workloads = baseBillDetailMapper.selectClinicEmployeeReceivedWorkload(query);
            return mapEmployeeWorkload(workloads);
        });
    }

    /**
     * 多线程查询门诊员工的应收工作量
     *
     * @param query
     * @return
     */
    private Future<Map<String, BigDecimal>> findClinicEmployeeReceivableWorkload(ClinicEmployeeWorkloadQuery query) {
        return threadPool.submit(()->{
            List<EmployeeWorkloadVO> workloads = baseBillDetailMapper.selectClinicEmployeeReceivableWorkload(query);
            return mapEmployeeWorkload(workloads);
        });
    }

    /**
     * 多线程查询员工和门诊的笛卡尔积
     *
     * @param query
     * @return
     */
    private Future<List<ClinicEmployeBonusCoefficientVO>> findClinicEmployeeCartesianProduct(MultiClinicEmployeeQuery query) {
        return threadPool.submit(()-> baseEmployeeMapper.selectClinicEmployeeCartesianProduct(query));
    }

    /**
     * 转换成员工map
     *
     * @param workloads
     * @return
     */
    private Map<String, BigDecimal> mapEmployeeWorkload(List<EmployeeWorkloadVO> workloads) {
        Map<String, BigDecimal> result = new HashMap<>();
        workloads.forEach(vo-> result.put(vo.getEmployeeId()+","+vo.getOrgId(), vo.getWorkload()));
        return result;
    }

    /**
     * 根据条件查询人事报表的员工工作量列表
     *
     * @param query
     * @return
     * @throws Exception
     */
    public PageInfo<ClinicEmployeeWorkloadOfPersonnelVO> findEmployeeWorkloadListOfPersonnel(ClinicEmployeeWorkloadQuery query) throws Exception {
        List<ClinicEmployeeWorkloadOfOperationVO> list = findEmployeeWorkloadList(query);
        List<ClinicEmployeeWorkloadOfPersonnelVO> result = computeBonusAndConvertPersonnel(list);
        return PageUtl.doPage(query.getPageNum(), query.getPageSize(), result, query.getWhetherPage());
    }

    /**
     * 计算奖金：
     *      应收奖金、应收奖金基数
     *      实收奖金、实收奖金基数
     * 转换实体：ClinicEmployeeWorkloadOfOperationVO -> ClinicEmployeeWorkloadOfPersonnelVO
     *
     * @param list
     */
    private List<ClinicEmployeeWorkloadOfPersonnelVO> computeBonusAndConvertPersonnel(List<ClinicEmployeeWorkloadOfOperationVO> list) {
        List<ClinicEmployeeWorkloadOfPersonnelVO> result = new ArrayList<>();
        if (StringHelper.isNotEmpty(list)) {
            list.forEach(vo -> {
                ClinicEmployeeWorkloadOfPersonnelVO workloadVO = convertEntity(vo);
                // 奖金系数
                BigDecimal bonusCoefficient = vo.getBonusCoefficient();
                // 补入工作量
                BigDecimal supplementWorkload = vo.getSupplementWorkload();
                // 实收工作量
                BigDecimal actualWorkload = vo.getActualWorkload();
                // 退费工作量
                BigDecimal refundWorkload = vo.getRefundWorkload();
                // 加工费
                BigDecimal processingFee = vo.getProcessingFee();
                // 正畸加工费
                BigDecimal orthodonticsFee = vo.getOrthodonticsFee();
                // 大额材料费
                BigDecimal largeMaterialCost = vo.getLargeMaterialCost();
                // 基础工作量
                BigDecimal baseWorkload = vo.getBaseWorkload();
                // 已收工作量
                BigDecimal receivedWorkload = vo.getReceivedWorkload();
                BigDecimal actualBonusBase =
                        actualWorkload
                                .add(supplementWorkload)
                                .subtract(refundWorkload)
                                .subtract(processingFee)
                                .subtract(orthodonticsFee)
                                .subtract(largeMaterialCost)
                                .subtract(baseWorkload);
                workloadVO.setActualBonusBase(actualBonusBase);
                workloadVO.setActualBonus(actualBonusBase.multiply(bonusCoefficient));
                BigDecimal receivedBonusBase =
                        receivedWorkload
                                .add(supplementWorkload)
                                .subtract(refundWorkload)
                                .subtract(processingFee)
                                .subtract(orthodonticsFee)
                                .subtract(largeMaterialCost)
                                .subtract(baseWorkload);
                workloadVO.setReceivedBonusBase(receivedBonusBase);
                workloadVO.setReceivedBonus(receivedBonusBase.multiply(bonusCoefficient));
                result.add(workloadVO);
            });
        }
        return result;
    }

    /**
     * 实体转换
     * @param vo
     * @return
     */
    private ClinicEmployeeWorkloadOfPersonnelVO convertEntity(ClinicEmployeeWorkloadOfOperationVO vo) {
        ClinicEmployeeWorkloadOfPersonnelVO result = new ClinicEmployeeWorkloadOfPersonnelVO();
        result.setEmployeeId(vo.getEmployeeId());
        result.setEmployeeName(vo.getEmployeeName());
        result.setOrgId(vo.getOrgId());
        result.setAbbreviation(vo.getAbbreviation());
        result.setActualWorkload(vo.getActualWorkload());
        result.setReceivedWorkload(vo.getReceivedWorkload());
        result.setSupplementWorkload(vo.getSupplementWorkload());
        result.setRefundWorkload(vo.getRefundWorkload());
        result.setFreePaymentWorkload(vo.getFreePaymentWorkload());
        result.setProcessingFee(vo.getProcessingFee());
        result.setOrthodonticsFee(vo.getOrthodonticsFee());
        result.setLargeMaterialCost(vo.getLargeMaterialCost());
        result.setBaseWorkload(vo.getBaseWorkload());
        result.setBonusCoefficient(vo.getBonusCoefficient());
        return result;
    }

    public void exportEmployeeWorkloadListOfOperation(HttpServletResponse response, ClinicEmployeeWorkloadQuery query) throws Exception {
        query.setWhetherPage(false);
        List<ClinicEmployeeWorkloadOfOperationVO> resultList = findEmployeeWorkloadListOfOperation(query).getList();
        ExcelUtil<ClinicEmployeeWorkloadOfOperationVO> excelUtil =
                new ExcelUtil<>(ClinicEmployeeWorkloadOfOperationVO.class);
        String fileName = query.getStartDate() + "-" + query.getEndDate() + "员工工作量统计";
        excelUtil.exportExcel(response, resultList, "员工工作量（运营报表）", fileName);
    }

    public void exportEmployeeWorkloadListOfPersonnel(HttpServletResponse response, ClinicEmployeeWorkloadQuery query) throws Exception {
        query.setWhetherPage(false);
        List<ClinicEmployeeWorkloadOfPersonnelVO> resultList = findEmployeeWorkloadListOfPersonnel(query).getList();
        ExcelUtil<ClinicEmployeeWorkloadOfPersonnelVO> excelUtil =
                new ExcelUtil<>(ClinicEmployeeWorkloadOfPersonnelVO.class);
        String fileName = query.getStartDate() + "-" + query.getEndDate() + "员工工作量统计";
        excelUtil.exportExcel(response, resultList, "员工工作量（人事报表）", fileName);

    }

    public PageInfo<BillItemTollAndWorkloadVO> findStatisticsTariffPaymentWorkloadList(BillItemTollAndWorkloadQuery query) {
        return null;
    }

    public void exportTariffPaymentWorkloadList(HttpServletResponse response, BillItemTollAndWorkloadQuery query) {
    }
}
