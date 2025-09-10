package co.com.pragma.sqs.sender.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;

import java.net.URI;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSSenderConfigTest {

    @Mock
    private SQSSenderProperties properties;

    @Mock
    private MetricPublisher publisher;

    @Mock
    private SqsAsyncClientBuilder builder;

    @Mock
    private SqsAsyncClient mockSqsAsyncClient;

    @InjectMocks
    private SQSSenderConfig config;

    @BeforeEach
    void setUp() {
        // Mock the builder chain to avoid NullPointerExceptions
        when(builder.endpointOverride(any())).thenReturn(builder);
        when(builder.region(any())).thenReturn(builder);
        when(builder.credentialsProvider(any())).thenReturn(builder);
        when(builder.overrideConfiguration(any(Consumer.class))).thenReturn(builder);
        when(builder.build()).thenReturn(mockSqsAsyncClient);
    }

    @Test
    void shouldConfigureClientForLocalStackWhenEndpointIsPresent() {
        // Arrange
        String localEndpoint = "http://localhost:4566";
        String localRegion = "us-east-1";
        when(properties.endpoint()).thenReturn(localEndpoint);
        when(properties.region()).thenReturn(localRegion);

        // Mock the static SqsAsyncClient.builder() method
        try (MockedStatic<SqsAsyncClient> mockedClient = Mockito.mockStatic(SqsAsyncClient.class)) {
            mockedClient.when(SqsAsyncClient::builder).thenReturn(builder);

            // Act
            SqsAsyncClient client = config.configSqs(properties, publisher);

            // Assert
            assertNotNull(client);
            verify(builder).endpointOverride(URI.create(localEndpoint));
            verify(builder).region(Region.of(localRegion));
            verify(builder).credentialsProvider(any(StaticCredentialsProvider.class));
            verify(builder).overrideConfiguration(any(Consumer.class));
            verify(builder).build();
        }
    }

    @Test
    void shouldConfigureClientForAwsWhenEndpointIsAbsent() {
        // Arrange
        String awsRegion = "us-west-2";
        when(properties.endpoint()).thenReturn(null); // No endpoint for AWS
        when(properties.region()).thenReturn(awsRegion);

        // Mock the static SqsAsyncClient.builder() method
        try (MockedStatic<SqsAsyncClient> mockedClient = Mockito.mockStatic(SqsAsyncClient.class)) {
            mockedClient.when(SqsAsyncClient::builder).thenReturn(builder);

            // Act
            SqsAsyncClient client = config.configSqs(properties, publisher);

            // Assert
            assertNotNull(client);
            // The endpoint override is called with null, which is the correct behavior
            verify(builder).endpointOverride(null);
            verify(builder).region(Region.of(awsRegion));
            verify(builder).credentialsProvider(any(AwsCredentialsProviderChain.class));
            verify(builder).overrideConfiguration(any(Consumer.class));
            verify(builder).build();
        }
    }
}