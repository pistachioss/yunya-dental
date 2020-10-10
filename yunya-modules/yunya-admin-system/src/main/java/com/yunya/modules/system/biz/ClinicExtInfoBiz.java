package com.yunya.modules.system.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.system.ClinicExtInfo;
import com.yunya.modules.system.domain.form.MedicalOrganizationInfoForm;
import com.yunya.modules.system.mapper.ClinicExtInfoMapper;
import com.yunya.modules.system.vo.MedicalOrganizationInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.yunya.framework.common.constant.BusinessConstants.CLINIC_BUSINESS_PATTER;
import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简单介绍:</br> 医疗机构扩展信息控制层
 *
 * @author: chow
 * @date: 2020/6/6 11:04
 * @description:
 * @since: 1.0.0
 */
@Service
public class ClinicExtInfoBiz extends BaseBiz<ClinicExtInfoMapper, ClinicExtInfo> {

  /** 注入对象 */
  @Resource private OrganizationBiz organizationBiz;

  /**
   * 根据组织ID获取医疗机构详细信息
   *
   * @param companyId 组织ID
   * @return MedicalOrganizationInfoVO
   */
  public MedicalOrganizationInfoVO findMedicalOrganizationInfo(Integer companyId) {
    return mapper.selectClinicExtInfoByCompanyId(companyId);
  }

  /**
   * 编辑医疗机构
   *
   * @param companyId 组织ID
   * @param form 参数封装
   */
  @Transactional(rollbackFor = Exception.class)
  public void edit(Integer companyId, MedicalOrganizationInfoForm form) {
    ClinicExtInfo entity = new ClinicExtInfo();
    entity.setCompanyId(companyId);
    ClinicExtInfo extInfo = mapper.selectOne(entity);
    if (null == extInfo) {
      throw new ClientServiceException("修改失败，ID为'" + companyId + "'的诊所不存在！", QUERY_RESULT_INVALID);
    }
    // 校验信用代码
    String creditCode = form.getCreditCode();
    // 校验时间
    judgeBusinessTime(form.getBusinessStartTime(), form.getBusinessEndTime());
    // 更新组织表信用代码
    organizationBiz.updateCompanyCredit(companyId, creditCode);
    ClinicExtInfo build = EntityUtils.build(form, ClinicExtInfo.class);
    build.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    build.setUpdName(BaseContextHandler.getName());
    build.setUpdTime(new Date(System.currentTimeMillis()));
    build.setId(extInfo.getId());
    mapper.updateByPrimaryKeySelective(build);
  }

  /**
   * 判断门诊营业时间是否冲突
   *
   * @param startBusinessTime 开始营业时间
   * @param endBusinessTime 结束营业时间
   */
  private void judgeBusinessTime(String startBusinessTime, String endBusinessTime) {
    if (StringUtils.isNotBlank(startBusinessTime) && StringUtils.isNotBlank(endBusinessTime)) {
      LocalTime startTime =
          LocalTime.parse(startBusinessTime, DateTimeFormatter.ofPattern(CLINIC_BUSINESS_PATTER));
      LocalTime endTime =
          LocalTime.parse(endBusinessTime, DateTimeFormatter.ofPattern(CLINIC_BUSINESS_PATTER));
      if (startTime.isAfter(endTime)) {
        throw new ClientServiceException("结束时间不可以小于等于开始时间", PARAMETERS_IS_ILLEGAL);
      }
    }
  }
}
