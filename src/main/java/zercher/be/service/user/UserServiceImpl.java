package zercher.be.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import zercher.be.dto.user.*;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.UserMapper;
import zercher.be.model.User;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;
import zercher.be.repository.VerificationTokenRepository;
import zercher.be.specification.UserSpecifications;

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
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public UserViewDTO getView() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));
        return userMapper.userToUserViewDTO(user);
    }

    @Override
    public UserViewDTO getView(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("userWithIdNotFound"));
        return userMapper.userToUserViewDTO(user);
    }

    @Override
    public Page<UserViewAdminDTO> getSearchPageAdmin(Pageable pageable, UserSearchAdminDTO searchDTO) {
        Specification<User> specification = Specification.where(null);

        if (searchDTO.getUsername() != null && !searchDTO.getUsername().isEmpty()) {
            specification = specification.and(UserSpecifications.usernameContains(searchDTO.getUsername()));
        }
        if (searchDTO.getEmail() != null && !searchDTO.getEmail().isEmpty()) {
            specification = specification.and(UserSpecifications.emailContains(searchDTO.getEmail()));
        }
        if (searchDTO.getId() != null) {
            specification = specification.and(UserSpecifications.idEquals(searchDTO.getId()));
        }
        if (searchDTO.getEnabled() != null) {
            specification = specification.and(UserSpecifications.enabledEquals(searchDTO.getEnabled()));
        }
        if (searchDTO.getLocked() != null) {
            specification = specification.and(UserSpecifications.lockedEquals(searchDTO.getLocked()));
        }

        var entities = userRepository.findAll(specification, pageable);
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

    @Override
    public void updateUser(UserUpdateDTO updateDTO) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));

        if (!updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new ResourceExistsException("userWithEmailExists");
            }
        }

        if (!updateDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateDTO.getUsername())) {
                throw new ResourceExistsException("userWithUsernameExists");
            }
        }

        userMapper.updateUserFromDTO(updateDTO, user);
        userRepository.save(user);
    }

    @Override
    public void deleteUser() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("userWithUsernameNotFound"));
        verificationTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }

    @Override
    public void deleteUser(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("userWithIdNotFound"));
        verificationTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
    }
}