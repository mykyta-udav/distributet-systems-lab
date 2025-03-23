package com.tutorial.facade.service;

import com.tutorial.facade.config.LoggingServiceProperties;
import com.tutorial.facade.grpc.LogRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@RestController
public class FacadeService {

    private final RestTemplate restTemplate;
    private final GrpcLoggingService grpcLoggingService;
    private final List<Integer> restPorts;

    public FacadeService(RestTemplate restTemplate,
                         GrpcLoggingService grpcLoggingService,
                         LoggingServiceProperties props) {
        this.restTemplate = restTemplate;
        this.grpcLoggingService = grpcLoggingService;
        this.restPorts = props.getRestPortsAsList();
    }


    @PostMapping("/message")
    @CircuitBreaker(name = "logging-service", fallbackMethod = "handleMessageFallback")
    public ResponseEntity<UUID> handleMessage(@RequestBody String message) {
        UUID messageId = UUID.randomUUID();

        LogRequest logRequest = LogRequest.newBuilder()
                .setId(messageId.toString())
                .setMessage(message)
                .build();

        grpcLoggingService.log(logRequest);
        return ResponseEntity.ok(messageId);
    }

    public ResponseEntity<UUID> handleMessageFallback(String message, Exception ex) {
        log.error("Circuit Breaker відкритий. Помилка при спробі логування повідомлення: {}", ex.getMessage());
        return ResponseEntity.ok(UUID.randomUUID());
    }

    @GetMapping("/messages")
    public String getAllMessages() {
        // Обираємо випадковий порт для logging-service (REST-запит)
        int chosenPort = pickRandomRestPort();
        String loggingUrl = "http://localhost:" + chosenPort + "/messages";

        // Викликаємо messages-service
        String messageServiceUrl = "http://localhost:8080/messages";

        String loggingMessages = restTemplate.getForObject(loggingUrl, String.class);
        String staticMessage = restTemplate.getForObject(messageServiceUrl, String.class);

        return loggingMessages + "\n" + staticMessage;
    }

    private int pickRandomRestPort() {
        List<Integer> copy = new ArrayList<>(restPorts);
        Collections.shuffle(copy, new Random());
        return copy.getFirst();
    }
}