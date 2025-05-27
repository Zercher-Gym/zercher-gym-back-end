package zercher.be.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import zercher.be.dto.role.RoleLimitUpdateDTO;
import zercher.be.dto.role.RoleLimitViewDTO;
import zercher.be.model.entity.Role;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Named("roleToString")
    default String roleToString(Role role) {
        return role != null ? role.getName() : null;
    }

    @IterableMapping(qualifiedByName = "roleToString")
    Set<String> rolesToStrings(Set<Role> roles);

    RoleLimitViewDTO roleToRoleLimitViewDTO(Role role);

    void updateRoleLimitFromDTO(RoleLimitUpdateDTO updateDTO, @MappingTarget Role role);
}
