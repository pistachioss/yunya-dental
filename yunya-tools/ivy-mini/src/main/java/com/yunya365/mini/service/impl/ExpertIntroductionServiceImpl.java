package com.yunya365.mini.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionAddAndUpdateForm;
import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionForm;
import com.yunya.feign.ivy_mini.domain.vo.ExpertIntroductionVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.ExpertIntroduction;
import com.yunya365.mini.mapper.ExpertIntroductionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/10
 * @description: 专家介绍
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ExpertIntroductionServiceImpl extends BaseBiz<ExpertIntroductionMapper, ExpertIntroduction> {


    public PageInfo<ExpertIntroductionVO> findList(ExpertIntroductionForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<ExpertIntroductionVO> result = mapper.findExpertIntroductionList(form);
        return new PageInfo<>(result);
    }
    public void add(ExpertIntroductionAddAndUpdateForm form) {
        ExpertIntroduction entity = new ExpertIntroduction();
        BeanUtils.copyProperties(form, entity);
        //测试用，发布切换
//        entity.setCrtId(1);
        entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
    }

    public ResponseResult update(ExpertIntroductionAddAndUpdateForm form) {
        Integer id = form.getId();
        ExpertIntroduction expertIntroduction = mapper.selectByPrimaryKey(id);
        if (expertIntroduction == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }

        BeanUtils.copyProperties(form, expertIntroduction);
        expertIntroduction.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        //测试用，发布切换
//        expertIntroduction.setUptId(1);
        expertIntroduction.setUpdTime(new Date(System.currentTimeMillis()));

        int result = mapper.updateByPrimaryKeySelective(expertIntroduction);
        if (result <= 0){
            return ResponseUtil.success("数据修改失败！");
        }
        return ResponseUtil.success();
    }
    public ResponseResult delete(Integer id) {
        ExpertIntroduction expertIntroduction = mapper.selectByPrimaryKey(id);
        if (expertIntroduction == null) {
            return ResponseUtil.success("删除的记录不存在！");
        }
        int result = mapper.delete(expertIntroduction);
        if (result <= 0){
            return ResponseUtil.success("数据删除失败！");
        }
        return ResponseUtil.success();
    }
}
