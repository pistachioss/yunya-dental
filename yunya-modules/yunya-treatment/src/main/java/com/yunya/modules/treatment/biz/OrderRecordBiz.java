package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.emr.RemoteEmrServiceFeign;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffInfoModel;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.form.OrderRecordForm;
import com.yunya.feign.treatment.domain.model.BillAdjustDetailModel;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.model.OrderRecordModel;
import com.yunya.feign.treatment.domain.query.OrderProcessQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.feign.treatment_other.RemoteTreatmentOtherFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.MemberType;
import com.yunya.models.tariff.ClinicOralTariffMemberPrice;
import com.yunya.models.tariff.ClinicTariffMemberPrice;
import com.yunya.models.treatment.*;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@Slf4j
public class OrderRecordBiz extends BaseBiz<OrderRecordMapper, OrderRecord> {

  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 就诊其他信息服务调用 */
  @Autowired private RemoteTreatmentOtherFeign treatmentOtherFeign;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 优惠服务调用 */
  @Autowired private RemoteDiscountFeign discountFeign;
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
  /** 患者服务 */
  @Autowired private RemotePatientCentralServiceFeign patientCentralServiceFeign;
  /** 门诊价目表会员价 */
  @Autowired private ClinicTariffMemberPriceBiz clinicTariffMemberPriceBiz;
  /** 门诊商品项目会员价 */
  @Autowired private ClinicOralTariffMemberPriceBiz clinicOralTariffMemberPriceBiz;
  /** 电子病历 */
  @Autowired private RemoteEmrServiceFeign remoteEmrServiceFeign;
  /** 患者服务 */
  @Autowired private RemotePatientCentralServiceFeign remotePatientCentralServiceFeign;

  private boolean lock;

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
    entity.setInservice(true);
    // 订单详情信息
    OrderRecord orderRecord = mapper.selectOne(entity);

