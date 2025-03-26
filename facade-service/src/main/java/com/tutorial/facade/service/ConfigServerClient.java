package com.tutorial.facade.service;

import com.tutorial.facade.config.GrpcConfig;
import com.tutorial.facade.dto.ServiceInfoDto;
import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.grpc.LogResponse;
import com.tutorial.facade.grpc.LoggingServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ConfigServerClient {

    private final RestTemplate restTemplate;
    private final String configServerUrl;
    private final GrpcConfig grpcConfig;

    public ConfigServerClient(RestTemplate restTemplate,
                              @Value("${config.server.url}") String configServerUrl, GrpcConfig grpcConfig) {
        this.restTemplate = restTemplate;
        this.configServerUrl = configServerUrl;
        this.grpcConfig = grpcConfig;
    }

    public ServiceInfoDto getServiceInfo(String serviceName) {
        String url = UriComponentsBuilder.fromHttpUrl(configServerUrl)
                .pathSegment("services", serviceName)
                .build()
                .toString();
        try {
            return restTemplate.getForObject(url, ServiceInfoDto.class);
        } catch (Exception ex) {
            log.error("Помилка при виклику config-server: {}", ex.getMessage());
            throw new RuntimeException("Не вдалося отримати IP/порти сервісу " + serviceName);
        }
    }

    public void invokeLoggingServiceGrpc(LogRequest request) {
        ServiceInfoDto info = getServiceInfo("logging-service");
        if (info == null || info.getGrpcPorts() == null || info.getGrpcPorts().isEmpty()) {
            throw new RuntimeException("Немає gRPC портів для logging-service");
        }
        List<Integer> ports = new ArrayList<>(info.getGrpcPorts());
        Collections.shuffle(ports);

        RuntimeException lastEx = null;
        for (Integer port : ports) {
            try {
                LoggingServiceGrpc.LoggingServiceBlockingStub stub =
                        grpcConfig.buildStub(info.getHost(), port);
                LogResponse resp = stub.log(request);
                if (!resp.getSuccess()) {
                    throw new RuntimeException("Помилка при логуванні");
                }
                return; // успіх
            } catch (RuntimeException e) {
                lastEx = e;
            }
        }
        throw new RuntimeException("Не вдалося викликати logging-service (gRPC) на жодному з портів: " + lastEx.getMessage());
    }

    public String pickLoggingServiceRestUrl() {
        ServiceInfoDto info = getServiceInfo("logging-service");
        if (info == null || info.getRestPorts() == null || info.getRestPorts().isEmpty()) {
            throw new RuntimeException("Немає REST портів для logging-service");
        }
        List<Integer> restPorts = new ArrayList<>(info.getRestPorts());
        Collections.shuffle(restPorts);
        int chosenPort = restPorts.getFirst();
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(info.getHost())
                .port(chosenPort)
                .path("/messages")
                .build()
                .toString();
    }

    public String pickMessageServiceRestUrl() {
        ServiceInfoDto info = getServiceInfo("message-service");
        if (info == null || info.getRestPorts() == null || info.getRestPorts().isEmpty()) {
            throw new RuntimeException("Немає REST портів для message-service");
        }
        int port = info.getRestPorts().getFirst();
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(info.getHost())
                .port(port)
                .path("/messages")
                .build()
                .toString();
    }
}
