package co.com.pragma.sqs.sender.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueueAlias {
    public static final String NOTIFY_STATE_CHANGE = "state-change";
    public static final String CALCULATE_DEBT_CAPACITY = "debt-capacity";
    public static final String METRIC = "metric";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MetricNames{
        public static final String QUANTITY = "quantity";
    }
}
