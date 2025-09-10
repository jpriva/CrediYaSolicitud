package co.com.pragma.metrics.aws;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricPublisher;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
@AllArgsConstructor
public class MicrometerMetricPublisher implements MetricPublisher {
    private final ExecutorService awsMetricsExecutor;
    private final MeterRegistry registry;

    @Override
    public void publish(MetricCollection metricCollection) {
        awsMetricsExecutor.submit(() -> {
            List<Tag> tags = buildTags(metricCollection);
            metricCollection.stream()
                    .filter(metricRecord -> metricRecord.value() instanceof Duration || metricRecord.value() instanceof Integer)
                    .forEach(metricRecord -> {
                        if (metricRecord.value() instanceof Duration) {
                            registry.timer(metricRecord.metric().name(), tags).record((Duration) metricRecord.value());
                        } else if (metricRecord.value() instanceof Integer) {
                            registry.counter(metricRecord.metric().name(), tags).increment((Integer) metricRecord.value());
                        }
                    });
        });
    }

    @Override
    public void close() {
        //Spring manages Lifecycle
    }

    private List<Tag> buildTags(MetricCollection metricCollection) {
        return metricCollection.stream()
                .filter(metricRecord -> metricRecord.value() instanceof String || metricRecord.value() instanceof Boolean)
                .map(metricRecord -> Tag.of(metricRecord.metric().name(), metricRecord.value().toString()))
                .toList();
    }
}
