package com.yunya.modules.employeeattend.biz;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


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
import com.yunya.framework.common.utils.EntityUtils;

import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.employee_attend.EmployeeSchedule;
import com.yunya.modules.employeeattend.form.EmployeeScheduleCopyForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.modules.employeeattend.mapper.EmployeeScheduleMapper;
import com.yunya.modules.employeeattend.vo.ClinicScheduleVO;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleCopyVO;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleExportVO;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述:
 *
 * @author 杨柳絮
 * @create 2020-06-12 11:26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmployeeScheduleBiz extends BaseBiz<EmployeeScheduleMapper, EmployeeSchedule> {
    private static final int SHIFT_DAYS = 14;
    private static final int COUNT = 1;
    @Autowired
    private ClinicScheduleBiz clinicScheduleBiz;
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;

    /**
     * 添加排班表
     *
     * @param employeeScheduleForm
     */
    public void create(EmployeeScheduleForm employeeScheduleForm) {
        // 判断排班是否冲突
        if (!isExist(employeeScheduleForm)) {
            throw new ClientServiceException("排班冲突", OperationCodeConstants.SAME_DATA_EXIST);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        EmployeeSchedule employeeSchedule = EntityUtils.build(employeeScheduleForm, EmployeeSchedule.class);
        employeeSchedule.setEmployeeId(Integer.valueOf(employeeScheduleForm.getUserId()));
        employeeSchedule.setClinicId(employeeScheduleForm.getClinicId());
        employeeSchedule.setScheduleId(Integer.valueOf(employeeScheduleForm.getScheduleId()));
        try {
            employeeSchedule.setWorkDate(simpleDateFormat.parse(employeeScheduleForm.getWorkDate()));
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
        mapper.insertSelective(employeeSchedule);
    }

    /**
     * 复制排班表
     * @param employeeScheduleCopyForm
     * @return
     */
    public List<EmployeeScheduleExportVO> copy(EmployeeScheduleCopyForm employeeScheduleCopyForm) {
        //冲突列表
        List<EmployeeScheduleExportVO> employeeConflict = new ArrayList<>();
        //复制排班的员工Id列表
        List<Integer> employeeIdList = employeeScheduleCopyForm.getEmployeeIdLIst();
        //获取员工信息
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        //查询总数不分页
        model.setWhetherPage(false);
        List<Integer> orgIds = new ArrayList<>();
        //设置门诊ID
        orgIds.add(Integer.valueOf(employeeScheduleCopyForm.getClinicId()));
        model.setOrgIds(orgIds);
        Byte[] userStatus = {0, 1, 3};
        //离职状态
        model.setWorkStatus(userStatus);
        //当前门诊下全部员工
        List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        Map<String, SysUserInfoDetail> employeeMap = new HashMap();
        employees.forEach(z -> employeeMap.put(z.getUserId() + "", z));
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        //全部门诊信息
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));
        //注意月份是MM
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        Date targetStartDate = null;
        try {
            startDate = simpleDateFormat.parse(employeeScheduleCopyForm.getStartDate());
            endDate = simpleDateFormat.parse(employeeScheduleCopyForm.getEndDate());
            targetStartDate = simpleDateFormat.parse(employeeScheduleCopyForm.getTargetStartDate());
        } catch (Exception e) {
            throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
        // 目标时间与开始时间的天数差
        Long a = targetStartDate.getTime();
        Long b = startDate.getTime();
        Long c = a - b;
        int d = c.intValue();
        int days1 = (int) ((targetStartDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24));
        // 时间范围
        int days2 = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24));
        //复制时段内的排班列表
        List<EmployeeScheduleCopyVO> EmployeeSchedules = mapper.selectAllByDate(startDate, endDate, employeeIdList);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetStartDate);
        calendar.add(Calendar.DATE, days2);
        //目标时段的结束时间
        Date targetEndDate = calendar.getTime();
        //目标时段内的排班列表
        List<EmployeeScheduleCopyVO> CopyEmployeeSchedules = mapper.selectAllByDate(targetStartDate, targetEndDate, employeeIdList);
        //根据员工ID对复制时段内的排班列表进行分组
        Map<Integer, List<EmployeeScheduleCopyVO>> EmployeeScheduleMap =
                EmployeeSchedules.stream().collect((Collectors.groupingBy(EmployeeScheduleCopyVO -> EmployeeScheduleCopyVO.getEmployeeId())));
        //根据员工ID对目标时段内的排班列表进行分组
        Map<Integer, List<EmployeeScheduleCopyVO>> CopyEmployeeSchedulesMap =
                CopyEmployeeSchedules.stream().collect((Collectors.groupingBy(EmployeeScheduleCopyVO -> EmployeeScheduleCopyVO.getEmployeeId())));
        //遍历根据ID分组后的map
        for (List<EmployeeScheduleCopyVO> forEmplist : EmployeeScheduleMap.values()) {
            //取出与当前遍历的员工id相同的目标排班的集合
            List<EmployeeScheduleCopyVO> copyList = CopyEmployeeSchedulesMap.get(forEmplist.get(0).getEmployeeId());
            //遍历复制排班的 当前遍历的员工ID下的排班信息
            for (EmployeeScheduleCopyVO employeeScheduleCopyVO : forEmplist) {
                //目标排班时间段不为空
                if (copyList != null) {
                    //遍历当前员工ID的目标排班 进行比对
                    for (EmployeeScheduleCopyVO copyVO : copyList) {
                        //设置复制排班与目标排班的日期对应
                        Calendar calendarCopy = Calendar.getInstance();
                        calendarCopy.setTime(employeeScheduleCopyVO.getWorkDate());
                        calendarCopy.add(Calendar.DATE, days1);
                        //日期相同的排班取出 比对时间段
                        if (calendarCopy.getTime().getTime() == copyVO.getWorkDate().getTime()) {
                            //判断是否启用第二个时间段
                            if (employeeScheduleCopyVO.getSecondEndTime() == null) {
                                employeeScheduleCopyVO.setSecondEndTime(employeeScheduleCopyVO.getFirstEndTime());
                            }
                            //判断是否启用第二个时间段
                            if (copyVO.getSecondEndTime() == null) {
                                copyVO.setSecondEndTime(copyVO.getFirstEndTime());
                            }
                            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                            //获取时间段的时分秒格式进行比较
                            Date copyVoEndTime = null;
                            Date copyVoStratTime = null;
                            Date EmpEndTime = null;
                            Date EmpStartTime = null;
                            try {
                                copyVoEndTime = dateFormat.parse(dateFormat.format(copyVO.getSecondEndTime()));
                                copyVoStratTime = dateFormat.parse(dateFormat.format(copyVO.getFirstStartTime()));
                                EmpEndTime = dateFormat.parse(dateFormat.format(employeeScheduleCopyVO.getSecondEndTime()));
                                EmpStartTime = dateFormat.parse(dateFormat.format(employeeScheduleCopyVO.getFirstStartTime()));
                            } catch (ParseException e) {
                                throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
                            }

                            if (copyVoEndTime.before(EmpStartTime) || EmpEndTime.before(copyVoStratTime)) {

                            } else {
                                SimpleDateFormat dateFormatExport = new SimpleDateFormat("yyyy-MM-dd");
                                EmployeeScheduleExportVO employeeScheduleExportVO = new EmployeeScheduleExportVO();
                                employeeScheduleExportVO.setName(employeeMap.get(employeeScheduleCopyVO.getEmployeeId() + "").getName());
                                employeeScheduleExportVO.setCopy_date(dateFormatExport.format(employeeScheduleCopyVO.getWorkDate()));
                                employeeScheduleExportVO.setCopy_company_name(clinicMap.get(employeeScheduleCopyVO.getClinicId() + "").getAbbreviation());
                                employeeScheduleExportVO.setCopy_schedule(employeeScheduleCopyVO.getScheduleName() + "(" + dateFormat.format(EmpStartTime) + "-" + dateFormat.format(EmpEndTime) + ")");

                                employeeScheduleExportVO.setCover_date(dateFormatExport.format(copyVO.getWorkDate()));
                                employeeScheduleExportVO.setCover_company_name(clinicMap.get(copyVO.getClinicId() + "").getAbbreviation());
                                employeeScheduleExportVO.setCover_schedule(copyVO.getScheduleName() + "(" + dateFormat.format(copyVoStratTime) + "-" + dateFormat.format(copyVoEndTime) + ")");
                                employeeConflict.add(employeeScheduleExportVO);
                            }
                        }
                    }
                }
            }
        }
        // 删除原有日期排班,插入目标范围排班
        if (!EmployeeSchedules.isEmpty() && employeeConflict.size() == 0) {
            Calendar calendar2 = Calendar.getInstance();
            EmployeeSchedules.forEach(x -> {
                calendar2.setTime(x.getWorkDate());
                calendar2.add(Calendar.DATE, days1);
                x.setId(null);
                x.setWorkDate(calendar2.getTime());
            });
            mapper.batchInsert(EmployeeSchedules);
            return employeeConflict;
        }
        return employeeConflict;
    }

    /**
     * 获取排班表列表
     *
     * @param employeeScheduleQueryForm
     * @return
     */
    public Map<String, Object> findList(EmployeeScheduleQueryForm employeeScheduleQueryForm) {
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
//      endDate = new Date(startDate.getTime() + 14 * 24 * 60 * 60 * 1000);
        }

        // 请求参数
        List<Integer> postNames = employeeScheduleQueryForm.getPostNames();
        Integer page = employeeScheduleQueryForm.getPage();
        Integer size = employeeScheduleQueryForm.getSize();
        String name = employeeScheduleQueryForm.getName();
        Integer clinicId = employeeScheduleQueryForm.getClinicId();
        //获取班次信息
        List<ClinicScheduleVO> ClinicSchedules = clinicScheduleBiz.findVOsByClinicId(null);
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
        Byte[] userStatus = {0, 1, 3};

        model.setWorkStatus(userStatus);//离职状态
        int count = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model).size();
        model.setWhetherPage(false);
