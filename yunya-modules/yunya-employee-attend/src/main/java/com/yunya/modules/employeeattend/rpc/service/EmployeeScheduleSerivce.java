package com.yunya.modules.employeeattend.rpc.service;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.biz.ClinicScheduleBiz;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.mapper.EmployeeScheduleMapper;
import com.yunya.modules.employeeattend.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
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
    //注意月份是MM
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String startDateString = employeeScheduleQueryForm.getStartDate();
    String endDateString = employeeScheduleQueryForm.getEndDate();
    Date startDate = null;
    Date endDate = null;
    if (null == endDateString) {
      Calendar calendar = Calendar.getInstance();
      try {
        startDate = DateUtil.getThisWeekMonday(simpleDateFormat.parse(startDateString));
      } catch (ParseException e) {
        throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
      }
      calendar.setTime(startDate);
      calendar.add(Calendar.DATE, +SHIFT_DAYS);
      endDate = calendar.getTime();
    } else {
      try {
        startDate = simpleDateFormat.parse(employeeScheduleQueryForm.getStartDate());
        endDate = simpleDateFormat.parse(employeeScheduleQueryForm.getEndDate());
      } catch (ParseException e) {
        throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
      }
    }

    //请求参数
    List<Integer> postNames = employeeScheduleQueryForm.getPostNames();
    Integer page = employeeScheduleQueryForm.getPage();
    Integer size = employeeScheduleQueryForm.getSize();
    String name = employeeScheduleQueryForm.getName();
    Integer clinicId = employeeScheduleQueryForm.getClinicId();
    Integer formuserId = employeeScheduleQueryForm.getUserId();

    List<ClinicScheduleVO> ClinicSchedules = clinicScheduleBiz.findVOsByClinicId(null);
    Map<String, ClinicScheduleVO> ClinicScheduleMap = new HashMap();
    ClinicSchedules.forEach(x -> ClinicScheduleMap.put(x.getScheduleId() + "", x));
    if (postNames == null || postNames.size() == 0) {
      postNames = null;
    }
    //获取员工信息
    SysUserEmployeeModel model = new SysUserEmployeeModel();
    //查询总数不分页
    model.setWhetherPage(false);
    List<Integer> orgIds = new ArrayList<>();
    //设置门诊ID
    orgIds.add(clinicId);
    model.setOrgIds(orgIds);
    model.setPostIds(postNames);
    model.setKeyWord(name);
    Byte[]userStatus = {0,1,3};
    //离职状态
    model.setWorkStatus(userStatus);
    model.setUserId(formuserId);

    int count = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model).size();
    List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
    //获取门诊信息
    OrganizationModel organizationModel = new OrganizationModel();
    organizationModel.setWhetherPage(false);
    List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
    Map<String, OrganizationInfoDetail> clinicMap = new HashMap(16);
    clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    int days = ((int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24)))+1;
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
        int num = 0;
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
            workDayData.setStime(simtime);
            workDayData.setType(employeeScheduleVO.getType());
            //班次名称
            workDayData.setName( ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getName());
            workDayData.setDate(employeeScheduleVO.getWorkDate());
            workDayData.setCompClinId(employeeScheduleVO.getClinicId());
            num = num+1;
          }
          if(workDayData.getId()!=null){
            workDayDatas.add(workDayData);
          }
        }
        if(num==0){
          WorkDayVO workDayData = new WorkDayVO();
          workDayData.setDate(calendar.getTime());
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
