package com.yunya365.mini.service.impl;

import com.yunya.feign.ivy_mini.domain.form.UpdateCartForm;
import com.yunya.feign.ivy_mini.domain.model.AddCartModel;
import com.yunya.feign.ivy_mini.domain.vo.CartVO;
import com.yunya365.mini.entity.CartItem;
import com.yunya365.mini.mapper.CartItemMapper;
import com.yunya365.mini.service.ICartItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-27
 */
@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements ICartItemService {

    @Override
    public CartVO listCart() {

        return null;
    }

    @Override
    public void add(AddCartModel model) {

    }

    @Override
    public void delete(Integer cartId) {

    }

    @Override
    public void updateQuantity(UpdateCartForm form) {

    }

    @Override
    public void clear() {

    }
}
