package com.yunya.modules.discount.biz;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.CouponFileInfo;
import com.yunya.models.discount.DeductionCoupon;
import com.yunya.modules.discount.enums.CouponTypeEnum;
import com.yunya.modules.discount.form.DeductionCouponForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.DeductionPackageCouponMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCoupon;
import static com.yunya.framework.common.constant.OperationCodeConstants.INSERT_MODEL;
import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeductionCouponBiz extends BaseBiz<DeductionPackageCouponMapper, DeductionCoupon> {

    //划扣券编码类型
    private static final String DEDUCTION_PACKAGE_COUPON_TYPE = "HK";

    @Resource
    private CouponAllocateMapper couponAllocateMapper;
    @Resource private CouponCommonInfoMapper couponCommonInfoMapper;
    @Resource private CouponCommonInfoBiz couponCommonInfoBiz;
    @Resource private CouponFileInfoBiz couponFileInfoBiz;
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;

    /**
     * 新增
     *
     * @param deductionCouponForm
     */
    public Integer saveDeductionCoupon(DeductionCouponForm deductionCouponForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setType((byte) CouponTypeEnum.DEDUCTION.getCode().intValue());
        data.setName(deductionCouponForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("划扣券名称与系统中已有划扣券重复，不允许新增!", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(deductionCouponForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("5"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        //基础信息表中插入数据
        couponCommonInfoBiz.insertSelective(couponCommonInfo);

        DeductionCoupon deductionCoupon = new  DeductionCoupon();
        BeanUtils.copyProperties(deductionCouponForm, deductionCoupon);
        deductionCoupon.setCouponId(couponCommonInfo.getId());
        deductionCoupon.setUseableClinic(deductionCouponForm.getUseableClinic());
        //插入卡券信息
        insertSelective(deductionCoupon);
        //同一种卡券最多添加9999个
        if(selectCount(new DeductionCoupon())<10000){
        String num = String.format("%04d", deductionCoupon.getId());
        couponCommonInfo.setCouponCode(DEDUCTION_PACKAGE_COUPON_TYPE + num);
            //插入卡券编码
        couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);
        }else{
            throw new BaseException("已超过系统允许新增划扣券产品的最大数量9999，不允许新增！", INSERT_MODEL);
        }
        mqServiceFeign.sendMessage(couponCommonInfo.getId(), BusinessConstants.ADD, BaseCoupon);
        return couponCommonInfo.getId();
    }

    /**
     * 修改
     *
     * @param deductionCouponForm
     */
    public void updateDeductionCoupon(DeductionCouponForm deductionCouponForm) {
        boolean flag = false;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(deductionCouponForm.getId());
        List<CouponAllocate> coList = couponAllocateMapper.select(couponAllocate);
        for(CouponAllocate fco:coList){
            if (fco.getAllocateUserId()!=null) {
                // 完成分配
                flag = true;
            }
        }
        DeductionCoupon deductionCoupon = new DeductionCoupon();
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        if (flag) {
            // 只能修改时间
            couponCommonInfo.setId(deductionCouponForm.getId());
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(couponCommonInfo);
            BeanUtils.copyProperties(copy, couponCommonInfo);

            couponCommonInfo.setAvailableSaleStartDate(deductionCouponForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(deductionCouponForm.getAvailableSaleEndDate());
            couponCommonInfo.setIsOnlineSale(deductionCouponForm.getIsOnlineSale());
            //更新基础信息
            couponCommonInfoBiz.updateById(couponCommonInfo);
            couponCommonInfoBiz.removeHot(copy, deductionCouponForm.getIsOnlineSale());
            deductionCoupon.setCouponId(deductionCouponForm.getId());
            deductionCoupon = selectOne(deductionCoupon);
            if(deductionCoupon!=null){
                deductionCoupon.setUseableClinic(deductionCouponForm.getUseableClinic());
                deductionCoupon.setRemark(deductionCouponForm.getRemark());
                deductionCoupon.setActivationDeadline(deductionCouponForm.getActivationDeadline());
                deductionCoupon.setEffectiveDays(deductionCouponForm.getEffectiveDays());
                //更新明细信息
                updateById(deductionCoupon);
            }else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }

        } else {
            // 重名判断
            String name = deductionCouponForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 1) {
                data = new CouponCommonInfo();
                data.setId(deductionCouponForm.getId());
                if(!couponCommonInfoMapper.selectOne(data).getName().equals(name)){
                    throw new BaseException("划扣券名称与系统中已有划扣券重复，不允许修改!", NAME_IS_OCCUPIED);
                }
            }
            data = new CouponCommonInfo();
            data.setId(deductionCouponForm.getId());
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(data);
            BeanUtils.copyProperties(copy, couponCommonInfo);
            BeanUtils.copyProperties(deductionCouponForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            //基础信息表中修改数据
            couponCommonInfoBiz.updateById(couponCommonInfo);
            couponCommonInfoBiz.removeHot(copy, deductionCouponForm.getIsOnlineSale());
            deductionCoupon.setCouponId(deductionCouponForm.getId());
            deductionCoupon = selectOne(deductionCoupon);
            if (deductionCoupon != null) {
                DeductionCoupon pc = new DeductionCoupon();
                BeanUtils.copyProperties(deductionCoupon, pc);
                BeanUtils.copyProperties(deductionCouponForm, pc);
                pc.setId(deductionCoupon.getId());
                //更新明细信息
                updateById(pc);
            } else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        }
        mqServiceFeign.sendMessage(couponCommonInfo.getId(), BusinessConstants.UPDATE, BaseCoupon);
    }
    /**
     * 删除
     *
     * @param id
     */
    public void deletePackageCoupon(Integer id) {
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
//        if (!couponAllocateMapper.select(couponAllocate).isEmpty()) {
//            throw new BaseException("卡券已完成分配，无法删除", DELETE_NOT_ALLOW);
//        }
        DeductionCoupon deductionCoupon = new DeductionCoupon();
        deductionCoupon.setCouponId(id);
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(id);
        //删除卡券公用信息
        couponCommonInfoBiz.deleteById(id);
        //删除划扣券卡券信息
        delete(deductionCoupon);
        //清除图片文档信息
        couponFileInfoBiz.delete(couponFiledelete);
        mqServiceFeign.sendMessage(id, BusinessConstants.DELETE, BaseCoupon);
    }
}
