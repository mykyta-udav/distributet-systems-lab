package com.tutorial.logging.storage;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageStorage {
  private final Map<UUID, String> messages = new ConcurrentHashMap<>();

  public void addMessage(UUID id, String message) {
    messages.put(id, message);
  }

  public String getAllMessages() {
    return String.join("\n", messages.values());
  }
}