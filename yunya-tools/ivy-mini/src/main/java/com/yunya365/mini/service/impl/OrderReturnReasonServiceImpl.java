package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya.feign.ivy_mini.domain.vo.RefundReasonVO;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya365.mini.entity.OrderReturnReason;
import com.yunya365.mini.mapper.OrderReturnReasonMapper;
import com.yunya365.mini.service.IOrderReturnReasonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yunya.framework.common.enums.TrueFalseEnum.*;
import static java.util.stream.Collectors.*;

/**
 * <p>
 * 退货原因表 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
@Service
public class OrderReturnReasonServiceImpl extends ServiceImpl<OrderReturnReasonMapper, OrderReturnReason> implements IOrderReturnReasonService {

    @Override
    public List<RefundReasonVO> reasonList() {
        List<OrderReturnReason> list = ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderReturnReason::getStatus, TRUE.getCode()).list();
        return list.stream().map(t -> BeanCopierUtils.generalCopyBean(t, RefundReasonVO.class)).collect(toList());
    }
}
