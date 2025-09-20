package co.com.pragma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SolicitudeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SolicitudeServiceApplication.class, args);
    }
}
