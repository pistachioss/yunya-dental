package com.yunya.report.ultimate.mapper;

import com.yunya.models.report.PatientManage;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PatientManageMapper extends Mapper<PatientManage> {
    /**
     * 批量插入
     * @param list
     */
    void insertList(@Param("list") List<PatientManage> list);

    /**
     * 批量插入
     * @param list
     */
    void updateList(@Param("list") List<PatientManage> list);

    Integer countTable();
}