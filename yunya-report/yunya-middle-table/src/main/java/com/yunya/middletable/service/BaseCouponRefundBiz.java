package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.discount.*;
import com.yunya.middletable.dao.report.BaseCouponRefundDetailMapper;
import com.yunya.middletable.dao.report.BaseCouponRefundMapper;
import com.yunya.middletable.dao.report.BaseCouponRefundPayMapper;
import com.yunya.models.discount.*;
import com.yunya.models.report.BaseCouponRefund;
import com.yunya.models.report.BaseCouponRefundDetail;
import com.yunya.models.report.BaseCouponRefundPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BaseCouponRefundBiz extends BaseBiz<BaseCouponRefundMapper, BaseCouponRefund> {

    /**
     * 退费记录
     */
    @Resource
    private CouponRefundMapper couponRefundMapper;

    /**
     * 退费开单详情
     */
    @Resource
    private CouponRefundDetailMapper couponRefundDetailMapper;

    @Resource
    private CouponRefundPayMapper couponRefundPayMapper;

    @Resource
    private CouponOrderVirtualMapper couponOrderVirtualMapper;

    /**
     * 退费明细
     */
    @Resource
    private BaseCouponRefundDetailMapper baseCouponRefundDetailMapper;

    /**
     * 账单
     */
    @Resource
    private CouponBillMapper couponBillMapper;

    /**
     * 订单明细
     */
    @Resource
    private CouponOrderDetailMapper couponOrderDetailMapper;

    @Resource
    private BaseCouponRefundPayMapper baseCouponRefundPayMapper;

    /**
     * 根据消息更新中间表退费信息
     *
     * @param msg 消息
     */
    public void operateRefund(MessageModel msg) {
        Map<String, Object> paramMap = msg.getParamMap();
        Integer dataId = (Integer) paramMap.get("id");
        BaseCouponRefund refund = generateBaseRefund(dataId);
        Integer operateType = msg.getOperateType();
        switch (operateType) {
            case 0:
                mapper.deleteByPrimaryKey(dataId);
                if (null != refund) {
                    mapper.insertSelective(refund);
                    saveBaseRefundDetail(dataId);
                }
                break;
            case 1:
                if (null != refund) {
                    BaseCouponRefund result = mapper.selectByPrimaryKey(dataId);
                    if (null == result) {
                        mapper.deleteByPrimaryKey(dataId);
                        mapper.insertSelective(refund);
                        saveBaseRefundDetail(dataId);
                    } else {
                        mapper.updateByPrimaryKeySelective(refund);
                        updateBaseRefundDetail(dataId);
                    }
                } else {
                    mapper.deleteByPrimaryKey(dataId);
                }
                break;
            case 2:
                if (null == refund) {
                    mapper.deleteByPrimaryKey(dataId);
                    baseCouponRefundDetailMapper.deleteByRefundId(dataId);
                    deleteByRefundId(dataId);
                } else {
                    mapper.insertSelective(refund);
                    saveBaseRefundDetail(dataId);
                }
                break;
            default:
                break;
        }
    }

    private void deleteByRefundId(Integer refundId) {
        BaseCouponRefundPay entity = new BaseCouponRefundPay();
        entity.setRefundId(refundId);
        baseCouponRefundPayMapper.delete(entity);
    }

    /**
     * 构建中间表退费信息
     *
     * @param refundId 退费记录ID
     * @return BaseRefund
     */
    private BaseCouponRefund generateBaseRefund(Integer refundId) {
        CouponRefund billRefundRecord = couponRefundMapper.selectByPrimaryKey(refundId);
        return null != billRefundRecord ? setBaseRefundValue(billRefundRecord) : null;
    }

    /**
     * 设置退费账单
     *
     * @param billRefundRecord 账单退费记录
     * @return BaseRefund
     */
    private BaseCouponRefund setBaseRefundValue(CouponRefund billRefundRecord) {
        BaseCouponRefund refund = new BaseCouponRefund();
        refund.setRefundId(billRefundRecord.getId());
        refund.setOrgId(billRefundRecord.getOrgId());
        refund.setPatientId(billRefundRecord.getPatientId());
        refund.setOrderId(billRefundRecord.getOrderId());
        refund.setRefundAmount(billRefundRecord.getRefundAmount());
        refund.setRefundOperatorId(billRefundRecord.getCrtId());
        refund.setRefundDate(billRefundRecord.getCrtTime());
        refund.setRefundReason(billRefundRecord.getReason());
        putBillInfo(refund);
        return refund;
    }

    /**
     * 填充账单信息
     *
     * @param refund
     */
    private void putBillInfo(BaseCouponRefund refund) {
        CouponBill query = new CouponBill();
        query.setOrderRecordId(refund.getOrderId());
        List<CouponBill> bills = couponBillMapper.select(query);
        if (StringHelper.isNotEmpty(bills)) {
            CouponBill billRecord = bills.get(0);
            refund.setBillNum(billRecord.getBillNumber());
            refund.setBillDate(billRecord.getCrtTime());
            refund.setOrderAmount(billRecord.getReceivableAmount());
            refund.setReceivableAmount(billRecord.getReceivableAmount());
            refund.setReceivedAmount(billRecord.getReceivedAmount());
        }
    }

    /**
     * 根据退费记录ID保存退费明细
     *
     * @param refundId 退费记录ID
     */
    private void saveBaseRefundDetail(Integer refundId) {
        CouponRefundDetail refundOrderDetail = new CouponRefundDetail();
        refundOrderDetail.setRefundId(refundId);
        List<CouponRefundDetail> refundOrderDetails =
                couponRefundDetailMapper.select(refundOrderDetail);
        if (StringHelper.isNotEmpty(refundOrderDetails)) {
            BaseCouponRefundDetail refundDetail = new BaseCouponRefundDetail();
            refundOrderDetails.forEach(
                    detail -> {
                        Integer refundDetailId = detail.getId();
                        refundDetail.setRefundDetailId(refundDetailId);
                        refundDetail.setRefundId(refundId);
                        refundDetail.setOrderDetailId(detail.getOrderDetailId());
                        refundDetail.setCardId(detail.getCardId());
                        refundDetail.setRefundAmount(detail.getRefundAmount());
//                        refundDetail.setCardNumber(detail.getca);
                        putBillDetailInfo(refundDetail);
                        baseCouponRefundDetailMapper.deleteByPrimaryKey(refundDetailId);
                        baseCouponRefundDetailMapper.insertSelective(refundDetail);
                    });
        }
        saveBaseRefundPayDetail(refundId);
    }

    private void putBillDetailInfo(BaseCouponRefundDetail refundDetail) {
        Integer billDetailId = refundDetail.getOrderDetailId();
        CouponOrderDetail detail = couponOrderDetailMapper.selectByPrimaryKey(billDetailId);
        if (!ObjectUtils.isEmpty(detail)) {
            refundDetail.setExecutorId(detail.getExecutorId());
            refundDetail.setConsulterId(detail.getConsulterId());
            refundDetail.setType(detail.getType());
            refundDetail.setCouponName(detail.getCouponName());
        }
    }

    private void saveBaseRefundPayDetail(Integer refundId) {
        CouponRefundPay billRefundPayDetailRecord = new CouponRefundPay();
        billRefundPayDetailRecord.setRefundId(refundId);
        List<CouponRefundPay> refundPayDetails =
                couponRefundPayMapper.select(billRefundPayDetailRecord);
        if (StringHelper.isNotEmpty(refundPayDetails)) {
            deleteByRefundId(refundId);
            BaseCouponRefundPay refundPayDetail = new BaseCouponRefundPay();
            refundPayDetails.forEach(
                    detail -> {
                        Integer refundDetailId = detail.getId();
                        refundPayDetail.setBillRefundPayId(refundDetailId);
                        refundPayDetail.setRefundId(refundId);
                        refundPayDetail.setAccountItemId(detail.getAccountItemId());
                        refundPayDetail.setAccountItemName(detail.getAccountItemName());
                        refundPayDetail.setPatientNumber(detail.getPatientNumber());
                        refundPayDetail.setTotalAmount(detail.getTotalAmount());
                        refundPayDetail.setBonusAmount(detail.getGiftAmount());
                        refundPayDetail.setPrincipalAmount(detail.getPrincipalAmount());
                        baseCouponRefundPayMapper.insertSelective(refundPayDetail);
                    });
        }
    }

    /**
     * 更新中间表退费明细
     *
     * @param refundId 退费记录ID
     */
    private void updateBaseRefundDetail(Integer refundId) {
        CouponRefundDetail refundOrderDetail = new CouponRefundDetail();
        refundOrderDetail.setRefundId(refundId);
        List<CouponRefundDetail> refundOrderDetails =
                couponRefundDetailMapper.select(refundOrderDetail);
        if (StringHelper.isNotEmpty(refundOrderDetails)) {
            for (CouponRefundDetail refundDetail : refundOrderDetails) {
                Integer refundOrderDetailId = refundDetail.getId();
                BaseCouponRefundDetail baseRefundDetail =
                        baseCouponRefundDetailMapper.selectByPrimaryKey(refundOrderDetailId);
                if (null != baseRefundDetail) {
                    baseRefundDetail.setRefundDetailId(refundOrderDetailId);
                    baseRefundDetail.setOrderDetailId(refundDetail.getOrderDetailId());
                    baseRefundDetail.setRefundId(refundId);
                    baseRefundDetail.setRefundAmount(refundDetail.getRefundAmount());
                    baseRefundDetail.setCardId(refundDetail.getCardId());
                    baseRefundDetail.setRefundAmount(refundDetail.getRefundAmount());
                    putBillDetailInfo(baseRefundDetail);
                    baseCouponRefundDetailMapper.updateByPrimaryKeySelective(baseRefundDetail);
                } else {
                    baseRefundDetail = new BaseCouponRefundDetail();
                    baseRefundDetail.setRefundDetailId(refundOrderDetailId);
                    baseRefundDetail.setRefundId(refundId);
                    baseRefundDetail.setOrderDetailId(refundDetail.getOrderDetailId());
                    baseRefundDetail.setCardId(refundDetail.getCardId());
                    baseRefundDetail.setRefundAmount(refundDetail.getRefundAmount());
//                        refundDetail.setCardNumber(detail.getca);
                    putBillDetailInfo(baseRefundDetail);
                    baseCouponRefundDetailMapper.deleteByPrimaryKey(refundOrderDetailId);
                    baseCouponRefundDetailMapper.insertSelective(baseRefundDetail);
                }
            }
        }
        saveBaseRefundPayDetail(refundId);
    }

    /**
     * 通过时间段更新中间表退费记录
     *
     * @param form 时间段
     */
    public void pullRefundData(PullForm form) {
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        Example refundExample = new Example(CouponRefund.class);
        refundExample.createCriteria().andBetween("updTime", startDate, endDate);
        List<CouponRefund> refundRecords = couponRefundMapper.selectByExample(refundExample);
        if (StringHelper.isNotEmpty(refundRecords)) {
            refundRecords.stream()
                    .map(CouponRefund::getId)
                    .forEach(
                            refundRecordId -> {
                                mapper.deleteByPrimaryKey(refundRecordId);
                                BaseCouponRefund baseRefund = generateBaseRefund(refundRecordId);
                                if (null != baseRefund) {
                                    mapper.insertSelective(baseRefund);
                                    saveBaseRefundDetail(refundRecordId);
                                } else {
                                    baseCouponRefundDetailMapper.deleteByRefundId(refundRecordId);
                                }
                            });
        }
    }

//    public List<BillExecutorItemVO> findBillItemRefundListByDate(StatisticsEmployeeQueryForm query) {
//        return mapper.selectBillItemRefundListByDate(query);
//    }
}
