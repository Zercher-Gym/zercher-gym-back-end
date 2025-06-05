package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import zercher.be.dto.unit.UnitCreateUpdateDTO;
import zercher.be.dto.unit.UnitViewDTO;
import zercher.be.model.entity.Unit;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    @Named("id")
    @Mapping(target = "id", source = "id")
    UnitViewDTO entityToViewDTO(Unit unit);

    Unit createDTOToUnit(UnitCreateUpdateDTO createDTO);

    void updateUnitFromDTO(UnitCreateUpdateDTO updateDTO, @MappingTarget Unit unit);
}
