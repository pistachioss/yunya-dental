package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.form.UpdateCartForm;
import com.yunya.feign.ivy_mini.domain.model.AddCartModel;
import com.yunya.feign.ivy_mini.domain.vo.CartVO;
import com.yunya365.mini.entity.CartItem;

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

    void delete(Integer cartId);

    void updateQuantity(UpdateCartForm form);

    void clear();
}
