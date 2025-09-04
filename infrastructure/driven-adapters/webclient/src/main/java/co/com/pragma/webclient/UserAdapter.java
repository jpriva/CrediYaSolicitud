package co.com.pragma.webclient;

import co.com.pragma.model.constants.Errors;
import co.com.pragma.model.exceptions.UserGatewayException;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.reports.SolicitudeReportFilter;
import co.com.pragma.model.user.UserProjection;
import co.com.pragma.model.user.gateways.UserPort;
import co.com.pragma.webclient.config.WebClientProperties;
import co.com.pragma.webclient.dto.UserDTO;
import co.com.pragma.webclient.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final WebClientProperties properties;
    private final WebClient webClient;
    private final LoggerPort logger;
    private final UserMapper userMapper;


    @Override
    public Flux<UserProjection> getUsersByEmails(List<String> emails) {
        return Mono.justOrEmpty(emails)
                .filter(e -> !e.isEmpty())
                .flatMapMany(this::retrieveUsersByEmailsFromService)
                .map(userMapper::toProjection)
                .doOnNext(userProj ->
                        logger.debug("USER name: {}, email: {}", userProj.getName(), userProj.getEmail())
                )
                .doOnComplete(() ->
                        logger.debug("Successfully retrieved users for {} emails.", emails != null ? emails.size() : 0)
                )
                .onErrorMap(
                        ex -> !(ex instanceof UserGatewayException),
                        ex -> new UserGatewayException(Errors.USER_GATEWAY_COMMUNICATION_FAILED, Errors.USER_GATEWAY_COMMUNICATION_FAILED_CODE)
                )
                .doOnError(ex ->
                        logger.error("Failed to communicate with user service for bulk email fetch.", ex)
                );
    }

    public Mono<UserProjection> getUserByEmail(String email) {
        return Mono.justOrEmpty(email)
                .flatMap(this::retrieveUserByEmailFromService)
                .map(userMapper::toProjection)
                .doOnSuccess(user ->
                        logger.info("User found for email [{}]: {}", email, user != null ? user.getName() : "null")
                );
    }

    public Flux<UserProjection> getUserByFilter(SolicitudeReportFilter filter) {
        return Mono.justOrEmpty(filter)
                .flatMapMany(this::retrieveUserByFilterFromService)
                .map(userMapper::toProjection)
                .doOnNext(userProj ->
                        logger.debug("USER name: {}, email: {}", userProj.getName(), userProj.getEmail())
                )
                .doOnComplete(() ->
                        logger.debug("Successfully retrieved users.")
                )
                .onErrorMap(
                        ex -> !(ex instanceof UserGatewayException),
                        ex -> new UserGatewayException(Errors.USER_GATEWAY_COMMUNICATION_FAILED, Errors.USER_GATEWAY_COMMUNICATION_FAILED_CODE)
                )
                .doOnError(ex ->
                        logger.error("Failed to communicate with user service for bulk email fetch.", ex)
                );
    }

    //PRIVATE Methods ****************************************************************

    private Flux<UserDTO> retrieveUsersByEmailsFromService(List<String> emails) {
        return webClient
                .post()
                .uri(properties.getPathUsersByEmails())
                .bodyValue(emails)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody ->
                                        Mono.error(new UserGatewayException(Errors.USER_GATEWAY_ERROR_FROM_SERVICE, Errors.USER_GATEWAY_ERROR_FROM_SERVICE_CODE))
                                )
                )
                .bodyToFlux(UserDTO.class);
    }

    private Mono<UserDTO> retrieveUserByEmailFromService(String email) {
        return webClient.get()
                .uri(properties.getPathUserByEmail() + email)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Error from user service when fetching by emails. Status: {}, Body: {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new UserGatewayException("Error from user service", "GW-001"));
                                })
                ).bodyToMono(UserDTO.class);
    }

    private Flux<UserDTO> retrieveUserByFilterFromService(SolicitudeReportFilter filter) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(properties.getPathUsersByFilter());
                    if (filter.getClientEmail() != null) uriBuilder.queryParam("email", filter.getClientEmail());
                    if (filter.getClientName() != null) uriBuilder.queryParam("name", filter.getClientName());
                    if (filter.getClientIdNumber() != null) uriBuilder.queryParam("idNumber", filter.getClientIdNumber());
                    if (filter.getMinBaseSalary() != null) uriBuilder.queryParam("minBaseSalary", filter.getMinBaseSalary());
                    if (filter.getMaxBaseSalary() != null) uriBuilder.queryParam("maxBaseSalary", filter.getMaxBaseSalary());
                    return uriBuilder.build();
                })
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    logger.error("Error from user service when fetching by filter. Status: {}, Body: {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new UserGatewayException(Errors.USER_GATEWAY_ERROR_FROM_SERVICE, Errors.USER_GATEWAY_ERROR_FROM_SERVICE_CODE));
                                })
                ).bodyToFlux(UserDTO.class);
    }
}
