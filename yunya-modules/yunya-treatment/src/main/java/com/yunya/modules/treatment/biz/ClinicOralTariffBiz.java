package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.treatment.domain.form.ClinicItemMemberPriceForm;
import com.yunya.feign.treatment.domain.form.ClinicOralTariffForm;
import com.yunya.feign.treatment.domain.form.ClinicOralTariffUniteDiscountForm;
import com.yunya.feign.treatment.domain.form.MemberUniteDiscountForm;
import com.yunya.feign.treatment.domain.query.BaseOralTariffQueryForm;
import com.yunya.feign.treatment.domain.query.ClinicOralTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffInfoVO;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffVO;
import com.yunya.feign.treatment.domain.vo.ClinicOralTariffVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.system.MemberType;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.ClinicOralTariff;
import com.yunya.models.tariff.ClinicOralTariffMemberPrice;
import com.yunya.modules.treatment.mapper.BaseOralTariffMapper;
import com.yunya.modules.treatment.mapper.ClinicOralTariffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 描述: 门诊商品项目管理业务层
 *
 * @author GaoLuding
 * @create 2020-05-27 13:58
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicOralTariffBiz extends BaseBiz<ClinicOralTariffMapper, ClinicOralTariff> {

  /** 基础商品 */
  @Autowired private BaseOralTariffMapper baseOralTariffMapper;
  /** 门诊商品项目会员价 */
  @Autowired private ClinicOralTariffMemberPriceBiz clinicOralTariffMemberPriceBiz;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /**
   * 根据门诊商品项目ID获取门诊商品项目信息
   *
   * @param orgId 组织ID
   * @param oralTariffId 门诊商品项目ID
   * @return
   */
  public ClinicOralTariffVO findById(Integer orgId, Integer oralTariffId) {
    ClinicOralTariffVO resultData = mapper.selectClinicOralTariffById(orgId, oralTariffId);
    if (null == resultData) {
      BaseOralTariffInfoVO baseOralTariffInfo =
          baseOralTariffMapper.selectBaseOralTariffInfoById(oralTariffId);
      resultData = new ClinicOralTariffVO();
      resultData.setOrgId(orgId);
      resultData.setOralTariffCategoryId(baseOralTariffInfo.getOralTariffCategoryId());
      resultData.setOralTariffCategoryName(baseOralTariffInfo.getOralTariffCategoryName());
      resultData.setOralTariffCategoryNumber(baseOralTariffInfo.getOralTariffCategoryNumber());
      resultData.setOralTariffId(oralTariffId);
      resultData.setName(baseOralTariffInfo.getName());
      resultData.setEnglishName(baseOralTariffInfo.getEnglishName());
      resultData.setNumber(baseOralTariffInfo.getItemNumber());
      resultData.setUnit(baseOralTariffInfo.getUnit());
      resultData.setPrice(baseOralTariffInfo.getPrice());
      resultData.setInservice(true);
    }
    List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
    if (StringHelper.isNotEmpty(memberTypes)) {
      Map<Integer, Object> memberPrices = new HashMap<>(16);
      setClinicOralTariffMemberPrice(memberPrices, memberTypes, orgId, resultData);
    }
    return resultData;
  }

  /**
   * 根据条件查询门诊商品项目信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<ClinicOralTariffVO> findList(ClinicOralTariffQueryForm queryForm) {
    return findList(queryForm, false);
  }

  public PageInfo<ClinicOralTariffVO> findList(ClinicOralTariffQueryForm queryForm, boolean isExport) {
    PageInfo pageInfo;
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    Integer orgId = queryForm.getOrgId();
    BaseOralTariffQueryForm form = new BaseOralTariffQueryForm();
    form.setOralTariffCategoryId(queryForm.getOralTariffCategoryId());
    form.setKeyWord(queryForm.getKeyWord());
    List<BaseOralTariffVO> baseOralTariffs = baseOralTariffMapper.selectBaseOralTariffList(form);
    pageInfo = new PageInfo(baseOralTariffs);
    List<ClinicOralTariffVO> resultList = Lists.newArrayList();
    if (StringHelper.isNotEmpty(baseOralTariffs)) {
      List<Integer> tariffIds = baseOralTariffs.stream().map(BaseOralTariffVO::getId).collect(Collectors.toList());
      List<ClinicOralTariff> clinicOralTariffs = mapper.selectClinicOralTariffInId(orgId, tariffIds);
      baseOralTariffs.forEach(
          baseOralTariff -> {
            Integer tariffId = baseOralTariff.getId();
            ClinicOralTariffVO vo = new ClinicOralTariffVO();
            vo.setOrgId(orgId);
            vo.setOralTariffCategoryId(baseOralTariff.getOralTariffCategoryId());
            vo.setOralTariffCategoryName(baseOralTariff.getOralTariffCategoryName());
            vo.setOralTariffCategoryNumber(baseOralTariff.getOralTariffCategoryNumber());
            vo.setOralTariffId(tariffId);
            vo.setName(baseOralTariff.getName());
            vo.setEnglishName(baseOralTariff.getEnglishName());
            vo.setNumber(baseOralTariff.getItemNumber());
            vo.setUnit(baseOralTariff.getUnit());
            vo.setPrice(baseOralTariff.getPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
            vo.setInservice(baseOralTariff.getInservice());
            if (StringHelper.isNotEmpty(clinicOralTariffs)) {
              clinicOralTariffs.forEach(clinicOralTariff -> {
                if (clinicOralTariff.getOralTariffId().equals(tariffId)) {
                  vo.setId(clinicOralTariff.getId());
                  vo.setPrice(clinicOralTariff.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
                  vo.setInservice(clinicOralTariff.getInservice());
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
                setClinicOralTariffMemberPrice(memberPrices, memberTypes, orgId, tariffVO);
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
      ClinicOralTariffVO tariffVO) {
    ClinicOralTariffMemberPrice clinicOralTariffMemberPrice = new ClinicOralTariffMemberPrice();
    clinicOralTariffMemberPrice.setClinicId(orgId);
    Integer oralTariffId = tariffVO.getOralTariffId();
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
      memberPrices.put(memberTypeId, memberPrice.setScale(2,BigDecimal.ROUND_HALF_UP));
    }
    tariffVO.setMemberPrices(memberPrices);
  }

  /**
   * 修改门诊商品项目
   *
   * @param form 修改参数
   */
  public void modify(ClinicOralTariffForm form) {
    List<ClinicItemMemberPriceForm> memberPrices = form.getClinicItemMemberPrices();
    if (StringHelper.isEmpty(memberPrices)) {
      throw new ClientServiceException("修改失败，门诊价目表会员卡价格不能为空！", PARAMETERS_IS_ILLEGAL);
    }
    Integer oralTariffId = form.getOralTariffId();
    BaseOralTariff oralTariff = baseOralTariffMapper.selectByPrimaryKey(oralTariffId);
    if (null == oralTariff) {
      throw new ClientServiceException("门诊商品表修改失败，商品不存在！", PARAMETERS_IS_ILLEGAL);
    }
    BigDecimal formPrice = form.getPrice();
    Integer orgId = form.getOrgId();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    ClinicOralTariff entity = new ClinicOralTariff();
    entity.setClinicId(orgId);
    entity.setOralTariffId(oralTariffId);
    ClinicOralTariff resultData = mapper.selectOne(entity);
    if (null == resultData) {
      entity.setPrice(formPrice);
      entity.setCrtId(userId);
      entity.setCrtName(name);
      entity.setUpdId(userId);
      entity.setUpdName(name);
      mapper.insertSelective(entity);
    } else {
      BigDecimal resultDataPrice = resultData.getPrice();
      if (!resultDataPrice.equals(formPrice)) {
        resultData.setPrice(formPrice);
        resultData.setUpdId(userId);
        resultData.setUpdName(name);
        mapper.updateByPrimaryKeySelective(resultData);
      }
    }

    memberPrices.forEach(
        memberPrice -> {
          ClinicOralTariffMemberPrice clinicOralTariffMemberPrice =
              new ClinicOralTariffMemberPrice();
          clinicOralTariffMemberPrice.setClinicId(orgId);
          clinicOralTariffMemberPrice.setOralTariffId(oralTariffId);
          Integer memberTypeId = memberPrice.getMemberTypeId();
          clinicOralTariffMemberPrice.setMemberTypeId(memberTypeId);
          BigDecimal discountPrice = memberPrice.getDiscountPrice();
          ClinicOralTariffMemberPrice resultClinicOralTariffMemberPrice =
              clinicOralTariffMemberPriceBiz.selectOne(clinicOralTariffMemberPrice);
          clinicOralTariffMemberPrice.setDiscountPrice(discountPrice);
          if (null != resultClinicOralTariffMemberPrice) {
            clinicOralTariffMemberPrice.setUpdId(userId);
            clinicOralTariffMemberPrice.setUpdName(name);
            clinicOralTariffMemberPrice.setId(resultClinicOralTariffMemberPrice.getId());
            clinicOralTariffMemberPriceBiz.updateSelectiveById(clinicOralTariffMemberPrice);
          } else {
            clinicOralTariffMemberPrice.setCrtId(userId);
            clinicOralTariffMemberPrice.setCrtName(name);
            clinicOralTariffMemberPriceBiz.insertSelective(clinicOralTariffMemberPrice);
          }
        });
  }

  /**
   * 设置门诊商品项目是否启用
   *
   * @param orgId 组织ID
   * @param oralTariffId 商品项目ID
   */
  public void switchClinicOralTariff(Integer orgId, Integer oralTariffId) {
    BaseOralTariff oralTariff = baseOralTariffMapper.selectByPrimaryKey(oralTariffId);
    if (null == oralTariff) {
      throw new ClientServiceException("门诊商品启用设置失败，商品不存在！", PARAMETERS_IS_ILLEGAL);
    }
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    ClinicOralTariff entity = new ClinicOralTariff();
    entity.setClinicId(orgId);
    entity.setOralTariffId(oralTariffId);
    ClinicOralTariff resultData = mapper.selectOne(entity);
    if (null == resultData) {
      entity.setPrice(oralTariff.getPrice());
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
   * 统一设置门诊商品项目会员折扣
   *
   * @param form 统一折扣设置参数
   */
  public void uniteMemberDiscount(ClinicOralTariffUniteDiscountForm form) {
    List<Integer> oralTariffIds = form.getOralTariffIds();
    if (StringHelper.isEmpty(oralTariffIds)) {
      throw new ClientServiceException("统一设置门诊商品项目折扣失败，当前未选择任何门诊商品项目项目！", PARAMETERS_IS_ILLEGAL);
    }

    Set<MemberUniteDiscountForm> memberUniteDiscountForms = form.getMemberUniteDiscountForms();
    if (StringHelper.isEmpty(memberUniteDiscountForms)) {
      throw new ClientServiceException("统一设置门诊商品项目折扣失败,当前未选择任何会员卡类型", PARAMETERS_IS_ILLEGAL);
    }
    // 校验是否有相同会员卡类型
    checkMemberUniteDiscountForms(memberUniteDiscountForms);
    // 设置门诊商品会员折扣价
    Integer orgId = form.getOrgId();
    oralTariffIds.stream()
        .<Consumer<? super MemberUniteDiscountForm>>map(
            oralTariffId ->
                discountForm -> {
                  ClinicOralTariffMemberPrice clinicOralTariffMemberPrice =
                      new ClinicOralTariffMemberPrice();
                  clinicOralTariffMemberPrice.setClinicId(orgId);
                  clinicOralTariffMemberPrice.setOralTariffId(oralTariffId);
                  Integer memberType = discountForm.getMemberTypeId();
                  clinicOralTariffMemberPrice.setMemberTypeId(memberType);
                  ClinicOralTariffMemberPrice resultClinicOralTariffMemberPrice =
                      clinicOralTariffMemberPriceBiz.selectOne(clinicOralTariffMemberPrice);
                  ClinicOralTariff entity = new ClinicOralTariff();
                  entity.setClinicId(orgId);
                  entity.setOralTariffId(oralTariffId);
                  ClinicOralTariff resultData = mapper.selectOne(entity);
                  BigDecimal price;
                  if (null != resultData) {
                    price = resultData.getPrice();
                  } else {
                    BaseOralTariff oralTariff =
                        baseOralTariffMapper.selectByPrimaryKey(oralTariffId);
                    price = oralTariff.getPrice();
                  }
                  BigDecimal discountPrice =
                      price
                          .multiply(BigDecimal.valueOf(discountForm.getRate()))
                          .divide(BigDecimal.valueOf(100), 2);
                  clinicOralTariffMemberPrice.setDiscountPrice(discountPrice);
                  if (resultClinicOralTariffMemberPrice == null) {
                    clinicOralTariffMemberPrice.setCrtId(
                        Integer.valueOf(BaseContextHandler.getUserID()));
                    clinicOralTariffMemberPrice.setCrtName(BaseContextHandler.getName());
                    clinicOralTariffMemberPriceBiz.insertSelective(clinicOralTariffMemberPrice);
                  } else {
                    resultClinicOralTariffMemberPrice.setDiscountPrice(discountPrice);
                    resultClinicOralTariffMemberPrice.setUpdId(
                        Integer.valueOf(BaseContextHandler.getUserID()));
                    resultClinicOralTariffMemberPrice.setUpdName(BaseContextHandler.getName());
                    clinicOralTariffMemberPriceBiz.updateSelectiveById(
                        resultClinicOralTariffMemberPrice);
                  }
                })
        .forEachOrdered(memberUniteDiscountForms::forEach);
  }

  /**
   * 返回会员折扣检测结果
   *
   * @param memberUniteDiscounts 会员卡折扣信息列表
   */
  private void checkMemberUniteDiscountForms(Set<MemberUniteDiscountForm> memberUniteDiscounts) {
    Set<MemberUniteDiscountForm> forms =
        new TreeSet<>(Comparator.comparing(MemberUniteDiscountForm::getMemberTypeId));
    forms.addAll(memberUniteDiscounts);
    if (forms.size() < memberUniteDiscounts.size()) {
      throw new ClientServiceException("统一门诊价目表折扣失败，同一个会员卡不能设置两条折扣！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 根据条件导出门诊商品项目列表
   *
   * @param response 响应
   * @param queryForm 查询条件
   */
  public void exportClinicOralTariffList(
      HttpServletResponse response, ClinicOralTariffQueryForm queryForm) throws IOException {
      queryForm.setWhetherPage(false);
      List<ClinicOralTariffVO> resultList = findList(queryForm, true).getList();
//    List<BaseOralTariffVO> resultList = mapper.selectClinicOralTariffExportList(queryForm);
    Integer orgId = queryForm.getOrgId();
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    String abbreviation = null;
    if (null != orgInfo) {
      abbreviation = orgInfo.getAbbreviation();
    }
    ExcelUtil<ClinicOralTariffVO> excelUtil = new ExcelUtil<>(ClinicOralTariffVO.class);
    excelUtil.exportExcel(response, resultList, abbreviation + "_商品项目列表");
  }
}
