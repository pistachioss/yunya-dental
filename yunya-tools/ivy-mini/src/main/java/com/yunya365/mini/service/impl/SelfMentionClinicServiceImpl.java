package com.yunya365.mini.service.impl;

import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.ivy_mini.domain.form.SelfMentionClinicForm;
import com.yunya.feign.ivy_mini.domain.vo.SelfMentionClinicVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.AttendanceAddressSet;
import com.yunya365.mini.entity.SelfMentionClinic;
import com.yunya365.mini.mapper.SelfMentionClinicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SelfMentionClinicServiceImpl extends BaseBiz<SelfMentionClinicMapper, SelfMentionClinic> {
    @Resource
    private EmployeeAttendServiceFeign employeeAttendServiceFeign;

    public List<SelfMentionClinicVO> findList(){
        List<SelfMentionClinicVO>reList = new ArrayList<>();
        List<AttendanceAddressSet>list = employeeAttendServiceFeign.findAddress();
        List<SelfMentionClinic>sslit = mapper.selectAll();
        Map<String, AttendanceAddressSet> clinicMap = new HashMap(16);
        list.forEach(z -> clinicMap.put(z.getOrgId() + "", z));
        for (SelfMentionClinic s:sslit){
            SelfMentionClinicVO sc = new SelfMentionClinicVO();
            BeanUtil.copy(s,sc);
            sc.setClinicAddress(clinicMap.get(s.getClinicId()+"").getAttendanceAddress());
            sc.setClinicName(clinicMap.get(s.getClinicId()+"").getOrganizationName());
            sc.setLatitude(clinicMap.get(s.getClinicId()+"").getLatitude());
            sc.setLongitude(clinicMap.get(s.getClinicId()+"").getLongitude());
            reList.add(sc);
        }
    return reList;
    }

    public void add(SelfMentionClinicForm form) {
        SelfMentionClinic entity = new SelfMentionClinic();
        BeanUtils.copyProperties(form, entity);
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
    }

    public ResponseResult update(SelfMentionClinicForm form) {
        Integer id = form.getId();
        SelfMentionClinic article = mapper.selectByPrimaryKey(id);
        if (article == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }

        BeanUtils.copyProperties(form, article);

        article.setUpdTime(new Date(System.currentTimeMillis()));

        int result = mapper.updateByPrimaryKey(article);
        if (result <= 0){
            return ResponseUtil.success("数据修改失败！");
        }
        return ResponseUtil.success();
    }
}
