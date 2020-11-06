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
import com.yunya.models.discount.RechargeCard;
import com.yunya.modules.discount.form.RechargeCardForm;
import com.yunya.modules.discount.mapper.CouponAllocateMapper;
import com.yunya.modules.discount.mapper.CouponCommonInfoMapper;
import com.yunya.modules.discount.mapper.RechargeCardMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCoupon;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-17 10:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RechargeCardBiz extends BaseBiz<RechargeCardMapper, RechargeCard> {
    // 充值卡编码类型
    private static final String RECHARGE_CARD_TYPE = "CZ";


    @Autowired private CouponAllocateMapper couponAllocateMapper;
    @Autowired private CouponCommonInfoMapper couponCommonInfoMapper;
    @Autowired private CouponCommonInfoBiz couponCommonInfoBiz;
    @Autowired private CouponFileInfoBiz couponFileInfoBiz;
    @Resource private RemoteRabbitMqServiceFeign mqServiceFeign;
    /**
     * 新增
     *
     * @param rechargeCardForm
     */
    public Integer saveRechargeCard(RechargeCardForm rechargeCardForm) {
        CouponCommonInfo data = new CouponCommonInfo();
        data.setName(rechargeCardForm.getName());
        if (couponCommonInfoMapper.selectOne(data) != null) {
            throw new BaseException("充值卡名称与系统中已有充值卡重复，不允许新增!", NAME_IS_OCCUPIED);
        }
        CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
        BeanUtils.copyProperties(rechargeCardForm, couponCommonInfo);
        couponCommonInfo.setType(new Byte("4"));
        couponCommonInfo.setIsInservice(true);
        couponCommonInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        //基础信息表中插入数据
        couponCommonInfoBiz.insertSelective(couponCommonInfo);

        RechargeCard rechargeCard = new  RechargeCard();
        BeanUtils.copyProperties(rechargeCardForm, rechargeCard);
        rechargeCard.setCouponId(couponCommonInfo.getId());
        rechargeCard.setBonus(rechargeCardForm.getFaceValue().subtract(rechargeCardForm.getSoldAmount()));
        //插入卡券信息
        insertSelective(rechargeCard);
        //同一种卡券最多添加9999个
        if(rechargeCard.getId()<10000){
        String num = String.format("%04d", rechargeCard.getId());
        couponCommonInfo.setCouponCode(RECHARGE_CARD_TYPE + num);
        //插入卡券编码
        couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);
        }else{
            throw new BaseException("已超过系统允许新增充值卡产品的最大数量9999，不允许新增！", INSERT_MODEL);
        }
        mqServiceFeign.sendMessage(couponCommonInfo.getId(), BusinessConstants.ADD, BaseCoupon);
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
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(couponCommonInfo);
            BeanUtils.copyProperties(copy, couponCommonInfo);

            couponCommonInfo.setAvailableSaleStartDate(rechargeCardForm.getAvailableSaleStartDate());
            couponCommonInfo.setAvailableSaleEndDate(rechargeCardForm.getAvailableSaleEndDate());
            //更新基础信息
            couponCommonInfoBiz.updateById(couponCommonInfo);
            rechargeCard.setCouponId(rechargeCardForm.getId());
            rechargeCard = selectOne(rechargeCard);
            if(rechargeCard!=null){
                rechargeCard.setRemark(rechargeCardForm.getRemark());
                rechargeCard.setRechargeDeadline(rechargeCardForm.getRechargeDeadline());
                //更新明细信息
                updateById(rechargeCard);
            }else {
                throw new BaseException("修改错误，查无结果", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
        } else {
            // 重名判断
            String name = rechargeCardForm.getName();
            CouponCommonInfo data = new CouponCommonInfo();
            data.setName(name);
            if (couponCommonInfoMapper.select(data).size() >= 1) {
                data = new CouponCommonInfo();
                data.setId(rechargeCardForm.getId());
                if(!couponCommonInfoMapper.selectOne(data).getName().equals(name)){
                    throw new BaseException("充值卡名称与系统中已有充值卡重复，不允许修改!", NAME_IS_OCCUPIED);
                }
            }
            data = new CouponCommonInfo();
            data.setId(rechargeCardForm.getId());
            CouponCommonInfo copy = couponCommonInfoMapper.selectOne(data);
            BeanUtils.copyProperties(copy, couponCommonInfo);
            BeanUtils.copyProperties(rechargeCardForm, couponCommonInfo);
            couponCommonInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            couponCommonInfo.setUpdTime(new Date());
            //基础信息表中修改数据
            couponCommonInfoBiz.updateById(couponCommonInfo);
            rechargeCard.setCouponId(rechargeCardForm.getId());
            rechargeCard = selectOne(rechargeCard);
            if (rechargeCard != null) {
                RechargeCard pc = new RechargeCard();
                BeanUtils.copyProperties(rechargeCard, pc);
                BeanUtils.copyProperties(rechargeCardForm, pc);
                pc.setId(rechargeCard.getId());
                pc.setBonus(rechargeCardForm.getFaceValue().subtract(rechargeCardForm.getSoldAmount()));
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
        //删除卡券公用信息
        couponCommonInfoBiz.deleteById(id);
        //删除兑换券卡券信息
        delete(rechargeCard);
        //清除图片文档信息
        couponFileInfoBiz.delete(couponFiledelete);
        mqServiceFeign.sendMessage(id, BusinessConstants.DELETE, BaseCoupon);
    }

}
