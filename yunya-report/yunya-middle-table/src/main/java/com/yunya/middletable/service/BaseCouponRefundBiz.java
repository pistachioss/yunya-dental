//package com.yunya.middletable.service;
//
//import com.yunya.feign.report.domain.form.PullForm;
//import com.yunya.feign.report.domain.model.MessageModel;
//import com.yunya.feign.report.domain.query.StatisticsEmployeeQueryForm;
//import com.yunya.feign.report.domain.vo.BillExecutorItemVO;
//import com.yunya.framework.common.biz.BaseBiz;
//import com.yunya.framework.common.utils.StringHelper;
//import com.yunya.middletable.dao.discount.*;
//import com.yunya.middletable.dao.report.BaseCouponRefundDetailMapper;
//import com.yunya.middletable.dao.report.BaseCouponRefundMapper;
//import com.yunya.middletable.dao.report.BaseCouponRefundPayMapper;
//import com.yunya.models.discount.CouponBill;
//import com.yunya.models.discount.CouponRefund;
//import com.yunya.models.report.BaseCouponRefund;
//import com.yunya.models.report.BaseRefund;
//import com.yunya.models.report.BaseRefundDetail;
//import com.yunya.models.report.BaseRefundPayDetail;
//import com.yunya.models.treatment.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.util.ObjectUtils;
//import tk.mybatis.mapper.entity.Example;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class BaseCouponRefundBiz extends BaseBiz<BaseCouponRefundMapper, BaseCouponRefund> {
//
//    /**
//     * 退费记录
//     */
//    @Resource
//    private CouponRefundMapper couponRefundMapper;
//
//    /**
//     * 退费开单详情
//     */
//    @Resource
//    private CouponRefundDetailMapper couponRefundDetailMapper;
//
//    @Resource
//    private CouponRefundPayMapper couponRefundPayMapper;
//
//    /**
//     * 退费明细
//     */
//    @Resource
//    private BaseCouponRefundDetailMapper baseCouponRefundDetailMapper;
//
//    /**
//     * 账单
//     */
//    @Resource
//    private CouponBillMapper couponBillMapper;
//
//    /**
//     * 订单明细
//     */
//    @Resource
//    private CouponOrderDetailMapper couponOrderDetailMapper;
//
//    @Resource
//    private BaseCouponRefundPayMapper baseCouponRefundPayMapper;
//
//    /**
//     * 根据消息更新中间表退费信息
//     *
//     * @param msg 消息
//     */
//    public void operateRefund(MessageModel msg) {
//        Map<String, Object> paramMap = msg.getParamMap();
//        Integer dataId = (Integer) paramMap.get("id");
//        BaseCouponRefund refund = generateBaseRefund(dataId);
//        Integer operateType = msg.getOperateType();
//        switch (operateType) {
//            case 0:
//                mapper.deleteByPrimaryKey(dataId);
//                if (null != refund) {
//                    mapper.insertSelective(refund);
//                    saveBaseRefundDetail(dataId);
//                }
//                break;
//            case 1:
//                if (null != refund) {
//                    BaseCouponRefund result = mapper.selectByPrimaryKey(dataId);
//                    if (null == result) {
//                        mapper.deleteByPrimaryKey(dataId);
//                        mapper.insertSelective(refund);
//                        saveBaseRefundDetail(dataId);
//                    } else {
//                        mapper.updateByPrimaryKeySelective(refund);
//                        updateBaseRefundDetail(dataId);
//                    }
//                } else {
//                    mapper.deleteByPrimaryKey(dataId);
//                }
//                break;
//            case 2:
//                if (null == refund) {
//                    mapper.deleteByPrimaryKey(dataId);
//                    baseCouponRefundDetailMapper.deleteByRefundId(dataId);
//                    deleteByRefundId(dataId);
//                } else {
//                    mapper.insertSelective(refund);
//                    saveBaseRefundDetail(dataId);
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void deleteByRefundId(Integer refundId) {
//        BaseRefundPayDetail entity = new BaseRefundPayDetail();
//        entity.setRefundId(refundId);
//        refundPayDetailMapper.delete(entity);
//    }
//
//    /**
//     * 构建中间表退费信息
//     *
//     * @param refundId 退费记录ID
//     * @return BaseRefund
//     */
//    private BaseCouponRefund generateBaseRefund(Integer refundId) {
//        CouponRefund billRefundRecord = couponRefundMapper.selectByPrimaryKey(refundId);
//        return null != billRefundRecord ? setBaseRefundValue(billRefundRecord) : null;
//    }
//
//    /**
//     * 设置退费账单
//     *
//     * @param billRefundRecord 账单退费记录
//     * @return BaseRefund
//     */
//    private BaseCouponRefund setBaseRefundValue(CouponRefund billRefundRecord) {
//        BaseCouponRefund refund = new BaseCouponRefund();
//        refund.setRefundId(billRefundRecord.getId());
//        refund.setOrgId(billRefundRecord.getOrgId());
//        refund.setPatientId(billRefundRecord.getPatientId());
//        refund.setOrderId(billRefundRecord.getOrderId());
//        refund.setRefundAmount(billRefundRecord.getRefundAmount());
//        refund.setRefundOperatorId(billRefundRecord.getCrtId());
//        refund.setRefundDate(billRefundRecord.getCrtTime());
//        refund.setRefundReason(billRefundRecord.getReason());
//        putBillInfo(refund);
//        return refund;
//    }
//
//    /**
//     * 填充账单信息
//     *
//     * @param refund
//     */
//    private void putBillInfo(BaseCouponRefund refund) {
//        CouponBill query = new CouponBill();
//        query.setOrderRecordId(refund.getOrderId());
//        List<CouponBill> bills = couponBillMapper.select(query);
//        if (StringHelper.isNotEmpty(bills)) {
//            CouponBill billRecord = bills.get(0);
//            refund.setBillNum(billRecord.getBillNumber());
//            refund.setBillDate(billRecord.getCrtTime());
//            refund.setOrderAmount(billRecord.getReceivableAmount());
//            refund.setReceivableAmount(billRecord.getReceivableAmount());
//            refund.setReceivedAmount(billRecord.getReceivedAmount());
//        }
//    }
//
//    /**
//     * 根据退费记录ID保存退费明细
//     *
//     * @param refundId 退费记录ID
//     */
//    private void saveBaseRefundDetail(Integer refundId) {
//        BillRefundOrderDetail refundOrderDetail = new BillRefundOrderDetail();
//        refundOrderDetail.setBillRefundRecordId(refundId);
//        List<BillRefundOrderDetail> refundOrderDetails =
//                refundOrderDetailMapper.select(refundOrderDetail);
//        if (StringHelper.isNotEmpty(refundOrderDetails)) {
//            BaseRefundDetail refundDetail = new BaseRefundDetail();
//            refundOrderDetails.forEach(
//                    detail -> {
//                        Integer refundDetailId = detail.getId();
//                        refundDetail.setRefundDetailId(refundDetailId);
//                        refundDetail.setRefundId(refundId);
//                        refundDetail.setBillDetailId(detail.getOrderDetailId());
//                        refundDetail.setRefundAmount(detail.getRefundAmount());
//                        putBillDetailInfo(refundDetail);
//                        refundDetailMapper.deleteByPrimaryKey(refundDetailId);
//                        refundDetailMapper.insertSelective(refundDetail);
//                    });
//        }
//
//        saveBaseRefundPayDetail(refundId);
//    }
//
//    private void putBillDetailInfo(BaseRefundDetail refundDetail) {
//        Integer billDetailId = refundDetail.getBillDetailId();
//        OrderDetail detail = orderDetailMapper.selectByPrimaryKey(billDetailId);
//        if (!ObjectUtils.isEmpty(detail)) {
//            refundDetail.setExecutorId(detail.getExecutorId());
//            refundDetail.setConsulterId(detail.getConsulterId());
//            refundDetail.setItemId(detail.getBillingItemId());
//            refundDetail.setItemType(detail.getType());
//            refundDetail.setItemName(detail.getBillingItemName());
//        }
//    }
//
//    private void saveBaseRefundPayDetail(Integer refundId) {
//        BillRefundPayDetailRecord billRefundPayDetailRecord = new BillRefundPayDetailRecord();
//        billRefundPayDetailRecord.setBillRefundRecordId(refundId);
//        List<BillRefundPayDetailRecord> refundPayDetails =
//                refundPayDetailRecordMapper.select(billRefundPayDetailRecord);
//        if (StringHelper.isNotEmpty(refundPayDetails)) {
//            deleteByRefundId(refundId);
//            BaseRefundPayDetail refundPayDetail = new BaseRefundPayDetail();
//            refundPayDetails.forEach(
//                    detail -> {
//                        Integer refundDetailId = detail.getId();
//                        refundPayDetail.setBillRefundPayDetailRecordId(refundDetailId);
//                        refundPayDetail.setRefundId(refundId);
//                        refundPayDetail.setAccountItemId(detail.getAccountItemId());
//                        refundPayDetail.setBonusAmount(detail.getGiftAmount());
//                        refundPayDetail.setPrincipalAmount(detail.getPrincipalAmount());
//                        refundPayDetailMapper.insertSelective(refundPayDetail);
//                    });
//        }
//    }
//
//    /**
//     * 更新中间表退费明细
//     *
//     * @param refundId 退费记录ID
//     */
//    private void updateBaseRefundDetail(Integer refundId) {
//        BillRefundOrderDetail refundOrderDetail = new BillRefundOrderDetail();
//        refundOrderDetail.setBillRefundRecordId(refundId);
//        List<BillRefundOrderDetail> refundOrderDetails =
//                refundOrderDetailMapper.select(refundOrderDetail);
//        if (StringHelper.isNotEmpty(refundOrderDetails)) {
//            for (BillRefundOrderDetail refundDetail : refundOrderDetails) {
//                Integer refundOrderDetailId = refundDetail.getId();
//                BaseRefundDetail baseRefundDetail =
//                        refundDetailMapper.selectByPrimaryKey(refundOrderDetailId);
//                if (null != baseRefundDetail) {
//                    baseRefundDetail.setRefundDetailId(refundOrderDetailId);
//                    baseRefundDetail.setBillDetailId(refundDetail.getOrderDetailId());
//                    baseRefundDetail.setRefundId(refundId);
//                    baseRefundDetail.setRefundAmount(refundDetail.getRefundAmount());
//                    putBillDetailInfo(baseRefundDetail);
//                    refundDetailMapper.updateByPrimaryKeySelective(baseRefundDetail);
//                } else {
//                    baseRefundDetail = new BaseRefundDetail();
//                    baseRefundDetail.setRefundDetailId(refundOrderDetailId);
//                    baseRefundDetail.setBillDetailId(refundDetail.getOrderDetailId());
//                    baseRefundDetail.setRefundId(refundId);
//                    baseRefundDetail.setRefundAmount(refundDetail.getRefundAmount());
//                    putBillDetailInfo(baseRefundDetail);
//                    refundDetailMapper.deleteByPrimaryKey(refundOrderDetailId);
//                    refundDetailMapper.insertSelective(baseRefundDetail);
//                }
//            }
//        }
//        saveBaseRefundPayDetail(refundId);
//    }
//
//    /**
//     * 通过时间段更新中间表退费记录
//     *
//     * @param form 时间段
//     */
//    public void pullRefundData(PullForm form) {
//        String startDate = form.getStartDate();
//        String endDate = form.getEndDate();
//        Example refundExample = new Example(BillRefundRecord.class);
//        refundExample.createCriteria().andBetween("updTime", startDate, endDate);
//        List<BillRefundRecord> refundRecords = refundRecordMapper.selectByExample(refundExample);
//        if (StringHelper.isNotEmpty(refundRecords)) {
//            refundRecords.stream()
//                    .map(BillRefundRecord::getId)
//                    .forEach(
//                            refundRecordId -> {
//                                mapper.deleteByPrimaryKey(refundRecordId);
//                                BaseRefund baseRefund = generateBaseRefund(refundRecordId);
//                                if (null != baseRefund) {
//                                    mapper.insertSelective(baseRefund);
//                                    saveBaseRefundDetail(refundRecordId);
//                                } else {
//                                    refundDetailMapper.deleteByRefundId(refundRecordId);
//                                }
//                            });
//        }
//    }
//
//    public List<BillExecutorItemVO> findBillItemRefundListByDate(StatisticsEmployeeQueryForm query) {
//        return mapper.selectBillItemRefundListByDate(query);
//    }
//}
