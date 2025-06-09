package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import zercher.be.dto.workout.WorkoutLabelCreateDTO;
import zercher.be.dto.workout.WorkoutLabelUpdateDTO;
import zercher.be.dto.workout.WorkoutLabelViewDTO;
import zercher.be.model.entity.WorkoutLabel;

@Mapper(componentModel = "spring")
public interface WorkoutLabelMapper {
    WorkoutLabel createDTOToWorkoutLabel(WorkoutLabelCreateDTO workoutLabelCreateDTO);

    void updateUserFromDTO(WorkoutLabelUpdateDTO updateDTO, @MappingTarget WorkoutLabel workoutLabel);

    WorkoutLabelViewDTO entityToViewDTO(WorkoutLabel workoutLabel);
}
