package com.tutorial.facade.service;

import com.tutorial.facade.dto.ServiceInfoDto;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
public class FacadeService {

    private final GrpcLoggingService grpcLoggingService;
    private final ConfigServerClient configServerClient;
    private final RestTemplate restTemplate;

    public FacadeService(GrpcLoggingService grpcLoggingService,
                         ConfigServerClient configServerClient,
                         RestTemplate restTemplate) {
        this.grpcLoggingService = grpcLoggingService;
        this.configServerClient = configServerClient;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/message")
    @CircuitBreaker(name = "logging-service", fallbackMethod = "handleMessageFallback")
    public ResponseEntity<UUID> handleMessage(@RequestBody String message) {
        UUID messageId = UUID.randomUUID();

        LogRequest request = LogRequest.newBuilder()
                .setId(messageId.toString())
                .setMessage(message)
                .build();

        grpcLoggingService.log(request);

        return ResponseEntity.ok(messageId);
    }

    public ResponseEntity<UUID> handleMessageFallback(String message, Exception ex) {
        log.error("Circuit Breaker відкритий. Помилка при спробі логування повідомлення: {}", ex.getMessage());
        return ResponseEntity.ok(UUID.randomUUID());
    }

    @GetMapping("/messages")
    public String getAllMessages() {
        // 1) Отримуємо IP/порти logging-service з config-server
        ServiceInfoDto loggingDto = configServerClient.getServiceInfo("logging-service");
        // 2) Випадково обираємо один порт:
        List<Integer> restPorts = parsePorts(loggingDto.getRestPorts());

        String chosenLoggingUrl = null;
        if (!restPorts.isEmpty()) {
            Collections.shuffle(restPorts);
            int port = restPorts.get(0);
            chosenLoggingUrl = "http://" + loggingDto.getHost() + ":" + port + "/messages";
        }

        // 3) Беремо адресу message-service
        ServiceInfoDto msgDto = configServerClient.getServiceInfo("message-service");
        // У нього може бути лише один порт - 8080, але теоретично можна і кілька
        List<Integer> msgPorts = parsePorts(msgDto.getRestPorts());
        int msgPort = msgPorts.isEmpty() ? 8080 : msgPorts.get(0); // умовно перший
        String messageServiceUrl = "http://" + msgDto.getHost() + ":" + msgPort + "/messages";

        // 4) Викликаємо обидва (якщо loggingUrl не null)
        String loggingMessages = "";
        if (chosenLoggingUrl != null) {
            try {
                loggingMessages = restTemplate.getForObject(chosenLoggingUrl, String.class);
            } catch (Exception e) {
                loggingMessages = "Неможливо отримати повідомлення від logging-service: " + e.getMessage();
            }
        }

        String staticMessage = "";
        try {
            staticMessage = restTemplate.getForObject(messageServiceUrl, String.class);
        } catch (Exception e) {
            staticMessage = "Неможливо отримати повідомлення від message-service: " + e.getMessage();
        }

        return loggingMessages + "\n" + staticMessage;
    }

    private List<Integer> parsePorts(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}