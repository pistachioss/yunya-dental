package com.yunya.modules.employeeattend.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.employee_attend.ApprovalCriteria;
import com.yunya.models.employee_attend.ApprovalLevelSet;
import com.yunya.modules.employeeattend.form.ApprovalCriteriaByDayForm;
import com.yunya.modules.employeeattend.form.ApprovalCriteriaForm;
import com.yunya.modules.employeeattend.form.ApprovalLevelSetQuery;
import com.yunya.modules.employeeattend.mapper.ApprovalCriteriaMapper;
import com.yunya.modules.employeeattend.mapper.ApprovalLevelSetMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ApprovalCriteriaBiz extends BaseBiz<ApprovalCriteriaMapper, ApprovalCriteria> {

    @Autowired
    private ApprovalLevelSetMapper approvalLevelSetMapper;

    public int create(ApprovalCriteriaForm approvalCriteriaForm) {
        ApprovalCriteria approvalCriteria = new ApprovalCriteria();
        BeanUtils.copyProperties(approvalCriteriaForm, approvalCriteria);
        approvalCriteria.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        int re = mapper.insertSelective(approvalCriteria);
        return re;
    }

    public int update(ApprovalCriteriaForm approvalCriteriaForm) {
        ApprovalCriteria approvalCriteria = new ApprovalCriteria();
        BeanUtils.copyProperties(approvalCriteriaForm, approvalCriteria);
        approvalCriteria.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        approvalCriteria.setUpdTime(new Date());
        int re = mapper.updateByPrimaryKeySelective(approvalCriteria);
        return re;
    }

    public int delete(ApprovalCriteriaForm approvalCriteriaForm) {
        ApprovalCriteria approvalCriteria = new ApprovalCriteria();
        BeanUtils.copyProperties(approvalCriteriaForm, approvalCriteria);
        int re = mapper.delete(approvalCriteria);
        return re;
    }

    public List<ApprovalLevelSet> findByDate(ApprovalCriteriaByDayForm approvalCriteriaByDayForm) {
        Long daysBetween =
                (approvalCriteriaByDayForm.getEndDay().getTime() - approvalCriteriaByDayForm.getStartDay().getTime() + 1000000)
                        / (60 * 60 * 24 * 1000);
        int num = daysBetween.intValue() + 1;
        List<ApprovalCriteria> list = mapper.selectAll();
        ApprovalLevelSet approvalLevelSet = new ApprovalLevelSet();
        for (ApprovalCriteria approvalCriteria : list) {
            if (approvalCriteria.getEndDay() != null) {
                if (num > approvalCriteria.getStartDay() && num <= approvalCriteria.getEndDay()) {
                    approvalLevelSet.setApprovalCriteriaId(approvalCriteria.getId());
                    break;
                }
            } else {
                if (num > approvalCriteria.getStartDay()) {
                    approvalLevelSet.setApprovalCriteriaId(approvalCriteria.getId());
                    break;
                }
            }
        }
        return approvalLevelSetMapper.select(approvalLevelSet);
    }
}
