package com.tutorial.facade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "loggingservice")
@Data
public class LoggingServiceProperties {

    // У YAML: loggingService.grpcPorts: 9090,9091,9092
    private String grpcPorts;
    private String restPorts;

    public List<Integer> getGrpcPortsAsList() {
        return Arrays.stream(grpcPorts.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    public List<Integer> getRestPortsAsList() {
        return Arrays.stream(restPorts.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
