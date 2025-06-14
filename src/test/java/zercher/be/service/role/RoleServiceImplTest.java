package zercher.be.service.role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zercher.be.dto.role.RoleLimitUpdateDTO;
import zercher.be.dto.role.RoleLimitViewDTO;
import zercher.be.mapper.RoleMapper;
import zercher.be.model.entity.Role;
import zercher.be.repository.RoleRepository;
import zercher.be.exception.global.ResourceNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru RoleServiceImpl,
 * mockând RoleRepository și RoleMapper.
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role exampleRole;
    private RoleLimitViewDTO viewDTO1, viewDTO2;

    @BeforeEach
    void setUp() {
        // Construim două entități Role de test
        exampleRole = new Role();
        exampleRole.setId(1L);
        exampleRole.setName("ADMIN");
        exampleRole.setExerciseLimit(100);

        // Și două DTO-uri corespunzătoare
        viewDTO1 = new RoleLimitViewDTO();
        viewDTO1.setId(1L);
        viewDTO1.setName("ADMIN");
        viewDTO1.setExerciseLimit(100);

        viewDTO2 = new RoleLimitViewDTO();
        viewDTO2.setId(2L);
        viewDTO2.setName("USER");
        viewDTO2.setExerciseLimit(50);
    }

    @Test
    void getRoleLimits_ShouldReturnMappedDTOs_WhenRolesExist() {
        // Aranjament (Arrange):
        // - simulăm ca repository să returneze o listă de două entități
        Role anotherRole = new Role();
        anotherRole.setId(2L);
        anotherRole.setName("USER");
        anotherRole.setExerciseLimit(50);

        when(roleRepository.findAll()).thenReturn(Arrays.asList(exampleRole, anotherRole));
        // mapper-ul trebuie să primească entitățile și să returneze DTO-urile deja create
        when(roleMapper.roleToRoleLimitViewDTO(exampleRole)).thenReturn(viewDTO1);
        when(roleMapper.roleToRoleLimitViewDTO(anotherRole)).thenReturn(viewDTO2);

        // Acțiune (Act):
        List<RoleLimitViewDTO> results = roleService.getRoleLimits();

        // Verificare (Assert):
        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(viewDTO1, viewDTO2);

        // Confirmăm că roleRepository.findAll() și roleMapper.roleToRoleLimitViewDTO(...) au fost apelate corect:
        verify(roleRepository, times(1)).findAll();
        verify(roleMapper, times(1)).roleToRoleLimitViewDTO(exampleRole);
        verify(roleMapper, times(1)).roleToRoleLimitViewDTO(anotherRole);
    }

    @Test
    void updateRoleLimit_ShouldThrowException_WhenRoleNotFound() {
        // Aranjament:
        Long fakeId = 999L;
        RoleLimitUpdateDTO updateDto = new RoleLimitUpdateDTO();
        updateDto.setExerciseLimit(123);

        // simulăm că nu există entitatea cu ID-ul respectiv
        when(roleRepository.findById(fakeId)).thenReturn(Optional.empty());

        // Acțiune + Verificare excepție:
        assertThatThrownBy(() -> roleService.updateRoleLimit(fakeId, updateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("roleWithIdDoesNotExist");

        // Confirmăm că nici updateRoleLimitFromDTO și nici save nu au fost apelate:
        verify(roleMapper, never()).updateRoleLimitFromDTO(any(), any());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateRoleLimit_ShouldUpdateAndSave_WhenRoleExists() {
        // Aranjament:
        Long existingId = 1L;
        Role foundRole = new Role();
        foundRole.setId(existingId);
        foundRole.setName("ADMIN");
        foundRole.setExerciseLimit(10);

        RoleLimitUpdateDTO updateDto = new RoleLimitUpdateDTO();
        updateDto.setExerciseLimit(200);

        when(roleRepository.findById(existingId)).thenReturn(Optional.of(foundRole));
        // mapper-ul nu returnează nimic, ci modifică obiectul direct:
        // roleMapper.updateRoleLimitFromDTO(updateDto, foundRole) – vom verifica apelul cu ArgumentCaptor

        // Acțiune:
        roleService.updateRoleLimit(existingId, updateDto);

        // Verificare:
        // 1) s-a apelat roleRepository.findById(existingId):
        verify(roleRepository, times(1)).findById(existingId);

        // 2) s-a apelat roleMapper.updateRoleLimitFromDTO(updateDto, foundRole):
        ArgumentCaptor<RoleLimitUpdateDTO> dtoCaptor = ArgumentCaptor.forClass(RoleLimitUpdateDTO.class);
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleMapper, times(1)).updateRoleLimitFromDTO(dtoCaptor.capture(), roleCaptor.capture());
        assertThat(dtoCaptor.getValue()).isEqualTo(updateDto);
        assertThat(roleCaptor.getValue()).isSameAs(foundRole);

        // 3) s-a apelat roleRepository.save(foundRole):
        verify(roleRepository, times(1)).save(foundRole);
    }
}
