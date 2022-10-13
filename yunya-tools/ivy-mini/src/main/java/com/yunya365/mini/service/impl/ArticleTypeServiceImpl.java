package com.yunya365.mini.service.impl;

import com.yunya.feign.ivy_mini.domain.form.ArticleForm;
import com.yunya.feign.ivy_mini.domain.form.ArticleTypeForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleTypeVO;
import com.yunya.feign.ivy_mini.domain.vo.ArticleVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.entity.ArticleType;
import com.yunya365.mini.entity.ExpertType;
import com.yunya365.mini.mapper.ArticleMapper;
import com.yunya365.mini.mapper.ArticleTypeMapper;
import com.yunya365.mini.mapper.ExpertTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ExpertTypeMapper expertTypeMapper;

    public List<ArticleTypeVO> findList(ArticleTypeForm form) {
        List<ArticleTypeVO> result = mapper.findArticleTypeList(form);
        return result;
    }
    public ResponseResult add(ArticleTypeForm form) {
        ArticleTypeForm findName = new ArticleTypeForm();
        findName.setName(form.getName());
        List<ArticleTypeVO> resuname = mapper.findArticleTypeList(findName);
        if(resuname.size()>0){
            return ResponseUtil.success("名称已存在！");
        }
        ArticleTypeForm findform = new ArticleTypeForm();
        findform.setType(form.getType());
        ArticleType entity = new ArticleType();
        BeanUtils.copyProperties(form, entity);
        List<ArticleTypeVO> result = mapper.findArticleTypeList(findform);
        if(result.size()==0){
            entity.setAcquiesce(1);
        }else{
            entity.setAcquiesce(0);
        }
        //测试用，发布切换
//        entity.setCrtId(1);
        entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
        return ResponseUtil.success();
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
        int type = article.getType();
        ArticleType findform = new ArticleType();
        findform.setType(type);
        findform.setAcquiesce(1);
        findform = mapper.selectOne(findform);
        int findformId = findform.getId();
        if (article == null) {
            return ResponseUtil.success("删除的记录不存在！");
        }
        int result = mapper.delete(article);
        if (result <= 0){
            return ResponseUtil.success("数据删除失败！");
        }
        if(type<3){
            ArticleForm articleForm = new ArticleForm();
            articleForm.setType(type);
            List<ArticleVO>list = articleMapper.findArticleList(articleForm);
            list.forEach(t->{
                t.setTypeId(findformId);
            });
            articleMapper.updateBatch(list);
        }else{
            List<ExpertType>insertlist = new ArrayList<>();

            ExpertType expertType = new ExpertType();
            expertType.setTypeId(id);
            List<ExpertType>list = expertTypeMapper.select(expertType);
            expertTypeMapper.delete(expertType);
            for(ExpertType ext:list){
                ExpertType findall = new ExpertType();
                findall.setExpertId(ext.getExpertId());
                int a = expertTypeMapper.selectCount(findall);
                if(a==0){
                    findall.setTypeId(findformId);
                    insertlist.add(findall);
                }
            }
            if(insertlist.size()>0){
                expertTypeMapper.BatchInsert(insertlist);
            }
        }

        return ResponseUtil.success();
    }

}
