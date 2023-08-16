package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.feign.treatment.domain.model.CouponDiscountInfoModel;
import com.yunya.feign.treatment.domain.model.TreatTollModel;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.PatientBillStatistics;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.modules.treatment.task.AutoChargeTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/28 16:12
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillRecordBizTest {
  @Autowired private BillRecordBiz billRecordBiz;
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;
  @Autowired private OrderDetailBiz orderDetailBiz;
  @Autowired private BillPayRecordBiz billPayRecordBiz;
  @Autowired private AutoChargeTask autoChargeTask;

  @Autowired private RemoteDiscountFeign discountFeign;

  @Test
  public void getNum() {
    String number = billRecordBiz.generateBillNumber(21);
    System.out.println(number);
  }

  @Test
  public void test() {
    PatientBillStatistics statistics = billRecordBiz.statisticsBill(106058);
    System.out.println(statistics);
  }

  @Test
  public void test1() {
    String param = "{\"startDate\":null,\"endDate\":null,\"treatStatus\":[],\"orgIds\":[],\"dates\":null,\"patientId\":\"106058\",\"pageNum\":1,\"pageSize\":10,\"whetherPage\":true}";
    PatientTreatmentRecordQueryForm queryForm = JSONObject.parseObject(param,PatientTreatmentRecordQueryForm.class);
    PageInfo<PatientTreatmentRecordVO> resultList =
            treatmentRecordBiz.findPatientTreatList(queryForm);
    System.out.println(JSONObject.toJSON(resultList));
  }

  @Test
  public void test2() {
    String param = "{\"orgIds\":[26,27,28,29],\"dateType\":1,\"startDate\":\"2021-02\",\"endDate\":\"2021-02\"}";
    DataStatisticsQuery query = JSONObject.parseObject(param, DataStatisticsQuery.class);
    PageInfo<SpecialistProjectCompletedInfoVO> resultList = orderDetailBiz.specialistProjectTargetCompletedList(query);
    System.out.println(JSONObject.toJSON(resultList));
  }

  @Test
  public void testRevoke() {
    BaseContextHandler.setOrgId("26");
    BaseContextHandler.setUserID("635");
    BaseContextHandler.setName("测试-chenlin");
    billPayRecordBiz.revoke(280587);
  }


  @Test
  public void testFindDebtList() {
    BillOfReceivableQuery query = new BillOfReceivableQuery();
    query.setPageNum(1);
    query.setPageSize(5);
    query.setOrgIds(Arrays.asList(43, 26, 27, 36, 35, 37, 28, 29, 30, 31, 32, 33, 34, 39, 40, 45));
//    query.setBillRecordIds(Arrays.asList(439647));
    query.setQueryDate("2021-03-23");
    long t1 = System.currentTimeMillis();
    PageInfo<BillRestReceivableAmountVO> debtList = billRecordBiz.findDebtList(query);
    long t2 = System.currentTimeMillis();
    System.out.println("耗时：" + (t2 - t1));
    System.out.println("数据：" + JSONObject.toJSON(debtList));
  }

  @Test
  public void testFindCurrentMonthStatementStatistic(){
    StatementStatisticQuery query = new StatementStatisticQuery();
    query.setOrgId(45);
    query.setQueryDate("2022-05");
    CurrentMonthBillStatisticVO currentMonthStatementStatistic = billRecordBiz.findCurrentMonthStatementStatistic(query);
    System.out.println(JSONObject.toJSON(currentMonthStatementStatistic));
  }

  @Test
  public void autoCharge() throws InterruptedException {
    autoChargeTask.autoCharge();
  }

  @Test
  public void cardDiscount() {
    Integer patientId = 334;
    Integer orderRecordId = 619;
    Integer memberCardId = null;
    Integer discountId = null;
    PatientChooseBenefitForm form = new PatientChooseBenefitForm();
    form.setPatientId(patientId);
    form.setOrderId(orderRecordId);
    form.setOrgId(63);
    form.setMemberCardId(memberCardId);
    form.setDiscountId(discountId);
    List<Integer> exchangeIds = Lists.newArrayList();
    List<Integer> voucherIds = Lists.newArrayList();
    List<Integer> packageIds = Lists.newArrayList();
    List<CouponDiscountInfoModel> models = new ArrayList<>();
    CouponDiscountInfoModel model = new CouponDiscountInfoModel();
    model.setCouponType((byte) 3);
    model.setCouponCommonInfoId(31873);
    models.add(model);
    // 分类卡券列表
    setCouponListValue(models, voucherIds, exchangeIds, packageIds);
    form.setExchangeIds(exchangeIds);
    form.setPackageIds(packageIds);
    form.setVoucherIds(voucherIds);
    PatientOrderBenefitVo data = discountFeign.choiceBenefit(form).getData();
    System.out.println(JSONArray.toJSON(data.getItemList()));
  }

  private void setCouponListValue(
          List<CouponDiscountInfoModel> coupons,
          List<Integer> voucherIds,
          List<Integer> exchangeIds,
          List<Integer> packageIds) {
    if (StringHelper.isNotEmpty(coupons)) {
      for (CouponDiscountInfoModel model : coupons) {
        Byte couponType = model.getCouponType();
        Integer couponCommonInfoId = model.getCouponCommonInfoId();
        switch (couponType) {
          // 代金券
          case 0:
            voucherIds.add(couponCommonInfoId);
            break;
          // 兑换券
          case 2:
            exchangeIds.add(couponCommonInfoId);
            break;
          // 套餐券
          case 3:
            packageIds.add(couponCommonInfoId);
            break;
          default:
            break;
        }
      }
    }
  }

  @Autowired private MinorChargeProcessBiz minorChargeProcessBiz;
  @Test
  public void testSavePrivilegeDetail() {
    BaseContextHandler.setOrgId("63");
    Byte discountType = 1;
    Integer patientId = 419;
    Integer orderRecordId = 624711;
    String param = "{\"discountType\":1,\"generalDiscountModel\":{\"couponDiscountInfoModels\":[{\"couponCommonInfoId\":40045,\"couponType\":5}],\"memberTypeId\":1},\"invoiceModel\":{\"invoice\":false},\"memberAccountModels\":[],\"orderRecordId\":624711,\"outstandingAmount\":0,\"paymentModels\":[],\"prepaymentAccountModels\":[]}";
    TreatTollModel model = JSONObject.parseObject(param, TreatTollModel.class);
    minorChargeProcessBiz.savePrivilegeDetail(discountType, patientId, orderRecordId, model);
  }
}
