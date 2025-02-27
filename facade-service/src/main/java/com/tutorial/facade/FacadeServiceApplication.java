package com.tutorial.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FacadeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacadeServiceApplication.class, args);
    }


    @Bean
    public RestTemplate restTemplate() {
        // Налаштовуємо таймаути
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3 секунди на підключення
        factory.setReadTimeout(3000);     // 3 секунди на читання

        return new RestTemplate(factory);
    }
}
