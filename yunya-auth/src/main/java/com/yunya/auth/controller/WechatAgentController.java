package com.yunya.auth.controller;

import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "微信网页授权域名代理控制器")
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
     * oauth2授权，需与下面的 转发 code 接口一起使用
     *
     * @param appId
     * @param redirectUrl 目标地址
     * @param agentHost    中间代理域名，可直接访问到 /wx/oauth2, 可以不填
     * @param response
     * @throws IOException
     */
    @ApiOperation("中间代理域名：构建微信网页授权oauth2，跳转授权后，使得微信oauth2能回调【api/auth/white/code】接口，再由【api/auth/white/code】接口重定向到目标地址")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "appId", value = "公众号的appId或企微企业corpId", required = true),
        @ApiImplicitParam(name = "redirectUrl", value = "目标地址，授权后重定向的回调链接地址，无需编码处理", required = true),
        @ApiImplicitParam(name = "agentHost", value = "代理授权域名，在微信公众号或者企微或者小程序后台上配置的可信域名", example = "http://test.ivy2.yunya365.com", defaultValue = "http://ivy2.yunya365.com"),
        @ApiImplicitParam(name = "scope", value = "应用授权作用域：snsapi_base-静默授权(可获取成员的UserId与DeviceId）；\n" +
                "snsapi_privateinfo-手动授权(可获取成员的头像、手机号、邮箱、性别、二维码等敏感信息）；", defaultValue = "snsapi_privateinfo"),
        @ApiImplicitParam(name = "state", value = "重定向后会带上state参数，企业可以填写a-zA-Z0-9的参数值，长度不可超过128个字节", defaultValue = "STATE"),
        @ApiImplicitParam(name = "agentid", value = "应用agentid，建议填上该参数（如果为第三方应用或者代开发自建应用，未填该参数不会触发接口许可自动激活）。snsapi_privateinfo时必填否则报错；", defaultValue = ""),
    })
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
    @ApiOperation("处理微信授权后回调，重定向到目标地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。", required = true),
            @ApiImplicitParam(name = "ret", value = "目标地址", required = true)
    })
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
