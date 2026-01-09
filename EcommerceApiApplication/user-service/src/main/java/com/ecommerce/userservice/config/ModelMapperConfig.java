package com.ecommerce.userservice.config;

import com.ecommerce.common.dto.UserDto;
import com.ecommerce.userservice.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Skip mapping the 'roles' field from UserDto to User entity
        // because we handle it manually in the service layer.
        PropertyMap<UserDto, User> userMap = new PropertyMap<>() {
            protected void configure() {
                skip(destination.getRoles());
            }
        };

        modelMapper.addMappings(userMap);
        return modelMapper;
    }
}
