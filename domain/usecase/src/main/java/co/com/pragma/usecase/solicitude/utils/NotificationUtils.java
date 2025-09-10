package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.solicitude.Solicitude;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationUtils {

    public static final String DEFAULT_STATE_CHANGE = "Your loan application's state change";

    public static Map<String, Object> solicitudeChangeStateBody(Solicitude solicitude) {
        Map<String, Object> context = new HashMap<>();
        context.put("solicitudeId", solicitude.getSolicitudeId());
        context.put("loanType", solicitude.getLoanType().getName());
        context.put("value", formatCurrency(solicitude.getValue()));
        context.put("stateName", solicitude.getState().getName());
        context.put("stateDescription", solicitude.getState().getDescription());
        context.put("stateClass", getStateClass(solicitude.getState().getName()));

        return context;
    }

    private static String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "N/A";
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return currencyFormatter.format(value);
    }

    private static String getStateClass(String stateName) {
        return switch (stateName) {
            case DefaultValues.APPROVED_STATE -> "approved";
            case DefaultValues.REJECTED_STATE -> "rejected";
            default -> "pending";
        };
    }
}
