package com.yunya.modules.treatment.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.vo.BillPayDetailRecordVO;
import com.yunya.feign.treatment.domain.vo.BillPayRecordVO;
import com.yunya.feign.treatment.domain.vo.BillPaymentAdjustDetailVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.treatment.BillExceptionHandleDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.treatment.mapper.BillExceptionHandleDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介: 账单异常处理详情业务层
 *
 * @author: chow
 * @date: 2020/9/25 10:18
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillExceptionHandleDetailRecordBiz
    extends BaseBiz<BillExceptionHandleDetailRecordMapper, BillExceptionHandleDetailRecord> {

  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 开单记录 */
  @Autowired private OrderRecordBiz orderRecordBiz;
  /** 账单记录 */
  @Autowired private BillRecordBiz billRecordBiz;
  /** 账单支付记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单支付明细记录 */
  @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;

  /**
   * 根据账单异常处理记录ID查询
   *
   * @param handledRecordId 被处理记录ID（收费记录ID）
   * @param billExceptionHandleRecordId 账单异常处理记录ID
   * @param preExceptionHandleRecordId 上一次异常处理记录ID
   */
  public BillPaymentAdjustDetailVO findBillPaymentAdjustDetail(
      Integer handledRecordId,
      Integer billExceptionHandleRecordId,
      Integer preExceptionHandleRecordId) {
    BillPaymentAdjustDetailVO billAdjustDetailInfo = new BillPaymentAdjustDetailVO();
    Map<String, Object> resultMap = new HashMap<>(16);
    BillPayRecord billPayRecord = billPayRecordMapper.selectByPrimaryKey(handledRecordId);
    // 调整前收费信息
    billAdjustDetailInfo.setBillPayRecordId(handledRecordId);
    billAdjustDetailInfo.setChargeDate(
        new DateTime(billPayRecord.getCrtTime()).toString("yyyy-MM-dd"));
    Integer orgId = billPayRecord.getOrgId();
    billAdjustDetailInfo.setOrgId(orgId);
    // todo 从缓存中查询组织信息
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    if (null != orgInfo) {
      billAdjustDetailInfo.setOrgName(orgInfo.getAbbreviation());
    }
    billAdjustDetailInfo.setPayeeId(billPayRecord.getCrtId());
    billAdjustDetailInfo.setPayeeName(billPayRecord.getCrtName());
    billAdjustDetailInfo.setReceivedAmount(billPayRecord.getReceivedAmount());
    // 调整前后收费方式列表
    List<BillPayDetailRecordVO> beforeAdjustPayList = Lists.newArrayList();
    List<BillPayDetailRecordVO> afterAdjustPayList = Lists.newArrayList();
    if (0 == preExceptionHandleRecordId) {
      // 第一次调整，取当前异常记录对应的异常明细记录作为调整前的收费方式；
      setBillPaymentValue(billExceptionHandleRecordId, beforeAdjustPayList);
      // 当前记录对应的收费明细，且有效的为调整后收费方式
      List<BillPayDetailRecordVO> billPayDetailRecords =
          billPayDetailRecordMapper.selectBillPayDetailRecord(handledRecordId, true);
      if (StringHelper.isNotEmpty(billPayDetailRecords)) {
        billPayDetailRecords.forEach(
            record -> {
              Integer accountItemId = record.getAccountItemId();
              AccountItem accountItem = systemServiceFeign.findAccountItemById(accountItemId);
              if (null != accountItem) {
                record.setAccountItemName(accountItem.getName());
              }
              afterAdjustPayList.add(record);
            });
      }
    } else {
      // 取之前异常处理记录ID对应的异常处理明细列表，查询每条异常明细对应的支付记录作为调整前的收费方式；
      setBillPaymentValue(preExceptionHandleRecordId, beforeAdjustPayList);
      // 取当前异常处理记录ID对应的异常处理记录列表，查询每条异常明细对应的收费方式作为调增后的收费方式列表
      setBillPaymentValue(billExceptionHandleRecordId, afterAdjustPayList);
    }
    resultMap.put("beforeAdjust", beforeAdjustPayList);
    resultMap.put("afterAdjust", afterAdjustPayList);
    billAdjustDetailInfo.setBillPayDetailRecords(resultMap);
    return billAdjustDetailInfo;
  }

  /**
   * 根据异常处理记录ID查询账单收费撤销详情
   *
   * @param billExceptionHandleRecordId 账单异常处理记录ID
   * @return
   */
  public BillPayRecordVO findBillRevokePayRecord(Integer billExceptionHandleRecordId) {
    BillPayRecordVO billPayInfo = new BillPayRecordVO();
    BillExceptionHandleDetailRecord entity = new BillExceptionHandleDetailRecord();
    entity.setBillHandleRecordId(billExceptionHandleRecordId);
    BillExceptionHandleDetailRecord billExceptionHandleDetailRecord = mapper.selectOne(entity);
    if (null != billExceptionHandleDetailRecord) {
      Integer billPayRecordId = billExceptionHandleDetailRecord.getAssociateRecordId();
      BillPayRecord billPayRecord = billPayRecordMapper.selectByPrimaryKey(billPayRecordId);
      if (null != billPayRecord) {
        billPayInfo.setBillPayRecordId(billPayRecordId);
        billPayInfo.setChargeDate(new DateTime(billPayRecord.getCrtTime()).toString("yyyy-MM-dd"));
        Integer orgId = billPayRecord.getOrgId();
        billPayInfo.setOrgId(orgId);
        OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
        if (null != orgInfo) {
          billPayInfo.setOrgName(orgInfo.getAbbreviation());
        }
        Integer payeeId = billPayRecord.getCrtId();
        billPayInfo.setPayeeId(payeeId);
        SysEmployee employee = systemServiceFeign.findSysEmployeeById(payeeId);
        if (null != employee) {
          billPayInfo.setPayeeName(employee.getName());
        }
        billPayInfo.setReceivedAmount(billPayRecord.getReceivedAmount());
        billPayInfo.setStillOweAmount(billPayRecord.getStillOweAmount());
        List<BillPayDetailRecordVO> payDetailList =
            billPayDetailRecordMapper.selectBillPayDetailRecord(billPayRecordId, null);
        billPayInfo.setBillPayDetailRecords(payDetailList);
      }
    }
    return billPayInfo;
  }

  /**
   * 设置调整账单信息的账单收费方式
   *
   * @param billExceptionHandleRecordId 账单异常处理记录ID
   * @param payDetailList 收费方式列表
   */
  private void setBillPaymentValue(
      Integer billExceptionHandleRecordId, List<BillPayDetailRecordVO> payDetailList) {
    BillExceptionHandleDetailRecord entity = new BillExceptionHandleDetailRecord();
    entity.setBillHandleRecordId(billExceptionHandleRecordId);
    List<BillExceptionHandleDetailRecord> handleDetailRecords = mapper.select(entity);
    if (StringHelper.isNotEmpty(handleDetailRecords)) {
      handleDetailRecords.stream()
          .map(BillExceptionHandleDetailRecord::getAssociateRecordId)
          .map(recordId -> billPayDetailRecordMapper.selectByPrimaryKey(recordId))
          .forEachOrdered(
              detailRecord -> {
                BillPayDetailRecordVO vo = new BillPayDetailRecordVO();
                vo.setBillPayRecordId(detailRecord.getBillPayRecordId());
                vo.setBillPayDetailRecordId(detailRecord.getId());
                Integer accountItemId = detailRecord.getAccountItemId();
                vo.setAccountItemId(accountItemId);
                // todo 从缓存中查询支付方式
                AccountItem accountItem = systemServiceFeign.findAccountItemById(accountItemId);
                if (null != accountItem) {
                  vo.setAccountItemName(accountItem.getName());
                }
                vo.setAmount(detailRecord.getAmount());
                payDetailList.add(vo);
              });
    }
  }

  /**
   * 查询账单调整详情
   *
   * @param handledRecordId 被处理记录ID（就诊记录ID）
   * @param billExceptionHandleRecordId 异常处理记录ID
   * @param preExceptionHandleRecordId 上一条异常处理记录ID
   * @return
   */
  public Map<String, Object> findBillOrderDetailAdjustDetails(
      Integer handledRecordId,
      Integer billExceptionHandleRecordId,
      Integer preExceptionHandleRecordId) {
    Map<String, Object> resultMap = new HashMap<>(16);
    List<OrderDetailChargeVO> beforeAdjustBillDetail = Lists.newArrayList();
    List<OrderDetailChargeVO> afterAdjustBillDetail = Lists.newArrayList();
    if (0 == preExceptionHandleRecordId) {
      // 第一次调整，当前异常记录ID对应的订单ID
      beforeAdjustBillDetail = getOrderDetailChargeList(billExceptionHandleRecordId);
      // 当前记录ID对应调整后订单明细列表
      OrderRecord order = new OrderRecord();
      order.setTreatmentRecordId(handledRecordId);
      order.setInservice(true);
      OrderRecord orderRecord = orderRecordBiz.selectOne(order);
      if (null != orderRecord) {
        Integer id = orderRecord.getId();
        afterAdjustBillDetail = billRecordBiz.getOrderDetailChargeVOS(id);
      }
    } else {
      // 调整前订单明细列表
      beforeAdjustBillDetail = getOrderDetailChargeList(preExceptionHandleRecordId);
      // 调整后订单明细列表
      afterAdjustBillDetail = getOrderDetailChargeList(billExceptionHandleRecordId);
    }
    resultMap.put("beforeAdjust", beforeAdjustBillDetail);
    resultMap.put("afterAdjust", afterAdjustBillDetail);
    return resultMap;
  }

  /**
   * 获取订单详情列表
   *
   * @param billExceptionHandleRecordId 账单异常处理记录ID
   * @return
   */
  private List<OrderDetailChargeVO> getOrderDetailChargeList(Integer billExceptionHandleRecordId) {
    List<OrderDetailChargeVO> beforeAdjustBillDetail;
    BillExceptionHandleDetailRecord entity = new BillExceptionHandleDetailRecord();
    entity.setBillHandleRecordId(billExceptionHandleRecordId);
    BillExceptionHandleDetailRecord billExceptionHandleDetailRecords = mapper.selectOne(entity);
    Integer associateRecordId = billExceptionHandleDetailRecords.getAssociateRecordId();
    beforeAdjustBillDetail = billRecordBiz.getOrderDetailChargeVOS(associateRecordId);
    return beforeAdjustBillDetail;
  }
}
