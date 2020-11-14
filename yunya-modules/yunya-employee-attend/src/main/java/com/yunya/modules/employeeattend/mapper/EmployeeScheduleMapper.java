package com.yunya.modules.employeeattend.mapper;



import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.EmployeeSchedule;

import com.yunya.modules.employeeattend.vo.EmployeeScheduleCopyVO;
import com.yunya.modules.employeeattend.vo.EmployeeScheduleVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface EmployeeScheduleMapper extends Mapper<EmployeeSchedule> {

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
   * 根据日期范围查询指定员工的休息排班信息
   *
   * @param userId
   * @param startDate
   * @param endDate
   * @return
   */
  List<EmployeeScheduleVO> findRestEmployeeScheduleListInDate(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("firstDate") Date endDate);

  /**
   * 根据主键id列表查询员工排班信息
   *
   * @param ids
   * @return
   */
  List<EmployeeScheduleVO> selectInIds(@Param("ids") List<Integer> ids);
}