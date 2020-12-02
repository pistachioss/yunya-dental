package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.LeaveInfoQueryForm;
import com.yunya.feign.employee_attend.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.*;
import com.yunya.modules.employeeattend.form.FindApprovalByMeForm;
import com.yunya.modules.employeeattend.form.LeaveInfoByEmForm;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
import com.yunya.modules.employeeattend.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

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
    @Autowired
    private FieldInfoMapper fieldInfoMapper;
    @Autowired
    private WorkOvertimeInfoMapper workOvertimeInfoMapper;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = simpleDateFormat.format(leaveInfoForm.getStartTime());
        String nowString = simpleDateFormat.format(new Date());
        Date date = null;
        Date now = new Date();
        try {
            date = simpleDateFormat.parse(dateString);
            now = simpleDateFormat.parse(nowString);
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
        }
        if (now.before(date)) {
            //判断是否有其他类型的申请
            LeaveInfo copy = new LeaveInfo();
            BeanUtils.copyProperties(leaveInfoForm, copy);
            int fiwi = mapper.countFiWi(copy);
            if (fiwi == 0) {
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
                    if (leaveInfoForm.getCopyList() != null) {
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
        throw new ClientServiceException("不可以发起申请当天及以前的申请事项", INSERT_MODEL);
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
     * 新增按班次请假
     *
     * @param leaveInfoByEmForm
     * @return
     */
    public Integer addEm(LeaveInfoByEmForm leaveInfoByEmForm) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = simpleDateFormat.format(leaveInfoByEmForm.getStartTime());
        String nowString = simpleDateFormat.format(new Date());
        Date date = null;
        Date now = new Date();
        try {
            date = simpleDateFormat.parse(dateString);
            now = simpleDateFormat.parse(nowString);
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
        }
        if (now.before(date)) {
            //判断是否有其他类型的申请
            LeaveInfo li = new LeaveInfo();
            BeanUtils.copyProperties(leaveInfoByEmForm, li);
            int fiwi = mapper.countFiWi(li);
            if (fiwi == 0) {
                //判断是否与同类型其他申请时间冲突
                List<LeaveSchedule> scList = leaveInfoByEmForm.getScList();
                int isConflict = leaveScheduleMapper.selectNum(scList);
                if (isConflict == 0) {
                    LeaveInfo leaveInfo = new LeaveInfo();
                    BeanUtils.copyProperties(leaveInfoByEmForm, leaveInfo);
                    leaveInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    leaveInfo.setCrtTime(new Date());
                    int num = mapper.insert(leaveInfo);
                    int leaveId = leaveInfo.getId();
                    //插入班次请假信息
                    for (LeaveSchedule leaveSchedule : scList) {
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
                    if (leaveInfoByEmForm.getCopyList() != null) {
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
        throw new ClientServiceException("不可以发起申请当天及以前的申请事项", INSERT_MODEL);
    }

    /**
     * 审批请假
     *
     * @param
     * @return
     */
    public Integer examine(LeaveInfoForm leaveInfoForm) {
        LeaveInfo leaveInfo = new LeaveInfo();
        leaveInfo.setId(leaveInfoForm.getId());
        leaveInfo = mapper.selectByPrimaryKey(leaveInfo);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = simpleDateFormat.format(leaveInfo.getStartTime());
        String nowString = simpleDateFormat.format(new Date());
        Date date = null;
        Date now = new Date();
        try {
            date = simpleDateFormat.parse(dateString);
            now = simpleDateFormat.parse(nowString);
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
        }
        //必须提前一天申请或审批
        if (now.before(date)) {
            if (leaveInfo.getApprpvalStatus() == 0) {
                //根据当前登录人Id和请假信息ID 获取审批流中当前登录人的待审批流程
                ApprovalInfo approvalInfo = new ApprovalInfo();
                approvalInfo.setLeaveId(leaveInfoForm.getId());
                approvalInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                approvalInfo = approvalInfoMapper.findNow(approvalInfo);
                //当前审批最后一层级的审批人信息
                ApprovalInfo Minuser = approvalInfoMapper.findMin(leaveInfoForm);
                //是否为最后一层审批或者是否为拒绝
                if(approvalInfo.getId().equals(Minuser.getId())||leaveInfoForm.getApprpvalStatus()==2){
                    leaveInfo.setApprpvalStatus(leaveInfoForm.getApprpvalStatus());
                }
                approvalInfo.setApprovalStatus(leaveInfoForm.getApprpvalStatus());
                //更新审批流程表中的审批状态
                approvalInfoMapper.updateByPrimaryKey(approvalInfo);
                //为了统计待我审批 为请假信息添加 当前审批人(现在审批的下一层级的审批人)字段
                ApprovalInfo next = approvalInfoMapper.findNow(approvalInfo);
                if(next!=null){
                    leaveInfo.setApprovalNowPeopleId(next.getApprovalPeopleId());
                }
                return mapper.updateByPrimaryKey(leaveInfo);
            }
            throw new ClientServiceException("当前申请已被处理", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已过期", OBJECT_EDIT_FAIL);
    }

    /**
     * 撤销加班
     *
     * @param leaveInfoForm
     * @return
     */
    public Integer revoke(LeaveInfoForm leaveInfoForm) {
        LeaveInfo leaveInfo = new LeaveInfo();
        leaveInfo.setId(leaveInfoForm.getId());
        leaveInfo = mapper.selectByPrimaryKey(leaveInfo);
        if (leaveInfo.getApprpvalStatus() == 0) {
            if (leaveInfo.getUserId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
                leaveInfo.setApprpvalStatus(3);
                return mapper.updateByPrimaryKey(leaveInfo);
            }
            throw new ClientServiceException("当前用户无撤销该申请的权限", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已被处理或已过期", OBJECT_EDIT_FAIL);
    }

    public List<LeaveInfoListVO> findList(LeaveInfoForm leaveInfoForm) {
//        LeaveInfo leaveInfo = new LeaveInfo();
//        BeanUtils.copyProperties(leaveInfoForm, leaveInfo);
        List<LeaveInfoListVO> reList = mapper.selectLeave(leaveInfoForm);
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1, 3};
            model.setWorkStatus(userStatus);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> emMap = new HashMap(16);
            employees.forEach(z -> emMap.put(z.getUserId() + "", z));
            for (LeaveInfoListVO li : reList) {
                li.setUserName(emMap.get(li.getUserId() + "").getName());
            }
        }
        return reList;
    }

    /**
     * 获取请假的审批明细
     *
     * @param
     * @return
     */
    public List<ApprovalInfoVO> findApproval(LeaveInfoForm leaveInfoForm) {
        List<ApprovalInfoVO> reList = mapper.findApproval(leaveInfoForm);
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1, 3};
            model.setWorkStatus(userStatus);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> emMap = new HashMap(16);
            employees.forEach(z -> emMap.put(z.getUserId() + "", z));
            for (ApprovalInfoVO li : reList) {
                li.setApprovalPeopleName(emMap.get(li.getUserId() + "").getName());
            }
        }
        return reList;
    }

    public List<LeaveAppVO> findApprovalByMe(FindApprovalByMeForm findApprovalByMeForm) {
        List<LeaveAppVO> reList = mapper.findApprovalByMe(findApprovalByMeForm);
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1, 3};
            model.setWorkStatus(userStatus);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> emMap = new HashMap(16);
            employees.forEach(z -> emMap.put(z.getUserId() + "", z));
            for (LeaveAppVO li : reList) {
                li.setUserName(emMap.get(li.getUserId() + "").getName());
            }
        }
        return reList;

    }

    public List<LeaveAppVO> findOverApprovalByMe(FindApprovalByMeForm findApprovalByMeForm) {
        List<LeaveAppVO> reList = mapper.findOverApprovalByMe(findApprovalByMeForm);
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1, 3};
            model.setWorkStatus(userStatus);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> emMap = new HashMap(16);
            employees.forEach(z -> emMap.put(z.getUserId() + "", z));
            for (LeaveAppVO li : reList) {
                li.setUserName(emMap.get(li.getUserId() + "").getName());
            }
        }
        return reList;

    }

    /**
     * 分页查询请假时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<LeaveInfoVO> statisticsLeavesByMinute(LeaveInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        return mapper.statisticsLeavesByMinute(queryForm);
    }

    /**
     * 条件查询按班次请假的申请
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<LeaveInfoVO> findLeaveInfosBySchedule(LeaveInfoQueryForm queryForm) {
        return mapper.findLeaveInfosBySchedule(queryForm);
    }
}
