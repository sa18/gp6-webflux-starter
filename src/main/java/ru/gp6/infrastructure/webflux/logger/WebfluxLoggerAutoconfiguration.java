package ru.gp6.infrastructure.webflux.logger;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;

@AutoConfiguration
public class WebfluxLoggerAutoconfiguration {

    public WebfluxLoggerAutoconfiguration() {
        // starting from reactor-core 3.5.3:
        Hooks.enableAutomaticContextPropagation();

        //ContextRegistry.getInstance().registerThreadLocalAccessor("TraceId", MDC::getCopyOfContextMap, MDC::setContextMap, MDC::clear);
    }

    @Bean
    @ConditionalOnMissingBean
    //@ConditionalOnProperty(prefix = "logging.webflux.http", name = "enabled", havingValue = "true", matchIfMissing = true)
    public LoggingFilter loggingWebFilter() {
        return new LoggingFilter();
    }
}
