package com.yunya.feign.treatment;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.discount.domain.form.FreeStockForm;
import com.yunya.feign.discount.domain.form.LockStockForm;
import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.feign.ivy_mini.domain.bo.ProductBO;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.vo.GoodsVO;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.report.domain.query.SpecialistProjectCompletedCountQuery;
import com.yunya.feign.treatment.domain.model.DebtAmountModel;
import com.yunya.feign.treatment.domain.query.ClinicMemberPriceQuery;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.feign.treatment.factory.RemoteTreatmentServiceFeignFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.Registered;
import com.yunya.models.treatment.TreatmentRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 简介: 云牙价目表、就诊服务接口调用
 *
 * @author: chow
 * @date: 2020/8/6 17:50
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_TREATMENT_SERVICE,
    fallbackFactory = RemoteTreatmentServiceFeignFallBackFactory.class)
public interface RemoteTreatmentServiceFeign {

  /**
   * 根据开单ID查询开单详情与账单详情信息
   *
   * @return List<BaseOralTariffCategory>
   */
  @RequestMapping(value = "/rpc/bill/detail/{orderRecordId}", method = RequestMethod.GET)
  BillDetailGroupVO findOrderDetailAndBillDetailByOrderRecordId(
          @PathVariable(value = "orderRecordId") Integer orderRecordId);

  /**
   * 根据商品分类ID查询商品分类信息
   *
   * @param id 商品分类ID
   * @return BaseOralTariffCategory
   */
  @RequestMapping(value = "/rpc/oral/category/{id}", method = RequestMethod.GET)
  BaseOralTariffCategory findBaseOralTariffCategoryById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询商品分类列表
   *
   * @param queryForm 查询条件
   * @return List<BaseOralTariffCategory>
   */
  @RequestMapping(value = "/rpc/oral/category/list", method = RequestMethod.POST)
  List<BaseOralTariffCategory> findBaseOralTariffCategoryList(
      @RequestBody BaseOralTariffCategory queryForm);

  /**
   * 根据商品项目ID查询商品项目信息
   *
   * @param id 商品项目ID
   * @return BaseOralTariff
   */
  @RequestMapping(value = "/rpc/oral/one/{id}", method = RequestMethod.GET)
  BaseOralTariff findBaseOralTariffById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询商品项目列表
   *
   * @param entity 查询条件
   * @return List<BaseOralTariff
   */
  @RequestMapping(value = "/rpc/oral/list", method = RequestMethod.POST)
  List<BaseOralTariff> findBaseOralTariffList(@RequestBody BaseOralTariff entity);

  /**
   * 根据价目表分类ID查询价目表分类信息
   *
   * @param id 价目表分类ID
   * @return BaseTariffCategory
   */
  @RequestMapping(value = "/rpc/base/one/{id}", method = RequestMethod.GET)
  BaseTariffCategory findBaseTariffCategoryById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询价目表分类列表
   *
   * @param entity 查询条件
   * @return List<BaseTariffCategory>
   */
  @RequestMapping(value = "/rpc/base/list", method = RequestMethod.POST)
  List<BaseTariffCategory> findBaseTariffCategoryList(@RequestBody BaseTariffCategory entity);

  /**
   * 根据多个价目表ID查询价目表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  @RequestMapping(value = "/rpc/tariff/name", method = RequestMethod.POST)
  String findBaseTariffNamesByIds(@RequestBody @NotEmpty String[] ids);

  /**
   * 根据价目表项目ID查询基础价目表信息
   *
   * @param id 基础价目表ID
   * @return BaseTariff
   */
  @RequestMapping(value = "/rpc/tariff/{id}", method = RequestMethod.GET)
  BaseTariff findBaseTariffById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询价目表列表
   *
   * @param entity 查询条件
   * @return List<BaseTariff>
   */
  @RequestMapping(value = "/rpc/tariff/list", method = RequestMethod.POST)
  List<BaseTariff> findBaseTariffList(@RequestBody BaseTariff entity);

  /**
   * 根据条件查询门诊价目表信息
   *
   * @param entity 门诊价目表
   * @return ClinicTariff
   */
  @RequestMapping(value = "/rpc/clinic/tariff/one", method = RequestMethod.POST)
  ClinicTariff findClinicTariff(@RequestBody ClinicTariff entity);

