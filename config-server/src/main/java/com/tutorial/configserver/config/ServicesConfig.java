package com.tutorial.configserver.config;

import com.tutorial.configserver.model.ServiceInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "services")
@Data
public class ServicesConfig {
    private Map<String, ServiceInfo> map;
}
