package zercher.be.service.unit;

import org.springframework.data.domain.Pageable;
import zercher.be.dto.unit.UnitCreateUpdateDTO;
import zercher.be.dto.unit.UnitViewDTO;

import java.util.List;

public interface UnitService {
    List<UnitViewDTO> getUnits(Pageable pageable);

    void createUnit(UnitCreateUpdateDTO createDTO);

    void updateUnit(Long id, UnitCreateUpdateDTO updateDTO);

    void deleteUnit(Long id);
}
