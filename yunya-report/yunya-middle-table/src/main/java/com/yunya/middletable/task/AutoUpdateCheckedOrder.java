package com.yunya.middletable.task;

import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.middletable.dao.treatment.BillRecordMapper;
import com.yunya.middletable.dao.treatment.TreatmentRecordMapper;
import com.yunya.middletable.service.BaseBillBiz;
import com.yunya.middletable.service.BaseTreatmentProcessBiz;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseTreatmentProcess;
import com.yunya.models.treatment.BillRecord;
import com.yunya.models.treatment.TreatmentRecord;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
  /** 接诊 */
  @Resource private TreatmentRecordMapper treatmentRecordMapper;
  /** 中间吧就诊流程 */
  @Resource private BaseTreatmentProcessBiz treatmentProcessBiz;
  /** 中间表账单 */
  @Resource private BaseBillBiz baseBillBiz;
  /** 订单 */
  @Resource private BillRecordMapper billRecordMapper;

  @Scheduled(cron = "0 55 16 * * ?")
  public void autoUpdateOrderAndTreatment() throws InterruptedException {
    BaseTreatmentProcess treatmentProcess = new BaseTreatmentProcess();
    treatmentProcess.setTreatStatus((byte) 3);
    List<BaseTreatmentProcess> treatmentProcesses =
        treatmentProcessBiz.selectList(treatmentProcess);
    if (!CollectionUtils.isEmpty(treatmentProcesses)) {
      CountDownLatch countDownLatch = new CountDownLatch(treatmentProcesses.size());
      BillRecord bill = new BillRecord();
      BaseBill baseBill = new BaseBill();
      importExcelThreadPool.submit(
          () -> {
            try {
              treatmentProcesses.forEach(
                  process -> {
                    Integer treatmentId = process.getTreatmentId();
                    TreatmentRecord treatmentRecord =
                        treatmentRecordMapper.selectByPrimaryKey(treatmentId);
                    if (BusinessConstants.TREATMENT_PROCESS_FINISH_STATUS.equals(
                        treatmentRecord.getStatus())) {
                      process.setTreatStatus((byte) 4);
                    }
                    treatmentProcessBiz.updateSelectiveById(treatmentProcess);
                    bill.setTreatmentRecordId(treatmentId);
                    bill.setInservice(true);
                    BillRecord billRecord = billRecordMapper.selectOne(bill);
                    if (!ObjectUtils.isEmpty(billRecord)) {
                      baseBill.setBillId(billRecord.getOrderRecordId());
                      baseBill.setFirstPrivilege(billRecord.getFirstPrivilege());
                      baseBill.setBillDate(billRecord.getCrtTime());
                      baseBill.setBillNum(billRecord.getBillNumber());
                      baseBill.setActualAmount(billRecord.getActualReceivableAmount());
                      baseBill.setReceivedAmount(billRecord.getReceivedAmount());
                      baseBill.setDebtAmount(billRecord.getDebtAmount());
                      baseBill.setCheckerId(billRecord.getCrtId());
                      baseBillBiz.updateSelectiveById(baseBill);
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
