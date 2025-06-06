package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import zercher.be.dto.workoutexercise.WorkoutExerciseViewDTO;
import zercher.be.model.entity.WorkoutExercise;

@Mapper(componentModel = "spring", uses = {UnitMapper.class})
public interface WorkoutExerciseMapper {
    @Mapping(source = "exercise.id", target = "id")
    @Mapping(source = "exercise.identifier", target = "identifier")
    @Mapping(source = "unit", target = "unit", qualifiedByName = "id")
    WorkoutExerciseViewDTO entityToViewDTO(WorkoutExercise workoutExercise);
}
