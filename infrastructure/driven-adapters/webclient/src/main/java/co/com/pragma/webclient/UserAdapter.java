package co.com.pragma.webclient;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.user.gateways.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final WebClient webClient;
    private final LoggerPort logger;


}
