package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.vo.RefundReasonVO;
import com.yunya365.mini.entity.OrderReturnReason;

import java.util.List;

/**
 * <p>
 * 退货原因表 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
public interface IOrderReturnReasonService extends IService<OrderReturnReason> {

    List<RefundReasonVO> reasonList();
}
