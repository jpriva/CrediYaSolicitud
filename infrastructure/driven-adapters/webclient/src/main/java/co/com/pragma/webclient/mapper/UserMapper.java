package co.com.pragma.webclient.mapper;

import co.com.pragma.model.user.UserProjection;
import co.com.pragma.webclient.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "dto", target = "name", qualifiedByName = "mapFullName")
    UserProjection toProjection(UserDTO dto);

    @Named("mapFullName")
    default String mapFullName(UserDTO dto) {
        String name = dto.getName() != null ? dto.getName() : "";
        String lastName = dto.getLastName() != null ? dto.getLastName() : "";
        return (name + " " + lastName).trim();
    }
}