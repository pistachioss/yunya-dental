package com.yunya.modules.tariff.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.tariff.domain.form.ClinicItemMemberPriceForm;
import com.yunya.feign.tariff.domain.form.ClinicTariffForm;
import com.yunya.feign.tariff.domain.form.ClinicTariffUniteDiscountForm;
import com.yunya.feign.tariff.domain.form.MemberUniteDiscountForm;
import com.yunya.feign.tariff.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.ClinicTariffExportVO;
import com.yunya.feign.tariff.domain.vo.ClinicTariffVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.system.MemberType;
import com.yunya.models.tariff.ClinicTariff;
import com.yunya.models.tariff.ClinicTariffMemberPrice;
import com.yunya.modules.tariff.mapper.ClinicTariffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 描述: 门诊价目表业务层
 *
 * @author GaoLuding
 * @create 2020-05-27 13:58
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicTariffBiz extends BaseBiz<ClinicTariffMapper, ClinicTariff> {

  /** 门诊价目表会员价 */
  @Autowired private ClinicTariffMemberPriceBiz clinicTariffMemberPriceBiz;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /**
   * 根据门诊价目表ID获取门诊价目表信息
   *
   * @param orgId 组织ID
   * @param clinicTariffId 门诊价目表ID
   * @return
   */
  public ClinicTariffVO findById(Integer orgId, Integer clinicTariffId) {
    ClinicTariffVO resultData = mapper.selectClinicTariffById(clinicTariffId);
    if (null != resultData) {
      List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
      if (StringHelper.isNotEmpty(memberTypes)) {
        HashMap<Integer, Object> memberPrices = new HashMap<>(16);
        setClinicTariffMemberPrice(memberPrices, memberTypes, orgId, resultData);
      }
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
        HashMap<Integer, Object> memberPrices = new HashMap<>(16);
        Integer orgId = queryForm.getOrgId();
        resultList.forEach(
            tariffVO -> setClinicTariffMemberPrice(memberPrices, memberTypes, orgId, tariffVO));
      }
    }
    return new PageInfo<>(resultList);
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
      HashMap<Integer, Object> memberPrices,
      List<MemberType> memberTypes,
      Integer orgId,
      ClinicTariffVO tariffVO) {
    ClinicTariffMemberPrice clinicTariffMemberPrice;
    ClinicTariffMemberPrice memberPriceResult;
    Integer memberTypeId;
    BigDecimal memberPrice;
    for (MemberType memberType : memberTypes) {
      clinicTariffMemberPrice = new ClinicTariffMemberPrice();
      clinicTariffMemberPrice.setClinicId(orgId);
      clinicTariffMemberPrice.setTariffId(tariffVO.getTariffId());
      clinicTariffMemberPrice.setMemberTypeId(memberType.getId());
      memberPriceResult = clinicTariffMemberPriceBiz.selectOne(clinicTariffMemberPrice);
      if (null != memberPriceResult) {
        memberTypeId = memberPriceResult.getMemberTypeId();
        memberPrice = memberPriceResult.getDiscountPrice();
      } else {
        memberTypeId = memberType.getId();
        memberPrice =
            tariffVO
                .getPrice()
                .multiply(BigDecimal.valueOf(memberType.getRate()))
                .divide(BigDecimal.valueOf(100), 2);
      }
      memberPrices.put(memberTypeId, memberPrice);
    }
    tariffVO.setMemberPrices(memberPrices);
  }

  /**
   * 修改门诊价目表
   *
   * @param id 门诊价目表ID
   * @param form 修改参数
   */
  public void modify(Integer id, ClinicTariffForm form) {
    ClinicTariff resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "'的门诊价目表不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    BigDecimal resultDataPrice = resultData.getPrice();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String name = BaseContextHandler.getName();
    if (!resultDataPrice.equals(form.getPrice())) {
      BigDecimal price = form.getPrice();
      resultData.setPrice(price);
      resultData.setUpdId(userId);
      resultData.setUpdName(name);
      resultData.setUpdTime(new Date(System.currentTimeMillis()));
    }
    mapper.updateByPrimaryKeySelective(resultData);
    List<ClinicItemMemberPriceForm> memberPrices = form.getClinicItemMemberPrices();
    if (StringHelper.isEmpty(memberPrices)) {
      throw new ClientServiceException(
          "修改失败，门诊价目表会员卡价格不能为空！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    ClinicTariffMemberPrice clinicTariffMemberPrice;
    ClinicTariffMemberPrice resultClinicTariffMemberPrice;
    Integer tariffId = form.getTariffId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer memberTypeId;
    BigDecimal discountPrice;
    for (ClinicItemMemberPriceForm memberPrice : memberPrices) {
      clinicTariffMemberPrice = new ClinicTariffMemberPrice();
      clinicTariffMemberPrice.setClinicId(orgId);
      clinicTariffMemberPrice.setTariffId(tariffId);
      memberTypeId = memberPrice.getMemberTypeId();
      clinicTariffMemberPrice.setMemberTypeId(memberTypeId);
      discountPrice = memberPrice.getDiscountPrice();
      resultClinicTariffMemberPrice = clinicTariffMemberPriceBiz.selectOne(clinicTariffMemberPrice);
      clinicTariffMemberPrice.setDiscountPrice(discountPrice);
      if (null != resultClinicTariffMemberPrice) {
        clinicTariffMemberPrice.setUpdId(userId);
        clinicTariffMemberPrice.setUpdName(name);
        clinicTariffMemberPrice.setUpdTime(new Date(System.currentTimeMillis()));
        clinicTariffMemberPrice.setId(resultClinicTariffMemberPrice.getId());
        clinicTariffMemberPriceBiz.updateSelectiveById(clinicTariffMemberPrice);
      } else {
        clinicTariffMemberPrice.setCrtId(userId);
        clinicTariffMemberPrice.setCrtName(name);
        clinicTariffMemberPriceBiz.insertSelective(clinicTariffMemberPrice);
      }
    }
  }

  /**
   * 设置门诊价目表是否启用
   *
   * @param id 价目表ID
   */
  public void switchClinicTariff(Integer id) {
    ClinicTariff resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "更新失败，ID为'" + id + "'的门诊价目表不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    resultData.setInservice(!resultData.getInservice());
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 统一设置门诊价目表会员折扣
   *
   * @param form 统一折扣设置参数
   */
  public void uniteMemberDiscount(ClinicTariffUniteDiscountForm form) {
    List<Integer> clinicTariffIds = form.getClinicTariffIds();
    if (StringHelper.isEmpty(clinicTariffIds)) {
      throw new ClientServiceException(
          "统一设置门诊价目表折扣失败，当前未选择任何门诊价目表项目！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    List<MemberUniteDiscountForm> memberUniteDiscountForms = form.getMemberUniteDiscountForms();
    if (StringHelper.isEmpty(memberUniteDiscountForms)) {
      throw new ClientServiceException(
          "统一设置门诊价目表折扣失败,当前未选择任何会员卡类型", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    Integer orgId = form.getOrgId();
    BigDecimal price;
    Integer memberType;
    for (Integer clinicTariffId : clinicTariffIds) {
      for (MemberUniteDiscountForm discountForm : memberUniteDiscountForms) {
        ClinicTariff resultData = mapper.selectByPrimaryKey(clinicTariffId);
        if (null != resultData) {
          ClinicTariffMemberPrice clinicTariffMemberPrice = new ClinicTariffMemberPrice();
          clinicTariffMemberPrice.setClinicId(orgId);
          clinicTariffMemberPrice.setTariffId(resultData.getTariffId());
          memberType = discountForm.getMemberTypeId();
          clinicTariffMemberPrice.setMemberTypeId(memberType);
          ClinicTariffMemberPrice resultClinicTariffMemberPrice =
              clinicTariffMemberPriceBiz.selectOne(clinicTariffMemberPrice);
          price = resultData.getPrice();
          BigDecimal discountPrice =
              price
                  .multiply(BigDecimal.valueOf(discountForm.getRate()))
                  .divide(BigDecimal.valueOf(100), 2);
          clinicTariffMemberPrice.setDiscountPrice(discountPrice);
          if (resultClinicTariffMemberPrice == null) {
            clinicTariffMemberPrice.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicTariffMemberPrice.setCrtName(BaseContextHandler.getName());
            clinicTariffMemberPriceBiz.insertSelective(clinicTariffMemberPrice);
          } else {
            resultClinicTariffMemberPrice.setDiscountPrice(discountPrice);
            resultClinicTariffMemberPrice.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
            resultClinicTariffMemberPrice.setUpdName(BaseContextHandler.getName());
            resultClinicTariffMemberPrice.setUpdTime(new Date(System.currentTimeMillis()));
            clinicTariffMemberPriceBiz.updateSelectiveById(resultClinicTariffMemberPrice);
          }
        }
      }
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
}
