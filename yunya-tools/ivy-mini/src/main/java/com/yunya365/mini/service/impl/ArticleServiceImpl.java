package com.yunya365.mini.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.AdjustOrderReq;
import com.yunya.feign.ivy_mini.domain.form.ArticleAddForm;
import com.yunya.feign.ivy_mini.domain.form.ArticleForm;
import com.yunya.feign.ivy_mini.domain.form.ArticleUpdateForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.system.SysUserPost;
import com.yunya365.mini.entity.Article;
import com.yunya365.mini.mapper.ArticleMapper;
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
 * @description: 文章管理
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleServiceImpl extends BaseBiz<ArticleMapper, Article> {

    public PageInfo<ArticleVO> findList(ArticleForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<ArticleVO> result = mapper.findArticleList(form);
        return new PageInfo<>(result);
    }

    public void updateList(Integer type,List<ArticleVO>list) {
        mapper.updateAllList(type);
        if(list.size()>0){
            mapper.updateList(list);
        }
    }

    public PageInfo<ArticleVO> findAppList(ArticleForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<ArticleVO> result = mapper.findArticleAppList(form);
        return new PageInfo<>(result);
    }

    public void add(ArticleAddForm form) {
        Article entity = new Article();
        BeanUtils.copyProperties(form, entity);
        //测试用，发布切换
//        entity.setCrtId(1);
        entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
        entity.setSort(0);
        mapper.updateByPrimaryKeySelective(entity);
    }

    public ResponseResult update(ArticleUpdateForm form) {
        Integer id = form.getId();
        Article article = mapper.selectByPrimaryKey(id);
        if (article == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }

        BeanUtils.copyProperties(form, article);
        article.setSort(form.getIsSort());
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

    public ResponseResult addReading(Integer id) {
        Article article = mapper.selectByPrimaryKey(id);
        if (article == null) {
            return ResponseUtil.success("修改的记录不存在！");
        }
        article.setReadingNumber(article.getReadingNumber()+1);
        int result = mapper.updateByPrimaryKey(article);
        if (result <= 0){
            return ResponseUtil.success("数据修改失败！");
        }
        return ResponseUtil.success();
    }
    public ResponseResult delete(Integer id) {
        Article article = mapper.selectByPrimaryKey(id);
        if (article == null) {
            return ResponseUtil.success("删除的记录不存在！");
        }
        int result = mapper.delete(article);
        if (result <= 0){
            return ResponseUtil.success("数据删除失败！");
        }
        return ResponseUtil.success();
    }

    public ResponseResult adjustOrder(AdjustOrderReq orderReq) {
        Article article = mapper.selectByPrimaryKey(orderReq.getId());
        if (article == null) {
            return ResponseUtil.success("移动的记录不存在！");
        }
        // 2、上移
        if(orderReq.getOperaType().equals(1)){
            //查询下一条记录Id和sort
            Integer nextId = mapper.selectNextId(article);
            if(nextId==0){
                return ResponseUtil.success();
            }
            Article nextNotice =  mapper.selectByPrimaryKey(nextId);
            int one = article.getSort();
            //更新下一条记录的sort为当前值
            article.setSort(nextNotice.getSort());
            //更新当前记录的sort为下一条
            nextNotice.setSort(one);
            mapper.updateSortById(article);
            mapper.updateSortById(nextNotice);
        }
        // 3、下移动
        if(orderReq.getOperaType().equals(2)){
            //查询上一条记录Id和sort
            Integer previousId = mapper.selectPreviousId(article);
            if(previousId==0){
                return ResponseUtil.success();
            }
            Article previousNotice =  mapper.selectByPrimaryKey(previousId);
            int one = article.getSort();
            //更新上一条记录的sort为当前值
            article.setId(previousNotice.getSort());
            //更新当前记录的sort为上一条
            previousNotice.setId(one);
            mapper.updateSortById(article);
            mapper.updateSortById(previousNotice);
        }
        return ResponseUtil.success();

    }
}
