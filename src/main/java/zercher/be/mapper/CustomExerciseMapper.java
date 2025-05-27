package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import zercher.be.dto.customexercise.CustomExerciseCreateDTO;
import zercher.be.dto.customexercise.CustomExerciseUpdateDTO;
import zercher.be.dto.customexercise.CustomExerciseViewDTO;
import zercher.be.model.entity.CustomExercise;

@Mapper(componentModel = "spring")
public interface CustomExerciseMapper {
    CustomExerciseViewDTO customExerciseToViewDTO(CustomExercise customExercise);

    CustomExercise createDTOToCustomExercise(CustomExerciseCreateDTO createDTO);

    void updateCustomExerciseFromDTO(CustomExerciseUpdateDTO updateDTO, @MappingTarget CustomExercise customExercise);
}
