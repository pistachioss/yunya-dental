package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseCard;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseCardMapper extends Mapper<BaseCard> {
	/**
	 * 批量插入
	 * @param list
	 */
	void insertList(@Param("list") List<BaseCard> list);

	/**
	 * 批量更新
	 * @param list
	 */
	void updateList(@Param("list") List<BaseCard> list);
}