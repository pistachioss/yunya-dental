package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.WorkOvertimeInfoQueryForm;
import com.yunya.feign.employee_attend.vo.AttendanceOvertimeMinuteVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoListVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.feign.employee_attend.vo.findNoWorkEmByDateVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.*;
import com.yunya.modules.employeeattend.form.*;
import com.yunya.modules.employeeattend.mapper.*;
import com.yunya.modules.employeeattend.util.JpushManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkOvertimeInfoBiz extends BaseBiz<WorkOvertimeInfoMapper, WorkOvertimeInfo> {
    @Autowired
    private BaseScheduleMapper baseScheduleMapper;
    @Autowired
    private CopyInfoMapper copyInfoMapper;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    private EmployeeScheduleMapper employeeScheduleMapper;
    @Autowired
    private FieldInfoMapper fieldInfoMapper;
    @Autowired
    private LeaveInfoMapper leaveInfoMapper;
    @Autowired
    private EmployeePushBiz employeePushBiz;
    @Autowired
    private ApprovalPeopleBiz approvalPeopleBiz;

    /**
     * 根据日期和用户id列表查询加班列表
     *
     * @param userIds 用户id
     * @param date    日期
     * @return
     */
    public List<WorkOvertimeInfoVO> findWorkOvertimeInfosByUserIdsAndDate(List<Integer> userIds, Date date) {
        return mapper.findWorkOvertimeInfoByUserIdsAndDate(userIds, date);
    }

    /**
     * 新增加班申请
     *
     * @param workOvertimeInfoForm
     * @return
     */
    public int create(WorkOvertimeInfoForm workOvertimeInfoForm) {
        //判断是否有其他类型的申请
        //判断当天是否有外勤申请
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setStartTime(workOvertimeInfoForm.getWorkDate());
        fieldInfo.setUserId(workOvertimeInfoForm.getUserId());
        int fi = fieldInfoMapper.countByDay(fieldInfo);
        //判断是否有请假申请
        LeaveInfo leaveInfo = new LeaveInfo();
        leaveInfo.setStartTime(workOvertimeInfoForm.getWorkDate());
        leaveInfo.setUserId(workOvertimeInfoForm.getUserId());
        int li = leaveInfoMapper.countByDay(leaveInfo);
        if (fi==0&&li==0) {
            WorkOvertimeInfo one = new WorkOvertimeInfo();
            one.setRestScheduleId(workOvertimeInfoForm.getRestScheduleId());
            int a = mapper.selectCountById(one);
            //每个休息班只能排一个加班
            if (a == 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = simpleDateFormat.format(workOvertimeInfoForm.getWorkDate());
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
                    EmployeeSchedule employeeSchedule = new EmployeeSchedule();
                    employeeSchedule.setId(workOvertimeInfoForm.getRestScheduleId());
                    employeeSchedule = employeeScheduleMapper.selectByPrimaryKey(employeeSchedule);
                    BaseSchedule baseSchedule = new BaseSchedule();
                    baseSchedule.setId(employeeSchedule.getScheduleId());
                    //休息班的班次信息
                    BaseSchedule reba = baseScheduleMapper.selectByPrimaryKey(baseSchedule);
                    baseSchedule.setId(workOvertimeInfoForm.getScheduleId());
                    //加班的班次信息
                    BaseSchedule ba = baseScheduleMapper.selectByPrimaryKey(baseSchedule);

                    if (reba != null && ba != null) {
                        DateFormat df = DateFormat.getTimeInstance();
                        Date restartTime = null;
                        Date reendTime = null;
                        Date startTime = null;
                        Date endTime = null;
                        try {
                            startTime = df.parse(df.format(ba.getFirstStartTime()));
                            if (ba.getSecondEndTime() != null) {
                                endTime = df.parse(df.format(ba.getSecondEndTime()));
                            } else {
                                endTime = df.parse(df.format(ba.getFirstEndTime()));
                            }
                            restartTime = df.parse(df.format(reba.getFirstStartTime()));
                            if (reba.getSecondEndTime() != null) {
                                reendTime = df.parse(df.format(reba.getSecondEndTime()));
                            } else {
                                reendTime = df.parse(df.format(reba.getFirstEndTime()));
                            }
                        } catch (ParseException e) {
                            throw new ClientServiceException("时间转换错误", DATA_TRANSFORMATION_EXIST);
                        }
                        if (startTime.getTime() <= reendTime.getTime() && startTime.getTime() >= restartTime.getTime()
                                && endTime.getTime() <= reendTime.getTime() && endTime.getTime() >= restartTime.getTime()) {
                            WorkOvertimeInfo workOvertimeInfo = new WorkOvertimeInfo();
                            BeanUtils.copyProperties(workOvertimeInfoForm, workOvertimeInfo);
                            workOvertimeInfo.setCrtId(workOvertimeInfo.getUserId());
                            workOvertimeInfo.setCrtTime(new Date());
                            workOvertimeInfo.setApprovalStatus(0);
                            int num = mapper.insertSelective(workOvertimeInfo);

                            // TODO :一级审批人
                            // start 添加推送 需求1450 by zd.xie
                            SysUserInfoDetail ui = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(workOvertimeInfoForm.getUserId());
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
                                // 根据leaveInfoForm.getApprovalPeopleId();查推送号与平台
//                                Integer userid = approvalPeopleBiz.selectById(workOvertimeInfoForm.getApprovalPeopleId()).getUserId();
                                // fix: bug3476
                                Integer userid = workOvertimeInfoForm.getApprovalPeopleId();
                                emp_ids.add(userid);
                                employeePushForm.setId(workOvertimeInfo.getId());
                                List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                                employeePushFormList.forEach(el -> {
                                    JpushManager.getInstance().pushLeaveApproval(el, 2);
                                });
                            }
                            // end 添加推送 需求1450 by zd.xie

                            //生成抄送信息
                            if (workOvertimeInfoForm.getCopyList()!=null) {
                                List<CopyInfo> copyInfoList = new ArrayList<>();
                                for (Integer copyId : workOvertimeInfoForm.getCopyList()) {
                                    CopyInfo copyInfo = new CopyInfo();
                                    copyInfo.setApplyId(workOvertimeInfo.getId());
                                    copyInfo.setApplyType(1);
                                    copyInfo.setUserId(copyId);
                                    copyInfo.setCrtId(workOvertimeInfo.getUserId());
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
                                    workOvertimeInfoForm.getCopyList().forEach(nn -> {
                                        emp_ids.add(nn);
                                    });
                                    employeePushForm.setEmpId(emp_ids);
                                    employeePushForm.setShowName(showName);
                                    employeePushForm.setId(workOvertimeInfo.getId());
                                    List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                                    employeePushFormList.forEach(el -> {
                                        JpushManager.getInstance().pushLeaveCope(el, 2);
                                    });
                                }
                                // end 添加推送 需求1450 by zd.xie
                            }
                            return num;
                        }
                        throw new ClientServiceException("加班班次时间应处于休息班次时间段之内", DATA_TRANSFORMATION_EXIST);
                    }
                    throw new ClientServiceException("班次信息错误", DATA_TRANSFORMATION_EXIST);
                }
                throw new ClientServiceException("不可以发起申请当天及以前的申请事项", INSERT_MODEL);
            }
            throw new ClientServiceException("该申请与其他加班申请时间冲突", INSERT_MODEL);
        }
        throw new ClientServiceException("每天只能发起一种类型的申请", INSERT_MODEL);
    }

    public List<WorkOvertimeInfoListVO> findList(WorkOvertimeInfoForm workOvertimeInfoForm) {
        List<WorkOvertimeInfoListVO> list = mapper.findList(workOvertimeInfoForm);
        if (list.size() > 0) {
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
            //获取门诊信息
            OrganizationModel organizationModel = new OrganizationModel();
            organizationModel.setWhetherPage(false);
            List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
            Map<String, OrganizationInfoDetail> clinicMap = new HashMap(16);
            clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

            for (WorkOvertimeInfoListVO workOvertimeInfoListVO : list) {
                workOvertimeInfoListVO.setCompanyName(clinicMap.get(workOvertimeInfoListVO.getCompanyId() + "").getName());
                workOvertimeInfoListVO.setApprovalPeopleName(emMap.get(workOvertimeInfoListVO.getApprovalPeopleId() + "").getName());
                workOvertimeInfoListVO.setUserName(emMap.get(workOvertimeInfoListVO.getUserId() + "").getName());
            }
        }
        return list;
    }

    /**
     * 审批加班
     *
     * @param workOvertimeInfoForm
     * @return
     */
    public Integer examine(WorkOvertimeInfoForm workOvertimeInfoForm) {
        WorkOvertimeInfo workOvertimeInfo = new WorkOvertimeInfo();
        workOvertimeInfo.setId(workOvertimeInfoForm.getId());
        workOvertimeInfo = mapper.selectByPrimaryKey(workOvertimeInfo);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = simpleDateFormat.format(workOvertimeInfo.getWorkDate());
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

            if (workOvertimeInfo.getApprovalStatus() == 0) {
                if (workOvertimeInfo.getApprovalPeopleId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
                    workOvertimeInfo.setApprovalStatus(workOvertimeInfoForm.getApprovalStatus());
                    workOvertimeInfo.setUpdTime(new Date());
                    workOvertimeInfo.setRefuseReason(workOvertimeInfoForm.getRefuseReason());
                    int num = mapper.updateByPrimaryKey(workOvertimeInfo);

                    // TODO :执行审批时
                    // start 添加推送 需求1450 by zd.xie
                    if(num > 0){
                        SysUserInfoDetail ui = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(workOvertimeInfo.getUserId());
                        String showName = "xxx";
                        if(ui != null){
                            showName = ui.getName();
                        }
                        EmployeePushForm employeePushForm = new EmployeePushForm();
                        // 组装
                        Set<Integer> emp_ids = new HashSet<>();
                        employeePushForm.setEmpId(emp_ids);
                        employeePushForm.setShowName(showName);
                        employeePushForm.setId(workOvertimeInfoForm.getId());
                        switch (workOvertimeInfoForm.getApprovalStatus()){
                            case 1:
                                // 通过
                                emp_ids.add(workOvertimeInfo.getUserId());
                                List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                                employeePushFormList.forEach(el -> {
                                    JpushManager.getInstance().pushLeaveYes(el, 2);
                                });
                                break;
                            case 2:
                                // 拒绝
                                emp_ids.add(workOvertimeInfo.getUserId());
                                List<EmployeePushForm> employeePushFormList1 = employeePushBiz.makeEmployeePushForm(employeePushForm);
                                employeePushFormList1.forEach(el -> {
                                    JpushManager.getInstance().pushLeaveNo(el, 2);
                                });
                                break;
                        }
                    }
                    // end 添加推送 需求1450 by zd.xie
                    return num;
                }
                throw new ClientServiceException("当前用户无审批该申请的权限", OBJECT_EDIT_FAIL);
            }
            throw new ClientServiceException("当前申请已被处理或已过期", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已过期", OBJECT_EDIT_FAIL);
    }

    /**
     * 撤销加班
     *
     * @param workOvertimeInfoForm
     * @return
     */
    public Integer revoke(WorkOvertimeInfoForm workOvertimeInfoForm) {
        WorkOvertimeInfo workOvertimeInfo = new WorkOvertimeInfo();
        workOvertimeInfo.setId(workOvertimeInfoForm.getId());
        workOvertimeInfo = mapper.selectByPrimaryKey(workOvertimeInfo);
        if (workOvertimeInfo.getApprovalStatus() == 0) {
            if (workOvertimeInfo.getUserId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
                workOvertimeInfo.setApprovalStatus(3);
                int num = mapper.updateByPrimaryKey(workOvertimeInfo);

                // TODO :撤销审批时
                // start 添加推送 需求1450 by zd.xie
                if(num > 0){
                    SysUserInfoDetail ui = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(workOvertimeInfo.getUserId());
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
                    emp_ids.add(workOvertimeInfo.getUserId());
//                    Integer userid = approvalPeopleBiz.selectById(workOvertimeInfoForm.getApprovalPeopleId()).getUserId();
                    // fix: bug3476
                    Integer userid = workOvertimeInfoForm.getApprovalPeopleId();
                    emp_ids.add(userid);
                    employeePushForm.setId(workOvertimeInfoForm.getId());
                    List<EmployeePushForm> employeePushFormList = employeePushBiz.makeEmployeePushForm(employeePushForm);
                    employeePushFormList.forEach(el -> {
                        JpushManager.getInstance().pushLeaveCancel(el, 2);
                    });
                }
                // end 添加推送 需求1450 by zd.xie
                return num;
            }
            throw new ClientServiceException("当前用户无撤销该申请的权限", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已被处理或已过期", OBJECT_EDIT_FAIL);
    }

    /**
     * 根据时间段和门诊iD获取加班班次列表
     *
     * @param
     * @return
     */
    public List<BaseSchedule> findWorkEm(WorkForm workForm) {
        return mapper.findWorkEm(workForm);
    }

    /**
     * 根据日期时间段和用户id获取休息班班次列表
     *
     * @param
     * @return
     */
    public Set<String> findNoWorkEm(NoWorkForm NoWorkForm) {
        return mapper.findNoWorkEm(NoWorkForm);
    }

    /**
     * 根据日期时间段和用户id获取休息班班次列表
     *
     * @param
     * @return
     */
    public List<findNoWorkEmByDateVO> findNoWorkEmByDate(NoWorkByDateForm noWorkByDateForm) {
        List<findNoWorkEmByDateVO>list = mapper.findNoWorkEmByDate(noWorkByDateForm);
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap(16);
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));
        for(findNoWorkEmByDateVO vo:list){
            vo.setCompanyName(clinicMap.get(vo.getCompanyId()+"").getName());
        }
        return list;
    }

    /**
     * 分页条件查询
     * @param queryForm 查询参数
     * @return
     */
    public List<WorkOvertimeInfoVO> findWorkOvertimeInfoList(WorkOvertimeInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(),queryForm.getPageSize());
        }
        return mapper.findWorkOvertimeInfoList(queryForm);
    }

    /**
     * 分页查询休息日加班时长的考勤汇总明细
     *
     * @param queryForm 查询参数
     * @return
     */
    public List<AttendanceOvertimeMinuteVO> statisticsWorkOvertimesByMinute(WorkOvertimeInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(),queryForm.getPageSize());
        }
        return mapper.statisticsWorkOvertimesByMinute(queryForm);
    }
}
