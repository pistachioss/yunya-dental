package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yunya.feign.ivy_mini.domain.form.FansPickUpForm;
import com.yunya.feign.ivy_mini.domain.model.FansPickUpModel;
import com.yunya.feign.ivy_mini.domain.vo.FansPickUpVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya365.mini.entity.FansPickUp;
import com.yunya365.mini.entity.FansReceiveAddress;
import com.yunya365.mini.mapper.FansPickUpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Objects;

import static com.yunya365.mini.enums.IvyMiniError.*;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/16
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class FansPickUpServiceImpl extends BaseBiz<FansPickUpMapper, FansPickUp> {

    public void add(FansPickUpModel model) {
        Integer fansId = Integer.valueOf(BaseContextHandler.getUserID());
        FansPickUp fansPickUp = new FansPickUp();
        BeanUtil.copy(model,fansPickUp);
        fansPickUp.setFansId(fansId);
        mapper.insertSelective(fansPickUp);
    }

    public void update(FansPickUpForm form) {
        FansPickUp fansPickUp  = mapper.selectByPrimaryKey(form.getId());
        if (Objects.isNull(fansPickUp)) {
            throw ClientServiceException.wrap(PICKUP_NOT_EXIST);
        }
        FansPickUp fans = new FansPickUp();
        BeanUtil.copy(form,fans);
        mapper.updateByPrimaryKeySelective(fans);
    }

    public List<FansPickUpVO> findList() {
        Integer fansId = Integer.valueOf(BaseContextHandler.getUserID());
        List<FansPickUpVO>list =  mapper.findList(fansId);
        return list;
    }

    public FansPickUp getDefaultAddress(Integer fansId) {
        Example example = new Example(FansPickUp.class);
        example.createCriteria().andEqualTo("fansId", fansId)
                .andEqualTo("defaultStatus", 1);
        return mapper.selectOneByExample(example);
    }
}
