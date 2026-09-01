package com.romanpulov.piastriawss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "turso")
public record TursoConfigurationProperties(String dbUrl, String token) { }
