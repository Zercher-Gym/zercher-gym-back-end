package zercher.be.mapper;

import org.mapstruct.*;
import zercher.be.dto.custom.CustomExerciseCreateDTO;
import zercher.be.dto.custom.CustomExerciseUpdateDTO;
import zercher.be.dto.custom.CustomExerciseViewDTO;
import zercher.be.model.entity.CustomExercise;

@Mapper(componentModel = "spring", uses = {UnitMapper.class})
public interface CustomExerciseMapper {
    @Mapping(target = "unit", source = "unit", qualifiedByName = "id")
    CustomExerciseViewDTO customExerciseToViewDTO(CustomExercise customExercise);

    CustomExercise createDTOToCustomExercise(CustomExerciseCreateDTO createDTO);

    void updateCustomExerciseFromDTO(CustomExerciseUpdateDTO updateDTO, @MappingTarget CustomExercise customExercise);
}
