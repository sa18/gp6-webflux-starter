package ru.gp6.infrastructure.webflux.logger;

import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class WebfluxLoggerAutoconfiguration {

    public WebfluxLoggerAutoconfiguration() {
        // starting from reactor-core 3.5.3:
        Hooks.enableAutomaticContextPropagation();

        //ContextRegistry.getInstance().registerThreadLocalAccessor("TraceId", MDC::getCopyOfContextMap, MDC::setContextMap, MDC::clear);
    }
}
