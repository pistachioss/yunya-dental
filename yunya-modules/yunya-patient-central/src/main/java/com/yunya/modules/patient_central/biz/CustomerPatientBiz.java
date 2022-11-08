package com.yunya.modules.patient_central.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.CustomerPatientQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientExpInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientSimpleInfoVO;
import com.yunya.feign.patient_central.domain.vo.web.PatientSimpleRefererVO;
import com.yunya.feign.report.RemoteReportServiceFeign;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.models.report.CreditsShop;
import com.yunya.modules.patient_central.mapper.PatientExpInfoMapper;
import com.yunya.modules.patient_central.mapper.PatientOriginMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2022/11/7 10:46
 * @description:
 * @since: 1.0.0
 */
@Service
public class CustomerPatientBiz {

    @Autowired
    private PatientBaseInfoBiz patientBaseInfoBiz;
    @Autowired
    private PatientOriginMapper patientOriginMapper;
    @Autowired
    private PatientExpInfoMapper patientExpInfoMapper;
    @Autowired
    private RemoteReportServiceFeign remoteReportServiceFeign;


    /**
     * 根据患者id查询患者基础资料
     *
     * @param patientId
     * @return
     */
    public PatientSimpleInfoVO findPatientSimpleInfo(Integer patientId) {
        PatientBaseInfo patientBaseInfo = this.patientBaseInfoBiz.selectById(patientId);
        if (StringHelper.isNull(patientBaseInfo)) {
            return null;
        }
        PatientSimpleInfoVO result = new PatientSimpleInfoVO();
        BeanUtil.copyProperties(patientBaseInfo, result);
        result.setPatientName(patientBaseInfo.getName());
        result.setPatientId(patientId);
        Date birthday = patientBaseInfo.getBirthday();
        if (StringHelper.isNotNull(birthday)) {
            // 计算年龄
            Integer age = DateUtil.differFromDate(birthday, new Date(System.currentTimeMillis()));
            patientBaseInfo.setAge(age);
            String timeStr = new DateTime(birthday).toString("yyyy-MM-dd");
            result.setBirthday(timeStr);
        }
        int originType = 2;
        if (patientBaseInfo.getOriginType() != null && patientBaseInfo.getOriginType() > originType) {
            PatientOrigin patientOrigin =
                    patientOriginMapper.selectByPrimaryKey(patientBaseInfo.getOriginId());
            if (patientOrigin != null) {
                // 获取患者来源的父级id
                result.setSourceParentId(patientOrigin.getParentId());
                result.setSourceName(patientOrigin.getName());
            }
        }
        PatientExpInfoVo patientExpInfoVo = patientExpInfoMapper.selectByPatientId(patientId);
        if (StringHelper.isNotNull(patientExpInfoVo)) {
            result.setAddress(patientExpInfoVo.getAddress());
        }
        CreditsShop creditsShop = remoteReportServiceFeign.lastPatientCredits(patientId);
        if (StringHelper.isNotNull(creditsShop)) {
            result.setCreditsAccount(creditsShop.getCreditsAccount());
        }
        return result;
    }

    /**
     * 根据患者id查询患者推荐人列表
     *
     * @param query
     * @return
     */
    public PageInfo<PatientSimpleRefererVO> findPatientReferrerList(CustomerPatientQueryForm query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        PatientBaseInfo introducer = patientBaseInfoBiz.findPatientIntroducerByPatientId(query.getPatientId());
        List<PatientSimpleRefererVO> result = new ArrayList<>();
        if (StringHelper.isNotNull(introducer)) {
            PatientSimpleRefererVO referer = new PatientSimpleRefererVO();
            referer.setName(introducer.getName());
            referer.setGender(introducer.getGender());
            referer.setMobile(introducer.getMobile());
            result.add(referer);
        }
        return new PageInfo<>(result);
    }
}
