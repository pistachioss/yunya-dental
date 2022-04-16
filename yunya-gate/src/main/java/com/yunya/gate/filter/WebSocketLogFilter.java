package com.yunya.gate.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class WebSocketLogFilter extends RouteToRequestUrlFilter implements Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String upgrade = exchange.getRequest().getHeaders().getUpgrade();
        if("websocket".equals(upgrade)) {
            ServerHttpRequest request = exchange.getRequest();
            log.info("websocket={address:{}, path:{}, method:{}, URI:{}]",
                    request.getRemoteAddress(),
                    request.getPath(),
                    request.getMethodValue(),
                    request.getURI());
//            logger.info("Headers: " + request.getHeaders());
//            logger.info("body: "+ exchange.getAttribute("cachedRequestBodyObject"));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ROUTE_TO_URL_FILTER_ORDER + 10;
    }
}