package com.tutorial.facade.service;

import com.tutorial.facade.grpc.LogRequest;
import org.springframework.stereotype.Service;

@Service
public class GrpcLoggingService {

  private final ConfigServerClient configServerClient;

  public GrpcLoggingService(ConfigServerClient configServerClient) {
    this.configServerClient = configServerClient;
  }

  public void log(LogRequest request) {
    configServerClient.invokeLoggingServiceGrpc(request);
  }
}