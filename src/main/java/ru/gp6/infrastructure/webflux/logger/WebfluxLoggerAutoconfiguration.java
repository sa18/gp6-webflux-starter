package ru.gp6.infrastructure.webflux.logger;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Hooks;

@AutoConfiguration
public class WebfluxLoggerAutoconfiguration {

    public WebfluxLoggerAutoconfiguration() {
        // starting from reactor-core 3.5.3:
        Hooks.enableAutomaticContextPropagation();
        // prior versions:
        //ContextRegistry.getInstance().registerThreadLocalAccessor("TraceId", MDC::getCopyOfContextMap, MDC::setContextMap, MDC::clear);
    }

    @Bean
    @Order(20)
    public TraceIDFilter traceIDFilter() {
        return new TraceIDFilter();
    }

    @Bean
    @Order(21)
    public TimingFilter elapsedTimeLoggingFilter() {
        return new TimingFilter();
    }

    /*@Bean
    @Order(22)
    public HTTPHeadersLoggingFilter httpHeadersLoggingFilter() {
        return new HTTPHeadersLoggingFilter();
    }*/
}
