package zercher.be.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import zercher.be.dto.user.*;
import zercher.be.exception.global.ResourceExistsException;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.UserMapper;
import zercher.be.model.entity.Role;
import zercher.be.model.entity.User;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;
import zercher.be.repository.VerificationTokenRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplAdditionalTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        // Pregătim doar entitatea existentă, stubbarea SecurityContext o facem în teste când e nevoie
        existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUsername("testuser");
        existingUser.setEmail("test@example.com");
    }

    // -------------------------------
    // 1. Test pentru getView() (fără parametru)
    // -------------------------------
    @Test
    void getView_ShouldReturnUserViewDTO_WhenUserExists() {
        // Aranjament:
        // - setăm SecurityContext local pentru acest test
        UserDetails principalUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .authorities(new ArrayList<>())
                .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principalUserDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // - mockăm repository-ul și mapper-ul
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        UserViewDTO viewDTO = new UserViewDTO();
        viewDTO.setId(existingUser.getId());
        viewDTO.setUsername("testuser");
        viewDTO.setEmail("test@example.com");
        when(userMapper.userToUserViewDTO(existingUser)).thenReturn(viewDTO);

        // Acțiune
        UserViewDTO result = userService.getView();

        // Verificare
        assertThat(result).isSameAs(viewDTO);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userMapper, times(1)).userToUserViewDTO(existingUser);
    }

    @Test
    void getView_ShouldThrowException_WhenUserNotFound() {
        // Aranjament: setăm SecurityContext
        UserDetails principalUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .authorities(new ArrayList<>())
                .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principalUserDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // mock repo nu găsește user
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Acțiune + Verificare excepție
        assertThatThrownBy(() -> userService.getView())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("userWithUsernameNotFound");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userMapper, never()).userToUserViewDTO(any());
    }

    // -------------------------------
    // 2. Test pentru getView(UUID id)
    // -------------------------------
    @Test
    void getView_ById_ShouldReturnUserViewDTO_WhenUserExists() {
        UUID someId = existingUser.getId();
        when(userRepository.findById(someId)).thenReturn(Optional.of(existingUser));

        UserViewDTO viewDTO = new UserViewDTO();
        viewDTO.setId(someId);
        viewDTO.setUsername("testuser");
        viewDTO.setEmail("test@example.com");
        when(userMapper.userToUserViewDTO(existingUser)).thenReturn(viewDTO);

        UserViewDTO result = userService.getView(someId);

        assertThat(result).isSameAs(viewDTO);
        verify(userRepository, times(1)).findById(someId);
        verify(userMapper, times(1)).userToUserViewDTO(existingUser);
    }

    @Test
    void getView_ById_ShouldThrowException_WhenUserNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getView(fakeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("userWithIdNotFound");

        verify(userRepository, times(1)).findById(fakeId);
        verify(userMapper, never()).userToUserViewDTO(any());
    }

    // -------------------------------
    // 3. Test pentru getSearchList(UserSearchDTO searchDTO)
    // -------------------------------
    @Test
    void getSearchList_ShouldReturnMappedList_WhenUsersExist() {
        // Aranjament
        UserSearchDTO searchDTO = new UserSearchDTO();
        searchDTO.setUsername("user");
        searchDTO.setLimit(2);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEmail("a@example.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEmail("b@example.com");

        Limit limitObj = Limit.of(2);
        when(userRepository.getUsersByUsernameContainingIgnoreCase("user", limitObj))
                .thenReturn(List.of(user1, user2));

        UserListViewDTO dto1 = new UserListViewDTO();
        dto1.setId(user1.getId());
        dto1.setUsername("user1");
        UserListViewDTO dto2 = new UserListViewDTO();
        dto2.setId(user2.getId());
        dto2.setUsername("user2");

        when(userMapper.userToUserListViewDTO(user1)).thenReturn(dto1);
        when(userMapper.userToUserListViewDTO(user2)).thenReturn(dto2);

        // Acțiune
        List<UserListViewDTO> result = userService.getSearchList(searchDTO);

        // Verificare
        assertThat(result).hasSize(2).containsExactly(dto1, dto2);
        verify(userRepository, times(1))
                .getUsersByUsernameContainingIgnoreCase("user", limitObj);
        verify(userMapper, times(1)).userToUserListViewDTO(user1);
        verify(userMapper, times(1)).userToUserListViewDTO(user2);
    }

    // -------------------------------
    // 4. Test pentru updateUserAdmin(UUID id, UserUpdateAdminDTO updateDTO)
    // -------------------------------
    @Test
    void updateUserAdmin_ShouldUpdateAndSave_WhenUserExists() {
        // Aranjament
        UUID someId = existingUser.getId();
        when(userRepository.findById(someId)).thenReturn(Optional.of(existingUser));

        // DTO-ul actualizează doar enabled, locked și roles
        UserUpdateAdminDTO updateAdminDTO = new UserUpdateAdminDTO();
        updateAdminDTO.setEnabled(true);
        updateAdminDTO.setLocked(false);
        Set<String> newRolesNames = Set.of("ROLE_USER", "ROLE_ADMIN");
        updateAdminDTO.setRoles(newRolesNames);

        // Mapper: setăm enabled și locked în entitate
        doAnswer(invocation -> {
            UserUpdateAdminDTO dtoArg = invocation.getArgument(0);
            User userArg = invocation.getArgument(1);
            userArg.setEnabled(dtoArg.getEnabled());
            userArg.setLocked(dtoArg.getLocked());
            return null;
        }).when(userMapper).updateUserFromAdminDTO(eq(updateAdminDTO), eq(existingUser));

        // roleRepository.findAllByNameIn → returnează Set<Role>
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");
        when(roleRepository.findAllByNameIn(newRolesNames)).thenReturn(Set.of(role1, role2));

        // Acțiune
        userService.updateUserAdmin(someId, updateAdminDTO);

        // Verificare
        verify(userRepository, times(1)).findById(someId);
        verify(userMapper, times(1)).updateUserFromAdminDTO(updateAdminDTO, existingUser);
        verify(roleRepository, times(1)).findAllByNameIn(newRolesNames);

        assertThat(existingUser.getEnabled()).isTrue();
        assertThat(existingUser.getLocked()).isFalse();
        assertThat(existingUser.getRoles()).containsExactlyInAnyOrder(role1, role2);

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUserAdmin_ShouldThrowException_WhenUserNotFound() {
        // Aranjament
        UUID fakeId = UUID.randomUUID();
        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        UserUpdateAdminDTO updateAdminDTO = new UserUpdateAdminDTO();
        updateAdminDTO.setEnabled(true);
        updateAdminDTO.setLocked(false);
        updateAdminDTO.setRoles(Set.of("ROLE_USER"));

        // Acțiune + Verificare excepție
        assertThatThrownBy(() -> userService.updateUserAdmin(fakeId, updateAdminDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("userWithIdNotFound");

        verify(userRepository, times(1)).findById(fakeId);
        verify(userMapper, never()).updateUserFromAdminDTO(any(), any());
        verify(roleRepository, never()).findAllByNameIn(any());
        verify(userRepository, never()).save(any());
    }

    // -------------------------------
    // 5. Test pentru updateUser(UserUpdateDTO updateDTO)
    // -------------------------------

    @Test
    void updateUser_ShouldThrowException_WhenEmailExists() {
        // Aranjament: setăm SecurityContext pentru acest test
        UserDetails principalUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .authorities(new ArrayList<>())
                .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principalUserDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("another@example.com");  // diferit de existingUser.getEmail()
        updateDTO.setUsername("testuser");          // rămâne la fel

        // Repository: găsește utilizatorul curent și spune că există altcineva cu email-ul nou
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("another@example.com")).thenReturn(true);

        // Acțiune + Verificare
        assertThatThrownBy(() -> userService.updateUser(updateDTO))
                .isInstanceOf(ResourceExistsException.class)
                .hasMessage("userWithEmailExists");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("another@example.com");
        verify(userMapper, never()).updateUserFromDTO(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldThrowException_WhenUsernameExists() {
        // Aranjament: setăm SecurityContext
        UserDetails principalUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .authorities(new ArrayList<>())
                .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principalUserDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("test@example.com");    // rămâne la fel
        updateDTO.setUsername("otheruser");        // există alt user cu acest username

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        // Deoarece updateDTO.email == existingUser.email, codul nu apelează existsByEmail(...)
        when(userRepository.existsByUsername("otheruser")).thenReturn(true);

        // Acțiune + Verificare
        assertThatThrownBy(() -> userService.updateUser(updateDTO))
                .isInstanceOf(ResourceExistsException.class)
                .hasMessage("userWithUsernameExists");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername("otheruser");
        verify(userMapper, never()).updateUserFromDTO(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateAndSave_WhenValid() {
        // Aranjament: setăm SecurityContext
        UserDetails principalUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .authorities(new ArrayList<>())
                .build();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principalUserDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("new@example.com");
        updateDTO.setUsername("newuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        // Mapper: actualizează câmpurile
        doAnswer(invocation -> {
            UserUpdateDTO dtoArg = invocation.getArgument(0);
            User userArg = invocation.getArgument(1);
            userArg.setEmail(dtoArg.getEmail());
            userArg.setUsername(dtoArg.getUsername());
            return null;
        }).when(userMapper).updateUserFromDTO(eq(updateDTO), eq(existingUser));

        // Acțiune
        userService.updateUser(updateDTO);

        // Verificare
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userMapper, times(1)).updateUserFromDTO(updateDTO, existingUser);
        verify(userRepository, times(1)).save(existingUser);

        assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
        assertThat(existingUser.getUsername()).isEqualTo("newuser");
    }
}
