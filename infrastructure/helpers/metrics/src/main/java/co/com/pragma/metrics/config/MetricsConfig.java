package co.com.pragma.metrics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class MetricsConfig {

    public static final String AWS_METRICS_EXECUTOR = "awsMetricsExecutor";

    @Bean(name = AWS_METRICS_EXECUTOR, destroyMethod = "shutdown")
    public ExecutorService awsMetricsExecutor() {
        return Executors.newFixedThreadPool(10);
    }
}