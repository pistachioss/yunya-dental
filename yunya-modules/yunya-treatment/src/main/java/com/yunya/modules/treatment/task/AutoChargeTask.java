package com.yunya.modules.treatment.task;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayRecordLog;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.biz.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBill;
import static com.yunya.framework.common.constant.BusinessConstants.TREATMENT_PROCESSED_STATUS;
import static com.yunya.framework.common.constant.BusinessConstants.TREATMENT_PROCESS_FINISH_STATUS;

/**
 * 简介: 自动收费定时任务
 *
 * @author: chow
 * @date: 2021/10/18 09:27
 * @description: 所有就诊完成的未收费的订单，当天23点（23:50）自动收费确认
 * @since: 1.0.0
 */
@Slf4j
@Component
@EnableScheduling
public class AutoChargeTask {

  /** 线程池 */
  @Resource(name = "treatmentThreadPool")
  private ExecutorService executorService;
  /** 就诊记录业务层 */
  @Resource private TreatmentRecordBiz treatmentRecordBiz;
  /** 开单业务层 */
  @Resource private OrderRecordBiz orderRecordBiz;
  /** 收费业务层 */
  @Resource private TollBiz tollBiz;
  /** 账单业务层 */
  @Resource private BillRecordBiz billRecordBiz;

  @Resource private TreatTollBiz TreatTollBiz;
  /** 中间表 */
  @Resource private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

  /**
   * 定时任务自动收费
   *
   * @throws InterruptedException
   */
  @Scheduled(cron = "0 50 23 * * ?")
  public void autoCharge() throws InterruptedException {
    // 获取当天全部就诊完成数据列表
    List<Integer> treatmentRecordIds =
        treatmentRecordBiz.getTreatCompletedList(
            DateUtil.format(new Date(), "yyyy-MM-dd"),
            TREATMENT_PROCESSED_STATUS);
    if (!CollectionUtils.isEmpty(treatmentRecordIds)) {
      List<OrderRecord> orderRecords = orderRecordBiz.getUnCheckedOrderRecords(treatmentRecordIds);
      if (!CollectionUtils.isEmpty(orderRecords)) {
        CountDownLatch countDownLatch = new CountDownLatch(orderRecords.size());
        orderRecords.forEach(
            orderRecord ->
                executorService.submit(
                    () -> {
                      try {
                        Integer treatmentId = orderRecord.getTreatmentRecordId();
                        // 更新就诊记录状态为离店
                        TreatmentRecord treatmentRecord = new TreatmentRecord();
                        treatmentRecord.setId(treatmentId);
                        treatmentRecord.setStatus(
                            TREATMENT_PROCESS_FINISH_STATUS);
                        int result = treatmentRecordBiz.updateTreatmentStatus(treatmentRecord, treatmentId, TREATMENT_PROCESSED_STATUS);
                        if (result > 0) {
                          // 保存账单记录
                          BillRecord billRecord = buildBillRecord(orderRecord);
                          billRecordBiz.insertBillRecord(billRecord);
                          // 保存开单明细收费记录
                          tollBiz.saveOrderDetailPayRecordWithNoDiscount(
                              BigDecimal.ZERO, orderRecord.getId(), billRecord.getId(), true);
                          // 更新开单状态为结账
                          orderRecord.setStatus(BusinessConstants.ORDER_FINISH_STATUS);
                          int i = orderRecordBiz.updateOrderStatus(orderRecord);
                          if (i > 0) {
                            rabbitMqServiceFeign.sendMessage(billRecord.getOrderRecordId(), 0, BaseBill);
                            log.info("发送中间表开单记录同步消息{}", "开单记录ID：-------》》》" + billRecord.getOrderRecordId());
                          }
                        }
                      } finally {
                        countDownLatch.countDown();
                      }
                    }));
        countDownLatch.await();
      }
    }
  }

  /**
   * 构建账单记录
   *
   * @param orderRecord
   * @return
   */
  private BillRecord buildBillRecord(OrderRecord orderRecord) {
    BillRecord billRecord =
        tollBiz.generateBillRecord(
            orderRecord.getTreatmentRecordId(),
            orderRecord.getPatientId(),
            orderRecord.getId(),
            orderRecord.getOrgId());
    billRecord.setPrivilegeType((byte) 0);
    BigDecimal totalAmount = orderRecord.getTotalAmount();
    billRecord.setReceivableAmount(totalAmount);
    billRecord.setPrivilegeAmount(BigDecimal.ZERO);
    billRecord.setActualReceivableAmount(totalAmount);
    billRecord.setReceivedAmount(BigDecimal.ZERO);
    billRecord.setDebtAmount(totalAmount);
    billRecord.setInvoice(false);
    billRecord.setCrtId(BusinessConstants.ADMIN_ID);
    billRecord.setCrtName(BusinessConstants.ADMIN_NAME);
    return billRecord;
  }


  /**
   * 定时任务：每天凌晨1点自动处理昨天的就诊收费日志
   *
   * @throws InterruptedException
   */
//  @Scheduled(cron = "0 30 01 * * ?")
  public void autoProcessTreatTollLog() {
    Date yesterday = DateUtil.yesterday();
    log.info("开始处理：{}的就诊账单收费日志", yesterday);
    Example example = new Example(BillRecord.class);
    example.createCriteria().andEqualTo("crt_time", yesterday);
    List<BillRecord> bills = billRecordBiz.selectByExample(example);
    if (StringHelper.isEmpty(bills)) {
      log.info("日期：{}， 暂无账单", yesterday);
    }
    for (BillRecord bill : bills) {
      BigDecimal debtAmount = bill.getDebtAmount();
      if (StringHelper.leZero(debtAmount)) {
        List<BillPayRecordLog> tollLogs = TreatTollBiz.findTreatTollLog(bill.getOrderRecordId());
        if (StringHelper.isNotEmpty(tollLogs)) {
          tollLogs.forEach(tollLog->{
            Byte status = tollLog.getStatus();
            if (status == 2) {

            }
          });
        }
      }
    }
    log.info("{}的就诊账单收费日志处理完毕", yesterday);
  }
}
