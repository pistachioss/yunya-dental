package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.LeaveInfoQueryForm;
import com.yunya.feign.employee_attend.vo.LeaveInfoVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.*;
import com.yunya.modules.employeeattend.form.LeaveInfoByEmForm;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
import com.yunya.modules.employeeattend.mapper.ApprovalInfoMapper;
import com.yunya.modules.employeeattend.mapper.CopyInfoMapper;
import com.yunya.modules.employeeattend.mapper.LeaveInfoMapper;
import com.yunya.feign.employee_attend.vo.EmLeaveVO;
import com.yunya.modules.employeeattend.mapper.LeaveScheduleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.INSERT_MODEL;

/**
 * 简介：请假信息业务层
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/10 20:50
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveInfoBiz extends BaseBiz<LeaveInfoMapper, LeaveInfo> {

    @Autowired
    private ApprovalInfoMapper approvalInfoMapper;
    @Autowired
    private CopyInfoMapper copyInfoMapper;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private LeaveScheduleMapper leaveScheduleMapper;

    /**
     * 根据日期和用户id列表查询请假列表
     *
     * @param userIds 用户id列表
     * @param date    日期
     * @return
     */
    public List<LeaveInfoVO> findLeaveInfosByUserIdsAndDate(List<Integer> userIds, Date date) {
        return mapper.findLeaveInfosByUserIdsAndDate(userIds, date);
    }

    /**
     * 根据条件分页查询请假列表
     *
     * @param queryForm
     * @return
     */
    public List<LeaveInfoVO> findLeaveInfoList(LeaveInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.findLeaveInfoList(queryForm);
    }

    /**
     * 新增按天请假
     *
     * @param leaveInfoForm
     * @return
     */
    public Integer createDay(LeaveInfoForm leaveInfoForm) {
        //判断是否有其他类型的申请
        if (true) {
            //判断是否与同类型其他申请时间冲突
            LeaveInfo find = new LeaveInfo();
            find.setUserId(leaveInfoForm.getUserId());
            List<LeaveInfo> findlist = mapper.select(find);
            Boolean flag = true;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date listartTime = null;
            Date liendTime = null;
            Date startTime = leaveInfoForm.getStartTime();
            Date endTime = leaveInfoForm.getEndTime();
            for (LeaveInfo li : findlist) {
                try {
                    listartTime = format.parse(format.format(li.getStartTime()));
                    liendTime = format.parse(format.format(li.getEndTime()));
                } catch (ParseException e) {
                    throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                }
                if ((startTime.before(liendTime) && startTime.after(listartTime)) ||
                        (leaveInfoForm.getEndTime().before(liendTime) && endTime.after(listartTime)) ||
                        startTime.equals(listartTime) || endTime.equals(liendTime) ||
                        (startTime.before(listartTime) && endTime.after(liendTime))
                ) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                LeaveInfo leaveInfo = new LeaveInfo();
                BeanUtils.copyProperties(leaveInfoForm, leaveInfo);
                leaveInfo.setCrtTime(new Date());
                int num = mapper.insert(leaveInfo);
                //插入审批人信息
                int leaveId = leaveInfo.getId();
                List<ApprovalInfo> list = leaveInfoForm.getApprpvalPeopleList();
                for (ApprovalInfo approvalInfo : list) {
                    approvalInfo.setCrtId(leaveInfoForm.getCrtId());
                    approvalInfo.setCrtTime(new Date());
                    approvalInfo.setLeaveId(leaveId);
                }
                approvalInfoMapper.batchInsert(list);
                //插入抄送人信息
                if (leaveInfoForm.getCopyList().size() > 0) {
                    List<CopyInfo> copyInfoList = new ArrayList<>();
                    for (Integer copyId : leaveInfoForm.getCopyList()) {
                        CopyInfo copyInfo = new CopyInfo();
                        copyInfo.setApplyId(leaveId);
                        copyInfo.setApplyType(0);
                        copyInfo.setUserId(copyId);
                        copyInfo.setCrtId(leaveInfoForm.getUserId());
                        copyInfo.setCrtTime(new Date());
                        copyInfoList.add(copyInfo);
                    }
                    copyInfoMapper.batchInsert(copyInfoList);
                }
                return num;
            }
            throw new ClientServiceException("该申请与其他请假申请时间冲突", INSERT_MODEL);
        }
        throw new ClientServiceException("每天只能发起一种类型的申请", INSERT_MODEL);
    }

    /**
     * 根据用户ID和日期查看班次列表
     *
     * @return
     */
    public List<EmLeaveVO> selectBaseByDay(LeaveInfoForm leaveInfoForm) {
        List<EmLeaveVO> list = mapper.selectBaseByDay(leaveInfoForm);
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap(16);
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));
        for (EmLeaveVO emLeaveVO : list) {
            emLeaveVO.setCompanyName(clinicMap.get(emLeaveVO.getCompanyId() + "").getName());
        }
        return list;
    }

    /**
     * 根据天数获得审批信息
     *
     * @param leaveInfoForm
     * @return
     */
    public List<ApprovalLevelSet> selectApprovalByDay(LeaveInfoForm leaveInfoForm) {
        return mapper.selectApprovalByDay(leaveInfoForm);
    }

    /**
     * 根据天数获得审批信息
     *
     * @param leaveInfoByEmForm
     * @return
     */
    public Integer addEm(LeaveInfoByEmForm leaveInfoByEmForm) {
        //判断是否有其他类型的申请
        if (true) {
            //判断是否与同类型其他申请时间冲突
            List<LeaveSchedule>scList = leaveInfoByEmForm.getScList();
            int isConflict = leaveScheduleMapper.selectNum(scList);
            if(isConflict==0){
                LeaveInfo leaveInfo = new LeaveInfo();
                BeanUtils.copyProperties(leaveInfoByEmForm, leaveInfo);
                leaveInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                leaveInfo.setCrtTime(new Date());
                int num = mapper.insert(leaveInfo);
                int leaveId = leaveInfo.getId();
                //插入班次请假信息
                for(LeaveSchedule leaveSchedule:scList){
                    leaveSchedule.setCrtId(leaveInfoByEmForm.getCrtId());
                    leaveSchedule.setCrtTime(new Date());
                    leaveSchedule.setLeaveId(leaveId);
                }
                leaveScheduleMapper.batchInsert(scList);
                //插入审批人信息
                List<ApprovalInfo> list = leaveInfoByEmForm.getApprpvalPeopleList();
                for (ApprovalInfo approvalInfo : list) {
                    approvalInfo.setCrtId(leaveInfoByEmForm.getCrtId());
                    approvalInfo.setCrtTime(new Date());
                    approvalInfo.setLeaveId(leaveId);
                }
                approvalInfoMapper.batchInsert(list);
                //插入抄送人信息
                if (leaveInfoByEmForm.getCopyList().size() > 0) {
                    List<CopyInfo> copyInfoList = new ArrayList<>();
                    for (Integer copyId : leaveInfoByEmForm.getCopyList()) {
                        CopyInfo copyInfo = new CopyInfo();
                        copyInfo.setApplyId(leaveId);
                        copyInfo.setApplyType(0);
                        copyInfo.setUserId(copyId);
                        copyInfo.setCrtId(leaveInfoByEmForm.getUserId());
                        copyInfo.setCrtTime(new Date());
                        copyInfoList.add(copyInfo);
                    }
                    copyInfoMapper.batchInsert(copyInfoList);
                }
                return num;
            }
            throw new ClientServiceException("该申请与其他请假申请时间冲突", INSERT_MODEL);
        }
        throw new ClientServiceException("每天只能发起一种类型的申请", INSERT_MODEL);
    }
}
