package com.yunya.report.ultimate.rpc;

import com.yunya.feign.report.domain.vo.BenefitItemVo;
import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
import com.yunya.report.ultimate.biz.DiscountBiz;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api("报表服务接口暴露")
@RestController
@RequestMapping("api")
public class ReportServiceRest {
    @Resource
    private DiscountBiz discountBiz;

    @PostMapping("/card/{cardId}/item/usage")
    public List<BenefitItemVo> listWxCouponsUseItem(@PathVariable(value = "cardId") Integer cardId) {
        return discountBiz.listWxCouponsUseItem(cardId);
    }

    @PostMapping("/card/{noticeType}/push/list")
    List<WxTemplateMsgModel> listPushCard(@PathVariable(value = "noticeType") Integer noticeType) {
        return discountBiz.pushCard(noticeType);
    }
}
