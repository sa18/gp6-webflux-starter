package ru.gp6.infrastructure.webflux.filters;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class HttpResponseFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        AtomicReference<Throwable> error = new AtomicReference(null);
        AtomicReference<Boolean> isCanceled = new AtomicReference(false);

        return chain.filter(exchange)
                .onErrorComplete(err -> {
                    error.set(err);
                    return false;
                })
                .doOnCancel(() -> {
                    log.debug("Request cancelled by origin");
                    isCanceled.set(true);
                })
                .doFinally(signal -> {
                    log.debug("HTTP Response Status = {}", exchange.getResponse().getStatusCode());

                    if (!isCanceled.get() && exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                        log.debug("HTTP Response Headers:");

                        exchange.getResponse().getHeaders().toSingleValueMap().entrySet().forEach(entry -> {
                            log.debug("\t{} = {}", entry.getKey(), entry.getValue());
                        });
                    }

                    if (!isCanceled.get() && error.get() != null) {
                        if (exchange.getResponse().getStatusCode().is4xxClientError() ||
                                exchange.getResponse().getStatusCode().is5xxServerError()) {
                            log.debug(error.get().getMessage(), error.get());
                        }
                    }
                });

    }
}
