package com.tutorial.facade.config;

import com.tutorial.facade.grpc.LoggingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

  @Bean
  public ManagedChannel managedChannel() {
    return ManagedChannelBuilder
        .forAddress("localhost", 9090)
        .usePlaintext()
        .build();
  }

  @Bean
  public LoggingServiceGrpc.LoggingServiceBlockingStub loggingStub(ManagedChannel channel) {
    return LoggingServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(5, TimeUnit.SECONDS);
  }
}