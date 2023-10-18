package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectTargetVO;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.query.DiscountCouponQuery;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.PatientItemBenefitVo;
import com.yunya.feign.emr.RemoteEmrServiceFeign;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.MasertMemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.MemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.feign.report.domain.query.CategoryIncomeQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.SpecialistProjectCompletedCountQuery;
import com.yunya.feign.report.domain.vo.BillItemAmountSharedVO;
import com.yunya.feign.report.domain.vo.CategoryInfoIncomeVO;
import com.yunya.feign.report.domain.vo.SpecialistProjectCompletedInfoVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.form.BillPrintInfoForm;
import com.yunya.feign.treatment.domain.form.ModificationExecutorForm;
import com.yunya.feign.treatment.domain.model.GeneralDiscountModel;
import com.yunya.feign.treatment.domain.model.GoodsDetailModel;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
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
import com.yunya.framework.common.utils.SortUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.clinic_base.SpecialistProject;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.system.AccountItem;
import com.yunya.models.system.MemberType;
import com.yunya.models.system.SysEmployee;
import com.yunya.models.tariff.*;
import com.yunya.models.treatment.*;
import com.yunya.modules.treatment.config.SysConfig;
import com.yunya.modules.treatment.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBillDetail;
import static com.yunya.framework.common.constant.BusinessConstants.ORDER_FINISH_STATUS;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_ORDER_PROCESSING_CHARGE;
import static com.yunya.framework.common.constant.RedisConstants.REDIS_KEY_ITEM_INFO;
import static java.util.stream.Collectors.toMap;

/**
 * 简介: 开单明细业务层（开单明细列表查询、添加商品、删除开单明细）
 *
 * @author: chow
 * @date: 2020/8/18 11:16
 * @description:
 * @since: 1.0.0
 */
