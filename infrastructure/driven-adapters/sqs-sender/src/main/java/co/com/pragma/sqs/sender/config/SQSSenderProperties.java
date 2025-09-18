package co.com.pragma.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "adapters.sqs")
public record SQSSenderProperties(
     String region,
     Map<String, String> queues,
     String endpoint,
     Map<String, String> metrics
){
}
