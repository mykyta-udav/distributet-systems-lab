package com.tutorial.logging.config;

import com.tutorial.logging.service.GrpcLoggingService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class GrpcServerConfig {

  private Server server;

  @Bean
  public Server grpcServer(GrpcLoggingService grpcLoggingService) throws IOException {
    server = ServerBuilder.forPort(9090)
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