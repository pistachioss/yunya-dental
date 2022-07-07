package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya.feign.ivy_mini.domain.vo.WxPaymentVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya365.mini.entity.WxPayInfo;
import com.yunya365.mini.mapper.WxPayInfoMapper;
import com.yunya365.mini.service.IWxPayInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-07
 */
@Service
public class WxPayInfoServiceImpl extends ServiceImpl<WxPayInfoMapper, WxPayInfo> implements IWxPayInfoService {

    @Override
    public void save(WxPaymentVO wxPaymentVO) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        WxPayInfo wxPayInfo = BeanCopierUtils.generalCopyBean(wxPaymentVO, WxPayInfo.class);
        wxPayInfo.setCrtId(userId);
        save(wxPayInfo);
    }

    @Override
    public WxPaymentVO getWxPay(Integer orderId) {
        WxPayInfo wxPayInfo = ChainWrappers.lambdaQueryChain(baseMapper).eq(WxPayInfo::getOrderId, orderId).one();
        return BeanCopierUtils.generalCopyBean(wxPayInfo, WxPaymentVO.class);
    }
}
