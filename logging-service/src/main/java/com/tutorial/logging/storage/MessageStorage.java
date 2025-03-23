package com.tutorial.logging.storage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class MessageStorage {
  private final IMap<UUID, String> messages;

    public MessageStorage(HazelcastInstance hazelcastInstance) {
        this.messages = hazelcastInstance.getMap("messages");
    }

    public void addMessage(UUID id, String message) {
    messages.put(id, message);
  }

  public String getAllMessages() {
    return String.join("\n", messages.values());
  }
}