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
import com.yunya.modules.employeeattend.form.EmployeePushForm;
import com.yunya.modules.employeeattend.form.FindApprovalByMeForm;
import com.yunya.modules.employeeattend.form.LeaveInfoByEmForm;
import com.yunya.modules.employeeattend.form.LeaveInfoForm;
import com.yunya.modules.employeeattend.mapper.ApprovalInfoMapper;
import com.yunya.modules.employeeattend.mapper.CopyInfoMapper;
import com.yunya.modules.employeeattend.mapper.LeaveInfoMapper;
import com.yunya.modules.employeeattend.mapper.LeaveScheduleMapper;
import com.yunya.modules.employeeattend.util.JpushManager;
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
    private EmployeePushBiz employeePushBiz;
    @Autowired
    private ApprovalPeopleBiz approvalPeopleBiz;
    @Autowired
    private CopyInfoBiz copyInfoBiz;

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
        if (now.compareTo(date) <= 0) {
            //判断是否有其他类型的申请
            LeaveInfo copy = new LeaveInfo();
            BeanUtils.copyProperties(leaveInfoForm, copy);
            int fiwi = mapper.countFiWi(copy);
            if (fiwi == 0) {
                //判断是否与同类型其他申请时间冲突
                LeaveInfo find = new LeaveInfo();
                find.setUserId(leaveInfoForm.getUserId());
                List<LeaveInfo> findlist = mapper.findLeaveListByDay(find);
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
                    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
                    LeaveInfo leaveInfo = new LeaveInfo();
                    BeanUtils.copyProperties(leaveInfoForm, leaveInfo);
                    leaveInfo.setCrtId(userId);
                    leaveInfo.setCrtTime(new Date());
                    leaveInfo.setApprovalStatus(0);
                    int num = mapper.insert(leaveInfo);
                    int leaveId = leaveInfo.getId();
                    // TODO :一级审批人
                    // start 添加推送 需求1450 by zd.xie
                    SysUserInfoDetail ui = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(leaveInfoForm.getUserId());
                    String showName = "xxx";
                    if(ui != null){
                        showName = ui.getName();
                    }
                    if(num > 0){
                        EmployeePushForm employeePushForm = new EmployeePushForm();
                        // 组装
                        Set<Integer> emp_ids = new HashSet<>();
                        employeePushForm.setEmpId(emp_ids);
                        employeePushForm.setShowName(showName);
                        // 根据leaveInfoForm.getApprovalNowPeopleId();查推送号与平台
                        Integer userid = approvalPeopleBiz.selectById(leaveInfoForm.getApprovalNowPeopleId()).getUserId();
                        emp_ids.add(userid);
                        employeePushForm.setIds(Arrays.asList(leaveId));
                        employeePushForm.setOptId(userId);
                        List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                        employeePushFormList.forEach(el -> {
                            JpushManager.getInstance().pushLeaveApproval(el, 1);
                        });
                    }
                    // end 添加推送 需求1450 by zd.xie
                    //插入审批人信息
                    List<ApprovalInfo> list = leaveInfoForm.getApprovalPeopleList();
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
                            copyInfo.setHadRead(false);
                            copyInfo.setCrtId(leaveInfoForm.getUserId());
                            copyInfo.setCrtTime(new Date());
                            copyInfoList.add(copyInfo);
                        }
                        int n = copyInfoMapper.batchInsert(copyInfoList);
                        // TODO :所有抄送人
                        // start 添加推送 需求1450 by zd.xie
                        if(n > 0){
                            EmployeePushForm employeePushForm = new EmployeePushForm();
                            // 组装
                            Set<Integer> emp_ids = new HashSet<>();
                            leaveInfoForm.getCopyList().forEach(nn -> {
                                emp_ids.add(nn);
                            });
                            employeePushForm.setEmpId(emp_ids);
                            employeePushForm.setShowName(showName);
                            employeePushForm.setIds(Arrays.asList(leaveId));
                            employeePushForm.setOptId(userId);
                            List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                            employeePushFormList.forEach(el -> {
                                JpushManager.getInstance().pushLeaveCope(el, 1);
                            });
                        }
                        // end 添加推送 需求1450 by zd.xie
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
        if(leaveInfoForm.getId()!=null){
            int a  = mapper.findVacationStaus(leaveInfoForm);
            leaveInfoForm.setVacationStatus(a);
        }
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
        List<ApprovalLevelSet>list = mapper.selectApprovalByDay(leaveInfoForm);
        list.remove(null);
        return list;
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
        if (now.compareTo(date) <= 0) {
            //判断是否有其他类型的申请
            LeaveInfo li = new LeaveInfo();
            BeanUtils.copyProperties(leaveInfoByEmForm, li);
            int fiwi = mapper.countFiWi(li);
            if (fiwi == 0) {
                //判断是否与同类型其他申请时间冲突
                List<LeaveSchedule> scList = leaveInfoByEmForm.getScList();
                int isConflict = leaveScheduleMapper.selectNum(scList);
                if (isConflict == 0) {
                    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
                    LeaveInfo leaveInfo = new LeaveInfo();
                    BeanUtils.copyProperties(leaveInfoByEmForm, leaveInfo);
                    leaveInfo.setCrtId(userId);
                    leaveInfo.setCrtTime(new Date());
                    leaveInfo.setApprovalStatus(0);
                    int num = mapper.insert(leaveInfo);
                    int leaveId = leaveInfo.getId();
                    // TODO :一级审批人
                    // start 添加推送 需求1450 by zd.xie
                    SysUserInfoDetail ui = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(leaveInfoByEmForm.getUserId());
                    String showName = "xxx";
                    if(ui != null){
                        showName = ui.getName();
                    }
                    if(num > 0){
                        EmployeePushForm employeePushForm = new EmployeePushForm();
                        Set<Integer> emp_ids = new HashSet<>();
                        employeePushForm.setEmpId(emp_ids);
                        employeePushForm.setShowName(showName);
                        // 根据leaveInfoForm.getApprovalNowPeopleId();查推送号与平台
                        Integer userid = approvalPeopleBiz.selectById(leaveInfoByEmForm.getApprovalNowPeopleId()).getUserId();
                        emp_ids.add(userid);
                        employeePushForm.setIds(Arrays.asList(leaveId));
                        employeePushForm.setOptId(userId);
                        // 组装
                        List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                        employeePushFormList.forEach(el -> {
                            JpushManager.getInstance().pushLeaveApproval(el, 1);
                        });
                    }
                    // end 添加推送 需求1450 by zd.xie
                    //插入审批人信息
                    //插入班次请假信息
                    for (LeaveSchedule leaveSchedule : scList) {
                        leaveSchedule.setCrtId(leaveInfoByEmForm.getCrtId());
                        leaveSchedule.setCrtTime(new Date());
                        leaveSchedule.setLeaveId(leaveId);
                    }
                    leaveScheduleMapper.batchInsert(scList);
                    //插入审批人信息
                    List<ApprovalInfo> list = leaveInfoByEmForm.getApprovalPeopleList();
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
                            copyInfo.setHadRead(false);
                            copyInfo.setCrtId(leaveInfoByEmForm.getUserId());
                            copyInfo.setCrtTime(new Date());
                            copyInfoList.add(copyInfo);
                        }
                        int n = copyInfoMapper.batchInsert(copyInfoList);
                        // TODO :所有抄送人
                        // start 添加推送 需求1450 by zd.xie
                        if(n > 0){
                            EmployeePushForm employeePushForm = new EmployeePushForm();
                            // 组装
                            Set<Integer> emp_ids = new HashSet<>();
                            leaveInfoByEmForm.getCopyList().forEach(nn -> {
                                emp_ids.add(nn);
                            });
                            employeePushForm.setEmpId(emp_ids);
                            employeePushForm.setShowName(showName);
                            employeePushForm.setIds(Arrays.asList(leaveId));
                            employeePushForm.setOptId(userId);
                            List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                            employeePushFormList.forEach(el -> {
                                JpushManager.getInstance().pushLeaveCope(el, 1);
                            });
                        }
                        // end 添加推送 需求1450 by zd.xie
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
        if (now.compareTo(date) <= 0) {
            if (leaveInfo.getApprovalStatus() == 0) {
                Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
                //根据当前登录人Id和请假信息ID 获取审批流中当前登录人的待审批流程
                ApprovalInfo approvalInfo = new ApprovalInfo();
                approvalInfo.setLeaveId(leaveInfoForm.getId());
                approvalInfo.setCrtId(userId);
                approvalInfo = approvalInfoMapper.findNow(approvalInfo);
                //当前审批最后一层级的审批人信息
                ApprovalInfo Minuser = approvalInfoMapper.findMin(leaveInfoForm);
                //是否为最后一层审批或者是否为拒绝
                if(approvalInfo.getId().equals(Minuser.getId())||leaveInfoForm.getApprovalStatus()==2){
                    leaveInfo.setApprovalStatus(leaveInfoForm.getApprovalStatus());
                }
                approvalInfo.setApprovalStatus(leaveInfoForm.getApprovalStatus());
                approvalInfo.setUpdTime(new Date());
                //更新审批流程表中的审批状态
                approvalInfoMapper.updateByPrimaryKey(approvalInfo);
                //为了统计待我审批 为请假信息添加 当前审批人(现在审批的下一层级的审批人)字段
                ApprovalInfo next = approvalInfoMapper.findNow(approvalInfo);
                if(next!=null){
                    leaveInfo.setApprovalNowPeopleId(next.getApprovalPeopleId());
                }
                leaveInfo.setRefuseReason(leaveInfoForm.getRefuseReason());
                leaveInfo.setUpdTime(new Date());
                int num = mapper.updateByPrimaryKey(leaveInfo);

                // TODO :执行审批时
                // start 添加推送 需求1450 by zd.xie
                if(num > 0){
                    SysUserInfoDetail ui = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(leaveInfo.getUserId());
                    String showName = "xxx";
                    if(ui != null){
                        showName = ui.getName();
                    }
                    EmployeePushForm employeePushForm = new EmployeePushForm();
                    // 组装
                    Set<Integer> emp_ids = new HashSet<>();
                    employeePushForm.setEmpId(emp_ids);
                    employeePushForm.setShowName(showName);
                    employeePushForm.setIds(Arrays.asList(leaveInfoForm.getId()));
                    employeePushForm.setOptId(userId);
                    switch (leaveInfoForm.getApprovalStatus()){
                        case 1:
                            if(next!=null){
                                // 下一级审批人
                                // 根据leaveInfoForm.getApprovalNowPeopleId();查推送号与平台
                                Integer userid = approvalPeopleBiz.selectById(next.getApprovalPeopleId()).getUserId();
                                emp_ids.add(userid);
                                List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                                employeePushFormList.forEach(el -> {
                                    JpushManager.getInstance().pushLeaveApproval(el, 1);
                                });
                            }
                            else{
                                // 通过
                                emp_ids.add(leaveInfo.getUserId());
                                List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                                employeePushFormList.forEach(el -> {
                                    JpushManager.getInstance().pushLeaveYes(el, 1);
                                });
                            }
                            break;
                        case 2:
                            // 拒绝
                            emp_ids.add(leaveInfo.getUserId());
                            List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                            employeePushFormList.forEach(el -> {
                                JpushManager.getInstance().pushLeaveNo(el, 1);
                            });
                            break;
                    }
                }
                // end 添加推送 需求1450 by zd.xie
                return num;
            }
            throw new ClientServiceException("当前申请已被处理", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已过期", OBJECT_EDIT_FAIL);
    }

    /**
     * 撤销请假
     *
     * @param leaveInfoForm
     * @return
     */
    public Integer revoke(LeaveInfoForm leaveInfoForm) {
        LeaveInfo leaveInfo = new LeaveInfo();
        leaveInfo.setId(leaveInfoForm.getId());
        leaveInfo = mapper.selectByPrimaryKey(leaveInfo);
        if (leaveInfo.getApprovalStatus() == 0) {
            Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
            if (leaveInfo.getUserId().equals(userId)) {
                leaveInfo.setApprovalStatus(3);
                int num = mapper.updateByPrimaryKey(leaveInfo);

                // TODO :撤销审批时
                // start 添加推送 需求1450 by zd.xie
                if(num > 0){
                    SysUserInfoDetail ui = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(leaveInfo.getUserId());
                    String showName = "xxx";
                    if(ui != null){
                        showName = ui.getName();
                    }
                    EmployeePushForm employeePushForm = new EmployeePushForm();
                    // 组装
                    Set<Integer> emp_ids = new HashSet<>();
                    employeePushForm.setEmpId(emp_ids);
                    employeePushForm.setShowName(showName);
                    // 撤销
                    emp_ids.add(leaveInfo.getUserId());
                    Integer userid = approvalPeopleBiz.selectById(leaveInfo.getApprovalNowPeopleId()).getUserId();
                    emp_ids.add(userid);
                    ApprovalInfo approvalInfo = new ApprovalInfo();
                    approvalInfo.setLeaveId(leaveInfoForm.getId());
                    approvalInfo.setApprovalStatus(1);
                    approvalInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    List<ApprovalInfo> approvalInfoList = approvalInfoMapper.select(approvalInfo);
                    if(approvalInfoList!=null){
                        approvalInfoList.forEach(approvalInfo1 -> {
                            emp_ids.add(approvalInfo1.getApprovalPeopleId());
                        });
                    }
                    employeePushForm.setOptId(userId);
                    employeePushForm.setIds(Arrays.asList(leaveInfoForm.getId()));
                    List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                    employeePushFormList.forEach(el -> {
                        JpushManager.getInstance().pushLeaveCancel(el, 1);
                    });
                }
                // end 添加推送 需求1450 by zd.xie
                return num;
            }
            throw new ClientServiceException("当前用户无撤销该申请的权限", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已被处理或已过期", OBJECT_EDIT_FAIL);
    }

    public List<LeaveInfoListVO> findList(LeaveInfoForm leaveInfoForm) {
        List<LeaveInfoListVO> reList = mapper.selectLeave(leaveInfoForm);
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1,2, 3};
            model.setWorkStatus(userStatus);
            List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
            Map<String, SysUserInfoDetail> emMap = new HashMap(16);
            employees.forEach(z -> emMap.put(z.getUserId() + "", z));
            for (LeaveInfoListVO li : reList) {
                li.setUserName(emMap.get(li.getUserId() + "").getName());
            }
            if (leaveInfoForm.isQueryCopyInfo()) {
                copyInfoBiz.updCopyInfoHadRead(leaveInfoForm.getId(), 0, reList.get(0));
            }
        }
        return reList;
    }

    public List<LeaveInfoListVO> findListByDateAndIds(LeaveInfoForm leaveInfoForm) {
        List<LeaveInfoListVO> reList = mapper.selectLeaveByIds(leaveInfoForm);
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1,2, 3};
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

    public List<LeaveInfoListVO> backFindListByIds(LeaveInfoForm leaveInfoForm) {
        List<LeaveInfoListVO> reList = mapper.backFindListByIds(leaveInfoForm);
        if (reList.size() > 0) {
            //获取用户信息
            SysUserEmployeeModel model = new SysUserEmployeeModel();
            model.setWhetherPage(false);
            List<Integer> orgIds = new ArrayList<>();
            model.setOrgIds(orgIds);
            Byte[] userStatus = {0, 1,2, 3};
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
            Byte[] userStatus = {0, 1,2, 3};
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
            Byte[] userStatus = {0, 1,2, 3};
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
            Byte[] userStatus = {0, 1, 2,3};
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
