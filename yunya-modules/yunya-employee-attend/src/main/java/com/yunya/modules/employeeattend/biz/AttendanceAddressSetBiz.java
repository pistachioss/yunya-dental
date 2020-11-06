package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetForm;
import com.yunya.feign.employee_attend.form.AttendanceAddressSetQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceAddressSetModel;
import com.yunya.feign.employee_attend.vo.AttendanceAddressSetVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.AttendanceAddressSet;
import com.yunya.modules.employeeattend.mapper.AttendanceAddressSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

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
    /** 注入对象 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

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
        List<AttendanceAddressSetVO> attendanceAddressSetVOList = mapper.findAttendanceAddressSetList(queryForm);
        List<Integer> orgIds = new ArrayList<>(10);
        attendanceAddressSetVOList.forEach(attendanceAddressSetVO->{
            Integer orgId = attendanceAddressSetVO.getOrgId();
            if (!orgIds.contains(orgId)) {
                orgIds.add(orgId);
            }
        });

        if (!orgIds.isEmpty()) {
            List<OrganizationInfoDetail> organizationInfoDetails = remoteSystemServiceFeign.findOrgInfoInIds(orgIds);
            organizationInfoDetails.forEach(organizationInfoDetail->{
                Integer orgId = organizationInfoDetail.getId();
                attendanceAddressSetVOList.forEach(attendanceAddressSetVO -> {
                    Integer attendOrgId = attendanceAddressSetVO.getOrgId();
                    String orgName = "";
                    if (orgId.equals(attendOrgId)) {
                        orgName = organizationInfoDetail.getName();
                    }
                    attendanceAddressSetVO.setOrganizationName(orgName);
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
        if (attendanceAddressSetVO != null) {
            Integer orgId = attendanceAddressSetVO.getOrgId();
            OrganizationInfo organizationInfo = remoteSystemServiceFeign.findOrgInfoByOrgId(orgId);
            String organizationName = "";
            if (organizationInfo != null) {
                organizationName = organizationInfo.getName();
            }
            attendanceAddressSetVO.setOrganizationName(organizationName);
        }
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
        mapper.insertSelective(attendanceAddressSet);
    }

    /**
     * 修改考勤地址设置信息
     *
     * @param id 主键id
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
}
