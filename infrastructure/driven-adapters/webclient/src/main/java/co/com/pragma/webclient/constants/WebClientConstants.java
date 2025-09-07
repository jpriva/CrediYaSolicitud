package co.com.pragma.webclient.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebClientConstants {

    // --- URIs ---
    public static final String USER_BY_ID_NUMBER_URI = "/api/v1/usuarios/{idNumber}";

    // --- Log Messages ---
    public static final String LOG_USER_NOT_FOUND = "User not found in external service with idNumber: {}";
    public static final String LOG_ERROR_FROM_USER_SERVICE = "Error from user service. Status: {}, Body: {}";
    public static final String LOG_USER_RETRIEVED_SUCCESS = "Successfully retrieved user with idNumber: {}";
    public static final String LOG_COMMUNICATION_FAILED = "Failed to communicate with user service for idNumber: {}";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RequestParams {
        public static final String EMAILS = "emails";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String ID_NUMBER = "idNumber";
        public static final String MIN_BASE_SALARY = "minBaseSalary";
        public static final String MAX_BASE_SALARY = "maxBaseSalary";
    }
}