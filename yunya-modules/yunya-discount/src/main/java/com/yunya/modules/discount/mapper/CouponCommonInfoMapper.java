package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.GenerateAllocatePageBo;
import com.yunya.feign.discount.domain.query.CouponCommonInfoQuery;
import com.yunya.feign.discount.domain.vo.CouponCommonInfoVO;
import com.yunya.feign.discount.domain.vo.CouponGoodsVO;
import com.yunya.models.discount.CouponCommonInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface CouponCommonInfoMapper extends Mapper<CouponCommonInfo> {

    /**
     * 查询产品生成分配
     * @param couponName
     * @param couponTypeList
     * @return
     */
    List<GenerateAllocatePageBo> listBatchAllocateByParam(@Param("couponName") String couponName, @Param("couponTypeList") List<Integer> couponTypeList);

    /**
     * 条件查询卡券公用信息列表
     *
     * @param query
     * @return
     */
    List<CouponCommonInfoVO> selectCouponCommonList(@Param("query") CouponCommonInfoQuery query);

    List<CouponGoodsVO> listCouponGoods(@Param("couponIds") Collection<Integer> couponIds);
}