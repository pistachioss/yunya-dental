package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.appointment.domain.query.ClinicListQuery;
import com.yunya.feign.appointment.vo.ClinicListVO;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetForm;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceAddressSetModel;
import com.yunya.feign.employee_attend.vo.AttendanceAddressSetVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.LocationUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.employee_attend.AttendanceAddressSet;
import com.yunya.modules.employeeattend.mapper.AttendanceAddressSetMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 简介：考勤地址设置业务层
 *
 * @author: chenlin
 * @Description: 考勤地址设置业务层
 * @Date: 2020/11/5 9:29
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttendanceAddressSetBiz extends BaseBiz<AttendanceAddressSetMapper, AttendanceAddressSet> {
    /**
     * 注入对象
     */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 查询门诊列表
     */
    public List<ClinicListVO> findClinicList(ClinicListQuery query) {
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));
        List<AttendanceAddressSet> list = mapper.selectAll();
        List<ClinicListVO> reList = new ArrayList<>();

        list = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(AttendanceAddressSet::getOrganizationName))), ArrayList::new));

        ArrayList<String> channelArray = new ArrayList<>();
        channelArray.add("总院");
        channelArray.add("天目山路门诊");
        channelArray.add("云牙测试门诊");
        channelArray.add("文二西路门诊");
        channelArray.add("曙晖医疗投资管理有限公司");
        channelArray.add("杭州艾维医疗投资管理有限公司");
        channelArray.add("金华市永康市艾维口腔");

        for (AttendanceAddressSet addressSet : list) {
            if(!StringUtils.isEmpty(addressSet.getOrganizationName())){
                if(channelArray.contains(addressSet.getOrganizationName())){
                    continue;
                }
            }
            ClinicListVO clinicListVO = new ClinicListVO();
            clinicListVO.setId(addressSet.getOrgId());
            clinicListVO.setAddress(addressSet.getAttendanceAddress());
            clinicListVO.setAbbreviation(addressSet.getOrganizationName());
            clinicListVO.setBusinessEndTime(clinicMap.get(addressSet.getOrgId()+"").getBusinessEndTime());
            clinicListVO.setBusinessStartTime(clinicMap.get(addressSet.getOrgId()+"").getBusinessStartTime());
            clinicListVO.setPath(clinicMap.get(addressSet.getOrgId()+"").getClinicPath());
            clinicListVO.setLatitude(addressSet.getLatitude());
            clinicListVO.setLongitude(addressSet.getLongitude());
            double distance = 0;
            if (query.getLongitude() != null) {
                distance = LocationUtil.getDistance(query.getLongitude(), query.getLatitude(), Double.parseDouble(addressSet.getLongitude()), Double.parseDouble(addressSet.getLatitude()));
            }
            clinicListVO.setDistance(distance / 1000);
            reList.add(clinicListVO);
        }

        List<ClinicListVO> zhenList = reList.stream().sorted(Comparator.comparing(ClinicListVO::getDistance)).collect(Collectors.toList());
        return zhenList;
    }

    /**
     * 分页查询考勤地址设置列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceAddressSetVO> findAttendanceAddressSets(AttendanceAddressSetQueryForm queryForm) {
        return mapper.findAttendanceAddressSetList(queryForm);
    }

    /**
     * 分页查询考勤地址设置列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public PageInfo<AttendanceAddressSetVO> findAttendanceAddressSetList(AttendanceAddressSetQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<AttendanceAddressSetVO> attendanceAddressSetVOList = findAttendanceAddressSets(queryForm);
        List<Integer> orgIds = new ArrayList<>(10);
        attendanceAddressSetVOList.forEach(attendanceAddressSetVO -> {
            Integer orgId = attendanceAddressSetVO.getOrgId();
            if (!orgIds.contains(orgId)) {
                orgIds.add(orgId);
            }
        });

        if (!orgIds.isEmpty()) {
            List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
            organizationInfoDetails.forEach(organizationInfoDetail -> {
                Integer orgId = organizationInfoDetail.getId();
                attendanceAddressSetVOList.forEach(attendanceAddressSetVO -> {
                    Integer attendOrgId = attendanceAddressSetVO.getOrgId();
                    if (orgId.equals(attendOrgId)) {
                        attendanceAddressSetVO.setOrganizationName(organizationInfoDetail.getName());
                        return;
                    }
                });
            });
        }
        return new PageInfo<>(attendanceAddressSetVOList);
    }

    /**
     * 根据id查询考勤地址设置信息
     *
     * @param id 主键id
     * @return
     */
    public AttendanceAddressSetVO findAttendanceAddressSetById(Integer id) {
        AttendanceAddressSetVO attendanceAddressSetVO = mapper.findAttendanceAddressSetById(id);
        /*if (attendanceAddressSetVO != null) {
            Integer orgId = attendanceAddressSetVO.getOrgId();
            OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
            String organizationName = "";
            if (organizationInfo != null) {
                organizationName = organizationInfo.getName();
            }
            attendanceAddressSetVO.setOrganizationName(organizationName);
        }*/
        return attendanceAddressSetVO;
    }

    /**
     * 添加考勤地址设置信息
     *
     * @param attendanceAddressSetModel 考勤地址设置模型
     * @return
     */
    public void add(AttendanceAddressSetModel attendanceAddressSetModel) {
        Integer orgId = attendanceAddressSetModel.getOrgId();
        String address = attendanceAddressSetModel.getAttendanceAddress();
        AttendanceAddressSet attendanceAddressSet = new AttendanceAddressSet();
        attendanceAddressSet.setOrgId(orgId);
        attendanceAddressSet.setAttendanceAddress(address);
        int count = mapper.selectCount(attendanceAddressSet);
        if (count > 0) {
            throw new ClientServiceException("添加考勤地址失败，考勤地址冲突", OperationCodeConstants.SAME_DATA_EXIST);
        }
        Date now = new Date(System.currentTimeMillis());
        Integer userid = Integer.valueOf(BaseContextHandler.getUserID());
        attendanceAddressSet.setLatitude(attendanceAddressSetModel.getLatitude());
        attendanceAddressSet.setLongitude(attendanceAddressSetModel.getLongitude());
        attendanceAddressSet.setAttendanceRange(attendanceAddressSetModel.getAttendanceRange());
        attendanceAddressSet.setCrtId(userid);
        attendanceAddressSet.setCrtTime(now);
        attendanceAddressSet.setUpdId(userid);
        attendanceAddressSet.setUpdTime(now);
        attendanceAddressSet.setOrganizationName(attendanceAddressSetModel.getOrganizationName());
        mapper.insertSelective(attendanceAddressSet);
    }

    /**
     * 修改考勤地址设置信息
     *
     * @param id                       主键id
     * @param attendanceAddressSetForm 考勤地址设置模型
     * @return
     */
    public void update(Integer id, AttendanceAddressSetForm attendanceAddressSetForm) {
        AttendanceAddressSet attendanceAddressSet = mapper.selectByPrimaryKey(id);
        if (null == attendanceAddressSet) {
            throw new ClientServiceException(
                    "修改考勤地址失败，数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
        }
        Integer orgId = attendanceAddressSetForm.getOrgId();
        String address = attendanceAddressSetForm.getAttendanceAddress();
        // 名称有修改，校验名称是否重复
        if (!attendanceAddressSet.getId().equals(id) && attendanceAddressSet.getAttendanceAddress().equals(address)
                && attendanceAddressSet.getOrgId().equals(orgId)) {
            throw new ClientServiceException(
                    "修改考勤地址失败，考勤地址冲突", OperationCodeConstants.SAME_DATA_EXIST);
        }
        attendanceAddressSet.setOrgId(orgId);
        attendanceAddressSet.setAttendanceAddress(address);
        Date now = new Date(System.currentTimeMillis());
        Integer userid = Integer.valueOf(BaseContextHandler.getUserID());
        attendanceAddressSet.setLatitude(attendanceAddressSetForm.getLatitude());
        attendanceAddressSet.setLongitude(attendanceAddressSetForm.getLongitude());
        attendanceAddressSet.setAttendanceRange(attendanceAddressSetForm.getAttendanceRange());
        attendanceAddressSet.setUpdId(userid);
        attendanceAddressSet.setUpdTime(now);
        attendanceAddressSet.setOrganizationName(attendanceAddressSetForm.getOrganizationName());
        mapper.updateByPrimaryKey(attendanceAddressSet);
    }

    /**
     * 根据ID删除考勤地址设置
     *
     * @param id 主键id
     */
    public void delete(Integer id) {
        mapper.deleteByPrimaryKey(id);
    }

    /**
     * 分页查询所有机构以及关联的考勤地址列表
     *
     * @param model 查询参数
     * @return
     */
    public PageInfo<AttendanceAddressSetVO> findOrganizationAttendanceAddressSetList(OrganizationModel model) {
        if (model.getWhetherPage()) {
            PageHelper.startPage(model.getPageNum(), model.getPageSize());
        }
        List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoList(model);
        Map<Integer, String> organizationMap = new HashMap<>();
        if (organizationInfoDetails != null && !organizationInfoDetails.isEmpty()) {
            organizationInfoDetails.forEach(organizationInfoDetail -> {
                Integer orgId = organizationInfoDetail.getId();
                String orgName = organizationInfoDetail.getName();
                organizationMap.put(orgId, orgName);
            });
            AttendanceAddressSetQueryForm queryForm = new AttendanceAddressSetQueryForm();
            queryForm.setWhetherPage(false);
            queryForm.setOrgIds(organizationMap.keySet());
            List<AttendanceAddressSetVO> attendanceAddressSetVOList = mapper.findAttendanceAddressSetList(queryForm);
            List<AttendanceAddressSetVO> attendanceAddressSetVOS = attendanceAddressSetVOList.stream().filter(attendanceAddressSetVO -> {
                Integer orgId = attendanceAddressSetVO.getOrgId();
                String orgName = organizationMap.remove(orgId);
                if (StringHelper.isNotEmpty(orgName)) {
                    attendanceAddressSetVO.setOrganizationName(orgName);
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            if (!organizationMap.isEmpty()) {
                organizationMap.entrySet().forEach(entry -> {
                    AttendanceAddressSetVO attendanceAddressSetVO = new AttendanceAddressSetVO();
                    attendanceAddressSetVO.setOrgId(entry.getKey());
                    attendanceAddressSetVO.setOrganizationName(entry.getValue());
                    attendanceAddressSetVOS.add(attendanceAddressSetVO);
                });
            }
            return new PageInfo<>(attendanceAddressSetVOS);
        }
        return new PageInfo<>(null);
    }

    /**
     * 根据orgId查询考勤地址列表
     *
     * @param orgIds
     * @return
     */
    public List<AttendanceAddressSetVO> findAttendanceAddressByOrgId(List<Integer> orgIds) {
        return mapper.selectAttendanceAddressByOrgId(orgIds);
    }
}
