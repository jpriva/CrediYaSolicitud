package co.com.pragma.r2dbc;

import co.com.pragma.model.logs.gateways.LoggerPort;
import co.com.pragma.r2dbc.mapper.PersistenceSolicitudeMapper;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.lang.NonNull;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SolicitudeEntityRepositoryAdapterIntegrationTest.TestConfig.class)
class SolicitudeEntityRepositoryAdapterIntegrationTest {

    @Configuration
    @EnableR2dbcRepositories(basePackages = "co.com.pragma.r2dbc")
    @ComponentScan(basePackages = "co.com.pragma.r2dbc.mapper")
    static class TestConfig extends AbstractR2dbcConfiguration {

        @Override
        @Bean
        @NonNull
        public ConnectionFactory connectionFactory() {
            return ConnectionFactories.get("r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;");
        }

        @Bean
        ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
            ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
            initializer.setConnectionFactory(connectionFactory);
            initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
            return initializer;
        }

        @Bean
        public SolicitudeEntityRepositoryAdapter userRepositoryAdapter(
                SolicitudeEntityRepository solicitudeEntityRepository,
                PersistenceSolicitudeMapper solicitudeMapper,
                LoggerPort loggerPort
        ) {
            return new SolicitudeEntityRepositoryAdapter(solicitudeEntityRepository, solicitudeMapper, loggerPort);
        }
    }

    @Autowired
    private SolicitudeEntityRepositoryAdapter solicitudeRepository;

    @Autowired
    private SolicitudeEntityRepository solicitudeEntityRepository;
/*

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    private RoleEntity savedRoleEntity;
    private Role savedRole;

    @BeforeEach
    void setUp() {
        userEntityRepository.deleteAll().block();

        savedRoleEntity = roleEntityRepository.findOne(Example.of(RoleEntity.builder().rolId(DefaultValues.DEFAULT_ROLE_ID).build())).block();
        savedRole = Role.builder().rolId(savedRoleEntity.getRolId()).name(savedRoleEntity.getName()).description(savedRoleEntity.getDescription()).build();
    }
    @Test
    void save_shouldPersistAndReturnSolicitude_whenSuccessful() {
        Solicitude solicitudeToSave = Solicitude.builder()
                .value(new BigDecimal("50000000.00"))
                .deadline(12)
                .email("jane.doe@example.com")
                .loanType(LoanType.builder().loanTypeId(1).build())
                .build();

        Mono<Solicitude> result = solicitudeEntityRepository.save(solicitudeToSave);

        StepVerifier.create(result)
                .assertNext(savedSolicitude -> {
                    assert savedSolicitude.getSolicitudeId() != null;
                    assert savedSolicitude.getValue().equals(solicitudeToSave.getValue());
                    assert savedSolicitude.getDeadline().equals(solicitudeToSave.getDeadline());
                    assert savedSolicitude.getEmail().equals(solicitudeToSave.getEmail());
                    assert savedSolicitude.getLoanType()!= null;
                    assert savedSolicitude.getLoanType().getLoanTypeId().equals(solicitudeToSave.getLoanType().getLoanTypeId());
                    assert savedSolicitude.getState()!= null;
                })
                .verifyComplete();

        Mono<Long> countOperation = solicitudeEntityRepository.count(Example.of(Solicitude.builder().email("jane.doe@example.com").build()));

        StepVerifier.create(countOperation).expectNext(1L).verifyComplete();
    }
/*
    @Test
    void exists_shouldReturnTrue_whenUserExists() {
        userEntityRepository.save(UserEntity.builder()
                .email("exists@example.com")
                .name("a")
                .lastName("b")
                .idNumber("1")
                .rolId(savedRoleEntity.getRolId())
                .baseSalary(BigDecimal.ONE)
                .build()
        ).block();
        User example = User.builder().email("exists@example.com").build();

        Mono<Boolean> result = userRepository.exists(example);

        StepVerifier.create(result)
                .expectNext(true).verifyComplete();
    }

    @Test
    void exists_shouldReturnFalse_whenUserDoesNotExist() {
        User example = User.builder().email("nonexistent@example.com").build();

        Mono<Boolean> result = userRepository.exists(example);

        StepVerifier.create(result)
                .expectNext(false).verifyComplete();
    }
    */
}