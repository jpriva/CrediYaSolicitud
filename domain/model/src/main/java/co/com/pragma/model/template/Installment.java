package co.com.pragma.model.template;

public record Installment(
        int month,
        String principal,
        String interest,
        String remainingBalance
) {}
