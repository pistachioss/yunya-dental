package com.yunya.modules.treatment.biz;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.treatment.domain.form.OrderRecordForm;
import com.yunya.feign.treatment.domain.model.BillAdjustDetailModel;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.feign.treatment.domain.vo.AssistantInfoVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailInfoVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.treatment.*;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBill;
import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseTreatmentProcess;
import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.*;

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
  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 就诊其他信息服务调用 */
  @Autowired private RemoteTreatmentOtherFeign treatmentOtherFeign;
  /** 就诊记录 */
  @Autowired private TreatmentRecordBiz treatmentRecordBiz;
  /** 开单详情 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 助手匹配 */
  @Autowired private AssistantMatchingRecordBiz matchingRecordBiz;
  /** 账单记录 */
  @Autowired private BillRecordMapper billRecordMapper;
  /** 账单支付记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 账单异常处理记录 */
  @Autowired private BillExceptionHandleRecordMapper billExceptionHandleRecordMapper;
  /** 账单异常处理详情记录 */
  @Autowired private BillExceptionHandleDetailRecordMapper billExceptionHandleDetailRecordMapper;

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
    OrderRecord orderRecord = mapper.selectOne(entity);
    List<OrderDetailVO> orderDetails = new ArrayList<>();
    if (null != orderRecord) {
      Integer orderRecordId = orderRecord.getId();
      resultData.setOrderRecordId(orderRecordId);
      resultData.setTotalAmount(orderRecord.getTotalAmount());
      resultData.setStatus(orderRecord.getStatus());
      orderDetails = orderDetailBiz.findOrderDetailVOList(orderRecordId, (byte) 0);
    }
    // 配诊助手列表
    List<AssistantInfoVO> assistants = matchingRecordBiz.findAssistantInfoVOList(treatmentRecordId);
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
    String orderKey = LOCK_ORDER_PROCESSING_CREATE + treatmentRecordId;
    redisUtils.set(orderKey, treatmentRecordId, 5);
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
      String orderRecordNumber = generateOrderRecordNumber(orgId);
      orderRecord.setOrderRecordNum(orderRecordNumber);
      orderRecord.setPatientId(treatmentRecord.getPatientId());
      orderRecord.setTotalAmount(totalAmount);
      orderRecord.setCrtId(userId);
      orderRecord.setCrtName(name);
      int result = mapper.insertSelective(orderRecord);
      orderRecordId = orderRecord.getId();
      if (StringHelper.isNotEmpty(orderDetails)) {
        orderDetails.forEach(
            detail -> {
              detail.setOrderRecordId(orderRecordId);
              orderDetailBiz.insertSelective(detail);
            });
      }
      if (result > 0) {
        rabbitMqServiceFeign.sendMessage(orderRecordId, 0, BaseBill);
      }
    } else {
      orderResult.setTotalAmount(totalAmount);
      orderResult.setUpdId(userId);
      orderResult.setUpdName(name);
      int result = mapper.updateByPrimaryKeySelective(orderResult);
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
      if (result > 0) {
        rabbitMqServiceFeign.sendMessage(orderRecordId, 1, BaseBill);
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
    Integer appointmentId = treatmentRecord.getAppointmentId();
    // 发送消息更新中间表就诊流程
    if (null != appointmentId) {
      rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
    } else {
      Integer registeredId = treatmentRecord.getRegisteredId();
      rabbitMqServiceFeign.sendMessage(registeredId, 1, 1, BaseTreatmentProcess);
    }
    redisUtils.delete(orderKey);
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
      throw new ClientServiceException("开单失败，当前未选择就诊记录或传入参数有误！", PARAMETERS_IS_ILLEGAL);
    }

    Byte status = treatmentRecord.getStatus();
    if (!status.equals(TREATMENT_PROCESSING_STATUS)
        && !status.equals(TREATMENT_PROCESS_ORDER_STATUS)) {
      throw new ClientServiceException("开单失败，当前就诊处于开单完成或结算状态，无法重复开单！", PARAMETERS_IS_ILLEGAL);
    }

    String recordId = redisUtils.get(LOCK_ORDER_PROCESSING_CREATE + treatmentRecordId);
    if (StringHelper.isNotBlank(recordId)) {
      throw new ClientServiceException("开单失败，当前就诊记录处于正在开单状态，无法同时开单！", SAME_DATA_EXIST);
    }
    return treatmentRecord;
  }

  /**
   * 生成账单编号
   *
   * @param orgId 组织ID
   * @return
   */
  private String generateOrderRecordNumber(Integer orgId) {
    String number = mapper.selectOrderNumberByOrgId(orgId, new Date(System.currentTimeMillis()));
    String suffix = String.format("%04d", Integer.parseInt(number) + 1);
    return String.format(
        "DD%s%s%s", String.format("%04d", orgId), new DateTime().toString("yyMMdd"), suffix);
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
    AssistantMatchingRecord resultMatchingRecord =
        matchingRecordBiz.selectOneByTreatmentIdAndType(treatmentRecordId, (byte) 0);
    if (null != assistantId1) {
      if (null != resultMatchingRecord) {
        if (resultMatchingRecord.getOperatorPostType() == 0
            && !resultMatchingRecord.getAssistantId().equals(assistantId1)) {
          throw new ClientServiceException("开单失败，配诊助手1不可被修改！", PARAMETERS_IS_ILLEGAL);
        }
      }
      matchingRecord.setType((byte) 0);
      addAssistantMatchingRecord(assistantId1, orderRecordId, matchingRecord);
    } else {
      if (null != resultMatchingRecord) {
        if (resultMatchingRecord.getOperatorPostType() != 0) {
          matchingRecord.setType((byte) 0);
          matchingRecordBiz.delete(matchingRecord);
        }
      }
    }
    if (null != assistantId2) {
      if (assistantId2.equals(assistantId1)) {
        throw new ClientServiceException("开单失败，助手2与助手1不能是同一个人！", PARAMETERS_IS_ILLEGAL);
      }
      matchingRecord.setType((byte) 1);
      addAssistantMatchingRecord(assistantId2, orderRecordId, matchingRecord);
    } else {
      matchingRecord.setType((byte) 1);
      matchingRecordBiz.delete(matchingRecord);
    }
    if (null != assistantId3) {
      if (assistantId3.equals(assistantId1) || assistantId3.equals(assistantId2)) {
        throw new ClientServiceException("开单失败，巡回与助手1或助手2不能是同一个人！", PARAMETERS_IS_ILLEGAL);
      }
      matchingRecord.setType((byte) 2);
      addAssistantMatchingRecord(assistantId3, orderRecordId, matchingRecord);
    } else {
      matchingRecord.setType((byte) 2);
      matchingRecordBiz.delete(matchingRecord);
    }
  }

  /**
   * 添加助手匹配记录
   *
   * @param assistantId 助手ID
   * @param orderRecordId 订单记录ID
   * @param matchingRecord 匹配记录
   */
  private void addAssistantMatchingRecord(
      Integer assistantId, Integer orderRecordId, AssistantMatchingRecord matchingRecord) {
    AssistantMatchingRecord matchingResult = matchingRecordBiz.selectOne(matchingRecord);
    if (null == matchingResult) {
      matchingRecord.setOrderRecordId(orderRecordId);
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
    storage(model);
    Integer treatmentRecordId = model.getTreatmentRecordId();
    treatmentRecordBiz.completeTreatment(treatmentRecordId);
  }

  /**
   * 根据开单记录ID解锁账单
   *
   * @param orderRecordId 开单记录ID
   */
  public void unlockOrder(Integer orderRecordId) {
    OrderRecord orderRecord = mapper.selectByPrimaryKey(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException("解锁失败，请选择正确账单后进行解锁！", DATA_NOT_EXIST);
    }

    Byte status = orderRecord.getStatus();
    if (ORDER_FINISH_STATUS.equals(status)) {
      throw new ClientServiceException("解锁失败，无法解锁已经完成结算的账单！", PARAMETERS_IS_ILLEGAL);
    }

    String orderKey = LOCK_ORDER_PROCESSING_CHARGE + orderRecordId;
    String orderValue = redisUtils.get(orderKey);
    if (StringHelper.isNotBlank(orderValue) || ORDER_CHARGING_STATUS.equals(status)) {
      throw new ClientServiceException("解锁失败，当前账单处于收费中，与相关工作人员联系并关闭收费后可继续解锁账单！", SAME_DATA_EXIST);
    }

    redisUtils.set(orderKey, orderRecordId, 5);
    orderRecord.setStatus((byte) 0);
    orderRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    orderRecord.setUpdName(BaseContextHandler.getName());
    int result = mapper.updateByPrimaryKeySelective(orderRecord);
    redisUtils.delete(orderKey);
    // 发送消息更新中间表数据
    if (result > 0) {
      rabbitMqServiceFeign.sendMessage(orderRecordId, 1, BaseBill);
    }
  }

  /**
   * 修改开单明细并提交开单信息
   *
   * @param orderRecordId 开单记录ID
   * @param form 开单修改信息
   */
  public void modifyAndCommitOrder(Integer orderRecordId, OrderRecordForm form) {
    OrderRecord orderRecord = mapper.selectByPrimaryKey(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException(
          "修改开单失败，系统未查询到ID为'" + orderRecordId + "'的账单信息！", SAME_DATA_EXIST);
    }

    Byte status = orderRecord.getStatus();
    if (ORDER_FINISH_STATUS.equals(status)) {
      throw new ClientServiceException("修改开单失败，无法修改已完成结算的账单！", OBJECT_EDIT_FAIL);
    }

    List<OrderDetailModel> models = form.getOrderDetails();
    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException("修改开单失败，请至少提交一条开单项目！", PARAM_NOT_ALLOW_EMPTY);
    }

    redisUtils.set(LOCK_ORDER_PROCESSING_UNLOCK, orderRecordId, 5);
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetailBiz.delete(orderDetail);

    List<OrderDetail> orderDetails =
        orderDetailBiz.transferModelToEntity(orgId, treatmentRecordId, models);
    List<VisitingRecord> visitingRecordList = new ArrayList<>();
    if (StringHelper.isNotEmpty(orderDetails)) {
      treatmentOtherFeign.deleteVisitingRecordByTreatmentIdRest(treatmentRecordId);
      orderDetails.forEach(
          detail -> {
            detail.setOrderRecordId(orderRecordId);
            orderDetailBiz.insertSelective(detail);
            if (0 == detail.getType()) {
              List<VisitingRecord> orderDetailVisitRecord =
                  treatmentRecordBiz.createOrderDetailVisitRecord(treatmentRecordId, orderDetail);
              visitingRecordList.stream()
                  .sequential()
                  .collect(Collectors.toCollection(() -> orderDetailVisitRecord));
              //              treatmentRecordBiz.saveOrderDetailVisitRecord(treatmentRecordId,
              // detail);
            }
          });
      // 设置分组计划
      treatmentRecordBiz.saveOrderDetailVisitRecord(visitingRecordList);
    }

    BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);
    orderRecord.setTotalAmount(totalAmount);
    orderRecord.setStatus((byte) 1);
    orderRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    orderRecord.setUpdName(BaseContextHandler.getName());
    int result = mapper.updateByPrimaryKeySelective(orderRecord);
    redisUtils.delete(LOCK_ORDER_PROCESSING_UNLOCK + orderRecordId);
    // 发送消息同步账单
    if (result > 0) {
      rabbitMqServiceFeign.sendMessage(orderRecordId, 1, BaseBill);
    }
  }

  /**
   * 调整已收费账单信息
   *
   * @param model 参数信息
   */
  public void adjust(BillAdjustDetailModel model) {
    Integer billRecordId = model.getBillRecordId();
    BillRecord entity = new BillRecord();
    entity.setId(billRecordId);
    entity.setInservice(true);
    BillRecord billRecord = billRecordMapper.selectOne(entity);
    if (null == billRecord) {
      throw new ClientServiceException("调整账单失败，请选择正确的就诊记录进行账单调整！", PARAMETERS_IS_ILLEGAL);
    }

    BillPayRecord billPayRecord = new BillPayRecord();
    billPayRecord.setBillRecordId(billRecordId);
    billPayRecord.setInservice(true);
    int billPayRecordCount = billPayRecordMapper.selectCount(billPayRecord);
    if (billPayRecordCount > 0) {
      throw new ClientServiceException("调整账单失败，当前账单存在为撤销的支付记录！", PARAMETERS_IS_ILLEGAL);
    }

    Integer orderRecordId = billRecord.getOrderRecordId();
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetail.setInservice(true);
    List<OrderDetail> orderDetailsData = orderDetailBiz.selectList(orderDetail);
    List<OrderDetailModel> detailModels = model.getOrderDetailModels();
    if (orderDetailsData.size() == detailModels.size()) {
      compareOrderDetails(orderDetailsData, detailModels);
    }

    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Integer patientId = billRecord.getPatientId();
    Integer treatmentRecordId = billRecord.getTreatmentRecordId();

    BillExceptionHandleRecord exceptionHandleRecord = new BillExceptionHandleRecord();
    exceptionHandleRecord.setOrgId(orgId);
    exceptionHandleRecord.setPatientId(patientId);
    exceptionHandleRecord.setTreatmentRecordId(treatmentRecordId);
    exceptionHandleRecord.setHandledRecordId(treatmentRecordId);
    exceptionHandleRecord.setOperateType((byte) 2);
    exceptionHandleRecord.setRemark(model.getRemark());
    exceptionHandleRecord.setCrtId(userId);
    exceptionHandleRecord.setCrtName(name);
    billExceptionHandleRecordMapper.insertSelective(exceptionHandleRecord);

    // todo 将优惠置为不可用

    Integer handleRecordId = exceptionHandleRecord.getId();
    BillExceptionHandleDetailRecord handleDetailRecord = new BillExceptionHandleDetailRecord();
    handleDetailRecord.setBillHandleRecordId(handleRecordId);
    handleDetailRecord.setCrtId(userId);
    handleDetailRecord.setCrtName(name);
    orderDetailsData.forEach(
        detail -> {
          detail.setInservice(false);
          detail.setUpdId(userId);
          detail.setUptName(name);
          handleDetailRecord.setAssociateRecordId(detail.getId());
          billExceptionHandleDetailRecordMapper.insertSelective(handleDetailRecord);
        });

    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setId(orderRecordId);
    orderRecord.setInservice(false);
    orderRecord.setUpdId(userId);
    orderRecord.setUpdName(name);
    int i = mapper.updateByPrimaryKeySelective(orderRecord);

    billRecord.setInservice(false);
    billRecord.setUpdId(userId);
    billRecord.setUpdName(name);
    billRecordMapper.updateByPrimaryKeySelective(billRecord);
    // 发送消息同步中间表账单数据
    if (i > 0) {
      rabbitMqServiceFeign.sendMessage(orderRecordId, 2, BaseBill);
    }

    List<OrderDetail> orderDetails =
        orderDetailBiz.transferModelToEntity(orgId, treatmentRecordId, detailModels);
    BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);
    orderRecord.setId(null);
    orderRecord.setPatientId(patientId);
    orderRecord.setOrgId(orgId);
    orderRecord.setTreatmentRecordId(treatmentRecordId);
    String orderRecordNumber = generateOrderRecordNumber(orgId);
    orderRecord.setOrderRecordNum(orderRecordNumber);
    orderRecord.setTotalAmount(totalAmount);
    orderRecord.setInservice(true);
    orderRecord.setCrtId(userId);
    orderRecord.setCrtName(name);
    int result = mapper.insertSelective(orderRecord);
    orderRecordId = orderRecord.getId();
    for (OrderDetail detail : orderDetails) {
      detail.setOrderRecordId(orderRecordId);
      orderDetailBiz.insertSelective(detail);
    }
    // 发送消息同步中间表账单数据
    if (result > 0) {
      rabbitMqServiceFeign.sendMessage(orderRecordId, 0, BaseBill);
    }
  }

  /**
   * 比较调整开单明细与数据库开单明细
   *
   * @param orderDetailsData 数据库开单明细
   * @param detailModels 调整开单明细
   */
  private void compareOrderDetails(
      List<OrderDetail> orderDetailsData, List<OrderDetailModel> detailModels) {
    List<OrderDetail> details = new ArrayList<>();
    orderDetailsData.forEach(
        detail ->
            detailModels.stream()
                .filter(
                    model ->
                        detail.getBillingItemId().equals(model.getBillingItemId())
                            && detail.getQuantity().equals(model.getQuantity()))
                .map(model -> detail)
                .forEachOrdered(details::add));
    if (detailModels.size() == details.size()) {
      throw new ClientServiceException("调整账单失败，当前账单开单明细的项目与数量未发生任何变动！", PARAMETERS_IS_ILLEGAL);
    }
  }
}