@Slf4j
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

  /** 账单记录 */
  @Autowired private BillRecordMapper billRecordBiz;

  @Autowired private RemoteEmrServiceFeign remoteEmrServiceFeign;

  @Resource(name = "treatmentThreadPool")
  private ExecutorService executorService;

  @Autowired private SysConfig sysConfig;
  @Autowired private BillPayShareDetailMapper billPayShareDetailMapper;
  @Autowired private TreatTollBiz treatTollBiz;
  @Resource
  private RemoteReportServiceFeign remoteReportServiceFeign;
  
  /** 重试次数 */
  private static final int RETRY_TIMES = 5;

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
              SysEmployee executor = systemServiceFeign.findSysEmployeeById(executorId);
              vo.setExecutorName(null != executor ? executor.getName() : "--");
            }
            Integer consulterId = vo.getConsulterId();
            if (null != consulterId) {
              SysEmployee consulter = systemServiceFeign.findSysEmployeeById(consulterId);
              vo.setConsulterName(null != consulter ? consulter.getName() : "--");
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
    List<OrderDetailVO> resultList = findOrderDetailList(orderRecordId, sourceType);
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
                // 新数据不查询实时名称 老数据用当前价目表的名字 需求修改
                if (StringUtils.isEmpty(vo.getBillingItemName())) {
                  vo.setBillingItemName(tariff.getName());
                }
                vo.setUnit(tariff.getUnit());
                break;
              case 1:
                BaseOralTariff oralTariff = redisUtils.get(itemKey, BaseOralTariff.class);
                if (null == oralTariff) {
                  oralTariff = baseOralTariffBiz.selectById(billingItemId);
                  redisUtils.set(itemKey, oralTariff);
                }
                if (StringUtils.isEmpty(vo.getBillingItemName())) {
                  vo.setBillingItemName(oralTariff.getName());
                }
                vo.setUnit(oralTariff.getUnit());
                break;
              default:
                break;
            }
            // 从缓存中获取用户（员工）信息
            Integer executorId = vo.getExecutorId();
            if (null != executorId) {
              SysEmployee executor =
                  redisUtils.get(
                      RedisConstants.REDIS_KEY_EMPLOYEE_INFO + executorId, SysEmployee.class);
              if (executor == null) {
                executor = systemServiceFeign.findSysEmployeeById(executorId);
              }
              vo.setExecutorName(null != executor ? executor.getName() : "--");
            }
            Integer consulterId = vo.getConsulterId();
            if (null != consulterId) {
              SysEmployee consulter =
                  redisUtils.get(
                      RedisConstants.REDIS_KEY_EMPLOYEE_INFO + consulterId, SysEmployee.class);
              if (Objects.isNull(consulter)) {
                consulter = systemServiceFeign.findSysEmployeeById(consulterId);
              }
              vo.setConsulterName(null != consulter ? consulter.getName() : "--");
            }
          });
    } else {
      resultList = new ArrayList<>();
    }
    return resultList;
  }

  public List<OrderDetailVO> findOrderDetailList(Integer orderRecordId, Byte sourceType) {
    return mapper.selectOrderDetailVOList(orderRecordId, sourceType);
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
    List<OrderDetailChargeVO> chargeOrderDetailList = this.buildMember(orderRecordId, orderRecord.getPatientId());
    assemblyMemberDiscountPrice(chargeOrderDetailList);
    if (StringHelper.isNotEmpty(chargeOrderDetailList)) {
      List<Integer> orderDetailIds =
          chargeOrderDetailList.stream()
              .map(OrderDetailChargeVO::getOrderDetailId)
              .collect(Collectors.toList());
      Map<Integer, List<Integer>> planDetails =
          remoteEmrServiceFeign.findOrderWithPlanDetailById(orderDetailIds);
      chargeOrderDetailList.forEach(
          vo -> vo.setPlanDetailIds(planDetails.get(vo.getOrderDetailId())));
    }
    // 设置10分钟（该段时间内不允许其他用户重复收费，解锁）
    redisUtils.set(redisKey, orderRecordId + ":" + userId, 600);
    return chargeOrderDetailList;
  }

  /**
   * 装配会员折扣价
   *
   * @param chargeVOS
   */
  public void assemblyMemberDiscountPrice(List<? extends OrderDetailVO> chargeVOS) {
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
        memberPrice = memberPriceResult.getDiscountPrice();
      } else {
        memberTypeId = memberType.getId();
        memberPrice = tariffVO.getPrice().multiply(BigDecimal.valueOf(memberType.getRate()))
                    .divide(BigDecimal.valueOf(100), 2);
      }
      memberPrices.put(memberTypeId, memberPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    // 设置价格精度小数点后两位四舍五入，没有在上个方法中设置精度是为了保证会员价计算精确
    //    tariffVO.setPrice(tariffVO.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
    tariffVO.setMemberPrices(memberPrices);
  }

  private List<OrderDetailChargeVO> buildMember(Integer orderRecordId, Integer patientId) {
    BillRecord bill = billRecordBiz.selectOneByOrderRecordId(orderRecordId);
    if (StringHelper.isNull(bill) || StringHelper.gtZero(bill.getDebtAmount())) {
      Map<Integer, String> maxType = getPatientMemberCards(patientId);
      if (StringHelper.isNotEmpty(maxType)) {
        OrderPrivilegeQuery query = new OrderPrivilegeQuery();
        GeneralDiscountModel generalDiscountModel = new GeneralDiscountModel();
        generalDiscountModel.setMemberTypeId(Lists.newArrayList(maxType.keySet()).get(0));
        query.setOrderRecordId(orderRecordId);
        query.setDiscountType((byte) 1);
        query.setGeneralDiscountModel(generalDiscountModel);
        TreatOrderRecordVO orderBenefitVO = treatTollBiz.matchOrderTailPrivilege(query);
        if (StringHelper.isNotNull(orderBenefitVO)) {
          treatTollBiz.cacheOrderBenefitInfo(orderRecordId, orderBenefitVO);
          List<OrderDetailChargeVO> chargeVOS = orderBenefitVO.getItemList();
//      List<OrderDetailChargeVO> chargeVOS = tollBiz.matchOrderTailPrivilege(query);
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
      }
    }
    return getChargeOrderDetailList(orderRecordId);
  }

  /**
   * 获取患者的会员身份信息（规则）
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
    if (StringHelper.isNotNull(memberInfo)) {
      int minTypeSec = 0;
      MasertMemberInfoVo masertMemberInfoVo = memberInfo.getMasertMemberInfoVo();
      if (StringHelper.isNotNull(masertMemberInfoVo)) {
//        List<SecondaryMemberInfoVo> secondaryMemberInfoVos = memberInfo.getSecondaryMemberInfoVos();
//        if (CollectionUtils.isNotEmpty(secondaryMemberInfoVos)) {
//          Optional<SecondaryMemberInfoVo> min =
//              secondaryMemberInfoVos.stream()
//                  .min(Comparator.comparing(SecondaryMemberInfoVo::getSecondaryMemberTypeId));
//          if (min.isPresent()) {
//            SecondaryMemberInfoVo secondaryMemberInfoVo = min.get();
//            minTypeSec = secondaryMemberInfoVo.getSecondaryMemberTypeId();
//            map.put(minTypeSec, secondaryMemberInfoVo.getSecondaryCardNumber());
//          }
//        }
        // 非普通会员
        int secType = masertMemberInfoVo.getMemberTypeId();
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
              SysUserInfoDetail executor =
                  systemServiceFeign.findSysUserEmployeeInfoByUserId(executorId);
              vo.setExecutorName(null != executor ? executor.getName() : "--");
            }
            Integer consulterId = vo.getConsulterId();
            if (null != consulterId) {
              SysUserInfoDetail consulter =
                  systemServiceFeign.findSysUserEmployeeInfoByUserId(consulterId);
              vo.setConsulterName(null != consulter ? consulter.getName() : "--");
            }
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
        // 新数据不查询实时名称 老数据用当前价目表的名字 需求修改
        if (StringUtils.isEmpty(vo.getBillingItemName())) {
          vo.setBillingItemName(tariff.getName());
        }
        vo.setBillingItemEnglishName(tariff.getEnglishName());
        vo.setUnit(tariff.getUnit());
        vo.setItemNum(tariff.getItemNumber());
        break;
      case 1:
        BaseOralTariff oralTariff = redisUtils.get(itemKey, BaseOralTariff.class);
        if (oralTariff == null) {
          oralTariff = baseOralTariffBiz.selectById(billingItemId);
          redisUtils.set(itemKey, oralTariff);
        }
        if (StringUtils.isEmpty(vo.getBillingItemName())) {
          vo.setBillingItemName(oralTariff.getName());
        }
        vo.setBillingItemEnglishName(oralTariff.getEnglishName());
        vo.setUnit(oralTariff.getUnit());
        vo.setItemNum(oralTariff.getItemNumber());
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
      for (OrderDetailModel model : models) {
        if (!sysConfig.getExecutorAndconsulterCanSame()){
          model.compareParams();
        }
        OrderDetail entity = new OrderDetail();
        entity.setOrgId(orgId);
        entity.setTreatmentRecordId(treatmentRecordId);
        entity.setToothBit(model.getToothBit());
        entity.setExecutorId(model.getExecutorId());
        entity.setConsulterId(model.getConsulterId());
        entity.setRemarks(model.getRemarks());
        Byte type = model.getType();
        entity.setType(type);
        Integer itemId = model.getBillingItemId();
        entity.setBillingItemId(itemId);
        // 记录当时的开单项目名称 需求修改
        entity.setBillingItemName(model.getBillingItemName());
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
        Integer quantity = model.getQuantity();
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
      PatientTotalInfoVo patientTotalInfo = patientFeign.findPatientTotalInfo(patientId);
      if (Objects.nonNull(patientTotalInfo)) {
        billPrintInfoVO.setPatientId(patientId);
        billPrintInfoVO.setPatientName(patientTotalInfo.getName());
        billPrintInfoVO.setBirthday(patientTotalInfo.getBirthday());
        billPrintInfoVO.setAge(patientTotalInfo.getAge());
        billPrintInfoVO.setMedicalNumber(patientTotalInfo.getMedicalNumber());
        Integer memberTypeId = patientTotalInfo.getMemberTypeId();
        if (Objects.nonNull(memberTypeId)) {
          billPrintInfoVO.setMemberTypeId(memberTypeId);
          MemberType memberType = systemServiceFeign.findMemberTypeById(memberTypeId);
          if (Objects.nonNull(memberType)) {
            billPrintInfoVO.setMemberTypeName(memberType.getName());
          }
        }
      }
      Map<Integer, List<PatientItemBenefitVo>> itemBenefitMap = findItemBenefitMapWithRetry(billPrintInfoVO, RETRY_TIMES);
      billPrintInfoVO
          .getBillDetail()
          .forEach(
              billDetailPrintInfoVO -> {
                List<PatientItemBenefitVo> itemBenefit = itemBenefitMap.get(billDetailPrintInfoVO.getOrderDetailId());
                if (StringHelper.isNotEmpty(itemBenefit)) {
                  PatientItemBenefitVo orderBenefitDetailVo = itemBenefit.get(0);
                  List<ItemUseBenefitVo> itemBenefitList =
                      orderBenefitDetailVo.getItemBenefitList();
                  List<Integer> couponTypes = new ArrayList<>();
                  itemBenefitList.forEach(
                      itemUseBenefitVo -> couponTypes.add(itemUseBenefitVo.getCouponType()));
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
   * 查询非划扣项目的优惠明细（重试）
   *
   * @param billPrintInfoVO
   * @return
   */
  private Map<Integer, List<PatientItemBenefitVo>> findItemBenefitMapWithRetry(BillPrintInfoVO billPrintInfoVO, int times) {
    if (StringHelper.gtZero(billPrintInfoVO.getTotalPrivilegeAmount())) {
      Integer orderRecordId = billPrintInfoVO.getOrderRecordId();
      List<PatientItemBenefitVo> orderBenefitD = discountFeign.getOrderBenefitD(orderRecordId).getItemList();
      if (StringHelper.isEmpty(orderBenefitD) && times>0) {
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          log.error("findItemBenefitMapWithRetry error:", e);
          throw new ClientServiceException("查询账单优惠信息错误", DATA_ERROR);
        }
        return findItemBenefitMapWithRetry(billPrintInfoVO, --times);
      }
    }
    return Maps.newHashMap();
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
    if (countBD.intValue() > 0 && CollectionUtils.isNotEmpty(specialistProjectReportVOList)) {
      for (SpecialistProjectReportVO specialistProjectReportVO : specialistProjectReportVOList) {
        numberOfItemsBD =
            new BigDecimal(
                ObjectUtils.isEmpty(specialistProjectReportVO.getPercentage())
                    ? "0"
                    : specialistProjectReportVO.getPercentage());
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
      List<OrganizationInfoDetail> orgs =
          systemServiceFeign.findOrgInfoInIds(Arrays.asList(query.getOrgIds()));
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

  /**
   * 根据条件查询项目分类收入汇总列表
   *
   * @param query
   * @return
   */
  public PageInfo<CategoryInfoIncomeVO> findCategoryIncomeList(CategoryIncomeQuery query)
      throws Exception {
    //    RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(),
    // true);
    //    List<List<Integer>> part = Lists.partition(query.getOrgIds(), query.getOrgIds().size());
    //    List<CategoryInfoIncomeVO> list = new ArrayList<>();
    //    CountDownLatch cdl = new CountDownLatch(part.size());
    //    part.forEach(orgIds-> executorService.submit(()->{
    //      try {
    //        list.addAll(findCategoryIncomeList(orgIds, query.getStartDate(), query.getEndDate()));
    //      } catch (Exception e) {
    //        log.error("", e);
    //      } finally {
    //        cdl.countDown();
    //      }
    //    }));
    //    cdl.await();
    List<CategoryInfoIncomeVO> list =
        findCategoryIncomeList(query.getOrgIds(), query.getStartDate(), query.getEndDate());
    // 分页
    return PageUtl.doPage(query.getPageNum(), query.getPageSize(), list, query.getWhetherPage());
  }

  public List<CategoryInfoIncomeVO> findCategoryIncomeList(
      List<Integer> orgIds, String startDate, String endDate)
      throws ExecutionException, InterruptedException {
    CategoryIncomeQuery query = new CategoryIncomeQuery();
    query.setOrgIds(orgIds);
    query.setStartDate(startDate);
    query.setEndDate(endDate);
    // 项目表（价目表 + 价目表）
    Future<List<BaseTariffVO>> tariffFuture = multiFindAllTariffList();

    // 项目的原价合计
    Future<List<ClinicTariffOrderVO>> originalFuture = multiFindTariffCategoryOriginalAmount(query);

    // 项目的当月免单
    Future<Map<String, BigDecimal>> freePaymentFuture =
        multiFindTariffCategoryFreePaymentAmount(query);

    // 门诊组织列表
    List<OrganizationInfoDetail> orgs = multiFindOrganizationList(query.getOrgIds());

    // 项目的优惠合计和补入工作量
    List<ClinicTariffDiscountCouponVO> discounts = findTariffCategoryDiscountAmount(query);
      List<com.yunya.models.report.BaseBillDetail> baseBillDetails = remoteReportServiceFeign.billDeduction(query);
      // 组装数据并排序
    List<CategoryInfoIncomeVO> list =
        mergeCategoryIncomeList(
            tariffFuture.get(), originalFuture.get(), freePaymentFuture.get(), discounts, orgs,baseBillDetails);
    return list;
  }

  /**
   * 查询组织信息列表
   *
   * @return
   * @param orgIds
   */
  private List<OrganizationInfoDetail> multiFindOrganizationList(List<Integer> orgIds) {
    return systemServiceFeign.findOrgInfoInIds(orgIds);
  }

  /**
   * 组装数据并排序
   *
   * @param baseTariffVOS
   * @param originals
   * @param freePaymentMap
   * @param discountCoupons
   * @param orgList
   * @return
   * @throws Exception
   */
  private List<CategoryInfoIncomeVO> mergeCategoryIncomeList(
      List<BaseTariffVO> baseTariffVOS,
      List<ClinicTariffOrderVO> originals,
      Map<String, BigDecimal> freePaymentMap,
      List<ClinicTariffDiscountCouponVO> discountCoupons,
      List<OrganizationInfoDetail> orgList,
      List<com.yunya.models.report.BaseBillDetail> baseBillDetails) {
    Map<String, String> categoryMap = new HashMap<>(16);
    Map<String, CategoryInfoIncomeVO> resultMap =
        createEntityBaseMap(orgList, baseTariffVOS, categoryMap);
      Map<String, com.yunya.models.report.BaseBillDetail> collect = baseBillDetails.stream().collect(toMap(t -> t.getItemType() + "," + t.getItemId() + "," +t.getOrgId(), Function.identity(), (o, n) -> n));
      // 填充项目分类的原价
    if (StringHelper.isNotEmpty(originals)) {
      originals.forEach(
          vo -> {
            String categoryKey = categoryMap.get(vo.getType() + "," + vo.getBillingItemId());
            CategoryInfoIncomeVO income = resultMap.get(categoryKey + "." + vo.getOrgId());
              com.yunya.models.report.BaseBillDetail baseBillDetail = collect.get(vo.getType() + "," + vo.getBillingItemId() + "," + vo.getOrgId());
              BigDecimal originalAmount = vo.getAmount().add(income.getTotalOriginalAmount());
            income.setTotalOriginalAmount(originalAmount);
            income.setTotalActualAmount(originalAmount);
            income.setTotalAmount(originalAmount);
            if (Objects.nonNull(baseBillDetail)) {
                income.setTotalDeductionAmount(income.getTotalDeductionAmount().add(baseBillDetail.getSwipeWorkload()));
                income.setTotalDeductionWorkload(income.getTotalDeductionWorkload().add(baseBillDetail.getSwipeCouponWorkload()));
            }
          });
    }

    // 统计项目分类的优惠和补入、应收
    if (StringHelper.isNotEmpty(discountCoupons)) {
      discountCoupons.forEach(
          vo -> {
            String categoryKey = categoryMap.get(vo.getItemType() + "," + vo.getItemId());
            CategoryInfoIncomeVO income = resultMap.get(categoryKey + "." + vo.getOrgId());
            if (ObjectUtils.isEmpty(income)) {
              income = new CategoryInfoIncomeVO();
            }
            BigDecimal totalDiscountAmount =
                income.getTotalDiscountAmount().add(vo.getDiscountAmount());
            income.setTotalDiscountAmount(totalDiscountAmount);
            BigDecimal couponAmount = income.getTotalCouponAmount().add(vo.getSupplyWorkload());
            income.setTotalCouponAmount(couponAmount);
            BigDecimal actualAmount = income.getTotalOriginalAmount().subtract(totalDiscountAmount);
            income.setTotalActualAmount(actualAmount);
            income.setTotalAmount(actualAmount.add(couponAmount));
          });
    }

    // 统计项目分类的当月免单
    if (StringHelper.isNotEmpty(freePaymentMap)) {
      freePaymentMap.forEach(
          (key, freePayment) -> {
            String[] keys = key.split("\\.");
            String categoryKey = categoryMap.get(keys[0]);
            CategoryInfoIncomeVO income = resultMap.get(categoryKey + "." + keys[1]);
            BigDecimal freePaymentAmount = income.getTotalFreePaymentAmount().add(freePayment);
            income.setTotalFreePaymentAmount(freePaymentAmount);
            income.setTotalAmount(income.getTotalAmount().subtract(freePaymentAmount));
          });
    }
    // 排序
    return SortUtil.sort(new ArrayList<>(resultMap.values()), categoryIncomeCmpList());
  }

  /**
   * 创建分类收入汇总实体的基本信息
   *
   * @param orgList
   * @param baseTariffVOS
   * @param categoryMap
   * @return
   */
  private Map<String, CategoryInfoIncomeVO> createEntityBaseMap(
      List<OrganizationInfoDetail> orgList,
      List<BaseTariffVO> baseTariffVOS,
      Map<String, String> categoryMap) {
    Map<String, CategoryInfoIncomeVO> resultMap = new HashMap<>(16);
    baseTariffVOS.forEach(
        vo -> {
          Integer itemId = vo.getId();
          Integer categoryId = vo.getTariffCategoryId();
          String itemType = vo.getTariffCategoryNumber(); // 0-价目，1-商品
          orgList.forEach(
              org -> {
                String key = itemType + "," + categoryId + "." + org.getId();
                CategoryInfoIncomeVO income = new CategoryInfoIncomeVO();
                income.setOrgId(org.getId());
                income.setAbbreviation(org.getAbbreviation());
                income.setCategoryType(Byte.parseByte(itemType));
                income.setCategoryName(vo.getTariffCategoryName());
                income.setCategoryId(categoryId);
                resultMap.put(key, income);
              });
          categoryMap.put(itemType + "," + itemId, itemType + "," + categoryId);
        });
    return resultMap;
  }

  /**
   * 项目分类收入汇总的排序规则
   *
   * @return
   */
  private Comparator categoryIncomeCmpList() {
    return SortUtil.comparing(CategoryInfoIncomeVO::getOrgId)
        .thenComparing(CategoryInfoIncomeVO::getCategoryType)
        .thenComparing(CategoryInfoIncomeVO::getCategoryId);
  }

  /**
   * 多线程查询项目分类的当月免单
   *
   * @param query
   * @return
   */
  private Future<Map<String, BigDecimal>> multiFindTariffCategoryFreePaymentAmount(
      CategoryIncomeQuery query) {
    return executorService.submit(()->{
      List<BillItemAmountSharedVO> list = billPayShareDetailMapper.selectBillItemFreeAmountInRevoked(query);
      Map<String, BigDecimal> result = new HashMap<>(16);
      list.forEach(vo->{
        String itemKey = StringHelper.joinWith(",", vo.getItemType(), vo.getItemId());
        String key = StringHelper.joinWith(".", itemKey, vo.getOrgId());
        result.put(key, vo.getFreeAmount());
      });
      return result;
    });
  }

  /**
   * 多线程查询项目分类的当月免单
   *
   * @param query
   * @return
   */
  @Deprecated
  public Future<Map<String, BigDecimal>> multiFindTariffCategoryFreePaymentAmount0(
      CategoryIncomeQuery query) {
    return executorService.submit(
        () -> {
          // 撤销收费记录ID
          List<Integer> payIds = billPayRecordMapper.selectRevokePayList(query);
          // 调整收费方式
          payIds.addAll(billPayRecordMapper.selectAdjustPayList(query));
          // 有效账单的项目应收
          List<BillItemAmountSharedVO> orderDetails =
              mapper.selectClinicOrderDetailList(query, payIds);
          if (StringHelper.isNotEmpty(payIds)) {
            List<BillItemAmountSharedVO> revokeDetails =
                mapper.selectClinicOrderDetailBeforeRevoke(payIds);
            if (StringHelper.isNotEmpty(revokeDetails)) {
              DiscountCouponQuery queryForm = new DiscountCouponQuery();
              queryForm.setDateType((byte) 1);
              queryForm.setStartDate(query.getStartDate());
              queryForm.setEndDate(query.getEndDate());
              queryForm.setOrderRecordIds(
                  revokeDetails.stream()
                      .map(BillItemAmountSharedVO::getBillId)
                      .collect(Collectors.toSet()));
              List<ClinicTariffDiscountCouponVO> discounts =
                  discountFeign.findClinicTariffCategoryDiscountCoupon(queryForm);
              for (BillItemAmountSharedVO vo : revokeDetails) {
                Integer orderRecordId = vo.getBillId();
                Byte itemType = vo.getItemType();
                Integer itemId = vo.getItemId();
                for (ClinicTariffDiscountCouponVO discount : discounts) {
                  if (orderRecordId.equals(discount.getOrderRecordId())
                      && itemType.equals(discount.getItemType())
                      && itemId.equals(discount.getItemId())) {
                    vo.setItemActualAmount(
                        vo.getItemActualAmount().subtract(discount.getDiscountAmount()));
                  }
                }
              }
              orderDetails.addAll(revokeDetails);
            }
          }
          // 有效账单的免单收费总价
          List<OrderDetailInfoVO> freePaymentTotal =
              billPayDetailRecordBiz.findBillPayDetailByFreePayment(query, null, true);
          if (StringHelper.isNotEmpty(payIds)) {
            List<OrderDetailInfoVO> freePaymentTotal1 =
                billPayDetailRecordBiz.findBillPayDetailByFreePayment(query, payIds, false);
            freePaymentTotal.addAll(freePaymentTotal1);
          }
          return shareTariffFreePayment(orderDetails, freePaymentTotal);
        });
  }

  /**
   * 分摊免单支付金额到项目上
   *
   * @param orderDetails
   * @param freePaymentTotal
   * @return
   */
  private Map<String, BigDecimal> shareTariffFreePayment(
      List<BillItemAmountSharedVO> orderDetails, List<OrderDetailInfoVO> freePaymentTotal) {
    Map<String, BigDecimal> result = new HashMap<>(16);
    if (StringHelper.isNotEmpty(orderDetails)) {
      // 开单的[价目总免单、商品总免单、价目总应收、商品总应收、价目总实收]
      Map<Integer, BigDecimal[]> orderMap = new HashMap<>(16);
      orderDetails.forEach(
          vo -> {
            Integer orderRecordId = vo.getBillId();
            BigDecimal itemReceivalbeAmount = vo.getItemActualAmount();
            BigDecimal[] amount = orderMap.get(orderRecordId);
            if (ObjectUtils.isEmpty(amount)) {
              amount =
                  new BigDecimal[] {
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                  };
            }
            if (vo.getItemType().intValue() == 0) {
              amount[2] = amount[2].add(itemReceivalbeAmount);
            } else {
              amount[3] = amount[3].add(itemReceivalbeAmount);
            }
            orderMap.put(orderRecordId, amount);
          });
      orderDetails.forEach(
          vo -> {
            BigDecimal itemActualAmount = vo.getItemActualAmount();
            BigDecimal billReceivedAmount = vo.getBillReceivedAmount();
            if (billReceivedAmount.compareTo(BigDecimal.ZERO) != 0
                && vo.getItemType().intValue() == 0) {
              Integer billId = vo.getBillId();
              BigDecimal itemReceivedAmount =
                  itemActualAmount
                      .divide(billReceivedAmount, 8, BigDecimal.ROUND_HALF_UP)
                      .multiply(vo.getBillReceivedAmount());
              BigDecimal[] amount = orderMap.get(billId);
              if (ObjectUtils.isEmpty(amount)) {
                amount =
                    new BigDecimal[] {
                      BigDecimal.ZERO,
                      BigDecimal.ZERO,
                      BigDecimal.ZERO,
                      BigDecimal.ZERO,
                      BigDecimal.ZERO
                    };
              }
              amount[4] = amount[4].add(itemReceivedAmount);
              orderMap.put(billId, amount);
            }
          });
      freePaymentTotal.forEach(
          vo -> {
            Integer orderRecordId = vo.getOrderRecordId();
            BigDecimal[] amount = orderMap.get(orderRecordId);
            if (ObjectUtils.isEmpty(amount)) {
              amount =
                  new BigDecimal[] {
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
                  };
            }
            amount[0] = vo.getTotalAmount();
            BigDecimal billReceivedAmount = amount[4]; // 账单总实收
            BigDecimal oralFree = amount[0].subtract(billReceivedAmount); // 商品免单
            // 只有价目表项目免单时
            if (oralFree.compareTo(BigDecimal.ZERO) > 0) {
              // 既有价目表，又有商品表项目免单时
              amount[0] = billReceivedAmount;
              amount[1] = oralFree;
            } else {
              amount[1] = BigDecimal.ZERO;
            }
            orderMap.put(orderRecordId, amount);
          });
      orderDetails.forEach(
          vo -> {
            Integer orderRecordId = vo.getBillId();
            BigDecimal[] amount = orderMap.get(orderRecordId);
            BigDecimal itemActualAmount = vo.getItemActualAmount(); // 项目应收
            BigDecimal itemFreeTotal = amount[0]; // 价目总免单
            BigDecimal oralFreeTotal = amount[1]; // 商品总免单
            BigDecimal itemActualTotal = amount[2];
            BigDecimal oralActualTotal = amount[3];
            // 价目免单 = 价目应收/价目总应收 * 价目总免单
            BigDecimal free = BigDecimal.ZERO;
            if (vo.getItemType().intValue() == 0) {
              if (itemFreeTotal.compareTo(BigDecimal.ZERO) != 0) {
                free =
                    itemActualAmount
                        .divide(itemActualTotal, 8, BigDecimal.ROUND_HALF_UP)
                        .multiply(itemFreeTotal);
              }
            } else {
              if (oralFreeTotal.compareTo(BigDecimal.ZERO) != 0
                  && oralActualTotal.compareTo(BigDecimal.ZERO) != 0) {
                free =
                    itemActualAmount
                        .divide(oralActualTotal, 8, BigDecimal.ROUND_HALF_UP)
                        .multiply(oralFreeTotal);
              }
            }
            String key = vo.getItemType() + "," + vo.getItemId() + "." + vo.getOrgId();
            BigDecimal freePayment = result.get(key);
            if (ObjectUtils.isEmpty(freePayment)) {
              freePayment = BigDecimal.ZERO;
            }
            result.put(key, freePayment.add(free));
          });
    }
    return result;
  }

  /**
   * 查询门诊下项目的优惠合计和补入工作量
   *
   * @param query
   * @return
   */
  private List<ClinicTariffDiscountCouponVO> findTariffCategoryDiscountAmount(
      CategoryIncomeQuery query) {
    List<BillRecord> billRecords = billRecordBiz.selectRemoveBillAdjustDiscountOrderIds(query);
    if (StringHelper.isNotEmpty(billRecords)) {
      Map<Integer, Integer> map =
          billRecords.stream().collect(toMap(BillRecord::getOrderRecordId, BillRecord::getOrgId));
      DiscountCouponQuery queryForm = new DiscountCouponQuery();
      queryForm.setOrderRecordIds(map.keySet());
      List<ClinicTariffDiscountCouponVO> discounts =
          discountFeign.findClinicTariffCategoryDiscountCoupon(queryForm);
      if (StringHelper.isNotEmpty(discounts)) {
        discounts.forEach(vo -> vo.setOrgId(map.get(vo.getOrderRecordId())));
      }
      return discounts;
    }
    return new ArrayList();
  }

  /**
   * 多线程查询门诊下项目分类的原价合计
   *
   * @param query
   * @return
   */
  private Future<List<ClinicTariffOrderVO>> multiFindTariffCategoryOriginalAmount(
      CategoryIncomeQuery query) {
    return executorService.submit(() -> mapper.selectClinicTariffCategoryOriginalAmount(query));
  }

  /**
   * 查询全部项目表（包含价目表和商品表）
   *
   * @return
   */
  private Future<List<BaseTariffVO>> multiFindAllTariffList() {
    return executorService.submit(
        () -> {
          BaseTariffQueryForm queryForm = new BaseTariffQueryForm();
          queryForm.setWhetherPage(false);
          return baseTariffBiz.findAllTariffList(queryForm).getList();
        });
  }

  /**
   * 公司端报表-财务报表-分类收入汇总-导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCategoryIncome(HttpServletResponse response, CategoryIncomeQuery query)
      throws Exception {
    query.setWhetherPage(false);
    List<CategoryInfoIncomeVO> list = findCategoryIncomeList(query).getList();
    ExcelUtil<CategoryInfoIncomeVO> excelUtil = new ExcelUtil<>(CategoryInfoIncomeVO.class);
    excelUtil.exportExcel(response, list, "门诊分类收入汇总", "门诊分类收入汇总");
  }

  /**
   * 根据订单详情id查询订单详情列表
   *
   * @param orderDetailIds
   * @return
   */
  public List<OrderDetailVO> findOrderDetailById(List<Integer> orderDetailIds) {
    List<OrderDetailVO> result = new ArrayList<>();
    if (StringHelper.isNotEmpty(orderDetailIds)) {
      orderDetailIds.forEach(
          orderDetailId -> {
            OrderDetailVO vo = new OrderDetailVO();
            OrderDetail detail = mapper.selectByPrimaryKey(orderDetailId);
            BeanUtils.copyProperties(detail, vo);
            vo.setOrderDetailId(orderDetailId);
            Integer executorId = detail.getExecutorId();
            if (!ObjectUtils.isEmpty(executorId)) {
              SysEmployee employee = systemServiceFeign.findSysEmployeeById(executorId);
              if (!ObjectUtils.isEmpty(employee)) {
                vo.setExecutorName(employee.getName());
              }
            }
            result.add(vo);
          });
    }
    return result;
  }
}
