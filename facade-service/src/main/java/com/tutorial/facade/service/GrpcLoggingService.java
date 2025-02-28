package com.tutorial.facade.service;

import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.grpc.LogResponse;
import com.tutorial.facade.grpc.LoggingServiceGrpc;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class GrpcLoggingService {

  private final LoggingServiceGrpc.LoggingServiceBlockingStub loggingStub;

  public GrpcLoggingService(LoggingServiceGrpc.LoggingServiceBlockingStub loggingStub) {
    this.loggingStub = loggingStub;
  }

  public void log(LogRequest request) {
    try {
      LogResponse response = loggingStub
          .withDeadlineAfter(5, TimeUnit.SECONDS)
          .log(request);

      if (!response.getSuccess()) {
        throw new RuntimeException("Помилка при логуванні повідомлення");
      }
    } catch (Exception e) {
      throw new RuntimeException("Помилка при логуванні повідомлення: " + e.getMessage());
    }
  }
}