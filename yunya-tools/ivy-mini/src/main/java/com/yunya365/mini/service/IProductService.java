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

    PageInfo<GoodsVO> pageGoods(GoodsQuery query);

    PageInfo<VirtualProductVO> pageVirtual(VirtualProductQuery query);

    void goodsDetail(Integer itemId);

    void virtualDetail(Integer couponId);

}
