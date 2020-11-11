package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.employee_attend.VacationSet;
import com.yunya.modules.employeeattend.form.VacationDeleteForm;
import com.yunya.modules.employeeattend.form.VacationSetForm;
import com.yunya.modules.employeeattend.form.VacationSetQuery;
import com.yunya.modules.employeeattend.mapper.VacationSetMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DELETE_NOT_ALLOW;

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
public class VacationSetBiz extends BaseBiz<VacationSetMapper, VacationSet> {

    public PageInfo<VacationSet> findlist(VacationSetQuery vacationSetQuery){
        if (vacationSetQuery.getWhetherPage()) {
            PageHelper.startPage(vacationSetQuery.getPage(), vacationSetQuery.getSize());
        }
        List<VacationSet> reList = mapper.selectAll();
        return new PageInfo<>(reList);
    }

    public int create(VacationSetForm vacationSetForm){
        VacationSet vacationSet = new VacationSet();
        BeanUtils.copyProperties(vacationSetForm, vacationSet);
        vacationSet.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        int re = mapper.insertSelective(vacationSet);
        return re;
    }

    public int update(VacationSetForm vacationSetForm){
        VacationSet vacationSet = new VacationSet();
        BeanUtils.copyProperties(vacationSetForm, vacationSet);
        vacationSet.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
        vacationSet.setUpdTime(new Date());
        int re = mapper.updateByPrimaryKeySelective(vacationSet);
        return re;
    }

    public int delete(VacationDeleteForm vacationDeleteForm){
        //判断该假期是否已经有请假信息
        if(true){
            VacationSet vacationSet = new VacationSet();
            BeanUtils.copyProperties(vacationDeleteForm, vacationSet);
            int re = mapper.delete(vacationSet);
            return re;
        }
        throw new ClientServiceException("该假期已被使用，不允许被删除", DELETE_NOT_ALLOW);
    }
}
