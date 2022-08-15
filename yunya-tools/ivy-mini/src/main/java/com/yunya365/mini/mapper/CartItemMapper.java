package com.yunya365.mini.mapper;

import com.yunya365.mini.entity.CartItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 购物车 Mapper 接口
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-27
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

}
