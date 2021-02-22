package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.MemberOverviewQueryForm;
import com.yunya.feign.report.domain.vo.BaseMemberOverviewVo;
import com.yunya.feign.report.domain.vo.BasePatientMemberOverviewVo;
import com.yunya.feign.report.domain.vo.ExcelBasePatientPrepaymentOverviewVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BasePatientMember;
import com.yunya.report.ultimate.mapper.BasePatientMemberMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简介: 会员卡概况业务层
 *
 * @author: WY
 * @date: 2020/10/27 10:10
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOverviewBiz extends BaseBiz<BasePatientMemberMapper, BasePatientMember> {


  /**
   * 会员卡概况查询
   *
   * @param form 概况查询form
   * @return 会员卡概况信息
   */
  public PageInfo<BasePatientMemberOverviewVo> patientOverviewList(MemberOverviewQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BasePatientMemberOverviewVo> basePatientMemberOverviewVoList =
        mapper.selectMemberOverviewList(form);
    return new PageInfo<>(basePatientMemberOverviewVoList);
  }

  /**
   * 导出患者会员卡/预付款概况记录列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportPatientOverviewList(HttpServletResponse response, MemberOverviewQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BasePatientMemberOverviewVo> resultList = mapper.selectMemberOverviewList(form);
    if (form.getType() == 0) {
      ExcelUtil<BasePatientMemberOverviewVo> excelUtil =
          new ExcelUtil<>(BasePatientMemberOverviewVo.class);
      excelUtil.exportExcel(response, resultList, "会员卡账户统计", "会员卡账户统计");

    } else {
      ExcelUtil<ExcelBasePatientPrepaymentOverviewVo> excelUtil =
          new ExcelUtil<>(ExcelBasePatientPrepaymentOverviewVo.class);
      List<ExcelBasePatientPrepaymentOverviewVo> build =
          EntityUtils.build(resultList, ExcelBasePatientPrepaymentOverviewVo.class);
      excelUtil.exportExcel(response, build, "预付款账户统计", "预付款账户统计");
    }
  }

  /**
   * 会员卡概况
   *
   * @return 会员卡概况信息
   */
  public Map<String, Object> memberList() {
    Map<String, Object> map = new HashMap<String, Object>(16);
    int sumAmount = 0;
    BigDecimal sumPrincipalAmount = new BigDecimal(0);
    BigDecimal sumBonusAmount = new BigDecimal(0);
    List<BaseMemberOverviewVo> baseMemberOverviewVos = mapper.memberOverviewList();
    for (BaseMemberOverviewVo baseMemberOverviewVo : baseMemberOverviewVos) {
      if (baseMemberOverviewVo.getAmount() > 0) {
        sumAmount = sumAmount + baseMemberOverviewVo.getAmount();
      }
      if (baseMemberOverviewVo.getPrincipalAmount().compareTo(new BigDecimal(0)) > 0) {
        sumPrincipalAmount = sumPrincipalAmount.add(baseMemberOverviewVo.getPrincipalAmount());
      }
      if (baseMemberOverviewVo.getBonusAmount().compareTo(new BigDecimal(0)) > 0) {
        sumBonusAmount = sumBonusAmount.add(baseMemberOverviewVo.getBonusAmount());
      }
    }
    map.put("baseMemberOverviewVoList", baseMemberOverviewVos);
    map.put("sumAmount", sumAmount);
    map.put("sumPrincipalAmount", sumPrincipalAmount);
    map.put("sumBonusAmount", sumBonusAmount);
    return map;
  }
}
