package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.vo.WxPaymentVO;
import com.yunya365.mini.entity.WxPayInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-07
 */
public interface IWxPayInfoService extends IService<WxPayInfo> {

    void save(WxPaymentVO wxPaymentVO);

    WxPaymentVO getWxPay(Integer orderId);
}
