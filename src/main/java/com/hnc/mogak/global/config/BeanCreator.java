package com.hnc.mogak.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanCreator {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
