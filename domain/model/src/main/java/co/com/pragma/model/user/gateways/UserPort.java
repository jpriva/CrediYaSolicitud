package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserPort {
    Mono<User> getUserByIdNumber(String idNumber);
}
