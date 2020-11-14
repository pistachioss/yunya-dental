package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.employee_attend.form.FieldInfoQueryForm;
import com.yunya.feign.employee_attend.vo.FieldInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.employee_attend.FieldInfo;
import com.yunya.modules.employeeattend.mapper.FieldInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简介：外勤信息业务层
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/11/10 20:52
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FieldInfoBiz extends BaseBiz<FieldInfoMapper, FieldInfo> {

    /**
     * 根据日期和用户id列表查询外勤列表
     *
     * @param userIds 用户id
     * @param date 日期
     * @return
     */
    public List<FieldInfoVO> findFieldInfosByUserIdAndDate(List<Integer> userIds, Date date) {
        return mapper.findFieldInfosByUserIdAndDate(userIds,date);
    }

    /**
     * 根据查询条件分页查询外勤列表
     * @param queryForm 查询条件
     * @return
     */
    public List<FieldInfoVO> findFieldInfoList(FieldInfoQueryForm queryForm) {
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(),queryForm.getPageSize());
        }
        return mapper.findFieldInfoList(queryForm);
    }
}
