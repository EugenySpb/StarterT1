package ru.novikov.startert1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.novikov.startert1.config.LogProperties;

import java.util.Arrays;

@Aspect
@Component
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
        if (needLog("INFO")) {
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

        logger.info("Calling method around: {}", joinPoint.getSignature().toShortString());
        Object[] args = joinPoint.getArgs();

        if (args != null && args.length > 0) {
            logger.info("Method args: {}", Arrays.toString(args));
        }

        long start = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error("message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        long finish = System.currentTimeMillis();
        logger.info("Calling method around time: {} ms", finish - start);

        return result;
    }

    @AfterReturning(pointcut = "@annotation(LogAfterReturningAspect)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (needLog("INFO")) {
            logger.info("Calling method afterReturning: {}. Result: {}",
                    joinPoint.getSignature().toShortString(), result);
        }
    }
}
