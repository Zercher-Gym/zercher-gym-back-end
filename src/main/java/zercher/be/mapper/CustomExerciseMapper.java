package zercher.be.mapper;

import org.mapstruct.*;
import zercher.be.dto.customexercise.CustomExerciseCreateDTO;
import zercher.be.dto.customexercise.CustomExerciseUpdateDTO;
import zercher.be.dto.customexercise.CustomExerciseViewDTO;
import zercher.be.model.entity.CustomExercise;

@Mapper(componentModel = "spring", uses = {UnitMapper.class})
public interface CustomExerciseMapper {
    @Mapping(target = "unit", source = "unit", qualifiedByName = "id")
    CustomExerciseViewDTO customExerciseToViewDTO(CustomExercise customExercise);

    CustomExercise createDTOToCustomExercise(CustomExerciseCreateDTO createDTO);

    void updateCustomExerciseFromDTO(CustomExerciseUpdateDTO updateDTO, @MappingTarget CustomExercise customExercise);
}
