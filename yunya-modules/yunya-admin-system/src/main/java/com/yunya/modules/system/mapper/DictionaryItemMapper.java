package com.yunya.modules.system.mapper;

import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.system.domain.query.DictQueryForm;
import com.yunya.modules.system.vo.DictionaryItemVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DictionaryItemMapper extends Mapper<DictionaryItem> {
    /**
     * 根据条件查询字典明细列表
     * @param queryForm 参数封装
     * @return
     */
    List<DictionaryItemVO> selectDictionaryByExample(@Param("queryForm") DictQueryForm queryForm);

    /**
     * 根据ID查询字典明细列表
     * @param ids ID列表
     * @return 返回字典信息列表
     */
    List<DictionaryItem> findByIds(@Param("ids") List<Integer> ids);
}