package com.yunya.middletable.service;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.MemberExpendRecordMapper;
import com.yunya.middletable.dao.patient.PrepaidExpendRecordMapper;
import com.yunya.middletable.dao.report.BaseBillDetailMapper;
import com.yunya.middletable.dao.report.BaseBillMapper;
import com.yunya.middletable.dao.report.BaseBillPayDetailMapper;
import com.yunya.middletable.dao.report.BaseBillPayMapper;
import com.yunya.middletable.dao.treatment.BillPayDetailRecordMapper;
import com.yunya.middletable.dao.treatment.BillPayRecordMapper;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import com.yunya.models.patient_central.MemberExpendRecord;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import com.yunya.models.report.*;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
  /** 积分商城业务层 */
  @Autowired
  private CreditsShopBiz creditsShopBiz;
  @Autowired
  private BaseBillMapper baseBillMapper;
  @Autowired
  private BaseBillDetailMapper baseBillDetailMapper;
  @Autowired
  private BaseBillPayMapper baseBillPayMapper;
  /** 线程池 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

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
        log.info("\n\n\n==============撤销收费==============\n");
        log.info("====> dataId = {}\n",dataId);
        log.info("====>  baseBillPay = \n{}",baseBillPay);
        log.info("=======================================\n\n\n");
        if (null != baseBillPay) {
          mapper.insertSelective(baseBillPay);
          // 保存收费记录明细
          saveBillPayDetailRecord(dataId);
          // 增加会员积分
          addCredits(baseBillPay.getReceivedAmount(),baseBillPay.getBillPayId(),baseBillPay.getBillId());
        } else {
          // 撤销积分
          scrapCredits(dataId);
          baseBillPayDetailMapper.deleteByBillPayId(dataId);
        }
        break;
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
   * 增加会员积分
   * @param receivedAmount 应收金额
   * @param baseBillPayId 支付记录ID
   */
  private void addCredits(BigDecimal receivedAmount, Integer baseBillPayId,Integer billId) {
    if (billId != null) {
      BaseBill baseBill = baseBillMapper.selectByPrimaryKey(billId);

      if (baseBill == null || baseBill.getBillStatus() == 0 || hasScrapedCredits(baseBill.getPatientId())) {
        return ;
      } else if (receivedAmount != null && baseBillPayId != null) {
        receivedAmount = baseBill.getReceivedAmount();
        // 从总的收费中过滤出医疗费用（不包含医疗护理用品和其他商品）
        BigDecimal medicalExpenses = medicalExpenses(receivedAmount, billId);
        // 增加会员积分  1元=1积分
        JSONObject jsonObject = new JSONObject();

        BaseBillPay query = new BaseBillPay();
        query.setBillId(billId);
        List<BaseBillPay> baseBillPays = baseBillPayMapper.select(query);
        if (StringHelper.isNotEmpty(baseBillPays)) {
          List<Integer> baseBillPayIds = baseBillPays.stream().mapToInt(BaseBillPay::getBillPayId).boxed().collect(Collectors.toList());
          jsonObject.put("baseBillPayId",baseBillPayIds);
          jsonObject.put("baseBillId",billId);
          jsonObject.put("scrapCharges",0);
        }
        creditsShopBiz.ivyConsumeAddCredits(baseBill.getPatientId(), medicalExpenses, jsonObject.toJSONString(),(byte) 0);
      }
    }
  }

  /**
   * 是否有过调整收费
   * @param patientId 患者ID
   * @return 调整过账单返回true 否则返回false
   */
  private boolean hasScrapedCredits(Integer patientId) {
    CreditsShop lastCreditsInfo = creditsShopBiz.lastPatientCredits(patientId).getData();
    String remarks = lastCreditsInfo.getRemarks();
    JSONObject jsonObject = JSONObject.parseObject(remarks);
    Integer scrapCharges = jsonObject.getInteger("scrapCharges");
    return scrapCharges == 1;
  }

  /**
   * 撤销积分
   * @param baseBillPayId 支付记录ID
   */
  private void scrapCredits(Integer baseBillPayId) {
    BaseBillPayDetail baseBillPayQuery = new BaseBillPayDetail();
    baseBillPayQuery.setBillPayId(baseBillPayId);
    BaseBillPayDetail baseBillPay = baseBillPayDetailMapper.selectOne(baseBillPayQuery);
    log.info("\n\n====================scrapCredits===============\n");
    log.info("===========> baseBillPayId:{}\n",baseBillPayId);
    log.info("===========> baseBillPay: {}\n",baseBillPay);
    log.info("===========> baseBillPay.billId: {}\n", baseBillPay != null ? baseBillPay.getBillId() : "");
    BaseBill baseBill = baseBillMapper.selectByPrimaryKey(baseBillPay.getBillId());
    log.info("===========> baseBill: {}\n",baseBill);
    log.info("======================================================\n\n");
    Integer patientId = baseBill.getPatientId();
    Integer billId = baseBillPay.getBillId();
    Example example = new Example(CreditsShop.class);
    Example.Criteria criteria = example.createCriteria();
    criteria.andEqualTo("patientId",patientId);
    criteria.andEqualTo("type","offlineConsume");
    criteria.andEqualTo("channel",0);
    criteria.andEqualTo("creditsOption",0);
    String remarks = "{\"baseBillId\":"+billId+",\"baseBillPayId\":";
    criteria.andLike("remarks",remarks);
    example.orderBy("id").desc();
    List<CreditsShop> creditsShops = creditsShopBiz.selectByExample(example);
    if (StringHelper.isNotEmpty(creditsShops)) {
      CreditsShop creditsShop = creditsShops.get(0);
      String jsonRemarks = creditsShop.getRemarks();
      if (StringHelper.isNotBlank(jsonRemarks)) {
        JSONObject jsonObject = JSONObject.parseObject(jsonRemarks);
        jsonObject.put("scrapCharges",1);
        creditsShop.setRemarks(jsonObject.toJSONString());
      }
      creditsShop.setUpdId(patientId);
      creditsShop.setUpdTime(new Date(System.currentTimeMillis()));
      creditsShopBiz.updateSelectiveById(creditsShop);
    }
  }

  /**
   * 从总的收费中过滤出医疗费用（不包含医疗护理用品和其他商品）
   * @param receivedAmount 订单总费用
   * @param billId  订单编号
   * @return  返回医疗总费用（不包含医疗护理用品和其他商品）
   */
  private BigDecimal medicalExpenses(BigDecimal receivedAmount, Integer billId) {
    BaseBillDetail bb = new BaseBillDetail();
    bb.setBillId(billId);
    List<BaseBillDetail> billDetails = baseBillDetailMapper.select(bb);
    BigDecimal pPCPsExpenses = new BigDecimal(0);
    if (StringHelper.isNotEmpty(billDetails)) {
      for (BaseBillDetail item: billDetails) {
        Byte itemType = item.getItemType();
        if (itemType.intValue() == 1) {
          pPCPsExpenses = pPCPsExpenses.add(item.getReceivedAmount() == null ? new BigDecimal(0) : item.getReceivedAmount());
        }
      }
    }
    if (receivedAmount != null && receivedAmount.compareTo(pPCPsExpenses) >= 0) {
      return receivedAmount.subtract(pPCPsExpenses);
    }
    return new BigDecimal(0);
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
      BaseBillPay baseBillPay = new BaseBillPay();
      baseBillPay.setBillPayId(billPayId);
      baseBillPay.setBillId(billPayRecord.getOrderRecordId());
      baseBillPay.setOrgId(billPayRecord.getOrgId());
      baseBillPay.setTreatmentId(billPayRecord.getTreatmentRecordId());
      baseBillPay.setPayeeUserId(billPayRecord.getCrtId());
      baseBillPay.setPayeeDate(billPayRecord.getCrtTime());
      baseBillPay.setReceivedAmount(billPayRecord.getReceivedAmount());
      baseBillPay.setStillOweAmount(billPayRecord.getStillOweAmount());
      return baseBillPay;
    }
    return null;
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
}
