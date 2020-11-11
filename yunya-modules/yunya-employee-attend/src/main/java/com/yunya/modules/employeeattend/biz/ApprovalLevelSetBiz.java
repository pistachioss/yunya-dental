package com.yunya.modules.employeeattend.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.ApprovalLevelSet;
import com.yunya.modules.employeeattend.form.ApprovalLevelSetQuery;
import com.yunya.modules.employeeattend.mapper.ApprovalLevelSetMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.SAME_DATA_EXIST;

/**
 * 简介:审批人级别设置
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
public class ApprovalLevelSetBiz extends BaseBiz<ApprovalLevelSetMapper, ApprovalLevelSet> {

    public List<ApprovalLevelSet> findList(ApprovalLevelSetQuery approvalLevelSetQuery){
        ApprovalLevelSet approvalLevelSet = new ApprovalLevelSet();
        BeanUtils.copyProperties(approvalLevelSetQuery, approvalLevelSet);
        List<ApprovalLevelSet> re = mapper.select(approvalLevelSet);
        return re;
    }

    public int create(ApprovalLevelSetQuery approvalLevelSetQuery){
        ApprovalLevelSet approvalLevelSet = new ApprovalLevelSet();
        approvalLevelSet.setApprovalCriteriaId(approvalLevelSetQuery.getApprovalCriteriaId());
        approvalLevelSet.setApprovalPriority(approvalLevelSetQuery.getApprovalPriority());
        int num = mapper.selectCount(approvalLevelSet);
        if(num>0){
            throw new ClientServiceException("优先级重复", SAME_DATA_EXIST);
        }
        approvalLevelSet.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        BeanUtils.copyProperties(approvalLevelSetQuery, approvalLevelSet);
        int re = mapper.insert(approvalLevelSet);
        return re;
    }

    public int update(ApprovalLevelSetQuery approvalLevelSetQuery){
        ApprovalLevelSet approvalLevelSet = new ApprovalLevelSet();
        approvalLevelSet.setId(approvalLevelSetQuery.getId());
        approvalLevelSet = mapper.selectOne(approvalLevelSet);
        //判断此次修改是否改动优先级，若改动则判断是否与其他优先级重复
        if(!approvalLevelSet.getApprovalPriority().equals(approvalLevelSetQuery.getApprovalPriority())){
            approvalLevelSet = new ApprovalLevelSet();
            approvalLevelSet.setApprovalCriteriaId(approvalLevelSetQuery.getApprovalCriteriaId());
            approvalLevelSet.setApprovalPriority(approvalLevelSetQuery.getApprovalPriority());
            int num = mapper.selectCount(approvalLevelSet);
            if(num>0){
                throw new ClientServiceException("优先级重复", SAME_DATA_EXIST);
            }
        }
        approvalLevelSet.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        BeanUtils.copyProperties(approvalLevelSetQuery, approvalLevelSet);
        int re = mapper.updateByPrimaryKey(approvalLevelSet);
        return re;
    }

    public int delete(ApprovalLevelSetQuery approvalLevelSetQuery){
        ApprovalLevelSet approvalLevelSet = new ApprovalLevelSet();
        BeanUtils.copyProperties(approvalLevelSetQuery, approvalLevelSet);
        int re = mapper.delete(approvalLevelSet);
        return re;
    }
}
