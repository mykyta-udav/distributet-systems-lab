package com.tutorial.configserver.model;

import lombok.Data;

import java.util.List;

@Data
public class ServiceInfo {
    private String host;
    private List<Integer> grpcPorts;
    private List<Integer> restPorts;
}