package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya.feign.ivy_mini.domain.form.UpdateCartForm;
import com.yunya.feign.ivy_mini.domain.model.AddCartModel;
import com.yunya.feign.ivy_mini.domain.vo.CartItemVO;
import com.yunya.feign.ivy_mini.domain.vo.CartVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya365.mini.entity.CartItem;
import com.yunya365.mini.mapper.CartItemMapper;
import com.yunya365.mini.service.ICartItemService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.*;

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
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        List<CartItem> list = ChainWrappers.lambdaQueryChain(baseMapper).eq(CartItem::getFansId, userID).list();
        CartVO cartVO = new CartVO();
        if (CollectionUtils.isNotEmpty(list)) {
            List<CartItemVO> itemVOS = list.stream()
                    .map(t -> BeanCopierUtils.generalCopyBean(t, CartItemVO.class)).collect(toList());
            BigDecimal totalPrice = list.stream().map(t -> t.getProductPrice().multiply(BigDecimal.valueOf(t.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cartVO.setItemList(itemVOS);
            cartVO.setTotalPrice(totalPrice);
        }
        return cartVO;
    }

    @Override
    public void add(AddCartModel model) {
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        String userName = BaseContextHandler.getName();
        CartItem existItem = getCartItem(userID, model.getProductId());
        if (Objects.nonNull(existItem)) {
            existItem.setQuantity(existItem.getQuantity() + model.getQuantity());
            updateById(existItem);
        } else {
            CartItem cartItem = BeanCopierUtils.generalCopyBean(model, CartItem.class);
            cartItem.setFansId(userID);
            cartItem.setNickName(userName);
            save(cartItem);
        }
    }

    @Override
    public void delete(List<Integer> cartIds) {
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        ChainWrappers.lambdaUpdateChain(baseMapper)
                .eq(CartItem::getFansId, userID).in(CartItem::getProductId, cartIds).remove();
    }

    @Override
    public void updateQuantity(UpdateCartForm form) {
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        ChainWrappers.lambdaUpdateChain(baseMapper).set(CartItem::getQuantity, form.getQuantity())
                .eq(CartItem::getFansId, userID).eq(CartItem::getId, form.getId())
                .update();
    }

    @Override
    public void clear() {
        Integer userID = Integer.valueOf(BaseContextHandler.getUserID());
        ChainWrappers.lambdaUpdateChain(baseMapper)
                .eq(CartItem::getFansId, userID).remove();
    }

    private CartItem getCartItem(Integer fansId, Integer productId) {
        Wrapper<CartItem> wrapper = Wrappers.lambdaQuery(CartItem.class)
                .eq(CartItem::getFansId, fansId).eq(CartItem::getProductId, productId);
        return baseMapper.selectOne(wrapper);
    }
}
