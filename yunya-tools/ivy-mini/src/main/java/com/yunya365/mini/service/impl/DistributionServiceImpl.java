package com.yunya365.mini.service.impl;

import com.yunya.feign.ivy_mini.domain.form.DistributionForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya365.mini.entity.Distribution;
import com.yunya365.mini.mapper.DistributionMapper;
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
 * @date: 2022/5/16
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DistributionServiceImpl extends BaseBiz<DistributionMapper, Distribution> {
    public Distribution findList() {
        Distribution distribution = new Distribution();
        List<Distribution> result = mapper.selectAll();
        if(result.size()>0){
            distribution = result.get(0);
        }
        return distribution;
    }

    public void add(DistributionForm form) {
        Distribution entity = new Distribution();
        BeanUtils.copyProperties(form, entity);
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.deleteAll();
        mapper.insertSelective(entity);
    }

}
