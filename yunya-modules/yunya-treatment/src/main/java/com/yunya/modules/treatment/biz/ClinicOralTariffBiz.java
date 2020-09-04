package com.yunya.modules.treatment.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.tariff.domain.form.ClinicItemMemberPriceForm;
import com.yunya.feign.tariff.domain.form.ClinicOralTariffForm;
import com.yunya.feign.tariff.domain.form.ClinicOralTariffUniteDiscountForm;
import com.yunya.feign.tariff.domain.form.MemberUniteDiscountForm;
import com.yunya.feign.tariff.domain.query.ClinicOralTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.ClinicOralTariffExportVO;
import com.yunya.feign.tariff.domain.vo.ClinicOralTariffVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.system.MemberType;
import com.yunya.models.tariff.ClinicOralTariff;
import com.yunya.models.tariff.ClinicOralTariffMemberPrice;
import com.yunya.modules.treatment.mapper.ClinicOralTariffMapper;
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
 * 描述: 门诊商品项目管理业务层
 *
 * @author GaoLuding
 * @create 2020-05-27 13:58
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicOralTariffBiz extends BaseBiz<ClinicOralTariffMapper, ClinicOralTariff> {

  /** 门诊商品项目会员价 */
  @Autowired private ClinicOralTariffMemberPriceBiz clinicOralTariffMemberPriceBiz;
  /** 系统服务调用 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /**
   * 根据门诊商品项目ID获取门诊商品项目信息
   *
   * @param orgId 组织ID
   * @param clinicOralTariffId 门诊商品项目ID
   * @return
   */
  public ClinicOralTariffVO findById(Integer orgId, Integer clinicOralTariffId) {
    ClinicOralTariffVO resultData = mapper.selectClinicOralTariffById(clinicOralTariffId);
    if (null != resultData) {
      List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
      if (StringHelper.isNotEmpty(memberTypes)) {
        HashMap<Integer, Object> memberPrices = new HashMap<>(16);
        setClinicOralTariffMemberPrice(memberPrices, memberTypes, orgId, resultData);
      }
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
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<ClinicOralTariffVO> resultList = mapper.selectClinicOralTariffList(queryForm);
    if (StringHelper.isNotEmpty(resultList)) {
      List<MemberType> memberTypes = systemServiceFeign.findMemberTypeList(new MemberType());
      if (StringHelper.isNotEmpty(memberTypes)) {
        Integer orgId = queryForm.getOrgId();
        resultList.forEach(
            tariffVO -> {
              HashMap<Integer, Object> memberPrices = new HashMap<>(16);
              setClinicOralTariffMemberPrice(memberPrices, memberTypes, orgId, tariffVO);
            });
      }
    }
    return new PageInfo<>(resultList);
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
      HashMap<Integer, Object> memberPrices,
      List<MemberType> memberTypes,
      Integer orgId,
      ClinicOralTariffVO tariffVO) {
    for (MemberType memberType : memberTypes) {
      Integer memberTypeId;
      BigDecimal memberPrice;
      ClinicOralTariffMemberPrice  clinicOralTariffMemberPrice = new ClinicOralTariffMemberPrice();
      clinicOralTariffMemberPrice.setClinicId(orgId);
      clinicOralTariffMemberPrice.setOralTariffId(tariffVO.getOralTariffId());
      clinicOralTariffMemberPrice.setMemberTypeId(memberType.getId());
      ClinicOralTariffMemberPrice memberPriceResult = clinicOralTariffMemberPriceBiz.selectOne(clinicOralTariffMemberPrice);
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
   * 修改门诊商品项目
   *
   * @param id 门诊商品项目ID
   * @param form 修改参数
   */
  public void modify(Integer id, ClinicOralTariffForm form) {
    ClinicOralTariff resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "'的门诊商品项目不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
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
          "修改失败，门诊商品项目会员卡价格不能为空！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    ClinicOralTariffMemberPrice clinicOralTariffMemberPrice;
    ClinicOralTariffMemberPrice resultClinicOralTariffMemberPrice;
    Integer oralTariffId = form.getOralTariffId();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    Integer memberTypeId;
    BigDecimal discountPrice;
    for (ClinicItemMemberPriceForm memberPrice : memberPrices) {
      clinicOralTariffMemberPrice = new ClinicOralTariffMemberPrice();
      clinicOralTariffMemberPrice.setClinicId(orgId);
      clinicOralTariffMemberPrice.setOralTariffId(oralTariffId);
      memberTypeId = memberPrice.getMemberTypeId();
      clinicOralTariffMemberPrice.setMemberTypeId(memberTypeId);
      discountPrice = memberPrice.getDiscountPrice();
      resultClinicOralTariffMemberPrice =
          clinicOralTariffMemberPriceBiz.selectOne(clinicOralTariffMemberPrice);
      clinicOralTariffMemberPrice.setDiscountPrice(discountPrice);
      if (null != resultClinicOralTariffMemberPrice) {
        clinicOralTariffMemberPrice.setUpdId(userId);
        clinicOralTariffMemberPrice.setUpdName(name);
        clinicOralTariffMemberPrice.setUpdTime(new Date(System.currentTimeMillis()));
        clinicOralTariffMemberPrice.setId(resultClinicOralTariffMemberPrice.getId());
        clinicOralTariffMemberPriceBiz.updateSelectiveById(clinicOralTariffMemberPrice);
      } else {
        clinicOralTariffMemberPrice.setCrtId(userId);
        clinicOralTariffMemberPrice.setCrtName(name);
        clinicOralTariffMemberPriceBiz.insertSelective(clinicOralTariffMemberPrice);
      }
    }
  }

  /**
   * 设置门诊商品项目是否启用
   *
   * @param id 商品项目ID
   */
  public void switchClinicOralTariff(Integer id) {
    ClinicOralTariff resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "更新失败，ID为'" + id + "'的门诊商品项目不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    resultData.setInservice(!resultData.getInservice());
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 统一设置门诊商品项目会员折扣
   *
   * @param form 统一折扣设置参数
   */
  public void uniteMemberDiscount(ClinicOralTariffUniteDiscountForm form) {
    List<Integer> clinicOralTariffIds = form.getClinicOralTariffIds();
    if (StringHelper.isEmpty(clinicOralTariffIds)) {
      throw new ClientServiceException(
          "统一设置门诊商品项目折扣失败，当前未选择任何门诊商品项目项目！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    List<MemberUniteDiscountForm> memberUniteDiscountForms = form.getMemberUniteDiscountForms();
    if (StringHelper.isEmpty(memberUniteDiscountForms)) {
      throw new ClientServiceException(
          "统一设置门诊商品项目折扣失败,当前未选择任何会员卡类型", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    Integer orgId = form.getOrgId();
    BigDecimal price;
    Integer memberType;
    for (Integer clinicOralTariffId : clinicOralTariffIds) {
      for (MemberUniteDiscountForm discountForm : memberUniteDiscountForms) {
        ClinicOralTariff resultData = mapper.selectByPrimaryKey(clinicOralTariffId);
        if (null != resultData) {
          ClinicOralTariffMemberPrice clinicOralTariffMemberPrice =
              new ClinicOralTariffMemberPrice();
          clinicOralTariffMemberPrice.setClinicId(orgId);
          clinicOralTariffMemberPrice.setOralTariffId(resultData.getOralTariffId());
          memberType = discountForm.getMemberTypeId();
          clinicOralTariffMemberPrice.setMemberTypeId(memberType);
          ClinicOralTariffMemberPrice resultClinicOralTariffMemberPrice =
              clinicOralTariffMemberPriceBiz.selectOne(clinicOralTariffMemberPrice);
          price = resultData.getPrice();
          BigDecimal discountPrice =
              price
                  .multiply(BigDecimal.valueOf(discountForm.getRate()))
                  .divide(BigDecimal.valueOf(100), 2);
          clinicOralTariffMemberPrice.setDiscountPrice(discountPrice);
          if (resultClinicOralTariffMemberPrice == null) {
            clinicOralTariffMemberPrice.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
            clinicOralTariffMemberPrice.setCrtName(BaseContextHandler.getName());
            clinicOralTariffMemberPriceBiz.insertSelective(clinicOralTariffMemberPrice);
          } else {
            resultClinicOralTariffMemberPrice.setDiscountPrice(discountPrice);
            resultClinicOralTariffMemberPrice.setUpdId(
                Integer.valueOf(BaseContextHandler.getUserID()));
            resultClinicOralTariffMemberPrice.setUpdName(BaseContextHandler.getName());
            resultClinicOralTariffMemberPrice.setUpdTime(new Date(System.currentTimeMillis()));
            clinicOralTariffMemberPriceBiz.updateSelectiveById(resultClinicOralTariffMemberPrice);
          }
        }
      }
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
    List<ClinicOralTariffExportVO> resultList = mapper.selectClinicOralTariffExportList(queryForm);
    Integer orgId = queryForm.getOrgId();
    OrganizationInfo orgInfo = systemServiceFeign.findOrgInfoByOrgId(orgId);
    String abbreviation = null;
    if (null != orgInfo) {
      abbreviation = orgInfo.getAbbreviation();
    }
    ExcelUtil<ClinicOralTariffExportVO> excelUtil = new ExcelUtil<>(ClinicOralTariffExportVO.class);
    excelUtil.exportExcel(response, resultList, abbreviation + "_商品项目列表");
  }
}