  /**
   * 根据条件查询门诊商品信息
   *
   * @param entity 商品价目表
   * @return ClinicOralTariff
   */
  @RequestMapping(value = "/rpc/clinic/oral/one", method = RequestMethod.POST)
  ClinicOralTariff findClinicOralTariff(@RequestBody ClinicOralTariff entity);

  /**
   * 根据对象查询门诊价目表会员价
   *
   * @param orgId 组织ID
   * @param memberType 会员类型
   * @param itemId 项目ID
   * @return ClinicTariffMemberPrice
   */
  @RequestMapping(value = "/rpc/clinic/tariff/param", method = RequestMethod.POST)
  ClinicTariffMemberPrice findClinicTariffMemberPrice(
      @RequestParam(value = "orgId") Integer orgId,
      @RequestParam(value = "memberType") Integer memberType,
      @RequestParam(value = "itemId") Integer itemId);

  /**
   * 根据对象查询门诊商品会员价
   *
   * @param orgId 组织ID
   * @param memberType 会员类型
   * @param itemId 项目ID
   * @return ClinicOralTariffMemberPrice
   */
  @RequestMapping(value = "/rpc/clinic/oral/param", method = RequestMethod.POST)
  ClinicOralTariffMemberPrice findClinicOralTariffMemberPrice(
      @RequestParam(value = "orgId") Integer orgId,
      @RequestParam(value = "memberType") Integer memberType,
      @RequestParam(value = "itemId") Integer itemId);

  /**
   * 根据挂号记录ID查询挂号记录
   *
   * @param id 挂号记录ID
   * @return Registered
   */
  @RequestMapping(value = "/rpc/registered/one/{id}", method = RequestMethod.GET)
  Registered findRegisteredById(@PathVariable(value = "id") Integer id);

  /**
   * 根据挂号对象查询挂号信息
   *
   * @param entity 挂号对象
   * @return Registered
   */
  @RequestMapping(value = "/rpc/registered/example", method = RequestMethod.POST)
  Registered findRegisteredByExample(@RequestBody Registered entity);

  /**
   * 根据条件查询挂号记录列表
   *
   * @param entity 挂号记录
   * @return * @param entity 挂号记录
   */
  @RequestMapping(value = "/rpc/registered/list", method = RequestMethod.POST)
  List<Registered> findRegisteredList(@RequestBody Registered entity);

  /**
   * 根据就诊记录ID查询就诊记录
   *
   * @param id 就诊记录ID
   * @return TreatmentRecord
   */
  @RequestMapping(value = "/rpc/treatment/one/{id}", method = RequestMethod.GET)
  TreatmentRecord findTreatmentRecordById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询就诊记录信息
   *
   * @param entity 就诊记录
   * @return TreatmentRecord
   */
  @RequestMapping(value = "/rpc/treatment/example", method = RequestMethod.POST)
  TreatmentRecord findTreatmentRecordByExample(@RequestBody TreatmentRecord entity);

  /**
   * 根据就诊记录ID列表查询就诊记录列表
   *
   * @param ids 就诊记录ID列表
   * @return List<TreatmentRecordExtendVO>
   */
  @RequestMapping(value = "/rpc/treatment/section", method = RequestMethod.POST)
  List<TreatmentRecordExtendVO> findTreatmentRecordByIds(@RequestBody @NotEmpty Set<Integer> ids);

  /**
   * 根据条件查询就诊记录列表
   *
   * @param entity 就诊记录
   * @return List<TreatmentRecord>
   */
  @RequestMapping(value = "/rpc/treatment/list", method = RequestMethod.POST)
  List<TreatmentRecord> findTreatmentRecordList(@RequestBody TreatmentRecord entity);

  /**
   * 修改就诊记录病历书写状态
   *
   * @param id 就诊记录ID
   */
  @RequestMapping(value = "/rpc/treatment/modify/{id}", method = RequestMethod.GET)
  void updateTreatmentRecord(@PathVariable(value = "id") Integer id);

  /**
   * 根据开单记录ID查询开单记录
   *
   * @param id 开单记录ID
   * @return OrderRecord
   */
  @RequestMapping(value = "/rpc/order/one/{id}", method = RequestMethod.GET)
  OrderRecord findOrderRecordById(@PathVariable(value = "id") Integer id);

