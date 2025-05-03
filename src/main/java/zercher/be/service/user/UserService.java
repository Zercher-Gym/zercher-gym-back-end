package zercher.be.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import zercher.be.dto.user.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserViewDTO getView();

    UserViewDTO getView(UUID id);

    Page<UserViewAdminDTO> getSearchPageAdmin(Pageable pageable, UserSearchAdminDTO searchDTO);

    List<UserListViewDTO> getSearchList(UserSearchDTO searchDTO);

    void updateUserAdmin(UUID id, UserUpdateAdminDTO updateDTO);

    void deleteUser();

    void deleteUser(UUID id);
}
