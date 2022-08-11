package com.yunya365.mini.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunya365.mini.entity.OrderReturnApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 订单退货申请 Mapper 接口
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
@Mapper
public interface OrderReturnApplyMapper extends BaseMapper<OrderReturnApply> {

    List<OrderReturnApply> listLast(@Param("orderIds") Collection<Integer> orderIds);
}
