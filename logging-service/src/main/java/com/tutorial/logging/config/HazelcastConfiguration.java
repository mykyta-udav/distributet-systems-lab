package com.tutorial.logging.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setClusterName("logging-service-cluster");

        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPortAutoIncrement(true);
        return Hazelcast.newHazelcastInstance(config);
    }
}
