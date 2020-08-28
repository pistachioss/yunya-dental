package com.yunya.modules.discount.biz;

import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.CouponAllocate;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.CouponFileInfo;
import com.yunya.models.discount.RechargeCard;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.modules.discount.form.RechargeCardForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.RechargeCardMapper;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.modules.discount.constant.ExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.yunya.framework.common.constant.OperationCodeConstants.DELETE_NOT_ALLOW;
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
    @Autowired private CouponCommonInfoMapper couponCommonInfoMapper;
    @Autowired private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired private CouponFileInfoBiz couponFileInfoBiz;

    /**
     * 新增
     *
     * @param rechargeCardForm
     */
    public Integer saveRechargeCard(RechargeCardForm rechargeCardForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setName(rechargeCardForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(rechargeCardForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("4"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        couponCommonInfoBiz.insertSelective(couponCommonInfo);//基础信息表中插入数据

        RechargeCard rechargeCard = new  RechargeCard();
        BeanUtils.copyProperties(rechargeCardForm, rechargeCard);
        rechargeCard.setCouponId(couponCommonInfo.getId());
        rechargeCard.setBonus(rechargeCardForm.getFaceValue().subtract(rechargeCardForm.getSoldAmount()));
        insertSelective(rechargeCard);//插入卡券信息
        return couponCommonInfo.getId();

    }

    /**
     * 修改
     *
     * @param rechargeCardForm
     */
    public void updateRechargeCard( RechargeCardForm rechargeCardForm) {
        boolean flag = true;
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(rechargeCardForm.getId());
        if (couponAllocateMapper.select(couponAllocate).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        RechargeCard rechargeCard = new RechargeCard();
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        if (flag) {
            // 只能修改时间
            couponCommonInfo.setId(rechargeCardForm.getId());
            couponCommonInfo.setAvailableSaleStartDate(rechargeCardForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(rechargeCardForm.getAvailableSaleEndDate());
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//更新基础信息
            rechargeCard.setCouponId(rechargeCardForm.getId());
            rechargeCard = selectOne(rechargeCard);
            if(rechargeCard!=null){
                rechargeCard.setRemark(rechargeCardForm.getRemark());
                rechargeCard.setRechargeDeadline(rechargeCardForm.getRechargeDeadline());
                updateSelectiveById(rechargeCard);//更新明细信息
            }else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        } else {
            // 重名判断
            String name = rechargeCardForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 2) {
                throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
            }
            BeanUtils.copyProperties(rechargeCardForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);//基础信息表中修改数据
            rechargeCard.setCouponId(rechargeCardForm.getId());
            rechargeCard = selectOne(rechargeCard);
            if (rechargeCard != null) {
                RechargeCard pc = new RechargeCard();
                BeanUtils.copyProperties(rechargeCardForm, pc);
                pc.setId(rechargeCard.getId());
                pc.setBonus(rechargeCardForm.getFaceValue().subtract(rechargeCardForm.getSoldAmount()));
                updateSelectiveById(pc);//更新明细信息
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
    public void deleteRechargeCard(Integer id) {
        // 判断是否完成分配
        CouponAllocate couponAllocate = new CouponAllocate();
        couponAllocate.setCouponId(id);
        if (!couponAllocateMapper.select(couponAllocate).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", DELETE_NOT_ALLOW);
        }
        RechargeCard rechargeCard = new RechargeCard();
        rechargeCard.setCouponId(id);
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(id);
        couponCommonInfoBiz.deleteById(id);//删除卡券公用信息
        delete(rechargeCard);//删除兑换券卡券信息
        couponFileInfoBiz.delete(couponFiledelete);//清除图片文档信息

    }

}
