package com.yunya.middletable.dao.report;

import com.yunya.models.middletable.BaseCoupon;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseCouponMapper extends Mapper<BaseCoupon> {
	/**
	 * 批量插入
	 * @param list
	 */
	void insertList(@Param("list") List<BaseCoupon> list);

	/**
	 * 批量更新
	 * @param list
	 */
	void updateList(@Param("list") List<BaseCoupon> list);
}