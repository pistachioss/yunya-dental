package com.yunya365.mini.service;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.feign.ivy_mini.domain.bo.OrderItemBO;
import com.yunya.feign.ivy_mini.domain.query.GoodsQuery;
import com.yunya.feign.ivy_mini.domain.query.VirtualProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;

import java.util.Collection;
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

    /**
     * 商品详情
     * @param itemId:
     * @return GoodsDetailVO
     */
    GoodsDetailVO goodsDetail(Integer itemId);

    /**
     * 虚拟服务详情
     * @param couponId:
     * @return VirtualDetailVO
     */
    VirtualDetailVO virtualDetail(Integer couponId);

    /**
     * 查询商品或虚拟服务分类
     * @param type:
     * @return List<ProductTypeVO>
     */
    List<ProductTypeVO> cateGoryList(Integer type);

    List<OrderItemBO> listGoodsOrderItem(Collection<Integer> ids);
}
