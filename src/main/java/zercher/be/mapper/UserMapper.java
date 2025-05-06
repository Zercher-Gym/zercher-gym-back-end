package zercher.be.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import zercher.be.dto.user.*;
import zercher.be.model.User;


@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserViewDTO userToUserViewDTO(User user);

    UserViewAdminDTO userToUserAdminViewDTO(User user);

    UserListViewDTO userToUserListViewDTO(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUserFromAdminDTO(UserUpdateAdminDTO updateDTO, @MappingTarget User user);

    void updateUserFromDTO(UserUpdateDTO updateDTO, @MappingTarget User user);
}
