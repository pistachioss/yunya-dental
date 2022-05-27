package com.yunya365.mini.service;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;

import java.util.List;

/**
 * @description: 产品服务
 * @author: xy
 **/
public interface IProductService {

    /**
     * 热销产品
     * @return List<HotSaleVO>
     */
    List<HotSaleVO> hotSale();

    /**
     * 搜索商品
     * @param query:
     * @return PageInfo<GoodsVO>
     */
    PageInfo<GoodsVO> pageGoods(GoodsQuery query);

    /**
     * 搜索虚拟服务
     * @param query:
     * @return PageInfo<VirtualProductVO>
     */
    PageInfo<VirtualProductVO> pageVirtual(VirtualProductQuery query);

    void goodsDetail(Integer itemId);

    void virtualDetail(Integer couponId);

}
