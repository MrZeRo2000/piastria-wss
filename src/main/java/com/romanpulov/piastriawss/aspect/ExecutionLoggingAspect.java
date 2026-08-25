package com.romanpulov.piastriawss.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class ExecutionLoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Object proceedMethod(ProceedingJoinPoint joinPoint, boolean showDetails)  throws Throwable {
        String methodName = joinPoint.toLongString();

        if (showDetails) {
            logger.debug("Before " + methodName);
        }
        long startTime = System.nanoTime();
        Object result = joinPoint.proceed();
        long endTime = System.nanoTime();
        if (showDetails) {
            logger.debug("After " + methodName);
        }

        long elapsedTime = endTime - startTime;
        long elapsedTimeInMS = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.MILLISECONDS);
        logger.info(String.format("Executed %s in %d ms", methodName, elapsedTimeInMS));

        return result;
    }

    @Around("com.romanpulov.piastriawss.aspect.JointPointConfig.controllerExecution()")
    public Object proceedController(ProceedingJoinPoint joinPoint) throws Throwable {
        return proceedMethod(joinPoint, true);
    }

    @Around("com.romanpulov.piastriawss.aspect.JointPointConfig.repositoryExecution()")
    public Object proceedRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        return proceedMethod(joinPoint, false);
    }
}
