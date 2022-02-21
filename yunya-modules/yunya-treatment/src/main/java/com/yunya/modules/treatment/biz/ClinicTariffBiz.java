package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.form.ClinicItemMemberPriceForm;
import com.yunya.feign.treatment.domain.form.ClinicTariffForm;
import com.yunya.feign.treatment.domain.form.ClinicTariffUniteDiscountForm;
import com.yunya.feign.treatment.domain.form.MemberUniteDiscountForm;
import com.yunya.feign.treatment.domain.model.ClinicTariffSwitchModel;
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
import com.yunya.feign.treatment.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseCategoryInfoVO;
import com.yunya.feign.treatment.domain.vo.BaseTariffInfoVO;
import com.yunya.feign.treatment.domain.vo.BaseTariffVO;
import com.yunya.feign.treatment.domain.vo.ClinicTariffVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.system.MemberType;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.ClinicTariff;
import com.yunya.models.tariff.ClinicTariffMemberPrice;
import com.yunya.modules.treatment.mapper.BaseTariffMapper;
import com.yunya.modules.treatment.mapper.ClinicTariffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 描述: 门诊价目表业务层
 *
 * @author GaoLuding
 * @create 2020-05-27 13:58
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicTariffBiz extends BaseBiz<ClinicTariffMapper, ClinicTariff> {

  /** 基础价目表 */
  @Autowired private BaseTariffMapper baseTariffMapper;
  /** 门诊价目表会员价 */
  @Autowired private ClinicTariffMemberPriceBiz clinicTariffMemberPriceBiz;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;
  /** 线程池 */
  @Resource(name = "treatmentThreadPool")
  private ExecutorService executorService;

  /**
   * 根据门诊价目表ID获取门诊价目表信息
   *
   * @param orgId 组织ID
   * @param tariffId 门诊价目表ID
   * @return
   */
  public ClinicTariffVO findById(Integer orgId, Integer tariffId) {
    ClinicTariffVO resultData = mapper.selectClinicTariffById(orgId, tariffId);
    if (null == resultData) {
      BaseTariffInfoVO baseTariffInfo = baseTariffMapper.selectBaseTariffInfoById(tariffId);
      resultData = new ClinicTariffVO();
      resultData.setOrgId(orgId);
      resultData.setTariffCategoryId(baseTariffInfo.getTariffCategoryId());
      resultData.setTariffCategoryName(baseTariffInfo.getTariffCategoryName());
      resultData.setTariffCategoryNumber(baseTariffInfo.getTariffCategoryNumber());
      resultData.setTariffId(tariffId);
      resultData.setName(baseTariffInfo.getName());
      resultData.setEnglishName(baseTariffInfo.getEnglishName());
      resultData.setNumber(baseTariffInfo.getItemNumber());
      resultData.setUnit(baseTariffInfo.getUnit());
      resultData.setPrice(baseTariffInfo.getPrice());
      resultData.setInservice(true);
    }
    List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
    if (StringHelper.isNotEmpty(memberTypes)) {
      Map<Integer, Object> memberPrices = new HashMap<>(16);
      setClinicTariffMemberPrice(memberPrices, memberTypes, orgId, resultData);
    }
    return resultData;
  }

  /**
   * 根据条件查询门诊价目表信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<ClinicTariffVO> findList(ClinicTariffQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<ClinicTariffVO> resultList = mapper.selectClinicTariffList(queryForm);
    if (StringHelper.isNotEmpty(resultList)) {
      List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
      if (StringHelper.isNotEmpty(memberTypes)) {
        resultList.forEach(
            tariffVO -> {
              Map<Integer, Object> memberPrices = new HashMap<>(16);
              // 设置门诊价目表会员价,设置价格精度，为小数点后两位四舍五入
              setClinicTariffMemberPrice(memberPrices, memberTypes, queryForm.getOrgId(), tariffVO);
            });
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询门诊价目表
   *
   * @param queryForm 查询条件
   * @param isExport 是否导出
   * @return
   */
  public PageInfo<ClinicTariffVO> findList(ClinicTariffQueryForm queryForm, boolean isExport) {
    PageInfo pageInfo;
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    Integer orgId = queryForm.getOrgId();
    BaseTariffQueryForm form = new BaseTariffQueryForm();
    form.setTariffCategoryId(queryForm.getTariffCategoryId());
    form.setKeyWord(queryForm.getKeyWord());
    List<BaseTariffVO> baseTariffs = baseTariffMapper.selectBaseTariffList(form);
    pageInfo = new PageInfo(baseTariffs);
    List<ClinicTariffVO> resultList = Lists.newArrayList();
    if (StringHelper.isNotEmpty(baseTariffs)) {
      List<Integer> tariffIds =
          baseTariffs.stream().map(BaseTariffVO::getId).collect(Collectors.toList());
      List<ClinicTariff> clinicTariffs = mapper.selectClinicTariffInId(orgId, tariffIds);
      baseTariffs.forEach(
          baseTariff -> {
            Integer tariffId = baseTariff.getId();
            ClinicTariffVO vo = new ClinicTariffVO();
            vo.setOrgId(orgId);
            vo.setTariffCategoryId(baseTariff.getTariffCategoryId());
            vo.setTariffCategoryName(baseTariff.getTariffCategoryName());
            vo.setTariffCategoryNumber(baseTariff.getTariffCategoryNumber());
            vo.setTariffId(tariffId);
            vo.setName(baseTariff.getName());
            vo.setEnglishName(baseTariff.getEnglishName());
            vo.setNumber(baseTariff.getItemNumber());
            vo.setUnit(baseTariff.getUnit());
            vo.setPrice(baseTariff.getPrice());
            vo.setInservice(baseTariff.getInservice());
            if (StringHelper.isNotEmpty(clinicTariffs)) {
              clinicTariffs.forEach(
                  clinicTariff -> {
                    if (clinicTariff.getTariffId().equals(tariffId)) {
                      vo.setId(clinicTariff.getId());
                      vo.setPrice(clinicTariff.getPrice());
                      vo.setInservice(clinicTariff.getInservice());
                    }
                  });
            }
            resultList.add(vo);
          });
      if (StringHelper.isNotEmpty(resultList) && !isExport) {
        List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
        if (StringHelper.isNotEmpty(memberTypes)) {
          resultList.forEach(
              tariffVO -> {
                Map<Integer, Object> memberPrices = new HashMap<>(16);
                // 设置门诊价目表会员价,设置价格精度，为小数点后两位四舍五入
                setClinicTariffMemberPrice(memberPrices, memberTypes, orgId, tariffVO);
              });
        }
      }
      pageInfo.setList(resultList);
    } else {
      pageInfo.setList(new ArrayList());
    }
    return pageInfo;
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
      ClinicTariffVO tariffVO) {
    ClinicTariffMemberPrice clinicTariffMemberPrice = new ClinicTariffMemberPrice();
    clinicTariffMemberPrice.setClinicId(orgId);
    clinicTariffMemberPrice.setTariffId(tariffVO.getTariffId());
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
    tariffVO.setPrice(tariffVO.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
    tariffVO.setMemberPrices(memberPrices);
  }

  /**
   * 修改门诊价目表
   *
   * @param form 修改参数
   */
  public void modify(ClinicTariffForm form) {
    Integer tariffId = form.getTariffId();
    BaseTariff tariff = baseTariffMapper.selectByPrimaryKey(tariffId);
    if (null == tariff) {
      throw new ClientServiceException("门诊价目表修改失败，价目表不存在！", PARAMETERS_IS_ILLEGAL);
    }
    Integer orgId = form.getOrgId();
    BigDecimal formPrice = form.getPrice();
    Integer userId = 1;
    String name = "22";
    ClinicTariff entity = new ClinicTariff();
    entity.setClinicId(orgId);
    entity.setTariffId(tariffId);
    ClinicTariff resultData = mapper.selectOne(entity);
    if (null == resultData) {
      entity.setPrice(formPrice);
      entity.setCrtId(userId);
      entity.setCrtName(name);
      entity.setUpdId(userId);
      entity.setUpdName(name);
      mapper.insertSelective(entity);
    } else {
      BigDecimal resultDataPrice = resultData.getPrice();
      if (resultDataPrice.compareTo(formPrice) != 0) {
        resultData.setPrice(formPrice);
        resultData.setUpdId(userId);
        resultData.setUpdName(name);
        mapper.updateByPrimaryKeySelective(resultData);
      }
    }
    List<ClinicItemMemberPriceForm> memberPrices = form.getClinicItemMemberPrices();
    if (StringHelper.isNotEmpty(memberPrices)) {
      ClinicTariffMemberPrice clinicTariffMemberPrice = new ClinicTariffMemberPrice();
      memberPrices.forEach(
          memberPrice -> {
            clinicTariffMemberPrice.setClinicId(orgId);
            clinicTariffMemberPrice.setTariffId(tariffId);
            Integer memberTypeId = memberPrice.getMemberTypeId();
            clinicTariffMemberPrice.setMemberTypeId(memberTypeId);
            ClinicTariffMemberPrice resultClinicTariffMemberPrice =
                clinicTariffMemberPriceBiz.selectOne(clinicTariffMemberPrice);
            BigDecimal discountPrice = memberPrice.getDiscountPrice();
            if (null != resultClinicTariffMemberPrice) {
              resultClinicTariffMemberPrice.setDiscountPrice(discountPrice);
              resultClinicTariffMemberPrice.setUpdId(userId);
              resultClinicTariffMemberPrice.setUpdName(name);
              clinicTariffMemberPriceBiz.updateSelectiveById(resultClinicTariffMemberPrice);
            } else {
              clinicTariffMemberPrice.setDiscountPrice(discountPrice);
              clinicTariffMemberPrice.setCrtId(userId);
              clinicTariffMemberPrice.setCrtName(name);
              clinicTariffMemberPriceBiz.insertSelective(clinicTariffMemberPrice);
            }
          });
    }
  }

  /**
   * 设置门诊价目表是否启用
   *
   * @param orgId 组织ID
   * @param tariffId 价目表ID
   */
  public void switchClinicTariff(Integer orgId, Integer tariffId) {
    BaseTariff tariff = baseTariffMapper.selectByPrimaryKey(tariffId);
    if (null == tariff) {
      throw new ClientServiceException("门诊价目表启用设置失败，价目表不存在！", PARAMETERS_IS_ILLEGAL);
    }
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    ClinicTariff entity = new ClinicTariff();
    entity.setClinicId(orgId);
    entity.setTariffId(tariffId);
    ClinicTariff resultData = mapper.selectOne(entity);
    if (null == resultData) {
      entity.setPrice(tariff.getPrice());
      entity.setInservice(false);
      entity.setCrtId(userId);
      entity.setCrtName(name);
      entity.setUpdId(userId);
      entity.setUpdName(name);
      mapper.insertSelective(entity);
    } else {
      resultData.setInservice(!resultData.getInservice());
      resultData.setUpdId(userId);
      resultData.setUpdName(name);
      mapper.updateByPrimaryKeySelective(resultData);
    }
  }

  /**
   * 统一设置门诊价目表会员折扣
   *
   * @param form 统一折扣设置参数
   */
  public void uniteMemberDiscount(ClinicTariffUniteDiscountForm form) {
    List<Integer> tariffIds = form.getTariffIds();
    if (StringHelper.isEmpty(tariffIds)) {
      throw new ClientServiceException("统一设置门诊价目表折扣失败，当前未选择任何门诊价目表项目！", PARAMETERS_IS_ILLEGAL);
    }

    List<MemberUniteDiscountForm> memberUniteDiscountForms = form.getMemberUniteDiscountForms();
    if (StringHelper.isEmpty(memberUniteDiscountForms)) {
      throw new ClientServiceException("统一设置门诊价目表折扣失败,当前未选择任何会员卡类型", PARAMETERS_IS_ILLEGAL);
    }
    // 检查是否有相同会员卡折扣
    checkMemberUniteDiscountForms(memberUniteDiscountForms);
    // 设置门诊价目表会员折扣价
    Integer[] orgIds = form.getOrgIds();
    if (StringHelper.isNotEmpty(orgIds)) {
      Arrays.stream(orgIds)
          .forEachOrdered(
              orgId ->
                  tariffIds.stream()
                      .<Consumer<? super MemberUniteDiscountForm>>map(
                          tariffId ->
                              discountForm -> {
                                ClinicTariffMemberPrice clinicTariffMemberPrice =
                                    new ClinicTariffMemberPrice();
                                clinicTariffMemberPrice.setClinicId(orgId);
                                clinicTariffMemberPrice.setTariffId(tariffId);
                                Integer memberType = discountForm.getMemberTypeId();
                                clinicTariffMemberPrice.setMemberTypeId(memberType);
                                ClinicTariffMemberPrice resultClinicTariffMemberPrice =
                                    clinicTariffMemberPriceBiz.selectOne(clinicTariffMemberPrice);
                                BigDecimal price;
                                ClinicTariff entity = new ClinicTariff();
                                entity.setClinicId(orgId);
                                entity.setTariffId(tariffId);
                                ClinicTariff resultData = mapper.selectOne(entity);
                                if (null != resultData) {
                                  price = resultData.getPrice();
                                } else {
                                  BaseTariff tariff = baseTariffMapper.selectByPrimaryKey(tariffId);
                                  price = tariff.getPrice();
                                }
                                BigDecimal discountPrice =
                                    price
                                        .multiply(BigDecimal.valueOf(discountForm.getRate()))
                                        .divide(BigDecimal.valueOf(100), 2);
                                clinicTariffMemberPrice.setDiscountPrice(discountPrice);
                                if (resultClinicTariffMemberPrice == null) {
                                  clinicTariffMemberPrice.setCrtId(
                                      Integer.valueOf(BaseContextHandler.getUserID()));
                                  clinicTariffMemberPrice.setCrtName(BaseContextHandler.getName());
                                  clinicTariffMemberPriceBiz.insertSelective(
                                      clinicTariffMemberPrice);
                                } else {
                                  resultClinicTariffMemberPrice.setDiscountPrice(discountPrice);
                                  resultClinicTariffMemberPrice.setUpdId(
                                      Integer.valueOf(BaseContextHandler.getUserID()));
                                  resultClinicTariffMemberPrice.setUpdName(
                                      BaseContextHandler.getName());
                                  clinicTariffMemberPriceBiz.updateSelectiveById(
                                      resultClinicTariffMemberPrice);
                                }
                              })
                      .forEachOrdered(memberUniteDiscountForms::forEach));
    }
  }

  /**
   * 返回会员折扣检测结果
   *
   * @param memberUniteDiscounts 会员卡折扣列表
   */
  private void checkMemberUniteDiscountForms(List<MemberUniteDiscountForm> memberUniteDiscounts) {
    Set<MemberUniteDiscountForm> forms =
        new TreeSet<>(Comparator.comparing(MemberUniteDiscountForm::getMemberTypeId));
    forms.addAll(memberUniteDiscounts);
    if (forms.size() < memberUniteDiscounts.size()) {
      throw new ClientServiceException("统一门诊价目表折扣失败，同一个会员卡不能设置两条折扣！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 根据条件导出门诊价目表
   *
   * @param response 响应
   * @param queryForm 查询条件
   */
  public void exportClinicTariffList(HttpServletResponse response, ClinicTariffQueryForm queryForm)
      throws IOException {
    List<ClinicTariffVO> resultList = findList(queryForm, true).getList();
    if (!CollectionUtils.isEmpty(resultList)) {
      resultList.removeIf(clinicTariffVO -> !clinicTariffVO.getInservice());
    }
    //    List<BaseTariffVO> resultList = mapper.selectClinicTariffExportList(queryForm);
    Integer orgId = queryForm.getOrgId();
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    String abbreviation = null;
    if (null != orgInfo) {
      abbreviation = orgInfo.getAbbreviation();
    }
    ExcelUtil<ClinicTariffVO> excelUtil = new ExcelUtil<>(ClinicTariffVO.class);
    excelUtil.exportExcel(response, resultList, abbreviation + "-价目表信息列表");
  }

  /**
   * 根据条件查询门诊基础价目表（卡券设计-产品详情-适用项目）
   *
   * @param type
   * @param search
   * @return
   */
  public ResponseResult clinicBaseTariff(Byte type, String search) {
    if (type == 0) {
      List<BaseCategoryInfoVO> baseCategoryInfoVOS = mapper.selectTariffList(search);
      return ResponseUtil.success(baseCategoryInfoVOS);
    } else if (type == 1) {
      List<BaseCategoryInfoVO> baseCategoryInfoVOS = mapper.selectBaseOralTariffList(search);
      return ResponseUtil.success(baseCategoryInfoVOS);
    }
    return ResponseUtil.fail(PARAMETERS_IS_ILLEGAL, "参数错误", null);
  }

  /**
   * 批量插入数据
   *
   * @param list
   */
  public int insertEntities(List<ClinicTariff> list) {
    return mapper.insertEntities(list);
  }

  /**
   * 根据价目表ID启用门诊价目表
   *
   * @param tariffId 价目表ID
   * @param userId 操作人ID
   * @param userName 操作人姓名
   */
  public void enableClinicTariffByTariffId(Integer tariffId, Integer userId, String userName) {
    mapper.enableClinicTariffByTariffId(tariffId, userId, userName);
  }

  /**
   * 根据价目表ID禁用门诊价目表
   *
   * @param tariffId 价目表ID
   * @param userId 操作人ID
   * @param userName 操作人姓名
   */
  public void disableClinicTariffByTariffId(Integer tariffId, Integer userId, String userName) {
    mapper.disableClinicTariffByTariffId(tariffId, userId, userName);
  }

  /**
   * 批量插入门诊价目表列表
   *
   * @param clinicTariffs 门诊价目表列表
   */
  public void batchInsert(List<ClinicTariff> clinicTariffs) {
    mapper.batchInsert(clinicTariffs);
  }

  /**
   * 一键启用/禁用门诊价目表整个分类
   *
   * @param model 价目表分类参数
   */
  public void switchClinicTariffCategory(ClinicTariffSwitchModel model)
      throws InterruptedException {
    BaseTariff baseTariff = new BaseTariff();
    baseTariff.setTariffCategoryId(model.getTariffCategoryId());
    List<BaseTariff> tariffs = baseTariffMapper.select(baseTariff);
    if (StringHelper.isNotEmpty(tariffs)) {
      CountDownLatch countDownLatch = new CountDownLatch(tariffs.size());
      List<ClinicTariff> clinicTariffs = Lists.newArrayList();
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String userName = BaseContextHandler.getName();
      tariffs.forEach(
          tariff -> {
            try {
              executorService.submit(
                  () -> {
                    ClinicTariff clinicTariff = new ClinicTariff();
                    clinicTariff.setClinicId(model.getOrgId());
                    clinicTariff.setInservice(true);
                    clinicTariff.setCrtId(userId);
                    clinicTariff.setCrtName(userName);
                    clinicTariff.setCrtTime(new Date());
                    clinicTariff.setUpdId(userId);
                    clinicTariff.setUpdName(userName);
                    clinicTariff.setTariffId(tariff.getId());
                    clinicTariff.setPrice(tariff.getPrice());
                    clinicTariff.setInservice(model.getIsAvailable());
                    clinicTariffs.add(clinicTariff);
                  });
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              countDownLatch.countDown();
            }
          });
      countDownLatch.await();

      ClinicTariff clinicTariff = new ClinicTariff();
      clinicTariff.setClinicId(model.getOrgId());
      List<ClinicTariff> list = mapper.select(clinicTariff);
      if (StringHelper.isNotEmpty(list)) {
        CountDownLatch countDownLatch1 = new CountDownLatch(list.size());
        list.stream()
            .<Consumer<? super ClinicTariff>>map(
                tariff ->
                    clinicTariff1 -> {
                      try {
                        executorService.submit(
                            () -> {
                              if (tariff.getTariffId().equals(clinicTariff1.getTariffId())) {
                                clinicTariff1.setPrice(tariff.getPrice());
                                mapper.delete(tariff);
                              }
                            });
                      } finally {
                        countDownLatch1.countDown();
                      }
                    })
            .forEach(clinicTariffs::forEach);
        countDownLatch1.await();
      }

      List<List<ClinicTariff>> lists = Lists.partition(clinicTariffs, 500);
      CountDownLatch latch = new CountDownLatch(lists.size());
      lists.forEach(
          tariffList -> {
            try {
              executorService.submit(() -> batchInsert(tariffList));
            } finally {
              latch.countDown();
            }
          });
      latch.await();
    }
  }
}
