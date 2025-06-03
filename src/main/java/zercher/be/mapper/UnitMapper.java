package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import zercher.be.dto.unit.UnitCreateUpdateDTO;
import zercher.be.dto.unit.UnitViewDTO;
import zercher.be.model.entity.Unit;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    UnitViewDTO entityToViewDTO(Unit unit);

    Unit createDTOToUnit(UnitCreateUpdateDTO createDTO);

    void updateUnitFromDTO(UnitCreateUpdateDTO updateDTO, @MappingTarget Unit unit);
}
