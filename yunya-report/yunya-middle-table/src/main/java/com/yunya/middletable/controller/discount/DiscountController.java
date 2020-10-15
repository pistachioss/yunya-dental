package com.yunya.middletable.controller.discount;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.discount.BaseCouponServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscountController {

    @Autowired
    BaseCouponServiceImpl discountService;

    @PostMapping("/discount/pull")
    public ResponseResult pullData(@RequestBody PullForm form){
        discountService.pullCoupon(form.getStartDate(), form.getEndDate());
        return ResponseUtil.success();
    }
}
