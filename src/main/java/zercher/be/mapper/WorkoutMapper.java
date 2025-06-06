package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import zercher.be.dto.workout.WorkoutCreateDTO;
import zercher.be.dto.workout.WorkoutViewDTO;
import zercher.be.dto.workout.WorkoutViewListDTO;
import zercher.be.model.entity.Workout;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {
    @Mapping(target = "labels", ignore = true)
    Workout createDTOToWorkout(WorkoutCreateDTO createDTO);

    @Mapping(target = "labels", ignore = true)
    WorkoutViewListDTO entityToViewListDTO(Workout workout);

    @Mapping(target = "labels", ignore = true)
    WorkoutViewDTO entityToViewDTO(Workout workout);
}
