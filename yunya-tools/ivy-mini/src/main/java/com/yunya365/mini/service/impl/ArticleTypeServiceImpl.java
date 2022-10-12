package com.yunya365.mini.service.impl;

import com.yunya.feign.ivy_mini.domain.form.ArticleTypeForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleTypeVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.ArticleType;
import com.yunya365.mini.mapper.ArticleTypeMapper;
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
 * @date: 2022/10/12
 * @description:
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleTypeServiceImpl extends BaseBiz<ArticleTypeMapper, ArticleType> {

    public List<ArticleTypeVO> findList(ArticleTypeForm form) {
        List<ArticleTypeVO> result = mapper.findArticleTypeList(form);
        return result;
    }
    public void add(ArticleTypeForm form) {
        ArticleType entity = new ArticleType();
        BeanUtils.copyProperties(form, entity);
        //测试用，发布切换
//        entity.setCrtId(1);
        entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
    }

    public ResponseResult update(ArticleTypeForm form) {
        Integer id = form.getId();
        ArticleType article = mapper.selectByPrimaryKey(id);
        if (article == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }

        BeanUtils.copyProperties(form, article);
        article.setUptId(Integer.valueOf(BaseContextHandler.getUserID()));
        //测试用，发布切换
//        article.setUptId(1);
        article.setUpdTime(new Date(System.currentTimeMillis()));

        int result = mapper.updateByPrimaryKey(article);
        if (result <= 0){
            return ResponseUtil.success("数据修改失败！");
        }
        return ResponseUtil.success();
    }

    public ResponseResult delete(Integer id) {
        ArticleType article = mapper.selectByPrimaryKey(id);
        if (article == null) {
            return ResponseUtil.success("删除的记录不存在！");
        }
        int result = mapper.delete(article);
        if (result <= 0){
            return ResponseUtil.success("数据删除失败！");
        }
        return ResponseUtil.success();
    }

}
