package com.tutorial.facade.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServiceInfoDto {
    private String host;
    private List<Integer> grpcPorts;
    private List<Integer> restPorts;
}
