package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import zercher.be.dto.exercise.ExerciseCreateDTO;
import zercher.be.dto.exercise.ExerciseViewAdminDTO;
import zercher.be.dto.exercise.ExerciseViewDTO;
import zercher.be.model.entity.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    @Mapping(target = "labels", ignore = true)
//    @Mapping(target = "units", ignore = true)
    Exercise createDTOToExercise(ExerciseCreateDTO createDTO);

    @Mapping(target = "labels", ignore = true)
//    @Mapping(target = "units", ignore = true)
    ExerciseViewAdminDTO entityToViewAdminDTO(Exercise exercise);

    @Mapping(target = "labels", ignore = true)
//    @Mapping(target = "units", ignore = true)
    ExerciseViewDTO entityToViewDTO(Exercise exercise);
}
