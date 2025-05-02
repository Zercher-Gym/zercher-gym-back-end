package zercher.be.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import zercher.be.dto.user.UserViewDTO;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.UserMapper;
import zercher.be.model.User;
import zercher.be.repository.UserRepository;
import zercher.be.security.token.TokenUtilities;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final TokenUtilities tokenUtilities;

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