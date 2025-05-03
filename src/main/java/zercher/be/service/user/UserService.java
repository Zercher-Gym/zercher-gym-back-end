package zercher.be.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import zercher.be.dto.user.UserViewDTO;

import java.util.UUID;

public interface UserService {
    UserViewDTO getView(String token);

    UserViewDTO getView(UUID id);

    Page<UserViewDTO> getViewList(Pageable pageable);

    void deleteUser(String token);

    void deleteUser(UUID id);
}
