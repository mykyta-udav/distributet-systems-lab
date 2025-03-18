package com.tutorial.logging.service;

import com.tutorial.logging.storage.MessageStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class LoggingService {

  private final MessageStorage messageStorage;

  public LoggingService(MessageStorage messageStorage) {
    this.messageStorage = messageStorage;
  }

  @GetMapping("/messages")
  public String getAllMessages() {
    return messageStorage.getAllMessages();
  }
}