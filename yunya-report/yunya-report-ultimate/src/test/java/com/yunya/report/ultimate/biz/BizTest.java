package com.yunya.report.ultimate.biz;

import com.alibaba.excel.*;
import com.alibaba.fastjson.*;
import com.github.pagehelper.*;
import com.yunya.feign.report.domain.bo.*;
import com.yunya.feign.report.domain.model.*;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/1/15 10:14
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BizTest {
    @Autowired
    private DiscountBiz discountBiz;
    @Autowired
    private BaseBillBiz baseBillBiz;
    @Autowired
    private BaseBillDetailBiz baseBillDetailBiz;
    @Autowired
    private PatientReportBiz patientReportBiz;
    @Autowired
    private BaseAccountItemBiz baseAccountItemBiz;
    @Autowired
    private EmployeeWorkloadCostBiz employeeWorkloadCostBiz;
    @Autowired
    private BaseUserPostBiz baseUserPostBiz;
    @Autowired
    private ClinicDataStatisticsBiz clinicDataStatisticsBiz;
    @Autowired
    private BaseTariffInfoBiz baseTariffInfoBiz;
    @Autowired
    private BaseBillPayBiz baseBillPayBiz;

    @Test
    public void test1() {
        System.out.println(discountBiz.getCouponName(510));
    }

    @Test
    public void test() throws Exception {
        CouponActiveDetailQuery query = new CouponActiveDetailQuery();
        List<CouponActiveDetailVo> list = discountBiz.getCouponActiveList(510, query);
        if(list != null && !list.isEmpty()){
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\users.xlsx");
//            discountBiz.buildResponse(response, discountBiz.getCouponName(couponId) + "第三方平台卡券售出明细");
            EasyExcel.write(fileOutputStream, CouponActiveDetailVo.class)
                    .sheet("sheet").doWrite(list);
        }
    }

    @Test
    public void test2() {
        System.out.println(baseBillBiz.billDiscountDetailInfo(437245));
    }

    @Test
    public void test3() {
        String param = "{\"dateType\":0,\"employeeIds\":[635],\"postIds\":[],\"workStatus\":[],\"orgId\":26,\"pageNum\":1,\"pageSize\":10,\"queryDate\":\"2021-03\",\"whetherPage\":true}";
        EmployeeWorkloadQuery query = JSONObject.parseObject(param, EmployeeWorkloadQuery.class);
        PageInfo<EmployeeWorkloadOfOperationVO> pageInfo = baseBillDetailBiz.findEmployeeWorkloadListOfOperation(query);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    @Test
    public void test4() {
        String param = "{\"billNum\":\"\",\"dateType\":0,\"employeeId\":612,\"keyword\":\"\",\"billDate\":\"\",\"orgId\":35,\"queryDate\":\"2021-04\",\"whetherPage\":true,\"pageNum\":1,\"pageSize\":10}";
        EmployeePersonalWorkloadDetailQuery query = JSONObject.parseObject(param, EmployeePersonalWorkloadDetailQuery.class);
        PageInfo<EmployeeFreepaymentWorkloadDetailVO> pageInfo =
                baseBillDetailBiz.findEmployeeFreepaymentWorkloadDetailList(query);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    @Test
    public void test5() {
        String param = "{\"billId\":433298,\"whetherPage\":true,\"pageNum\":1,\"pageSize\":10,\"employeeId\":635,\"billPayId\":276695,\"orgId\":26}";
        EmployeeFreePaymentWorkloadDetailQuery query = JSONObject.parseObject(param, EmployeeFreePaymentWorkloadDetailQuery.class);
        PageInfo<EmployeeReceivedDetailWorkloadVO> pageInfo =
                baseBillDetailBiz.findFreePaymentDetailList(query);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    @Test
    public void test6() {
        String param = "{\"dateType\":0,\"employeeIds\":[],\"postIds\":[43],\"workStatus\":[],\"orgId\":26,\"pageNum\":1,\"pageSize\":10,\"queryDate\":\"2021-03\",\"whetherPage\":true}";
        EmployeeWorkloadQuery query = JSONObject.parseObject(param, EmployeeWorkloadQuery.class);
        PageInfo<EmployeeWorkloadOfPersonnelVO> result = baseBillDetailBiz.findEmployeeWorkloadListOfPersonnel(query);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void test7() {
        String param = "{\"orgId\":26,\"type\":\"day\",\"time\":[\"2021-03-01\",\"2021-03-31\"],\"treatTypes\":[],\"date\":null,\"attendingDoctors\":[],\"registeredDentistIds\":[],\"combination\":null,\"monthDay\":null,\"year\":null,\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true,\"startDate\":\"2021-03-01\",\"endDate\":\"2021-03-31\"}";
        PatientReportQueryForm form = JSONObject.parseObject(param, PatientReportQueryForm.class);
        PageInfo<BasePatientNotSeenVo> basePatientNotSeenVoList = patientReportBiz.notSeenList(form);
        System.out.println(JSONObject.toJSON(basePatientNotSeenVoList));
    }

    @Test
    public void test8() {
        String param = "{\"orgId\":26,\"dateType\":1,\"date\":null,\"date2\":\"2021-03\",\"date3\":null,\"startDate\":\"2021-03\",\"endDate\":\"2021-03\"}";
        InboundAndOutboundStatementQuery query = JSONObject.parseObject(param, InboundAndOutboundStatementQuery.class);
        List<ClinicInboundAndOutboundVO> resultList =
                baseAccountItemBiz.findInboundAndOutboundStatement(query);
        System.out.println(JSONObject.toJSON(resultList));
    }

    @Test
    public void testUpdate() {
        String param = "{\"orgId\":26,\"employeeId\":642,\"entryMonth\":\"2021-03\",\"materialFee\":\"\",\"processingFee\":\"\",\"orthodonticsFee\":\"1\"}";
        EmployeeWorkloadCostModel model = JSONObject.parseObject(param, EmployeeWorkloadCostModel.class);
        employeeWorkloadCostBiz.addOrModifyCost(model);
    }

    @Test
    public void test9() {
        String param = "{\"dentistIds\":[659],\"workStatus\":[],\"endDate\":\"2021-03-26\",\"orgId\":35,\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-03-26\",\"whetherPage\":true}";
        EmployeeDiagnosisQuery query = JSONObject.parseObject(param, EmployeeDiagnosisQuery.class);
        PageInfo<EmployeeDiagnosisInfoVO> result = baseUserPostBiz.findEmployeeDiagnosisInfoList(query);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void test10() throws ExecutionException, InterruptedException {
        String param = "{\"assistantIds\":[],\"dateType\":1,\"endDate\":\"2021-01\",\"orgId\":26,\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-01\",\"workStatus\":[3],\"whetherPage\":true}";
        EmployeeMatchingRecordQuery query = JSONObject.parseObject(param, EmployeeMatchingRecordQuery.class);
        baseUserPostBiz.findTreatMatchingStatisticsList(query);
    }

    @Test
    public void test11() {
        String param = "{\"orgIds\":[26],\"dateType\":1,\"startDate\":\"2021-03\",\"endDate\":\"2021-03\"}";
        DataStatisticsQuery query = JSONObject.parseObject(param, DataStatisticsQuery.class);
        ClinicDataStatisticsInfoVO clinicDataStatisticsInfoVO = clinicDataStatisticsBiz.findClinicDataStatisticsInfo(query);
        System.out.println(JSONObject.toJSON(clinicDataStatisticsInfoVO));
    }

    @Test
    public void test12() {
        String param = "{\"dateType\":1,\"startDate\":\"2021-03\",\"endDate\":\"2021-03\"}";
        DataStatisticsQuery query = JSONObject.parseObject(param, DataStatisticsQuery.class);
        PageInfo<OperationDataBusinessGoalVO> result = clinicDataStatisticsBiz.findAnalysisBusinessGoalList(query);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void test13() {
        String param = "{\"dateType\":0,\"employeeIds\":[],\"pageNum\":1,\"pageSize\":10,\"queryDate\":\"2021-03\",\"whetherPage\":true}";
        EmployeeWorkloadQuery query = JSONObject.parseObject(param, EmployeeWorkloadQuery.class);
        long t1 = System.currentTimeMillis();
        PageInfo<PersonalWorkloadVO> result = baseBillDetailBiz.personalWorkloadList(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(result));
    }

    /**
     * 开单项目及实收金额列表
     */
    @Test
    public void test14() {
        String param = "{\"itemType\":1,\"categoryItems\":[],\"employeeIds\":[],\"dateType\":1,\"workStatus\":[],\"startDate\":\"2021-04\",\"endDate\":\"2021-04\",\"orgId\":26,\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true,\"categoryList\":[]}";
        BillItemInfoQuery query = JSONObject.parseObject(param, BillItemInfoQuery.class);
        long t1 = System.currentTimeMillis();
        PageInfo<BillItemStatisticsVO> result = baseBillDetailBiz.billItemStatistics(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(result));
    }

    /**
     * 开单项目及实收金额明细
     */
    @Test
    public void test15() {
        String param = "{\"keyword\":\"\",\"billNum\":\"\",\"executorIds\":[],\"orgId\":26,\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true,\"startDate\":\"2021-04\",\"endDate\":\"2021-04\",\"dateType\":1,\"itemId\":101,\"itemType\":1,\"regDentistId\":\"303\",\"showClinic\":true,\"toMan\":false}";
        BillItemDetailQuery query = JSONObject.parseObject(param, BillItemDetailQuery.class);
        long t1 = System.currentTimeMillis();
        PageInfo<BillItemStatisticsDetailVO> result = baseBillDetailBiz.billItemStatiticsDetail(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(result));
    }

    /**
     * 分类ID查询全部项目列表
     */
    @Test
    public void test16() {
        long t1 = System.currentTimeMillis();
        List<ItemInfoVO> result = baseTariffInfoBiz.findItemListByCategoryId(0,7);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(result));
    }

    /**
     * 开单项目分类列表
     */
    @Test
    public void test17() {
        long t1 = System.currentTimeMillis();
        List<ItemCategoryInfoVO> result = baseTariffInfoBiz.findItemCategoryList(0);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(result));
    }

    /**
     * 折扣&免单列表
     */
    @Test
    public void test18() {
        String param = "{\"employeeIds\":[660],\"accountType\":\"\",\"startDate\":\"2021-04-02\",\"orgIds\":[30],\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true,\"endDate\":\"2021-04-02\"}";
        BillDiscountAndFreePaymentQuery query = JSONObject.parseObject(param, BillDiscountAndFreePaymentQuery.class);
        long t1 = System.currentTimeMillis();
        PageInfo<BillDiscountAndFreePaymentVO> pageInfo = baseBillPayBiz.billDiscountAndFreePaymentList(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    @Test
    public void test19() {
        System.out.println(JSONObject.toJSON(baseBillPayBiz.billDiscountAndFreePaymentItems()));
    }

    /**
     * 产品使用列表
     */
    @Test
    public void test20() {
        String param = "{\"couponIds\":[],\"executorName\":\"\",\"workStatus\":\"\",\"startDate\":\"2021-03-01\",\"endDate\":\"2021-03-31\",\"orgIds\":[26],\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
        CouponExecutoredQuery query = JSONObject.parseObject(param, CouponExecutoredQuery.class);
        long t1 = System.currentTimeMillis();
        PageInfo<CouponExecutoredVO> pageInfo = baseBillDetailBiz.couponExecutoredList(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    /**
     * 产品使用明细
     */
    @Test
    public void test21() {
        String param = "{\"endDate\":\"2021-03-31\",\"startDate\":\"2021-03-01\",\"executorId\":27,\"keyword\":\"\",\"itemId\":729,\"itemType\":0,\"couponId\":135,\"orgId\":26,\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true,\"abbreviation\":\"古墩路门诊\",\"executorName\":\"谢玮\",\"couponName\":\"973单次达妃琦美白\",\"itemName\":\"牙齿美白（线上活动专享）\",\"num\":\"4\",\"index\":2,\"showClinic\":true}";
        CouponExecutoredDetailQuery query = JSONObject.parseObject(param, CouponExecutoredDetailQuery.class);
        long t1 = System.currentTimeMillis();
        PageInfo<CouponExecutoredDetailVO> pageInfo = baseBillDetailBiz.couponExecutoredDetails(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    /**
     * 数据总览-工作量
     */
    @Test
    public void test22() {
        String param = "{\"orgIds\":[31],\"dateType\":1,\"startDate\":\"2021-04\",\"endDate\":\"2021-04\"}";
        DataStatisticsQuery query = JSONObject.parseObject(param, DataStatisticsQuery.class);
        WorkloadStatisticsVO clinicWorkloadStatistic = baseBillDetailBiz.findClinicWorkloadStatistic(query);
        System.out.println(JSONObject.toJSON(clinicWorkloadStatistic));
    }

    /**
     * 月工作量完成度导出
     */
    @Test
    public void test23() throws IOException {
        String startDate = "2021-04-01";
        String curDate = "2021-04-13";
        DynamicHeaderPageInfo<JSONObject> resultList = baseBillDetailBiz.workloadCompleted(startDate, curDate);
        System.out.println(JSONObject.toJSON(resultList));
    }

    /**
     * 门诊业绩
     */
    @Test
    public void test24() {
        String param = "{\"dateType\":1,\"endDate\":\"2021-03-31\",\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-03-01\",\"whetherPage\":true}";
        ClinicPerformanceBusinessQuery query = JSONObject.parseObject(param,ClinicPerformanceBusinessQuery.class);
        long t1 = System.currentTimeMillis();
        DynamicHeaderPageInfo<JSONObject> pageInfo = baseBillDetailBiz.clinicPerformanceList(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    /**
     * 初诊来源数量分析
     */
    @Test
    public void testClinicFirstVisitStatistics() {
        String param = "{\"dateType\":1,\"originTypes\":[1],\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-04\",\"endDate\":\"2021-04\",\"whetherPage\":true}";
        ClinicPerformanceBusinessQuery query = JSONObject.parseObject(param,ClinicPerformanceBusinessQuery.class);
        long t1 = System.currentTimeMillis();
        DynamicHeaderPageInfo<JSONObject> pageInfo = baseBillDetailBiz.clinicFirstVisitSourceList(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    /**
     * 门诊专科项目数量统计
     */
    @Test
    public void testClinicSpecialItemList() {
        String param = "{\"dateType\":1,\"endDate\":\"2021-05-01\",\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-03-01\",\"whetherPage\":true}";
        ClinicPerformanceBusinessQuery query = JSONObject.parseObject(param,ClinicPerformanceBusinessQuery.class);
        long t1 = System.currentTimeMillis();
        DynamicHeaderPageInfo<JSONObject> pageInfo = baseBillDetailBiz.clinicSpecialItemList(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(pageInfo));
    }

    /**
     * 门诊365卡销售激活统计
     */
    @Test
    public void testClinic365CardSaleActivitedList() {
        String param = "{\"dateType\":1,\"orgIds\":[30],\"pageNum\":1,\"pageSize\":10,\"startDate\":\"2021-04\",\"endDate\":\"2021-04\",\"whetherPage\":true}";
        ClinicPerformanceBusinessQuery query = JSONObject.parseObject(param,ClinicPerformanceBusinessQuery.class);
        long t1 = System.currentTimeMillis();
        PageInfo<SaleActivited365CardVO>  pageInfo = baseBillDetailBiz.clinic365CardSaleActivitedList(query);
        System.out.println(System.currentTimeMillis() - t1);
        System.out.println(JSONObject.toJSON(pageInfo));
    }
}
