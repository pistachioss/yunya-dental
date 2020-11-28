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
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
import com.yunya.feign.treatment.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.*;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;

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
      baseTariffs.forEach(
          baseTariff -> {
            Integer tariffId = baseTariff.getId();
            ClinicTariff entity = new ClinicTariff();
            entity.setTariffId(tariffId);
            entity.setClinicId(orgId);
            ClinicTariff clinicTariff = mapper.selectOne(entity);
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
            if (null != clinicTariff) {
              vo.setId(clinicTariff.getId());
              vo.setPrice(clinicTariff.getPrice());
              vo.setInservice(clinicTariff.getInservice());
            } else {
              vo.setPrice(baseTariff.getPrice());
              vo.setInservice(baseTariff.getInservice());
            }
            resultList.add(vo);
          });
      if (StringHelper.isNotEmpty(resultList)) {
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
        memberPrice = memberPriceResult.getDiscountPrice().setScale(2,BigDecimal.ROUND_HALF_UP);
      } else {
        memberTypeId = memberType.getId();
        memberPrice =
                (tariffVO
                .getPrice()
                .multiply(BigDecimal.valueOf(memberType.getRate()))
                .divide(BigDecimal.valueOf(100), 2))
                .setScale(2,BigDecimal.ROUND_HALF_UP);
      }
      memberPrices.put(memberTypeId, memberPrice);
    }
    // 设置价格精度小数点后两位四舍五入，没有在上个方法中设置精度是为了保证会员价计算精确
    tariffVO.setPrice(tariffVO.getPrice().setScale(2,BigDecimal.ROUND_HALF_UP));
    tariffVO.setMemberPrices(memberPrices);
  }

  /**
   * 修改门诊价目表
   *
   * @param form 修改参数
   */
  public void modify(ClinicTariffForm form) {
    List<ClinicItemMemberPriceForm> memberPrices = form.getClinicItemMemberPrices();
    if (StringHelper.isEmpty(memberPrices)) {
      throw new ClientServiceException("修改失败，门诊价目表会员卡价格不能为空！", PARAMETERS_IS_ILLEGAL);
    }
    Integer tariffId = form.getTariffId();
    BaseTariff tariff = baseTariffMapper.selectByPrimaryKey(tariffId);
    if (null == tariff) {
      throw new ClientServiceException("门诊价目表修改失败，价目表不存在！", PARAMETERS_IS_ILLEGAL);
    }
    Integer orgId = form.getOrgId();
    BigDecimal formPrice = form.getPrice();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
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
      if (!resultDataPrice.equals(formPrice)) {
        resultData.setPrice(formPrice);
        resultData.setUpdId(userId);
        resultData.setUpdName(name);
        mapper.updateByPrimaryKeySelective(resultData);
      }
    }

    memberPrices.forEach(
        memberPrice -> {
          ClinicTariffMemberPrice clinicTariffMemberPrice = new ClinicTariffMemberPrice();
          clinicTariffMemberPrice.setClinicId(orgId);
          clinicTariffMemberPrice.setTariffId(tariffId);
          Integer memberTypeId = memberPrice.getMemberTypeId();
          clinicTariffMemberPrice.setMemberTypeId(memberTypeId);
          BigDecimal discountPrice = memberPrice.getDiscountPrice();
          ClinicTariffMemberPrice resultClinicTariffMemberPrice =
              clinicTariffMemberPriceBiz.selectOne(clinicTariffMemberPrice);
          clinicTariffMemberPrice.setDiscountPrice(discountPrice);
          if (null != resultClinicTariffMemberPrice) {
            clinicTariffMemberPrice.setUpdId(userId);
            clinicTariffMemberPrice.setUpdName(name);
            clinicTariffMemberPrice.setId(resultClinicTariffMemberPrice.getId());
            clinicTariffMemberPriceBiz.updateSelectiveById(clinicTariffMemberPrice);
          } else {
            clinicTariffMemberPrice.setCrtId(userId);
            clinicTariffMemberPrice.setCrtName(name);
            clinicTariffMemberPriceBiz.insertSelective(clinicTariffMemberPrice);
          }
        });
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

    Set<MemberUniteDiscountForm> memberUniteDiscountForms = form.getMemberUniteDiscountForms();
    if (StringHelper.isEmpty(memberUniteDiscountForms)) {
      throw new ClientServiceException("统一设置门诊价目表折扣失败,当前未选择任何会员卡类型", PARAMETERS_IS_ILLEGAL);
    }
    // 检查是否有相同会员卡折扣
    checkMemberUniteDiscountForms(memberUniteDiscountForms);
    // 设置门诊价目表会员折扣价
    Integer orgId = form.getOrgId();
    tariffIds.stream()
        .<Consumer<? super MemberUniteDiscountForm>>map(
            tariffId ->
                discountForm -> {
                  ClinicTariffMemberPrice clinicTariffMemberPrice = new ClinicTariffMemberPrice();
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
                    clinicTariffMemberPriceBiz.insertSelective(clinicTariffMemberPrice);
                  } else {
                    resultClinicTariffMemberPrice.setDiscountPrice(discountPrice);
                    resultClinicTariffMemberPrice.setUpdId(
                        Integer.valueOf(BaseContextHandler.getUserID()));
                    resultClinicTariffMemberPrice.setUpdName(BaseContextHandler.getName());
                    clinicTariffMemberPriceBiz.updateSelectiveById(resultClinicTariffMemberPrice);
                  }
                })
        .forEachOrdered(memberUniteDiscountForms::forEach);
  }

  /**
   * 返回会员折扣检测结果
   *
   * @param memberUniteDiscounts 会员卡折扣列表
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
   * 根据条件导出门诊价目表
   *
   * @param response 响应
   * @param queryForm 查询条件
   */
  public void exportClinicTariffList(HttpServletResponse response, ClinicTariffQueryForm queryForm)
      throws IOException {
    List<ClinicTariffExportVO> resultList = mapper.selectClinicTariffExportList(queryForm);
    Integer orgId = queryForm.getOrgId();
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    String abbreviation = null;
    if (null != orgInfo) {
      abbreviation = orgInfo.getAbbreviation();
    }
    ExcelUtil<ClinicTariffExportVO> excelUtil = new ExcelUtil<>(ClinicTariffExportVO.class);
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
   * @param list
   */
  public int insertEntities(List<ClinicTariff> list) {
   return mapper.insertEntities(list);
  }

}
