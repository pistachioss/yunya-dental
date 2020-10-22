package com.yunya.modules.discount.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.CouponFileInfo;
import com.yunya.models.discount.PackageCoupon;
import com.yunya.modules.discount.form.PackageCouponForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.PackageCouponMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * yanlgiuxu
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PackageCouponBiz extends BaseBiz<PackageCouponMapper, PackageCoupon> {
    // 兑换券编码类型
    private static final String PACKAGE_COUPON_TYPE = "DH";

    @Autowired private CouponAllocateMapper couponAllocateMapper;
    @Autowired private CouponCommonInfoMapper couponCommonInfoMapper;
    @Autowired private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired private CouponFileInfoBiz couponFileInfoBiz;
    /**
     * 新增
     *
     * @param packageCouponForm
     */
    public Integer savePackageCoupon(PackageCouponForm packageCouponForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setName(packageCouponForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("兑换券名称与系统中已有兑换券重复，不允许新增!", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(packageCouponForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("2"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        couponCommonInfoBiz.insertSelective(couponCommonInfo);//基础信息表中插入数据

        PackageCoupon packageCoupon = new  PackageCoupon();
        BeanUtils.copyProperties(packageCouponForm, packageCoupon);
        packageCoupon.setCouponId(couponCommonInfo.getId());
        packageCoupon.setUseableClinic(packageCouponForm.getUseableClinic());
        insertSelective(packageCoupon);//插入卡券信息

        if(packageCoupon.getId()<10000){//同一种卡券最多添加9999个
        String num = String.format("%04d", packageCoupon.getId());
        couponCommonInfo.setCouponCode(PACKAGE_COUPON_TYPE + num);
        couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//插入卡券编码
        }else{
            throw new BaseException("已超过系统允许新增兑换券产品的最大数量9999，不允许新增！", INSERT_MODEL);
        }

        return couponCommonInfo.getId();

    }

    /**
     * 修改
     *
     * @param packageCouponForm
     */
    public void updatePackageCoupon(PackageCouponForm packageCouponForm) {
        boolean flag = true;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(packageCouponForm.getId());
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        PackageCoupon packageCoupon = new PackageCoupon();
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        if (flag) {
            // 只能修改时间
            couponCommonInfo.setId(packageCouponForm.getId());
            couponCommonInfo.setAvailableSaleStartDate(packageCouponForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(packageCouponForm.getAvailableSaleEndDate());
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//更新基础信息
            packageCoupon.setCouponId(packageCouponForm.getId());
            packageCoupon = selectOne(packageCoupon);
            if(packageCoupon!=null){
                packageCoupon.setUseableClinic(packageCouponForm.getUseableClinic());
                packageCoupon.setRemark(packageCouponForm.getRemark());
                packageCoupon.setActivationDeadline(packageCouponForm.getActivationDeadline());
//                packageCoupon.setWorkloadRate(packageCouponForm.getWorkloadRate());
                packageCoupon.setEffectiveDays(packageCouponForm.getEffectiveDays());
                updateSelectiveById(packageCoupon);//更新明细信息
            }else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }

        } else {
            // 重名判断
            String name = packageCouponForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 1) {
                data = new CouponCommonInfo();
                data.setId(packageCouponForm.getId());
                if(!couponCommonInfoMapper.selectOne(data).getName().equals(name)){
                    throw new BaseException("兑换券名称与系统中已有兑换券重复，不允许修改!", NAME_IS_OCCUPIED);
                }
            }
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(data);
            BeanUtils.copyProperties(copy, couponCommonInfo);
            BeanUtils.copyProperties(packageCouponForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            //基础信息表中修改数据
            couponCommonInfoBiz.updateById(couponCommonInfo);
            packageCoupon.setCouponId(packageCouponForm.getId());
            packageCoupon = selectOne(packageCoupon);
            if (packageCoupon != null) {
                PackageCoupon pc = new PackageCoupon();
                BeanUtils.copyProperties(packageCoupon, pc);
                BeanUtils.copyProperties(packageCouponForm, pc);
                pc.setId(packageCoupon.getId());
                //更新明细信息
                updateById(pc);
            } else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        }

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
        if (!couponAllocateMapper.select(couponAllocate).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", DELETE_NOT_ALLOW);
        }
        PackageCoupon packageCoupon = new PackageCoupon();
        packageCoupon.setCouponId(id);
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(id);
        couponCommonInfoBiz.deleteById(id);//删除卡券公用信息
        delete(packageCoupon);//删除兑换券卡券信息
        couponFileInfoBiz.delete(couponFiledelete);//清除图片文档信息
    }

}
