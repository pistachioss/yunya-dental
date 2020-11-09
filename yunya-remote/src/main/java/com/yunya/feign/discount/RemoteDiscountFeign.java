package com.yunya.feign.discount;

import com.yunya.feign.discount.domain.form.OwnCardActiveForm;
import com.yunya.feign.discount.domain.form.PatientChooseBenefitForm;
import com.yunya.feign.discount.domain.model.AuthDiscountBenefitModel;
import com.yunya.feign.discount.domain.model.PatientOrderBenefitModel;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.discount.domain.vo.PatientOrderBenefitVo;
import com.yunya.feign.discount.factory.RemoteDiscountFallBackFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(
        name = YunyaServiceNameConstants.YUNYA_DISCOUNT,
        fallbackFactory = RemoteDiscountFallBackFactory.class)
public interface RemoteDiscountFeign {

    @ApiOperation(value = "患者档案-产品管理-激活-自有平台激活")
    @PutMapping("/patient/{patientId}/product/card/owner/activation")
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
    public List<OrderBenefitDetailVo> getOrderBenefitD(@PathVariable(value = "orderId") Integer orderId);
}
