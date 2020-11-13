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
import com.yunya.models.discount.SpecialPackageCoupon;
import com.yunya.modules.discount.form.SpecialPackageCouponForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.SpecialPackageCouponMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCoupon;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * @author 杨柳絮
 * @className SpecialPackageCouponBiz
 * @description
 * @date 2020/8/27 14:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SpecialPackageCouponBiz  extends BaseBiz<SpecialPackageCouponMapper, SpecialPackageCoupon> {

    // 套餐券编码类型
    private static final String SPECIAL_PACKAGE_COUPON_TYPE = "TC";

    @Autowired
    private CouponAllocateMapper couponAllocateMapper;
    @Autowired private CouponCommonInfoMapper couponCommonInfoMapper;
    @Autowired private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired private CouponFileInfoBiz couponFileInfoBiz;
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;

    /**
     * 新增
     *
     * @param specialPackageCouponForm
     */
    public Integer saveSpecialPackageCoupon(SpecialPackageCouponForm specialPackageCouponForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setName(specialPackageCouponForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("套餐券名称与系统中已有套餐券重复，不允许新增!", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(specialPackageCouponForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("3"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        //基础信息表中插入数据
        couponCommonInfoBiz.insertSelective(couponCommonInfo);

        SpecialPackageCoupon specialPackageCoupon = new  SpecialPackageCoupon();
        BeanUtils.copyProperties(specialPackageCouponForm, specialPackageCoupon);
        specialPackageCoupon.setCouponId(couponCommonInfo.getId());
        specialPackageCoupon.setUseableClinic(specialPackageCouponForm.getUseableClinic());
        //插入卡券信息
        insertSelective(specialPackageCoupon);
        //同一种卡券最多添加9999个
        if(specialPackageCoupon.getId()<10000){
        String num = String.format("%04d", specialPackageCoupon.getId());
        couponCommonInfo.setCouponCode(SPECIAL_PACKAGE_COUPON_TYPE + num);
            //插入卡券编码
        couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);
        }else{
            throw new BaseException("已超过系统允许新增套餐券产品的最大数量9999，不允许新增！", INSERT_MODEL);
        }
        mqServiceFeign.sendMessage(couponCommonInfo.getId(), BusinessConstants.ADD, BaseCoupon);
        return couponCommonInfo.getId();
    }

    /**
     * 修改
     *
     * @param specialPackageCouponForm
     */
    public void updateSpecialPackageCoupon(SpecialPackageCouponForm specialPackageCouponForm) {
        boolean flag = true;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(specialPackageCouponForm.getId());
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        SpecialPackageCoupon specialPackageCoupon = new SpecialPackageCoupon();
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        if (flag) {
            // 只能修改时间
            couponCommonInfo.setId(specialPackageCouponForm.getId());
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(couponCommonInfo);
            BeanUtils.copyProperties(copy, couponCommonInfo);

            couponCommonInfo.setAvailableSaleStartDate(specialPackageCouponForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(specialPackageCouponForm.getAvailableSaleEndDate());
            //更新基础信息
            couponCommonInfoBiz.updateById(couponCommonInfo);
            specialPackageCoupon.setCouponId(specialPackageCouponForm.getId());
            specialPackageCoupon = selectOne(specialPackageCoupon);
            if(specialPackageCoupon!=null){
                specialPackageCoupon.setUseableClinic(specialPackageCouponForm.getUseableClinic());
                specialPackageCoupon.setRemark(specialPackageCouponForm.getRemark());
                specialPackageCoupon.setActivationDeadline(specialPackageCouponForm.getActivationDeadline());
                specialPackageCoupon.setEffectiveDays(specialPackageCouponForm.getEffectiveDays());
                //更新明细信息
                updateById(specialPackageCoupon);
            }else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }

        } else {
            // 重名判断
            String name = specialPackageCouponForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 1) {
                data = new CouponCommonInfo();
                data.setId(specialPackageCouponForm.getId());
                if(!couponCommonInfoMapper.selectOne(data).getName().equals(name)){
                    throw new BaseException("套餐券名称与系统中已有套餐券重复，不允许修改!", NAME_IS_OCCUPIED);
                }
            }
            data = new CouponCommonInfo();
            data.setId(specialPackageCouponForm.getId());
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(data);
            BeanUtils.copyProperties(copy, couponCommonInfo);
            BeanUtils.copyProperties(specialPackageCouponForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            //基础信息表中修改数据
            couponCommonInfoBiz.updateById(couponCommonInfo);
            specialPackageCoupon.setCouponId(specialPackageCouponForm.getId());
            specialPackageCoupon = selectOne(specialPackageCoupon);
            if (specialPackageCoupon != null) {
                SpecialPackageCoupon pc = new SpecialPackageCoupon();
                BeanUtils.copyProperties(specialPackageCoupon, pc);
                BeanUtils.copyProperties(specialPackageCouponForm, pc);
                pc.setId(specialPackageCoupon.getId());
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
        SpecialPackageCoupon specialPackageCoupon = new SpecialPackageCoupon();
        specialPackageCoupon.setCouponId(id);
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(id);
        //删除卡券公用信息
        couponCommonInfoBiz.deleteById(id);
        //删除兑换券卡券信息
        delete(specialPackageCoupon);
        //清除图片文档信息
        couponFileInfoBiz.delete(couponFiledelete);
        mqServiceFeign.sendMessage(id, BusinessConstants.DELETE, BaseCoupon);
    }
}
