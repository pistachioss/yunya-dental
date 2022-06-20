package com.yunya365.mini.service.impl;

import com.yunya.feign.ivy_mini.domain.vo.CustomerListVO;
import com.yunya.feign.ivy_mini.domain.vo.CustomerVO;
import com.yunya.feign.ivy_mini.domain.vo.WxAccessTokenVo;
import com.yunya365.mini.config.WxMiniProperties;
import com.yunya365.mini.service.WxApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.yunya.framework.common.constant.WxMiniUri.WXCORP_ACCESS_TOKEN_URL;
import static com.yunya.framework.common.constant.WxMiniUri.WX_CUSTOMER_URL;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/20
 * @description:
 */
@Slf4j
@Service
public class CustomerServiceImpl {
    @Resource
    private WxApi wxApi;
    @Resource
    private WxMiniProperties wxMiniProperties;

    public List<CustomerVO> customerList() {
        String url = String.format(WX_CUSTOMER_URL, getAccessToken());
        CustomerListVO customerListVO  = wxApi.getWxResult(url, CustomerListVO.class);
        return customerListVO.getAccountList();
    }

    public String getAccessToken() {
        String url = String.format(WXCORP_ACCESS_TOKEN_URL,wxMiniProperties.getCorpId(),wxMiniProperties.getCorpSecret());
        WxAccessTokenVo wxAccessTokenVo =  wxApi.getWxResult(url, WxAccessTokenVo.class);
        return  wxAccessTokenVo.getAccessToken();
    }

}