  /**
   * 根据开单记录ID查询开单明细列表
   *
   * @param orderRecordId 开单记录ID
   * @return List<OrderDetail>
   */
  @RequestMapping(value = "/rpc/order/detail/list/{orderRecordId}", method = RequestMethod.GET)
  List<OrderDetail> findOrderDetailByOrderRecordId(
      @PathVariable(value = "orderRecordId") Integer orderRecordId);

  /**
   * 通过患者ID批量查询患者欠费总额
   *
   * @param patientIds 患者ID
   * @return 返回患者欠费集合
   */
  @RequestMapping(value = "/rpc/patient/debt/amount", method = RequestMethod.POST)
  List<DebtAmountModel> findDebtAmountList(@RequestBody List<Integer> patientIds);

  /**
   * 通过挂号ID批量查询挂号信息
   *
   * @param registeredIds 挂号ID
   * @return 返回挂号信息集合
   */
  @RequestMapping(value = "/rpc/patient/registered/list", method = RequestMethod.POST)
  List<RegisteredVO> registeredInfoDetails(@RequestBody List<Integer> registeredIds);

  /**
   * 查询完成的业务目标
   *
   * @param query 查询条件
   * @return CompletedBusinessWorkGoalVO
   */
  @RequestMapping(value = "/rpc/business/goal", method = RequestMethod.POST)
  BusinessCompletedWorkGoalVO findClinicCompletedBusinessWorkGoal(
      @RequestBody @Validated CompletedWorkGoalQuery query);

  /**
   * 根据条件查询门诊营业收入完成情况
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/rpc/business/goal/completed", method = RequestMethod.POST)
  BigDecimal findBusinessIncomeCompletedCount(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query);

  /**
   * 根据条件查询门诊工作量完成情况
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/rpc/business/workload/completed", method = RequestMethod.POST)
  BigDecimal findBusinessWorkloadCompletedCount(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query);

  /**
   * 根据条件查询门诊初诊人数完成情况
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/rpc/business/first/treat/completed", method = RequestMethod.POST)
  BigDecimal findBusinessFirstTreatCompletedCount(
      @RequestBody @Validated BusinessGoalCompletedInfoQuery query);

  /**
   * 根据条件查询门诊开单专科项目完成信息
   *
   * @param query 查询条件
   * @return SpecialistProjectTariffCompletedInfoVO
   */
  @RequestMapping(value = "/rpc/tariff/completed/goal", method = RequestMethod.POST)
  SpecialistProjectTariffCompletedInfoVO findClinicTariffOrderCompletedInfo(
      @RequestBody @Validated SpecialistProjectTariffCompletedInfoQuery query);

  /**
   * 查询专科项目数量
   *
   * @param specialistProjectReportModel 查询条件
   * @return Integer
   */
  @RequestMapping(value = "/rpc/tariff/specialist/percentage", method = RequestMethod.POST)
  List<SpecialistProjectReportVO> findTariffSpecialistPercentage(
      @RequestBody @Validated SpecialistProjectReportModel specialistProjectReportModel);

  /**
   * 根据支付方式统计账单的入账金额
   *
   * @param cashReceiptQuery 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/rpc/bill/sumBillPayAmount", method = RequestMethod.POST)
  BigDecimal sumBillPayAmount(@RequestBody CashReceiptOrRefundQuery cashReceiptQuery);

  /**
   * 根据条件查询账单退费退费现金总额
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  @RequestMapping(value = "/rpc/bill/refund/cash", method = RequestMethod.POST)
  BigDecimal findBillRefundTotalCashAmount(@RequestBody @Validated CashReceiptOrRefundQuery query);

  /**
   * 根据条件查询专科项目完成数量
   *
   * @param query 查询条件
   * @return Integer
   */
  @RequestMapping(value = "/rpc/tariff/specialist/completed", method = RequestMethod.POST)
  Integer findSpecialistProjectCompletedCount(
      @RequestBody @Validated SpecialistProjectCompletedCountQuery query);

  /**
   * 查询患者末次就诊记录
   *
   * @param patientId 患者ID
   * @return 就诊记录信息
   */
  @RequestMapping(value = "/rpc/treat/last/{patientId}", method = RequestMethod.GET)
  LastTreatmentInfoVO findLastTreatmentRecord(@PathVariable(value = "patientId") Integer patientId);

