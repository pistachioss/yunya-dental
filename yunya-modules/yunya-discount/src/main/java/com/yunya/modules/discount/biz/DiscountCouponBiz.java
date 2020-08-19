package com.yunya.modules.discount.biz;

import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.DiscountCoupon;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.DiscountCouponMapper;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.modules.discount.constant.ExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    // 折扣券类型
    private static final Integer DISCOUNT_COUPON_TYPE = 0;
    // 状态
    private static final Integer FINISH = 1;
    private static final Integer PLAN = 0;

    @Autowired private CouponAllocateMapper couponAllocateMapper;

    /**
     * 新增折扣券
     *
     * @param discountCoupon
     */
    public Integer saveDiscountCoupon(DiscountCoupon discountCoupon) {
        DiscountCoupon data = new DiscountCoupon();
//        data.setName(discountCoupon.getName());
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
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(discountUpdateForm.getId());
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        DiscountCoupon discountCoupon = new DiscountCoupon();
        if (flag) {
            // 只能修改时间
//            discountCoupon.setSellingStartDate(discountUpdateForm.getSellingStartDate());
//            discountCoupon.setSellingEndDate(discountUpdateForm.getSellingEndDate());
            discountCoupon.setEffectiveDays(discountUpdateForm.getEffectiveDays());
            discountCoupon.setActivationDeadline(discountUpdateForm.getActivationDeadline());
        } else {
            // 重名判断
            String name = discountUpdateForm.getName();
            if (StringUtils.isNotBlank(name)) {
                DiscountCoupon data = new DiscountCoupon();
//                data.setName(name);
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
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", ExceptionCode.CARD_EXIST);
        }
        deleteById(id);
    }

//    /**
//     * 查询列表
//     *
//     * @param discountQueryForm
//     * @return
//     */
//    public List<DiscountVO> search(DiscountQueryForm discountQueryForm) {
//        return mapper.selectVOs(discountQueryForm.getMarketProductTypeId(), discountQueryForm.getName(),
//                discountQueryForm.getStartDate(), discountQueryForm.getEndDate());
//    }
}
