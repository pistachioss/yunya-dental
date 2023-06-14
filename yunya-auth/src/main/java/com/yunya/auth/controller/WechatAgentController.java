package com.yunya.auth.controller;

import com.yunya.framework.common.utils.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;

@Slf4j
@RestController
public class WechatAgentController {

    /**
     * 微信网页授权地址， 需要人工点击确认的那种
     */
    private static final String AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize"
    +"?appid={0}&redirect_uri={1}&response_type=code&scope={2}&state={3}&agentid={4}#wechat_redirect";

    /**
     * 代理授权域名,换成你自己真实的公网域名！！
     */
    private static final String OAUTH2_AGENT_HOST = "http://test.ivy2.yunya365.com";

    private static final String CODE_URL = "api/auth/white/code?ret={0}";

    private static String defScope = "snsapi_userinfo";

    private static String defState = "STATE";

    /**
     * 授权
     *
     * @param appId
     * @param redirectUrl 目标地址
     * @param agentHost    中间代理域名，可直接访问到 /wx/oauth2, 可以不填
     * @param response
     * @throws IOException
     */
    @GetMapping("/white/oauth2")
    public void openAuth(@RequestParam String appId,
                         @RequestParam String redirectUrl,
                         @RequestParam(required = false) String agentHost,
                         @RequestParam(required = false) String scope,
                         @RequestParam(required = false) String state,
                         @RequestParam(required = false) String agentid,
                         HttpServletResponse response) throws IOException {
        String oauthAgentHost = StringUtils.hasText(agentHost) ? agentHost : OAUTH2_AGENT_HOST;
        if (oauthAgentHost.lastIndexOf("/") != -1) {
            oauthAgentHost = oauthAgentHost.concat("/");
        }
        oauthAgentHost = oauthAgentHost.concat(CODE_URL);
        redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        String tmpUrl = MessageFormat.format(oauthAgentHost, redirectUrl);
        String authUrl = MessageFormat.format(AUTH_URL,
                appId, tmpUrl,
                StringHelper.defaultString(scope, defScope),
                StringHelper.defaultString(state, defState),
                StringHelper.defaultString(agentid, StringHelper.EMPTY));
        //重定向到 /wx/code 请求
        log.info("authUrl:  " + authUrl);
        response.sendRedirect(authUrl);
    }


    /**
     * 转发 code （用于前端）
     *
     * @param code
     * @param ret
     * @param response
     * @throws IOException
     */
    @GetMapping("/white/code")
    public static void jump(@RequestParam String code, @RequestParam String ret,
                     HttpServletResponse response) throws IOException {
        log.info("code: {}, ret: {}", code, ret);
        StringBuilder redirectUrl = new StringBuilder(ret);
        if (ret.indexOf("?") != -1) {
            if (ret.endsWith("?")) {
                redirectUrl.append("code=").append(code);
            } else {
                redirectUrl.append("&code=").append(code);
            }
        } else {
            redirectUrl.append("?code=").append(code);
        }
        response.sendRedirect(redirectUrl.toString());
    }
}
