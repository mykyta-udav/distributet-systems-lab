package com.tutorial.facade.service;

import com.tutorial.facade.config.GrpcConfig;
import com.tutorial.facade.dto.ServiceInfoDto;
import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.grpc.LogResponse;
import com.tutorial.facade.grpc.LoggingServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GrpcLoggingService {

  private final GrpcConfig grpcConfig;
  private final ConfigServerClient configServerClient;

  public GrpcLoggingService(GrpcConfig grpcConfig, ConfigServerClient configServerClient) {
    this.grpcConfig = grpcConfig;
    this.configServerClient = configServerClient;
  }

  public void log(LogRequest request) {
    // 1) Отримуємо з config-server інформацію про logging-service
    ServiceInfoDto dto = configServerClient.getServiceInfo("logging-service");
    String host = dto.getHost();
    List<Integer> grpcPorts = parsePorts(dto.getGrpcPorts());

    // 2) Перемішуємо список портів, пробуємо викликати по черзі
    List<Integer> shuffled = new ArrayList<>(grpcPorts);
    Collections.shuffle(shuffled, new Random());

    RuntimeException lastEx = null;
    for (Integer port : shuffled) {
      try {
        LoggingServiceGrpc.LoggingServiceBlockingStub stub =
                grpcConfig.buildStub(host, port);
        LogResponse response = stub.log(request);
        if (!response.getSuccess()) {
          throw new RuntimeException("Помилка при логуванні повідомлення");
        }
        return; // успішний виклик
      } catch (RuntimeException e) {
        lastEx = e;
      }
    }
    throw (lastEx != null)
            ? new RuntimeException("Не вдалось викликати жоден із logging-service gRPC портів: " + lastEx.getMessage())
            : new RuntimeException("Невідома помилка при виклику gRPC log(...)");
  }

  private List<Integer> parsePorts(String portsStr) {
    if (portsStr == null || portsStr.isEmpty()) {
      return Collections.emptyList();
    }
    return Arrays.stream(portsStr.split(","))
            .map(String::trim)
            .map(Integer::valueOf)
            .collect(Collectors.toList());
  }
}