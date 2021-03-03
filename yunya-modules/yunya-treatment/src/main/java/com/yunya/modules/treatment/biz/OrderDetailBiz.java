package com.yunya.modules.treatment.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.vo.ItemUseBenefitVo;
import com.yunya.feign.discount.domain.vo.OrderBenefitDetailVo;
import com.yunya.feign.report.domain.query.SpecialistProjectCompletedCountQuery;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.feign.treatment.domain.form.BillPrintInfoForm;
import com.yunya.feign.treatment.domain.form.ModificationExecutorForm;
import com.yunya.feign.treatment.domain.model.GoodsDetailModel;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.clinic_base.SpecialistProject;
import com.yunya.models.system.AccountItem;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.ClinicOralTariff;
import com.yunya.models.tariff.ClinicTariff;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import com.yunya.modules.treatment.mapper.OrderDetailMapper;
import com.yunya.modules.treatment.mapper.OrderRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.ORDER_FINISH_STATUS;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.LOCK_ORDER_PROCESSING_CHARGE;

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
            if (1 == type) {
              BaseOralTariff oralTariff = baseOralTariffBiz.selectById(billingItemId);
              if (null != oralTariff) {
                vo.setBillingItemName(oralTariff.getName());
                vo.setUnit(oralTariff.getUnit());
              }
            }
            // todo 从缓存中获取用户（员工）信息
            Integer executorId = vo.getExecutorId();
            if (null != executorId) {
              SysUserInfoDetail employeeInfo =
                  systemServiceFeign.findSysUserEmployeeInfoByUserId(executorId);
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
            // todo 从缓存中获取开单项目信息
            Integer billingItemId = vo.getBillingItemId();
            Byte type = vo.getType();
            switch (type) {
              case 0:
                BaseTariff tariff = baseTariffBiz.selectById(billingItemId);
                if (null != tariff) {
                  vo.setBillingItemName(tariff.getName());
                  vo.setUnit(tariff.getUnit());
                }
                break;
              case 1:
                BaseOralTariff oralTariff = baseOralTariffBiz.selectById(billingItemId);
                if (null != oralTariff) {
                  vo.setBillingItemName(oralTariff.getName());
                  vo.setUnit(oralTariff.getUnit());
                }
                break;
              default:
                break;
            }
            // todo 从缓存中获取用户（员工）信息
            Integer executorId = vo.getExecutorId();
            if (null != executorId) {
              SysUserInfoDetail employeeInfo =
                  systemServiceFeign.findSysUserEmployeeInfoByUserId(executorId);
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
    List<OrderDetailChargeVO> chargeOrderDetailList = getChargeOrderDetailList(orderRecordId);
    // 设置10分钟（该段时间内不允许其他用户重复收费，解锁）
    redisUtils.set(redisKey, orderRecordId + ":" + userId, 600);
    return chargeOrderDetailList;
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
    // todo 从缓存中获取开单项目信息
    Integer billingItemId = vo.getBillingItemId();
    Byte type = vo.getType();
    switch (type) {
      case 0:
        BaseTariff tariff = baseTariffBiz.selectById(billingItemId);
        if (null != tariff) {
          vo.setBillingItemName(tariff.getName());
          vo.setUnit(tariff.getUnit());
        }
        break;
      case 1:
        BaseOralTariff oralTariff = baseOralTariffBiz.selectById(billingItemId);
        if (null != oralTariff) {
          vo.setBillingItemName(oralTariff.getName());
          vo.setUnit(oralTariff.getUnit());
        }
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
        // todo 从缓存查询开单项目
        switch (type) {
          case 0:
            tariff.setClinicId(orgId);
            tariff.setTariffId(itemId);
            ClinicTariff clinicTariff = clinicTariffBiz.selectOne(tariff);
            if (null != clinicTariff) {
              price = clinicTariff.getPrice();
            } else {
              BaseTariff baseTariff = baseTariffBiz.selectById(itemId);
              if (null != baseTariff) {
                price = baseTariff.getPrice();
              }
            }
            break;
          case 1:
            oralTariff.setClinicId(orgId);
            oralTariff.setOralTariffId(itemId);
            ClinicOralTariff clinicOralTariff = clinicOralTariffBiz.selectOne(oralTariff);
            if (null != clinicOralTariff) {
              price = clinicOralTariff.getPrice();
            } else {
              BaseOralTariff baseOralTariff = baseOralTariffBiz.selectById(itemId);
              if (null != baseOralTariff) {
                price = baseOralTariff.getPrice();
              }
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
            mapper.updateByPrimaryKey(orderDetail);
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
      SpecialistProjectReportModel specialistProjectReportModel) {
    Integer count = 0;
    List<SpecialistProjectReportVO> specialistProjectReportVOList = new ArrayList<>();
    for (SpecialistProject specialistProject :
        specialistProjectReportModel.getSpecialistProjects()) {
      SpecialistProjectReportVO specialistProjectReportVO = new SpecialistProjectReportVO();
      specialistProjectReportVO.setSpecialistProjectName(specialistProject.getName());
      String tariffIds = specialistProject.getTariffIds();
      if (tariffIds != null) {
        String[] billingItemIds = tariffIds.split(",");
        if (billingItemIds.length > 0) {
          specialistProjectReportModel.setBillingItemIds(billingItemIds);
          Integer numberOfItems =
              mapper.selectTariffSpecialistPercentage(specialistProjectReportModel);
          count = count + numberOfItems;
          specialistProjectReportVO.setPercentage(numberOfItems.toString());
        }
      }
      specialistProjectReportVOList.add(specialistProjectReportVO);
    }
    for (SpecialistProjectReportVO specialistProjectReportVO : specialistProjectReportVOList) {
      String percentage =
          mapper.percentage(Integer.parseInt(specialistProjectReportVO.getPercentage()), count);
      specialistProjectReportVO.setPercentage(percentage);
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
}
