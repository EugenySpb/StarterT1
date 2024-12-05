package ru.novikov.startert1.config;

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
        return new LogAspect(logProperties);
    }
}
