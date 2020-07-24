package com.yunya.modules.employeeattend.rpc.service;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.biz.ClinicScheduleBiz;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.mapper.EmployeeScheduleMapper;
import com.yunya.modules.employeeattend.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 杨柳絮
 * @className EmployeeScheduleSerivce
 * @description
 * @date 2020/7/23 16:03
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmployeeScheduleSerivce extends BaseBiz<EmployeeScheduleMapper, EmployeeSchedule> {
  private static final int SHIFT_DAYS = 14;
  private static final int COUNT = 1;
  @Autowired
  private ClinicScheduleBiz clinicScheduleBiz;
  @Autowired
  private RemoteSystemServiceFeign remoteSystemServiceFeign;

  /**
   * 获取排班表列表
   *
   * @param employeeScheduleQueryForm
   * @return
   */
  public EmployeeScheduleResultVO findList(EmployeeScheduleQueryForm employeeScheduleQueryForm) {
    Date startDate = employeeScheduleQueryForm.getStartDate();
    Date endDate = employeeScheduleQueryForm.getEndDate();
    if (null == startDate || null == endDate) {
      Calendar calendar = Calendar.getInstance();
      startDate = DateUtil.getThisWeekMonday(new Date());
      calendar.setTime(startDate);
      calendar.add(Calendar.DATE, +SHIFT_DAYS);
      endDate = calendar.getTime();
    } else {
      startDate = employeeScheduleQueryForm.getStartDate();
      endDate = new Date(startDate.getTime() + 14 * 24 * 60 * 60 * 1000);
    }

    // 请求参数
    List<Integer> postNames = employeeScheduleQueryForm.getPostNames();
    Integer page = employeeScheduleQueryForm.getPage();
    Integer size = employeeScheduleQueryForm.getSize();
    String name = employeeScheduleQueryForm.getName();
    Integer clinicId = employeeScheduleQueryForm.getClinicId();

    List<ClinicScheduleVO> ClinicSchedules = clinicScheduleBiz.findVOsByClinicIdAndInservice(employeeScheduleQueryForm.getClinicId());
    Map<String, ClinicScheduleVO> ClinicScheduleMap = new HashMap();
    ClinicSchedules.forEach(x -> ClinicScheduleMap.put(x.getScheduleId() + "", x));
    if (postNames == null || postNames.size() == 0) {
      postNames = null;
    }
    //获取员工信息
    SysUserEmployeeModel model = new SysUserEmployeeModel();
    model.setWhetherPage(false);//查询总数不分页
    List<Integer> orgIds = new ArrayList<>();
    orgIds.add(clinicId);//设置门诊ID
    model.setOrgIds(orgIds);
    model.setPostIds(postNames);
    model.setKeyWord(name);
    model.setWorkStatus(BusinessConstants.USER_RESIGNATION_STATUS);//离职状态
    int count = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model).size();
    model.setWhetherPage(true);
    model.setPageNum(page);
    model.setPageSize(size);
//    System.out.println(remoteSystemServiceFeign.getAllPermissionInfo());
    List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
    //获取门诊信息
    OrganizationModel organizationModel = new OrganizationModel();
    organizationModel.setWhetherPage(false);
    List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
    Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
    clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    int days = (int) (endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24);
    List<UserWorkVO> shiftWorkDatas = new ArrayList<>();
    for (SysUserInfoDetail baseEmployee : employees) {
      UserWorkVO userWorkMap = new UserWorkVO();
      String userId = baseEmployee.getUserId()+"";
      userWorkMap.setCompEmpId(baseEmployee.getUserId());
      userWorkMap.setName(baseEmployee.getName());
      userWorkMap.setPostNames(baseEmployee.getPosts());
      // 获取时间内的排班
      List<EmployeeScheduleVO> EmployeeScheduleVOs = mapper.selectVOByDateAndCompEmpId(startDate, endDate, null, userId);
      List<WorkDayVO>workDayDatas = new ArrayList();
      // 设置排班列表
      for (int i = 0; i < days; i++) {
        calendar.getTime();
        for (EmployeeScheduleVO employeeScheduleVO : EmployeeScheduleVOs) {
          WorkDayVO workDayData = new WorkDayVO();
          if (calendar.getTime().equals(employeeScheduleVO.getWorkDate())) {
            workDayData.setId(employeeScheduleVO.getScheduleId());
            //拼接排班的时间段
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date startTime = ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getFirstStartTime();
            Date endTime = new Date();
            if (ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getSecondEndTime() != null) {
              endTime = ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getSecondEndTime();
            } else {
              endTime = ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getFirstEndTime();
            }
            String simtime = dateFormat.format(startTime) + "-" + dateFormat.format(endTime);

            workDayData.setName(clinicMap.get(employeeScheduleVO.getClinicId() + "").getName() + ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getName() + simtime);
            workDayData.setDate(employeeScheduleVO.getWorkDate());
            workDayData.setCompClinId(employeeScheduleVO.getClinicId());
          }
          workDayDatas.add(workDayData);
        }

        calendar.add(Calendar.DATE, +COUNT);
      }
      userWorkMap.setDays(workDayDatas);
      shiftWorkDatas.add(userWorkMap);
      calendar.add(Calendar.DATE, -days);
    }
    EmployeeScheduleResultVO employeeScheduleResultVO = new EmployeeScheduleResultVO();
    employeeScheduleResultVO.setShiftWorkDatas(shiftWorkDatas);
    employeeScheduleResultVO.setCount(count);
    return employeeScheduleResultVO;
  }
}
