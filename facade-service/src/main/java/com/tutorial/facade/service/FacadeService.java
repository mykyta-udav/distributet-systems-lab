package com.tutorial.facade.service;

import com.tutorial.facade.grpc.LogRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@RestController
public class FacadeService {

    private final RestTemplate restTemplate;
    private final GrpcLoggingService grpcLoggingService;
    private static final String MESSAGES_SERVICE_URL = "http://localhost:8080/messages";
    private static final String LOGGING = "http://localhost:8081/messages";

    public FacadeService(RestTemplate restTemplate, GrpcLoggingService grpcLoggingService) {
        this.restTemplate = restTemplate;
        this.grpcLoggingService = grpcLoggingService;
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
        String loggingMessages = restTemplate.getForObject(LOGGING, String.class);
        String staticMessage = restTemplate.getForObject(MESSAGES_SERVICE_URL, String.class);
        return loggingMessages + "\n" + staticMessage;
    }
}