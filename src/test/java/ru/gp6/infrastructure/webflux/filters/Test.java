package ru.gp6.infrastructure.webflux.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
class TestApp {
}

@RestController
@Slf4j
class TestController {
    @GetMapping("/test")
    public Mono<Integer> test() {
        log.info("Hello from test controller (success)");
        return Mono.just(777)
                .delayElement(Duration.of(2200, ChronoUnit.MILLIS));
    }

    @GetMapping("/test-exception")
    public Integer testException() {
        throw new RuntimeException("Fail from test controller");
    }
}


@SpringBootTest(
        classes = {TestApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Slf4j
//@AutoConfigureWebTestClient(timeout = "100")
public class Test {

    @Autowired
    private WebTestClient webTestClient;

    @org.junit.jupiter.api.Test
    void в_логе_должна_быть_напечатаны_время_и_заголовки() {
        var result = webTestClient
                .get()
                .uri("/test")
                .header("Some-Custom-Header", Math.random() + "")
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(Integer.class);
        log.info("result: {}", result);
    }

    @org.junit.jupiter.api.Test
    void в_логе_должна_быть_напечатаны_время_и_информация_об_ошибке() {
        var result = webTestClient
                .get()
                .uri("/test-exception")
                //.uri("/incorrect-uri")
                .header("Some-Custom-Header", Math.random() + "")
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(Integer.class);
        log.info("result: {}", result);
    }
}
