package com.example.petitionplatform.config;


import com.example.petitionplatform.security.JwtTokenProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JwtConfig {
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider();
    }
//    private String secret = "shangrilaSecretKey2025";
//    private long expirationTime =864000000;
//
//    public String getSecret() {
//        return secret;
//    }
//
//    public void setSecret(String secret) {
//        this.secret = secret;
//    }
//
//    public long getExpirationTime() {
//        return expirationTime;
//    }
//
//    public void setExpirationTime(long expirationTime) {
//        this.expirationTime = expirationTime;
//    }
}
