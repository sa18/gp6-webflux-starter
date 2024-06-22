package ru.gp6.infrastructure.webflux.logger;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;

@Slf4j
public class LoggingFilter implements WebFilter {

    public static final String TRACE_ID_HEADER_NAME = "X-Trace-Id";
    public static final String TRACE_ID_CONTEXT_NAME = "TraceID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        final var traceIdFromHeader = Optional.ofNullable(
                exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER_NAME)
        );

        return Mono.fromCallable(() -> {
            final long startTime = System.currentTimeMillis(); // TODO

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

        }).contextWrite(context -> {

            String traceId;
            if (traceIdFromHeader.isPresent() && !traceIdFromHeader.get().isEmpty()) {
                traceId = traceIdFromHeader.get();
            } else {
                traceId = UuidCreator.getTimeOrderedWithRandom().toString();
            }

            MDC.put(TRACE_ID_CONTEXT_NAME, traceId);
            Context ctx = context.put(TRACE_ID_CONTEXT_NAME, traceId);
            exchange.getAttributes().put(TRACE_ID_CONTEXT_NAME, traceId);

            return ctx;

        }).flatMap(chain::filter);
    }
}
