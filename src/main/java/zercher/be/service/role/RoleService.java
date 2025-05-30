package zercher.be.service.role;

import zercher.be.dto.role.RoleLimitUpdateDTO;
import zercher.be.dto.role.RoleLimitViewDTO;

import java.util.List;

public interface RoleService {
    void updateRoleLimit(Long id, RoleLimitUpdateDTO updateDTO);

    List<RoleLimitViewDTO> getRoleLimits();
}
