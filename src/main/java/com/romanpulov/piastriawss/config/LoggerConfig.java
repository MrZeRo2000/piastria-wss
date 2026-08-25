package com.romanpulov.piastriawss.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.MethodParameter;

@Configuration
public class LoggerConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger logger(InjectionPoint injectionPoint){
        Class<?> loggerClass;
        MethodParameter methodParameter= injectionPoint.getMethodParameter();
        if (methodParameter != null) {
            loggerClass = methodParameter.getContainingClass();
        } else {
            loggerClass = getClass();
        }
        return LoggerFactory.getLogger(loggerClass);
    }
}
