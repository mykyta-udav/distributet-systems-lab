package com.tutorial.facade.dto;

import lombok.Data;

@Data
public class ServiceInfoDto {
    private String host;
    private String grpcPorts;
    private String restPorts;
}
