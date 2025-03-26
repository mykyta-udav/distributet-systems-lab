package com.tutorial.logging.service;

import com.tutorial.logging.LogRequest;
import com.tutorial.logging.LogResponse;
import com.tutorial.logging.LoggingServiceGrpc;
import com.tutorial.logging.storage.MessageStorage;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
public class GrpcLoggingService extends LoggingServiceGrpc.LoggingServiceImplBase {

  private final MessageStorage messageStorage;

  public GrpcLoggingService(MessageStorage messageStorage) {
    this.messageStorage = messageStorage;
  }

  @Override
  public void log(LogRequest request, StreamObserver<LogResponse> responseObserver) {
    log.info("Received message - ID: {}, Message: {}",
        request.getId(), request.getMessage());

    messageStorage.addMessage(
        UUID.fromString(request.getId()),
        request.getMessage());

    LogResponse response = LogResponse.newBuilder()
        .setSuccess(true)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}