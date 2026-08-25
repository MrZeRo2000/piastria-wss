package com.romanpulov.piastriawss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:gr.properties")
@ConfigurationProperties(prefix = "gr")
public class GrProperties {
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AppProperties{" +
                "version='" + version + '\'' +
                '}';
    }
}