//    model.setPageNum(page);
//    model.setPageSize(size);
        List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int days = ((int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24))) + 1;
        JSONArray shiftWorkDatas = new JSONArray();
        for (SysUserInfoDetail baseEmployee : employees) {
            Map<String, Object> userWorkMap = new LinkedHashMap<>();
            String userId = baseEmployee.getUserId() + "";
            userWorkMap.put("compEmpId", userId);
            userWorkMap.put("name", baseEmployee.getName());
            userWorkMap.put("postNames", baseEmployee.getPosts());
            // 获取时间内的排班
            List<EmployeeScheduleVO> EmployeeScheduleVOs = mapper.selectVOByDateAndCompEmpId(startDate, endDate, null, userId);
            // 设置排班列表
            List personDays = new LinkedList();
            for (int i = 0; i < days; i++) {
                calendar.getTime();
                JSONArray workDayDatas = new JSONArray();
                for (EmployeeScheduleVO employeeScheduleVO : EmployeeScheduleVOs) {
                    if (calendar.getTime().equals(employeeScheduleVO.getWorkDate())) {
                        JSONObject workDayData = new JSONObject();
                        workDayData.put("id", employeeScheduleVO.getScheduleId());
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

                        workDayData.put("companyType", clinicMap.get(employeeScheduleVO.getClinicId() + "").getType());
                        workDayData.put("companyName", clinicMap.get(employeeScheduleVO.getClinicId() + "").getAbbreviation());
                        workDayData.put("employeeName", ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getName());
                        workDayData.put("color", ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getColor());
                        workDayData.put("simtime", simtime);
                        workDayData.put("date", employeeScheduleVO.getWorkDate());
                        workDayData.put("compClinId", employeeScheduleVO.getClinicId());
                        workDayDatas.add(workDayData);
                    }
                }
                personDays.add(workDayDatas);
                calendar.add(Calendar.DATE, +COUNT);
            }
            userWorkMap.put("days", personDays);
            shiftWorkDatas.add(userWorkMap);
            calendar.add(Calendar.DATE, -days);
        }
        Map<String, Object> result = new HashMap();
        result.put("data", shiftWorkDatas);
        result.put("total", count);
        return result;
    }

    /**
     * 判断用户是否在该时段有排班冲突
     *
     * @param employeeScheduleForm
     * @return
     */
    private boolean isExist(EmployeeScheduleForm employeeScheduleForm) {
        boolean flag = false;
        // 获取门诊排班列表 （获取开始和结束时间）
        List<ClinicScheduleVO> ClinicSchedules = clinicScheduleBiz.findVOsByClinicId(null);

        Map<String, ClinicScheduleVO> clinicScheduleMap = new HashMap();
        ClinicSchedules.forEach(x -> clinicScheduleMap.put(x.getScheduleId() + "", x));
        // 根据排班表ID获取排班开始时间和结束时间
        String shiftId = employeeScheduleForm.getScheduleId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        Date startTime = null;
        Date endTime = null;
        try {
            startTime = dateFormat.parse(dateFormat.format(clinicScheduleMap.get(shiftId).getFirstStartTime()));
            endTime = new Date();
            if (clinicScheduleMap.get(shiftId).getSecondEndTime() != null) {
                endTime = dateFormat.parse(dateFormat.format(clinicScheduleMap.get(shiftId).getSecondEndTime()));
            } else {
                endTime = dateFormat.parse(dateFormat.format(clinicScheduleMap.get(shiftId).getFirstEndTime()));
            }
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
        EmployeeSchedule employeeSchedule = new EmployeeSchedule();
        employeeSchedule.setEmployeeId(Integer.valueOf(employeeScheduleForm.getUserId()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
        Date date = null;
        try {
            date = simpleDateFormat.parse(employeeScheduleForm.getWorkDate());
        } catch (ParseException e) {
            throw new ClientServiceException("时间转换错误", OperationCodeConstants.DATA_TRANSFORMATION_EXIST);
        }
        employeeSchedule.setWorkDate(date);
        // 获取排班信息,判断跟其他门诊以及当前门诊的排班是否冲突
        List<EmployeeSchedule> employeeSchedules =
                mapper.selectByDateAndComEmpId(employeeScheduleForm.getUserId(), date);
        if (!employeeSchedules.isEmpty()) {
            for (int d = 0; d < employeeSchedules.size(); d++) {
                EmployeeSchedule data = employeeSchedules.get(d);
                Integer dataShiftId = data.getScheduleId();
                // 获取当前数据排班的开始时间和结束时间
                ClinicScheduleVO oldShift = clinicScheduleMap.get(dataShiftId + "");
                if (oldShift.getSecondEndTime() != null) {
                    if (endTime.before(oldShift.getFirstStartTime()) ||
                            endTime.equals(oldShift.getFirstStartTime()) ||
                            startTime.equals(oldShift.getSecondEndTime()) ||
                            startTime.after(oldShift.getSecondEndTime())) {
                        flag = true;
                    } else {
                        flag = false;
                        return flag;
                    }
                } else {
                    if (endTime.before(oldShift.getFirstStartTime()) ||
                            endTime.equals(oldShift.getFirstStartTime()) ||
                            startTime.equals(oldShift.getSecondEndTime()) ||
                            startTime.after(oldShift.getFirstEndTime())) {
                        flag = true;
                    } else {
                        flag = false;
                        return flag;
                    }
                }
            }
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 导出
     *
     * @param response
     * @param employeeScheduleQueryForm
     * @throws Exception
     */
    public void export(HttpServletResponse response, EmployeeScheduleQueryForm employeeScheduleQueryForm) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
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
//      endDate = new Date(startDate.getTime() + 14 * 24 * 60 * 60 * 1000);
        }
        Integer clinicId = employeeScheduleQueryForm.getClinicId();

        //获取班次信息
        List<ClinicScheduleVO> ClinicSchedules = clinicScheduleBiz.findVOsByClinicId(null);
        Map<String, ClinicScheduleVO> ClinicScheduleMap = new HashMap();
        ClinicSchedules.forEach(x -> ClinicScheduleMap.put(x.getScheduleId() + "", x));
        //获取员工信息
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(false);//查询总数不分页
        List<Integer> orgIds = new ArrayList<>();
        orgIds.add(clinicId);//设置门诊ID
        model.setOrgIds(orgIds);
        Byte[] userStatus = {0, 1, 3};
        model.setWorkStatus(userStatus);//离职状态
        List<SysUserInfoDetail> employees = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);
        //获取门诊信息
        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setWhetherPage(false);
        List<OrganizationInfoDetail> clinics = remoteSystemServiceFeign.findOrgInfoList(organizationModel);
        Map<String, OrganizationInfoDetail> clinicMap = new HashMap();
        clinics.forEach(z -> clinicMap.put(z.getId() + "", z));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int days = ((int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24))) + 1;
        List<List<Object>> shiftWorkDatas = new ArrayList();
        for (SysUserInfoDetail baseEmployee : employees) {
            List<Object> row = new ArrayList<>();
            row.add(baseEmployee.getPosts());
            row.add(baseEmployee.getName());
            String userId = baseEmployee.getUserId() + "";
            // 获取时间内的排班
            List<EmployeeScheduleVO> EmployeeScheduleVOs = mapper.selectVOByDateAndCompEmpId(startDate, endDate, null, userId);

            for (int i = 0; i < days; i++) {
                String add = " ";
                for (EmployeeScheduleVO employeeScheduleVO : EmployeeScheduleVOs) {

                    if (calendar.getTime().equals(employeeScheduleVO.getWorkDate())) {
                        //拼接排班的时间段
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        Date startTime = ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getFirstStartTime();
                        Date endTime = new Date();
                        if (ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getSecondEndTime() != null) {
                            endTime = ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getSecondEndTime();
                        } else {
                            endTime = ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getFirstEndTime();
                        }
                        String simtime = dateFormat.format(startTime) + "~" + dateFormat.format(endTime);

                        add = add + clinicMap.get(employeeScheduleVO.getClinicId() + "").getAbbreviation() + "-" + ClinicScheduleMap.get(employeeScheduleVO.getScheduleId() + "").getName() + "-" + simtime + "\n";
                    }
                }
                row.add(add);
                calendar.add(Calendar.DATE, +COUNT);
            }
            shiftWorkDatas.add(row);
            calendar.add(Calendar.DATE, -days);
        }

        // 岗位+员工+排班
        List<String> heads = new ArrayList<>();
        heads.add("岗位");
        heads.add("员工");

        // 设置宽度
        List<Integer> columnWidths = new ArrayList();
        columnWidths.add(15);
        columnWidths.add(20);
        for (int i = 0; i < days; i++) {
            heads.add(simpleDateFormat.format(calendar.getTime()));
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + COUNT);
            columnWidths.add(20);
        }

        String fileName = "门诊排班表";
        try {
            this.simpleWrite(response, shiftWorkDatas, heads, fileName);
            System.out.println("导出成功");
        } catch (Exception e) {
            System.out.println("导出失败");
            e.printStackTrace();
        }

    }

    /**
     * 导出员工排班列表
     *
     * @param response
     * @param employeeConflict
     * @throws Exception
     */
    public void exportConflict(HttpServletResponse response, List<EmployeeScheduleExportVO> employeeConflict) throws Exception {
        ExcelUtil<EmployeeScheduleExportVO> excelUtil = new ExcelUtil<>(EmployeeScheduleExportVO.class);
        excelUtil.exportExcel(response, employeeConflict, "员工排班冲突列表");
    }

    /**
     * 输出方法
     *
     * @param response
     * @param list
     * @param headList
     * @param fileName
     */
    public void simpleWrite(HttpServletResponse response, List<List<Object>> list, List<String> headList, String fileName) {
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExcelWriter excelWriter = null;
        try {
            // 表单
            excelWriter = new ExcelWriter(getOutputStream(fileName, response), ExcelTypeEnum.XLSX);
            Sheet sheet = new Sheet(1, 0);
            sheet.setSheetName("第一个Sheet");
            // 创建一个表格
            Table table = new Table(1);
            List<List<String>> headLists = new ArrayList<List<String>>();
            for (String head : headList) {
                List<String> head1 = new ArrayList<>();
                head1.add(head);
                headLists.add(head1);
            }
            table.setHead(headLists);
            excelWriter.write1(list, sheet, table);
            // 记得 释放资源
            out.flush();
            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            excelWriter.finish();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 导出文件时为Writer生成OutputStream
     *
     * @param fileName
     * @param response
     * @return
     */
    private static OutputStream getOutputStream(String fileName, HttpServletResponse response) throws Exception {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            return response.getOutputStream();
        } catch (IOException e) {
            throw new Exception("导出excel表格失败!", e);
        }
    }

}
