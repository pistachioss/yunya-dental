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
import com.yunya.middletable.dao.report.BasePatientOriginLogMapper;
import com.yunya.middletable.dao.report.credits_shop.CreditsShopMapper;
import com.yunya.middletable.dao.treatment.BillPayDetailRecordMapper;
import com.yunya.middletable.dao.treatment.BillPayRecordMapper;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import com.yunya.models.credits_shop.CreditsShop;
import com.yunya.models.patient_central.MemberExpendRecord;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BaseBillPayDetail;
import com.yunya.models.report.BasePatientOriginLog;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
@Transactional(rollbackFor = Exception.class)
public class BaseBillPayBiz extends BaseBiz<BaseBillPayMapper, BaseBillPay> {

  /** 账单收费记录 */
  @Resource private BillPayRecordMapper billPayRecordMapper;
  /** 账单收费明细 */
  @Resource private BillPayDetailRecordMapper billPayDetailRecordMapper;
  /** 中间表收费明细 */
  @Resource private BaseBillPayDetailMapper baseBillPayDetailMapper;
  /** 会员卡消费记录 */
  @Resource private MemberExpendRecordMapper memberExpendRecordMapper;
  /** 预付款消费记录 */
  @Resource private PrepaidExpendRecordMapper prepaidExpendRecordMapper;
  /** 患者推荐关系 */
  @Resource private BasePatientOriginLogMapper basePatientOriginLogMapper;
  /** 患者积分信息 */
  @Resource private CreditsShopMapper creditsShopMapper;
  /** 订单记录 */
  @Resource private BaseBillMapper baseBillMapper;
  /** 积分商城业务层 */
  @Autowired
  private CreditsShopBiz creditsShopBiz;
  /** 线程池 */
  @Resource(name = "customizeThreadPool")
  private ExecutorService importExcelThreadPool;

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
        BillPayRecord payRecord = billPayRecordMapper.selectByPrimaryKey(dataId);
        Integer patientId = payRecord.getPatientId();
        mapper.deleteByPrimaryKey(dataId);
        if (null != baseBillPay) {
          mapper.insertSelective(baseBillPay);
          // 保存收费记录明细
          saveBillPayDetailRecord(dataId);
          // 推荐积分
          addPatientIntegral(baseBillPay.getBillId());
          // 增加会员积分  1元=1积分
          creditsShopBiz.ivyConsumeAddCredits(patientId,baseBillPay.getReceivedAmount(),baseBillPay.getBillPayId());
        } else {
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
      log.info("BaseBillPayBiz_saveBillPayDetailRecord_收费记录明细列表---:{}", billPayDetailRecords);
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

  /**
   * 判断是否首次下单，若是则推荐者增加500积分
   */
  public void addPatientIntegral(Integer patientId){
    Integer count =  baseBillMapper.selectCountByPatientId(patientId);
    CreditsShop addPatientIntegral = new CreditsShop();
    if (count <= 0){
      BasePatientOriginLog basePatientOrigin = new BasePatientOriginLog();
      basePatientOrigin.setPatientId(patientId);
      basePatientOrigin.setOriginType(2);
      BasePatientOriginLog basePatientOriginLog = basePatientOriginLogMapper.selectOne(basePatientOrigin);
      if (basePatientOriginLog != null){
        CreditsShop patientCreditsShop = creditsShopMapper.selectCreditsShopByPatientId(basePatientOriginLog.getOriginId());
        if (patientCreditsShop != null){
          addPatientIntegral.setPatientId(patientCreditsShop.getPatientId());
          // recommend 患者推荐
          addPatientIntegral.setType("recommend");
          addPatientIntegral.setChannel((byte)0);
          addPatientIntegral.setOrderNum("");
          addPatientIntegral.setCreditsAccount(patientCreditsShop.getCreditsAccount()+500);
          addPatientIntegral.setCredits(0L);
          addPatientIntegral.setCreditsOption((byte)1);
          addPatientIntegral.setActualPrice(0);
          addPatientIntegral.setItemCode("");
          addPatientIntegral.setDescription("500");
          addPatientIntegral.setRemarks("患者推荐");
          addPatientIntegral.setInservice(false);
          addPatientIntegral.setCrtId(0);
          addPatientIntegral.setCrtTime(new Date());
          addPatientIntegral.setUpdId(0);
          addPatientIntegral.setUpdTime(new Date());
        }else {
          // 没有患者积分帐户就新建
          addPatientIntegral.setPatientId(patientCreditsShop.getPatientId());
          // recommend 患者推荐
          addPatientIntegral.setType("recommend");
          addPatientIntegral.setChannel((byte)0);
          addPatientIntegral.setOrderNum("");
          addPatientIntegral.setCreditsAccount(500L);
          addPatientIntegral.setCredits(0L);
          addPatientIntegral.setCreditsOption((byte)1);
          addPatientIntegral.setActualPrice(0);
          addPatientIntegral.setItemCode("");
          addPatientIntegral.setDescription("500");
          addPatientIntegral.setRemarks("患者推荐");
          addPatientIntegral.setInservice(false);
          addPatientIntegral.setCrtId(0);
          addPatientIntegral.setCrtTime(new Date());
          addPatientIntegral.setUpdId(0);
          addPatientIntegral.setUpdTime(new Date());
        }
        // 增加500积分
        creditsShopMapper.insertSelective(addPatientIntegral);
      }
    }
  }

}
