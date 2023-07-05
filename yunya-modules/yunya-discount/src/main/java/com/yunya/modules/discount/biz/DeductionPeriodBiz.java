package com.yunya.modules.discount.biz;

import com.yunya.feign.discount.domain.vo.DeductionItemPeriodVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DeductionItemPeriod;
import com.yunya.modules.discount.form.SpecialPackageCouponItemForm;
import com.yunya.modules.discount.mapper.DeductionItemPeriodMapper;
import com.yunya.modules.discount.mapper.VoucherDiscountItemMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.yunya.modules.discount.enums.CouponTypeEnum.DEDUCTION;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeductionPeriodBiz extends BaseBiz<DeductionItemPeriodMapper, DeductionItemPeriod> {
    @Resource
    private VoucherDiscountItemMapper voucherDiscountItemMapper;
    @Resource
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Resource
    private CardBiz cardBiz;

    public void save(List<SpecialPackageCouponItemForm> form) {
        Date date = new Date();
        if (CollectionUtils.isNotEmpty(form)) {
            CouponCommonInfo commonInfo = couponCommonInfoBiz.selectById(form.get(0).getCouponId());
            if (!DEDUCTION.equals(commonInfo.getType().intValue())) {
                return;
            }
            form.forEach(t -> {
                t.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
                t.setCrtTime(date);
            });
            voucherDiscountItemMapper.saveDeductionPeriod(form);
        }
    }

    public List<DeductionItemPeriodVO> list(Integer cardId) {
        Card card = cardBiz.selectById(cardId);
        //todo
        return null;
    }

    public List<DeductionItemPeriod> listByCoupon(Collection<Integer> couponIds, Date date) {
        return mapper.selectItem(couponIds, date);
    }
}
