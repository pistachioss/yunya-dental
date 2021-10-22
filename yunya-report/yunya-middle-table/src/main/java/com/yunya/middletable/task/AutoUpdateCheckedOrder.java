package com.yunya.middletable.task;

import com.yunya.middletable.dao.treatment.BillRecordMapper;
import com.yunya.middletable.service.BaseBillBiz;
import com.yunya.middletable.service.BaseTreatmentProcessBiz;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.models.treatment.BillRecord;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 简介: 自动更新收费订单
 *
 * @author: chow
 * @date: 2021/10/21 15:58
 * @description: 因自动收费无法更新中间表相关记录
 * @since: 1.0.0
 */
@Component
@EnableScheduling
public class AutoUpdateCheckedOrder {
  /** 线程池 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;
  /** 中间吧就诊流程 */
  @Resource private BaseTreatmentProcessBiz treatmentProcessBiz;
  /** 中间表账单 */
  @Resource private BaseBillBiz baseBillBiz;
  /** 订单 */
  @Resource private BillRecordMapper billRecordMapper;

  @Scheduled(cron = "0 50 13 * * ?")
  public void autoUpdateOrderAndTreatment() throws InterruptedException {
    BaseTreatmentProcess treatmentProcess = new BaseTreatmentProcess();
    treatmentProcess.setTreatStatus((byte) 3);
    List<BaseTreatmentProcess> treatmentProcesses =
        treatmentProcessBiz.selectList(treatmentProcess);
    if (!CollectionUtils.isEmpty(treatmentProcesses)) {
      CountDownLatch countDownLatch = new CountDownLatch(treatmentProcesses.size());
      importExcelThreadPool.submit(
          () -> {
            try {
              treatmentProcesses.forEach(
                  process -> {
                    treatmentProcessBiz.updateTreatProcessByRegisteredId(process.getRegisteredId());
                    BillRecord bill = new BillRecord();
                    bill.setTreatmentRecordId(process.getTreatmentId());
                    bill.setInservice(true);
                    BillRecord billRecord = billRecordMapper.selectOne(bill);
                    if (billRecord != null) {
                      baseBillBiz.addPatientIntegral(billRecord.getPatientId());
                      BaseBill baseBill = new BaseBill();
                      baseBill.setBillId(billRecord.getOrderRecordId());
                      baseBill.setFirstPrivilege(billRecord.getFirstPrivilege());
                      baseBill.setBillDate(billRecord.getCrtTime());
                      baseBill.setBillNum(billRecord.getBillNumber());
                      baseBill.setActualAmount(billRecord.getActualReceivableAmount());
                      baseBill.setReceivedAmount(billRecord.getReceivedAmount());
                      baseBill.setDebtAmount(billRecord.getDebtAmount());
                      baseBill.setCheckerId(billRecord.getCrtId());
                      baseBillBiz.updateBaseBill(baseBill);
                    }
                  });
            } finally {
              countDownLatch.countDown();
            }
          });
      countDownLatch.await();
    }
  }
}
