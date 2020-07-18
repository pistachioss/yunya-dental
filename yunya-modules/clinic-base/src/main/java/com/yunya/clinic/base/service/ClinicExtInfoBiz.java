package com.yunya.clinic.base.service;

import com.yunya.clinic.base.constant.ClinicBaseConstant;
import com.yunya.clinic.base.model.request.MedicalClinicInfoReq;
import com.yunya.clinic.base.mapper.ClinicExtInfoMapper;
import com.yunya.clinic.base.model.response.MedicalClinicExtInfoRes;
import com.yunya.feign.system.feign.OrganizationFeign;
import com.yunya.feign.system.form.CompanyEditForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.system.ClinicExtInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

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

    @Resource
    private OrganizationFeign organizationFeign;

    /**
     * 根据组织ID获取医疗机构详细信息
     *
     * @param companyId 组织ID
     * @return MedicalOrganizationInfoVO
     */
    public MedicalClinicExtInfoRes findMedicalOrganizationInfo(Integer companyId) {
        return mapper.selectClinicExtInfoByCompanyId(companyId);
    }

    /**
     * 编辑医疗机构
     *
     * @param companyId 组织ID
     * @param form 参数封装
     */
    @Transactional(rollbackFor = Exception.class)
    public void edit(Integer companyId, MedicalClinicInfoReq form) {
        //校验信用代码
        String creditCode = form.getCreditCode();
        Matcher matcher = ClinicBaseConstant.CREDIT_PATTERN.matcher(creditCode);
        if (!matcher.matches()) {
            throw new BaseException("统一信用代码格式出错");
        }
        String abbreviation = form.getAbbreviation();
        //校验时间
        judgeBusinessTime(form.getBusinessStartTime(), form.getBusinessEndTime());
        //调用组织接口更新 门诊主表信用代码
        CompanyEditForm editForm = CompanyEditForm.builder()
                .companyId(companyId).creditCode(creditCode).build();
        organizationFeign.updateCompanyCredit(editForm);
        Integer count = mapper.countByAbbreviation(abbreviation, companyId);
        if (count > 0) {
            throw new BaseException(
                    "修改医疗机构简称'" + abbreviation + "'失败，该简称名称已存在", ClinicBaseConstant.NAME_IS_OCCUPIED);
        }
        ClinicExtInfo entity = new ClinicExtInfo();
        entity.setCompanyId(companyId);
        ClinicExtInfo extInfo = mapper.selectOne(entity);
        ClinicExtInfo build = EntityUtils.build(form, ClinicExtInfo.class);
        build.setId(extInfo.getId());
        mapper.updateByPrimaryKeySelective(build);
    }


    private void judgeBusinessTime(String startBusinessTime, String endBusinessTime) {
        if (StringUtils.isNotBlank(startBusinessTime) && StringUtils.isNotBlank(endBusinessTime)) {
            LocalTime startTime = LocalTime.parse(startBusinessTime, DateTimeFormatter
                                            .ofPattern(ClinicBaseConstant.CLINIC_BUSINESS_PATTER));
            LocalTime endTime = LocalTime.parse(endBusinessTime, DateTimeFormatter
                                            .ofPattern(ClinicBaseConstant.CLINIC_BUSINESS_PATTER));
            if (startTime.isAfter(endTime)) {
                throw new BaseException("结束时间不可以小于等于开始时间");
            }
        }
    }

}
