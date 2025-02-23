package com.example.demo.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigCloudinary {

    @Bean
    public Cloudinary configKey() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dfotv2fbh");
        config.put("api_key", "492888877989748");
        config.put("api_secret", "FH9DkLAfaJ0kp4jkzIFtcJ6FrtA");
        return new Cloudinary(config);
    }
}
