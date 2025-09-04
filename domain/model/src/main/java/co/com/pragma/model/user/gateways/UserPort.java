package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.UserProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserPort {
    Flux<UserProjection> getUsersByEmails(List<String> emails);
    Mono<UserProjection> getUserByEmail(String email);
}
