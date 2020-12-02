package com.yunya.modules.employeeattend.mapper;

import com.yunya.models.employee_attend.ApprovalPeople;
import com.yunya.modules.employeeattend.form.ApprovalPeopleForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ApprovalPeopleMapper extends Mapper<ApprovalPeople> {

    int findCount(ApprovalPeopleForm approvalPeopleForm);

    int batchInsert(List<ApprovalPeople>list);
}