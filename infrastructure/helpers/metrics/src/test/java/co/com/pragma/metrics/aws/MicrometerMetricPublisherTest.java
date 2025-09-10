package co.com.pragma.metrics.aws;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricRecord;
import software.amazon.awssdk.metrics.SdkMetric;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MicrometerMetricPublisherTest {

    @Mock
    private ExecutorService mockExecutor;
    @Mock
    private MetricCollection mockMetricCollection;
    @Mock
    private MetricRecord<Duration> durationRecord;
    @Mock
    private MetricRecord<Integer> retryRecord;
    @Mock
    private MetricRecord<String> serviceIdRecord;
    @Mock
    private MetricRecord<Boolean> successRecord;
    @Mock
    private SdkMetric<Duration> durationMetric;
    @Mock
    private SdkMetric<Integer> retryMetric;
    @Mock
    private SdkMetric<String> serviceIdMetric;
    @Mock
    private SdkMetric<Boolean> successMetric;

    private MeterRegistry meterRegistry;
    private MicrometerMetricPublisher publisher;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        publisher = new MicrometerMetricPublisher(mockExecutor, meterRegistry);
    }

    @Test
    @DisplayName("Should publish AWS SDK metrics to Micrometer registry")
    void publish_shouldConvertAndRecordMetrics() {
        // Arrange
        // Mock the metric records to return their values and metrics
        when(durationRecord.value()).thenReturn(Duration.ofMillis(150));
        when(durationRecord.metric()).thenReturn(durationMetric);
        when(durationMetric.name()).thenReturn("ApiCallDuration");

        when(retryRecord.value()).thenReturn(2);
        when(retryRecord.metric()).thenReturn(retryMetric);
        when(retryMetric.name()).thenReturn("RetryCount");

        // Mock the tag records
        when(serviceIdRecord.value()).thenReturn("SQS");
        when(serviceIdRecord.metric()).thenReturn(serviceIdMetric);
        when(serviceIdMetric.name()).thenReturn("ServiceId");

        when(successRecord.value()).thenReturn(true);
        when(successRecord.metric()).thenReturn(successMetric);
        when(successMetric.name()).thenReturn("Successful");

        // Mock the MetricCollection to return a stream of these mocked records
        when(mockMetricCollection.stream()).thenAnswer(invocation -> Stream.of(durationRecord, retryRecord, serviceIdRecord, successRecord));

        // Capture the runnable submitted to the executor
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

        // Act
        publisher.publish(mockMetricCollection);

        // Execute the captured runnable to simulate the async operation
        verify(mockExecutor).submit(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        // Assert
        // Verify the Timer was created and recorded
        Timer apiCallTimer = meterRegistry.find("ApiCallDuration").timer();
        assertThat(apiCallTimer).isNotNull();
        assertThat(apiCallTimer.count()).isEqualTo(1);
        assertThat(apiCallTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS)).isEqualTo(150);
        assertThat(apiCallTimer.getId().getTags()).containsExactlyInAnyOrder(
                Tag.of("ServiceId", "SQS"),
                Tag.of("Successful", "true")
        );

        // Verify the Counter was created and incremented
        Counter retryCounter = meterRegistry.find("RetryCount").counter();
        assertThat(retryCounter).isNotNull();
        assertThat(retryCounter.count()).isEqualTo(2);
        assertThat(retryCounter.getId().getTags()).containsExactlyInAnyOrder(
                Tag.of("ServiceId", "SQS"),
                Tag.of("Successful", "true")
        );
    }
}