package com.yunya.framework.common.interceptor;

import com.yunya.framework.common.constant.CommonConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

@Slf4j
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {
	@Override
	public void apply(RequestTemplate requestTemplate) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (!ObjectUtils.isEmpty(attributes)) {
			HttpServletRequest request = attributes.getRequest();
			Enumeration<String> headerNames = request.getHeaderNames();
			if (headerNames != null) {
				while (headerNames.hasMoreElements()) {
					String name = headerNames.nextElement();
					if ("content-length".equals(name)) {
						continue;
					}
					String values = request.getHeader(name);
					requestTemplate.header(name, values);
				}
			}

			// 在Feign请求中增加token
			String token = (String) attributes.getAttribute(CommonConstants.TOKEN_HEADER, RequestAttributes.SCOPE_SESSION);
			if (StringHelper.isNotEmpty(token)) {
				requestTemplate.header(CommonConstants.TOKEN_HEADER, token);
			}
			Date curTime = BaseContextHandler.getCurTime();
			if (StringHelper.isNotNull(curTime)) {
				// 传递调用者的curTime
				requestTemplate.header(CommonConstants.CONTEXT_KEY_TIME, DateUtil.formatTime(curTime));
			}
		}
//		Enumeration<String> bodyNames = request.getParameterNames();
//		StringBuffer body =new StringBuffer();
//		if (bodyNames != null) {
//			while (bodyNames.hasMoreElements()) {
//				String name = bodyNames.nextElement();
//				String values = request.getParameter(name);
//				body.append(name).append("=").append(values).append("&");
//			}
//		}
//		if(body.length()!=0) {
//			body.deleteCharAt(body.length()-1);
//			requestTemplate.body(body.toString());
//			log.info("feign interceptor body:{}",body.toString());
//		}
	}
}