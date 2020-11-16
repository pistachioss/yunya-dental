package com.yunya.modules.employeeattend.biz;

import com.yunya.feign.employee_attend.vo.WorkOvertimeInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.employee_attend.WorkOvertimeInfo;
import com.yunya.modules.employeeattend.mapper.WorkOvertimeInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkOvertimeInfoBiz extends BaseBiz<WorkOvertimeInfoMapper, WorkOvertimeInfo> {

    /**
     * 根据日期和用户id列表查询加班列表
     *
     * @param userIds 用户id
     * @param date 日期
     * @return
     */
    public List<WorkOvertimeInfoVO> findWorkOvertimeInfosByUserIdsAndDate(List<Integer> userIds, Date date) {
        return mapper.findWorkOvertimeInfoByUserIdsAndDate(userIds,date);
    }
}
