package ru.gp6.infrastructure.webflux.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

@Slf4j
public class RequestLoggingInterceptor extends ServerHttpRequestDecorator {

    public RequestLoggingInterceptor(ServerHttpRequest delegate) {
        super(delegate);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        var byteArrayStream = new ByteArrayOutputStream();
        return super.getBody().doOnNext(dataBuffer -> {
            try {

                Channels.newChannel(byteArrayStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                // do logging

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    byteArrayStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
