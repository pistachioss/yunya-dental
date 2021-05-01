package com.yunya.modules.discount.rpc;

import com.yunya.feign.discount.domain.form.*;
import com.yunya.feign.discount.domain.model.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.emr.domain.bo.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.framework.common.annation.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya.modules.discount.biz.*;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.*;
import javax.validation.*;
import java.math.*;
import java.util.*;

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
}
