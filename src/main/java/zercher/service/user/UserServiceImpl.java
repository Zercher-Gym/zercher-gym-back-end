package zercher.service.user;

import zercher.dto.user.UserViewDTO;
import zercher.exception.ResourceNotFoundException;
import zercher.mapper.UserMapper;
import zercher.model.User;
import zercher.repository.UserRepository;
import zercher.security.token.TokenUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenUtilities tokenUtilities;

    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            TokenUtilities tokenUtilities,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.tokenUtilities = tokenUtilities;
        this.userMapper = userMapper;
    }

    @Override
    public UserViewDTO getView(String token) {
        var user = getUserFromToken(token);
        return userMapper.userToUserViewDTO(user);
    }

    @Override
    public UserViewDTO getView(UUID id) {
        var user = getUserFromID(id);
        return userMapper.userToUserViewDTO(user);
    }

    @Override
    public Page<UserViewDTO> getViewList(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::userToUserViewDTO);
    }

    @Override
    public void deleteUser(String token) {
        var user = getUserFromToken(token);
        userRepository.delete(user);
    }

    @Override
    public void deleteUser(UUID id) {
        var user = getUserFromID(id);
        userRepository.delete(user);
    }

    private User getUserFromToken(String token) throws ResourceNotFoundException {
        var username = tokenUtilities.getUsernameFromToken(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username not found."));
    }

    private User getUserFromID(UUID id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID not found."));
    }
}