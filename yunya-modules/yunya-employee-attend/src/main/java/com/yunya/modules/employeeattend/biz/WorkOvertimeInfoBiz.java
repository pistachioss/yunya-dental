package com.yunya.modules.employeeattend.biz;

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
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.CopyInfo;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.models.employee_attend.WorkOvertimeInfo;

import com.yunya.modules.employeeattend.form.NoWorkByDateForm;
import com.yunya.modules.employeeattend.form.NoWorkForm;
import com.yunya.modules.employeeattend.form.WorkForm;
import com.yunya.modules.employeeattend.form.WorkOvertimeInfoForm;
import com.yunya.modules.employeeattend.mapper.BaseScheduleMapper;
import com.yunya.modules.employeeattend.mapper.CopyInfoMapper;
import com.yunya.modules.employeeattend.mapper.EmployeeScheduleMapper;
import com.yunya.modules.employeeattend.mapper.WorkOvertimeInfoMapper;
import com.yunya.modules.employeeattend.vo.EmListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;
import static com.yunya.framework.common.constant.OperationCodeConstants.OBJECT_EDIT_FAIL;

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



        if (true) {
            WorkOvertimeInfo one = new WorkOvertimeInfo();
            one.setRestScheduleId(workOvertimeInfoForm.getRestScheduleId());
            int a = mapper.selectCount(one);
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
                            int num = mapper.insertSelective(workOvertimeInfo);
                            //生成抄送信息
                            if (workOvertimeInfoForm.getCopyList().size() > 0) {
                                List<CopyInfo> copyInfoList = new ArrayList<>();
                                for (Integer copyId : workOvertimeInfoForm.getCopyList()) {
                                    CopyInfo copyInfo = new CopyInfo();
                                    copyInfo.setApplyId(num);
                                    copyInfo.setApplyType(1);
                                    copyInfo.setUserId(copyId);
                                    copyInfo.setCrtId(workOvertimeInfo.getUserId());
                                    copyInfo.setCrtTime(new Date());
                                    copyInfoList.add(copyInfo);
                                }
                                copyInfoMapper.batchInsert(copyInfoList);
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
            Byte[] userStatus = {0, 1, 3};
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
        if (workOvertimeInfo.getApprpvalStatus() == 0) {
            if (workOvertimeInfo.getApprovalPeopleId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
                workOvertimeInfo.setApprpvalStatus(workOvertimeInfoForm.getApprpvalStatus());
                return mapper.updateByPrimaryKey(workOvertimeInfo);
            }
            throw new ClientServiceException("当前用户无审批该申请的权限", OBJECT_EDIT_FAIL);
        }
        throw new ClientServiceException("当前申请已被处理或已过期", OBJECT_EDIT_FAIL);
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
        if (workOvertimeInfo.getApprpvalStatus() == 0) {
            if (workOvertimeInfo.getUserId().equals(Integer.valueOf(BaseContextHandler.getUserID()))) {
                workOvertimeInfo.setApprpvalStatus(3);
                return mapper.updateByPrimaryKey(workOvertimeInfo);
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
}
