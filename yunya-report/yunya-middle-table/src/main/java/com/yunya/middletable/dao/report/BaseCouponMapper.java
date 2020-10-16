package com.yunya.middletable.dao.report;

import com.yunya.models.middletable.BaseCoupon;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseCouponMapper extends Mapper<BaseCoupon> {

	/**
	 * 查询产品基础表存在的产品id集合
	 * @param originIds
	 * @return
	 */
	List<Integer> existIds(@Param("list") List<Integer> originIds);

	void insertList(@Param("list") List<BaseCoupon> list);

	void updateList(@Param("list") List<BaseCoupon> list);
}