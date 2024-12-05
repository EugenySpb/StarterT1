package ru.novikov.startert1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.novikov.startert1.config.LogProperties;

import java.util.Arrays;

@Aspect
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class.getName());

    private final LogProperties properties;

    public LogAspect(LogProperties properties) {
        this.properties = properties;
    }

    private boolean needLog(String level) {
        return properties.isEnabled() && properties.getLevel().equalsIgnoreCase(level);
    }

    @Before("@annotation(LogBeforeAspect)")
    public void logBefore(JoinPoint joinPoint) {
        if (needLog("DEBUG")) {
            logger.debug("Calling method before: {}", joinPoint.getSignature().getName());
        } else if (needLog("INFO")) {
            logger.info("Calling method before: {}", joinPoint.getSignature().getName());
        }
    }

    @AfterThrowing(pointcut = "@annotation(LogExceptionAspect)", throwing = "throwable")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable throwable) {
        if (needLog("ERROR")) {
            logger.error("Calling method afterThrowing exception: {}, message: {}",
                    joinPoint.getSignature().getName(), throwable.getMessage());
        }
    }

    @Around("@annotation(LogAroundAspect)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }

        if (needLog("DEBUG")) {
            logger.debug("Calling method around: {}", joinPoint.getSignature().toShortString());
        } else if (needLog("INFO")) {
            logger.info("Calling method around: {}", joinPoint.getSignature().toShortString());
        }

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            if (needLog("DEBUG")) {
                logger.debug("Method args: {}", Arrays.toString(args));
            } else if (needLog("INFO")) {
                logger.info("Method args: {}", Arrays.toString(args));
            }
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long finish = System.currentTimeMillis() - start;
            if (needLog("DEBUG")) {
                logger.debug("Method executed in {} ms", finish);
            } else if (needLog("INFO")) {
                logger.info("Method executed in {} ms", finish);
            }
            return result;
        } catch (Throwable e) {
            logger.error("Exception in method {}: {}", joinPoint.getSignature().toShortString(), e.getMessage(), e);
            throw e;
        }
    }

    @AfterReturning(pointcut = "@annotation(LogAfterReturningAspect)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (needLog("INFO")) {
            logger.info("Calling method afterReturning: {}. Result: {}",
                    joinPoint.getSignature().toShortString(), result);
        }
    }
}
