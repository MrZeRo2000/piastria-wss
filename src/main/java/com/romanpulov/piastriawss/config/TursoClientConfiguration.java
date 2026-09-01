package com.romanpulov.piastriawss.config;

import com.romanpulov.tursocore.TursoClient;
import com.romanpulov.tursocore.TursoConfig;
import jakarta.servlet.ServletContext;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(TursoConfigurationProperties.class)
public class TursoClientConfiguration {

    @Bean
    public TursoConfig tursoConfig(
            TursoConfigurationProperties properties,
            ObjectProvider<ServletContext> servletContextProvider
    ) {
        ServletContext servletContext = servletContextProvider.getIfAvailable();
        String dbUrl = resolveValue(properties.dbUrl(), "turso.db-url", servletContext);
        String token = resolveValue(properties.token(), "turso.token", servletContext);
        return new TursoConfig(dbUrl, token);
    }

    @Bean
    public TursoClient tursoClient(
            OkHttpClient okHttpClient,
            TursoConfig tursoConfig
    ) {
        return new TursoClient(okHttpClient, tursoConfig, Clock.systemUTC());
    }

    private static String resolveValue(String propertyValue, String initParam, ServletContext servletContext) {
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue;
        }
        if (servletContext == null) {
            return propertyValue;
        }
        return servletContext.getInitParameter(initParam);
    }
}
