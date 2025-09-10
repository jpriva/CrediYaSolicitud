package co.com.pragma.model.template.gateways;

import java.util.Map;

public interface TemplatePort {
    String process(String templateName, Map<String, Object> context);
}
