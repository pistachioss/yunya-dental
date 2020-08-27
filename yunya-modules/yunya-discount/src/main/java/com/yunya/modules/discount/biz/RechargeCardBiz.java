package com.yunya.modules.discount.biz;

import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.RechargeCard;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.RechargeCardMapper;
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
 * @create 2020-07-17 10:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RechargeCardBiz extends BaseBiz<RechargeCardMapper, RechargeCard> {
    // 代金券类型
    private static final Integer RECHARGE_CARD_TYPE = 3;
    // 状态
    private static final Integer FINISH = 1;
    private static final Integer PLAN = 0;

    @Autowired private CouponAllocateMapper couponAllocateMapper;

    /**
     * 新增
     *
     * @param rechargeCard
     */
    public Integer saveRechargeCard(RechargeCard rechargeCard) {
        RechargeCard data = new RechargeCard();
//        data.setName(rechargeCard.getName());
        if (mapper.selectOne(data) != null) {
            throw new BaseException("充值卡名称已被占用", NAME_IS_OCCUPIED);
        }

        insertSelective(rechargeCard);
        return rechargeCard.getId();
    }

    /**
     * 修改
     *
     * @param discountUpdateForm
     */
    public void updateRechargeCard(DiscountUpdateForm discountUpdateForm) {
        Integer id = discountUpdateForm.getId();
        boolean flag = true;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(discountUpdateForm.getId());
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        RechargeCard rechargeCard = new RechargeCard();
        if (flag) {
            // 只能修改时间
//            rechargeCard.setSellingStartDate(discountUpdateForm.getSellingStartDate());
//            rechargeCard.setSellingEndDate(discountUpdateForm.getSellingEndDate());
//            rechargeCard.setActivationDeadline(discountUpdateForm.getActivationDeadline());
        } else {
            // 重名判断
            String name = discountUpdateForm.getName();
            if (StringUtils.isNotBlank(name)) {
                RechargeCard data = new RechargeCard();
//                data.setName(name);
                if (mapper.select(data).size() >= 2) {
                    throw new BaseException("充值卡名称已被占用", NAME_IS_OCCUPIED);
                }
            }
            rechargeCard = EntityUtils.build(discountUpdateForm, RechargeCard.class);
        }
        updateSelectiveById(rechargeCard);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteRechargeCard(Integer id) {
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", ExceptionCode.CARD_EXIST);
        }
        deleteById(id);

    }

}
