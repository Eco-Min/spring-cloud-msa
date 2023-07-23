package com.msa.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) ->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging filter: {}", config.baseMessage);

            if (config.preLogger) {
                log.info("Logging Pre filter : request id -> {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("Logging Post filter : response code -> {}", response.getStatusCode())
            ));
        }, Ordered.LOWEST_PRECEDENCE);

//        Ordered.HIGHEST_PRECEDENCE -> filter의 우선순위 설정 값이다 -> 최우선
//        Ordered.LOWEST_PRECEDENCE -> filter의 우선순위 설정 값이다 -> 나중

        return filter;
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }
}
