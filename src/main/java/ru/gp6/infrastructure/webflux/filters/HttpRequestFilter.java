package ru.gp6.infrastructure.webflux.filters;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class HttpRequestFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return chain.filter(exchange)
                .doFirst(() -> {
                    log.debug("HTTP Request method={} uri={}",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getURI());

                    log.debug("HTTP Request Headers:");

                    exchange.getRequest().getHeaders().toSingleValueMap().entrySet().forEach(entry -> {
                        log.debug("\t{} = {}", entry.getKey(), entry.getValue());
                    });
                });
    }
}
