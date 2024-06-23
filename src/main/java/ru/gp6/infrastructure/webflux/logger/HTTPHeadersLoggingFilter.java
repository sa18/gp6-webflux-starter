package ru.gp6.infrastructure.webflux.logger;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class HTTPHeadersLoggingFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // TODO

        return Mono.fromCallable(() -> {

            return new ServerWebExchangeDecorator(exchange) {
                @Override
                public ServerHttpRequest getRequest() {
                    return new RequestLoggingInterceptor(super.getRequest());
                }

                @Override
                public ServerHttpResponse getResponse() {
                    return new ResponseLoggingInterceptor(super.getResponse());
                }
            };

        })/*.doOnEach(signal -> {

            if (signal.isOnComplete() || signal.isOnError()) {
                log.debug("requestId: {}, method: {}, path: {}",
                        exchange.getRequest().getId(),
                        exchange.getRequest().getMethod(),
                        exchange.getRequest().getPath());

                //exchange.getResponse().getStatusCode();
            }

        })*/.doOnError(r -> {

            log.info("Request ended with error: {}", r.getMessage());

        }).doOnCancel(() -> {

            log.info("Request cancelled");

        }).flatMap(chain::filter);
    }
}
