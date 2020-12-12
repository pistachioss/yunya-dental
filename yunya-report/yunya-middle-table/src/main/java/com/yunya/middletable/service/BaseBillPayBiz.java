package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.patient.MemberExpendRecordMapper;
import com.yunya.middletable.dao.patient.PrepaidExpendRecordMapper;
import com.yunya.middletable.dao.report.BaseBillPayDetailMapper;
import com.yunya.middletable.dao.report.BaseBillPayMapper;
import com.yunya.middletable.dao.treatment.BillPayDetailRecordMapper;
import com.yunya.middletable.dao.treatment.BillPayRecordMapper;
import com.yunya.models.patient_central.MemberExpendRecord;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BaseBillPayDetail;
import com.yunya.models.system.AccountItem;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 中间表收费记录处理业务层
 *
 * @author: chow
 * @date: 2020/12/11 20:29
 * @description:
 * @since: 1.0.0
 */
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
        if (null != baseBillPay) {
          mapper.insertSelective(baseBillPay);
          saveBillPayDetailRecord(dataId);
        } else {
          mapper.deleteByPrimaryKey(dataId);
          baseBillPayDetailMapper.deleteByBillPayId(dataId);
        }
        break;
      case 1:
        if (null != baseBillPay) {
          BaseBillPay result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.insertSelective(baseBillPay);
            saveBillPayDetailRecord(dataId);
          } else {
            mapper.updateByPrimaryKeySelective(baseBillPay);
            updateBillPayDetailRecord(dataId);
          }
        } else {
          mapper.deleteByPrimaryKey(dataId);
          baseBillPayDetailMapper.deleteByBillPayId(dataId);
        }
        break;
      case 2:
        if (null == baseBillPay) {
          mapper.deleteByPrimaryKey(dataId);
          baseBillPayDetailMapper.deleteByBillPayId(dataId);
        } else {
          mapper.insertSelective(baseBillPay);
          saveBillPayDetailRecord(dataId);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 根据收费记录ID更新收费明细
   *
   * @param billPayRecordId 收费记录ID
   */
  private void updateBillPayDetailRecord(Integer billPayRecordId) {
    BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
    billPayDetailRecord.setBillPayRecordId(billPayRecordId);
    List<BillPayDetailRecord> billPayDetailRecords =
        billPayDetailRecordMapper.select(billPayDetailRecord);
    if (StringHelper.isNotEmpty(billPayDetailRecords)) {
      billPayDetailRecords.forEach(
          payDetailRecord -> {
            Integer payDetailRecordId = payDetailRecord.getId();
            billPayDetailRecordMapper.deleteByPrimaryKey(payDetailRecordId);
            BaseBillPayDetail baseBillPayDetail = new BaseBillPayDetail();
            baseBillPayDetail.setBillPayDetailRecordId(payDetailRecord.getId());
            baseBillPayDetail.setBillId(payDetailRecord.getOrderRecordId());
            baseBillPayDetail.setBillPayId(payDetailRecord.getBillPayRecordId());
            baseBillPayDetail.setType(payDetailRecord.getType());
            baseBillPayDetail.setAccountItemId(payDetailRecord.getAccountItemId());
            baseBillPayDetail.setPrincipalAmount(payDetailRecord.getAmount());
            baseBillPayDetail.setBonusAmount(new BigDecimal("0"));
            baseBillPayDetailMapper.insertSelective(baseBillPayDetail);
          });
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
    List<BillPayDetailRecord> billPayDetailRecords =
        billPayDetailRecordMapper.select(billPayDetailRecord);
    if (StringHelper.isNotEmpty(billPayDetailRecords)) {
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
              case 0:
                String memberNum = payDetailRecord.getRemark();
                if (StringHelper.isNotBlank(memberNum)) {
                  MemberExpendRecord memberExpendRecord = new MemberExpendRecord();
                  memberExpendRecord.setBillPayRecordId(billPayRecordId);
                  memberExpendRecord.setMemberId(memberNum);
                  MemberExpendRecord memberExpendRecordResult =
                      memberExpendRecordMapper.selectOne(memberExpendRecord);
                  if (null != memberExpendRecordResult) {
                    baseBillPayDetail.setPrincipalAmount(
                        memberExpendRecordResult.getExpendPrincipal());
                    baseBillPayDetail.setBonusAmount(memberExpendRecordResult.getExpendGift());
                  }
                }
                break;
                // 预付款
              case 1:
                String prepaidNum = payDetailRecord.getRemark();
                if (StringHelper.isNotBlank(prepaidNum)) {
                  PrepaidExpendRecord prepaidExpendRecord = new PrepaidExpendRecord();
                  prepaidExpendRecord.setBillPayRecordId(billPayRecordId);
                  prepaidExpendRecord.setPrepaidId(prepaidNum);
                  PrepaidExpendRecord prepaidExpendRecordResult =
                      prepaidExpendRecordMapper.selectOne(prepaidExpendRecord);
                  if (null != prepaidExpendRecordResult) {
                    baseBillPayDetail.setPrincipalAmount(
                        prepaidExpendRecordResult.getExpendPrincipal());
                    baseBillPayDetail.setBonusAmount(prepaidExpendRecordResult.getExpendGift());
                  }
                }
                break;
              default:
                baseBillPayDetail.setPrincipalAmount(payDetailRecord.getAmount());
                break;
            }
            baseBillPayDetailMapper.deleteByPrimaryKey(billPayDetailRecord.getId());
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
  public void pullBillPayData(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example emp = new Example(AccountItem.class);
    emp.createCriteria().andBetween("updTime", startDate, endDate);
    List<BillPayRecord> billPayRecords = billPayRecordMapper.selectByExample(emp);
    if (StringHelper.isNotEmpty(billPayRecords)) {
      billPayRecords.forEach(
          billPayRecord -> {
            Integer billPayRecordId = billPayRecord.getId();
            mapper.deleteByPrimaryKey(billPayRecordId);
            BaseBillPay baseBillPay = generateBaseBillPay(billPayRecordId);
            mapper.insertSelective(baseBillPay);
            saveBillPayDetailRecord(billPayRecordId);
          });
    }
  }
}
