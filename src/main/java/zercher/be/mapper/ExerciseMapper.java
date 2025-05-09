package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import zercher.be.dto.exercise.ExerciseCreateDTO;
import zercher.be.model.entity.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    @Mapping(target = "labels", ignore = true)
    Exercise createDTOToExercise(ExerciseCreateDTO createDTO);
}
