package com.clinic.discount.biz;

import com.clinic.discount.entity.CardClinic;
import com.clinic.discount.entity.DiscountCoupon;
import com.clinic.discount.form.DiscountQueryForm;
import com.clinic.discount.form.DiscountUpdateForm;
import com.clinic.discount.mapper.DiscountCouponMapper;
import com.clinic.discount.vo.DiscountVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.clinic.discount.constant.ExceptionCode.CARD_EXIST;
import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-14 17:26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DiscountCouponBiz extends BaseBiz<DiscountCouponMapper, DiscountCoupon> {
    // 代金券类型
    private static final Integer DISCOUNT_COUPON_TYPE = 0;
    // 状态
    private static final Integer FINISH = 1;
    private static final Integer PLAN = 0;

    @Autowired
    private CardClinicBiz cardClinicBiz;

    /**
     * 新增折扣券
     *
     * @param discountCoupon
     */
    public Integer saveDiscountCoupon(DiscountCoupon discountCoupon) {
        DiscountCoupon data = new DiscountCoupon();
        data.setName(discountCoupon.getName());
        if (mapper.selectOne(data) != null) {
            throw new BaseException("折扣券名字已被占用", NAME_IS_OCCUPIED);
        }

        insertSelective(discountCoupon);
        return discountCoupon.getId();
    }

    /**
     * 修改
     *
     * @param discountUpdateForm
     */
    public void updateDiscountCoupon(DiscountUpdateForm discountUpdateForm) {
        boolean flag = true;
        // 判断是否完成分配
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(discountUpdateForm.getId());
        cardClinic.setType(DISCOUNT_COUPON_TYPE);
        cardClinic.setStatus(FINISH);
        if (cardClinicBiz.selectList(cardClinic).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        DiscountCoupon discountCoupon = new DiscountCoupon();
        if (flag) {
            // 只能修改时间
            discountCoupon.setSellingStartDate(discountUpdateForm.getSellingStartDate());
            discountCoupon.setSellingEndDate(discountUpdateForm.getSellingEndDate());
            discountCoupon.setEffectiveDays(discountUpdateForm.getEffectiveDays());
            discountCoupon.setActivationDeadline(discountUpdateForm.getActivationDeadline());
        } else {
            // 重名判断
            String name = discountUpdateForm.getName();
            if (StringUtils.isNotBlank(name)) {
                DiscountCoupon data = new DiscountCoupon();
                data.setName(name);
                if (mapper.select(data).size() >= 2) {
                    throw new BaseException("折扣券名字已被占用", NAME_IS_OCCUPIED);
                }
            }
            discountCoupon = EntityUtils.build(discountUpdateForm, DiscountCoupon.class);
        }
        updateSelectiveById(discountCoupon);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteDiscountCoupon(Integer id) {
        // 判断是否完成分配
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(id);
        cardClinic.setType(DISCOUNT_COUPON_TYPE);
        cardClinic.setStatus(FINISH);
        if (!cardClinicBiz.selectList(cardClinic).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", CARD_EXIST);
        }

        deleteById(id);
        // 删除分配计划中的记录
        cardClinic.setStatus(PLAN);
        cardClinicBiz.delete(cardClinic);
    }

    /**
     * 查询列表
     *
     * @param discountQueryForm
     * @return
     */
    public List<DiscountVO> search(DiscountQueryForm discountQueryForm) {
        return mapper.selectVOs(discountQueryForm.getMarketProductTypeId(), discountQueryForm.getName(),
                discountQueryForm.getStartDate(), discountQueryForm.getEndDate());
    }
}
