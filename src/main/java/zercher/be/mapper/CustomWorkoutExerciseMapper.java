package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import zercher.be.dto.customexercise.CustomWorkoutCustomExerciseViewDTO;
import zercher.be.dto.exercise.WorkoutExerciseViewDTO;
import zercher.be.model.entity.CustomWorkoutExercise;

@Mapper(componentModel = "spring", uses = {UnitMapper.class})
public interface CustomWorkoutExerciseMapper {
    @Mapping(source = "exercise.id", target = "exerciseId")
    @Mapping(source = "exercise.identifier", target = "identifier")
    @Mapping(source = "unit", target = "unit", qualifiedByName = "id")
    WorkoutExerciseViewDTO customWorkoutExerciseToWorkoutExerciseViewDTO(CustomWorkoutExercise customWorkoutExercise);

    @Mapping(source = "customExercise.id", target="customExerciseId")
    @Mapping(source = "customExercise.title", target="title")
    @Mapping(source = "customExercise.description", target="description")
    @Mapping(source = "unit", target = "unit", qualifiedByName = "id")
    CustomWorkoutCustomExerciseViewDTO customWorkoutExerciseToCustomWorkoutExerciseViewDTO(CustomWorkoutExercise customWorkoutExercise);
}
