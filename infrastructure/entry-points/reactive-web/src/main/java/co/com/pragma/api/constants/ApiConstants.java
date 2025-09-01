package co.com.pragma.api.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPaths {
        public static final String BASE_PATH = "/api/v1";
        public static final String SOLICITUDE_PATH = BASE_PATH + "/solicitud";
        public static final String SWAGGER_PATH = "/swagger-ui.html";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ApiPathMatchers {
        //PERMIT ALL
        public static final String API_DOCS_MATCHER = "/v3/api-docs/**";
        public static final String SWAGGER_UI_MATCHER = "/swagger-ui/**";
        public static final String WEB_JARS_MATCHER = "/webjars/**";
        //ADMIN/ASESOR
        public static final String SOLICITUDE_MATCHER = ApiPaths.SOLICITUDE_PATH + "/**";
        //TEST ENDPOINT
        public static final String TEST_MATCHER = "/test-endpoint";

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Role {
        public static final String CLIENT_ROLE_NAME = "CLIENTE";
        public static final String ADMIN_ROLE_NAME = "ADMIN";
        public static final String ADVISOR_ROLE_NAME = "ASESOR";

    }
}