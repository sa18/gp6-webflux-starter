package ru.gp6.infrastructure.webflux.filters;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Фильтр выводит в лог время, затраченное на обработку запроса.
 */
@Slf4j
public class TimingFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        final AtomicLong start = new AtomicLong();

        return Mono.zip(chain.filter(exchange).doFirst(() -> {
                            start.set(System.nanoTime());
                            log.debug("Request started at {}", System.currentTimeMillis());
                        }),

                        Mono.never().doFinally(signalType -> {
                            long timeDelta = System.nanoTime() - start.get();
                            log.debug("Request ended at {}. Elapsed time = {}ms", System.currentTimeMillis(),
                                    timeDelta / 1_000_000);
                        }))
                .then();
    }

}
