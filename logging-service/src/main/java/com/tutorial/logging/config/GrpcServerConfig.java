package com.tutorial.logging.config;

import com.tutorial.logging.service.GrpcLoggingService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class GrpcServerConfig {

  @Value("${grpc.server.port}")
  private int grpcPort;

  private Server server;

  @Bean
  public Server grpcServer(GrpcLoggingService grpcLoggingService) throws IOException {
    server = ServerBuilder.forPort(grpcPort)
            .addService(grpcLoggingService)
            .build()
            .start();
    return server;
  }

  @PreDestroy
  public void stopServer() {
    if (server != null) {
      server.shutdown();
    }
  }
}