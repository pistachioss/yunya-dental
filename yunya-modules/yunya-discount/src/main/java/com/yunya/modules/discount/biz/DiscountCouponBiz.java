package com.yunya.modules.discount.biz;

import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.form.CouponCommonInfoQueryForm;
import com.yunya.modules.discount.form.DiscountCouponForm;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.DiscountCouponMapper;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.modules.discount.constant.ExceptionCode;
import com.yunya.modules.discount.vo.CouponCommonInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DELETE_NOT_ALLOW;
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
    @Autowired private CouponCommonInfoMapper couponCommonInfoMapper;
    @Autowired private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired private CouponFileInfoBiz couponFileInfoBiz;
    /**
     * 新增折扣券
     *
     * @param discountCouponForm
     */
    public Integer saveDiscountCoupon(DiscountCouponForm discountCouponForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setName(discountCouponForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(discountCouponForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("1"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        couponCommonInfoBiz.insertSelective(couponCommonInfo);//基础信息表中插入数据

        DiscountCoupon discountCoupon = new DiscountCoupon();
        BeanUtils.copyProperties(discountCouponForm, discountCoupon);
        discountCoupon.setCouponId(couponCommonInfo.getId());
        discountCoupon.setUseableClinic(discountCouponForm.getUseableClinic());
        insertSelective(discountCoupon);//插入卡券信息
        return couponCommonInfo.getId();
    }

    /**
     * 修改折扣券信息
     *
     * @param discountCouponForm
     */
    public void updateDiscountCoupon(DiscountCouponForm discountCouponForm) {
        boolean flag = true;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(discountCouponForm.getId());
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        DiscountCoupon discountCoupon = new DiscountCoupon();
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        if (flag) {
            couponCommonInfo.setId(discountCouponForm.getId());
            couponCommonInfo.setAvailableSaleStartDate(discountCouponForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(discountCouponForm.getAvailableSaleEndDate());
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//更新基础信息
            discountCoupon.setCouponId(discountCouponForm.getId());
            discountCoupon = selectOne(discountCoupon);
            if(discountCoupon!=null){
                discountCoupon.setUseableClinic(discountCouponForm.getUseableClinic());
                discountCoupon.setRemark(discountCouponForm.getRemark());
                discountCoupon.setActivationDeadline(discountCouponForm.getActivationDeadline());
                discountCoupon.setWorkloadRate(discountCouponForm.getWorkloadRate());
                discountCoupon.setEffectiveDays(discountCouponForm.getEffectiveDays());
                updateSelectiveById(discountCoupon);//更新明细信息
            }else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        } else {
            // 重名判断
            String name = discountCouponForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 2) {
                throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
            }
            BeanUtils.copyProperties(discountCouponForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//基础信息表中修改数据
            discountCoupon.setCouponId(discountCouponForm.getId());
            discountCoupon = selectOne(discountCoupon);
            if (discountCoupon != null) {
                DiscountCoupon dc = new DiscountCoupon();
                BeanUtils.copyProperties(discountCouponForm, dc);
                dc.setId(discountCoupon.getId());
                updateSelectiveById(dc);//更新明细信息
            } else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        }

    }

    /**
     * 删除折扣券
     *
     * @param id
     */
    public void deleteDiscountCoupon(Integer id) {
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (!couponAllocateMapper.select(couponAllocate).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", DELETE_NOT_ALLOW);
        }
        DiscountCoupon discountCoupon = new DiscountCoupon();
        discountCoupon.setCouponId(id);
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(id);
        couponCommonInfoBiz.deleteById(id);//删除卡券公用信息
        delete(discountCoupon);//删除折扣券卡券信息
        couponFileInfoBiz.delete(couponFiledelete);//清除图片文档信息
    }

}
