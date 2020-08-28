package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.feign.treatment.domain.vo.AssistantInfoVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
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
import java.util.Date;
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

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;

  /** 就诊记录 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;

  /** 开单详情 */
  @Autowired private OrderDetailBiz orderDetailBiz;

  /** 助手匹配 */
  @Autowired private AssistantMatchingRecordBiz matchingRecordBiz;

  /**
   * 根据就诊ID查询开单详情信息
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  public OrderDetailInfoVO findOrderDetailInfoVO(Integer treatmentRecordId) {
    OrderDetailInfoVO resultData = new OrderDetailInfoVO();
    OrderRecord entity = new OrderRecord();
    entity.setTreatmentRecordId(treatmentRecordId);
    // 订单详情信息
    List<OrderDetailVO> orderDetails;
    OrderRecord orderRecord = mapper.selectOne(entity);
    if (null != orderRecord) {
      Integer orderRecordId = orderRecord.getId();
      resultData.setOrderRecordId(orderRecordId);
      resultData.setTotalAmount(orderRecord.getTotalAmount());
      resultData.setStatus(orderRecord.getStatus());
      orderDetails = orderDetailBiz.findOrderDetailVOList(orderRecordId);
    } else {
      orderDetails = new ArrayList<>();
    }
    // 配诊助手列表
    List<AssistantInfoVO> assistants;
    assistants = matchingRecordBiz.findAssistantInfoVOList(treatmentRecordId);
    if (StringHelper.isEmpty(assistants)) {
      assistants = new ArrayList<>();
    }
    resultData.setOrderDetails(orderDetails);
    resultData.setAssistants(assistants);
    return resultData;
  }

  /**
   * 暂存开单信息
   *
   * @param model 开单信息
   */
  public void storage(OrderRecordModel model) {
    Integer treatmentRecordId = model.getTreatmentRecordId();
    // 校验开单参数
    TreatmentRecord treatmentRecord = checkOrderParam(treatmentRecordId);
    redisUtils.set(
        RedisConstants.LOCK_ORDER_PROCESSING_CREATE + treatmentRecordId, treatmentRecordId);
    List<OrderDetailModel> models = model.getOrderDetails();
    int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
    int userId = Integer.parseInt(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    // 将model转换成entity
    List<OrderDetail> orderDetails =
        orderDetailBiz.transferModelToEntity(orgId, treatmentRecordId, models);
    // 计算开单总额
    BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);

    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setTreatmentRecordId(treatmentRecordId);
    OrderRecord orderResult = mapper.selectOne(orderRecord);
    Integer orderRecordId;
    if (null == orderResult) {
      orderRecord.setOrgId(orgId);
      orderRecord.setPatientId(treatmentRecord.getPatientId());
      orderRecord.setTotalAmount(totalAmount);
      orderRecord.setCrtId(userId);
      orderRecord.setCrtName(name);
      mapper.insertSelective(orderRecord);
      orderRecordId = orderRecord.getId();
      if (StringHelper.isNotEmpty(orderDetails)) {
        orderDetails.forEach(
            detail -> {
              detail.setOrderRecordId(orderRecordId);
              orderDetailBiz.insertSelective(detail);
            });
      }
    } else {
      orderResult.setTotalAmount(totalAmount);
      orderResult.setUpdId(userId);
      orderResult.setUpdName(name);
      mapper.updateByPrimaryKeySelective(orderResult);
      orderRecordId = orderResult.getId();
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setTreatmentRecordId(treatmentRecordId);
      orderDetailBiz.delete(orderDetail);
      if (StringHelper.isNotEmpty(orderDetails)) {
        orderDetails.forEach(
            detail -> {
              detail.setOrderRecordId(orderRecordId);
              orderDetailBiz.insertSelective(detail);
            });
      }
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
    redisUtils.delete(RedisConstants.LOCK_ORDER_PROCESSING_CREATE + treatmentRecordId);
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
    if (!status.equals(BusinessConstants.TREATMENT_PROCESSING_STATUS)
        && !status.equals(BusinessConstants.TREATMENT_PROCESS_ORDER_STATUS)) {
      throw new ClientServiceException(
          "开单失败，当前就诊处于开单完成或结算状态，无法重复开单！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    String recordId =
        redisUtils.get(RedisConstants.LOCK_ORDER_PROCESSING_CREATE + treatmentRecordId);
    if (StringHelper.isNotBlank(recordId)) {
      throw new ClientServiceException(
          "开单失败，当前就诊记录处于正在开单状态，无法同时开单！", OperationCodeConstants.SAME_DATA_EXIST);
    }
    return treatmentRecord;
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
    AssistantMatchingRecord matchingRecord = new AssistantMatchingRecord();
    matchingRecord.setTreatmentRecordId(treatmentRecordId);
    matchingRecord.setOrderRecordId(orderRecordId);
    if (null != assistantId1) {
      matchingRecord.setType((byte) 0);
      addAssistantMatchingRecord(assistantId1, matchingRecord);
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
      addAssistantMatchingRecord(assistantId2, matchingRecord);
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
      addAssistantMatchingRecord(assistantId3, matchingRecord);
    } else {
      matchingRecord.setType((byte) 2);
      matchingRecordBiz.delete(matchingRecord);
    }
  }

  /**
   * 添加助手匹配记录
   *
   * @param assistantId 助手ID
   * @param matchingRecord 匹配记录
   */
  private void addAssistantMatchingRecord(
      Integer assistantId, AssistantMatchingRecord matchingRecord) {
    AssistantMatchingRecord matchingResult = matchingRecordBiz.selectOne(matchingRecord);
    if (null == matchingResult) {
      matchingRecord.setAssistantId(assistantId);
      matchingRecord.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
      matchingRecord.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      matchingRecord.setCrtName(BaseContextHandler.getName());
      matchingRecordBiz.insertSelective(matchingRecord);
    } else {
      Integer resultAssistantId = matchingResult.getAssistantId();
      if (!resultAssistantId.equals(assistantId)) {
        matchingResult.setAssistantId(assistantId);
        matchingResult.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        matchingResult.setUpdName(BaseContextHandler.getName());
        matchingRecordBiz.updateSelectiveById(matchingResult);
      }
    }
  }

  /**
   * 提交并完成接诊
   *
   * @param model 开单信息
   */
  public void submitAndCompleteOrder(OrderRecordModel model) {
    List<OrderDetailModel> orderDetails = model.getOrderDetails();
    if (StringHelper.isEmpty(orderDetails)) {
      throw new ClientServiceException(
          "开单失败，完成接诊要求至少开单一个项目！", OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
    }

    storage(model);
    Integer treatmentRecordId = model.getTreatmentRecordId();
    TreatmentRecord record = treatmentRecordBiz.selectById(treatmentRecordId);
    record.setTreatEndTime(new Date(System.currentTimeMillis()));
    record.setStatus((byte) 2);
    record.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    record.setUpdName(BaseContextHandler.getName());
    treatmentRecordBiz.updateSelectiveById(record);

    OrderRecord entity = new OrderRecord();
    entity.setTreatmentRecordId(treatmentRecordId);
    OrderRecord orderRecord = mapper.selectOne(entity);
    orderRecord.setStatus((byte) 1);
    orderRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    orderRecord.setUpdName(BaseContextHandler.getName());
    mapper.updateByPrimaryKeySelective(orderRecord);
  }

  /**
   * 根据开单记录ID解锁账单
   *
   * @param orderRecordId 开单记录ID
   */
  public void unlockOrder(Integer orderRecordId) {
    String recordId = redisUtils.get(RedisConstants.LOCK_ORDER_PROCESSING_CHARGE + orderRecordId);
    if (StringHelper.isNotBlank(recordId)) {
      throw new ClientServiceException(
          "解锁失败，当前账单处于收费中，与相关工作人员联系并关闭收费后可继续解锁账单！", OperationCodeConstants.SAME_DATA_EXIST);
    }

    OrderRecord orderRecord = mapper.selectByPrimaryKey(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException(
          "解锁失败，系统未查询到ID为'" + orderRecordId + "'的账单信息！", OperationCodeConstants.DATA_NOT_EXIST);
    }

    Byte status = orderRecord.getStatus();
    if (BusinessConstants.ORDER_FINISH_STATUS.equals(status)) {
      throw new ClientServiceException(
          "解锁失败，无法解锁已经完成结算的账单！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    orderRecord.setStatus((byte) 0);
    orderRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    orderRecord.setUpdName(BaseContextHandler.getName());
    mapper.updateByPrimaryKeySelective(orderRecord);
  }

  /**
   * 修改开单明细并提交开单信息
   *
   * @param orderRecordId 开单记录ID
   * @param models 开单信息
   */
  public void modifyAndCommitOrder(Integer orderRecordId, List<OrderDetailModel> models) {
    OrderRecord orderRecord = mapper.selectByPrimaryKey(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException(
          "修改开单失败，系统未查询到ID为'" + orderRecordId + "'的账单信息！", OperationCodeConstants.SAME_DATA_EXIST);
    }

    Byte status = orderRecord.getStatus();
    if (BusinessConstants.ORDER_FINISH_STATUS.equals(status)) {
      throw new ClientServiceException(
          "修改开单失败，无法修改已完成结算的账单！", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }

    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException(
          "修改开单失败，请至少提交一条开单项目！", OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY);
    }

    redisUtils.set(RedisConstants.LOCK_ORDER_PROCESSING_UNLOCK, orderRecordId);
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetailBiz.delete(orderDetail);

    List<OrderDetail> orderDetails =
        orderDetailBiz.transferModelToEntity(orgId, treatmentRecordId, models);
    if (StringHelper.isNotEmpty(orderDetails)) {
      orderDetails.forEach(
          detail -> {
            detail.setOrderRecordId(orderRecordId);
            orderDetailBiz.insertSelective(detail);
          });
    }

    BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);
    orderRecord.setTotalAmount(totalAmount);
    orderRecord.setStatus((byte) 1);
    orderRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    orderRecord.setUpdName(BaseContextHandler.getName());
    mapper.updateByPrimaryKeySelective(orderRecord);
    redisUtils.delete(RedisConstants.LOCK_ORDER_PROCESSING_UNLOCK + orderRecordId);
  }
}
