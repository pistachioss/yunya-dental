package com.yunya.modules.system.mapper;

import com.yunya.models.system.Post;
import com.yunya.modules.system.domain.query.PostQueryForm;
import com.yunya.modules.system.vo.PostVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PostMapper extends Mapper<Post> {
    /**
     * 根据条件查询岗位列表
     * @param form 参数封装
     * @return
     */
    List<PostVO> selectBrandByExample(@Param("form") PostQueryForm form);
}