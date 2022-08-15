package com.yunya365.mini.service.impl;

import com.yunya.feign.appointment.domain.query.ClinicListQuery;
import com.yunya.feign.employee_attend.EmployeeAttendServiceFeign;
import com.yunya.feign.ivy_mini.domain.form.SelfMentionClinicForm;
import com.yunya.feign.ivy_mini.domain.vo.SelfMentionClinicVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.LocationUtil;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.employee_attend.AttendanceAddressSet;
import com.yunya365.mini.entity.SelfMentionClinic;
import com.yunya365.mini.mapper.SelfMentionClinicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    public List<SelfMentionClinicVO> findAppList( ClinicListQuery query){
        List<SelfMentionClinicVO>reList = new ArrayList<>();
        List<AttendanceAddressSet>list = employeeAttendServiceFeign.findAddress();
        List<SelfMentionClinic>sslit = mapper.selectAll();

        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        //全部门诊信息
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> cMap = new HashMap(16);
        clinics.forEach(z -> cMap.put(z.getId() + "", z));

        Map<String, AttendanceAddressSet> clinicMap = new HashMap(16);
        list.forEach(z -> clinicMap.put(z.getOrgId() + "", z));

        for (SelfMentionClinic s:sslit){
            SelfMentionClinicVO sc = new SelfMentionClinicVO();
            BeanUtil.copy(s,sc);
            sc.setPath(cMap.get(s.getClinicId()+"").getPath());
            sc.setClinicAddress(clinicMap.get(s.getClinicId()+"").getAttendanceAddress());
            sc.setClinicName(cMap.get(s.getClinicId()+"").getAbbreviation());
            sc.setLatitude(clinicMap.get(s.getClinicId()+"").getLatitude());
            sc.setLongitude(clinicMap.get(s.getClinicId()+"").getLongitude());
            double distance = 0;
            if (query.getLongitude() != null) {
                distance = LocationUtil.getDistance(query.getLongitude(), query.getLatitude(), Double.parseDouble(sc.getLongitude()), Double.parseDouble(sc.getLatitude()));
            }
            sc.setDistance(distance / 1000);
            reList.add(sc);
        }
        reList =
                reList.stream().sorted(Comparator.comparing(SelfMentionClinicVO::getDistance)).collect(Collectors.toList());
        return reList;
    }

    public List<SelfMentionClinicVO> findList(){
        List<SelfMentionClinicVO>reList = new ArrayList<>();
        List<AttendanceAddressSet>list = employeeAttendServiceFeign.findAddress();
        List<SelfMentionClinic>sslit = mapper.selectAll();

        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        //全部门诊信息
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> cMap = new HashMap(16);
        clinics.forEach(z -> cMap.put(z.getId() + "", z));

        Map<String, AttendanceAddressSet> clinicMap = new HashMap(16);
        list.forEach(z -> clinicMap.put(z.getOrgId() + "", z));
        for (SelfMentionClinic s:sslit){
            SelfMentionClinicVO sc = new SelfMentionClinicVO();
            BeanUtil.copy(s,sc);
            sc.setClinicAddress(clinicMap.get(s.getClinicId()+"").getAttendanceAddress());
            sc.setClinicName(cMap.get(s.getClinicId()+"").getAbbreviation());
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
