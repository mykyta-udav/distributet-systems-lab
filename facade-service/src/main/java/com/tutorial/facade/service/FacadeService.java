package com.tutorial.facade.service;

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
    public static final String LOGGING = "http://localhost:8081";
    private static final String MESSAGES_SERVICE_URL = "http://localhost:8080/messages";

    public FacadeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/message")
    public ResponseEntity<UUID> handleMessage(@RequestBody String message) {
        UUID messageId = UUID.randomUUID();

        LogMessage logMessage = new LogMessage(messageId, message);
        restTemplate.postForEntity(LOGGING + "/log", logMessage, Void.class);

        return ResponseEntity.ok(messageId);
    }

    @GetMapping("/messages")
    public String getAllMessages() {
        // Отримуємо дані з обох сервісів
        log.info("FacadeService.getAllMessages");
        String loggingMessages = restTemplate.getForObject(LOGGING + "/messages", String.class);
        log.info("FacadeService.getAllMessages: loggingMessages = {}", loggingMessages);
        String staticMessage = restTemplate.getForObject(MESSAGES_SERVICE_URL, String.class);
        log.info("FacadeService.getAllMessages: staticMessage = {}", staticMessage);

        // Конкатенуємо результати
        return loggingMessages + "\n" + staticMessage;
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class LogMessage {
    private UUID id;
    private String message;
}