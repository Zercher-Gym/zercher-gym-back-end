package zercher.be.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import zercher.be.dto.user.*;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.UserMapper;
import zercher.be.model.User;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserViewDTO getView() {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.userToUserViewDTO(user);
    }

    @Override
    public UserViewDTO getView(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("userWithIdNotFound"));
        return userMapper.userToUserViewDTO(user);
    }

    @Override
    public Page<UserViewAdminDTO> getViewListAdmin(Pageable pageable) {
        var entities = userRepository.findAll(pageable);
        return entities.map(userMapper::userToUserAdminViewDTO);
    }

    @Override
    public List<UserListViewDTO> getSearchList(UserSearchDTO searchDTO) {
        var entities = userRepository.getUsersByUsernameContainingIgnoreCase(searchDTO.getUsername(), Limit.of(searchDTO.getLimit()));
        return entities.stream().map(userMapper::userToUserListViewDTO).collect(Collectors.toList());
    }

    @Override
    public void updateUserAdmin(UUID id, UserUpdateAdminDTO updateDTO) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("userWithIdNotFound"));

        userMapper.updateUserFromAdminDTO(updateDTO, user);
        var newRoles = roleRepository.findAllByNameIn(updateDTO.getRoles());
        user.setRoles(newRoles);

        userRepository.save(user);
    }
}