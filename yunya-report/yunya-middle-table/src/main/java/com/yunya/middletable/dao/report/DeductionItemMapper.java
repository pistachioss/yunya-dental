package com.yunya.middletable.dao.report;

import com.yunya.models.report.DeductionItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DeductionItemMapper extends Mapper<DeductionItem> {

	/**
	 * 批量插入
	 * @param list
	 */
	void insertList(@Param("list") List<DeductionItem> list);

	/**
	 * 批量更新
	 * @param list
	 */
	void updateList(@Param("list") List<DeductionItem> list);

	/**
	 * 批量删除
	 * @param list
	 */
	void deleteList(@Param("list") List<DeductionItem> list);
}