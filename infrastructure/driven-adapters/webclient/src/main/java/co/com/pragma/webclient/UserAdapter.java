package co.com.pragma.webclient;

import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.exceptions.UserGatewayException;
import co.com.pragma.model.user.exceptions.UserNotFoundException;
import co.com.pragma.model.user.gateways.UserPort;
import co.com.pragma.webclient.constants.WebClientConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final WebClient webClient;
    private final LoggerPort logger;

    @Override
    public Mono<User> getUserByIdNumber(String idNumber) {
        return webClient
                .get()
                .uri(WebClientConstants.USER_BY_ID_NUMBER_URI, idNumber)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND),
                        clientResponse -> {
                            logger.warn(WebClientConstants.LOG_USER_NOT_FOUND, idNumber);
                            return Mono.error(new UserNotFoundException(Errors.USER_NOT_FOUND + idNumber, Errors.USER_NOT_FOUND_CODE));
                        }
                )
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error(WebClientConstants.LOG_ERROR_FROM_USER_SERVICE, clientResponse.statusCode(), errorBody);
                                    return Mono.error(new UserGatewayException(Errors.ERROR_FROM_USER_SERVICE + errorBody, Errors.COMMUNICATION_FAILED_CODE));
                                })
                )
                .bodyToMono(User.class)
                .doOnSuccess(user -> logger.info(WebClientConstants.LOG_USER_RETRIEVED_SUCCESS, idNumber))
                .onErrorMap(
                        ex -> !(ex instanceof UserNotFoundException || ex instanceof UserGatewayException),
                        ex -> {
                            logger.error(WebClientConstants.LOG_COMMUNICATION_FAILED, idNumber, ex);
                            return new UserGatewayException(Errors.COMMUNICATION_FAILED, Errors.COMMUNICATION_FAILED_CODE);
                        }
                );
    }
}
