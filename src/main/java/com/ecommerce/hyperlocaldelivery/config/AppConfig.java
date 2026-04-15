package com.ecommerce.hyperlocaldelivery.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean //this is autowired in the category service implementation
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
