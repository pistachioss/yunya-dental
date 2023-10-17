package com.yunya.feign.discount;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.form.*;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.MixMatchBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.query.CardSaleQuery;
import com.yunya.feign.discount.domain.query.CouponCommonInfoQuery;
import com.yunya.feign.discount.domain.query.DiscountCouponQuery;
import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.discount.factory.RemoteDiscountFallBackFactory;
import com.yunya.feign.ivy_mini.domain.bo.ProductBO;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.VirtualDetailVO;
import com.yunya.feign.ivy_mini.domain.vo.VirtualProductVO;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientEventVO;
import com.yunya.feign.report.domain.query.base.EmployeeDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.feign.treatment.domain.vo.ClinicTariffDiscountCouponVO;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_DISCOUNT,
        fallbackFactory = RemoteDiscountFallBackFactory.class)
public interface RemoteDiscountFeign {

    @ApiOperation(value = "患者档案-产品管理-激活-自有平台激活")
    @PutMapping("/{patientId}/product/card/owner/activation")
    public ResponseResult ownActiveCard(@PathVariable(value = "patientId") Integer patientId, @Valid @RequestBody OwnCardActiveForm form);

    @ApiOperation(value = "收费-选择优惠")
    @PostMapping("/order/choice/benefit")
    public ResponseResult<PatientOrderBenefitVo> choiceBenefit(@Valid @RequestBody PatientChooseBenefitForm form);

    @ApiOperation(value = "收费-确认收费-保存优惠券优惠")
    @PostMapping("/order/benefit/save")
    public ResponseResult saveCardBenefit(@Valid @RequestBody PatientOrderBenefitModel model);

    @ApiOperation(value = "收费-确认收费-保存授权折扣优惠")
    @PostMapping("/order/benefit/authorization/save")
    public ResponseResult saveAuthBenefit(@Valid @RequestBody AuthDiscountBenefitModel model);

    @ApiOperation(value = "查询订单优惠明细")
    @GetMapping("/benefit/{orderId}")
    PatientOrderBenefitVo getOrderBenefitD(@PathVariable(value = "orderId") Integer orderId);

    @ApiOperation(value = "撤销优惠")
    @GetMapping("/benefit/revoke/{orderId}")
    public ResponseResult revokeBenefit(@PathVariable(value = "orderId") Integer orderId);

    /**
     * 根据支付方式统计卡券的售出金额
     *
     * @param saleCashReceiptQuery
     * @return
     */
    @RequestMapping(value = "/card/sumCardSoldAmount", method = RequestMethod.POST)
    BigDecimal sumCardSoldAmount(@RequestBody CashReceiptOrRefundQuery saleCashReceiptQuery);

    @PostMapping("/patient/{patientId}/effective/card")
    List<WxPatientEffectiveVo> getPatientEffectCardList(@PathVariable(value = "patientId") Integer patientId);

    @PostMapping("/card/{cardId}/usage")
    WxCardUsageVo getUserCardUsage(@PathVariable(value = "cardId") Integer cardId);

    @GetMapping("/patient/{patientId}/cards")
    List<Integer> listPatientAllCard(@PathVariable(value = "patientId") Integer patientId);

    /**
     * 查询门诊项目分类的优惠金额合计和补入工作量合计
     *
     * @param query
     * @return
     */
    @PostMapping("/benefit/tariffCategory/discountCoupon")
    List<ClinicTariffDiscountCouponVO> findClinicTariffCategoryDiscountCoupon(@RequestBody DiscountCouponQuery query);

    @RequestMapping(value = "/mini/virtual/page", method = RequestMethod.POST)
    PageInfo<VirtualProductVO> pageVirtual(@Validated @RequestBody VirtualProductQuery query);

    @RequestMapping(value = "/mini/coupon/{couponId}", method = RequestMethod.GET)
    VirtualDetailVO couponDetail(@PathVariable(value = "couponId") Integer couponId);

    @PostMapping("/productType/list")
    ResponseResult<PageInfo<ProductTypeVO>> findList(@RequestBody ProductTypeQueryForm queryForm);

    @RequestMapping(value = "/coupon/list/ids", method = RequestMethod.POST)
    List<ProductBO> listOnSaleOral(@NotEmpty @RequestBody Collection<Integer> ids);

    @PostMapping(value = "/coupon/list")
    ResponseResult<PageInfo<CouponCommonInfoVO>> findCouponList(@RequestBody CouponCommonInfoQuery query);

    @RequestMapping(value = "/coupon/lock/stock", method = RequestMethod.POST)
    void lockVirtualStock(@Valid @RequestBody List<LockStockForm> form);

    @RequestMapping(value = "/coupon/free/stock", method = RequestMethod.POST)
    void freeVirtualStock(@Valid @RequestBody List<FreeStockForm> form);

    @PostMapping("/coupon/sale/card/page")
    ResponseResult<PageInfo<CardSalePageVo>> getCardSalePageVo(@Valid @RequestBody CardSaleQuery query);

    @RequestMapping(value = "/card/use", method = RequestMethod.POST)
    boolean whetherUseCard(@NotEmpty @RequestBody List<Integer> cardIds);

    @PostMapping("/coupon/card/QRCode/batch/init")
    List<CardQrCodeVo> batchCardQrCode(@NotEmpty @RequestBody List<Integer> cardIds);

    @PostMapping("/coupon/card/cancel/batch")
    void batchCancelCard(@RequestBody BatchCancelCardForm form);

    @PutMapping("/mini/coupon/card/sale")
    ResponseResult miniSoldCard(@Valid @RequestBody MiniCardSoldForm form);

    @DeleteMapping("/mini/card/delete/batch")
    void deleteCard(@RequestBody List<Integer> cardIds);

    /**
     * 根据患者id查询患者卡券轨迹
     *
     * @param patientId
     * @return
     */
    @GetMapping("/card/trajectory/{patientId}")
    List<PatientEventVO> findPatientCardTrajectory(@PathVariable(value = "patientId") Integer patientId);

    /**
     * 查询患者最新激活的产品卡券
     *
     * @param patientId
     * @return
     */
    @GetMapping("/card/lastestActived/{patientId}")
    PatientCardBaseVo findPatientLastestActivedCardInfo(@PathVariable(value = "patientId") Integer patientId);

    @ApiOperation("混搭优惠明细保存")
    @PostMapping("/order/benefit/mixMatch/save")
    ResponseResult saveMixMatchBenefit(@Valid @RequestBody MixMatchBenefitModel model);

    @GetMapping("/card/listTwoYearsActive")
    Map<Integer, Long> listTwoYearsActive();

    @ApiOperation("卡券账单现金结存")
    @RequestMapping(value = "/coupon/bill/cashBalance", method = RequestMethod.POST)
    BigDecimal sumCouponBillCashBalanceReceipt(
            @RequestBody @Validated CashReceiptOrRefundQuery query);

    /**
     * 根据条件查询员工的剩余年度授权折扣额度
     *
     * @param query
     * @return
     */
    @ApiOperation("根据条件查询员工的剩余年度授权折扣额度")
    @PostMapping("/accredit/discount/total")
    BigDecimal findAccreditDiscountAmountByQuery(@RequestBody @Validated EmployeeDateRangeQueryForm query);
}
