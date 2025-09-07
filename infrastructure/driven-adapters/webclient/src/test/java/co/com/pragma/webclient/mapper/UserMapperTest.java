package co.com.pragma.webclient.mapper;

import co.com.pragma.model.user.UserProjection;
import co.com.pragma.webclient.dto.UserDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserMapperImpl.class) // Important: Test the MapStruct implementation
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Nested
    class ToProjectionTests {

        @Test
        void shouldMapFullDtoToProjection() {
            UserDTO dto = UserDTO.builder()
                    .name("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .idNumber("12345")
                    .baseSalary(new BigDecimal("60000.00"))
                    .build();

            UserProjection projection = userMapper.toProjection(dto);

            assertThat(projection).isNotNull();
            assertThat(projection.getName()).isEqualTo("John Doe");
            assertThat(projection.getEmail()).isEqualTo(dto.getEmail());
            assertThat(projection.getIdNumber()).isEqualTo(dto.getIdNumber());
            assertThat(projection.getBaseSalary()).isEqualTo(dto.getBaseSalary());
        }

        @Test
        void shouldHandleNullLastName() {
            UserDTO dto = UserDTO.builder()
                    .name("John")
                    .lastName(null)
                    .email("john@example.com")
                    .build();

            UserProjection projection = userMapper.toProjection(dto);

            assertThat(projection).isNotNull();
            assertThat(projection.getName()).isEqualTo("John");
            assertThat(projection.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        void shouldHandleNullFirstName() {
            UserDTO dto = UserDTO.builder()
                    .name(null)
                    .lastName("Doe")
                    .email("doe@example.com")
                    .build();

            UserProjection projection = userMapper.toProjection(dto);

            assertThat(projection).isNotNull();
            assertThat(projection.getName()).isEqualTo("Doe");
            assertThat(projection.getEmail()).isEqualTo("doe@example.com");
        }

        @Test
        void shouldHandleBothNamesNull() {
            UserDTO dto = UserDTO.builder()
                    .name(null)
                    .lastName(null)
                    .email("test@example.com")
                    .build();

            UserProjection projection = userMapper.toProjection(dto);

            assertThat(projection).isNotNull();
            assertThat(projection.getName()).isEmpty();
            assertThat(projection.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        void shouldReturnNullWhenDtoIsNull() {
            UserProjection projection = userMapper.toProjection(null);

            assertThat(projection).isNull();
        }
    }
}