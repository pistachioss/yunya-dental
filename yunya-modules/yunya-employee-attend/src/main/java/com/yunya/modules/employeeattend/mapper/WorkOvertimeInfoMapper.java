package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.WorkOvertimeInfoQueryForm;
import com.yunya.feign.employee_attend.vo.AttendanceWorkOvertimeMinuteVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoListVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.feign.employee_attend.vo.findNoWorkEmByDateVO;
import com.yunya.models.employee_attend.BaseSchedule;
import com.yunya.models.employee_attend.WorkOvertimeInfo;
import com.yunya.modules.employeeattend.form.NoWorkByDateForm;
import com.yunya.modules.employeeattend.form.NoWorkForm;
import com.yunya.modules.employeeattend.form.WorkForm;
import com.yunya.modules.employeeattend.form.WorkOvertimeInfoForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface WorkOvertimeInfoMapper extends Mapper<WorkOvertimeInfo> {
    /**
     * 根据日期和用户id列表查询加班列表
     *
     * @param userIds 用户id
     * @param date 日期
     * @return
     */
    List<WorkOvertimeInfoVO> findWorkOvertimeInfoByUserIdsAndDate(@Param("userIds") List<Integer> userIds, @Param("date") Date date);

    List<WorkOvertimeInfoListVO> findList(WorkOvertimeInfoForm workOvertimeInfoForm);

    List<BaseSchedule> findWorkEm(WorkForm workForm);

    Set<String> findNoWorkEm(NoWorkForm NoWorkForm);

    List<findNoWorkEmByDateVO> findNoWorkEmByDate(NoWorkByDateForm noWorkByDateForm);

    int selectCountById(WorkOvertimeInfo workOvertimeInfo);
    /**
     * 判断员工某天是否有加班申请
     * @return
     */
    int countByDay(WorkOvertimeInfo workOvertimeInfo);
    /**
     * 分页条件查询
     * @param queryForm 查询参数
     * @return
     */
    List<WorkOvertimeInfoVO> findWorkOvertimeInfoList(@Param("queryForm") WorkOvertimeInfoQueryForm queryForm);

    List<AttendanceWorkOvertimeMinuteVO> statisticsWorkOvertimesByMinute(@Param("queryForm") WorkOvertimeInfoQueryForm queryForm);
}