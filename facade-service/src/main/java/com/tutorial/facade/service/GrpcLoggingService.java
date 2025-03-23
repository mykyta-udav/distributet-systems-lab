package com.tutorial.facade.service;

import com.tutorial.facade.config.GrpcConfig;
import com.tutorial.facade.config.LoggingServiceProperties;
import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.grpc.LogResponse;
import com.tutorial.facade.grpc.LoggingServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class GrpcLoggingService {

  private final GrpcConfig grpcConfig;
  private final List<Integer> grpcPorts;

  public GrpcLoggingService(GrpcConfig grpcConfig,
                            LoggingServiceProperties props) {
    this.grpcConfig = grpcConfig;
    this.grpcPorts = props.getGrpcPortsAsList();
  }

  /**
   * Викликає один із екземплярів logging-service випадковим чином.
   */
  public void log(LogRequest request) {
    List<Integer> portsShuffled = shufflePorts();

    // Перебираємо порти у випадковому порядку, поки не вдасться виклик
    RuntimeException lastEx = null;
    for (Integer port : portsShuffled) {
      try {
        LoggingServiceGrpc.LoggingServiceBlockingStub stub =
                grpcConfig.buildStub("localhost", port);
        LogResponse response = stub.log(request);
        if (!response.getSuccess()) {
          throw new RuntimeException("Помилка при логуванні повідомлення");
        }
        return; // Якщо успішно - завершуємо метод
      } catch (RuntimeException ex) {
        lastEx = ex;
      }
    }
    // Якщо всі спроби не вдалися
    throw lastEx != null
            ? new RuntimeException("Не вдалось викликати жоден із logging-service gRPC портів: " + lastEx.getMessage())
            : new RuntimeException("Невідома помилка при виклику log(...)");
  }

  private List<Integer> shufflePorts() {
    List<Integer> copy = new ArrayList<>(grpcPorts);
    Collections.shuffle(copy, new Random());
    return copy;
  }
}