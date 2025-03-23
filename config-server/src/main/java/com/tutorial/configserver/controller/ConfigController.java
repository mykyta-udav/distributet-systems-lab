package com.tutorial.configserver.controller;

import com.tutorial.configserver.config.ServicesConfig;
import com.tutorial.configserver.model.ServiceInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class ConfigController {

    private final ServicesConfig servicesConfig;

    public ConfigController(ServicesConfig servicesConfig) {
        this.servicesConfig = servicesConfig;
    }


    @GetMapping("/{serviceName}")
    public ServiceInfo getServiceInfo(@PathVariable String serviceName) {
        return servicesConfig.getMap().get(serviceName);
    }
}
