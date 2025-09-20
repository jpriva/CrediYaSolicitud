package co.com.pragma.usecase.solicitude.utils;

import co.com.pragma.model.constants.DefaultValues;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.template.Installment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationUtils {

    public static final String DEFAULT_STATE_CHANGE = "Your loan application's state change";
    public static final String DEFAULT_PAY_PLAN = "Loan application pay plan";

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

    public static Map<String, Object> userPayPlan(Solicitude solicitude) {
        Objects.requireNonNull(solicitude, "Solicitude cannot be null for generating a pay plan.");
        Objects.requireNonNull(solicitude.getValue(), "Solicitude value (principal) cannot be null.");
        Objects.requireNonNull(solicitude.getLoanType(), "Solicitude loan type cannot be null.");
        Objects.requireNonNull(solicitude.getLoanType().getInterestRate(), "Solicitude interest rate cannot be null.");

        BigDecimal principal = solicitude.getValue();
        int termInMonths = solicitude.getDeadline();

        double monthlyInterestRate = solicitude.getLoanType().getInterestRate().doubleValue() / 100;
        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, monthlyInterestRate, termInMonths);

        List<Installment> installments = new ArrayList<>();
        BigDecimal remainingBalance = principal;
        BigDecimal totalInterest = BigDecimal.ZERO;

        for (int month = 1; month <= termInMonths; month++) {
            BigDecimal interestPaid = remainingBalance.multiply(BigDecimal.valueOf(monthlyInterestRate)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPaid;

            if (month == termInMonths) {
                principalPaid = remainingBalance;
            } else {
                principalPaid = monthlyPayment.subtract(interestPaid);
            }

            remainingBalance = remainingBalance.subtract(principalPaid);
            totalInterest = totalInterest.add(interestPaid);

            installments.add(new Installment(
                    month,
                    formatCurrency(principalPaid),
                    formatCurrency(interestPaid),
                    formatCurrency(remainingBalance)
            ));
        }

        Map<String, Object> context = new HashMap<>();
        context.put("solicitudeId", solicitude.getSolicitudeId());
        context.put("loanValue", formatCurrency(principal));
        context.put("interestRate", String.format("%.2f%%", solicitude.getLoanType().getInterestRate().doubleValue()));
        context.put("deadline", termInMonths);
        context.put("monthlyPayment", formatCurrency(monthlyPayment));
        context.put("totalInterest", formatCurrency(totalInterest));
        context.put("totalPayment", formatCurrency(principal.add(totalInterest)));
        context.put("installments", installments);
        return context;
    }

    private static BigDecimal calculateMonthlyPayment(BigDecimal principal, double monthlyInterestRate, int termInMonths) {
        if (monthlyInterestRate == 0) {
            return principal.divide(BigDecimal.valueOf(termInMonths), 2, RoundingMode.HALF_UP);
        }
        double factor = Math.pow(1 + monthlyInterestRate, termInMonths);
        return principal.multiply(BigDecimal.valueOf(monthlyInterestRate * factor / (factor - 1))).setScale(2, RoundingMode.HALF_UP);
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
            case DefaultValues.MANUAL_STATE -> "manual";
            default -> "pending";
        };
    }
}
