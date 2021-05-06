package com.yunya.feign.wechat.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @description:
 * @author: xy
 * @date 2021/4/20 14:05
 **/
@Data
@Builder
public class WxTemplatePushModel {
    /**
     * 接收者openid.
     */
    private String touser;
    /**
     * 模板ID.
     */
    private String template_id;
    /**
     * 模板跳转链接.
     * <pre>
     * url和miniprogram都是非必填字段，若都不传则模板无跳转；若都传，会优先跳转至小程序。
     * 开发者可根据实际需要选择其中一种跳转方式即可。当用户的微信客户端版本不支持跳小程序时，将会跳转至url。
     * </pre>
     */
    private String url;
    private Map<String, Object> data;


}
