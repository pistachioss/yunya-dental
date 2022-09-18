package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.bo.OrderItemBO;
import com.yunya.feign.ivy_mini.domain.form.UpdateCartForm;
import com.yunya.feign.ivy_mini.domain.model.AddCartModel;
import com.yunya.feign.ivy_mini.domain.vo.CartVO;
import com.yunya365.mini.entity.CartItem;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-27
 */
public interface ICartItemService extends IService<CartItem> {

    /**
     * 购物车列表
     * @param :
     * @return CartVO
     */
    CartVO listCart();

    /**
     * 天剑购物车
     * @param model:
     */
    void add(AddCartModel model);

    /**
     * 删除购物车某个商品
     * @param cartIds:
     */
    void delete(List<Integer> cartIds);

    /**
     * 修改购物车某个商品数量
     * @param form:
     */
    void updateQuantity(UpdateCartForm form);

    /**
     * 清空购物车
     */
    void clear();

    /**
     * 根据购物车ids获取购物车商品列表
     */
    List<OrderItemBO> listProductByIds(Collection<Integer> cartIds);
}
