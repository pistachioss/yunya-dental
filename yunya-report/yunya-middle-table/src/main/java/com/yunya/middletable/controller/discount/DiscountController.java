package com.yunya.middletable.controller.discount;

import com.yunya.feign.middletable.domain.form.PullForm;
import com.yunya.middletable.service.discount.DiscountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscountController {

    @Autowired
    DiscountServiceImpl discountService;

    @RequestMapping("/discount/pull")
    public String pullData(PullForm form){
        return null;
    }
}
