package com.github.hi_fi.tcpMockeServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.integration.http.config.EnableIntegrationGraphController;

@SpringBootApplication
@EnableConfigurationProperties
@EnableIntegrationGraphController(allowedOrigins = "*")
@EnableIntegrationManagement
public class TcpMockerServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TcpMockerServerApplication.class, args);
    }
}
