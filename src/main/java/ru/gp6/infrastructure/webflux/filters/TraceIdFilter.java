package ru.gp6.infrastructure.webflux.filters;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Фильтр создаёт параметр TraceID и вставляет его в Reactor Context, в ServerWebExchange и в MDC
 */
@Slf4j
public class TraceIdFilter implements WebFilter {

    public static final String TRACE_ID_HEADER_NAME = "X-Trace-Id";
    public static final String TRACE_ID_NAME = "TraceID";

    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        final var traceIdFromHeader = Optional.ofNullable(
                exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER_NAME)
        );

        final AtomicReference<MDC.MDCCloseable> mdcClosable = new AtomicReference<>();

        return Mono.defer(() -> chain.filter(exchange)
                .contextWrite(context -> {

                    String traceId;
                    if (traceIdFromHeader.isPresent() && !traceIdFromHeader.get().isEmpty()) {
                        // берём значение из заголовка (от вышестоящего сервера)
                        traceId = traceIdFromHeader.get();
                    } else {
                        // альтернативные варианты создания своего или использования готового Request ID
                        //traceId = UuidCreator.getTimeOrderedWithRandom().toString();
                        traceId = exchange.getRequest().getId();
                    }

                    log.debug("Using trace id {}", traceId);

                    mdcClosable.set(MDC.putCloseable(TRACE_ID_NAME, traceId));
                    Context ctx = context.put(TRACE_ID_NAME, traceId);
                    exchange.getAttributes().put(TRACE_ID_NAME, traceId);
                    return ctx;

                }))
                /*.doFinally(r -> {
                    log.debug("Cleaning up reactor context & MDC");

                    exchange.getAttributes().remove(TRACE_ID_NAME);
                    mdcClosable.get().close(); // or simply MDC.remove(TRACE_ID_NAME);
                })*/;
    }

}
