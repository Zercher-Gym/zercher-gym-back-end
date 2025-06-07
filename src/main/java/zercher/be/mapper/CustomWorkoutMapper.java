package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import zercher.be.dto.customworkout.CustomWorkoutCreateUpdateDTO;
import zercher.be.dto.customworkout.CustomWorkoutViewDTO;
import zercher.be.dto.customworkout.CustomWorkoutViewListDTO;
import zercher.be.model.entity.CustomWorkout;

@Mapper(componentModel = "spring")
public interface CustomWorkoutMapper {
    CustomWorkoutViewDTO customWorkoutToViewDTO(CustomWorkout customWorkout);

    CustomWorkoutViewListDTO customWorkoutToViewListDTO(CustomWorkout CustomWorkout);

    CustomWorkout createDTOToCustomWorkout(CustomWorkoutCreateUpdateDTO createDTO);

    void updateCustomWorkoutFromDTO(CustomWorkoutCreateUpdateDTO updateDTO, @MappingTarget CustomWorkout CustomWorkout);
}
