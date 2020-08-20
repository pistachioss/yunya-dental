package com.yunya.modules.treatment.biz;

import com.yunya.feign.tariff.RemoteTariffServiceFeign;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.tariff.ClinicOralTariff;
import com.yunya.models.tariff.ClinicTariff;
import com.yunya.models.treatment.AssistantMatchingRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.models.treatment.TreatmentRecord;
import com.yunya.modules.treatment.mapper.OrderRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介: 患者就诊开单业务层
 *
 * @author: chow
 * @date: 2020/8/17 20:16
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderRecordBiz extends BaseBiz<OrderRecordMapper, OrderRecord> {

  /** 价目表服务 */
  @Autowired private RemoteTariffServiceFeign tariffServiceFeign;

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;

  /** 就诊记录 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;

  /** 开单详情 */
  @Autowired private OrderDetailBiz orderDetailBiz;

  /** 助手匹配 */
  @Autowired private AssistantMatchingRecordBiz matchingRecordBiz;

  /**
   * 暂存开单信息
   *
   * @param model 开单信息
   */
  public void storage(OrderRecordModel model) {
    Integer treatmentRecordId = model.getTreatmentRecordId();
    // 校验开单参数
    TreatmentRecord treatmentRecord = checkOrderParam(treatmentRecordId);

    redisUtils.set(RedisConstants.LOCK_ORDER_PROCESSING + treatmentRecordId, treatmentRecordId);

    BigDecimal totalAmount = BigDecimal.valueOf(0);
    List<OrderDetail> details = new ArrayList<>();
    List<OrderDetailModel> orderDetails = model.getOrderDetails();

    int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
    int userId = Integer.parseInt(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();

    // 计算开单总额并初始化订单明细列表
    totalAmount = calculateTotalAmount(orgId, totalAmount, details, orderDetails);

    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setTreatmentRecordId(treatmentRecordId);
    OrderRecord orderResult = mapper.selectOne(orderRecord);
    Integer orderRecordId;
    if (null == orderResult) {
      orderRecord.setOrgId(orgId);
      orderRecord.setPatientId(treatmentRecord.getPatientId());
      orderRecord.setTreatmentRecordId(treatmentRecordId);
      orderRecord.setTotalAmount(totalAmount);
      orderRecord.setCrtId(userId);
      orderRecord.setCrtName(name);
      mapper.insertSelective(orderRecord);

      orderRecordId = orderRecord.getId();
      details.forEach(
          detail -> {
            detail.setOrgId(orgId);
            detail.setTreatmentRecordId(treatmentRecordId);
            detail.setOrderRecordId(orderRecordId);
            detail.setCrtId(userId);
            detail.setCrtName(name);
            orderDetailBiz.insertSelective(detail);
          });
    } else {
      orderResult.setTotalAmount(totalAmount);
      orderResult.setUpdId(userId);
      orderResult.setUpdName(name);
      mapper.updateByPrimaryKeySelective(orderResult);
      orderRecordId = orderResult.getId();

      OrderDetail orderDetail = new OrderDetail();
      details.forEach(
          detail -> {
            orderDetail.setOrgId(orgId);
            orderDetail.setTreatmentRecordId(treatmentRecordId);
            orderDetail.setOrderRecordId(orderRecordId);
            orderDetail.setBillingItemId(detail.getBillingItemId());
            OrderDetail detailResult = orderDetailBiz.selectOne(orderDetail);
            if (null == detailResult) {
              detail.setOrgId(orgId);
              detail.setTreatmentRecordId(treatmentRecordId);
              detail.setOrderRecordId(orderRecordId);
              detail.setCrtId(userId);
              detail.setCrtName(name);
              orderDetailBiz.insertSelective(detail);
            } else {
              detailResult.setToothBit(detail.getToothBit());
              detailResult.setPrice(detail.getPrice());
              detailResult.setQuantity(detail.getQuantity());
              detailResult.setReceivableAmount(detail.getReceivableAmount());
              detailResult.setExecutorId(detail.getExecutorId());
              detailResult.setRemarks(detail.getRemarks());
              detailResult.setUpdId(userId);
              detailResult.setUptName(name);
              orderDetailBiz.updateSelectiveById(detailResult);
            }
          });
    }

    Integer assistantId1 = model.getAssistantId1();
    Integer assistantId2 = model.getAssistantId2();
    Integer assistantId3 = model.getAssistantId3();
    saveAssistantMatchingRecord(
        treatmentRecordId, orderRecordId, assistantId1, assistantId2, assistantId3);

    // 开单完成，更新就诊记录状态为已开单
    treatmentRecord.setStatus((byte) 1);
    treatmentRecord.setUpdId(userId);
    treatmentRecord.setUpdName(name);
    treatmentRecordBiz.updateSelectiveById(treatmentRecord);

    redisUtils.delete(RedisConstants.LOCK_ORDER_PROCESSING + treatmentRecordId);
  }

  /**
   * 保存助手配诊记录
   *
   * @param treatmentRecordId 就诊记录ID
   * @param orderRecordId 开单记录ID
   * @param assistantId1 助手1ID
   * @param assistantId2 助手2ID
   * @param assistantId3 助手3ID
   */
  private void saveAssistantMatchingRecord(
      Integer treatmentRecordId,
      Integer orderRecordId,
      Integer assistantId1,
      Integer assistantId2,
      Integer assistantId3) {
    int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
    int userId = Integer.parseInt(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    AssistantMatchingRecord matchingRecord = new AssistantMatchingRecord();
    matchingRecord.setTreatmentRecordId(treatmentRecordId);
    matchingRecord.setOrderRecordId(orderRecordId);
    if (null != assistantId1) {
      matchingRecord.setType((byte) 0);
      AssistantMatchingRecord matchingResult = matchingRecordBiz.selectOne(matchingRecord);
      if (null == matchingResult) {
        matchingRecord.setAssistantId(assistantId1);
        matchingRecord.setOrgId(orgId);
        matchingRecord.setCrtId(userId);
        matchingRecord.setCrtName(name);
        matchingRecordBiz.insertSelective(matchingRecord);
      } else {
        Integer assistantId = matchingResult.getAssistantId();
        if (!assistantId.equals(assistantId1)) {
          matchingResult.setAssistantId(assistantId1);
          matchingResult.setUpdId(userId);
          matchingResult.setUpdName(name);
          matchingRecordBiz.updateSelectiveById(matchingResult);
        }
      }
    } else {
      matchingRecord.setType((byte) 0);
      matchingRecordBiz.delete(matchingRecord);
    }

    if (null != assistantId2) {
      if (assistantId2.equals(assistantId1)) {
        throw new ClientServiceException(
            "开单失败，助手2与助手1不能是同一个人！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
      matchingRecord.setType((byte) 1);
      AssistantMatchingRecord matchingResult = matchingRecordBiz.selectOne(matchingRecord);
      if (null == matchingResult) {
        matchingRecord.setAssistantId(assistantId2);
        matchingRecord.setOrgId(orgId);
        matchingRecord.setCrtId(userId);
        matchingRecord.setCrtName(name);
        matchingRecordBiz.insertSelective(matchingRecord);
      } else {
        Integer assistantId = matchingResult.getAssistantId();
        if (!assistantId.equals(assistantId2)) {
          matchingResult.setAssistantId(assistantId1);
          matchingResult.setUpdId(userId);
          matchingResult.setUpdName(name);
          matchingRecordBiz.updateSelectiveById(matchingResult);
        }
      }
    } else {
      matchingRecord.setType((byte) 1);
      matchingRecordBiz.delete(matchingRecord);
    }

    if (null != assistantId3) {
      if (assistantId3.equals(assistantId1) || assistantId3.equals(assistantId2)) {
        throw new ClientServiceException(
            "开单失败，巡回与助手1或助手2不能是同一个人！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
      }
      matchingRecord.setType((byte) 2);
      AssistantMatchingRecord matchingResult = matchingRecordBiz.selectOne(matchingRecord);
      if (null == matchingResult) {
        matchingRecord.setAssistantId(assistantId3);
        matchingRecord.setOrgId(orgId);
        matchingRecord.setCrtId(userId);
        matchingRecord.setCrtName(name);
        matchingRecordBiz.insertSelective(matchingRecord);
      } else {
        Integer assistantId = matchingResult.getAssistantId();
        if (!assistantId.equals(assistantId3)) {
          matchingResult.setAssistantId(assistantId1);
          matchingResult.setUpdId(userId);
          matchingResult.setUpdName(name);
          matchingRecordBiz.updateSelectiveById(matchingResult);
        }
      }
    } else {
      matchingRecord.setType((byte) 2);
      matchingRecordBiz.delete(matchingRecord);
    }
  }

  /**
   * 计算开单总额
   *
   * @param orgId 组织ID
   * @param totalAmount 初始化总额
   * @param details 订单明细列表
   * @param orderDetails 开单明细列表
   * @return
   */
  private BigDecimal calculateTotalAmount(
      int orgId,
      BigDecimal totalAmount,
      List<OrderDetail> details,
      List<OrderDetailModel> orderDetails) {
    if (orderDetails.size() > 0) {
      ClinicTariff tariff = new ClinicTariff();
      ClinicOralTariff oralTariff = new ClinicOralTariff();
      BigDecimal price = BigDecimal.valueOf(0);
      for (OrderDetailModel detail : orderDetails) {
        OrderDetail orderDetailEntity = new OrderDetail();
        orderDetailEntity.setToothBit(detail.getToothBit());
        orderDetailEntity.setExecutorId(detail.getExecutorId());
        orderDetailEntity.setRemarks(detail.getRemarks());
        Byte type = detail.getType();
        orderDetailEntity.setType(type);
        Integer itemId = detail.getBillingItemId();
        orderDetailEntity.setBillingItemId(itemId);
        // todo 缓存优化
        switch (type) {
          case 0:
            tariff.setClinicId(orgId);
            tariff.setTariffId(itemId);
            ClinicTariff clinicTariff = tariffServiceFeign.findClinicTariff(tariff);
            if (null != clinicTariff) {
              price = clinicTariff.getPrice();
            }
            break;
          case 1:
            oralTariff.setClinicId(orgId);
            oralTariff.setOralTariffId(itemId);
            ClinicOralTariff clinicOralTariff = tariffServiceFeign.findClinicOralTariff(oralTariff);
            if (null != clinicOralTariff) {
              price = clinicOralTariff.getPrice();
            }
            break;
          default:
            break;
        }
        orderDetailEntity.setPrice(price);
        Integer quantity = detail.getQuantity();
        orderDetailEntity.setQuantity(quantity);
        BigDecimal detailTotal = price.multiply(BigDecimal.valueOf(quantity));
        orderDetailEntity.setReceivableAmount(detailTotal);
        totalAmount = totalAmount.add(detailTotal);
        details.add(orderDetailEntity);
      }
    }
    return totalAmount;
  }

  /**
   * 校验开单参数
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  private TreatmentRecord checkOrderParam(Integer treatmentRecordId) {
    TreatmentRecord treatmentRecord = treatmentRecordBiz.selectById(treatmentRecordId);
    if (null == treatmentRecord) {
      throw new ClientServiceException(
          "开单失败，当前未选择就诊记录或传入参数有误！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    Byte status = treatmentRecord.getStatus();
    if (status != 0 && status != 1) {
      throw new ClientServiceException(
          "开单失败，当前就诊处于开单完成或结算状态，无法继续开单！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    String recordId = redisUtils.get(RedisConstants.LOCK_ORDER_PROCESSING + treatmentRecordId);
    if (StringHelper.isNotBlank(recordId)) {
      throw new ClientServiceException(
          "开单失败，当前就诊记录处于正在开单状态，无法继续开单！", OperationCodeConstants.SAME_DATA_EXIST);
    }
    return treatmentRecord;
  }
}
