package ru.gp6.infrastructure.webflux.filters;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
public class EmptyFilter implements WebFilter {
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, WebFilterChain chain) {

        return Mono.fromRunnable(() -> {
            log.info("Empty");
        }).then(chain.filter(exchange).doFinally(it -> {
            log.info("DoFinally");
        }));

    }
}
