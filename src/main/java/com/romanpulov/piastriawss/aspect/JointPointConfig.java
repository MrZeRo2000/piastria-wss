package com.romanpulov.piastriawss.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class JointPointConfig {

    @Pointcut("execution(* com.romanpulov.piastriawss.controller..*(..))")
    public void controllerExecution() {

    }

    @Pointcut("execution(* com.romanpulov.piastriawss.repository..*(..))")
    public void repositoryExecution() {

    }
}
