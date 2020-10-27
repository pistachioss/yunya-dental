package com.yunya.middletable.dao.report;

import com.yunya.models.middletable.BaseBenefit;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBenefitMapper extends Mapper<BaseBenefit> {
	/**
	 * 批量插入
	 * @param list
	 */
	void insertList(@Param("list") List<BaseBenefit> list);
}