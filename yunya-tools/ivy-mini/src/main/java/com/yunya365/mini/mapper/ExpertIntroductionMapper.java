package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.form.ExpertIntroductionForm;
import com.yunya.feign.ivy_mini.domain.vo.ExpertIntroductionVO;
import com.yunya365.mini.entity.ExpertIntroduction;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExpertIntroductionMapper extends Mapper<ExpertIntroduction> {

    List<ExpertIntroductionVO> findExpertIntroductionList(ExpertIntroductionForm form);
    void updateList(List<ExpertIntroductionVO>list);

    void updateAllList();
}