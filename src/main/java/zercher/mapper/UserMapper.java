package zercher.mapper;

import zercher.dto.user.UserSignInDTO;
import zercher.dto.user.UserSignUpDTO;
import zercher.dto.user.UserViewDTO;
import zercher.model.User;

import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserViewDTO userToUserViewDTO(User user);

    UserSignInDTO userSignUpDTOToUserSignInDTO(UserSignUpDTO userSignUpDTO);
}
