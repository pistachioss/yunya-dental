package com.yunya.modules.employeeattend.mapper;



import com.yunya.models.employee_attend.AttendanceAddressSet;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.EmployeeSchedule;

import com.yunya.modules.employeeattend.form.EmployeeScheduleDeleteForm;
import com.yunya.modules.employeeattend.form.EmployeeScheduleQueryForm;
import com.yunya.feign.employee_attend.vo.BaseEmployeeScheduleVO;
import com.yunya.modules.employeeattend.vo.EmListVO;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleCopyVO;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface EmployeeScheduleMapper extends Mapper<EmployeeSchedule> {

  List<AttendanceAddressSet> findAddress();

  /**
   * 按照时间范围获取排班表
   *
   * @param startDate
   * @param endDate
   * @return
   */
  List<EmployeeScheduleCopyVO> selectAllByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                               @Param("list") List<Integer> employeeIdList);

  /**
   * 删除列表
   *
   * @param startDate
   * @param endDate
   * @param employeeIdList
   */
  void deleteByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                    @Param("list") List<Integer> employeeIdList);

  /**
   * 批量添加
   *
   * @param EmployeeSchedules
   */
  void batchInsert(List<EmployeeScheduleCopyVO> EmployeeSchedules);

  /**
   * 根据排班日期和员工ID获取列表
   *
   * @param employeeId
   * @param workDate
   * @return
   */
  List<EmployeeSchedule> selectByDateAndComEmpId(@Param("employeeId") String employeeId, @Param("workDate") Date workDate);

  /**
   * 根据时间段获取排班
   * @param startDate
   * @param endDate
   * @param clinicId
   * @param employeeId
   * @return
   */
  List<EmployeeScheduleVO> selectVOByDateAndCompEmpId(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("clinicId") Date clinicId, @Param("employeeId") String employeeId);

  /**
   * 根据工作日期和员工id查询员工排班信息。
   * @param employeeIds
   * @param workDates
   * @return
   */
  List<EmployeeScheduleVO> selectEmployeeScheduleVOByDateAndCompEmpId(@Param("employeeIds") List<Integer> employeeIds, @Param("workDates") List<Date> workDates);

  /**
   * 根据排班id获取班次模板信息
   * @param id 排班id
   * @return
   */
  BaseSchedule selectBaseScheduleById(@Param("id") Integer id);
  /**
   * 获取当天的全部排班
   * @param employeeSchedule
   * @return
   */
  List<EmListVO> findemList(EmployeeSchedule employeeSchedule);

  /**
   * 根据主键id列表查询员工排班信息
   *
   * @param ids
   * @return
   */
  List<EmployeeScheduleVO> selectInIds(@Param("ids") List<Integer> ids);

  /**
   * 根据条件进行分页查询
   *
   * @param queryForm 查询参数
   * @return
   */
  List<EmployeeScheduleVO> findEmployeeScheduleList(@Param("queryForm")EmployeeScheduleQueryForm queryForm);

  EmployeeScheduleVO selectByCondition(EmployeeScheduleDeleteForm employeeScheduleDeleteForm);

  Integer selectApprovalCount(EmployeeScheduleVO employeeScheduleVO);

  BaseEmployeeScheduleVO findEmInfoById(@Param("id") Integer Id);
}