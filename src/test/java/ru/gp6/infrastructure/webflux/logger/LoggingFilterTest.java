package ru.gp6.infrastructure.webflux.logger;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
class TestApp {
}

@RestController
@Slf4j
class TestController {
    @GetMapping("/test")
    public Integer test() {
        log.info("Hello from test controller (success)");
        return 777;
    }

    @GetMapping("/test-exception")
    public Integer testException() {
        throw new RuntimeException("Fail from test controller");
    }
}


@SpringBootTest(
        classes = {TestApp.class, WebfluxLoggerAutoconfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=8080"
        }
)
@Slf4j
public class LoggingFilterTest {

    @Test
    void в_логе_должна_быть_напечатаны_время_и_заголовки() {
        var result = WebClient.create("http://localhost:8080/test")
                .get()
                .header("Some-Custom-Header", Math.random() + "")
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Integer>() {
                })
                .block();
        log.info("result: {}", result);
    }

    @Test
    void в_логе_должна_быть_напечатаны_время_и_информация_об_ошибке() {
        var result = WebClient.create("http://localhost:8080/test-exception")
                .get()
                .header("Some-Custom-Header", Math.random() + "")
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Integer>() {
                })
                .block();
        log.info("result: {}", result);
    }
}
