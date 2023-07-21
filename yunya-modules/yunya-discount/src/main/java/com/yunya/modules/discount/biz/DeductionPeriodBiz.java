package com.yunya.modules.discount.biz;

import com.google.common.collect.Lists;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.DeductionItemPeriod;
import com.yunya.modules.discount.form.SpecialPackageCouponItemForm;
import com.yunya.modules.discount.mapper.DeductionItemPeriodMapper;
import com.yunya.modules.discount.mapper.VoucherDiscountItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.yunya.modules.discount.enums.CouponTypeEnum.DEDUCTION;

@Service
@Slf4j
public class DeductionPeriodBiz extends BaseBiz<DeductionItemPeriodMapper, DeductionItemPeriod> {
    @Resource
    private VoucherDiscountItemMapper voucherDiscountItemMapper;
    @Resource
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Resource
    private CardBiz cardBiz;

    @Transactional(rollbackFor = Exception.class)
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

    public List<DeductionItemPeriod> list(Integer cardId) {
        Card card = cardBiz.selectById(cardId);
        if (Objects.isNull(card)) {
            log.info("查询划扣信息，卡券不存在:{}", cardId);
            return Lists.newArrayList();
        }
        return listByCoupon(Collections.singletonList(card.getCouponId()), DateUtil.localDateTimeToDate(card.getSoldDate()));
    }

    public List<DeductionItemPeriod> listByCoupon(Collection<Integer> couponIds, Date date) {
        return mapper.selectItem(couponIds, date);
    }
}
