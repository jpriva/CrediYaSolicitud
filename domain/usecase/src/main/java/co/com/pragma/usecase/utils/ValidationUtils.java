package co.com.pragma.usecase.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * A utility class for performing reactive validations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

    /**
     * Validates a given condition reactively.
     * If the condition is false, it returns a Mono that signals an error using the provided exception.
     * If the condition is true, it returns an empty Mono that completes successfully.
     *
     * @param condition The boolean condition to validate.
     * @param exceptionSupplier A supplier for the exception to be thrown if validation fails.
     * @return An empty {@link Mono} on successful validation, or a {@link Mono#error(Throwable)} on failure.
     */
    public static Mono<Void> validateCondition(boolean condition, Supplier<? extends Throwable> exceptionSupplier) {
        return
                Mono.defer(() ->
                condition ?
                        Mono.empty() :
                        Mono.error(exceptionSupplier.get())
        );
    }
}
