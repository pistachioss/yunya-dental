package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.ivy_mini.domain.form.recommedForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya365.mini.entity.recommed;
import com.yunya365.mini.mapper.RecommedMapper;
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
 * @date: 2022/5/12
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class RecommedServiceImpl extends BaseBiz<RecommedMapper, recommed> {

    public recommed findList() {
        recommed recommed = new recommed();
        List<recommed> result = mapper.selectAll();
        if(result.size()>0){
            recommed = result.get(0);
        }
        return recommed;
    }

    public void add(recommedForm form) {
        recommed entity = new recommed();
        BeanUtils.copyProperties(form, entity);
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.deleteAll();
        mapper.insertSelective(entity);
    }

}
