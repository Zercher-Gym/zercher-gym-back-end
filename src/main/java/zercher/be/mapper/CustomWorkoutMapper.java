package zercher.be.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import zercher.be.dto.custom.CustomWorkoutCreateUpdateDTO;
import zercher.be.dto.custom.CustomWorkoutViewDTO;
import zercher.be.dto.custom.CustomWorkoutViewListDTO;
import zercher.be.model.entity.CustomWorkout;

@Mapper(componentModel = "spring")
public interface CustomWorkoutMapper {
    CustomWorkoutViewDTO customWorkoutToViewDTO(CustomWorkout customWorkout);

    CustomWorkoutViewListDTO customWorkoutToViewListDTO(CustomWorkout CustomWorkout);

    CustomWorkout createDTOToCustomWorkout(CustomWorkoutCreateUpdateDTO createDTO);

    void updateCustomWorkoutFromDTO(CustomWorkoutCreateUpdateDTO updateDTO, @MappingTarget CustomWorkout CustomWorkout);
}
