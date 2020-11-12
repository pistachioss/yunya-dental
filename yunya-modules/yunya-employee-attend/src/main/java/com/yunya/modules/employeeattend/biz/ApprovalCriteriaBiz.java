package com.yunya.modules.employeeattend.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.employee_attend.ApprovalCriteria;
import com.yunya.modules.employeeattend.form.ApprovalCriteriaForm;
import com.yunya.modules.employeeattend.mapper.ApprovalCriteriaMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    public int create(ApprovalCriteriaForm approvalCriteriaForm){
        ApprovalCriteria approvalCriteria = new ApprovalCriteria();
        BeanUtils.copyProperties(approvalCriteriaForm, approvalCriteria);
        approvalCriteria.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        int re = mapper.insertSelective(approvalCriteria);
        return re;
    }

    public int update(ApprovalCriteriaForm approvalCriteriaForm){
        ApprovalCriteria approvalCriteria = new ApprovalCriteria();
        BeanUtils.copyProperties(approvalCriteriaForm, approvalCriteria);
        approvalCriteria.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        approvalCriteria.setUpdTime(new Date());
        int re = mapper.updateByPrimaryKeySelective(approvalCriteria);
        return re;
    }

    public int delete(ApprovalCriteriaForm approvalCriteriaForm){
        ApprovalCriteria approvalCriteria = new ApprovalCriteria();
        BeanUtils.copyProperties(approvalCriteriaForm, approvalCriteria);
        int re = mapper.delete(approvalCriteria);
        return re;
    }
}
