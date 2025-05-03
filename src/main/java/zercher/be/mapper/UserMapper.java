package zercher.be.mapper;

import org.mapstruct.Mapper;

import zercher.be.dto.user.UserViewDTO;
import zercher.be.model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserViewDTO userToUserViewDTO(User user);
}
