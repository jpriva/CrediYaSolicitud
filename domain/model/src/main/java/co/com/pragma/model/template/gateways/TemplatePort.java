package co.com.pragma.model.template.gateways;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface TemplatePort {
    Mono<String> process(String templateName, Map<String, Object> context);
}
