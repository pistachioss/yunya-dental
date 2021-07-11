package com.yunya.modules.discount.rpc;

import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.discount.domain.vo.WxPatientEffectiveVo;
import com.yunya.feign.emr.domain.bo.RestErrorBo;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.discount.biz.BenefitBiz;
import com.yunya.modules.discount.biz.CardBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
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
}
