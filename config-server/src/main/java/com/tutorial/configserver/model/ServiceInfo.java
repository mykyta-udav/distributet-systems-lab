package com.tutorial.configserver.model;

import lombok.Data;

@Data
public class ServiceInfo {
    private String host;
    private String grpcPorts;  // "9090,9091,9092"
    private String restPorts;  // "8081,8083,8085" або "8080"
}