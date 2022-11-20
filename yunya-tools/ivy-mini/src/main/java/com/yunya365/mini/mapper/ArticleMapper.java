package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.form.ArticleForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleVO;
import com.yunya365.mini.entity.Article;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ArticleMapper extends Mapper<Article> {

   List<ArticleVO> findArticleList(ArticleForm form);

   List<ArticleVO> findArticleAppList(ArticleForm form);

   Integer selectNextId(Article article);

   Integer selectPreviousId(Article article);

   void updateSortById(Article article);

   void updateBatch(List<ArticleVO>list);

   void updateList(List<ArticleVO>list);

   void updateAllList(Integer type);



}