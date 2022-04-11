package com.yunya.modules.employeeattend.mapper;

import com.yunya.feign.employee_attend.form.EmployeePushMessageRecordQueryForm;
import com.yunya.feign.employee_attend.vo.EmpPushMsgUnReadCountVO;
import com.yunya.feign.employee_attend.vo.EmployeePushMessageRecordVO;
import com.yunya.models.employee_attend.EmployeePushMessageRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface EmployeePushMessageRecordMapper extends Mapper<EmployeePushMessageRecord> {

    /**
     * 条件查询消息推送记录（查询时间之前的）
     *
     * @param query
     * @return
     */
    List<EmployeePushMessageRecordVO> selectPushMessageRecordList(@Param("query") EmployeePushMessageRecordQueryForm query);

    /**
     * 未读消息数量
     *
     * @param query
     * @return
     */
    EmpPushMsgUnReadCountVO selectCountUnRead(@Param("query") EmployeePushMessageRecordQueryForm query);
}