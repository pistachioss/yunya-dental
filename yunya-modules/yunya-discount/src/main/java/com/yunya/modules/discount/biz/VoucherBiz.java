package com.yunya.modules.discount.biz;

import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.modules.discount.constant.ExceptionCode;
import com.yunya.modules.discount.form.CouponCommonInfoQueryForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.VoucheCouponMapper;
import com.yunya.modules.discount.vo.CouponCommonInfoVO;
import com.yunya.modules.discount.form.VoucheCouponForm;
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
 * @create 2020-07-09 17:23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VoucherBiz extends BaseBiz<VoucheCouponMapper, VoucheCoupon> {
    // 代金券类型
    private static final Integer VOUCHER_TYPE = 0;
    // 状态
    private static final Integer FINISH = 1;
    private static final Integer PLAN = 0;


    @Autowired
    private CouponAllocateMapper couponAllocateMapper;
    @Autowired
    private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired
    private CouponFileInfoBiz couponFileInfoBiz;
    @Autowired
    private CouponCommonInfoMapper couponCommonInfoMapper;

    /**
     * 新增
     *
     * @param voucheCouponForm
     * @return
     */
    public Integer saveVoucher(VoucheCouponForm voucheCouponForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setName(voucheCouponForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("代金券名称与系统中已有代金券重复，不允许新增!", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(voucheCouponForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("0"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        couponCommonInfo.setCrtTime(new Date());
        couponCommonInfoBiz.insertSelective(couponCommonInfo);//基础信息表中插入数据

        VoucheCoupon voucheCoupon = new VoucheCoupon();
        BeanUtils.copyProperties(voucheCouponForm, voucheCoupon);
        voucheCoupon.setCouponId(couponCommonInfo.getId());
        voucheCoupon.setMixedUseType(voucheCouponForm.getMixedUseType().byteValue());
        voucheCoupon.setUseableClinci(voucheCouponForm.getUseableClinci());
        insertSelective(voucheCoupon);//插入卡券信息
        return couponCommonInfo.getId();

    }




    /**
     * 修改
     *
     * @param discountUpdateForm
     */
    public void updateVoucher(VoucheCouponForm discountUpdateForm) {
        Integer id = discountUpdateForm.getId();
        boolean flag = true;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(discountUpdateForm.getId());
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        VoucheCoupon voucheCoupon = new VoucheCoupon();
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        if (flag) {//已经分配
            // 只能修改时间
            couponCommonInfo.setId(discountUpdateForm.getId());
            couponCommonInfo.setAvailableSaleStartDate(discountUpdateForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(discountUpdateForm.getAvailableSaleEndDate());
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//更新基础信息
            voucheCoupon.setCouponId(discountUpdateForm.getId());
            voucheCoupon = selectOne(voucheCoupon);
            if (voucheCoupon != null) {
                voucheCoupon.setUseableClinci(discountUpdateForm.getUseableClinci());
                voucheCoupon.setRemark(discountUpdateForm.getRemark());
                voucheCoupon.setActivationDeadline(discountUpdateForm.getActivationDeadline());
                voucheCoupon.setWorkloadRate(discountUpdateForm.getWorkloadRate());
                voucheCoupon.setEffectiveDays(discountUpdateForm.getEffectiveDays());
                updateSelectiveById(voucheCoupon);//更新明细信息
            } else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        } else {//还没分配
            // 重名判断
            String name = discountUpdateForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 1) {
                data = new CouponCommonInfo();
                data.setId(discountUpdateForm.getId());
                if(!couponCommonInfoMapper.selectOne(data).getName().equals(name)){
                    throw new BaseException("代金券名称与系统中已有代金券重复，不允许修改!", NAME_IS_OCCUPIED);
                }
            }
            BeanUtils.copyProperties(discountUpdateForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//基础信息表中修改数据
            voucheCoupon.setCouponId(discountUpdateForm.getId());
            voucheCoupon = selectOne(voucheCoupon);
            if (voucheCoupon != null) {
                VoucheCoupon vc = new VoucheCoupon();
                BeanUtils.copyProperties(discountUpdateForm, vc);
                vc.setId(voucheCoupon.getId());
                updateSelectiveById(vc);//更新明细信息
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
    public void deleteVoucher(Integer id) {
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (!couponAllocateMapper.select(couponAllocate).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", DELETE_NOT_ALLOW);
        }
        VoucheCoupon voucheCoupon = new VoucheCoupon();
        voucheCoupon.setCouponId(id);
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(id);
        couponCommonInfoBiz.deleteById(id);//删除卡券公用信息
        delete(voucheCoupon);//删除代金券卡券信息
        couponFileInfoBiz.delete(couponFiledelete);//清除图片文档信息
    }

    /**
     * 根据分类查看列表
     * @param couponCommonInfoQueryForm
     * @return
     */
    public  List<CouponCommonInfoVO> findList(CouponCommonInfoQueryForm couponCommonInfoQueryForm){
        return mapper.findList(couponCommonInfoQueryForm);
    }

}
