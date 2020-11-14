package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendanceWifiSetForm;
import com.yunya.feign.employee_attend.form.AttendanceWifiSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceWifiSetModel;
import com.yunya.feign.employee_attend.vo.AttendanceWifiSetVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.AttendanceWifiSet;
import com.yunya.modules.employeeattend.mapper.AttendanceWifiSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简介：考勤Wifi设置业务层
 *
 * @author: chenlin
 * @Description: 考勤Wifi设置业务层
 * @Date: 2020/11/5 12:55
 * @since: 1.0.0
 */
@Service
@Transactional
public class AttendanceWifiSetBiz extends BaseBiz<AttendanceWifiSetMapper, AttendanceWifiSet> {
    /** 注入对象 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 分页查询考勤Wifi设置列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceWifiSetVO> findAttendanceWifiSets(AttendanceWifiSetQueryForm queryForm) {
        return mapper.findAttendanceWifiSetList(queryForm);
    }

    /**
     * 分页查询考勤Wifi设置列表
     *
     * @param queryForm 查询参数
     * @return 
     */
    public PageInfo<AttendanceWifiSetVO> findAttendanceWifiSetList(AttendanceWifiSetQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<AttendanceWifiSetVO> attendanceWifiSetVOList = findAttendanceWifiSets(queryForm);
        List<Integer> orgIds = new ArrayList<>(10);
        attendanceWifiSetVOList.forEach(attendanceAddressSetVO->{
            Integer orgId = attendanceAddressSetVO.getOrgId();
            if (!orgIds.contains(orgId)) {
                orgIds.add(orgId);
            }
        });

        if (!orgIds.isEmpty()) {
            List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
            organizationInfoDetails.forEach(organizationInfoDetail->{
                Integer orgId = organizationInfoDetail.getId();
                attendanceWifiSetVOList.forEach(attendanceAddressSetVO -> {
                    Integer attendOrgId = attendanceAddressSetVO.getOrgId();
                    String orgName = "";
                    if (orgId.equals(attendOrgId)) {
                        orgName = organizationInfoDetail.getName();
                    }
                    attendanceAddressSetVO.setOrganizationName(orgName);
                });
            });
        }
        return new PageInfo<>(attendanceWifiSetVOList);
    }

    /**
     * 根据id查询考勤Wifi设置信息
     *
     * @param id 主键id
     * @return ResponseResult<AttendanceWifiSetVO>
     */
    public AttendanceWifiSetVO findAttendanceWifiSetById(Integer id) {
        AttendanceWifiSetVO attendanceWifiSetVO = mapper.findAttendanceWifiSetById(id);
        if (attendanceWifiSetVO != null) {
            Integer orgId = attendanceWifiSetVO.getOrgId();
            OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
            String organizationName = "";
            if (organizationInfo != null) {
                organizationName = organizationInfo.getName();
            }
            attendanceWifiSetVO.setOrganizationName(organizationName);
        }
        return attendanceWifiSetVO;
    }

    /**
     * 添加考勤Wifi设置信息
     *
     * @param attendanceWifiSetModel 考勤地址设置模型
     * @return
     */
    public void add(AttendanceWifiSetModel attendanceWifiSetModel) {
        Integer orgId = attendanceWifiSetModel.getOrgId();
        String macAddress = attendanceWifiSetModel.getMacAddress();
        AttendanceWifiSet attendanceWifiSet = new AttendanceWifiSet();
        attendanceWifiSet.setOrgId(orgId);
        attendanceWifiSet.setMacAddress(macAddress);
        int count = mapper.selectCount(attendanceWifiSet);
        if (count > 0) {
            throw new ClientServiceException("添加考勤Wifi失败，考勤Wifi冲突", OperationCodeConstants.SAME_DATA_EXIST);
        }
        Date now = new Date(System.currentTimeMillis());
        Integer userid = Integer.valueOf(BaseContextHandler.getUserID());
        attendanceWifiSet.setWifiName(attendanceWifiSetModel.getWifiName());
        attendanceWifiSet.setCrtId(userid);
        attendanceWifiSet.setCrtTime(now);
        attendanceWifiSet.setUpdId(userid);
        attendanceWifiSet.setUpdTime(now);
        mapper.insertSelective(attendanceWifiSet);
    }

    /**
     * 修改考勤地址设置信息
     *
     * @param id 主键id
     * @param attendanceWifiSetForm 考勤地址设置模型
     * @return
     */
    public void update(Integer id, AttendanceWifiSetForm attendanceWifiSetForm) {
        AttendanceWifiSet attendanceWifiSet = mapper.selectByPrimaryKey(id);
        if (null == attendanceWifiSet) {
            throw new ClientServiceException(
                    "修改考勤Wifi失败，数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
        }
        Integer orgId = attendanceWifiSetForm.getOrgId();
        String macAddress = attendanceWifiSetForm.getMacAddress();
        // 名称有修改，校验名称是否重复
        if (!attendanceWifiSet.getId().equals(id) && attendanceWifiSet.getMacAddress().equals(macAddress)
                && attendanceWifiSet.getOrgId().equals(orgId)) {
            throw new ClientServiceException(
                    "修改考勤Wifi失败，考勤Wifi冲突", OperationCodeConstants.SAME_DATA_EXIST);
        }
        attendanceWifiSet.setOrgId(orgId);
        attendanceWifiSet.setMacAddress(macAddress);
        Date now = new Date(System.currentTimeMillis());
        Integer userid = Integer.valueOf(BaseContextHandler.getUserID());
        attendanceWifiSet.setWifiName(attendanceWifiSetForm.getWifiName());
        attendanceWifiSet.setUpdId(userid);
        attendanceWifiSet.setUpdTime(now);
        mapper.updateByPrimaryKey(attendanceWifiSet);
    }

    /**
     * 根据ID删除考勤地址设置
     *
     * @param id 主键id
     */
    public void delete(Integer id) {
        mapper.deleteByPrimaryKey(id);
    }
}
