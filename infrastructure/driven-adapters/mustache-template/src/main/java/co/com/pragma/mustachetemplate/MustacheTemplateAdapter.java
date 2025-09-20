package co.com.pragma.mustachetemplate;

import co.com.pragma.model.template.gateways.TemplatePort;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.StringWriter;
import java.util.Map;

@Component
public class MustacheTemplateAdapter implements TemplatePort {

    private final MustacheFactory mf = new DefaultMustacheFactory("templates");

    @Override
    public Mono<String> process(String templateName, Map<String, Object> context) {
        return Mono.fromCallable(() -> {
            Mustache mustache = this.mf.compile(templateName + ".mustache");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, context).flush();
            return writer.toString();
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
