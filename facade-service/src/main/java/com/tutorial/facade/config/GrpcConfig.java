package com.tutorial.facade.config;

import com.tutorial.facade.grpc.LoggingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

  public LoggingServiceGrpc.LoggingServiceBlockingStub buildStub(String host, int port) {
    ManagedChannel channel = ManagedChannelBuilder
            .forAddress(host, port)
            .usePlaintext()
            .build();

    // Задаємо deadline 5 с. (аналогічно facade-service налаштуванню)
    return LoggingServiceGrpc.newBlockingStub(channel)
            .withDeadlineAfter(5, TimeUnit.SECONDS);
  }
}