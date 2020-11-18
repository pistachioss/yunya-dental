package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoListVO;
import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.models.employee_attend.WorkOvertimeInfo;
import com.yunya.modules.employeeattend.form.WorkOvertimeInfoForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

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
}