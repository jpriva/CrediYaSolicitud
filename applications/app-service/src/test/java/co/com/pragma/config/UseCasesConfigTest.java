package co.com.pragma.config;

import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.model.state.gateways.StateRepository;
import co.com.pragma.model.transaction.gateways.TransactionalPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public SolicitudeRepository solicitudeRepository() {
            return Mockito.mock(SolicitudeRepository.class);
        }

        @Bean
        public LoanTypeRepository loanTypeRepository() {
            return Mockito.mock(LoanTypeRepository.class);
        }

        @Bean
        public StateRepository stateRepository() {
            return Mockito.mock(StateRepository.class);
        }

        @Bean
        public LoggerPort loggerPort() {
            return Mockito.mock(LoggerPort.class);
        }

        @Bean
        public TransactionalPort transactionalPort() {
            return Mockito.mock(TransactionalPort.class);
        }
    }
}