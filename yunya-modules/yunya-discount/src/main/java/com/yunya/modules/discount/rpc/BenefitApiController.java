package com.yunya.modules.discount.rpc;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.form.*;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.query.DiscountCouponQuery;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.ivy_mini.domain.bo.ProductBO;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.VirtualDetailVO;
import com.yunya.feign.ivy_mini.domain.vo.VirtualProductVO;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientEventVO;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.feign.treatment.domain.vo.ClinicTariffDiscountCouponVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/10/9
 */
@Api(tags = {"收费使用优惠api"})
@RestController
public class BenefitApiController {

    @Resource
    private CardBiz cardBiz;

    @Resource
    private BenefitBiz benefitBiz;
    @Resource
    private CouponCommonInfoBiz couponBiz;

    @ApiOperation(value = "收费-选择优惠")
    @PostMapping("/order/choice/benefit")
    @CurrentUser
    public ResponseResult<PatientOrderBenefitVo> choiceBenefit(
            @Valid @RequestBody PatientChooseBenefitForm form) {
        return cardBiz.choiceBenefit(form);
    }

    @ApiOperation(value = "收费-确认收费-保存优惠券优惠")
    @PostMapping("/order/benefit/save")
    @CurrentUser
    public ResponseResult saveCardBenefit(@Valid @RequestBody PatientOrderBenefitModel model) {
        return benefitBiz.saveCardBenefit(model);
    }

    @ApiOperation(value = "收费-确认收费-保存授权折扣优惠")
    @PostMapping("/order/benefit/authorization/save")
    @CurrentUser
    public ResponseResult saveAuthBenefit(@Valid @RequestBody AuthDiscountBenefitModel model) {
        return benefitBiz.saveAuthBenefit(model);
    }

    @ApiOperation(value = "查询订单优惠明细")
    @GetMapping("/benefit/{orderId}")
    public List<OrderBenefitDetailVo> getOrderBenefitD(
            @PathVariable(value = "orderId") Integer orderId) {
        return benefitBiz.getOrderBenefit(orderId);
    }

    @ApiOperation(value = "撤销优惠")
    @GetMapping("/benefit/revoke/{orderId}")
    @CurrentUser
    public ResponseResult revokeBenefit(@PathVariable(value = "orderId") Integer orderId) {
        RestErrorBo errorBo = benefitBiz.revokeBenefit(orderId);
        if (errorBo.getError() != null) {
            return ResponseUtil.error(errorBo.getError());
        }
        return ResponseUtil.success();
    }

    @ApiOperation("卡券售卖现金收款")
    @RequestMapping(value = "/card/sumCardSoldAmount", method = RequestMethod.POST)
    public BigDecimal getCardSaleCashReceipt(
            @RequestBody @Validated CashReceiptOrRefundQuery query) {
        return cardBiz.findCardSaleCashReceipt(query);
    }

    @PostMapping("/patient/{patientId}/effective/card")
    public List<WxPatientEffectiveVo> getPatientEffectCardList(@PathVariable(value = "patientId") Integer patientId) {
        return cardBiz.getPatientEffectiveCard(patientId);
    }

    @PostMapping("/card/{cardId}/usage")
    public WxCardUsageVo getUserCardUsage(@PathVariable(value = "cardId") Integer cardId) {
        return cardBiz.getWxUserCardUsage(cardId);
    }

    @GetMapping("/patient/{patientId}/cards")
    public List<Integer> listPatientAllCard(@PathVariable(value = "patientId") Integer patientId) {
        return cardBiz.listPatientAllCard(patientId);
    }

    /**
     * 查询门诊项目分类的优惠金额合计和补入工作量合计
     *
     * @param queryForm
     * @return
     */
    @PostMapping("/benefit/tariffCategory/discountCoupon")
    public List<ClinicTariffDiscountCouponVO> findClinicTariffCategoryDiscountCoupon(@RequestBody DiscountCouponQuery queryForm) {
        return benefitBiz.findClinicTariffCategoryDiscountCoupon(queryForm);
    }

    @RequestMapping(value = "/mini/virtual/page", method = RequestMethod.POST)
    public PageInfo<VirtualProductVO> pageVirtual(@Validated @RequestBody VirtualProductQuery query){
        return couponBiz.pageVirtual(query);
    }

    @RequestMapping(value = "/mini/coupon/{couponId}", method = RequestMethod.GET)
    public VirtualDetailVO couponDetail(@PathVariable(value = "couponId") Integer couponId){
        return couponBiz.couponDetail(couponId);
    }

    @RequestMapping(value = "/coupon/list/ids", method = RequestMethod.POST)
    public List<ProductBO> listOnSaleOral(@NotEmpty @RequestBody Collection<Integer> ids){
        return couponBiz.listOnSaleOral(ids);
    }

    @RequestMapping(value = "/coupon/lock/stock", method = RequestMethod.POST)
    void lockVirtualStock(@Valid @RequestBody List<LockStockForm> form){
        couponBiz.lockVirtualStock(form);
    }

    @RequestMapping(value = "/coupon/free/stock", method = RequestMethod.POST)
    void freeVirtualStock(@Valid @RequestBody List<FreeStockForm> form) {
        couponBiz.freeVirtualStock(form);
    }

    @RequestMapping(value = "/card/use", method = RequestMethod.POST)
    boolean whetherUseCard(@NotEmpty @RequestBody List<Integer> cardIds) {
        return cardBiz.whetherUseCard(cardIds);
    }

    @PostMapping("/coupon/card/cancel/batch")
    public void batchCancelCard(@RequestBody BatchCancelCardForm form) {
        cardBiz.batchCancelCardSold(form);
    }

    @PutMapping("/mini/coupon/card/sale")
    public ResponseResult miniSoldCard(@Valid @RequestBody MiniCardSoldForm form) {
        return cardBiz.miniSoldCard(form);
    }

    @DeleteMapping("/mini/card/delete/batch")
    void deleteCard(@RequestBody List<Integer> cardIds){
        cardBiz.removeCardList(cardIds);
    }

    @GetMapping("/card/trajectory/{patientId}")
    public List<PatientEventVO> findPatientCardTrajectory(@PathVariable(value = "patientId") Integer patientId) {
        return cardBiz.findPatientCardTrajectory(patientId);
    }
}
