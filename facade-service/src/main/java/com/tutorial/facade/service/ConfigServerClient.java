package com.tutorial.facade.service;

import com.tutorial.facade.dto.ServiceInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ConfigServerClient {

    private final RestTemplate restTemplate;
    private final String configServerUrl;

    public ConfigServerClient(RestTemplate restTemplate,
                              @Value("${config.server.url}") String configServerUrl) {
        this.restTemplate = restTemplate;
        this.configServerUrl = configServerUrl;
    }

    public ServiceInfoDto getServiceInfo(String serviceName) {
        String url = configServerUrl + "/services/" + serviceName;
        try {
            return restTemplate.getForObject(url, ServiceInfoDto.class);
        } catch (Exception ex) {
            log.error("Помилка при виклику config-server: {}", ex.getMessage());
            throw new RuntimeException("Не вдалося отримати IP/порти сервісу " + serviceName);
        }
    }
}