    List<OrderDetailVO> orderDetails = new ArrayList<>();
    if (null != orderRecord) {
      Integer orderRecordId = orderRecord.getId();
      resultData.setOrderRecordId(orderRecordId);
      resultData.setTotalAmount(orderRecord.getTotalAmount());
      resultData.setStatus(orderRecord.getStatus());
      OrderBill4AppVO orderAndBill4App =
          billRecordMapper.findOrderAndBill4App(orderRecord.getTreatmentRecordId());
      if (null != orderAndBill4App) {
        resultData.setPrivilegeAmount(orderAndBill4App.getPrivilegeAmount());
        resultData.setReceivableAmount(orderAndBill4App.getReceivableAmount());
        resultData.setActualReceivableAmount(orderAndBill4App.getActualReceivableAmount());
        resultData.setReceivedAmount(orderAndBill4App.getReceivedAmount());
        resultData.setDebtAmount(orderAndBill4App.getDebtAmount());
      }
      orderDetails = orderDetailBiz.findOrderDetailVOList(orderRecordId, (byte) 0);
    }
    // 配诊助手列表
    List<AssistantInfoVO> assistants = matchingRecordBiz.findAssistantInfoVOList(treatmentRecordId);
    if (StringHelper.isEmpty(assistants)) {
      assistants = new ArrayList<>();
    }
    // 处理门诊价目表价格
    if (StringHelper.isNotEmpty(orderDetails)) {
      List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
      List<Integer> orderDetailIds =
          orderDetails.stream().map(OrderDetailVO::getOrderDetailId).collect(Collectors.toList());
      Map<Integer, List<Integer>> planDetails =
          remoteEmrServiceFeign.findOrderWithPlanDetailById(orderDetailIds);
      if (StringHelper.isNotEmpty(memberTypes)) {
        orderDetails.forEach(
            tariffVO -> {
              tariffVO.setPlanDetailIds(planDetails.get(tariffVO.getOrderDetailId()));
              Map<Integer, Object> memberPrices = new HashMap<>(16);
              if (tariffVO.getType() == 0) {
                // 设置门诊价目表会员价,设置价格精度，为小数点后两位四舍五入
                setClinicTariffMemberPrice(
                    memberPrices, memberTypes, orderRecord.getOrgId(), tariffVO);
              } else {
                setClinicOralTariffMemberPrice(
                    memberPrices, memberTypes, orderRecord.getOrgId(), tariffVO);
              }
            });
      }
    }
    resultData.setOrderDetails(orderDetails);
    resultData.setAssistants(assistants);
    return resultData;
  }

  /**
   * 设置门诊商品项目会员价
   *
   * @param memberPrices 会员价
   * @param memberTypes 会员类型列表
   * @param orgId 组织ID
   * @param tariffVO 门诊商品项目信息
   */
  private void setClinicOralTariffMemberPrice(
      Map<Integer, Object> memberPrices,
      List<MemberType> memberTypes,
      Integer orgId,
      OrderDetailVO tariffVO) {
    ClinicOralTariffMemberPrice clinicOralTariffMemberPrice = new ClinicOralTariffMemberPrice();
    clinicOralTariffMemberPrice.setClinicId(orgId);
    Integer oralTariffId = tariffVO.getBillingItemId();
    clinicOralTariffMemberPrice.setOralTariffId(oralTariffId);
    for (MemberType memberType : memberTypes) {
      Integer memberTypeId;
      BigDecimal memberPrice;
      clinicOralTariffMemberPrice.setMemberTypeId(memberType.getId());
      ClinicOralTariffMemberPrice memberPriceResult =
          clinicOralTariffMemberPriceBiz.selectOne(clinicOralTariffMemberPrice);
      if (null != memberPriceResult) {
        memberTypeId = memberPriceResult.getMemberTypeId();
        memberPrice = memberPriceResult.getDiscountPrice();
      } else {
        memberTypeId = memberType.getId();
        memberPrice =
            (tariffVO
                .getPrice()
                .multiply(BigDecimal.valueOf(memberType.getRate()))
                .divide(BigDecimal.valueOf(100), 2));
      }
      memberPrices.put(memberTypeId, memberPrice.setScale(2, RoundingMode.HALF_UP));
    }
    tariffVO.setMemberPrices(memberPrices);
  }

  /**
   * 设置门诊价目表会员价
   *
   * @param memberPrices 会员价
   * @param memberTypes 会员类型列表
   * @param orgId 组织ID
   * @param tariffVO 门诊价目表信息
   */
  private void setClinicTariffMemberPrice(
      Map<Integer, Object> memberPrices,
      List<MemberType> memberTypes,
      Integer orgId,
      OrderDetailVO tariffVO) {
    ClinicTariffMemberPrice clinicTariffMemberPrice = new ClinicTariffMemberPrice();
    clinicTariffMemberPrice.setClinicId(orgId);
    clinicTariffMemberPrice.setTariffId(tariffVO.getBillingItemId());
    for (MemberType memberType : memberTypes) {
      Integer memberTypeId;
      BigDecimal memberPrice;
      clinicTariffMemberPrice.setMemberTypeId(memberType.getId());
      ClinicTariffMemberPrice memberPriceResult =
          clinicTariffMemberPriceBiz.selectOne(clinicTariffMemberPrice);
      if (null != memberPriceResult) {
        memberTypeId = memberPriceResult.getMemberTypeId();
        memberPrice = memberPriceResult.getDiscountPrice().setScale(2, RoundingMode.HALF_UP);
      } else {
        memberTypeId = memberType.getId();
        memberPrice =
            (tariffVO
                    .getPrice()
                    .multiply(BigDecimal.valueOf(memberType.getRate()))
                    .divide(BigDecimal.valueOf(100), 2))
                .setScale(2, RoundingMode.HALF_UP);
      }
      memberPrices.put(memberTypeId, memberPrice);
    }
    // 设置价格精度小数点后两位四舍五入，没有在上个方法中设置精度是为了保证会员价计算精确
    //    tariffVO.setPrice(tariffVO.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
    tariffVO.setMemberPrices(memberPrices);
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
    try {
      boolean orderLock =
          redisUtils.setLock(orderKey, BaseContextHandler.getUserID(), 300, TimeUnit.SECONDS);
      if (!orderLock) {
        throw new ClientServiceException("当前就诊记录处于正在开单状态，请稍后再试！", SAME_DATA_EXIST);
      }
      List<OrderDetailModel> models = model.getOrderDetails();
      Integer treatmentRecordOrgId = treatmentRecord.getOrgId();
      int userId = Integer.parseInt(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      // 将model转换成entity
      List<OrderDetail> orderDetails =
          orderDetailBiz.transferModelToEntity(treatmentRecordOrgId, treatmentRecordId, models);
      // 计算开单总额
      BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);

      OrderRecord orderRecord = new OrderRecord();
      orderRecord.setTreatmentRecordId(treatmentRecordId);
      OrderRecord orderResult = mapper.selectOne(orderRecord);
      Integer orderRecordId;
      int result = 0;
      int operateType = 0;
      List<Integer> deletedDetailIds = null;
      if (null == orderResult) {
        orderRecord.setOrgId(treatmentRecordOrgId);
        String orderRecordNumber = generateOrderRecordNumber(treatmentRecordOrgId);
        orderRecord.setOrderRecordNum(orderRecordNumber);
        orderRecord.setPatientId(treatmentRecord.getPatientId());
        orderRecord.setTotalAmount(totalAmount);
        orderRecord.setCrtId(userId);
        orderRecord.setCrtName(name);
        result = mapper.insertSelective(orderRecord);
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
        result = mapper.updateByPrimaryKeySelective(orderResult);
        orderRecordId = orderResult.getId();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setTreatmentRecordId(treatmentRecordId);
        List<OrderDetail> deletedDetails = orderDetailBiz.selectList(orderDetail);
        if (StringHelper.isNotEmpty(deletedDetails)) {
          deletedDetailIds =
              deletedDetails.stream().map(OrderDetail::getId).collect(Collectors.toList());
        }
        orderDetailBiz.delete(orderDetail);
        if (StringHelper.isNotEmpty(orderDetails)) {
          orderDetails.forEach(
              detail -> {
                detail.setOrderRecordId(orderRecordId);
                orderDetailBiz.insertSelective(detail);
              });
          operateType = 1;
        }
      }
      int detailSize = saveTreatPlanDetailWriteoffQuanity(orderDetails, models, deletedDetailIds);
      if (result > 0 && detailSize > 0) {
        rabbitMqServiceFeign.sendMessage(orderRecordId, operateType, BaseBill);
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
    } finally {
      redisUtils.unlock(orderKey, BaseContextHandler.getUserID());
    }
  }

  /**
   * 生成治疗计划与订单项目核销数据
   *
   * @param orderDetails
   * @param models
   * @param deletedDetailIds
   * @return
   */
  private int saveTreatPlanDetailWriteoffQuanity(
      List<OrderDetail> orderDetails,
      List<OrderDetailModel> models,
      List<Integer> deletedDetailIds) {
    if (StringHelper.isNotEmpty(models) && StringHelper.isNotEmpty(orderDetails)) {
      TreatPlanDetailWriteoffModel model = new TreatPlanDetailWriteoffModel();
      List<TreatPlanDetailWriteoffInfoModel> list = new ArrayList<>();
      orderDetails.forEach(
          detail -> {
            Integer billingItemId = detail.getBillingItemId();
            Byte type = detail.getType();
            models.forEach(
                vo -> {
                  List<Integer> planDetailIds = vo.getPlanDetailIds();
                  if (!ObjectUtils.isEmpty(planDetailIds)
                      && vo.getBillingItemId().equals(billingItemId)
                      && vo.getType().equals(type)) {
                    TreatPlanDetailWriteoffInfoModel obj = new TreatPlanDetailWriteoffInfoModel();
                    obj.setTreatmentId(detail.getTreatmentRecordId());
                    obj.setQuantity(detail.getQuantity());
                    obj.setOrderDetailId(detail.getId());
                    obj.setPlanDetailIds(planDetailIds);
                    obj.setCrtId(detail.getCrtId());
                    list.add(obj);
                  }
                });
          });
      model.setWriteoffInfoModels(list);
      model.setDeletedOrderDetailIds(deletedDetailIds);
      remoteEmrServiceFeign.treatPlanWriteOffQunatity(model);
    }
    return orderDetails.size();
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
    return treatmentRecord;
  }

  /**
   * 生成账单编号
   *
   * @param orgId 组织ID
   * @return
   */
  private synchronized String generateOrderRecordNumber(Integer orgId) {
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
    matchingRecord.setOrderRecordId(orderRecordId);
    matchingRecordBiz.delete(matchingRecord);
    if (null != assistantId1) {
      matchingRecord.setType((byte) 0);
      addAssistantMatchingRecord(assistantId1, matchingRecord);
    }

    if (null != assistantId2) {
      if (assistantId2.equals(assistantId1)) {
        throw new ClientServiceException("开单失败，助手2与助手1不能是同一个人！", PARAMETERS_IS_ILLEGAL);
      }
      matchingRecord.setType((byte) 1);
      addAssistantMatchingRecord(assistantId2, matchingRecord);
    }

    if (null != assistantId3) {
      if (assistantId3.equals(assistantId1) || assistantId3.equals(assistantId2)) {
        throw new ClientServiceException("开单失败，巡回与助手1或助手2不能是同一个人！", PARAMETERS_IS_ILLEGAL);
      }
      matchingRecord.setType((byte) 2);
      addAssistantMatchingRecord(assistantId3, matchingRecord);
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

    redisUtils.set(orderKey, orderRecordId, 600);
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
    if (BusinessConstants.ORDER_LOCK_STATUS.equals(status)) {
      throw new ClientServiceException("修改开单失败，无法修改锁定的账单！", OBJECT_EDIT_FAIL);
    }

    if (ORDER_FINISH_STATUS.equals(status)) {
      throw new ClientServiceException("修改开单失败，无法修改已完成结算的账单！", OBJECT_EDIT_FAIL);
    }

    List<OrderDetailModel> models = form.getOrderDetails();
    if (StringHelper.isEmpty(models)) {
      throw new ClientServiceException("修改开单失败，请至少提交一条开单项目！", PARAM_NOT_ALLOW_EMPTY);
    }

    redisUtils.set(LOCK_ORDER_PROCESSING_UNLOCK, orderRecordId, 300);
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = orderRecord.getOrgId();
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    List<OrderDetail> details = orderDetailBiz.selectList(orderDetail);
    List<Integer> deletedDetailIds =
        details.stream().map(OrderDetail::getId).collect(Collectors.toList());
    orderDetailBiz.delete(orderDetail);

    List<OrderDetail> orderDetails =
        orderDetailBiz.transferModelToEntity(orgId, treatmentRecordId, models);
    TreatmentRecord treatmentRecord = treatmentRecordBiz.selectById(treatmentRecordId);
    List<VisitingRecord> visitingRecordList = new ArrayList<>();
    if (StringHelper.isNotEmpty(orderDetails)
        && !treatmentRecordBiz.patientHasDied(treatmentRecord.getPatientId())) {
      orderDetails.forEach(
          detail -> {
            detail.setOrderRecordId(orderRecordId);
            orderDetailBiz.insertSelective(detail);
            if (0 == detail.getType()) {
              List<VisitingRecord> orderDetailVisitRecord =
                  treatmentRecordBiz.createOrderDetailVisitRecord(treatmentRecordId, detail);
              log.info(
                  "orderDetailVisitRecord》》》》》》》{}", JSON.toJSONString(orderDetailVisitRecord));
              visitingRecordList.addAll(orderDetailVisitRecord);
            }
          });
    }
    int detailSize = saveTreatPlanDetailWriteoffQuanity(orderDetails, models, deletedDetailIds);
    log.info("OrderRecordBiz.java>>>>>>>>>>>>>>>>[583]>>>>>>>>>>detailSize={}", detailSize);
    log.info(
        "OrderRecordBiz.java>>>>>>>>>>>>>>>>[584]>>>>>>>>>>visitingRecordList={}",
        JSON.toJSONString(visitingRecordList));
    if (detailSize > 0) {
      treatmentOtherFeign.deleteVisitingRecordByTreatmentIdRest(treatmentRecordId);
      // 设置分组计划
      treatmentRecordBiz.saveOrderDetailVisitRecord(visitingRecordList);
    }

    // 修改助手配诊
    saveAssistantMatchingRecord(
        treatmentRecordId,
        orderRecordId,
        form.getAssistantId1(),
        form.getAssistantId2(),
        form.getAssistantId3());

    BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);
    orderRecord.setTotalAmount(totalAmount);
    orderRecord.setStatus((byte) 1);
    orderRecord.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    orderRecord.setUpdName(BaseContextHandler.getName());
    int result = mapper.updateByPrimaryKeySelective(orderRecord);

    // 发送消息同步账单
    if (result > 0) {
      rabbitMqServiceFeign.sendMessage(orderRecordId, 1, BaseBill);
      redisUtils.delete(LOCK_ORDER_PROCESSING_UNLOCK + orderRecordId);

      // 发送消息更新中间表就诊流程
      Integer appointmentId = treatmentRecord.getAppointmentId();
      if (null != appointmentId) {
        rabbitMqServiceFeign.sendMessage(appointmentId, 0, 1, BaseTreatmentProcess);
      } else {
        Integer registeredId = treatmentRecord.getRegisteredId();
        rabbitMqServiceFeign.sendMessage(registeredId, 1, 1, BaseTreatmentProcess);
      }
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
    // 未收费不能调整
    if (null == billRecord) {
      throw new ClientServiceException("调整账单失败，请选择正确的就诊记录进行账单调整！", PARAMETERS_IS_ILLEGAL);
    }
    // 收费记录全部撤销后才能调整
    BillPayRecord billPayRecord = new BillPayRecord();
    billPayRecord.setBillRecordId(billRecordId);
    billPayRecord.setInservice(true);
    int billPayRecordCount = billPayRecordMapper.selectCount(billPayRecord);
    if (billPayRecordCount > 0) {
      throw new ClientServiceException("调整账单失败，当前账单存在未撤销的支付记录！", PARAMETERS_IS_ILLEGAL);
    }
    // 比较开单明细是否有调整
    Integer orderRecordId = billRecord.getOrderRecordId();
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetail.setInservice(true);
    List<OrderDetail> orderDetailsData = orderDetailBiz.selectList(orderDetail);
    List<Integer> deletedDetailIds =
        orderDetailsData.stream().map(OrderDetail::getId).collect(Collectors.toList());
    // 获取调整后的开单明细，并比较是否有修改
    List<OrderDetailModel> detailModels = model.getOrderDetailModels();
    if (orderDetailsData.size() == detailModels.size()) {
      compareOrderDetails(orderDetailsData, detailModels);
    }
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    Integer patientId = billRecord.getPatientId();
    Integer treatmentRecordId = billRecord.getTreatmentRecordId();
    Integer billRecordOrgId = billRecord.getOrgId();
    // 前一条调整账单异常处理记录ID
    Integer preExceptionHandleRecordId =
        billExceptionHandleRecordMapper.selectPreExceptionHandleRecordId(
            treatmentRecordId, (byte) 2);
    // 保存异常处理记录
    BillExceptionHandleRecord exceptionHandleRecord = new BillExceptionHandleRecord();
    exceptionHandleRecord.setOrgId(orgId);
    exceptionHandleRecord.setPatientId(patientId);
    exceptionHandleRecord.setPreExceptionHandleRecordId(preExceptionHandleRecordId);
    exceptionHandleRecord.setTreatmentRecordId(treatmentRecordId);
    exceptionHandleRecord.setHandledRecordId(treatmentRecordId);
    exceptionHandleRecord.setOperateType((byte) 2);
    exceptionHandleRecord.setRemark(model.getRemark());
    exceptionHandleRecord.setCrtId(userId);
    exceptionHandleRecord.setCrtName(name);
    billExceptionHandleRecordMapper.insertSelective(exceptionHandleRecord);
    // 保存异常处理明细记录
    Integer handleRecordId = exceptionHandleRecord.getId();
    BillExceptionHandleDetailRecord handleDetailRecord = new BillExceptionHandleDetailRecord();
    handleDetailRecord.setBillHandleRecordId(handleRecordId);
    handleDetailRecord.setCrtId(userId);
    handleDetailRecord.setCrtName(name);
    handleDetailRecord.setAssociateRecordId(orderRecordId);
    billExceptionHandleDetailRecordMapper.insertSelective(handleDetailRecord);
    // 更新订单明细为无效
    orderDetailsData.forEach(
        detail -> {
          detail.setInservice(false);
          detail.setUpdId(userId);
          detail.setUptName(name);
          orderDetailBiz.updateSelectiveById(detail);
        });
    // 更新订单记录为无效
    OrderRecord orderRecord = new OrderRecord();
    orderRecord.setId(orderRecordId);
    orderRecord.setInservice(false);
    orderRecord.setUpdId(userId);
    orderRecord.setUpdName(name);
    mapper.updateByPrimaryKeySelective(orderRecord);
    // 更新账单为无效
    billRecord.setInservice(false);
    billRecord.setUpdId(userId);
    billRecord.setUpdName(name);
    int i = billRecordMapper.updateByPrimaryKeySelective(billRecord);
    // 将优惠置为不可用
    discountFeign.revokeBenefit(orderRecordId);
    // 发送消息同步中间表账单数据
    if (i > 0) {
      rabbitMqServiceFeign.sendMessage(orderRecordId, 2, BaseBill);
    }
    // 订单明细对象转换(调整账单不改变原来开单门诊ID)
    List<OrderDetail> orderDetails =
        orderDetailBiz.transferModelToEntity(billRecordOrgId, treatmentRecordId, detailModels);
    // 保存调整后订单记录
    BigDecimal totalAmount = orderDetailBiz.calculateTotalAmount(orderDetails);
    orderRecord.setId(null);
    orderRecord.setPatientId(patientId);
    orderRecord.setOrgId(billRecordOrgId);
    orderRecord.setTreatmentRecordId(treatmentRecordId);
    String orderRecordNumber = generateOrderRecordNumber(billRecordOrgId);
    orderRecord.setOrderRecordNum(orderRecordNumber);
    orderRecord.setTotalAmount(totalAmount);
    orderRecord.setStatus((byte) 1);
    orderRecord.setInservice(true);
    orderRecord.setCrtId(userId);
    orderRecord.setCrtName(name);
    int result = mapper.insertSelective(orderRecord);
    orderRecordId = orderRecord.getId();
    // 保存调整后订单明细
    for (OrderDetail detail : orderDetails) {
      detail.setOrderRecordId(orderRecordId);
      orderDetailBiz.insertSelective(detail);
    }
    saveTreatPlanDetailWriteoffQuanity(orderDetails, detailModels, deletedDetailIds);
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
                        // 开单项目ID和数量是否有改变
                        detail.getBillingItemId().equals(model.getBillingItemId())
                            && detail.getQuantity().equals(model.getQuantity()))
                .map(model -> detail)
                .forEachOrdered(details::add));
    if (detailModels.size() == details.size()) {
      throw new ClientServiceException("调整账单失败，当前账单开单明细的项目与数量未发生任何变动！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 订单处理（门诊端-订单处理）
   *
   * @param query 参数封装模型
   * @return 返回订单处理列表
   */
  public List<OrderProcessVO> orderProcess(OrderProcessQuery query) {
    String orderRecordNum = query.getOrderRecordNum();
    Integer[] orgIds = query.getOrgIds();
    String search = query.getSearch();
    List<OrderProcessVO> orderProcesses = mapper.selectOrderProcess(orderRecordNum, orgIds);
    if (StringHelper.isNotEmpty(orderProcesses)) {
      orderProcesses.forEach(
          vo -> {
            Integer patientId = vo.getPatientId();
            PatientBaseInfo patientInfo = patientCentralServiceFeign.findPatientInfoById(patientId);
            if (null != patientInfo) {
              vo.setPatientName(patientInfo.getName());
              vo.setPinyinName(patientInfo.getPinyinName());
              vo.setPatientMobile(patientInfo.getMobile());
            }
            Integer orgId = vo.getOrgId();
            OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
            if (null != orgInfo) {
              vo.setOrgName(orgInfo.getAbbreviation());
            }
          });
      if (StringHelper.isNotBlank(search)) {
        List<OrderProcessVO> resultList = Lists.newArrayList();
        Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        orderProcesses.forEach(
            vo -> {
              Matcher matcherName = pattern.matcher(vo.getPatientName());
              Matcher matcherPinyinName = pattern.matcher(vo.getPinyinName());
              Matcher matcherMobile = pattern.matcher(vo.getPatientMobile());
              if (matcherName.find() || matcherMobile.find() || matcherPinyinName.find()) {
                resultList.add(vo);
              }
            });
        return resultList;
      }
    }
    return orderProcesses;
  }

  /**
   * 查询当前订单可用预付款支付金额
   *
   * @param orderRecordId 订单记录ID
   * @return
   */
  public BigDecimal currentOrderEnablePrepayment(Integer orderRecordId) {
    return mapper.currentOrderEnablePrepayment(orderRecordId);
  }

  /**
   * 获取未结账订单记录列表
   *
   * @param treatmentRecordIds 就诊记录ID列表
   * @return list
   */
  public List<OrderRecord> getUnCheckedOrderRecords(List<Integer> treatmentRecordIds) {
    return mapper.selectAllUnCheckedOrderRecords(treatmentRecordIds);
  }

  /**
   * 更新开单状态
   *
   * @param orderRecord 开单记录
   * @return int
   */
  public int updateOrderStatus(OrderRecord orderRecord) {
    return mapper.updateByPrimaryKeySelective(orderRecord);
  }

  /**
   * 自动开单（开免单）
   *
   * @param treatmentRecordId 就诊记录ID
   * @param patientId 患者ID
   */
  public OrderRecord autoOpenOrder(Integer treatmentRecordId, Integer patientId) {
    OrderRecord order = new OrderRecord();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    order.setOrgId(orgId);
    order.setPatientId(patientId);
    order.setTreatmentRecordId(treatmentRecordId);
    String number = generateOrderRecordNumber(orgId);
    order.setOrderRecordNum(number);
    order.setStatus(BusinessConstants.ORDER_LOCK_STATUS);
    order.setTotalAmount(new BigDecimal("0"));
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    order.setCrtId(userId);
    String name = BaseContextHandler.getName();
    order.setCrtName(name);
    order.setUpdId(userId);
    order.setUpdName(name);
    mapper.insertSelective(order);
    OrderDetail detail = new OrderDetail();
    detail.setOrgId(orgId);
    detail.setTreatmentRecordId(treatmentRecordId);
    detail.setOrderRecordId(order.getId());
    detail.setType((byte) 0);
    detail.setBillingItemId(BusinessConstants.FREE_TARIFF_ITEM_ID);
    detail.setPrice(new BigDecimal("0"));
    detail.setQuantity(1);
    detail.setReceivableAmount(new BigDecimal("0"));
    detail.setToothBit("");
    detail.setSourceType((byte) 0);
    detail.setCrtId(userId);
    detail.setCrtName(name);
    detail.setUpdId(userId);
    detail.setUptName(name);
    int result = orderDetailBiz.insertSelective(detail);
    if (result > 0) {
      rabbitMqServiceFeign.sendMessage(order.getId(), 0, BaseBill);
    }
    return order;
  }
}
