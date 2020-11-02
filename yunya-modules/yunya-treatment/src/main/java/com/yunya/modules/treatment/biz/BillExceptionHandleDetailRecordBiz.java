package com.yunya.modules.treatment.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.vo.BillPayDetailRecordVO;
import com.yunya.feign.treatment.domain.vo.BillPaymentAdjustDetailVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.AccountItem;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.mapper.BillExceptionHandleDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillExceptionHandleRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
  /** 开单详情 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 账单支付记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单支付明细记录 */
  @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;
  /** 账单异常处理数据详情记录 */
  @Autowired
  private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;
  /** 账单异常处理记录 */
  @Autowired
  private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;

  /**
   * 根据账单异常处理记录ID查询
   *
   * @param handledRecordId 被处理记录ID（收费记录）
   * @param billHandleRecordId 账单异常处理记录ID
   * @param preExceptionHandleRecordId 上一次异常处理记录ID
   */
  public Map<String, Object> findBillPaymentAdjustDetail(
      Integer handledRecordId, Integer billHandleRecordId, Integer preExceptionHandleRecordId) {
    Map<String, Object> resultMap = new HashMap<>(16);
    BillPayRecord billPayRecord = billPayRecordMapper.selectByPrimaryKey(handledRecordId);

    BillPaymentAdjustDetailVO billAdjustDetail = new BillPaymentAdjustDetailVO();
    billAdjustDetail.setBillPayRecordId(handledRecordId);
    billAdjustDetail.setChargeDate(new DateTime(billPayRecord.getCrtTime()).toString("yyyy-MM-dd"));
    Integer orgId = billPayRecord.getOrgId();
    billAdjustDetail.setOrgId(orgId);
    // todo 从缓存中查询组织信息
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    if (null != orgInfo) {
      billAdjustDetail.setOrgName(orgInfo.getAbbreviation());
    }
    billAdjustDetail.setPayeeId(billPayRecord.getCrtId());
    billAdjustDetail.setPayeeName(billPayRecord.getCrtName());
    billAdjustDetail.setReceivedAmount(billPayRecord.getReceivedAmount());
    List<BillPayDetailRecordVO> payDetailRecords = null;
    if (null == preExceptionHandleRecordId) {
      payDetailRecords = billPayDetailRecordMapper.selectBillPayDetailRecord(handledRecordId, true);
    } else {
      payDetailRecords = new ArrayList<>();
      BillExceptionHandleDetailRecord behdrEntity = new BillExceptionHandleDetailRecord();
      behdrEntity.setBillHandleRecordId(preExceptionHandleRecordId);
      // 根据bill_handle_record_id从账单异常处理数据详情记表中查询记录
      List<BillExceptionHandleDetailRecord> behdrLists = this.billExceptionHandleDetailRecordMapper.select(behdrEntity);
      for (BillExceptionHandleDetailRecord behdrItem : behdrLists) {
        // 根据id 从账单收费详情记录表中查询信息
        BillPayDetailRecordVO billPayDetailRecordVOS = this.billPayDetailRecordMapper.selectPreBillPayDetailRecord(behdrItem.getAssociateRecordId(), false);
        payDetailRecords.add(billPayDetailRecordVOS);
       }
    }

    payDetailRecords = BillPayDetailRecordBiz.getBillPayDetailRecordVOS(payDetailRecords, systemServiceFeign);
    // 设置修改收费记录
    billAdjustDetail.setBillPayDetailRecords(payDetailRecords);
    BillExceptionHandleDetailRecord entity = new BillExceptionHandleDetailRecord();
    entity.setBillHandleRecordId(billHandleRecordId);
    List<BillExceptionHandleDetailRecord> handleDetailRecords = mapper.select(entity);
    List<BillPayDetailRecordVO> afterAdjustBillPayDetails = Lists.newArrayList();
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
                afterAdjustBillPayDetails.add(vo);
              });
    }
    resultMap.put("afterAdjust", afterAdjustBillPayDetails);
    resultMap.put("beforeAdjust", billAdjustDetail);
    return resultMap;
  }

  /**
   * 查询账单开单记录调整详情
   *
   * @param handledRecordId 被处理记录ID（就诊记录ID）
   * @param billHandleRecordId 异常处理记录ID
   * @return
   */
  public Map<String, Object> findBillOrderDetailAdjustDetails(
      Integer handledRecordId, Integer billHandleRecordId) {
    Map<String, Object> resultMap = new HashMap<>(16);
    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setTreatmentRecordId(handledRecordId);
    orderRecord.setInservice(true);
    OrderRecord orderRecordResult = orderRecordBiz.selectOne(orderRecord);
    if (null != orderRecordResult) {
      Integer recordResultId = orderRecordResult.getId();
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrderRecordId(recordResultId);
      List<OrderDetail> orderDetails = orderDetailBiz.selectList(orderDetail);
    }
    BillExceptionHandleDetailRecord entity = new BillExceptionHandleDetailRecord();
    entity.setBillHandleRecordId(billHandleRecordId);
    List<BillExceptionHandleDetailRecord> exceptionHandleDetailRecords = mapper.select(entity);
    ArrayList<OrderDetailVO> vos = new ArrayList<>();
    if (StringHelper.isNotEmpty(exceptionHandleDetailRecords)) {
      for (BillExceptionHandleDetailRecord record : exceptionHandleDetailRecords) {
        Integer associateRecordId = record.getAssociateRecordId();

      }
    }
    return resultMap;
  }
}
