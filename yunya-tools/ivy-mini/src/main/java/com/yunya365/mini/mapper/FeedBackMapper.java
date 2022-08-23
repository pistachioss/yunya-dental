package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.form.FeedBackForm;
import com.yunya.feign.ivy_mini.domain.vo.FeedBackVO;
import com.yunya365.mini.entity.FeedBack;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FeedBackMapper extends Mapper<FeedBack> {
    List<FeedBackVO> findFeedBackList(FeedBackForm form);
}