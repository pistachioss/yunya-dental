package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.FieldInfoQueryForm;
import com.yunya.feign.employee_attend.vo.FieldInfoListVO;
import com.yunya.feign.employee_attend.vo.FieldInfoVO;
import com.yunya.models.employee_attend.FieldInfo;
import com.yunya.modules.employeeattend.form.FieldInfoForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

import java.util.Date;
import java.util.List;

public interface FieldInfoMapper extends Mapper<FieldInfo> {

    List<FieldInfo> findList(FieldInfo fieldInfo);

    List<FieldInfoListVO> findVOList(FieldInfoForm fieldInfoForm);

    /**
     * 根据日期和用户id列表查询外勤列表
     *
     * @param userIds 用户id
     * @param date 日期
     * @return
     */
    List<FieldInfoVO> findFieldInfosByUserIdAndDate(@Param("userIds") List<Integer> userIds, @Param("date") Date date);

    /**
     * 根据查询条件分页查询外勤列表
     * @param queryForm 查询条件
     * @return
     */
    List<FieldInfoVO> findFieldInfoList(@Param("queryForm") FieldInfoQueryForm queryForm);

    /**
     * 判断员工某天是否有外勤申请
     * @return
     */
    int countByDay(FieldInfo fieldInfo);
}
