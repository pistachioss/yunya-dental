package com.yunya.modules.treatment.task;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.biz.BillRecordBiz;
import com.yunya.modules.treatment.biz.OrderRecordBiz;
import com.yunya.modules.treatment.biz.TollBiz;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBill;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTreatmentProcess;

/**
 * 简介: 自动收费定时任务
 *
 * @author: chow
 * @date: 2021/10/18 09:27
 * @description: 所有就诊完成的未收费的订单，当天23点（23:00）自动收费确认
 * @since: 1.0.0
 */
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
  /** 消息队列 */
  @Resource private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

  /**
   * 定时任务自动收费
   *
   * @throws InterruptedException
   */
  @Scheduled(cron = "0 30 14 * * ?")
  public void autoCharge() throws InterruptedException {
    // 获取当天全部就诊完成数据列表
    List<Integer> treatmentRecordIds =
        treatmentRecordBiz.getTreatCompletedList(
            DateUtil.format(new Date(), "yyyy-MM-dd"),
            BusinessConstants.TREATMENT_PROCESSED_STATUS);
    if (!CollectionUtils.isEmpty(treatmentRecordIds)) {
      List<OrderRecord> orderRecords = orderRecordBiz.getUnCheckedOrderRecords(treatmentRecordIds);
      if (!CollectionUtils.isEmpty(orderRecords)) {
        CountDownLatch countDownLatch = new CountDownLatch(orderRecords.size());
        orderRecords.forEach(
            orderRecord ->
                executorService.submit(
                    () -> {
                      try {
                        // 更新就诊记录状态为离店
                        TreatmentRecord treatmentRecord =
                            treatmentRecordBiz.selectById(orderRecord.getTreatmentRecordId());
                        treatmentRecord.setStatus(
                            BusinessConstants.TREATMENT_PROCESS_FINISH_STATUS);
                        treatmentRecordBiz.updateTreatmentStatus(treatmentRecord);
                        Integer appointmentId = treatmentRecord.getAppointmentId();
                        if (!ObjectUtils.isEmpty(appointmentId)) {
                          rabbitMqServiceFeign.sendMessage(
                              appointmentId, 0, 1, BaseTreatmentProcess);
                        } else {
                          rabbitMqServiceFeign.sendMessage(
                              treatmentRecord.getRegisteredId(), 1, 1, BaseTreatmentProcess);
                        }
                        // 保存账单记录
                        BillRecord billRecord = buildBillRecord(orderRecord);
                        billRecordBiz.insertSelective(billRecord);
                        // 保存开单明细收费记录
                        tollBiz.saveOrderDetailPayRecordWithNoDiscount(
                            BigDecimal.ZERO, orderRecord.getId(), billRecord.getId(), true);
                        // 更新开单状态为结账
                        orderRecord.setStatus(BusinessConstants.ORDER_FINISH_STATUS);
                        int updateOrderStatus = orderRecordBiz.updateOrderStatus(orderRecord);
                        if (updateOrderStatus > 0) {
                          rabbitMqServiceFeign.sendMessage(orderRecord.getId(), 1, BaseBill);
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
}
