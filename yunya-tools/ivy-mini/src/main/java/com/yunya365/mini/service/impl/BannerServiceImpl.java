package com.yunya365.mini.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.BannerAddForm;
import com.yunya.feign.ivy_mini.domain.vo.BannerVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya365.mini.entity.Banner;
import com.yunya365.mini.mapper.BannerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/13
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BannerServiceImpl extends BaseBiz<BannerMapper, Banner> {

    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;

    public List<BannerVO> findList() {
        List<BannerVO> result = mapper.findBannerList();
        List<BaseOralTariff> botList = remoteTreatmentServiceFeign.findBaseOralTariffList(new BaseOralTariff());
        Map<String, String> cliListsMap = new HashMap(16);
        botList.forEach(z -> cliListsMap.put(z.getId() + "", z.getName()));
        for(BannerVO bannerVO:result){
            if(bannerVO.getLinkType()==1){
                bannerVO.setLinkContext(cliListsMap.get(bannerVO.getProductId()+""));
            }
        }
        return result;
    }

    public void add(BannerAddForm form) {
        Banner entity = new Banner();
        BeanUtils.copyProperties(form, entity);
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
    }

    public ResponseResult update(BannerAddForm form) {
        Integer id = form.getId();
        Banner article = mapper.selectByPrimaryKey(id);
        if (article == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }

        BeanUtils.copyProperties(form, article);

        article.setUpdTime(new Date(System.currentTimeMillis()));

        int result = mapper.updateByPrimaryKey(article);
        if (result <= 0){
            return ResponseUtil.success("数据修改失败！");
        }
        return ResponseUtil.success();
    }
}