  /**
   * 查询患者末次就诊记录 批量
   *
   * @param patientIds 患者ID
   * @return 就诊信息
   */
  @RequestMapping(value = "/rpc/treat/last/batch", method = RequestMethod.POST)
  List<LastTreatmentInfoVO> lastTreatmentInfoByBatch(@RequestBody List<Integer> patientIds);

  /**
   * 根据预约ID查询患者接诊记录
   *
   * @param appointIds 预约记录
   * @return List<TreatmentRecord>
   */
  @RequestMapping(value = "/rpc/treatment/by/appointIds", method = RequestMethod.POST)
  public List<TreatmentRecord> findTreatmentRecordListByAppointIds(@RequestBody List<Integer> appointIds);

  /**
   * 根据条件查询专科项目完成数量
   *
   * @param query 查询条件
   * @return Integer
   */
  @RequestMapping(value = "/rpc/tariff/specialist/completeds", method = RequestMethod.POST)
  List<OrderDetail> findSpecialistProjectCompletedList(SpecialistProjectCompletedCountQuery query);

  /**
   * 根据条件查询患者就诊记录列表
   *
   * @param queryForm 查询条件
   * @return resultList
   */
  @PostMapping(value = "/rpc/patient/list", name = "患者就诊记录列表")
  PageInfo<PatientTreatmentRecordVO> patientTreatmentRecordList(
          @RequestBody @Validated PatientTreatmentRecordQueryForm queryForm);

  /**
   * 根据就诊ID查询开单信息
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  @GetMapping("/rpc/list/{treatmentRecordId}")
  OrderDetailInfoVO findOrderInfoByTreatmentId(
          @PathVariable(value = "treatmentRecordId") Integer treatmentRecordId);

  /**
   * 患者就诊次数
   * @param patientId 患者ID
   * @return 返回就诊次数
   */
  @GetMapping("/rpc/{patientId}/treatment/times")
  int patientTreatmentTimes(@PathVariable(value = "patientId") Integer patientId);


  /**
   * 根据多个商品表ID查询商品表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  @RequestMapping(value = "/rpc/oral/name", method = RequestMethod.POST)
  String findBaseOralNamesByIds(@RequestBody @NotEmpty String[] ids);

  /**
   * 根据条件查询商品表or价目表门诊折扣价
   *
   * @param query 字符串ID
   * @return ClinicItemPriceVO
   */
  @RequestMapping(value = "/rpc/clinic/itemMemberPrice", method = RequestMethod.POST)
  List<ClinicItemPriceVO> findClinicItemMemberPrice(@Validated @RequestBody ClinicMemberPriceQuery query);

  /**
   * 根据订单明细id查询订单明细列表
   *
   * @param orderDetailIds
   * @return
   */
  @PostMapping(value = "/rpc/order/detail/ids")
  List<OrderDetailVO> findOrderDetailById(@Validated @RequestBody List<Integer> orderDetailIds);

  @RequestMapping(value = "/rpc/mini/goods/page", method = RequestMethod.POST)
  public PageInfo<GoodsVO> pageGoods(@Validated @RequestBody GoodsQuery query);

  /**
   * 根据商品ID集合查询商品集合
   *
   * @param ids 商品ids
   * @return List<BaseOralTariff>
   */
  @RequestMapping(value = "/rpc/list/oral", method = RequestMethod.POST)
  List<ProductBO> listOnSaleOral(@NotEmpty @RequestBody Collection<Integer> ids);

  @RequestMapping(value = "/rpc/goods/lock/stock", method = RequestMethod.POST)
  void lockGoodsStock(@Valid @RequestBody List<LockStockForm> form);

  @RequestMapping(value = "/rpc/goods/free/stock", method = RequestMethod.POST)
  void freeGoodsStock(@Valid @RequestBody List<FreeStockForm> form);

  @PostMapping("/rpc/goods/productType/list")
  List<ProductTypeVO> findList(@RequestBody ProductTypeQueryForm queryForm);

  /**
   * 根据就诊id获取就诊和开单信息
   *
   * @param treatmentId
   * @return
   */
  @GetMapping("/rpc/treatmentOrder/{treatmentRecordId}")
  TreatmentOrderVO findTreatmentOrderByTreatmentId(@PathVariable(value = "treatmentRecordId") Integer treatmentId);
}
