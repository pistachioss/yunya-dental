package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectTargetVO;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.MasertMemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.MemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.SecondaryMemberInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.SpecialistProjectCompletedCountQuery;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.form.BillPrintInfoForm;
import com.yunya.feign.treatment.domain.form.ModificationExecutorForm;
import com.yunya.feign.treatment.domain.model.GeneralDiscountModel;
import com.yunya.feign.treatment.domain.model.GoodsDetailModel;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.query.OrderPrivilegeQuery;
import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.clinic_base.SpecialistProject;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import com.yunya.modules.treatment.mapper.OrderDetailMapper;
import com.yunya.modules.treatment.mapper.OrderRecordMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBillDetail;
import static com.yunya.framework.common.constant.BusinessConstants.ORDER_FINISH_STATUS;
import static com.yunya.framework.common.constant.OperationCodeConstants.DELETE_NOT_ALLOW;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAM_NOT_ALLOW_EMPTY;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_ORDER_PROCESSING_CHARGE;
import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_ITEM_INFO;

/**
 * 简介: 开单明细业务层（开单明细列表查询、添加商品、删除开单明细）
 *
 * @author: chow
 * @date: 2020/8/18 11:16
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderDetailBiz extends BaseBiz<OrderDetailMapper, OrderDetail> {

  /** 缓存调用 */
  @Autowired private RedisUtils redisUtils;
  /** 系统管理服务 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 基础价目表 */
  @Autowired private BaseTariffBiz baseTariffBiz;
  /** 商品项目 */
  @Autowired private BaseOralTariffBiz baseOralTariffBiz;
  /** 门诊价目表 */
  @Autowired private ClinicTariffBiz clinicTariffBiz;
  /** 门诊商品表 */
  @Autowired private ClinicOralTariffBiz clinicOralTariffBiz;
  /** 开单记录 */
  @Autowired private OrderRecordMapper orderRecordMapper;
  /** 账单收费记录 */
  @Autowired private BillPayRecordMapper billPayRecordMapper;
  /** 优惠 */
  @Autowired private RemoteDiscountFeign discountFeign;
  /** 支付方式 */
  @Autowired private BillPayDetailRecordBiz billPayDetailRecordBiz;

  @Autowired private TollBiz tollBiz;
  /** 门诊基础服务 */
  @Autowired private RemoteClinicBaseServiceFeign remoteClinicBaseServiceFeign;
  /** 消息中间件 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

  @Resource private RemotePatientCentralServiceFeign patientFeign;
  /** 门诊价目表会员价 */
  @Autowired private ClinicTariffMemberPriceBiz clinicTariffMemberPriceBiz;
  /** 门诊商品项目会员价 */
  @Autowired private ClinicOralTariffMemberPriceBiz clinicOralTariffMemberPriceBiz;

  @Resource(name = "treatmentThreadPool")
  private ExecutorService executorService;
  /**
   * 根据账单（开单）记录ID查询商品开单详情列表
   *
   * @param orderRecordId 就诊记录ID
   * @return List<OrderDetailVO>
   */
  public List<OrderDetailVO> findGoodsDetailVOList(Integer orderRecordId) {
    List<OrderDetailVO> resultList = mapper.selectOrderDetailVOList(orderRecordId, (byte) 1);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            // todo 从缓存中获取开单项目信息
            Integer billingItemId = vo.getBillingItemId();
            Byte type = vo.getType();
            String itemKey = type + REDIS_KEY_ITEM_INFO + billingItemId;
            if (1 == type) {
              BaseOralTariff oralTariff = redisUtils.get(itemKey, BaseOralTariff.class);
              if (oralTariff == null) {
                oralTariff = baseOralTariffBiz.selectById(billingItemId);
                redisUtils.set(itemKey, oralTariff);
              }
              vo.setBillingItemName(oralTariff.getName());
              vo.setUnit(oralTariff.getUnit());
            }
            // 从缓存中获取用户（员工）信息
            Integer executorId = vo.getExecutorId();
            if (null != executorId) {
              SysEmployee employeeInfo = systemServiceFeign.findSysEmployeeById(executorId);
              vo.setExecutorName(null != employeeInfo ? employeeInfo.getName() : "--");
            }
          });
    } else {
      resultList = new ArrayList<>();
    }
    return resultList;
  }

  /**
   * 根据开单记录ID查询开单详情列表
   *
   * @param orderRecordId 开单记录ID
   * @return List<OrderDetailVO>
   */
  public List<OrderDetailVO> findOrderDetailVOList(Integer orderRecordId, Byte sourceType) {
    List<OrderDetailVO> resultList = mapper.selectOrderDetailVOList(orderRecordId, sourceType);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            //  从缓存中获取开单项目信息
            Integer billingItemId = vo.getBillingItemId();
            Byte type = vo.getType();
            String itemKey = type + REDIS_KEY_ITEM_INFO + billingItemId;
            switch (type) {
              case 0:
                BaseTariff tariff = redisUtils.get(itemKey, BaseTariff.class);
                if (null == tariff) {
                  tariff = baseTariffBiz.selectById(billingItemId);
                  redisUtils.set(itemKey, tariff);
                }
                vo.setBillingItemName(tariff.getName());
                vo.setUnit(tariff.getUnit());
                break;
              case 1:
                BaseOralTariff oralTariff = redisUtils.get(itemKey, BaseOralTariff.class);
                if (null == oralTariff) {
                  oralTariff = baseOralTariffBiz.selectById(billingItemId);
                  redisUtils.set(itemKey, oralTariff);
                }
                vo.setBillingItemName(oralTariff.getName());
                vo.setUnit(oralTariff.getUnit());
                break;
              default:
                break;
            }
            // 从缓存中获取用户（员工）信息
            Integer executorId = vo.getExecutorId();
            if (null != executorId) {
              SysEmployee employeeInfo =
                  redisUtils.get(
                      RedisConstants.REDIS_KEY_EMPLOYEE_INFO + executorId, SysEmployee.class);
              if (employeeInfo == null) {
                employeeInfo = systemServiceFeign.findSysEmployeeById(executorId);
              }
              vo.setExecutorName(null != employeeInfo ? employeeInfo.getName() : "--");
            }
          });
    } else {
      resultList = new ArrayList<>();
    }
    return resultList;
  }

  /**
   * 根据订单ID查询收费订单明细列表（含优惠信息）
   *
   * @param orderRecordId 开单记录ID
   * @return List<OrderDetailChargeVO>
   */
  public List<OrderDetailChargeVO> findChargeOrderDetailList(Integer orderRecordId) {
    String redisKey = LOCK_ORDER_PROCESSING_CHARGE + orderRecordId;
    String redisValue = redisUtils.get(redisKey);
    String userId = BaseContextHandler.getUserID();
    if (StringHelper.isNotBlank(redisValue) && !redisValue.equals(orderRecordId + ":" + userId)) {
      throw new ClientServiceException("收费失败，当前就诊正在收费中！", PARAMETERS_IS_ILLEGAL);
    }

    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
    if (BusinessConstants.ORDER_UN_LOCK_STATUS.equals(orderRecord.getStatus())) {
      throw new ClientServiceException("收费失败，当前账单已解锁，暂不能进行收费！", PARAMETERS_IS_ILLEGAL);
    }
    List<OrderDetailChargeVO> chargeOrderDetailList;
    List<OrderDetailChargeVO> chargeVOS = this.buildMember(orderRecordId);

    // 处理门诊价目表价格
    if (StringHelper.isNotEmpty(chargeVOS)) {
      List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
      if (StringHelper.isNotEmpty(memberTypes)) {
        chargeVOS.forEach(
            tariffVO -> {
              Map<Integer, Object> memberPrices = new HashMap<>(16);
              if (tariffVO.getType() == 0) {
                // 设置门诊价目表会员价,设置价格精度，为小数点后两位四舍五入
                setClinicTariffMemberPrice(
                    memberPrices,
                    memberTypes,
                    Integer.valueOf(BaseContextHandler.getOrgId()),
                    tariffVO);
              } else {
                setClinicOralTariffMemberPrice(
                    memberPrices,
                    memberTypes,
                    Integer.valueOf(BaseContextHandler.getOrgId()),
                    tariffVO);
              }
            });
      }
    }

    if (chargeVOS != null) {
      chargeOrderDetailList = chargeVOS;
    } else {
      chargeOrderDetailList = getChargeOrderDetailList(orderRecordId);
    }
    // 设置10分钟（该段时间内不允许其他用户重复收费，解锁）
    redisUtils.set(redisKey, orderRecordId + ":" + userId, 600);
    return chargeOrderDetailList;
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
      OrderDetailChargeVO tariffVO) {
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
      memberPrices.put(memberTypeId, memberPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
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
      OrderDetailChargeVO tariffVO) {
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
        memberPrice = memberPriceResult.getDiscountPrice().setScale(2, BigDecimal.ROUND_HALF_UP);
      } else {
        memberTypeId = memberType.getId();
        memberPrice =
            (tariffVO
                    .getPrice()
                    .multiply(BigDecimal.valueOf(memberType.getRate()))
                    .divide(BigDecimal.valueOf(100), 2))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
      }
      memberPrices.put(memberTypeId, memberPrice);
    }
    // 设置价格精度小数点后两位四舍五入，没有在上个方法中设置精度是为了保证会员价计算精确
    //    tariffVO.setPrice(tariffVO.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
    tariffVO.setMemberPrices(memberPrices);
  }

  private List<OrderDetailChargeVO> buildMember(Integer orderRecordId) {
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
    Map<Integer, String> maxType = getPatientMemberCards(orderRecord.getPatientId());
    if (maxType != null && maxType.size() > 0) {
      OrderPrivilegeQuery query = new OrderPrivilegeQuery();
      GeneralDiscountModel generalDiscountModel = new GeneralDiscountModel();
      generalDiscountModel.setMemberTypeId(Lists.newArrayList(maxType.keySet()).get(0));
      query.setOrderRecordId(orderRecordId);
      query.setDiscountType((byte) 1);
      query.setGeneralDiscountModel(generalDiscountModel);
      List<OrderDetailChargeVO> chargeVOS = tollBiz.matchOrderTailPrivilege(query);
      System.out.println("订单自动勾选优惠" + chargeVOS);
      if (CollectionUtils.isNotEmpty(chargeVOS)) {
        chargeVOS.stream()
            .filter(obj -> CollectionUtils.isNotEmpty(obj.getDiscountAppliesCoupons()))
            .forEach(
                obj ->
                    obj.getDiscountAppliesCoupons().stream()
                        .filter(benefit -> benefit.getCouponType() == 99)
                        .forEach(
                            benefit ->
                                benefit.setCardNumber(
                                    Lists.newArrayList(maxType.values()).get(0))));
        return chargeVOS;
      }
    }
    return null;
  }

  /**
   * 获取患者的会员卡信息
   *
   * @param patientId patientId
   * @return Byte
   */
  private Map<Integer, String> getPatientMemberCards(Integer patientId) {
    PatientMemberInfoQueryForm form = new PatientMemberInfoQueryForm();
    form.setPatientId(patientId);
    form.setBindType(0);
    // 查询患者的会员卡集合
    MemberInfoVo memberInfo = patientFeign.findMemberInfo(form);
    Map<Integer, String> map = Maps.newHashMap();
    if (memberInfo != null) {
      int minTypeSec = 0;
      MasertMemberInfoVo masertMemberInfoVo = memberInfo.getMasertMemberInfoVo();
      List<SecondaryMemberInfoVo> secondaryMemberInfoVos = memberInfo.getSecondaryMemberInfoVos();
      if (CollectionUtils.isNotEmpty(secondaryMemberInfoVos)) {
        Optional<SecondaryMemberInfoVo> min =
            secondaryMemberInfoVos.stream()
                .min(Comparator.comparing(SecondaryMemberInfoVo::getSecondaryMemberTypeId));
        if (min.isPresent()) {
          SecondaryMemberInfoVo secondaryMemberInfoVo = min.get();
          minTypeSec = secondaryMemberInfoVo.getSecondaryMemberTypeId();
          map.put(minTypeSec, secondaryMemberInfoVo.getSecondaryCardNumber());
        }
      }
      if (masertMemberInfoVo != null) {
        int secType = masertMemberInfoVo.getMasterCardTypeId();
        if (minTypeSec == 0) {
          map.put(secType, masertMemberInfoVo.getMasterCardNumber());
        } else if (minTypeSec > secType) {
          map.remove(minTypeSec);
          map.put(secType, masertMemberInfoVo.getMasterCardNumber());
        }
      }
      return map;
    }
    return null;
  }

  /**
   * 根据开单记录ID获取订单明细列表（含优惠信息）
   *
   * @param orderRecordId 开单记录ID
   */
  public List<OrderDetailChargeVO> getChargeOrderDetailList(Integer orderRecordId) {
    List<OrderDetailChargeVO> resultList = mapper.selectChargeOrderDetailList(orderRecordId);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            // 设置订单明细项目属性
            setOrderDetailItemValue(vo);
            // 从缓存中获取用户（员工）信息
            Integer executorId = vo.getExecutorId();
            if (null != executorId) {
              SysUserInfoDetail employeeInfo =
                  systemServiceFeign.findSysUserEmployeeInfoByUserId(executorId);
              vo.setExecutorName(null != employeeInfo ? employeeInfo.getName() : "--");
            }
            // 设置订单明细卡券匹配信息
            List<PrivilegeCouponInfoVO> couponInfos = Lists.newArrayList();
            vo.setDiscountAppliesCoupons(couponInfos);
          });
    }
    return resultList;
  }

  /**
   * 设置订单明细属性
   *
   * @param vo 订单明细
   */
  private void setOrderDetailItemValue(OrderDetailChargeVO vo) {
    // 从缓存中获取开单项目信息
    Integer billingItemId = vo.getBillingItemId();
    Byte type = vo.getType();
    String itemKey = type + REDIS_KEY_ITEM_INFO + billingItemId;
    switch (type) {
      case 0:
        BaseTariff tariff = redisUtils.get(itemKey, BaseTariff.class);
        if (tariff == null) {
          tariff = baseTariffBiz.selectById(billingItemId);
          redisUtils.set(itemKey, tariff);
        }
        vo.setBillingItemName(tariff.getName());
        vo.setBillingItemEnglishName(tariff.getEnglishName());
        vo.setUnit(tariff.getUnit());
        break;
      case 1:
        BaseOralTariff oralTariff = redisUtils.get(itemKey, BaseOralTariff.class);
        if (oralTariff == null) {
          oralTariff = baseOralTariffBiz.selectById(billingItemId);
          redisUtils.set(itemKey, oralTariff);
        }
        vo.setBillingItemName(oralTariff.getName());
        vo.setBillingItemEnglishName(oralTariff.getEnglishName());
        vo.setUnit(oralTariff.getUnit());
        break;
      default:
        break;
    }
  }

  /**
   * 添加/更新商品
   *
   * @param model 商品参数
   */
  public void addAndUpdGoodDetail(GoodsDetailModel model) {
    Integer orderRecordId = model.getOrderRecordId();
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException("添加商品失败，传入参数有误，为查询到与之匹配的开单记录！", PARAM_NOT_ALLOW_EMPTY);
    }
    if (ORDER_FINISH_STATUS.equals(orderRecord.getStatus())) {
      throw new ClientServiceException("添加商品失败，当前就诊已结账，无法继续添加商品！", PARAM_NOT_ALLOW_EMPTY);
    }
    Integer treatmentRecordId = orderRecord.getTreatmentRecordId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    // 将参数列表转换成实体列表
    List<OrderDetailModel> models = model.getOrderDetails();
    List<OrderDetail> orderDetails = transferModelToEntity(orgId, treatmentRecordId, models);

    // 删除收费新增开单商品
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrderRecordId(orderRecordId);
    orderDetail.setSourceType((byte) 1);
    mapper.delete(orderDetail);

    if (StringHelper.isNotEmpty(orderDetails)) {
      orderDetails.forEach(
          entity -> {
            entity.setOrderRecordId(orderRecordId);
            entity.setSourceType((byte) 1);
            mapper.insertSelective(entity);
          });
    }

    // 重新计算开单详情总额
    OrderDetail detail = new OrderDetail();
    detail.setOrderRecordId(orderRecordId);
    List<OrderDetail> details = mapper.select(detail);
    BigDecimal totalAmount = calculateTotalAmount(details);
    orderRecord.setTotalAmount(totalAmount);
    orderRecord.setUpdId(userId);
    orderRecord.setUpdName(name);
    orderRecordMapper.updateByPrimaryKeySelective(orderRecord);
  }

  /**
   * 计算开单明细总额
   *
   * @param orderDetails 开单明细列表
   * @return BigDecimal
   */
  public BigDecimal calculateTotalAmount(List<OrderDetail> orderDetails) {
    BigDecimal totalAmount = BigDecimal.valueOf(0);
    if (orderDetails.size() > 0) {
      for (OrderDetail detail : orderDetails) {
        BigDecimal price = detail.getPrice();
        Integer quantity = detail.getQuantity();
        BigDecimal detailTotal = price.multiply(BigDecimal.valueOf(quantity));
        totalAmount = totalAmount.add(detailTotal);
      }
    }
    return totalAmount;
  }

  /**
   * 将开单参数模型转换成开单详情实体
   *
   * @param orgId 组织ID
   * @param treatmentRecordId 就诊记录ID
   * @param models 参数列表
   * @return List<OrderDetail>
   */
  public List<OrderDetail> transferModelToEntity(
      Integer orgId, Integer treatmentRecordId, List<OrderDetailModel> models) {
    List<OrderDetail> orderDetails = new ArrayList<>();
    if (StringHelper.isNotEmpty(models)) {
      ClinicTariff tariff = new ClinicTariff();
      ClinicOralTariff oralTariff = new ClinicOralTariff();
      BigDecimal price = BigDecimal.valueOf(0);
      for (OrderDetailModel detail : models) {
        OrderDetail entity = new OrderDetail();
        entity.setOrgId(orgId);
        entity.setTreatmentRecordId(treatmentRecordId);
        entity.setToothBit(detail.getToothBit());
        entity.setExecutorId(detail.getExecutorId());
        entity.setRemarks(detail.getRemarks());
        Byte type = detail.getType();
        entity.setType(type);
        Integer itemId = detail.getBillingItemId();
        entity.setBillingItemId(itemId);
        String itemKey = type + REDIS_KEY_ITEM_INFO + itemId;
        // 从缓存查询开单项目
        switch (type) {
          case 0:
            tariff.setClinicId(orgId);
            tariff.setTariffId(itemId);
            ClinicTariff clinicTariff = clinicTariffBiz.selectOne(tariff);
            if (null != clinicTariff) {
              price = clinicTariff.getPrice();
            } else {
              BaseTariff baseTariff = redisUtils.get(itemKey, BaseTariff.class);
              if (baseTariff == null) {
                baseTariff = baseTariffBiz.selectById(itemId);
                redisUtils.set(itemKey, baseTariff);
              }
              price = baseTariff.getPrice();
            }
            break;
          case 1:
            oralTariff.setClinicId(orgId);
            oralTariff.setOralTariffId(itemId);
            ClinicOralTariff clinicOralTariff = clinicOralTariffBiz.selectOne(oralTariff);
            if (null != clinicOralTariff) {
              price = clinicOralTariff.getPrice();
            } else {
              BaseOralTariff baseOralTariff = redisUtils.get(itemKey, BaseOralTariff.class);
              if (baseOralTariff == null) {
                baseOralTariff = baseOralTariffBiz.selectById(itemId);
                redisUtils.set(itemKey, baseOralTariff);
              }
              price = baseOralTariff.getPrice();
            }
            break;
          default:
            break;
        }
        entity.setPrice(price);
        Integer quantity = detail.getQuantity();
        entity.setQuantity(quantity);
        BigDecimal detailTotal = price.multiply(BigDecimal.valueOf(quantity));
        entity.setReceivableAmount(detailTotal);
        entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        entity.setCrtName(BaseContextHandler.getName());
        orderDetails.add(entity);
      }
    }
    return orderDetails;
  }

  /**
   * 根据开单明细ID删除开单明细
   *
   * @param id 开单明细ID
   */
  public void deleteOrderDetailById(Integer id) {
    OrderDetail orderDetail = mapper.selectByPrimaryKey(id);
    if (null != orderDetail) {
      Integer orderRecordId = orderDetail.getOrderRecordId();
      OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
      Byte sourceType = orderDetail.getSourceType();
      Byte status = orderRecord.getStatus();
      switch (sourceType) {
        case 0:
          if (!BusinessConstants.ORDER_UN_LOCK_STATUS.equals(status)) {
            throw new ClientServiceException("删除开单明细失败，当前账单已锁定或已结账，无法删除！", DELETE_NOT_ALLOW);
          }
          break;
        case 1:
          if (ORDER_FINISH_STATUS.equals(status)) {
            throw new ClientServiceException("删除开单明细失败，当前账单已结账，无法删除！", DELETE_NOT_ALLOW);
          }
          break;
        default:
          break;
      }
      mapper.deleteByPrimaryKey(id);
      OrderDetail entity = new OrderDetail();
      entity.setOrderRecordId(orderRecordId);
      List<OrderDetail> orderDetails = mapper.select(entity);
      BigDecimal totalAmount = calculateTotalAmount(orderDetails);
      orderRecord.setTotalAmount(totalAmount);
      orderRecordMapper.updateByPrimaryKeySelective(orderRecord);
    }
  }

  /**
   * 修改执行人（患者档案）
   *
   * @param form 修改执行人表单
   */
  public void modificationExecutor(List<ModificationExecutorForm> form) {
    if (StringHelper.isEmpty(form)) {
      throw new ClientServiceException("没有修改任何项目的执行人,不可以提交", PARAM_NOT_ALLOW_EMPTY);
    }
    form.forEach(
        modificationExecutorForm -> {
          Integer executorId = modificationExecutorForm.getExecutorId();
          Integer id = modificationExecutorForm.getId();
          OrderDetail orderDetail = mapper.selectByPrimaryKey(id);
          if (null != orderDetail) {
            Integer orderRecordId = orderDetail.getOrderRecordId();
            BillPayRecord billPayRecord = new BillPayRecord();
            billPayRecord.setOrderRecordId(orderRecordId);
            List<BillPayRecord> select = billPayRecordMapper.select(billPayRecord);
            if (StringHelper.isEmpty(select)) {
              throw new ClientServiceException(
                  "账单未完成收费,不允许修改执行人", OperationCodeConstants.OBJECT_EDIT_FAIL);
            }
            orderDetail.setExecutorId(executorId);
            int i = mapper.updateByPrimaryKey(orderDetail);
            if (i > 0) {
              rabbitMqServiceFeign.sendMessage(orderDetail.getId(), 1, BaseBillDetail);
            }
          }
        });
  }

  /**
   * 打印账单信息
   *
   * @return 返回账单信息
   */
  public BillPrintInfoVO billPrintInfo(BillPrintInfoForm billPrintInfoForm) {
    Integer patientId = billPrintInfoForm.getPatientId();
    String billNumber = billPrintInfoForm.getBillNumber();
    BillPrintInfoVO billPrintInfoVO = mapper.billPrintInfo(patientId, billNumber);
    if (billPrintInfoVO != null) {
      Integer orderRecordId = billPrintInfoVO.getOrderRecordId();
      List<OrderBenefitDetailVo> orderBenefitD = discountFeign.getOrderBenefitD(orderRecordId);
      billPrintInfoVO
          .getBillDetail()
          .forEach(
              billDetailPrintInfoVO -> {
                List<OrderBenefitDetailVo> collect =
                    orderBenefitD.stream()
                        .filter(
                            orderBenefitDetailVo ->
                                orderBenefitDetailVo
                                    .getOrderDetailId()
                                    .equals(billDetailPrintInfoVO.getOrderDetailId()))
                        .collect(Collectors.toList());
                if (StringHelper.isNotEmpty(collect)) {
                  OrderBenefitDetailVo orderBenefitDetailVo = collect.get(0);
                  List<ItemUseBenefitVo> itemBenefitList =
                      orderBenefitDetailVo.getItemBenefitList();
                  List<Integer> couponTypes = new ArrayList<>();
                  itemBenefitList.forEach(
                      itemUseBenefitVo -> {
                        couponTypes.add(itemUseBenefitVo.getCouponType());
                      });
                  billDetailPrintInfoVO.setCouponTypes(couponTypes);
                }
              });
      // 支付方式以及金额
      BillPayDetailRecord billPayDetailRecord = new BillPayDetailRecord();
      billPayDetailRecord.setBillPayRecordId(billPrintInfoForm.getBillPayId());
      billPayDetailRecord.setInservice(true);
      List<BillPayDetailRecord> payList = billPayDetailRecordBiz.selectList(billPayDetailRecord);
      List<AccountItem> aiList = systemServiceFeign.findAccountItemList(new AccountItem());
      Map<String, AccountItem> clinicMap = new HashMap(16);
      aiList.forEach(z -> clinicMap.put(z.getId() + "", z));
      List<Map> mapList = new ArrayList<>();
      for (BillPayDetailRecord bpdr : payList) {
        Map map = new HashMap();
        if (bpdr.getType().equals(0)) {
          map.put("type", "预付款");
        } else if (bpdr.getType().equals(0)) {
          map.put("type", "会员卡");
        } else {
          map.put("type", clinicMap.get(bpdr.getAccountItemId() + "").getName());
        }
        map.put("amount", bpdr.getAmount());
        mapList.add(map);
      }
      billPrintInfoVO.setBillPayTypeList(mapList);
    }
    return billPrintInfoVO;
  }

  /**
   * 根据条件查询门诊开单专科项目完成信息
   *
   * @param query 查询条件
   * @return SpecialistProjectTariffCompletedInfoVO
   */
  public SpecialistProjectTariffCompletedInfoVO findClinicTariffOrderCompletedInfo(
      SpecialistProjectTariffCompletedInfoQuery query) {
    SpecialistProjectTariffCompletedInfoVO resultData =
        new SpecialistProjectTariffCompletedInfoVO();
    Integer specialistProjectTariffCompletedAmount =
        mapper.selectSpecialistProjectTariffCompletedAmount(query);
    resultData.setSpecialistProjectCompleted(specialistProjectTariffCompletedAmount);
    List<SpecialistTariffCompletedDetailVO> specialistProjectTariffDetails =
        mapper.selectSpecialistProjectTariffDetail(query);
    resultData.setSpecialistProjectCompletedDetails(specialistProjectTariffDetails);
    return resultData;
  }

  /**
   * 查询专科项目数量
   *
   * @param specialistProjectReportModel 查询条件
   * @return percentage
   */
  public List<SpecialistProjectReportVO> findTariffSpecialistPercentage(
      SpecialistProjectReportModel specialistProjectReportModel) throws Exception {
    AtomicInteger count = new AtomicInteger(0);
    List<SpecialistProjectReportVO> specialistProjectReportVOList = new ArrayList<>();
    List<SpecialistProject> specialistProjects =
        specialistProjectReportModel.getSpecialistProjects();
    List<SpecialistTariffProjectVO> specialistProjectVOs =
        mapper.selectTariffSpecialistPercentage(specialistProjectReportModel);
    CountDownLatch latch = new CountDownLatch(specialistProjects.size());

    List<Future<SpecialistProjectReportVO>> futureList = Lists.newArrayList();

    for (SpecialistProject specialistProject : specialistProjects) {
      SpecialistProjectReportModel clone =
          (SpecialistProjectReportModel) specialistProjectReportModel.clone();
      futureList.add(
          executorService.submit(
              () -> {
                try {
                  SpecialistProjectReportVO specialistProjectReportVO =
                      new SpecialistProjectReportVO();
                  specialistProjectReportVO.setSpecialistProjectName(specialistProject.getName());
                  String tariffIds = specialistProject.getTariffIds();
                  if (tariffIds != null) {
                    List<String> billingItemIds = Arrays.asList(tariffIds.split(","));
                    if (billingItemIds.size() > 0) {
                      int numberOfItems =
                          specialistProjectVOs.stream()
                              .filter(
                                  entity ->
                                      clone.getOrgIds().contains(entity.getOrgId())
                                          && billingItemIds.contains(
                                              String.valueOf(entity.getBillingItemId())))
                              .mapToInt(SpecialistTariffProjectVO::getQuantity)
                              .sum();
                      count.getAndAdd(numberOfItems);
                      specialistProjectReportVO.setPercentage(String.valueOf(numberOfItems));
                    }
                  }
                  return specialistProjectReportVO;
                } finally {
                  latch.countDown();
                }
              }));
    }

    latch.await();

    if (StringHelper.isNotEmpty(futureList)) {
      futureList.forEach(
          entity -> {
            try {
              SpecialistProjectReportVO specialistProjectReportVO = entity.get();
              specialistProjectReportVOList.add(specialistProjectReportVO);
            } catch (InterruptedException e) {
              e.printStackTrace();
            } catch (ExecutionException e) {
              e.printStackTrace();
            }
          });
    }

    BigDecimal numberOfItemsBD = null;
    BigDecimal countBD = new BigDecimal(count.get());
    BigDecimal percen100 = new BigDecimal(100);
    if (countBD.intValue() > 0) {
      for (SpecialistProjectReportVO specialistProjectReportVO : specialistProjectReportVOList) {
        numberOfItemsBD = new BigDecimal(specialistProjectReportVO.getPercentage());
        BigDecimal percentBD =
            numberOfItemsBD.multiply(percen100).divide(countBD, 2, RoundingMode.HALF_UP);
        String percentage = percentBD.toString();
        specialistProjectReportVO.setPercentage(percentage);
      }
    }
    return specialistProjectReportVOList;
  }

  /**
   * 根据条件查询专科项目完成数量
   *
   * @param query 查询条件
   * @return Integer
   */
  public Integer findSpecialistProjectCompletedCount(SpecialistProjectCompletedCountQuery query) {
    return mapper.selectSpecialistProjectCompletedCount(query);
  }

  /**
   * 更新账单详情
   *
   * @param orderDetail
   * @return
   */
  public Integer updateOrderDetail(OrderDetail orderDetail) {
    return mapper.updateByPrimaryKeySelective(orderDetail);
  }

  public PageInfo<SpecialistProjectCompletedInfoVO> specialistProjectTargetCompletedList(
      DataStatisticsQuery query) {
    // 专科项目 + 门诊为主数据
    List<SpecialistProjectCompletedInfoVO> resultList = new ArrayList<>();
    String startDate = query.getStartDate();
    String endDate = query.getEndDate();
    List<String> dateRange = DateUtil.sliceUpDateRange(startDate, endDate);
    List<SpecialistProjectTargetVO> specialistProjects =
        remoteClinicBaseServiceFeign.specialProjectAndGoalsList(
            query.getDateType(), dateRange, query.getOrgIds());
    if (StringHelper.isNotEmpty(specialistProjects)) {
      List<OrganizationInfoDetail> orgs = getOrganizationList(query.getOrgIds());
      Set<String> tids = new HashSet<>();
      specialistProjects.forEach(
          vo -> {
            String[] tariffIds = vo.getTariffIds().split(",");
            for (String tariffId : tariffIds) {
              tids.add(tariffId);
            }
          });
      orgs.forEach(
          vo -> {
            Integer orgId = vo.getId();
            List<OrderDetail> list =
                getSpecialistProjectCompletedList(orgId, startDate, endDate, tids);
            for (SpecialistProjectTargetVO specialistProject : specialistProjects) {
              SpecialistProjectCompletedInfoVO resultVO = new SpecialistProjectCompletedInfoVO();
              List<String> tariffIds = Arrays.asList(specialistProject.getTariffIds().split(","));
              Map<Integer, Integer> targets = specialistProject.getTargetMap();
              Integer completed = 0;
              if (StringHelper.isNotEmpty(list)) {
                for (OrderDetail detail : list) {
                  if (tariffIds.contains(detail.getBillingItemId() + "")) {
                    completed += detail.getQuantity();
                  }
                }
              }
              Integer goal = 0;
              if (targets != null) {
                goal = targets.get(orgId);
              }
              resultVO.setOrgId(orgId);
              resultVO.setSpecialistProjectGoalCount(goal);
              resultVO.setSpecialistProjectId(specialistProject.getId());
              resultVO.setSpecialistProjectName(specialistProject.getName());
              resultVO.setSpecialistProjectCompletedCount(completed);
              resultVO.setAbbreviation(vo.getAbbreviation());
              resultList.add(resultVO);
            }
          });
      // 分页
      if (query.getWhetherPage()) {
        return PageUtl.doPage(query.getPageNum(), query.getPageSize(), resultList);
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 手动分页
   *
   * @param pageNum
   * @param pageSize
   * @param resultList
   * @return
   */
  private PageInfo<SpecialistProjectCompletedInfoVO> doPage(
      Integer pageNum, Integer pageSize, List<SpecialistProjectCompletedInfoVO> resultList) {
    int total = resultList.size();
    PageInfo<SpecialistProjectCompletedInfoVO> pageInfo = new PageInfo<>();
    pageInfo.setPageNum(pageNum);
    pageInfo.setPageSize(pageSize);
    pageInfo.setTotal(total);
    List<SpecialistProjectCompletedInfoVO> list =
        resultList.subList(pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
    pageInfo.setList(list);
    return pageInfo;
  }

  private List<OrderDetail> getSpecialistProjectCompletedList(
      Integer orgId, String startDate, String endDate, Collection<String> tariffIds) {
    SpecialistProjectCompletedCountQuery specialistProjectCompletedQuery =
        new SpecialistProjectCompletedCountQuery();
    specialistProjectCompletedQuery.setOrgId(orgId);
    specialistProjectCompletedQuery.setTariffIds(tariffIds);
    specialistProjectCompletedQuery.setStartDate(startDate);
    specialistProjectCompletedQuery.setEndDate(endDate);
    return mapper.selectSpecialistProjectCompletedList(specialistProjectCompletedQuery);
  }

  /**
   * 查询组织信息列表
   *
   * @return
   */
  private List<OrganizationInfoDetail> getOrganizationList(Integer[] orgIds) {
    List<OrganizationInfoDetail> orgInfos =
        redisUtils.getJSONArray(RedisConstants.REDIS_KEY_ORG_LIST, OrganizationInfoDetail.class);
    if (StringHelper.isEmpty(orgInfos)) {
      orgInfos = systemServiceFeign.findOrgInfoInIds(Arrays.asList(orgIds));
      redisUtils.set(RedisConstants.REDIS_KEY_ORG_LIST, orgInfos);
    }
    return orgInfos.stream().filter(vo -> "2".equals(vo.getType())).collect(Collectors.toList());
  }

  public void specialistProjectTargetCompletedExport(
      DataStatisticsQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<SpecialistProjectCompletedInfoVO> resultList =
        specialistProjectTargetCompletedList(query).getList();
    ExcelUtil<SpecialistProjectCompletedInfoVO> excelUtil =
        new ExcelUtil<>(SpecialistProjectCompletedInfoVO.class);
    String fileName =
        excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "专科数量目标报表");
    excelUtil.exportExcel(response, resultList, "专科数量目标报表", fileName);
  }
}
