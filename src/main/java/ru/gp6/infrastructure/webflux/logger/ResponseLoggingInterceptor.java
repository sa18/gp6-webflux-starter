package ru.gp6.infrastructure.webflux.logger;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {
    private final AtomicLong start;

    public ResponseLoggingInterceptor(ServerHttpResponse delegate, AtomicLong start) {
        super(delegate);
        this.start = start;
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return super.writeAndFlushWith(body);
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        var buffer = Flux.from(body);
        return super.writeWith(buffer.doOnNext(dataBuffer -> {
            var byteArrayStream = new ByteArrayOutputStream();
            try {
                Channels.newChannel(byteArrayStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                // TODO log

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    byteArrayStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}
