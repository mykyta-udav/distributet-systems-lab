package com.tutorial.logging.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
public class LoggingService {

  private final Map<UUID, String> messageStorage = new ConcurrentHashMap<>();

  @PostMapping("/log")
  public ResponseEntity<Void> logMessage(@RequestBody LogMessage message) {
    messageStorage.put(message.getId(), message.getMessage());
    log.info("Отримано повідомлення: {} з ID: {}", message.getMessage(), message.getId());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/messages")
  public String getAllMessages() {
    // Повертаємо тільки повідомлення без ключів
    return String.join("\n", messageStorage.values());
  }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class LogMessage {
  private UUID id;
  private String message;
}