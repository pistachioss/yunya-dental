package com.yunya.middletable.service;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.discount.CouponBillMapper;
import com.yunya.middletable.dao.discount.CouponBillPayDetailMapper;
import com.yunya.middletable.dao.discount.CouponOrderDetailMapper;
import com.yunya.middletable.dao.discount.CouponOrderMapper;
import com.yunya.middletable.dao.report.BaseCouponBillDetailMapper;
import com.yunya.middletable.dao.report.BaseCouponBillMapper;
import com.yunya.models.discount.CouponBill;
import com.yunya.models.discount.CouponOrder;
import com.yunya.models.discount.CouponOrderDetail;
import com.yunya.models.report.BaseCouponBill;
import com.yunya.models.report.BaseCouponBillDetail;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class BaseCouponBillBiz extends BaseBiz<BaseCouponBillMapper, BaseCouponBill> {

    /**
     * 订单记录
     */
    @Resource
    private CouponOrderMapper couponOrderMapper;
    /**
     * 订单明细
     */
    @Resource
    private CouponOrderDetailMapper couponOrderDetailMapper;
    /**
     * 订单明细付款记录
     */
    @Resource
    private CouponBillPayDetailMapper couponBillPayDetailMapper;
    /**
     * 账单记录
     */
    @Resource
    private CouponBillMapper couponBillMapper;
    /**
     * 中间表账单详情
     */
    @Resource
    private BaseCouponBillDetailMapper baseCouponBillDetailMapper;

    @Resource
    private BaseCouponBillMapper baseCouponBillMapper;

    /**
     * 多线程
     */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;

    /**
     * 更新开单明细
     *
     * @param msg 消息
     */
    public void updateBaseBillDetail(MessageModel msg) {
        Integer dataId = (Integer) msg.getParamMap().get("id");
        CouponOrderDetail detail = couponOrderDetailMapper.selectByPrimaryKey(dataId);
        if (detail != null && detail.getInservice()) {
            BaseCouponBillDetail baseBillDetail = baseCouponBillDetailMapper.selectByPrimaryKey(dataId);
            if (baseBillDetail != null) {
                baseBillDetail.setExecutorId(detail.getExecutorId());
                baseBillDetail.setConsulterId(detail.getConsulterId());
                baseCouponBillDetailMapper.updateByPrimaryKeySelective(baseBillDetail);
            }
        }
    }

    /**
     * 根据消息操作中间表账单
     *
     * @param msg 消息
     */
    public void operateBill(MessageModel msg) {
        Integer dataId = (Integer) msg.getParamMap().get("id");
        BaseCouponBill bill = generateBaseBill(dataId);
        Integer operateType = msg.getOperateType();
        switch (operateType) {
            case 0:
            case 2:
            case 1:
                mapper.deleteByPrimaryKey(dataId);
                if (null != bill) {
                    mapper.insertSelective(bill);
                    baseCouponBillDetailMapper.deleteByBillId(dataId);
                    // 保存账单明细
                    saveBaseBillDetail(dataId);
                } else {
                    baseCouponBillDetailMapper.deleteByBillId(dataId);
                }
            default:
                break;
        }
    }


    /**
     * 是否是患者的首个账单
     *
     * @param patientId
     * @return
     */
    private Boolean isPatientFirstBill(Integer patientId) {
        Example example = new Example(BaseCouponBill.class);
        Example.Criteria c = example.createCriteria();
        c.andEqualTo("patientId", patientId);
        c.andIsNotNull("billDate");
        List<BaseCouponBill> bills = mapper.selectByExample(example);
        if (StringHelper.isNotEmpty(bills) && bills.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     * 初始化中间表账单信息
     *
     * @param orderRecordId 账单ID（开单记录ID）
     * @return BaseBill
     */
    private BaseCouponBill generateBaseBill(Integer orderRecordId) {
        CouponOrder orderRecord = couponOrderMapper.selectByPrimaryKey(orderRecordId);
        if (null != orderRecord && orderRecord.getInservice()) {
            BaseCouponBill baseBill = new BaseCouponBill();
            baseBill.setOrderId(orderRecord.getId());
            baseBill.setOrgId(orderRecord.getOrgId());
            baseBill.setPatientId(orderRecord.getPatientId());
            baseBill.setOrderStatus(orderRecord.getStatus());
            baseBill.setOrderNum(orderRecord.getOrderRecordNum());
            baseBill.setOrderAmount(orderRecord.getTotalAmount());
            baseBill.setBillerId(orderRecord.getCrtId());
            baseBill.setOrderDate(orderRecord.getCrtTime());
            baseBill.setReceivableAmount(new BigDecimal("0"));
            baseBill.setReceivedAmount(new BigDecimal("0"));
            baseBill.setDebtAmount(new BigDecimal("0"));
            // 设置账单的收费信息
            setBaseBillChargeValue(orderRecord, baseBill);
            return baseBill;
        }
        return null;
    }

    /**
     * 设置中间表收费汇总信息
     *
     * @param orderRecord 订单
     * @param baseBill    中间表账单
     */
    private void setBaseBillChargeValue(CouponOrder orderRecord, BaseCouponBill baseBill) {
        CouponBill bill = new CouponBill();
        if (Objects.equals(orderRecord.getStatus(), 1)) {
            bill.setOrderRecordId(orderRecord.getId());
            bill.setInservice(true);
            CouponBill billRecord = couponBillMapper.selectOne(bill);
            if (null != billRecord) {
                baseBill.setOrderStatus(orderRecord.getStatus());
                record2baseReport(billRecord, baseBill);
            }
        }
    }

    /**
     * 账单实体转换
     *
     * @param billRecord
     * @param baseBill
     */
    private void record2baseReport(CouponBill billRecord, BaseCouponBill baseBill) {
        BigDecimal debtAmount = billRecord.getDebtAmount();
        baseBill.setBillId(billRecord.getOrderRecordId());
        baseBill.setOrgId(billRecord.getOrgId());
        baseBill.setBillDate(billRecord.getCrtTime());
        baseBill.setBillNum(billRecord.getBillNumber());
        baseBill.setReceivableAmount(billRecord.getReceivableAmount());
        baseBill.setReceivedAmount(billRecord.getReceivedAmount());
        baseBill.setDebtAmount(debtAmount);
        baseBill.setCheckerId(billRecord.getCrtId());
    }

    /**
     * 保存中间表账单明细
     *
     * @param orderRecordId 订单记录ID
     */
    private void saveBaseBillDetail(Integer orderRecordId) {
        CouponOrderDetail orderDetail = new CouponOrderDetail();
        orderDetail.setOrderId(orderRecordId);
        orderDetail.setInservice(true);
        List<CouponOrderDetail> details = couponOrderDetailMapper.select(orderDetail);
        if (StringHelper.isNotEmpty(details)) {
            // 构建中间表账单明细列表
            List<BaseCouponBillDetail> billDetails = generateBaseBillDetail(details);
            if (StringHelper.isNotEmpty(billDetails)) {
                baseCouponBillDetailMapper.deleteByBillId(orderRecordId);
                billDetails.forEach(billDetail -> baseCouponBillDetailMapper.insertSelective(billDetail));
            }
        }
    }

    /**
     * 根据订单明细构建中间表账单明细
     *
     * @param details 订单明细列表
     * @return List<BaseBillDetail>
     */
    private List<BaseCouponBillDetail> generateBaseBillDetail(List<CouponOrderDetail> details) {
        List<BaseCouponBillDetail> billDetails = Lists.newArrayList();
        details.forEach(
                detail -> {
                    BaseCouponBillDetail billDetail = new BaseCouponBillDetail();
                    // 设置中间表订单明细属性
                    setBaseBillDetailValue(detail, billDetail);
                    billDetails.add(billDetail);
                });
        return billDetails;
    }

    /**
     * 设置中间表账单明细属性
     *
     * @param detail         原始订单明细
     * @param baseBillDetail 中间表账单明细
     */
    private void setBaseBillDetailValue(CouponOrderDetail detail, BaseCouponBillDetail baseBillDetail) {
        Integer detailId = detail.getId();
        baseBillDetail.setOrderDetailId(detailId);
        baseBillDetail.setOrgId(detail.getOrgId());
        baseBillDetail.setOrderId(detail.getOrderId());
        baseBillDetail.setCouponId(detail.getCouponId());
        baseBillDetail.setType(detail.getType());
        baseBillDetail.setCouponName(detail.getCouponName());
        baseBillDetail.setCouponNumber(detail.getCouponNumber());
        baseBillDetail.setExecutorId(detail.getExecutorId());
        baseBillDetail.setConsulterId(detail.getConsulterId());
        baseBillDetail.setQuantity(detail.getQuantity());
        baseBillDetail.setPrice(detail.getPrice());
        baseBillDetail.setReceivedAmount(detail.getReceivableAmount());
        baseBillDetail.setRemark(detail.getRemarks());
        baseBillDetail.setSaleChannelId(detail.getSaleChannelId());
        baseBillDetail.setReceivableAmount(detail.getReceivableAmount());
    }

    /**
     * 根据条件拉取账单数据并更新中间表
     *
     * @param form 时间段
     */
    public void pullBillData(PullForm form) throws InterruptedException {
        String startDate = form.getStartDate();
        String endDate = form.getEndDate();
        List<String> dateRanges = DateUtil.sliceUpDateRange(startDate, endDate);
        if (StringHelper.isNotEmpty(dateRanges)) {
            CountDownLatch latch = new CountDownLatch(dateRanges.size());
            List<Future> resultFutures = new ArrayList<>();
            for (String date : dateRanges) {
                resultFutures.add(
                        importExcelThreadPool.submit(
                                () -> {
                                    try {
                                        Example orderExample = new Example(CouponOrder.class);
                                        orderExample
                                                .createCriteria()
                                                .andEqualTo("inservice", true)
                                                .andCondition(
                                                        "crt_time >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                                                .andCondition(
                                                        "crt_time < '"
                                                                + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                                                + "'");
                                        List<CouponOrder> orderRecords =
                                                couponOrderMapper.selectByExample(orderExample);
                                        if (StringHelper.isNotEmpty(orderRecords)) {
                                            Set<BaseCouponBill> baseBills = generateBaseBillList(orderRecords);
                                            if (StringHelper.isNotEmpty(baseBills)) {
                                                Example baseBillEmp = new Example(BaseCouponBill.class);
                                                baseBillEmp
                                                        .createCriteria()
                                                        .andCondition(
                                                                "order_date >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                                                        .andCondition(
                                                                "order_date < '"
                                                                        + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                                                        + "'");
                                                mapper.deleteByExample(baseBillEmp);
                                                mapper.batchInsertSelective(baseBills);

                                                // 批量生成并保存中间表开单明细
                                                generateAndSaveBaseBillDetailByOrderDate(date, orderRecords);

                        /*for (BaseBill baseBill : baseBills) {
                          Integer billId = baseBill.getBillId();
                          mapper.deleteByPrimaryKey(billId);
                          baseBillDetailMapper.deleteByBillId(billId);
                          mapper.insertSelective(baseBill);
                          // 保存账单明细
                          saveBaseBillDetail(billId);
                        }*/
                                            }
                                        }
                                    } finally {
                                        latch.countDown();
                                    }
                                }));
            }
            latch.await();
            BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);
        }
    }

    /**
     * 根据日期批量生成中间表开单明细并保存
     *
     * @param date 查询日期
     */
    private void generateAndSaveBaseBillDetailByOrderDate(String date, List<CouponOrder> orderRecords) {
        Example orderDetailEmp = new Example(CouponOrderDetail.class);
        orderDetailEmp
                .createCriteria()
                .andEqualTo("inservice", true)
                .andCondition("crt_time >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                .andCondition("crt_time < '" + new DateTime(date).plusDays(1).toString("yyyy-MM-dd") + "'");
        List<CouponOrderDetail> details = couponOrderDetailMapper.selectByExample(orderDetailEmp);
        if (StringHelper.isNotEmpty(details)) {
            List<BaseCouponBillDetail> baseBillDetails = generateBaseBillDetailByOrderDetail(details);
            // 订单明细收费记录
            Map<Integer, CouponOrder> collect = orderRecords.stream().collect(toMap(CouponOrder::getId, Function.identity(), (o, v) -> v));
            if (StringHelper.isNotEmpty(baseBillDetails)) {
                for (BaseCouponBillDetail baseBillDetail : baseBillDetails) {
                    CouponOrder couponOrder = collect.get(baseBillDetail.getOrderId());
                    baseBillDetail.setReceivedAmount(Objects.isNull(couponOrder) ? null : couponOrder.getReceivedAmount());
                }
                for (BaseCouponBillDetail baseBillDetail : baseBillDetails) {
                    baseCouponBillDetailMapper.deleteByPrimaryKey(baseBillDetail.getOrderDetailId());
                }
                baseCouponBillDetailMapper.batchInsertSelective(baseBillDetails);
            }
        }
    }

    /**
     * 构建中间表订单明细列表
     *
     * @param details 订单列表
     * @return
     */
    private List<BaseCouponBillDetail> generateBaseBillDetailByOrderDetail(List<CouponOrderDetail> details) {
        List<BaseCouponBillDetail> billDetails = new ArrayList<>();
        for (CouponOrderDetail detail : details) {
            BaseCouponBillDetail baseBillDetail = new BaseCouponBillDetail();
            baseBillDetail.setOrderDetailId(detail.getId());
            baseBillDetail.setOrgId(detail.getOrgId());
            baseBillDetail.setOrderId(detail.getOrderId());
            baseBillDetail.setCouponId(detail.getCouponId());
            baseBillDetail.setType(detail.getType());
            baseBillDetail.setCouponName(detail.getCouponName());
            baseBillDetail.setCouponNumber(detail.getCouponNumber());
            baseBillDetail.setExecutorId(detail.getExecutorId());
            baseBillDetail.setConsulterId(detail.getConsulterId());
            baseBillDetail.setQuantity(detail.getQuantity());
            baseBillDetail.setPrice(detail.getPrice());
            baseBillDetail.setReceivedAmount(detail.getReceivableAmount());
            baseBillDetail.setRemark(detail.getRemarks());
            baseBillDetail.setRemark(detail.getRemarks());
            baseBillDetail.setSaleChannelId(detail.getSaleChannelId());
            baseBillDetail.setReceivableAmount(detail.getReceivableAmount());
            billDetails.add(baseBillDetail);
        }
        return billDetails;
    }

    /**
     * 批量生成中间表账单记录
     *
     * @param orderRecords 订单记录列表
     * @return 账单记录列表
     */
    private Set<BaseCouponBill> generateBaseBillList(List<CouponOrder> orderRecords) {
        Set<BaseCouponBill> baseBills = new LinkedHashSet<>();
        if (StringHelper.isNotEmpty(orderRecords)) {
            for (CouponOrder orderRecord : orderRecords) {
                BaseCouponBill baseBill = new BaseCouponBill();
                baseBill.setBillId(orderRecord.getId());
                baseBill.setOrgId(orderRecord.getOrgId());
                baseBill.setPatientId(orderRecord.getPatientId());
                baseBill.setOrderStatus(orderRecord.getStatus());
                baseBill.setOrderNum(orderRecord.getOrderRecordNum());
                baseBill.setOrderAmount(orderRecord.getTotalAmount());
                baseBill.setBillerId(orderRecord.getCrtId());
                baseBill.setOrderDate(orderRecord.getCrtTime());
                baseBill.setReceivedAmount(new BigDecimal("0"));
                baseBill.setDebtAmount(new BigDecimal("0"));
                // 设置账单的收费信息
                setBaseBillChargeValue(orderRecord, baseBill);
                baseBills.add(baseBill);
            }
        }
        return baseBills;
    }

    public void updateBaseBill(BaseCouponBill baseBill) {
        mapper.updateByPrimaryKeySelective(baseBill);
    }
}
