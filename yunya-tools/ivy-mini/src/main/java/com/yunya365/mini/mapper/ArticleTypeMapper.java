package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.form.ArticleTypeForm;
import com.yunya.feign.ivy_mini.domain.vo.ArticleTypeVO;
import com.yunya365.mini.entity.ArticleType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ArticleTypeMapper extends Mapper<ArticleType> {

    List<ArticleTypeVO> findArticleTypeList(ArticleTypeForm form);

}