package com.yunya.modules.treatment.task;

import com.yunya.feign.treatment.domain.model.TollModel;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.treatment.biz.OrderRecordBiz;
import com.yunya.modules.treatment.biz.TollBiz;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 简介: 自动收费定时任务
 *
 * @author: chow
 * @date: 2021/10/18 09:27
 * @description: 所有就诊完成的未收费的订单，当天23点（23:00）自动收费确认
 * @since: 1.0.0
 */
@Component
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

  @Scheduled(cron = "0 0 13 * * ?")
  public void autoCharge() {
    // 获取当天全部就诊完成数据列表
    List<Integer> treatmentRecordIds =
        treatmentRecordBiz.getTreatCompletedList(
            DateUtil.format(new Date(), "yyyy-MM-dd"),
            BusinessConstants.TREATMENT_PROCESSED_STATUS);
    if (!CollectionUtils.isEmpty(treatmentRecordIds)) {
      List<OrderRecord> orderRecords = orderRecordBiz.getUnCheckedOrderRecords(treatmentRecordIds);
      if (!CollectionUtils.isEmpty(orderRecords)) {
        CountDownLatch categoryLatch = new CountDownLatch(orderRecords.size());
        TollModel model = new TollModel();
        model.setDiscountType((byte) 0);
        orderRecords.forEach(
            orderRecord -> {
              model.setOrderRecordId(orderRecord.getId());
              model.setOutstandingAmount(orderRecord.getTotalAmount());
              executorService.submit(
                  () -> {
                    try {
                      tollBiz.confirmCharge(model);
                    } finally {
                      categoryLatch.countDown();
                    }
                  });
            });
      }
    }
  }
}
