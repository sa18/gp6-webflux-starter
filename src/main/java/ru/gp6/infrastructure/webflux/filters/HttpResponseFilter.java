package ru.gp6.infrastructure.webflux.filters;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class HttpResponseFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, WebFilterChain chain) {

        final AtomicReference<Throwable> error = new AtomicReference<>();
        final AtomicBoolean isCanceled = new AtomicBoolean();

        return Mono.zip(
                chain.filter(exchange)
                        .onErrorComplete(err -> {
                            error.set(err);
                            return false;
                        })
                        .doOnCancel(() -> {
                            log.debug("Request cancelled by origin");
                            isCanceled.set(true);
                        }),

                Mono.never().doFinally(signalType -> {

                    final var statusCode = exchange.getResponse().getStatusCode();
                    if (statusCode != null) {
                        log.debug("HTTP Response Status = {}", statusCode);
                    }
                    else log.debug("HTTP Response Status is null");

                    if (!isCanceled.get() && (statusCode == null || statusCode.is2xxSuccessful())) {
                        log.debug("HTTP Response Headers:");

                        exchange.getResponse().getHeaders().toSingleValueMap().forEach((key, value) -> {
                            log.debug("\t{} = {}", key, value);
                        });
                    }

                    if (!isCanceled.get() && error.get() != null) {
                        if (exchange.getResponse().getStatusCode().is4xxClientError() ||
                                exchange.getResponse().getStatusCode().is5xxServerError()) {
                            log.debug(error.get().getMessage(), error.get());
                        }
                    }
                })
        ).then();

    }
}
