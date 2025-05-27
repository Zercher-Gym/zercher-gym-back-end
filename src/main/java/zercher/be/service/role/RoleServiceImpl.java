package zercher.be.service.role;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zercher.be.dto.role.RoleLimitUpdateDTO;
import zercher.be.dto.role.RoleLimitViewDTO;
import zercher.be.exception.global.ResourceNotFoundException;
import zercher.be.mapper.RoleMapper;
import zercher.be.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    @Override
    public void updateRoleLimit(Long id, RoleLimitUpdateDTO updateDTO) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("roleWithIdDoesNotExist"));
        roleMapper.updateRoleLimitFromDTO(updateDTO, role);
        roleRepository.save(role);
    }

    @Override
    public List<RoleLimitViewDTO> getRoleLimits() {
        return roleRepository.findAll().stream().map(roleMapper::roleToRoleLimitViewDTO).collect(Collectors.toList());
    }
}
