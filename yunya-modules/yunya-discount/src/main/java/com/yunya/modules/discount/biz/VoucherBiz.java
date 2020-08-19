package com.yunya.modules.discount.biz;

import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.modules.discount.constant.ExceptionCode;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.VoucheCouponMapper;
import com.yunya.modules.discount.vo.VoucheCouponVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    @Autowired private CouponAllocateMapper couponAllocateMapper;
    @Autowired private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired private CouponFileInfoBiz couponFileInfoBiz;
    @Autowired private CouponCommonInfoMapper couponCommonInfoMapper;

    /**
     * 新增
     * @param voucheCouponVO
     * @return
     */
    public Integer saveVoucher(VoucheCouponVO voucheCouponVO) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setName(voucheCouponVO.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(voucheCouponVO,couponCommonInfo);
        couponCommonInfo.setType(new Byte("0"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        couponCommonInfo.setCrtTime(new Date());
        couponCommonInfoBiz.insertSelective(couponCommonInfo);//基础信息表中插入数据
        List<CouponFileInfo> list = new ArrayList<>();
        if(voucheCouponVO.getPaths()!=null && voucheCouponVO.getPaths().size()>0){//图片信息
            for(FileInfo fileInfo: voucheCouponVO.getPaths()){
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setCouponId(couponCommonInfo.getId());
                couponFileInfo.setInservice(true);
                couponFileInfo.setRemark(fileInfo.getMark());
                couponFileInfo.setFileType(new Byte("0"));
                couponFileInfo.setPath(fileInfo.getPath());
                couponFileInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                couponFileInfo.setCrtTime(new Date());
                list.add(couponFileInfo);
            }
        }
        if(voucheCouponVO.getDocs()!=null && voucheCouponVO.getDocs().size()>0){//文档信息
            for(String doc: voucheCouponVO.getDocs()){
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setCouponId(couponCommonInfo.getId());
                couponFileInfo.setInservice(true);
                couponFileInfo.setFileType(new Byte("1"));
                couponFileInfo.setPath(doc);
                couponFileInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                couponFileInfo.setCrtTime(new Date());
                list.add(couponFileInfo);
            }
        }
        if(list.size()>0){
            couponFileInfoBiz.insetAll(list);//插入文件信息
        }
        VoucheCoupon voucheCoupon = new VoucheCoupon();
        BeanUtils.copyProperties(voucheCouponVO,voucheCoupon);
        voucheCoupon.setCouponId(couponCommonInfo.getId());
        voucheCoupon.setMixedUseType(voucheCouponVO.getMixedUseType().byteValue());
        voucheCoupon.setUseableClinci(voucheCouponVO.getClinicIds());
        insertSelective(voucheCoupon);//插入卡券信息
        return voucheCoupon.getId();
    }

    /**
     * 修改
     *
     * @param discountUpdateForm
     */
    public void updateVoucher(VoucheCouponVO discountUpdateForm) {
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
            couponCommonInfoBiz.updateById(couponCommonInfo);//更新基础信息
            voucheCoupon.setCouponId(discountUpdateForm.getId());
            voucheCoupon = selectOne(voucheCoupon);
            if(voucheCoupon!=null){
                voucheCoupon.setUseableClinci(discountUpdateForm.getClinicIds());
                voucheCoupon.setRemark(discountUpdateForm.getRemark());
                voucheCoupon.setActivationDeadline(discountUpdateForm.getActivationDeadline());
                voucheCoupon.setWorkloadRate(discountUpdateForm.getWorkloadRate());
                voucheCoupon.setEffectiveDays(discountUpdateForm.getEffectiveDays());
                updateById(voucheCoupon);//更新明细信息
            }else{
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        } else {//还没分配
            // 重名判断
            String name = discountUpdateForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size()>=1) {
                throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
            }
            BeanUtils.copyProperties(discountUpdateForm,couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            couponCommonInfoBiz.updateById(couponCommonInfo);//基础信息表中修改数据
            voucheCoupon.setCouponId(discountUpdateForm.getId());
            voucheCoupon = selectOne(voucheCoupon);
            if(voucheCoupon!=null){
                VoucheCoupon vc = new VoucheCoupon();
                BeanUtils.copyProperties(discountUpdateForm,vc);
                vc.setId(voucheCoupon.getId());
                updateById(vc);//更新明细信息
            }else{
            throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
        }
        List<CouponFileInfo> list = new ArrayList<>();
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        if(discountUpdateForm.getPaths()!=null && discountUpdateForm.getPaths().size()>0){//图片信息
            couponFiledelete.setCouponId(discountUpdateForm.getId());
            couponFiledelete.setFileType(new Byte("0"));
            couponFileInfoBiz.delete(couponFiledelete);//清除之前的图片
            for(FileInfo fileInfo: discountUpdateForm.getPaths()){
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setCouponId(couponCommonInfo.getId());
                couponFileInfo.setInservice(true);
                couponFileInfo.setRemark(fileInfo.getMark());
                couponFileInfo.setFileType(new Byte("0"));
                couponFileInfo.setPath(fileInfo.getPath());
                couponFileInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                couponFileInfo.setCrtTime(new Date());
                list.add(couponFileInfo);
            }
        }
        if(discountUpdateForm.getDocs()!=null && discountUpdateForm.getDocs().size()>0){//文档信息
            couponFiledelete.setCouponId(discountUpdateForm.getId());
            couponFiledelete.setFileType(new Byte("1"));
            couponFileInfoBiz.delete(couponFiledelete);//清除之前的文档
            for(String doc: discountUpdateForm.getDocs()){
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setCouponId(couponCommonInfo.getId());
                couponFileInfo.setInservice(true);
                couponFileInfo.setFileType(new Byte("1"));
                couponFileInfo.setPath(doc);
                couponFileInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                couponFileInfo.setCrtTime(new Date());
                list.add(couponFileInfo);
            }
        }
        if(list.size()>0){
            couponFileInfoBiz.insetAll(list);//插入文件信息
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
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", ExceptionCode.CARD_EXIST);
        }
        deleteById(id);
    }

}
