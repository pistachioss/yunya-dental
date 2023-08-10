package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.discount.CouponBillPayDetailMapper;
import com.yunya.middletable.dao.discount.CouponBillPayMapper;
import com.yunya.middletable.dao.report.BaseCouponBillPayDetailMapper;
import com.yunya.middletable.dao.report.BaseCouponBillPayMapper;
import com.yunya.models.discount.CouponBillPay;
import com.yunya.models.discount.CouponBillPayDetail;
import com.yunya.models.report.BaseCouponBillPay;
import com.yunya.models.report.BaseCouponBillPayDetail;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Service
public class BaseCouponBillPayBiz extends BaseBiz<BaseCouponBillPayMapper, BaseCouponBillPay> {

    /**
     * 账单收费记录
     */
    @Resource
    private CouponBillPayMapper couponBillPayMapper;
    /**
     * 账单收费明细
     */
    @Resource
    private CouponBillPayDetailMapper couponBillPayDetailMapper;
    /**
     * 中间表收费明细
     */
    @Resource
    private BaseCouponBillPayDetailMapper baseCouponBillPayDetailMapper;
    /**
     * 线程池
     */
    @Resource(name = "customizeThreadPool")
    private ExecutorService importExcelThreadPool;



    /**
     * 根据消息类型操作（新增/修改/删除）中间表入账方式
     *
     * @param msg 消息
     */
    public void operateBillPay(MessageModel msg) {
        Integer dataId = (Integer) msg.getParamMap().get("id");
        BaseCouponBillPay baseBillPay = generateBaseBillPay(dataId);
        Integer operateType = msg.getOperateType();
        switch (operateType) {
            case 0:
            case 1:
            case 2:
                mapper.deleteByPrimaryKey(dataId);
                if (null != baseBillPay) {
                    mapper.insertSelective(baseBillPay);
                    // 保存收费记录明细
                    log.info("保存卡券收费记录明细baseBillPay: {}", baseBillPay);
                    saveBillPayDetailRecord(dataId);
                }
            default:
                break;
        }
    }


    /**
     * 保存收费记录收费明细
     *
     * @param billPayRecordId 收费记录ID
     */
    private void saveBillPayDetailRecord(Integer billPayRecordId) {
        CouponBillPayDetail billPayDetailRecord = new CouponBillPayDetail();
        billPayDetailRecord.setBillPayId(billPayRecordId);
        billPayDetailRecord.setInservice(true);
        List<CouponBillPayDetail> billPayDetailRecords =
                couponBillPayDetailMapper.select(billPayDetailRecord);
        if (StringHelper.isNotEmpty(billPayDetailRecords)) {
            BaseCouponBillPayDetail billPayDetail = new BaseCouponBillPayDetail();
            billPayDetail.setBillPayId(billPayRecordId);
            baseCouponBillPayDetailMapper.delete(billPayDetail);
            billPayDetailRecords.forEach(
                    payDetailRecord -> {
                        BaseCouponBillPayDetail baseBillPayDetail = new BaseCouponBillPayDetail();
                        baseBillPayDetail.setBillPayDetailId(payDetailRecord.getId());
                        baseBillPayDetail.setOrderId(payDetailRecord.getOrderId());
                        baseBillPayDetail.setBillPayId(payDetailRecord.getBillPayId());
                        Byte type = payDetailRecord.getType();
                        baseBillPayDetail.setAccountItemId(payDetailRecord.getAccountItemId());
                        baseBillPayDetail.setAccountItemName(payDetailRecord.getAccountItemName());
                        baseBillPayDetail.setType(type.intValue());
                        baseBillPayDetail.setAmount(payDetailRecord.getAmount());
                        baseBillPayDetail.setPrincipalAmount(payDetailRecord.getPrincipalAmount());
                        baseBillPayDetail.setBonusAmount(payDetailRecord.getBonusAmount());
                        baseBillPayDetail.setPatientNum(payDetailRecord.getPatientNum());
                        baseCouponBillPayDetailMapper.insertSelective(baseBillPayDetail);
                    });
        }
    }

