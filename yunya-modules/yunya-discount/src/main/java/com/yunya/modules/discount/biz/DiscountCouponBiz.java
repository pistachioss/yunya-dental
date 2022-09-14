package com.yunya.modules.discount.biz;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.form.DiscountCouponForm;
import com.yunya.modules.discount.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.*;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-14 17:26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DiscountCouponBiz extends BaseBiz<DiscountCouponMapper, DiscountCoupon> {
    // 折扣券编码类型
    private static final String DISCOUNT_COUPON_TYPE = "ZK";

    @Autowired
    private CouponAllocateMapper couponAllocateMapper;
    @Autowired
    private CouponCommonInfoMapper couponCommonInfoMapper;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired
    private CouponFileInfoBiz couponFileInfoBiz;
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    /**
     *  新增折扣券
     * @param discountCouponForm
     * @return
     */
    public Integer saveDiscountCoupon(DiscountCouponForm discountCouponForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setType((byte) CouponTypeEnum.DISCOUNT.getCode().intValue());
        data.setName(discountCouponForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("折扣券名称与系统中已有折扣券重复，不允许新增!", NAME_IS_OCCUPIED);
        }
        BigDecimal discountRate = discountCouponForm.getDiscountRate();
        int i = discountRate.compareTo(new BigDecimal("99.99"));
        if (i > 0) {
            throw new BaseException("折扣率不能大于100", PARAMETERS_IS_ILLEGAL);
        }

        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(discountCouponForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("1"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        //基础信息表中插入数据
        couponCommonInfoBiz.insertSelective(couponCommonInfo);

        DiscountCoupon discountCoupon = new DiscountCoupon();
        BeanUtils.copyProperties(discountCouponForm, discountCoupon);
        discountCoupon.setCouponId(couponCommonInfo.getId());
        discountCoupon.setUseableClinic(discountCouponForm.getUseableClinic());
        //插入卡券信息
        insertSelective(discountCoupon);
        //同一种卡券最多添加9999个
        if(selectCount(new DiscountCoupon())<10000){
        String num = String.format("%04d", discountCoupon.getId());
        couponCommonInfo.setCouponCode(DISCOUNT_COUPON_TYPE + num);
        //插入卡券编码
        couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);
        }else{
            throw new BaseException("已超过系统允许新增折扣券产品的最大数量9999，不允许新增！", INSERT_MODEL);
        }
        mqServiceFeign.sendMessage(couponCommonInfo.getId(), BusinessConstants.ADD, BaseCoupon);
        return couponCommonInfo.getId();
    }

    /**
     * 修改折扣券信息
     *
     * @param discountCouponForm
     */
    public void updateDiscountCoupon(DiscountCouponForm discountCouponForm) {
        boolean flag = false;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(discountCouponForm.getId());
        List<CouponAllocate> coList = couponAllocateMapper.select(couponAllocate);
        for(CouponAllocate fco:coList){
            if (fco.getAllocateUserId()!=null) {
                // 完成分配
                flag = true;
            }
        }
        DiscountCoupon discountCoupon = new DiscountCoupon();
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        if (flag) {
            couponCommonInfo.setId(discountCouponForm.getId());
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(couponCommonInfo);
            BeanUtils.copyProperties(copy, couponCommonInfo);

            couponCommonInfo.setAvailableSaleStartDate(discountCouponForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(discountCouponForm.getAvailableSaleEndDate());
            couponCommonInfo.setIsOnlineSale(discountCouponForm.getIsOnlineSale());
            //更新基础信息
            couponCommonInfoBiz.updateById(couponCommonInfo);
            couponCommonInfoBiz.removeHot(copy, discountCouponForm.getIsOnlineSale());
            discountCoupon.setCouponId(discountCouponForm.getId());
            discountCoupon = selectOne(discountCoupon);
            if (discountCoupon != null) {
                discountCoupon.setUseableClinic(discountCouponForm.getUseableClinic());
                discountCoupon.setRemark(discountCouponForm.getRemark());
                discountCoupon.setActivationDeadline(discountCouponForm.getActivationDeadline());
                discountCoupon.setWorkloadRate(discountCouponForm.getWorkloadRate());
                discountCoupon.setEffectiveDays(discountCouponForm.getEffectiveDays());
                //更新明细信息
                updateById(discountCoupon);
            } else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        } else {
            // 重名判断
            String name = discountCouponForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 1) {
                data = new CouponCommonInfo();
                data.setId(discountCouponForm.getId());
                //相等说明没改名字
                if (!couponCommonInfoMapper.selectOne(data).getName().equals(name)) {
                    throw new BaseException("折扣券名称与系统中已有折扣券重复，不允许修改!", NAME_IS_OCCUPIED);
                }
            }
            data = new CouponCommonInfo();
            data.setId(discountCouponForm.getId());
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(data);
            BeanUtils.copyProperties(copy, couponCommonInfo);
            BeanUtils.copyProperties(discountCouponForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            //基础信息表中修改数据
            couponCommonInfoBiz.updateById(couponCommonInfo);
            couponCommonInfoBiz.removeHot(copy, discountCouponForm.getIsOnlineSale());
            discountCoupon.setCouponId(discountCouponForm.getId());
            discountCoupon = selectOne(discountCoupon);
            if (discountCoupon != null) {
                DiscountCoupon dc = new DiscountCoupon();
                BeanUtils.copyProperties(discountCoupon, dc);
                BeanUtils.copyProperties(discountCouponForm, dc);
                dc.setId(discountCoupon.getId());
                //更新明细信息
                updateById(dc);
            } else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        }
        mqServiceFeign.sendMessage(couponCommonInfo.getId(), BusinessConstants.UPDATE, BaseCoupon);
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
//        if (!couponAllocateMapper.select(couponAllocate).isEmpty()) {
//            throw new BaseException("卡券已完成分配，无法删除", DELETE_NOT_ALLOW);
//        }
        DiscountCoupon discountCoupon = new DiscountCoupon();
        discountCoupon.setCouponId(id);
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(id);
        //删除卡券公用信息
        couponCommonInfoBiz.deleteById(id);
        //删除折扣券卡券信息
        delete(discountCoupon);
        //清除图片文档信息
        couponFileInfoBiz.delete(couponFiledelete);
        mqServiceFeign.sendMessage(id, BusinessConstants.DELETE, BaseCoupon);
    }

}
