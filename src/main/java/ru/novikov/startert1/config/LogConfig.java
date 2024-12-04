package ru.novikov.startert1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.novikov.startert1.aspect.LogAspect;

@Configuration
@EnableConfigurationProperties(LogProperties.class)
public class LogConfig {

    private final LogProperties logProperties;

    public LogConfig(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "log", name = "enabled", havingValue = "true", matchIfMissing = true)
    public LogAspect logAspect() {
        Logger logger = LoggerFactory.getLogger(LogAspect.class);
        switch (logProperties.getLevel().toUpperCase()) {
            case "DEBUG":
                logger.debug("Debug level set.");
                break;
            case "ERROR":
                logger.error("Error level set.");
                break;
            default:
                logger.info("Info level set.");
        }
        return new LogAspect(logProperties);
    }
}