    /**
     * 构建中间表收费记录
     *
     * @param billPayId 收费记录ID
     * @return BaseBillPay
     */
    private BaseCouponBillPay generateBaseBillPay(Integer billPayId) {
        CouponBillPay billPayRecord = couponBillPayMapper.selectByPrimaryKey(billPayId);
        if (null != billPayRecord && billPayRecord.getInservice()) {
            return record2BaseReport(billPayRecord);
        }
        return null;
    }

    private BaseCouponBillPay record2BaseReport(CouponBillPay billPayRecord) {
        BaseCouponBillPay baseBillPay = new BaseCouponBillPay();
        baseBillPay.setBillPayId(billPayRecord.getId());
        baseBillPay.setOrderId(billPayRecord.getOrderId());
        baseBillPay.setOrgId(billPayRecord.getOrgId());
        baseBillPay.setPatientId(billPayRecord.getPatientId());
        baseBillPay.setPayeeUserId(billPayRecord.getCrtId());
        baseBillPay.setPayeeDate(billPayRecord.getCrtTime());
        baseBillPay.setReceivedAmount(billPayRecord.getReceivedAmount());
        baseBillPay.setOweAmount(billPayRecord.getOweAmount());
        return baseBillPay;
    }

    /**
     * 拉取某段时间内的入账方式数据并更新中间表
     *
     * @param form 拉取时间
     */
    public void pullBillPayData(PullForm form) throws InterruptedException {
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
                                        Example billPayRecordEmp = new Example(CouponBillPay.class);
                                        billPayRecordEmp
                                                .createCriteria()
                                                .andEqualTo("inservice", true)
                                                .andCondition(
                                                        "crt_time >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                                                .andCondition(
                                                        "crt_time < '"
                                                                + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                                                + "'");
                                        List<CouponBillPay> billPayRecords =
                                                couponBillPayMapper.selectByExample(billPayRecordEmp);
                                        if (StringHelper.isNotEmpty(billPayRecords)) {
                                            List<BaseCouponBillPay> baseBillPays = generateBaseBillPayList(billPayRecords);
                                            if (StringHelper.isNotEmpty(baseBillPays)) {
                                                for (BaseCouponBillPay baseBillPay : baseBillPays) {
                                                    Integer billPayBillPayId = baseBillPay.getBillPayId();
                                                    mapper.deleteByPrimaryKey(billPayBillPayId);
                                                    baseCouponBillPayDetailMapper.deleteByBillPayId(billPayBillPayId);
                                                    mapper.insertSelective(baseBillPay);
                                                    // 保存收费记录明细
                                                    saveBillPayDetailRecord(billPayBillPayId);
                                                }
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
     * 构建中间表账单支付记录列表
     *
     * @param billPayRecords 账单支付记录
     * @return 中间表账单支付记录
     */
    private List<BaseCouponBillPay> generateBaseBillPayList(List<CouponBillPay> billPayRecords) {
        List<BaseCouponBillPay> baseBillPays = new ArrayList<>();
        for (CouponBillPay billPayRecord : billPayRecords) {
            BaseCouponBillPay baseBillPay = new BaseCouponBillPay();
            baseBillPay.setBillPayId(billPayRecord.getId());
            baseBillPay.setOrderId(billPayRecord.getOrderId());
            baseBillPay.setOrgId(billPayRecord.getOrgId());
            baseBillPay.setPatientId(billPayRecord.getPatientId());
            baseBillPay.setPayeeUserId(billPayRecord.getCrtId());
            baseBillPay.setPayeeDate(billPayRecord.getCrtTime());
            baseBillPay.setReceivedAmount(billPayRecord.getReceivedAmount());
            baseBillPay.setOweAmount(billPayRecord.getOweAmount());
            baseBillPays.add(baseBillPay);
        }
        return baseBillPays;
    }
}
