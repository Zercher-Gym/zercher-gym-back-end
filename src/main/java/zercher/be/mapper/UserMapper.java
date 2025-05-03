package zercher.be.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import zercher.be.dto.user.UserListViewDTO;
import zercher.be.dto.user.UserUpdateAdminDTO;
import zercher.be.dto.user.UserViewAdminDTO;
import zercher.be.dto.user.UserViewDTO;
import zercher.be.model.User;


@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserViewDTO userToUserViewDTO(User user);

    UserViewAdminDTO userToUserAdminViewDTO(User user);

    UserListViewDTO userToUserListViewDTO(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUserFromAdminDTO(UserUpdateAdminDTO updateDTO, @MappingTarget User user);
}
