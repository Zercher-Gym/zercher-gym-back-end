package zercher.be.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.UserMapper;
import zercher.be.model.entity.User;
import zercher.be.repository.RoleRepository;
import zercher.be.repository.UserRepository;
import zercher.be.repository.VerificationTokenRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru UserServiceImpl
 * – acoperim metoda deleteUser(UUID id).
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

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

    private UUID existingId;
    private User existingUser;

    @BeforeEach
    void setUp() {
        // Pregătim un utilizator de test cu UUID fix
        existingId = UUID.randomUUID();
        existingUser = new User();
        existingUser.setId(existingId);
        existingUser.setUsername("testuser");
        // (alte câmpuri pot fi setate dacă sunt obligatorii, dar pentru delete nu sunt necesare)
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        // Aranjament:
        when(userRepository.findById(existingId)).thenReturn(Optional.of(existingUser));

        // Acțiune:
        userService.deleteUser(existingId);

        // Verificare:
        // 1) s-a apelat userRepository.findById(existingId)
        verify(userRepository, times(1)).findById(existingId);

        // 2) s-a apelat verificationTokenRepository.deleteAllByUser(existingUser)
        verify(verificationTokenRepository, times(1)).deleteAllByUser(existingUser);

        // 3) s-a apelat userRepository.delete(existingUser)
        verify(userRepository, times(1)).delete(eq(existingUser));

        // Nu mai trebuie alte interacțiuni
        verifyNoMoreInteractions(userRepository, verificationTokenRepository);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Aranjament:
        UUID fakeId = UUID.randomUUID();
        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Acțiune + Verificare excepție:
        assertThatThrownBy(() -> userService.deleteUser(fakeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("userWithIdNotFound");

        // confirmăm că nu s-au apelat metodele de ștergere
        verify(userRepository, times(1)).findById(fakeId);
        verify(verificationTokenRepository, never()).deleteAllByUser(any());
        verify(userRepository, never()).delete(any(User.class));
    }
}
