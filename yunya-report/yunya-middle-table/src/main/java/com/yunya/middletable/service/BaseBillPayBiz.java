package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.MemberExpendRecordMapper;
import com.yunya.middletable.dao.patient.PrepaidExpendRecordMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.report.BaseBillPayDetailMapper;
import com.yunya.middletable.dao.report.BaseBillPayMapper;
import com.yunya.middletable.dao.treatment.BillPayDetailRecordMapper;
import com.yunya.middletable.dao.treatment.BillPayRecordMapper;
import com.yunya.middletable.dao.treatment.OrderDetailMapper;
import com.yunya.middletable.service.credits_shop.BillCreditsCallback;
import com.yunya.models.patient_central.MemberExpendRecord;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BaseBillPayDetail;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 简介: 中间表收费记录处理业务层
 *
 * @author: chow
 * @date: 2020/12/11 20:29
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class BaseBillPayBiz extends BaseBiz<BaseBillPayMapper, BaseBillPay> {

  /** 账单收费记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单收费明细 */
  @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;
  /** 中间表收费明细 */
  @Autowired private BaseBillPayDetailMapper baseBillPayDetailMapper;
  /** 会员卡消费记录 */
  @Autowired private MemberExpendRecordMapper memberExpendRecordMapper;
  /** 预付款消费记录 */
  @Autowired private PrepaidExpendRecordMapper prepaidExpendRecordMapper;
  /** 员工收费时统计*/
  @Autowired private StatEmpPayBiz statEmpPayBiz;
  /** 订单明细*/
  @Autowired private OrderDetailMapper orderDetailMapper;
  /** 账单*/
  @Autowired private BaseBillMapper baseBillMapper;
  /** 线程池 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;
  @Resource(name = "billCreditsCallbackImpl")
  private BillCreditsCallback billCreditsCallback;
  //
  private Map<Integer,BillCreditsCallback> chain = new ConcurrentHashMap<>();

  public void addCallBack(Integer bId,BillCreditsCallback callback) {
    this.chain.put(bId,callback);
  }

  /** 本次免单支付 */
  private final Integer PAYMENT_BY_CUSTOMER_FREE = 23;
  /** 艾维员工免单 */
  private final Integer PAYMENT_BY_EMPLOYEE_FREE = 26;

  /**
   * 根据消息类型操作（新增/修改/删除）中间表入账方式
   *
   * @param msg 消息
   */
  public void operateBillPay(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseBillPay baseBillPay = generateBaseBillPay(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
      case 1:
      case 2:
        mapper.deleteByPrimaryKey(dataId);
        if (null != baseBillPay) {
          mapper.insertSelective(baseBillPay);
          // 保存收费记录明细
          log.info("保存收费记录明细baseBillPay: {}",baseBillPay);
          saveBillPayDetailRecord(dataId);
          if(!chain.isEmpty()){
            Iterator<Integer> iterator = chain.keySet().iterator();
            while (iterator.hasNext()) {
              // 回调积分增加方法，
              // 这里存在消息消费顺序性问题，为了防止正常业务读不到账单支付记录的情况而导致新增积分失败，所以这里使用回调的方式将积分做新增操作
              // 回调的逻辑查看BaseBillBiz.java 中baseBillPayBiz.addCallBack(...)方法
              Integer bid = iterator.next();
              chain.get(bid).baseBillBizHandlerFinish(bid);
              iterator.remove();
            }
          }
        } else {
          // 撤销积分
          billCreditsCallback.scrapCredits(dataId);
          baseBillPayDetailMapper.deleteByBillPayId(dataId);
        }
      default:
        statisticsInPayDate(dataId);
        break;
    }
  }

  /**
   * 账单收费时统计执行人的账单相关数据
   *
   * @param dataId
   */
  private void statisticsInPayDate(Integer dataId) {
    BaseBillPay baseBillPay = record2BaseReport(billPayRecordMapper.selectByPrimaryKey(dataId));
    Integer billId = baseBillPay.getBillId();
    OrderDetail query = new OrderDetail();
    query.setOrderRecordId(billId);
    List<OrderDetail> orderDetails = orderDetailMapper.select(query);
    BaseBill bill = new BaseBill();
    bill.setBillId(billId);
    BaseBill baseBill = baseBillMapper.selectOne(bill);
    if (StringHelper.isNotNull(baseBill)) {
      statEmpPayBiz.statisticsEmployeeByPayDate(orderDetails, baseBill, baseBillPay);
    }
  }

  /**
   * 保存收费记录收费明细
   *
   * @param billPayRecordId 收费记录ID
   */
  private void saveBillPayDetailRecord(Integer billPayRecordId) {
    BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
    billPayDetailRecord.setBillPayRecordId(billPayRecordId);
    billPayDetailRecord.setInservice(true);
    List<BillPayDetailRecord> billPayDetailRecords =
        billPayDetailRecordMapper.select(billPayDetailRecord);
    if (StringHelper.isNotEmpty(billPayDetailRecords)) {
      BaseBillPayDetail billPayDetail = new BaseBillPayDetail();
      billPayDetail.setBillPayId(billPayRecordId);
      baseBillPayDetailMapper.delete(billPayDetail);
      billPayDetailRecords.forEach(
          payDetailRecord -> {
            BaseBillPayDetail baseBillPayDetail = new BaseBillPayDetail();
            baseBillPayDetail.setBillPayDetailRecordId(payDetailRecord.getId());
            baseBillPayDetail.setBillId(payDetailRecord.getOrderRecordId());
            baseBillPayDetail.setBillPayId(payDetailRecord.getBillPayRecordId());
            Byte type = payDetailRecord.getType();
            baseBillPayDetail.setType(type);
            baseBillPayDetail.setAccountItemId(payDetailRecord.getAccountItemId());
            switch (type) {
                // 会员卡
              case 1:
                String memberNum = payDetailRecord.getRemark();
                if (StringHelper.isNotBlank(memberNum)) {
                  MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
                  memberExpendRecord.setBillPayRecordId(billPayRecordId);
                  memberExpendRecord.setMemberId(memberNum);
                  memberExpendRecord.setInservice(true);
                  MemberExpendRecord memberExpendRecordResult =
                      memberExpendRecordMapper.selectOne(memberExpendRecord);
                  log.info(
                      "memberExpendRecordMapper.selectOne_查询会员卡消费记录:{}", memberExpendRecordResult);
                  if (null != memberExpendRecordResult) {
                    baseBillPayDetail.setPrincipalAmount(
                        memberExpendRecordResult.getExpendPrincipal());
                    baseBillPayDetail.setBonusAmount(memberExpendRecordResult.getExpendGift());
                    baseBillPayDetail.setCardNum(memberNum);
                  }
                }
                break;
                // 预付款
              case 0:
                String prepaidNum = payDetailRecord.getRemark();
                if (StringHelper.isNotBlank(prepaidNum)) {
                  PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord();
                  prepaidExpendRecord.setBillPayRecordId(billPayRecordId);
                  prepaidExpendRecord.setPrepaidId(prepaidNum);
                  prepaidExpendRecord.setInservice(true);
                  PrepaidExpendRecord prepaidExpendRecordResult =
                      prepaidExpendRecordMapper.selectOne(prepaidExpendRecord);
                  log.info(
                      "prepaidExpendRecordMapper.selectOne_查询预付款消费记录:{}",
                      prepaidExpendRecordResult);
                  if (null != prepaidExpendRecordResult) {
                    baseBillPayDetail.setPrincipalAmount(
                        prepaidExpendRecordResult.getExpendPrincipal());
                    baseBillPayDetail.setBonusAmount(prepaidExpendRecordResult.getExpendGift());
                    baseBillPayDetail.setCardNum(prepaidNum);
                  }
                }
                break;
              default:
                baseBillPayDetail.setPrincipalAmount(payDetailRecord.getAmount());
                baseBillPayDetail.setCardNum(payDetailRecord.getRemark());//备注
                break;
            }
            baseBillPayDetailMapper.insertSelective(baseBillPayDetail);
          });
    }
  }

  /**
   * 构建中间表收费记录
   *
   * @param billPayId 收费记录ID
   * @return BaseBillPay
   */
  private BaseBillPay generateBaseBillPay(Integer billPayId) {
    BillPayRecord billPayRecord = billPayRecordMapper.selectByPrimaryKey(billPayId);
    if (null != billPayRecord && billPayRecord.getInservice()) {
      return record2BaseReport(billPayRecord);
    }
    return null;
  }

  private BaseBillPay record2BaseReport(BillPayRecord billPayRecord) {
    BaseBillPay baseBillPay = new BaseBillPay();
    baseBillPay.setBillPayId(billPayRecord.getId());
    baseBillPay.setBillId(billPayRecord.getOrderRecordId());
    baseBillPay.setOrgId(billPayRecord.getOrgId());
    baseBillPay.setTreatmentId(billPayRecord.getTreatmentRecordId());
    baseBillPay.setPayeeUserId(billPayRecord.getCrtId());
    baseBillPay.setPayeeDate(billPayRecord.getCrtTime());
    baseBillPay.setReceivedAmount(billPayRecord.getReceivedAmount());
    baseBillPay.setStillOweAmount(billPayRecord.getStillOweAmount());
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
                    Example billPayRecordEmp = new Example(BillPayRecord.class);
                    billPayRecordEmp
                        .createCriteria()
                        .andEqualTo("inservice", true)
                        .andCondition(
                            "crt_time >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                        .andCondition(
                            "crt_time < '"
                                + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                + "'");
                    List<BillPayRecord> billPayRecords =
                        billPayRecordMapper.selectByExample(billPayRecordEmp);
                    if (StringHelper.isNotEmpty(billPayRecords)) {
                      List<BaseBillPay> baseBillPays = generateBaseBillPayList(billPayRecords);
                      if (StringHelper.isNotEmpty(baseBillPays)) {
                        for (BaseBillPay baseBillPay : baseBillPays) {
                          Integer billPayBillPayId = baseBillPay.getBillPayId();
                          mapper.deleteByPrimaryKey(billPayBillPayId);
                          baseBillPayDetailMapper.deleteByBillPayId(billPayBillPayId);
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
  private List<BaseBillPay> generateBaseBillPayList(List<BillPayRecord> billPayRecords) {
    List<BaseBillPay> baseBillPays = new ArrayList<>();
    for (BillPayRecord billPayRecord : billPayRecords) {
      BaseBillPay baseBillPay = new BaseBillPay();
      baseBillPay.setBillPayId(billPayRecord.getId());
      baseBillPay.setBillId(billPayRecord.getOrderRecordId());
      baseBillPay.setOrgId(billPayRecord.getOrgId());
      baseBillPay.setTreatmentId(billPayRecord.getTreatmentRecordId());
      baseBillPay.setPayeeUserId(billPayRecord.getCrtId());
      baseBillPay.setPayeeDate(billPayRecord.getCrtTime());
      baseBillPay.setReceivedAmount(billPayRecord.getReceivedAmount());
      baseBillPay.setStillOweAmount(billPayRecord.getStillOweAmount());
      baseBillPays.add(baseBillPay);
    }
    return baseBillPays;
  }


  /**
   * 根据时间段批量操作中间表账单收费时统计
   *
   * @param form
   * @throws InterruptedException
   */
  /*public void pullPayDateStatistics(PullForm form) throws InterruptedException {
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
                            Example example = new Example(BaseBillPay.class);
                            example
                                    .createCriteria()
                                    .andCondition(
                                            "payee_date >= '" + new DateTime(date).toString("yyyy-MM-dd") + "'")
                                    .andCondition(
                                            "payee_date < '"
                                                    + new DateTime(date).plusDays(1).toString("yyyy-MM-dd")
                                                    + "'");
                            List<BaseBillPay> baseBillPays =
                                    mapper.selectByExample(example);
                            if (StringHelper.isNotEmpty(baseBillPays)) {
                              baseBillPays.forEach(vo-> statisticsInPayDate(vo.getBillPayId()));
                            }
                          } finally {
                            latch.countDown();
                          }
                        }));
      }
      latch.await();
      BaseTreatmentProcessBiz.printExceptionLog(resultFutures, log);
    }
  }*/
}
